import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Srikanth on 3/18/2017.
 */
public class Block implements Serializable {
    private int termPointer;
    private List<byte[]> postingPointers;
    //TODO: Need to change doc frequency of all terms in the block to byte array
    private int[] docFrequencies;
    private List<byte[]> termFrequencies;

    public Block() {
        postingPointers = new ArrayList<>();
        docFrequencies = new int[Constants.BLOCK_SIZE];
        termFrequencies = new ArrayList<>();
    }

    public int getTermPointer() {
        return termPointer;
    }

    public void setTermPointer(int termPointer) {
        this.termPointer = termPointer;
    }

    public List<byte[]> getPostingPointers() {
        return postingPointers;
    }

    public void setPostingPointers(List<byte[]> postingPointers) {
        this.postingPointers = postingPointers;
    }

    public int[] getDocFrequencies() {
        return docFrequencies;
    }

    public void setDocFrequencies(int[] docFrequencies) {
        this.docFrequencies = docFrequencies;
    }

    public void setDocFrequency(int index, int docFrequency) {
        this.docFrequencies[index] = docFrequency;
    }

    public List<byte[]> getTermFrequencies() {
        return termFrequencies;
    }

    public void setTermFrequencies(List<byte[]> termFrequencies) {
        this.termFrequencies = termFrequencies;
    }
}
