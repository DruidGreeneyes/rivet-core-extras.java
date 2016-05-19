package rivet.extras.text.lda;

import java.util.Collection;
import java.util.HashSet;

import rivet.core.labels.ArrayRIV;

public class RIVSet extends HashSet<ArrayRIV> {
    private final int size;
    private ArrayRIV meanVector;
    private boolean upToDate;
    
    public RIVSet(int size) {super(); this.size = size; upToDate = false;}
    
    private void updateMeanVector() {
        meanVector = this.stream()
                .reduce(new ArrayRIV(size), ArrayRIV::add)
                .divide(size());
    }
    
    public ArrayRIV meanVector() {
        if (!upToDate)
            updateMeanVector();
        return meanVector;
    }
    
    @Override
    public boolean add(ArrayRIV riv) {
        boolean added = super.add(riv);
        if (added)
            upToDate = false;
        return added;
    }
    
    @Override
    public boolean addAll(Collection<? extends ArrayRIV> rivs) {
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
