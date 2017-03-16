import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by Srikanth on 1/31/2017.
 */
public class DatasetProcessor {

    private TokenSummary tokenSummary;
    private File[] lstFiles;
    // region Constructor

    public DatasetProcessor(TokenSummary tokenSummary, File[] listFiles) {
        this.tokenSummary = tokenSummary;
        lstFiles = listFiles;
        processFiles();
    }

    // endregion

    // region Private Methods

    private void processFiles() {
        FileInputStream fis = null;
        DataInputStream dis = null;
        BufferedReader bufferedReader = null;

        try {
            for (int docId = 0; docId < lstFiles.length; docId++) {
                File file = lstFiles[docId];
                if (file.isFile()) {
                    // increase the document count
                    tokenSummary.incrementDocumentCount();
                    fis = new FileInputStream(file);
                    dis = new DataInputStream(fis);
                    bufferedReader = new BufferedReader(new InputStreamReader(dis));

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        StringTokenizer tokenizer = new StringTokenizer(line, Constants.TOKENIZER_SPLIT);
                        while (tokenizer.hasMoreTokens()) {

                            String word = tokenizer.nextToken().trim().toLowerCase();
                            // remove words which are xml tags
                            if (word.matches("<[^>]+>") || word.matches("<\\/[^>]+>"))
                                continue;


                            // split the word if it has - or _ or white spaces like tab or \n
                            String[] subWords = word.split("\\s+|\\-|\\_|\\(|\\)|\\,|\\\\|\\/");

                            for (String subWord : subWords) {

                                if (subWord.trim().isEmpty())
                                    continue;

                                // remove workds which are just numbers or just symbols
                                if (subWord.matches("(\\d)*") || subWord.matches("(\\d)*.") || subWord.matches("(\\d)*.(\\d)*") ||
                                        subWord.matches("[^\\w\\s]+"))
                                    continue;

                                // handle the 's by spliting the part and taking the actual work
                                if (subWord.matches("(.*)\\'s"))
                                    subWord = subWord.replace("'s", "");

                                //tokenSummary.addToDictionary(subWord);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getCause());
        }
    }


    //endregion
}
