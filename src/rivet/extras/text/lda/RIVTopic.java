package rivet.extras.text.lda;

import rivet.core.arraylabels.RIV;

public class RIVTopic {
    private final ProbabilisticRIVBag rivs;
    public final String name;
    
    private RIVTopic (ProbabilisticRIVBag rivs, String name) {this.rivs = rivs; this.name = name;}
    
    public RIV meanVector() { return rivs.meanVector(); }
    
    public static RIVTopic make (ProbabilisticRIVBag rivs, String name) { return new RIVTopic(rivs, name); }
}