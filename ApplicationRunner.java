import java.io.File;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Srikanth on 1/31/2017.
 */
public class ApplicationRunner {
    private SortedMap<String, PostingNode> lemmaDictionary;
    private SortedMap<String, PostingNode> stemmingDictionary;
    private HashMap<Integer, DocumentNode> lemmaDocumentMap;
    private HashMap<Integer, DocumentNode> stemmingDocumentMap;

    public ApplicationRunner() {
        lemmaDictionary = new TreeMap<>();
        stemmingDictionary = new TreeMap<>();
        lemmaDocumentMap = new HashMap<>();
        stemmingDocumentMap = new HashMap<>();
    }

    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Please provide valid dataset path");
        else {
            processFile(args[0]);
        }
    }

    private static void processFile(String directoryPath) {
        // get the list of files
        File[] lstFiles = Utility.getFiles(directoryPath);

        if (lstFiles.length == 0)
            System.out.println("Error loading the directory");
        else {
            System.out.println("Tokenization Analysis of Documents");
            System.out.println("----------------------------------");
            TokenSummary tokenSummary = new TokenSummary();
            DatasetProcessor datasetProcessor = new DatasetProcessor(tokenSummary, lstFiles);
            System.out.println("Stemming Analysis of Documents");
            System.out.println("------------------------------");
            StemmingExecutor stemmingExecutor = new StemmingExecutor(tokenSummary);
            stemmingExecutor.processStemming();
        }
    }
}
