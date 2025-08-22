package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public final class Dump {
    private Dump() {}

    /** Write head k, every step-th, and tail k items to a UTF-8 text file. */
    public static <T> void writeHeadTailEvery(
            List<T> list, Function<T, String> proj, int k, int step, Path out) throws IOException {
        Files.createDirectories(out.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            int n = list.size();
            k = Math.min(k, n);

            bw.write("# n=" + n + " k=" + k + " step=" + step + "\n");
            bw.write("## HEAD\n");
            for (int i = 0; i < k; i++) {
                bw.write(proj.apply(list.get(i)));
                bw.newLine();
            }

            bw.write("## EVERY_" + step + "\n");
            if (step > 0) {
                for (int i = step; i < Math.max(0, n - k); i += step) {
                    bw.write(proj.apply(list.get(i)));
                    bw.newLine();
                }
            }

            bw.write("## TAIL\n");
            for (int i = Math.max(0, n - k); i < n; i++) {
                bw.write(proj.apply(list.get(i)));
                bw.newLine();
            }
        }
    }

    /** Write the entire projected list to a file (use sparingly on large lists). */
    public static <T> void writeFull(List<T> list, Function<T, String> proj, Path out) throws IOException {
        Files.createDirectories(out.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            for (T t : list) {
                bw.write(proj.apply(t));
                bw.newLine();
            }
        }
    }
}
