import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Srikanth on 3/16/2017.
 */
public class UncompressedLemmaProcessor implements IProcessor {

    private File[] lstFiles;

    public UncompressedLemmaProcessor(File[] lstFiles) {
        this.lstFiles = lstFiles;
    }

    @Override
    public void execute() {
        for (int docId = 0; docId < lstFiles.length; docId++) {
            File file = lstFiles[docId];
            ArrayList<String> tokenizedWords = Utility.processFiles(file);
            DocumentNode documentNode = new DocumentNode(docId);
            // set the document length
            documentNode.setDocumentLength(tokenizedWords.size());
            int maxTermFreq = 0;
            String maxTerm = "";
            Lemmatizer lemmatizer = new Lemmatizer();
            for (String word : tokenizedWords) {
                // Ignore stop words
                if (!ApplicationRunner.getStopWords().contains(word)) {
                    // lemmatize
                    List<String> lemmatizedWords = lemmatizer.lemmatize(word);
                    if (lemmatizedWords == null || lemmatizedWords.size() == 0)
                        continue;
                    word = lemmatizedWords.get(0);
                    // add postingnode to invertedmap
                    LinkedList<PostingNode> postingNodes = ApplicationRunner.getLemmaDictionary().get(word);
                    if (postingNodes == null || docId != postingNodes.get(postingNodes.size() - 1).getDocumentId()) {
                        PostingNode postingNode = new PostingNode(docId);
                        postingNode.setTermFrequency(1);

                        if (postingNodes == null)
                            postingNodes = new LinkedList<>();

                        postingNodes.add(postingNode);
                    } else {
                        PostingNode postingNode = postingNodes.get(postingNodes.size() - 1);
                        int currenttermFreq = postingNode.getTermFrequency();
                        currenttermFreq++;
                        if (currenttermFreq > maxTermFreq) {
                            maxTermFreq = currenttermFreq;
                            maxTerm = word;
                        }
                        postingNode.setTermFrequency(currenttermFreq);
                    }
                    ApplicationRunner.getLemmaDictionary().put(word, postingNodes);
                }
            }
            documentNode.setMaxFrequentTerm(maxTerm);
            documentNode.setMaxTermFrequency(maxTermFreq);
            ApplicationRunner.getLemmaDocumentMap().put(docId, documentNode);
        }
    }

    @Override
    public void writeFile() {
        Utility.serializeObject(Constants.UNCOMPRESSED_INDEX_VERSION1_FILENAME, ApplicationRunner.getLemmaDictionary(),
                ApplicationRunner.getLemmaDocumentMap());
    }

    @Override
    public void printDitionary() {

    }
}
