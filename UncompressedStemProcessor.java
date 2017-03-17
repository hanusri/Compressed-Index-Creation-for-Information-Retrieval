import java.io.File;

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

    }

    @Override
    public void processTerm() {

    }
}
