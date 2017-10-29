import java.io.*;

/**
 * Calculates the precision and recall of this model.
 */
public class FinalTesting {

  public static Integer tp = 0;
  public static Integer fp = 0;
  public static Integer fn = 0;

  public static Integer tp_base = 0;
  public static Integer fp_base = 0;
  public static Integer fn_base = 0;

  public static void main(String[] args) throws IOException {

    String corpusPath = new File("")
            .getAbsolutePath()
            .concat("/SpeechSynthesis/corpus_test.txt");
    File corpusFile = new File(corpusPath);

    BufferedReader readFile = new BufferedReader(new FileReader(corpusFile));
    ParseModifiedBoston pmb = new ParseModifiedBoston();
    pmb.parseFile(readFile);

    GraphemeToPhonemeCalculator gp_calculator = new GraphemeToPhonemeCalculator(pmb.getNetwork(),
            pmb.getTransitions(),pmb.getGraphemes(), pmb.getNormalizedPhones());
    SimpleGraphemePhoneme sgp_calculator = new SimpleGraphemePhoneme(pmb.getNetwork(),
            pmb.getTransitions(),pmb.getGraphemes(), pmb.getNormalizedPhones());

    String goldPath = new File("").getAbsolutePath().concat("/SpeechSynthesis/gold_standard.txt");
    File goldFile = new File(goldPath);
    BufferedReader readGoldFile = new BufferedReader(new FileReader(goldFile));

    String line;
    while ((line = readGoldFile.readLine()) != null) {
      String[] word_and_transcription = line.split(" ", 2);
      String attempt = gp_calculator.calculatePhoneme(word_and_transcription[0]);
      String baseline = sgp_calculator.getSimpleMapping(word_and_transcription[0]);
      String[] gold_phones = word_and_transcription[1].split(" ");

      calculateStats(attempt, gold_phones, false);
      calculateStats(baseline, gold_phones, true);

    }

    System.out.println("Viterbi Algorithm:");
    System.out.println("TP : " + tp + " FP : " + fp + " FN : " + fn);
    double prec = ((double) (tp) / (double) (tp + fp));
    double rec = ((double) (tp) / (double) (tp + fn));
    System.out.println("Precision: " + prec + " Recall: " + rec);
    System.out.println("Baseline Algorithm:");
    System.out.println("TP : " + tp_base + " FP : " + fp_base + " FN : " + fn_base);
    prec = ((double) (tp_base) / (double) (tp_base + fp_base));
    rec = ((double) (tp_base) / (double) (tp_base + fn_base));
    System.out.println("Precision: " + prec + " Recall: " + rec);
  }

  private static void calculateStats(String attempt, String[] gold_phones, boolean base) {
    if (attempt == null) {
      if (!base) {
        fn += (gold_phones.length / 2);
      } else {
        fn_base += (gold_phones.length / 2);
      }
    } else {
      String[] attempt_phones = attempt.split("-");
      if (!base) {
        fn += ((gold_phones.length / 2) - (attempt_phones.length));
      } else {
        fn_base += ((gold_phones.length / 2) - (attempt_phones.length));
      }
      int last_idx = 0;
      for (int i = 0; i < gold_phones.length - 1; i += 2) {
        String phone = gold_phones[i];
        String idx = gold_phones[i + 1];
        int currIdx = Integer.parseInt(idx);
        if (attempt_phones.length > currIdx) {
          if (attempt_phones[currIdx].equals(phone)) {
            if (!base) {
              tp++;
            } else {
              tp_base++;
            }
          } else {
            if (last_idx != currIdx) {
              if (!base) {
                fp++;
              } else {
                fp_base++;
              }
            }
          }
        }
        last_idx = currIdx;
      }
    }
  }
}
