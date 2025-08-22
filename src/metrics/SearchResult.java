package metrics;

public record SearchResult(
        boolean found, int index, long comparisons, long accesses, long ms) {

    public static SearchResult of(boolean found, int index, Metrics m) {
        return new SearchResult(found, index, m.comparisons, m.elementAccesses, m.ms());
    }
}
