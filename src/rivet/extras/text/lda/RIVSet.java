package rivet.extras.text.lda;

import java.util.Collection;
import java.util.HashSet;

import rivet.core.arraylabels.Labels;
import rivet.core.arraylabels.RIV;

public class RIVSet extends HashSet<RIV> {
    private final int size;
    private RIV meanVector;
    private boolean upToDate;
    
    public RIVSet(int size) {super(); this.size = size; upToDate = false;}
    
    private void updateMeanVector() {
        meanVector = this.stream()
                .reduce(new RIV(size), Labels::addLabels)
                .divideBy(size());
    }
    
    public RIV meanVector() {
        if (!upToDate)
            updateMeanVector();
        return meanVector;
    }
    
    @Override
    public boolean add(RIV riv) {
        boolean added = super.add(riv);
        if (added)
            upToDate = false;
        return added;
    }
    
    @Override
    public boolean addAll(Collection<? extends RIV> rivs) {
        boolean added = super.addAll(rivs);
        if (added)
            upToDate = false;
        return added;
    }
    
    @Override
    public boolean remove(Object riv) {
        boolean removed = super.remove(riv);
        if (removed)
            upToDate = false;
        return removed;
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = -1925033388040442727L;
}
