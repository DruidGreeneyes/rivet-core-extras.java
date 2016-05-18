package rivet.extras.text.lda;

import rivet.core.util.Counter;
import rivet.core.util.ProbabilisticBag;

public class ProbabilisticWordBag extends ProbabilisticBag<String> {
        
    public String name(int threshold) {
        Counter c = new Counter();
        return bag.descendingKeySet()
                .stream()
                .sequential()
                .reduce("", (i, s) -> (c.get() >= threshold)
                                            ? i
                                            : (i.length() == 0)
                                                    ? s
                                                    : i + "/" + s);
    }
    
    public String name() { return name(count() / 10); }
    
}
