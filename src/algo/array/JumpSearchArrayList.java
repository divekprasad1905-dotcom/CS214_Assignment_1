package algo.array;

import common.SearchAlgorithm;
import java.util.List;
import java.util.Comparator;

public class JumpSearchArrayList<T> implements SearchAlgorithm<T> {
    private long comparisons;

    @Override
    public int search(List<T> data, T key, Comparator<T> cmp) {
        comparisons = 0;
        int n = data.size();
        int step = (int) Math.floor(Math.sqrt(n));
        int prev = 0;

        // Jump in steps
        while (prev < n && cmp.compare(data.get(Math.min(step, n) - 1), key) < 0) {
            comparisons++;
            prev = step;
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) return -1;
        }

        // Linear search in the block
        while (prev < Math.min(step, n)) {
            comparisons++;
            if (cmp.compare(data.get(prev), key) == 0) return prev;
            prev++;
        }

        return -1;
    }

    @Override
    public long lastgetComparisons() {
        return comparisons;
    }

    @Override
    public String name() {
        return "Jump Search (ArrayList)";
    }
}
