package algo.array;

import common.SearchAlgorithm;
import java.util.List;
import java.util.Comparator;

public class LinearSearchArrayList<T> implements SearchAlgorithm<T> {
    private long comparisons;

    @Override
    public int search(List<T> data, T key, Comparator<T> cmp) {
        comparisons = 0;
        for (int i = 0; i < data.size(); i++) {
            comparisons++;
            if (cmp.compare(data.get(i), key) == 0) {
                return i; // found
            }
        }
        return -1; // not found
    }

    @Override
    public long lastgetComparisons() {
        return comparisons;
    }

    @Override
    public String name() {
        return "Linear Search (ArrayList)";
    }
}
