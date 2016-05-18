package rivet.extras.text.lda;

import rivet.core.arraylabels.Labels;
import rivet.core.arraylabels.RIV;
import rivet.core.util.Pair;
import rivet.core.util.ProbabilisticBag;

public class ProbabilisticRIVBag extends ProbabilisticBag<RIV> {
    
    public final int size;
    
    private RIV meanVector;
    
    public ProbabilisticRIVBag(int size) {super(); this.size = size;}
    
    @Override 
    public void redistribute() {
        super.redistribute();
        meanVector = bag.entrySet()
                        .stream()
                        .map((e) -> e.getKey().multiply(e.getValue()))
                        .reduce(new RIV(size), Labels::addLabels)
                        .divideBy(count());
    }
    
    @Override
    public void update() { if(!upToDate) redistribute(); }
    
    public ProbabilisticBag<Pair<RIV, Double>> neighbors(RIV riv, double targetDistance) {
        ProbabilisticBag<Pair<RIV, Double>> res = new ProbabilisticBag<>();
        keyStream()
            .map((r) -> Pair.make(r, Labels.similarity(r, riv)))
            .filter((p) -> p.right >= targetDistance)
            .forEach((p) -> res.add(p, this.count(p.left)));
        return res;
    }
    
    public RIV meanVector() { update(); return meanVector; }
}
