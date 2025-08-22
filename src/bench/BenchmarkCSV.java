package bench;

import metrics.SearchResult;

import java.io.*;
import java.nio.file.*;

public final class BenchmarkCSV implements Closeable {
    private final BufferedWriter bw;

    public BenchmarkCSV(Path out) throws IOException {
        Files.createDirectories(out.getParent());
        bw = Files.newBufferedWriter(out);
        bw.write("algo,structure,keyType,n,trial,found,comparisons,accesses,ms\n");
    }

    public void write(String algo, String structure, String keyType, int n, int trial, SearchResult r)
            throws IOException {
        bw.write(String.format("%s,%s,%s,%d,%d,%b,%d,%d,%d%n",
                algo, structure, keyType, n, trial, r.found(), r.comparisons(), r.accesses(), r.ms()));
    }

    @Override public void close() throws IOException { bw.flush(); bw.close(); }
}
