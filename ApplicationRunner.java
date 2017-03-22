import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

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
    private static HashMap<Integer, CompressedDocumentNode> lemmaCompressedDocumentMap;
    private static HashMap<Integer, CompressedDocumentNode> stemmingCompressedDocumentMap;
    private static BlockCollection lemmaBlockCollection;
    private static BlockCollection stemmingBlockCollection;
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
            lemmaCompressedDocumentMap = new HashMap<>();
            stemmingCompressedDocumentMap = new HashMap<>();
            stopWords = new HashSet<>();
            lemmaBlockCollection = new BlockCollection();
            stemmingBlockCollection = new BlockCollection();
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
            System.out.println("Uncompressed Index Version 1 started...");
            long startTime = System.currentTimeMillis();
            iProcessor = new UncompressedLemmaProcessor(lstFiles);
            iProcessor.execute();
            iProcessor.writeFile();
            long endTime = System.currentTimeMillis();
            System.out.println("Uncompressed Index Version 1 completed...");
            System.out.println("Time taken to complete Uncompressed Version 1 is " + (endTime - startTime) + " ms");

            System.out.println("Uncompressed Index Version 2 started...");
            startTime = System.currentTimeMillis();
            iProcessor = new UncompressedStemProcessor(lstFiles);
            iProcessor.execute();
            iProcessor.writeFile();
            endTime = System.currentTimeMillis();
            System.out.println("Uncompressed Index Version 2 completed...");
            System.out.println("Time taken to complete Uncompressed Version 2 is " + (endTime - startTime) + " ms");

            System.out.println("Compressed Index Version 1 started...");
            startTime = System.currentTimeMillis();
            iProcessor = new CompressedLemmaProcessor();
            iProcessor.execute();
            iProcessor.writeFile();
            endTime = System.currentTimeMillis();
            System.out.println("Compressed Index Version 1 completed...");
            System.out.println("Time taken to complete Compressed Version 1 is " + (endTime - startTime) + " ms");

            System.out.println("Compressed Index Version 2 started...");
            startTime = System.currentTimeMillis();
            iProcessor = new CompressedStemProcessor();
            iProcessor.execute();
            iProcessor.writeFile();
            endTime = System.currentTimeMillis();
            System.out.println("Compressed Index Version 2 completed...");
            System.out.println("Time taken to complete Compressed Version 2 is " + (endTime - startTime) + " ms");

            printInformation();
        }
    }

    private static void printInformation() {
        System.out.println("*******File Size Information***********");
        System.out.println("Index size of Version 1 uncompressed (in bytes):    " + new File(Constants.UNCOMPRESSED_INDEX_VERSION1_FILENAME).length());
        System.out.println("Index size of Version 1 compressed (in bytes):  " + new File(Constants.COMPRESSED_INDEX_VERSION1_FILENAME).length());
        System.out.println("Index size of Version 2 uncompressed (in bytes):  " + new File(Constants.UNCOMPRESSED_INDEX_VERSION2_FILENAME).length());
        System.out.println("Index size of Version 2 compressed (in bytes):  " + new File(Constants.COMPRESSED_INDEX_VERSION2_FILENAME).length());
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

    public static BlockCollection getLemmaBlockCollection() {
        return lemmaBlockCollection;
    }

    public static HashMap<Integer, CompressedDocumentNode> getLemmaCompressedDocumentMap() {
        return lemmaCompressedDocumentMap;
    }

    public static HashMap<Integer, CompressedDocumentNode> getStemmingCompressedDocumentMap() {
        return stemmingCompressedDocumentMap;
    }

    public static BlockCollection getStemmingBlockCollection() {
        return stemmingBlockCollection;
    }
}
