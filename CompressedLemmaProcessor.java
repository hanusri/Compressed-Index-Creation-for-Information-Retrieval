import javafx.geometry.Pos;
import sun.awt.image.ImageWatched;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
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
            LinkedList<PostingNode> invertedIndexValueEntry = termEntry.getValue();
            String term = termEntry.getKey();
            termPointer = termConstructor.length();
            termConstructor.append(term.length() + term);
            int currentTermInBlock = termCount % Constants.BLOCK_SIZE;
            if (currentTermInBlock == 0) {
                if (block != null)
                    ApplicationRunner.getBlockCollection().getBlocks().add(block);
                block = new Block();
                block.setTermPointer(termPointer);
            }
            block.setDocFrequency(currentTermInBlock, invertedIndexValueEntry.size());
            block.getPostingPointers().add(Utility.gammaEncode(invertedIndexValueEntry));
            termCount++;
        }

        if (block.getPostingPointers().size() != 0)
            ApplicationRunner.getBlockCollection().getBlocks().add(block);

        ApplicationRunner.getBlockCollection().setTerm(termConstructor.toString());
    }

    @Override
    public void writeFile() {
        Utility.serializeObject(Constants.COMPRESSED_INDEX_VERSION1_FILENAME, ApplicationRunner.getBlockCollection(),
                ApplicationRunner.getLemmaDocumentMap());
    }

    @Override
    public void printDitionary() {

    }
}
