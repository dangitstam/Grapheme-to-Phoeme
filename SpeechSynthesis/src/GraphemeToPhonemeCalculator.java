
import java.util.*;

/**
 * Serves as the model for grapheme-to-phoneme translation.
 * This class provides utilities for creating the Viterbi result
 * matrix and functionality for extracting the most probable result.
 *
 * @author Tam Dang.
 */
public class GraphemeToPhonemeCalculator {

  private Graph<String, Double> graphToPhones;
  private Graph<String, Double> transitions;
  private Map<String, Double> graphemes;
  private Map<String, Double> phonemes;

  /**
   * Constructor for a GraphemeToPhonemeCalculator
   * @param gToP a graph mapping graphemes to phonemes
   * @param t a transitional matrix of phonemes connected by
   *          normalized probabilities
   * @param g a map of graphemes to their probabilities
   * @param p a map of phonemes to their probabilities
   */
  public GraphemeToPhonemeCalculator(Graph<String, Double> gToP,
                                     Graph<String, Double> t,
                                     Map<String, Double> g,
                                     Map<String, Double> p) {
    this.graphToPhones = gToP;
    this.graphemes = g;
    this.transitions = t;
    this.phonemes = p;
  }

  /**
   * Constructor for a GraphemeToPhonemeCalculator
   * @param grapheme a grapheme in which to return a sequence of
   *                 phonemes for
   */
  public String calculatePhoneme(String grapheme) {
    String[] obs = grapheme.split("-");
    Map<String, IndexAndProbability> v_matrix = new HashMap<>();
    calculateMatrix(obs, 0, v_matrix, null, null);
    String result = null;
    String sec_result = null;
    double max_prob = 0.0;
    double sec_max_prob = 0.0;
    for (String mapping : v_matrix.keySet()) {
      IndexAndProbability mappingRes = v_matrix.get(mapping);
      if (mappingRes.index == obs.length - 1 && mappingRes.prob > max_prob) {
        max_prob = mappingRes.prob;
        result = mapping;
      } else if (mappingRes.index == obs.length - 2 && mappingRes.prob > sec_max_prob) {
        sec_max_prob = mappingRes.prob;
        sec_result = mapping;
      }
    }

    if (result != null) {
      return result;
    }
    return sec_result;
  }

  /**
   * Calculates a Viterbi results matrix. Each solution in the matrix
   * is attached to its probability. Returns the resulting matrix through
   * the matrix parameter.
   * @param obs The observed states
   * @param i The current iteration (represents the index of the phoneme
   *          to be inserted, e.g. currently on the 'ith' step).
   * @param matrix A map representing the Viterbi results matrix, but uses
   *               a map from string to IndexAndProbability instead of
   *               a 2D-array.
   * @param currentBuild A solution that is currently being built.
   * @param last_phone The previous phone to allow for a transitional
   *                   probability to be extracted (null on the first iteration).
   */
  private void calculateMatrix(String[] obs, int i,
                               Map<String, IndexAndProbability> matrix,
                               String currentBuild, String last_phone) {
    if (i < obs.length) {
      Set<String> current_phones = graphToPhones.getChildrenOf(obs[i]);
      if (current_phones != null) {
        Iterator<String> phones_it = current_phones.iterator();
        String next;
        String curr_phone;
        for (int k = 0; k < current_phones.size(); k++) {

          // a phone of obs[i]
          curr_phone = phones_it.next();
          if (currentBuild == null || last_phone == null) {

            // curr phone is a starting phone
            next = curr_phone;
            double init_prob = graphToPhones.getEdgeBetween(obs[i], curr_phone);
            if (phonemes.containsKey(curr_phone)) {
              init_prob = phonemes.get(curr_phone);
            }
            matrix.put(curr_phone, new IndexAndProbability(0, init_prob));
            calculateMatrix(obs, i + 1, matrix, next, curr_phone);

          } else {

            double currProb = 1.0;
            if (matrix.containsKey(currentBuild)) {
              currProb *= ((matrix.get(currentBuild)).prob);
              if (transitions.getChildrenOf(last_phone).contains(curr_phone)) {
                currProb *= (transitions.getEdgeBetween(last_phone, curr_phone));
              } else {
                currProb = 0;
              }
              if (currProb > 0) {
                next = currentBuild + "-" + curr_phone;
                matrix.put(next, new IndexAndProbability(i, currProb));
                calculateMatrix(obs, i + 1, matrix, next, curr_phone);
              }
            }
          }
        }
      }
    }
  }

  // Convenient way to map indices and probabilities to solutions.
  private class IndexAndProbability {
    private int index;
    private double prob;
    public IndexAndProbability(int idx, double pr) {
      this.index = idx;
      this.prob = pr;
    }
  }
}
