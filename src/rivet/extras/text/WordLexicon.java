package rivet.extras.text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import rivet.core.arraylabels.Labels;
import rivet.core.arraylabels.RIV;
import rivet.core.util.DCounter;
import rivet.core.util.Pair;
import rivet.core.util.ProbabilisticBag;
import rivet.core.util.Util;
import rivet.extras.text.lda.RIVTopicHeirarchy;
import rivet.extras.text.lda.ProbabilisticRIVBag;
import rivet.extras.text.lda.RIVTopic;

public class WordLexicon extends DualHashBidiMap<String, RIV> {
    private final int size;
    
    private RIVTopicHeirarchy topics;
    
    public WordLexicon(int size) {super(); this.size = size; topics = RIVTopicHeirarchy.makeRoot(RIVTopic.make(new ProbabilisticRIVBag(size), "root")); }
    
    public RIV meanVector() {
        return values().stream()
                .reduce(new RIV(size), Labels::addLabels)
                .divideBy((double)size());
    }
    
    public void buildTopicHeirarchy (double fuzziness) {
        final RIV meanVector = meanVector();
        final List<Pair<RIV, Double>> rivs = 
                values().stream()
                .map((riv) -> Pair.make(riv, Labels.similarity(riv, meanVector)))
                .collect(Collectors.toList());
        
        /*
         * Ultimately, I first want to break the rivs into groups such that the size of each 
         * group is roughly proportionate to the likely number of nodes at that level in the tree:
         * root should be small and sizes probably ought to increase at some vaguely exponential rate.
         * 
         * Given a set if groups of varying sizes, I then want to cluster
         * the elements of each group by similarity into nodes.
         * 
         * Finally, I want to go through each group (in descending order of heirarchy)
         *      for each node in that group
         *              connect it (Heirarchy.adopt()?) to the node in the previous
         *              group to which it bears the greatest similarity
         *
         * then return the root.
         */
        

        //break the rivs into groups. Each group represents a level in the heirarchy.
        final List<Pair<RIV, Double>> unsafeRIVs = Util.copyList(rivs);
        ArrayList<RIVWeb> groups = new ArrayList<>();
        
        while (unsafeRIVs.size() > 0) {
            final DCounter f = new DCounter(1);
            final RIVWeb bag = new RIVWeb(size);
            while (bag.count() < 1) {
                f.dec(fuzziness);
                final List<Pair<RIV, Double>> setRIVs = unsafeRIVs.stream()
                    .filter(p -> p.right > f.get())
                    .collect(Collectors.toList());
                unsafeRIVs.removeAll(setRIVs);
                setRIVs.stream()
                    .map(p -> p.left)
                    .forEach(web::add);
            }
            groups.add(web);
        }
        
        System.out.println("Topic Heirarchy is now believed to have depth " + groups.size());
        
        RIVTopic rootTopic = RIVTopic.make(groups.get(0), "root");
        groups.remove(0);
        
        //next, form subgroups by clustering the elements of each group by similarity
        ArrayList<ArrayList<ProbabilisticRIVBag>> clusteredGroups = new ArrayList<>();
        for (ProbabilisticRIVBag bag : groups) {
            final DCounter targetDistance = new DCounter(1);
            final int minNeighbors = 3;
            
            //Find center nodes (nodes with more than minNeighbors within targetDistance that don't share neighbors.
            ArrayList<ProbabilisticBag<Pair<RIV,Double>>> possibleNodes = new ArrayList<>();
            while (possibleNodes.size() < 1) {
                targetDistance.dec(fuzziness);
                possibleNodes.clear();
                bag.keyStream()
                    .map((riv) -> bag.neighbors(riv, targetDistance.get()))
                    .filter((b) -> b.count() >= minNeighbors)
                    .forEach(possibleNodes::add);
            }
        }
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = -8893148657223464815L;
    
        
}
