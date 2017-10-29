import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File("corpus_test.txt");
        BufferedReader readFile = new BufferedReader(new FileReader(file));
        ParseModifiedBoston pmb = new ParseModifiedBoston();
        pmb.parseFile(readFile);
        // System.out.println(pmb.getNetwork());
        GraphemeToPhonemeCalculator gp_calculator = new GraphemeToPhonemeCalculator(pmb.getNetwork(),
                pmb.getTransitions(),pmb.getGraphemes(), pmb.getNormalizedPhones());
        SimpleGraphemePhoneme sgp_calculator = new SimpleGraphemePhoneme(pmb.getNetwork(),
                pmb.getTransitions(),pmb.getGraphemes(), pmb.getNormalizedPhones());

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String inputLine;
        System.out.println("Here are the available graphemes: ");
        Set<String> graphs = new TreeSet<String>(pmb.getGraphemes().keySet());
        System.out.println(graphs);
        System.out.println("Each pair of graphemes should have a hyphen between them: ");
        System.out.println("Example: wh-a-t");
        System.out.print("Please provide a string to parse using these graphemes: ");
        while ((inputLine = console.readLine()) != null && !inputLine.equals("quit")) {
            System.out.println("Using the modified Viterbi algorithm: " +
                                gp_calculator.calculatePhoneme(inputLine));
            System.out.println("Baseline: " +
                               sgp_calculator.getSimpleMapping(inputLine));
            System.out.print("Please provide a string to parse using these graphemes: ");
        }
    }
}
