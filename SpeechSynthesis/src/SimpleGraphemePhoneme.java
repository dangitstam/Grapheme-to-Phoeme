import java.util.Map;
import java.util.Set;

/**
 * Serves as a baseline for the Grapheme-to-Phoneme Calculator.
 * Maps grahemes to their most proabable phonemes.
 */
public class SimpleGraphemePhoneme {

  private Graph<String, Double> graphToPhones;
  private Graph<String, Double> transitions;
  private Map<String, Double> graphemes;
  private Map<String, Double> phonemes;

  /**
   * Constructor for a SimpleGraphemePhoneme
   * @param gToP a graph mapping graphemes to phonemes
   * @param t a transitional matrix of phonemes connected by
   *          normalized probabilities
   * @param g a map of graphemes to their probabilities
   * @param p a map of phonemes to their probabilities
   */
  public SimpleGraphemePhoneme(Graph<String, Double> gToP,
                                     Graph<String, Double> t,
                                     Map<String, Double> g,
                                     Map<String, Double> p) {
    this.graphToPhones = gToP;
    this.graphemes = g;
    this.transitions = t;
    this.phonemes = p;
  }

  /**
   * Takes a sequence of graphemes (string of graphs separated by
   * hyphens) and maps each graph to its most probable phoneme.
   * @param input The sequence of graphemes to convert
   * @return
   */
  public String getSimpleMapping(String input) {
    String[] obs = input.split("-");
    String res = "";
    for (int i = 0; i < obs.length; i++) {
      String curr_obs = obs[i];
      Set<String> desc_obs = graphToPhones.getChildrenOf(curr_obs);
      if (desc_obs == null) {
        return null;
      }
      String currMax = null;
      double maxProb = 0.0;
      for (String d : desc_obs) {
        double currEdge = graphToPhones.getEdgeBetween(curr_obs, d);
        if (currEdge > maxProb) {
          maxProb = currEdge;
          currMax = d;
        }
      }

      if (currMax != null) {
        if (res.length() == 0) {
          res += currMax;
        } else {
          res += ("-" + currMax);
        }
      }
    }

    return res;
  }

}
