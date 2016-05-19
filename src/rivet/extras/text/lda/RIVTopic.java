package rivet.extras.text.lda;

import rivet.core.labels.ArrayRIV;

public class RIVTopic {
    private final ProbabilisticRIVBag rivs;
    public final String name;
    
    private RIVTopic (ProbabilisticRIVBag rivs, String name) {this.rivs = rivs; this.name = name;}
    
    public ArrayRIV meanVector() { return rivs.meanVector(); }
    
    public static RIVTopic make (ProbabilisticRIVBag rivs, String name) { return new RIVTopic(rivs, name); }
}
