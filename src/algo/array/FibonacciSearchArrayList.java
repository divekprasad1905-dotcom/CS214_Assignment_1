package algo.array;

import common.SearchAlgorithm;
import java.util.List;
import java.util.Comparator;

public class FibonacciSearchArrayList<T> implements SearchAlgorithm<T> {
    private long comparisons;

    @Override
    public int search(List<T> data, T key, Comparator<T> cmp) {
        comparisons = 0;
        int n = data.size();

        // Initialize Fibonacci numbers
        int fibMMm2 = 0;   // (m-2)'th Fibonacci
        int fibMMm1 = 1;   // (m-1)'th Fibonacci
        int fibM = fibMMm2 + fibMMm1;  // m'th Fibonacci

        // Find the smallest Fibonacci number greater than or equal to n
        while (fibM < n) {
            fibMMm2 = fibMMm1;
            fibMMm1 = fibM;
            fibM = fibMMm2 + fibMMm1;
        }

        // Marks the eliminated range from front
        int offset = -1;

        // While there are elements to be inspected
        while (fibM > 1) {
            int i = Math.min(offset + fibMMm2, n - 1);
            comparisons++;
            int result = cmp.compare(data.get(i), key);

            if (result < 0) {
                fibM = fibMMm1;
                fibMMm1 = fibMMm2;
                fibMMm2 = fibM - fibMMm1;
                offset = i;
            } else if (result > 0) {
                fibM = fibMMm2;
                fibMMm1 = fibMMm1 - fibMMm2;
                fibMMm2 = fibM - fibMMm1;
            } else {
                return i; // Found
            }
        }

        // Compare the last element
        if (fibMMm1 == 1 && offset + 1 < n) {
            comparisons++;
            if (cmp.compare(data.get(offset + 1), key) == 0) {
                return offset + 1;
            }
        }

        return -1; // Not found
    }

    @Override
    public long lastgetComparisons() {
        return comparisons;
    }

    @Override
    public String name() {
        return "Fibonacci Search (ArrayList)";
    }
}
