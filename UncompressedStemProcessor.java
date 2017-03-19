import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Srikanth on 3/16/2017.
 */
public class UncompressedStemProcessor implements IProcessor {

    private File[] lstFiles;

    public UncompressedStemProcessor(File[] lstFiles) {
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
            Stemmer stemmer;
            for (String word : tokenizedWords) {
                // Ignore stop words
                if (!ApplicationRunner.getStopWords().contains(word)) {
                    // perform stemming
                    stemmer = new Stemmer();
                    stemmer.add(word.toCharArray(), word.length());
                    stemmer.stem();
                    word = stemmer.toString();
                    // add postingnode to invertedmap
                    LinkedList<PostingNode> postingNodes = ApplicationRunner.getStemmingDictionary().get(word);
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
                    ApplicationRunner.getStemmingDictionary().put(word, postingNodes);
                }
            }
            documentNode.setMaxFrequentTerm(maxTerm);
            documentNode.setMaxTermFrequency(maxTermFreq);
            ApplicationRunner.getStemmingDocumentMap().put(docId, documentNode);
        }
    }

    @Override
    public void writeFile() {
        Utility.serializeObject(Constants.UNCOMPRESSED_INDEX_VERSION2_FILENAME, ApplicationRunner.getStemmingDictionary(),
                ApplicationRunner.getStemmingDocumentMap());
    }

    @Override
    public void printDitionary() {

    }
}
