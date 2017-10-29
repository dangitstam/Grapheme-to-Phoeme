import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Parsing utilities for parsing a modified version of the Boston
 * University Radio Corpus.
 */
public class ParseModifiedBoston {

  // graphToPhones = all mappings
  // graphemes = all graphemes and their frequency
  // phonemes = all phonemes and their frequency
  // transitions = Transitional matrix between phones
  private Graph<String, Double> graphToPhones;
  private Map<String, Double> graphemes;
  public Map<String, Double> phonemes;
  public Graph<String, Double> transitions;

  public ParseModifiedBoston() {
    this.graphToPhones = new Graph<>();
    this.graphemes = new HashMap<>();
    this.phonemes = new HashMap<>();
    this.transitions = new Graph<>();
  }

  /** Takes a stream in the format of a modded Boston corpus
   *  and adds the grapheme-phoneme mappings to the current graph,
   *  one line at a time.
   * @param fileReader the stream to be read
   */
  public void parseFile(BufferedReader fileReader) throws IOException {
    String inputLine;
    while ((inputLine = fileReader.readLine()) != null && !inputLine.equals("BREAK!")) {

      // Files begin with a list of the phones found in the corpus.
      // Skip these lines by only parsing input with spaces in them.
      if (inputLine.contains(" ")) {
        parseLine(inputLine);
      }
    }

    normalize(this.graphToPhones);
    normalize(this.transitions);
  }

  /** Takes a String in the format of a modded Boston corpus
   *  and adds the grapheme-phoneme mappings to the current graph.
   * @param input the line to be parsed
   * Also maps to phones and their counts of phones, whereas
   * consecutive phones should map to each other:
   * x1x2x3x4 ---> x1 -> x2, x2 -> x3, and x3 -> x4 to achieve
   * the HMM
  */
  private void parseLine(String input) {
    String[] wordAndPhones = input.split(" ", 2);
    String word = wordAndPhones[0];
    String[] phones = wordAndPhones[1].split(" ");
    String[] brokenWord = word.split("-");
    String prevPhone = null;

    // Allows for empty mappings to be mapped (ex. silent 'e')
    boolean[] processed = new boolean[brokenWord.length];
    Arrays.fill(processed, false);
    addPhoneToGraphs("");
    for (int i = 0; i < phones.length - 1; i += 2) {

      // Expect lines in the form of
      // word phone1 idx1 phone2 index2 ... phoneN indexN //
      String phone = phones[i].toLowerCase();
      if (phone.equals("//")) {
        break;
      }

      addPhoneme(phone);
      int index = Integer.parseInt(phones[i + 1]);
      String currGrapheme = brokenWord[index].toLowerCase();
      addPhoneToGraphs(phone);
      addGrapheme(currGrapheme);
      addMapping(currGrapheme, phone, graphToPhones);

      // Adding mappings between successive phones.
      if (prevPhone != null) {
        addMapping(prevPhone, phone, transitions);
      }

      // Update phone to continue the chaining
      prevPhone = phone;

      // Update boolean array as to continue processing later
      processed[index] = true;
    }

    // All mappings in brokenWord in the form (a-c-t --> a, c, t) that
    // did not receive a mapping will receive an empty by default.
    // This accounts for cases such as a silent 'e'.
    for (int i = 0; i < processed.length; i++) {
      if (!processed[i]) {
        String curr = brokenWord[i];

        // Since it was not processed, it must be added
        graphToPhones.addNode(curr);
        addMapping(curr, "", graphToPhones);
        if (i > 0) {
          addPhoneToGraphs(curr);
          addPhoneToGraphs(brokenWord[i - 1]);
          addMapping(brokenWord[i - 1], curr, transitions);
        }
      }
    }
  }


  // Helper method for adding a mapping between src and dest
  // by adding to it's count
  private void addMapping(String src, String dest,
                          Graph<String, Double> graph) {
    double currentCount = 1;
    if (graph.containsNode(src)) {
      if (graph.getChildrenOf(src).contains(dest)) {
        double oldCount = graph.getEdgeBetween(src, dest);
        currentCount += oldCount;
      }
    }

    graph.addEdge(src, dest, currentCount);
  }

  // Adds the current phone to all necessary graphs.
  private void addPhoneToGraphs(String curr_phone) {
    if (!graphToPhones.containsNode(curr_phone)) {
      graphToPhones.addNode(curr_phone);
    }

    if (!transitions.containsNode(curr_phone)) {
      transitions.addNode(curr_phone);
    }
  }

  // Adds grapheme to all necessary graphs
  private void addGrapheme(String curr_word) {

    if (!graphToPhones.containsNode(curr_word)) {
      graphToPhones.addNode(curr_word);
    }

    if (!graphemes.containsKey(curr_word)) {
      graphemes.put(curr_word, 1.0);
    } else {
      double currWordCount = graphemes.get(curr_word);
      graphemes.put(curr_word, 1.0 + currWordCount);
    }
  }

  // Updates phoneme count
  private void addPhoneme(String phoneme) {

    if (!phonemes.containsKey(phoneme)) {
      phonemes.put(phoneme, 1.0);
    } else {
      double currWordCount = phonemes.get(phoneme);
      phonemes.put(phoneme, 1.0 + currWordCount);
    }
  }

  /**
   * Normalizes the edges between a parent node and its descendants.
   * This allows for probabilities leaving a particular node to add
   * up to one.
   */
  private void normalize(Graph<String, Double> graph) {
    for (String g : graph.getNodes()) {
      Set<String> descPhones = graph.getChildrenOf(g);
      double denom = 0.0;
      for (String p : descPhones) {
        double currEdge = graph.getEdgeBetween(g, p);
        denom += currEdge;
      }

      for (String p : descPhones) {
        double currEdge = graph.getEdgeBetween(g, p);
        graph.addEdge(g, p, (currEdge / denom));
      }
    }
  }


  /**
   * Getter method for the current network.
   * @return The grapheme-phoneme and phoneme-phoneme mappings.
   */
  public Graph<String, Double> getNetwork() {
    return this.graphToPhones;
  }

  /**
   * Getter method for the current set of valid graphemes.
   * @return The set of valid graphemes for the network.
   */
  public Map<String, Double> getGraphemes() {
    return this.graphemes;
  }


  public Map<String, Double> getNormalizedPhones() {

    Map<String, Double> res = new HashMap<>();
    double total = 0.0;
    for (String p : phonemes.keySet()) {
      total += phonemes.get(p);
    }

    for (String p : phonemes.keySet()) {
      res.put(p, phonemes.get(p) / total);
    }

    return res;
  }

  public Graph<String, Double> getTransitions() {
    return this.transitions;
  }
}
