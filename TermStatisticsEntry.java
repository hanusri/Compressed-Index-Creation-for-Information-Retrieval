/**
 * Created by Srikanth on 3/22/2017.
 */
public class TermStatisticsEntry {
    private byte[] postingList;
    private byte[] termFrequencies;

    public TermStatisticsEntry(byte[] postingList, byte[] termFrequencies) {
        this.postingList = postingList;
        this.termFrequencies = termFrequencies;
    }

    public byte[] getPostingList() {
        return postingList;
    }

    public void setPostingList(byte[] postingList) {
        this.postingList = postingList;
    }

    public byte[] getTermFrequencies() {
        return termFrequencies;
    }

    public void setTermFrequencies(byte[] termFrequencies) {
        this.termFrequencies = termFrequencies;
    }
}
