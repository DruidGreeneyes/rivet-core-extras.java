package rivet.extras.text;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import rivet.core.labels.RandomIndexVector;
import rivet.core.labels.ArrayRIV;
import rivet.core.util.Pair;
import rivet.extras.text.lda.RIVTopicHeirarchy;
import rivet.extras.text.lda.ProbabilisticRIVBag;
import rivet.extras.text.lda.RIVTopic;

public class WordLexicon extends DualHashBidiMap<String, ArrayRIV> {
    private final int size;
    
    private RIVTopicHeirarchy topics;
    
    public WordLexicon(int size) {super(); this.size = size; topics = RIVTopicHeirarchy.makeRoot(RIVTopic.make(new ProbabilisticRIVBag(size), "root")); }
    
    public ArrayRIV meanVector() {
        return values().stream()
                .reduce(new ArrayRIV(size), ArrayRIV::add)
                .divide((double)size());
    }
    
    public void buildTopicHeirarchy (double fuzziness) {
        final ArrayRIV meanVector = meanVector();
        final List<Pair<ArrayRIV, Double>> rivs = 
                values().stream()
                .map((riv) -> Pair.make(riv, RandomIndexVector.similarity(riv, meanVector)))
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
        
        //next, form subgroups by clustering the elements of each group by similarity
        
            //Find center nodes (nodes with more than minNeighbors within targetDistance that don't share neighbors.
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = -8893148657223464815L;
    
        
}
