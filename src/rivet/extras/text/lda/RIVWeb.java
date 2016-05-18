package rivet.extras.text.lda;

import java.util.ArrayList;
import rivet.core.arraylabels.Labels;
import rivet.core.arraylabels.RIV;
import rivet.core.util.Pair;

public class RIVWeb {
    
    private final int size;
    private final RIV riv;
    private final ArrayList<Pair<RIVWeb, Double>> strongLinks;
    private final ArrayList<Pair<RIVWeb, Double>> weakLinks;
    private final double linkThreshold;
    
    private RIVWeb(int size, RIV riv, double linkThreshold, ArrayList<Pair<RIVWeb, Double>> strongLinks, ArrayList<Pair<RIVWeb, Double>> weakLinks) {
        this.riv = riv;
        this.size = size;
        this.linkThreshold = linkThreshold;
        this.strongLinks = strongLinks;
        this.weakLinks = weakLinks;
    }
    private RIVWeb(RIV riv, double linkThreshold) {
        this(
                riv.size(),
                riv,
                linkThreshold,
                new ArrayList<>(),
                new ArrayList<>());
    }
    
    private void strongLink (Pair<RIVWeb, Double> node) {
        strongLinks.add(node);
        node.left.strongLinks.add(Pair.make(this, node.right));
    }
    
    private void breakStrongLink (Pair<RIVWeb, Double> node) {
        strongLinks.remove(node);
        node.left.strongLinks.remove(Pair.make(this, node.right));
    }
    
    private void weakLink (Pair<RIVWeb, Double> node) {
        weakLinks.add(node);
        node.left.weakLinks.add(Pair.make(this, node.right));
    }
    
    private void breakWeakLink (Pair<RIVWeb, Double> node) {
        weakLinks.remove(node);
        node.left.weakLinks.remove(Pair.make(this, node.right));
    }
    
    private void add(RIV riv) {
        double sim = Labels.similarity(riv, this.riv);
        if (strongLinks.isEmpty() && weakLinks.isEmpty()) {
            if (sim >= linkThreshold)
                strongLink(Pair.make(new RIVWeb(riv, this.linkThreshold), sim));
            else
                weakLink(Pair.make(new RIVWeb(riv, this.linkThreshold), sim));
        } else {

        }
    }
    
    public static RIVWeb start(RIV riv, double linkThreshold) { return new RIVWeb(riv, linkThreshold); }
}
