package rivet.extras.text.lda;

import rivet.core.labels.ArrayRIV;

public class RTEntry {
    final ArrayRIV riv;
    final int topic;
    
    private RTEntry(ArrayRIV r, int t) {riv = r; topic = t;}
    
    public static RTEntry make(ArrayRIV riv, int topic) {return new RTEntry(riv, topic);}

}
