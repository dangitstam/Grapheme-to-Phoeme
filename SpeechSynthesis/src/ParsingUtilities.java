import java.util.*;

/**
 * Created by dangitstam on 5/28/17.
 */
public class ParsingUtilities {

  public static Set<String> phoneVowels = new HashSet<String>(Arrays.asList("aa+1", "ah", "ax", "ah+1"));
  public static Set<String> specialCons = new HashSet<String>(Arrays.asList("ch", "sh", "dg"));


  public static class Phoneme {
    String graph;
    int count;
    public Phoneme(String graph, int count) {
      this.graph = graph;
      this.count = count;
    }
  }

  public static void testProcessInput(String input, Graph<String, Phoneme> g) {

    Set<Character> vowels = new HashSet<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
    String[] tokens = input.split(" ", 2);
    String grapheme = tokens[0];
    String[] phonemes = tokens[1].split(" ");
    Integer k = 0;
    int start = 0;
    System.out.println("WORD " + grapheme);
    for (int i = 0; i < phonemes.length; i++) {
      System.out.print(phonemes[i] + " ");
    }

    for (int i = 0; i < grapheme.length(); i++) {
      String processInput = "";
      char c = grapheme.charAt(i);
      if (vowels.contains(c)) {

        // Consume all vowels
        while (vowels.contains(c) && i < grapheme.length() - 1) {
          processInput += c;
          i++;
          c = grapheme.charAt(i);
        }

        System.out.println("CURRENT GRAPH " + processInput);

        List<Set<String>> currParse = new ArrayList<Set<String>>();
        k = processGrapheme(k, phonemes, phoneVowels, currParse, "Vowel");
        if (i < grapheme.length() - 2) {
          i--;
        }

        if (currParse.size() > 0) {
          Set<String> perms = buildGraphMappings(currParse);
          for (String p : perms) {
            System.out.println(processInput + " : " + p);
          }
        }

      } else {
        // Consume all consonants
        int i_2 = i;
        char c_2 = c;
        while (!vowels.contains(c_2) && i_2 < grapheme.length() - 1) {
          processInput += c_2;
          i_2++;
          c_2 = grapheme.charAt(i_2);
        }

        if (!specialCons.contains(processInput)) {
          processInput = "" + c;
          System.out.println("NO CONS " + processInput);
        }

        System.out.println("CURRENT GRAPH " + processInput);

        List<Set<String>> currParse = new ArrayList<Set<String>>();
        System.out.println("K " + k);
        k = processGrapheme(k, phonemes, phoneVowels, currParse, "Consonant");

        if (currParse.size() > 0) {
          Set<String> perms = buildGraphMappings(currParse);
          for (String p : perms) {
            System.out.println(processInput + " : " + p);
          }
        }
      }
    }
  }

  public static int processGrapheme(int k, String[] phonemes, Set<String> phoneVowels,
                                    List<Set<String>> currentParse, String type) {
    int start_place = 0;
    int end_place = 0;
    Set<String> curr = new HashSet<String>();
    int start = k;
    while (k < phonemes.length) {
      String p = phonemes[k];
      int s = Integer.parseInt(phonemes[k + 1]);
      int e = Integer.parseInt(phonemes[k + 2]);
      // Init start and end:
      if (k == start) {
        start_place = s;
        end_place = e;
      }

      if (s == start_place && e == end_place) {
        // Some vowels may start with a 'consonant' phoneme!
        curr.add(p);
        if (k == phonemes.length - 3) {
          System.out.println("ADDED " + p);
          currentParse.add(curr); // Add the final step
        }
      } else if ((type.equals("Vowel") && !phoneVowels.contains(p)) ||
              (type.equals("Consonant") && phoneVowels.contains(p))) {
        // Once we've reached a wrong piece, add the current set to
        // the results and break
        currentParse.add(curr);
        break;
      } else {
        curr.add(p);
        // Only vowels will have multiple spanning phonemes
        if (type.equals("Vowel")) {
          currentParse.add(curr);
          start_place = s;
          end_place = e;
          curr = new HashSet<String>();
        } else {
          break;
        }
      }

      k += 3;
    }

    return k;
  }

  public static Set<String> buildGraphMappings(List<Set<String>> parse) {
    Set<String> res = parse.get(0);
    for (int i = 1; i < parse.size(); i++) {
      Set<String> new_res = new HashSet<String>();
      Set<String> curr = parse.get(i);
      for (String r : res) {
        for (String c : curr) {
          String next_perm = r + c;
          new_res.add(next_perm);
        }
      }

      res = new_res;
    }

    for (String r : res) {
      System.out.println(r);
    }

    return res;
  }
}
