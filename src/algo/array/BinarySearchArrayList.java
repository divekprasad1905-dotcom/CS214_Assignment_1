package algo.array;

import common.SearchAlgorithm;
import java.util.List;
import java.util.Comparator;

public class BinarySearchArrayList<T> implements SearchAlgorithm<T> {
    private long comparisons;

    @Override
    public int search(List<T> data, T key, Comparator<T> cmp) {
        comparisons = 0;
        int left = 0, right = data.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            comparisons++;
            int result = cmp.compare(data.get(mid), key);

            if (result == 0) return mid;
            if (result < 0) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    @Override
    public long lastgetComparisons() {
        return comparisons;
    }

    @Override
    public String name() {
        return "Binary Search (ArrayList)";
    }
}
