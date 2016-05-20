package rivet.extras.text;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import rivet.core.labels.ArrayRIV;
import rivet.extras.util.ProbabilisticBag;

public class ProbabilisticRIVBag extends ProbabilisticBag<NamedRIV> {
    
    private static double NAMING_THRESHOLD = 0.1;
    
    public final int size;
    
    private ArrayRIV meanVector;
    private double meanMag;
    private String name;
    
    public ProbabilisticRIVBag(int size) {super(); this.size = size;}
    public ProbabilisticRIVBag(int size, NamedRIV riv) {
        this(size);
        this.add(riv);
    }
    
    private void recalculateMean() {
        Stream<ArrayRIV> weightedRIVs = bag.entrySet()
                .stream()
                .map((e) -> e.getKey().riv().multiply(e.getValue()));
        meanVector = new ArrayRIV(size).add(weightedRIVs).divide(count());
        meanMag = meanVector.magnitude();
    }
    
    private void rename() {
        int numNames = (int) Math.round(count() * NAMING_THRESHOLD);
        name = bag.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e1.getValue(), e1.getValue()))
                .flatMap((e) -> Stream.of(e.getKey().name()).limit(e.getValue()))
                .limit(numNames)
                .distinct()
                .collect(Collectors.joining("/"));
    }
    
    @Override 
    public void redistribute() {
        recalculateMean();
        rename();
        super.redistribute();
    }
    
    @Override
    public void update() { if(!upToDate) redistribute(); }
    
    public ArrayRIV meanVector() { update(); return meanVector; }
    public double magnitude() { update(); return meanMag; }
    
    public String name() { update(); return name; }
    public String[] names() {
        return bag.keySet().stream().map(NamedRIV::name).toArray(String[]::new);
    }
}
