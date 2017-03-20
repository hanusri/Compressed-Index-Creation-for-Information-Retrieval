import java.io.Serializable;

/**
 * Created by Srikanth on 3/19/2017.
 */
public class CompressedDocumentNode implements Serializable {
    private byte[] maxTermFrequency;
    private byte[] documentLength;


    public CompressedDocumentNode() {
        maxTermFrequency = null;
        documentLength = null;
    }

    public byte[] getMaxTermFrequency() {
        return maxTermFrequency;
    }

    public void setMaxTermFrequency(byte[] maxTermFrequency) {
        this.maxTermFrequency = maxTermFrequency;
    }

    public byte[] getDocumentLength() {
        return documentLength;
    }

    public void setDocumentLength(byte[] documentLength) {
        this.documentLength = documentLength;
    }
}
