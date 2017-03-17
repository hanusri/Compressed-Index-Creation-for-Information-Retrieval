import java.io.File;
import java.util.ArrayList;

/**
 * Created by Srikanth on 3/16/2017.
 */
public class UncompressedLemmaProcessor implements IProcessor {

    private File[] lstFiles;

    public UncompressedLemmaProcessor(File[] lstFiles) {
        this.lstFiles = lstFiles;
    }

    @Override
    public void execute() {
        for (int docId = 0; docId < lstFiles.length; docId++) {
            File file = lstFiles[docId];
            ArrayList<String> tokenizedWords = Utility.processFiles(file);
            
        }
    }

    @Override
    public void processTerm() {

    }
}
