import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Srikanth on 1/31/2017.
 */
public class StemmingExecutor {
    private TokenSummary tokenSummary;
    private TokenSummary tokenStemSummary;

    public StemmingExecutor(TokenSummary tokenSummary) {
        this.tokenSummary = tokenSummary;
        this.tokenStemSummary = new TokenSummary();
    }

    public void processStemming() {
        HashMap<String, Integer> tokenMapData = tokenSummary.getTokenMap();
        HashMap<String, Integer> tokenStemMapData = tokenStemSummary.getTokenMap();
        long startTime = System.currentTimeMillis();
        Stemmer stemmer;
        String stem;
        for (Map.Entry<String, Integer> tokenEntry : tokenMapData.entrySet()) {
            String token = tokenEntry.getKey();
            int count = tokenEntry.getValue();

            stemmer = new Stemmer();
            stemmer.add(token.toCharArray(), token.length());
            stemmer.stem();
            stem = stemmer.toString();

            if (!tokenStemMapData.containsKey(stem)) {
                tokenStemMapData.put(stem, count);
            } else {
                tokenStemMapData.put(stem, count + tokenStemMapData.get(stem));
            }
        }

        long endTime = System.currentTimeMillis();
        tokenStemSummary.setTokenMap(tokenStemMapData);
        tokenStemSummary.setTimeTake(endTime - startTime);
        // set document size
        tokenStemSummary.setDocumentCount(tokenSummary.getDocumentCount());
        int totalStemCount = 0;
        int onlyOccurrenceCount = 0;
        for (String key : tokenStemMapData.keySet()) {
            totalStemCount += tokenStemMapData.get(key);
            if(tokenStemMapData.get(key) == 1)
                onlyOccurrenceCount++;
        }
        // set total count
        tokenStemSummary.setTokenCount(totalStemCount);
        tokenStemSummary.setOnlyOccuranceCount(onlyOccurrenceCount);
        tokenStemSummary.printStatistics();

    }
}
