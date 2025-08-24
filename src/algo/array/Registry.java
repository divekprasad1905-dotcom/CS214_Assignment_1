package algo.array;

import common.SearchAlgorithm;
import java.util.*;

public class Registry {
    private static final List<SearchAlgorithm<?>> registry = new ArrayList<>();

    static {
        registry.add(new LinearSearchArrayList<Integer>());
        registry.add(new BinarySearchArrayList<Integer>());
        registry.add(new JumpSearchArrayList<Integer>());
        registry.add(new FibonacciSearchArrayList<Integer>());
    }

    public static List<SearchAlgorithm<?>> registry() {
        return registry;
    }
}
