import java.io.Serializable;

/**
 * Created by Srikanth on 3/18/2017.
 */
public class Block implements Serializable {
    private int termPointer;
    private byte[] encodeBytes;
    //TODO: Need to change doc frequency of all terms in the block to byte array
    private int[] docFrequency;

    public int getTermPointer() {
        return termPointer;
    }

    public void setTermPointer(int termPointer) {
        this.termPointer = termPointer;
    }

    public byte[] getEncodeBytes() {
        return encodeBytes;
    }

    public void setEncodeBytes(byte[] encodeBytes) {
        this.encodeBytes = encodeBytes;
    }

    public int[] getDocFrequency() {
        return docFrequency;
    }

    public void setDocFrequency(int[] docFrequency) {
        this.docFrequency = docFrequency;
    }
}
