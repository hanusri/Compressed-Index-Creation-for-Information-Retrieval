


import java.util.*;


/**
 * Created by Srikanth on 3/18/2017.
 */
public class CompressedStemProcessor implements IProcessor {

    @Override
    public void execute() {
        StringBuilder termConstructor = new StringBuilder();
        int termPointer = 0;
        int termCount = 0;
        String[] blockTerms = new String[Constants.BLOCK_SIZE];
        Block block = null;
        for (Map.Entry<String, LinkedList<PostingNode>> termEntry : ApplicationRunner.getStemmingDictionary().entrySet()) {
            LinkedList<PostingNode> postingLists = termEntry.getValue();
            String term = termEntry.getKey();

            int currentTermInBlock = termCount % Constants.BLOCK_SIZE;
            blockTerms[currentTermInBlock] = term;
            if (currentTermInBlock == 0) {
                if (block != null) {
                    termPointer = termConstructor.length();
                    StringBuilder currentBlockTermList = constructTermList(blockTerms);
                    termConstructor.append(currentBlockTermList);
                    block.setTermPointer(termPointer);
                    ApplicationRunner.getStemmingBlockCollection().getBlocks().add(block);
                    blockTerms = new String[Constants.BLOCK_SIZE];
                }
                block = new Block();
            }
            block.setDocFrequency(currentTermInBlock, postingLists.size());
            byte[] postingList = Utility.encode(postingLists, false);
            block.getPostingPointers().add(postingList);

            List<Integer> termFreqs = new ArrayList<>();
            // set term frequencies
            for (int i = 0; i < postingLists.size(); i++) {
                termFreqs.add(postingLists.get(i).getTermFrequency());
            }

            byte[] termFrequency = Utility.encode(termFreqs, false);
            block.getTermFrequencies().add(termFrequency);
            termCount++;

            if (ApplicationRunner.getStemCompressedStatistics().containsKey(term)) {
                TermStatisticsEntry termStatisticsEntry = new TermStatisticsEntry(postingList, termFrequency);
                ApplicationRunner.getStemCompressedStatistics().put(term, termStatisticsEntry);
            }
        }

        if (block.getPostingPointers().size() != 0) {
            ApplicationRunner.getStemmingBlockCollection().getBlocks().add(block);
        }

        ApplicationRunner.getStemmingBlockCollection().setTerm(termConstructor.toString());
        constructCompressedDocumentMap();
    }

    private StringBuilder constructTermList(String[] terms) {
        int k = 0;
        for (String string : terms)
            if (string != null)
                k++;

        if (k == 0)
            return new StringBuilder();

        String[] newTerms = new String[k];
        for (int i = 0; i < k; i++)
            newTerms[i] = terms[i];

        int len = 0;

        while (len < k) {
            char ch = newTerms[0].charAt(len);
            boolean breakFlag = false;
            for (int i = 1; i < k; i++) {
                if (len >= newTerms[i].length() || newTerms[i].charAt(len) != ch) {
                    breakFlag = true;
                    break;
                }
            }
            if (breakFlag)
                break;
            len++;
        }

        StringBuilder result = new StringBuilder();
        result.append(len+1);
        result.append(newTerms[0].substring(0, len));
        result.append("*");

        int count = 1;
        for (int i = 0; i < newTerms.length; i++) {
            if(newTerms[i].length() > len)
                result.append(newTerms[i].substring(len));
            if (i != newTerms.length - 1) {
                result.append(count);
                result.append('|');
            }
            count++;
        }
        return result;
    }

    private void constructCompressedDocumentMap() {
        for (Map.Entry<Integer, DocumentNode> document : ApplicationRunner.getStemmingDocumentMap().entrySet()) {
            Integer documentId = document.getKey();
            DocumentNode documentNode = document.getValue();

            CompressedDocumentNode compressedDocumentNode = new CompressedDocumentNode();
            compressedDocumentNode.setDocumentLength(Utility.encode(documentNode.getDocumentLength(), false));
            compressedDocumentNode.setMaxTermFrequency(Utility.encode(documentNode.getMaxTermFrequency(), false));
            ApplicationRunner.getStemmingCompressedDocumentMap().put(documentId, compressedDocumentNode);
        }
    }

    @Override
    public void writeFile() {
        Utility.serializeObject(Constants.COMPRESSED_INDEX_VERSION2_FILENAME, ApplicationRunner.getStemmingBlockCollection(),
                ApplicationRunner.getStemmingCompressedDocumentMap());
    }
}
