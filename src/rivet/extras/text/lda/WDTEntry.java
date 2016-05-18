package rivet.extras.text.lda;

public class WDTEntry {
    final String word;
    final int document;
    final int topic;
    
    private WDTEntry (String w, int d, int t) { word = w; document = d; topic = t; }
    
    public WDTEntry updateTopic(int newTopic) { return make(word, document, newTopic); }
    
    public boolean equals(WDTEntry other) { 
        return word == other.word && document == other.document && topic == other.topic;
    }
    
    public static WDTEntry make(String word, int document, int topic) { 
        return new WDTEntry(word, document, topic); 
    }
}
