import java.io.*;
import java.util.*;

/**
 * Created by Srikanth on 1/31/2017.
 */
public class ApplicationRunner {
    private static SortedMap<String, LinkedList<PostingNode>> lemmaDictionary;
    private static SortedMap<String, LinkedList<PostingNode>> stemmingDictionary;
    private static HashMap<Integer, DocumentNode> lemmaDocumentMap;
    private static HashMap<Integer, DocumentNode> stemmingDocumentMap;
    private static BlockCollection blockCollection;
    private static HashSet<String> stopWords;
    private static IProcessor iProcessor;

    public ApplicationRunner() {

    }


    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println("Please provide valid dataset path and stop words path");
        else {
            lemmaDictionary = new TreeMap<>();
            stemmingDictionary = new TreeMap<>();
            lemmaDocumentMap = new HashMap<>();
            stemmingDocumentMap = new HashMap<>();
            stopWords = new HashSet<>();
            blockCollection = new BlockCollection();
            // load stop words
            loadStopwords(args[1]);
            processFile(args[0]);
        }
    }

    private static void loadStopwords(String stopwordsPath) {
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            File stopwordsFile = new File(stopwordsPath);
            fileInputStream = new FileInputStream(stopwordsFile);
            dataInputStream = new DataInputStream(fileInputStream);
            bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stopWords.add(line.toLowerCase());
            }
        } catch (Exception e) {
            try {
                fileInputStream.close();
                dataInputStream.close();
                bufferedReader.close();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void processFile(String directoryPath) {
        // get the list of files
        File[] lstFiles = Utility.getFiles(directoryPath);

        if (lstFiles.length == 0)
            System.out.println("Error loading the directory");
        else {
            iProcessor = new UncompressedLemmaProcessor(lstFiles);
            iProcessor.execute();
            iProcessor.writeFile();

            iProcessor = new UncompressedStemProcessor(lstFiles);
            iProcessor.execute();
            iProcessor.writeFile();
        }
    }

    public static SortedMap<String, LinkedList<PostingNode>> getLemmaDictionary() {
        return lemmaDictionary;
    }

    public static SortedMap<String, LinkedList<PostingNode>> getStemmingDictionary() {
        return stemmingDictionary;
    }

    public static HashMap<Integer, DocumentNode> getLemmaDocumentMap() {
        return lemmaDocumentMap;
    }

    public static HashMap<Integer, DocumentNode> getStemmingDocumentMap() {
        return stemmingDocumentMap;
    }

    public static HashSet<String> getStopWords() {
        return stopWords;
    }
}
