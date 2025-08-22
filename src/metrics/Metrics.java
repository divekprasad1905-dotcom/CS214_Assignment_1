package metrics;

public class Metrics {
    public long comparisons = 0;
    public long elementAccesses = 0;
    private long startNanos = 0, endNanos = 0;

    public void start() { startNanos = System.nanoTime(); }
    public void end()   { endNanos = System.nanoTime(); }
    public long ms()    { return (endNanos - startNanos) / 1_000_000; }
}
