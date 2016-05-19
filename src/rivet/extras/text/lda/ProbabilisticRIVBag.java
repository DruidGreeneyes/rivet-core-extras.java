package rivet.extras.text.lda;

import rivet.core.labels.RandomIndexVector;
import rivet.core.labels.ArrayRIV;
import rivet.core.util.Pair;
import rivet.extras.util.ProbabilisticBag;

public class ProbabilisticRIVBag extends ProbabilisticBag<ArrayRIV> {
    
    public final int size;
    
    private ArrayRIV meanVector;
    
    public ProbabilisticRIVBag(int size) {super(); this.size = size;}
    
    @Override 
    public void redistribute() {
        super.redistribute();
        meanVector = bag.entrySet()
                        .stream()
                        .map((e) -> e.getKey().multiply(e.getValue()))
                        .reduce(new ArrayRIV(size), ArrayRIV::add)
                        .divide(count());
    }
    
    @Override
    public void update() { if(!upToDate) redistribute(); }
    
    public ProbabilisticBag<Pair<ArrayRIV, Double>> neighbors(ArrayRIV riv, double targetDistance) {
        ProbabilisticBag<Pair<ArrayRIV, Double>> res = new ProbabilisticBag<>();
        keyStream()
            .map((r) -> Pair.make(r, RandomIndexVector.similarity(r, riv)))
            .filter((p) -> p.right >= targetDistance)
            .forEach((p) -> res.add(p, this.count(p.left)));
        return res;
    }
    
    public ArrayRIV meanVector() { update(); return meanVector; }
}
