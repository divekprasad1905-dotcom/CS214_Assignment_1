package common;

import java.util.List;
import java.util.Comparator;

public interface SearchAlgorithm<T> {
    int search(List<T> data, T key, Comparator<T> cmp);
    long lastgetComparisons();
    String name();
}
