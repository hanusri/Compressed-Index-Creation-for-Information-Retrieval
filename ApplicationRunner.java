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
    private static HashMap<String, TermStatisticsEntry> lemmaCompressedStatistics;
    private static HashMap<String, TermStatisticsEntry> stemCompressedStatistics;
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
            lemmaCompressedStatistics = new HashMap<>();
            stemCompressedStatistics = new HashMap<>();
            // load stop words
            loadStopwords(args[1]);
            loadLemmaStemStatisticalMap();
            processFile(args[0]);
        }
    }

    private static void loadLemmaStemStatisticalMap() {
        Lemmatizer lemmatizer = new Lemmatizer();
        String[] terms = {"Reynolds", "NASA", "Prandtl", "flow", "pressure", "boundary", "shock"};
        // convert the terms to its lemmas
        Stemmer stemmer;
        for (int i = 0; i < terms.length; i++) {
            String term = terms[i].toLowerCase();
            String lemmaTerm = lemmatizer.lemmatize(term).get(0);
            lemmaCompressedStatistics.put(lemmaTerm, null);

            stemmer = new Stemmer();
            stemmer.add(term.toCharArray(), term.length());
            stemmer.stem();
            String stemTerm = stemmer.toString();
            stemCompressedStatistics.put(stemTerm, null);
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

        System.out.println("*******Inverted List Count***********");
        System.out.println("Number of inverted Lists in index V1(Both Compressed and Uncompressed): " + ApplicationRunner.getLemmaDictionary().size());
        System.out.println("Number of inverted Lists in index V2(Both Compressed and Uncompressed): " + ApplicationRunner.getStemmingDictionary().size());


        System.out.println("*******Analysis of Following Terms***********");
        printLemmaIndexInformation();
        printStemIndexInformation();

        System.out.println("*******More Information about term NASA***********");
        // print details related to Lemma
        List<PostingNode> lemmaPostingNodes = lemmaDictionary.get("nasa");
        System.out.println("Version 1 details");
        System.out.println("-----------------");
        System.out.println(String.format("\n\t %-10s \t %-10s \t %-10s \t %10s", "Doc ID", "Term Freq", "Doc Length", "Max Term Frequency"));
        for (int i = 0; i < 3; i++) {
            PostingNode postingNode = lemmaPostingNodes.get(i);
            int docId = postingNode.getDocumentId();
            int termFrequency = postingNode.getTermFrequency();
            DocumentNode documentNode = lemmaDocumentMap.get(docId);
            int documentLength = documentNode.getDocumentLength();
            int maxFrequency = documentNode.getMaxTermFrequency();
            System.out.println(String.format("\t %-10d \t %-10d \t %-10d \t %10d",
                    docId,
                    termFrequency,
                    documentLength,
                    maxFrequency));
        }
        // print details related to Stem
        List<PostingNode> stemmingPostingNodes = stemmingDictionary.get("nasa");
        System.out.println("Version 2 details");
        System.out.println("-----------------");
        System.out.println(String.format("\n\t %-10s \t %-10s \t %-10s \t %10s", "Doc ID", "Term Freq", "Doc Length", "Max Term Frequency"));
        for (int i = 0; i < 3; i++) {
            PostingNode postingNode = stemmingPostingNodes.get(i);
            int docId = postingNode.getDocumentId();
            int termFrequency = postingNode.getTermFrequency();
            DocumentNode documentNode = stemmingDocumentMap.get(docId);
            int documentLength = documentNode.getDocumentLength();
            int maxFrequency = documentNode.getMaxTermFrequency();
            System.out.println(String.format("\t %-10d \t %-10d \t %-10d \t %10d",
                    docId,
                    termFrequency,
                    documentLength,
                    maxFrequency));
        }
    }

    private static void printLemmaIndexInformation() {
        System.out.println("Verion 1 Analysis");
        System.out.println("-----------------");
        System.out.println(String.format("\n %-15s \t %-10s  %-10s %-10s %-10s ", "Type", "Lemma", "Doc Frequency", "Total Term Frequency", "Inverted list in bytes"));

        for (String lemmaTerm : lemmaCompressedStatistics.keySet()) {
            List<PostingNode> lemmaPostingNodes = ApplicationRunner.getLemmaDictionary().get(lemmaTerm);
            int totalTermFrequency = 0;

            for (PostingNode node : lemmaPostingNodes)
                totalTermFrequency += node.getTermFrequency();

            System.out.println(String.format(" %15s \t %-10s  \t %-10d \t %-10d \t %-10d",
                    "Uncompressed",
                    lemmaTerm,
                    lemmaPostingNodes.size(),
                    totalTermFrequency,
                    Utility.getUncompressedPostingListSize(lemmaPostingNodes, lemmaTerm.length())));

            System.out.println(String.format(" %15s \t %-10s  \t %-10d \t %-10d \t %-10d",
                    "Compressed",
                    lemmaTerm,
                    lemmaPostingNodes.size(),
                    totalTermFrequency,
                    Utility.getCompressedPostingListSize(getLemmaCompressedStatistics().get(lemmaTerm), lemmaTerm.length())));
        }
    }

    private static void printStemIndexInformation() {
        System.out.println("Verion 2 Analysis");
        System.out.println("-----------------");
        System.out.println(String.format("\n %-15s %-10s  %-10s %-10s %-10s ", "Type", "Stem", "Doc Frequency", "Total Term Frequency", "Inverted list in bytes"));

        for (String stemTerm : stemCompressedStatistics.keySet()) {
            List<PostingNode> stemPostingNodes = ApplicationRunner.getStemmingDictionary().get(stemTerm);
            int totalTermFrequency = 0;

            for (PostingNode node : stemPostingNodes)
                totalTermFrequency += node.getTermFrequency();

            System.out.println(String.format(" %15s %-10s  \t %-10d \t %-10d \t %-10d",
                    "Uncompressed",
                    stemTerm,
                    stemPostingNodes.size(),
                    totalTermFrequency,
                    Utility.getUncompressedPostingListSize(stemPostingNodes, stemTerm.length())));

            System.out.println(String.format(" %15s %-10s  \t %-10d \t %-10d \t %-10d",
                    "Compressed",
                    stemTerm,
                    stemPostingNodes.size(),
                    totalTermFrequency,
                    Utility.getCompressedPostingListSize(getStemCompressedStatistics().get(stemTerm), stemTerm.length())));
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

    public static HashMap<String, TermStatisticsEntry> getLemmaCompressedStatistics() {
        return lemmaCompressedStatistics;
    }

    public static void setLemmaCompressedStatistics(HashMap<String, TermStatisticsEntry> lemmaCompressedStatistics) {
        ApplicationRunner.lemmaCompressedStatistics = lemmaCompressedStatistics;
    }

    public static HashMap<String, TermStatisticsEntry> getStemCompressedStatistics() {
        return stemCompressedStatistics;
    }

    public static void setStemCompressedStatistics(HashMap<String, TermStatisticsEntry> stemCompressedStatistics) {
        ApplicationRunner.stemCompressedStatistics = stemCompressedStatistics;
    }
}
