package util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public final class Checks {
    private Checks() {}

    /** Returns true if the list is nondecreasing under the comparator. */
    public static <T> boolean isSorted(List<T> list, Comparator<T> cmp) {
        for (int i = 1; i < list.size(); i++) {
            if (cmp.compare(list.get(i - 1), list.get(i)) > 0) return false;
        }
        return true;
    }

    /** Print first/last k projected values for a quick eyeball. */
    public static <T, U> void printHeadTail(List<T> list, Function<T, U> proj, int k) {
        int n = list.size();
        k = Math.min(k, n);
        System.out.println("Head " + k + ":");
        for (int i = 0; i < k; i++) {
            System.out.println("  " + proj.apply(list.get(i)));
        }
        System.out.println("Tail " + k + ":");
        for (int i = Math.max(0, n - k); i < n; i++) {
            System.out.println("  " + proj.apply(list.get(i)));
        }
    }
}
