package rivet.extras.text;

import static java.util.Arrays.stream;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import rivet.core.util.Util;
import rivet.extras.exceptions.ShingleInfection;
import rivet.core.labels.ArrayRIV;
import rivet.core.labels.RandomIndexVector;

public final class Shingles {
    private Shingles(){}
    
    public static int[] findShinglePoints (String text, int offset, int width) throws ShingleInfection {
        if (text == null || text.isEmpty())
            throw new ShingleInfection("THIS TEXT IS NOT TEXT!");
        if (offset == 0)
            throw new ShingleInfection("THIS OFFSET IS A VIOLATION OF THE TOS! PREPARE FOR LEGAL ACTION!");
        return (offset == 1)
                ? Util.range(text.length()).toArray()
                        : Util.range(0, text.length() - width, offset).toArray();
    }
    
    public static String[] shingleText(String text, int width, int offset) {
        String[] res = new String[0];
        for (int i = 0; i < text.length(); i += offset)
            res = ArrayUtils.add(res, text.substring(i, i + width));
        return res;
    }
    
    public static ArrayRIV[] rivShingles (String[] shingles, int size, int k) {
        return stream(shingles)
            .map(ArrayRIV.labelGenerator(size, k))
            .toArray(ArrayRIV[]::new);
    }
    
    public static ArrayRIV[] rivShingles (String text, int[] shinglePoints, int width, int size, int k) {
        return Arrays.stream(shinglePoints)
                    .mapToObj((point) -> 
                        ArrayRIV.generateLabel(
                                size,
                                k,
                                text,
                                point,
                                width))
                    .toArray(ArrayRIV[]::new);
    }
    
    public static ArrayRIV rivAndSumShingles (String text, int[] shinglePoints, int width, int size, int k) {
        return Arrays.stream(shinglePoints)
            .boxed()
            .reduce(new ArrayRIV(size),
                    (riv, point) -> riv.add(
                            ArrayRIV.generateLabel(
                                    size,
                                    k,
                                    text,
                                    point,
                                    width)),
                    (rivA, rivB) -> rivA.add(rivB));
    }
    
    public static RandomIndexVector sumRIVs (RandomIndexVector[] rivs) { return RandomIndexVector.addRIVs(rivs);}
    
    public static ArrayRIV rivettizeText(String text, int width, int offset, int size, int k) throws ShingleInfection {
        int[] points = findShinglePoints(text, offset, width);
        return rivAndSumShingles(text, points, width, size, k);
    }
}