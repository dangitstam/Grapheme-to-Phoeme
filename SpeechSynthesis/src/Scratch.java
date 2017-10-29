/**
 * Created by dangitstam on 8/12/17.
 */

import java.io.*;
import java.util.*;

public class Scratch {

  public static void main(String args[]) throws IOException {
    String corpusPath = new File("")
            .getAbsolutePath()
            .concat("/SpeechSynthesis/corpus_test.txt");
    File corpusFile = new File(corpusPath);
    BufferedReader readCorpus = new BufferedReader(new FileReader(corpusFile));

    Set<String> linesOrdered = new TreeSet<>();
    String line;
    while ((line = readCorpus.readLine()) != null) {
      linesOrdered.add(line);
    }

    PrintWriter corpusOrdered = new PrintWriter("corpus.txt", "UTF-8");
    for (String s : linesOrdered) {
      corpusOrdered.println(s);
    }

    corpusOrdered.close();
  }
}
