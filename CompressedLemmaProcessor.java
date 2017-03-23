import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Srikanth on 3/18/2017.
 */
public class CompressedLemmaProcessor implements IProcessor {

    @Override
    public void execute() {

        StringBuilder termConstructor = new StringBuilder();
        int termPointer = 0;
        int termCount = 0;
        Block block = null;

        for (Map.Entry<String, LinkedList<PostingNode>> termEntry : ApplicationRunner.getLemmaDictionary().entrySet()) {
            LinkedList<PostingNode> postingLists = termEntry.getValue();
            String term = termEntry.getKey();
            termPointer = termConstructor.length();
            termConstructor.append(term.length() + term);
            int currentTermInBlock = termCount % Constants.BLOCK_SIZE;
            if (currentTermInBlock == 0) {
                if (block != null)
                    ApplicationRunner.getLemmaBlockCollection().getBlocks().add(block);
                block = new Block();
                block.setTermPointer(termPointer);
            }
            block.setDocFrequency(currentTermInBlock, postingLists.size());
            byte[] postingList = Utility.encode(postingLists, true);
            block.getPostingPointers().add(postingList);
            List<Integer> termFreqs = new ArrayList<>();
            // set term frequencies
            for (int i = 0; i < postingLists.size(); i++) {
                termFreqs.add(postingLists.get(i).getTermFrequency());
            }
            byte[] termFrequency = Utility.encode(termFreqs, true);
            block.getTermFrequencies().add(termFrequency);
            termCount++;

            if (ApplicationRunner.getLemmaCompressedStatistics().containsKey(term)) {
                TermStatisticsEntry termStatisticsEntry = new TermStatisticsEntry(postingList, termFrequency);
                ApplicationRunner.getLemmaCompressedStatistics().put(term, termStatisticsEntry);
            }
        }

        if (block.getPostingPointers().size() != 0)
            ApplicationRunner.getLemmaBlockCollection().getBlocks().add(block);

        ApplicationRunner.getLemmaBlockCollection().setTerm(termConstructor.toString());
        constructCompressedDocumentMap();
    }

    private void constructCompressedDocumentMap() {
        for (Map.Entry<Integer, DocumentNode> document : ApplicationRunner.getLemmaDocumentMap().entrySet()) {
            Integer documentId = document.getKey();
            DocumentNode documentNode = document.getValue();

            CompressedDocumentNode compressedDocumentNode = new CompressedDocumentNode();
            compressedDocumentNode.setDocumentLength(Utility.encode(documentNode.getDocumentLength(), true));
            compressedDocumentNode.setMaxTermFrequency(Utility.encode(documentNode.getMaxTermFrequency(), true));
            ApplicationRunner.getLemmaCompressedDocumentMap().put(documentId, compressedDocumentNode);
        }
    }

    @Override
    public void writeFile() {
        Utility.serializeObject(Constants.COMPRESSED_INDEX_VERSION1_FILENAME, ApplicationRunner.getLemmaBlockCollection(),
                ApplicationRunner.getLemmaCompressedDocumentMap());
    }

}
