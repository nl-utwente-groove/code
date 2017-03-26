package groove.util.collect;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Compares two objects based on their appearance in a given list. All other
 * objects are compared according to their natural ordering, coming after the
 * list elements.
 */
public class ListComparator<T extends Comparable<T>> implements Comparator<T> {
    /**
     * Constructs a comparator based on a given list of keys. These keys are
     * ordered first, in the order given; other keys are ordered alphabetically.
     */
    public ListComparator(Iterable<T> defaultKeys) {
        int index = 0;
        for (T key : defaultKeys) {
            this.knownKeyIndexMap.put(key, index);
            index++;
        }
    }

    /**
     * First compares the objects as to their position in the known objects,
     * then in their natural order.
     */
    @Override
    public int compare(T o1, T o2) {
        Integer index1Value = this.knownKeyIndexMap.get(o1);
        int index1 = index1Value == null ? Integer.MAX_VALUE : index1Value;
        Integer index2Value = this.knownKeyIndexMap.get(o2);
        int index2 = index2Value == null ? Integer.MAX_VALUE : index2Value;
        int result = index1 - index2;
        if (result == 0) {
            result = o1.compareTo(o2);
        }
        return result;
    }

    /** Map of keys to display priority. */
    private final Map<T,Integer> knownKeyIndexMap = new HashMap<>();
}