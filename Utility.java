import java.io.*;
import java.util.*;

/**
 * Created by Srikanth on 1/31/2017.
 */
public class Utility {

    public static File[] getFiles(String directoryPath) {

        try {
            File folderPath = new File(directoryPath);
            return folderPath.listFiles();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return new File[0];
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMap(final Map<K, V> mapToSort) {
        List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(mapToSort.size());

        entries.addAll(mapToSort.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            public int compare(final Map.Entry<K, V> entry1, final Map.Entry<K, V> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static ArrayList<String> processFiles(File file) {
        FileInputStream fis = null;
        DataInputStream dis = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> tokenizedWords = new ArrayList<>();
        try {

            if (file.isFile()) {
                // increase the document count
                fis = new FileInputStream(file);
                dis = new DataInputStream(fis);
                bufferedReader = new BufferedReader(new InputStreamReader(dis));

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    StringTokenizer tokenizer = new StringTokenizer(line, Constants.TOKENIZER_SPLIT);
                    while (tokenizer.hasMoreTokens()) {

                        String word = tokenizer.nextToken().trim().toLowerCase();
                        // remove words which are xml tags
                        if (word.matches("<[^>]+>") || word.matches("<\\/[^>]+>"))
                            continue;


                        // split the word if it has - or _ or white spaces like tab or \n
                        String[] subWords = word.split("\\s+|\\-|\\_|\\(|\\)|\\,|\\\\|\\/");

                        for (String subWord : subWords) {

                            if (subWord.trim().isEmpty())
                                continue;

                            // remove workds which are just numbers or just symbols
                            if (subWord.matches("(\\d)*") || subWord.matches("(\\d)*.") || subWord.matches("(\\d)*.(\\d)*") ||
                                    subWord.matches("[^\\w\\s]+"))
                                continue;

                            // handle the 's by spliting the part and taking the actual work
                            if (subWord.matches("(.*)\\'s"))
                                subWord = subWord.replace("'s", "");

                            //tokenSummary.addToDictionary(subWord);
                            tokenizedWords.add(subWord);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getCause());
        }

        return tokenizedWords;
    }

    public static void serializeObject(String indexFileName, Object dictionary, Object documentMap) {
        File file = new File(indexFileName);
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        if (file.exists()) {
            file.delete();
        }

        try {
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dictionary);
            objectOutputStream.writeObject(documentMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static void EliasEncode(int gap, BitOutputStream output, boolean isGamma) {

        String encodedString;
        if (isGamma) {
            encodedString = gammaEncode(gap);
        } else {
            encodedString = deltaEncode(gap);
        }

        for (int i = encodedString.length() - 1; i >= 0; i--) {
            if (encodedString.charAt(i) == '0') {
                output.writeBit(0);
            } else if (encodedString.charAt(i) == '1') {
                output.writeBit(1);
            }
        }
    }

    private static String gammaEncode(int num) {
        if (num == 0) {
            return "0";
        }
        String binary = numToBinary(num);
        String offset = binary.substring(1);
        int offsetSize = offset.length();
        String offsetUnaryCode = getUnaryCode(offsetSize);
        String gammaCode = offsetUnaryCode + offset;
        return gammaCode;
    }

    private static String deltaEncode(int num) {
        String binary = numToBinary(num);
        String offset = binary.substring(1);
        int binaryLen = binary.length();
        String gammaEncodedString = gammaEncode(binaryLen);
        String deltaEncodedString = gammaEncodedString + offset;
        return deltaEncodedString;
    }

    public static String numToBinary(int num) {
        if (num == 0)
            return "0";

        String s = "";
        while (num > 0) {
            s = num % 2 + s;
            num /= 2;
        }
        return s;
    }

    public static int binaryToNum(String s) {
        int num = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            char ch = s.charAt(i);
            if (ch == '1') {
                num += (int) Math.pow(2, i);
            }
        }
        return num;
    }


    public static String getUnaryCode(int count) {
        String s = "";
        for (int i = 0; i < count; i++) {
            s += "1";
        }
        s += "0";
        return s;
    }

    public static byte[] encode(LinkedList<PostingNode> postingNodes, boolean isGamma) {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteOutput);
        int gap;
        int previousDocId = 0;
        for (int i = 0; i < postingNodes.size(); i++) {
            int currentDocId = postingNodes.get(i).getDocumentId();
            if (i == 0) {
                gap = currentDocId;
            } else {
                gap = currentDocId - previousDocId;
            }
            previousDocId = currentDocId;
            EliasEncode(gap, bitOutputStream, isGamma);
        }
        bitOutputStream.close();
        return byteOutput.toByteArray();
    }

    public static byte[] encode(List<Integer> list, boolean isGamma) {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteOutput);

        for (int i = 0; i < list.size(); i++) {
            EliasEncode(list.get(i), bitOutputStream, isGamma);
        }
        bitOutputStream.close();
        return byteOutput.toByteArray();
    }

    public static byte[] encode(int value, boolean isGamma) {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteOutput);
        EliasEncode(value, bitOutputStream, isGamma);
        bitOutputStream.close();
        return byteOutput.toByteArray();
    }

    public static int getUncompressedPostingListSize(List<PostingNode> postingNodes) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {

            out = new ObjectOutputStream(bos);
            out.writeObject(postingNodes);
            out.flush();
            byte[] byteData = bos.toByteArray();
            return byteData.length;

        } catch (Exception ex) {

        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return 0;
    }

    public static int getCompressedPostingListSize(TermStatisticsEntry termStatisticsEntry, int termLength) {
        int size = 8 + 8 + termLength;
        size += termStatisticsEntry.getPostingList().length;
        size += termStatisticsEntry.getTermFrequencies().length;
        return size;
    }


}
