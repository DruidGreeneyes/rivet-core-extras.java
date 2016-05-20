package rivet.extras.text.lda;

import java.util.HashMap;
import java.util.Random;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.ArrayList;
import java.util.Arrays;

import rivet.core.util.Pair;
import rivet.core.util.Util;


public final class LDA {
    private LDA(){}
    
    public static final double[] generateTopicProbabilities(final WDTEntry entry, final int documentLength, final ArrayList<WDTEntry> topicEntries, final ProbabilisticWordBag[] topicBags, final int[] topicNums, final int numTopics) {
        final double[] probs = new double[numTopics];
        for (int topic : topicNums) {
            final double timesThisTopicInDocument =
                    topicEntries.stream()
                        .filter((e) ->
                            e.document == entry.document && 
                            e.topic == topic)
                        .count();

            final double pTopicGivenDocument =
                    timesThisTopicInDocument / documentLength;
            final double pWordGivenTopic = topicBags[topic].probability(entry.word);
            probs[topic] = pTopicGivenDocument * pWordGivenTopic;
        }

        final double factor = Arrays.stream(probs).sum();
        return Arrays.stream(probs).map((p) -> p / factor).toArray();
    }
    
    public static final Pair<ArrayList<WDTEntry>, ProbabilisticWordBag[]> gibbsSample(final String[][] texts, final ArrayList<WDTEntry> topicEntries, final ProbabilisticWordBag[] topicBags, final int[] topicNums, final int numTopics) {
        
        final ProbabilisticWordBag[] newBags = topicBags.clone();
        final ArrayList<WDTEntry> newEntries = new ArrayList<>(topicEntries.size());
        
        for (WDTEntry entry : topicEntries) {
            final int newTopic = 
                    new EnumeratedIntegerDistribution(
                            topicNums, 
                            generateTopicProbabilities(
                                    entry,
                                    texts[entry.document].length,
                                    topicEntries,
                                    topicBags,
                                    topicNums,
                                    numTopics))
                    .sample();
            if (newTopic != entry.topic) {
                newBags[entry.topic].subtract(entry.word);
                newBags[newTopic].add(entry.word);
                newEntries.add(entry.updateTopic(newTopic));
            } else {
                newEntries.add(entry);
            }
        }
        
        return Pair.make(newEntries, newBags);
    }

    public static final HashMap<String, ProbabilisticWordBag> buildLDA (final String[][] texts, final int numTopics, final double changeThreshold) {
        final int[] topicNums = Util.range(numTopics).toArray();
        final ProbabilisticWordBag[] topicBags = 
                Arrays.stream(topicNums)
                .mapToObj((x) -> new ProbabilisticWordBag())
                .toArray(ProbabilisticWordBag[]::new);
        final ArrayList<WDTEntry> topicEntries = new ArrayList<>();

        final Random r = new Random();
        for (int c = 0; c < texts.length; c++) {
            final String[] text = texts[c];
            for (String token : text) {
                final int topic = r.nextInt(numTopics);
                topicEntries.add(WDTEntry.make(token, c, topic));
                topicBags[topic].add(token);
            }
        }

        final double numWords = topicEntries.size();

        long changes;
        do {
            changes = 0;
            Pair<ArrayList<WDTEntry>, ProbabilisticWordBag[]> newSample = gibbsSample(
                    texts,
                    topicEntries,
                    topicBags,
                    topicNums,
                    numTopics);
            
            changes = topicEntries.stream().filter((e) -> newSample.left.contains(e)).count();
            
            topicEntries.clear();
            topicEntries.addAll(newSample.left);
            for (int i : topicNums) 
                topicBags[i] = newSample.right[i];
        } while (changes/numWords > changeThreshold);

        final HashMap<String, ProbabilisticWordBag> topics = new HashMap<>();
        Arrays.stream(topicBags).forEach((bag) -> topics.put(bag.name(), bag));

        return topics;
    }
}
