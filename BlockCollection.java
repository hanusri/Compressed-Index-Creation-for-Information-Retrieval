import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Srikanth on 3/18/2017.
 */
public class BlockCollection implements Serializable {
    private String term;
    private List<Block> blocks;

    public BlockCollection()
    {
        term = "";
        blocks = new ArrayList<>();
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
}
