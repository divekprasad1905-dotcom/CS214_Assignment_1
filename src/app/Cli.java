package app;

import algo.array.ArrayListSearch;
import algo.linkedlist.LinkedListSearch;
import bench.BenchmarkCSV;
import metrics.Metrics;
import metrics.SearchResult;
import model.Article;
import util.Checks;
import util.Comparators;
import util.CsvLoader;
import util.Dump;
import util.KeyPicker;
import util.Sorts;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Cli {

    public static void main(String[] args) throws Exception {
        // Examples:
        // Dry run (array):   --csv=$ProjectFileDir$/data/Article.csv --algo=none  --structure=array  --sorted=true --dev=true --dump-sorted=both --k=10 --step=3000
        // Linear (linked):   --csv=$ProjectFileDir$/data/Article.csv --algo=linear --structure=linked --key=title --trials=30 --sorted=false
        Map<String, String> a = parseArgs(args);

        Path csvPath  = Paths.get(a.getOrDefault("csv", "data/Article.csv"));
        String algo   = a.getOrDefault("algo", "none");             // "none" = dry run (no search)
        String struct = a.getOrDefault("structure", "array");       // "array" | "linked"
        String key    = a.getOrDefault("key", "id");                // "id" | "title"
        int trials    = Integer.parseInt(a.getOrDefault("trials", "5"));
        boolean sorted = Boolean.parseBoolean(a.getOrDefault("sorted", "false"));

        // Dev / dump controls (debug-only; not used during real benchmarks unless you set them)
        boolean dev   = Boolean.parseBoolean(a.getOrDefault("dev", "false"));
        String dump   = a.getOrDefault("dump-sorted", "none");      // none | ids | titles | both
        int k         = Integer.parseInt(a.getOrDefault("k", "5")); // head/tail sample size
        int step      = Integer.parseInt(a.getOrDefault("step", "2000"));
        boolean full  = Boolean.parseBoolean(a.getOrDefault("full", "false"));

        // 1) Load data
        System.out.println("Loading CSV: " + csvPath.toAbsolutePath());
        List<Article> raw = CsvLoader.load(csvPath);
        System.out.println("Loaded " + raw.size() + " articles.");

        // 2) Choose structure, prep (optionally) sorted copies, and run dry-run or benchmarks
        if (struct.equalsIgnoreCase("array")) {
            ArrayList<Article> base = new ArrayList<>(raw);
            ArrayList<Article> listById = sorted ? Sorts.sortedArrayCopy(base, Comparators.BY_ID_ASC) : base;
            ArrayList<Article> listByTitle = sorted ? Sorts.sortedArrayCopy(base, Comparators.BY_TITLE_ASC) : base;

            if (algo.equalsIgnoreCase("none")) {
                System.out.println("[DRY RUN] ArrayList ready. Sorted=" + sorted + ", N=" + base.size());
                if (sorted && (dev || true)) {
                    boolean okId = Checks.isSorted(listById, Comparators.BY_ID_ASC);
                    boolean okTitle = Checks.isSorted(listByTitle, Comparators.BY_TITLE_ASC);
                    System.out.println("Sorted checks → BY_ID_ASC=" + okId + ", BY_TITLE_ASC=" + okTitle);

                    // small console sample (safe to increase k, but keep it reasonable)
                    Checks.printHeadTail(listById, a2 -> a2.id, k);
                    Checks.printHeadTail(listByTitle, a2 -> a2.title, k);

                    // optional file dumps (useful for large N; only if asked)
                    if (dump.equalsIgnoreCase("ids") || dump.equalsIgnoreCase("both")) {
                        Dump.writeHeadTailEvery(listById, a2 -> Integer.toString(a2.id), k, step,
                                Paths.get("results/array_sorted_ids_sample.txt"));
                        if (full) {
                            Dump.writeFull(listById, a2 -> Integer.toString(a2.id),
                                    Paths.get("results/array_sorted_ids_full.txt"));
                        }
                    }
                    if (dump.equalsIgnoreCase("titles") || dump.equalsIgnoreCase("both")) {
                        Dump.writeHeadTailEvery(listByTitle, a2 -> a2.title, k, step,
                                Paths.get("results/array_sorted_titles_sample.txt"));
                        if (full) {
                            Dump.writeFull(listByTitle, a2 -> a2.title,
                                    Paths.get("results/array_sorted_titles_full.txt"));
                        }
                    }
                }
                return;
            }

            // Real runs
            ArrayListSearch impl = loadArrayAlgo(algo);
            KeyPicker kp = new KeyPicker(base);
            try (BenchmarkCSV out = new BenchmarkCSV(Paths.get("results/array_results.csv"))) {
                for (int t = 1; t <= trials; t++) {
                    Metrics m = new Metrics();
                    SearchResult r;
                    if (key.equalsIgnoreCase("id")) {
                        int kId = (t % 4 == 0) ? kp.absentId() : kp.presentId();
                        r = impl.byId(listById, kId, m);
                    } else {
                        String kTitle = (t % 4 == 0) ? kp.absentTitle() : kp.presentTitle();
                        r = impl.byTitle(listByTitle, kTitle, m);
                    }
                    out.write(impl.name(), "ArrayList", key, base.size(), t, r);
                }
            }

        } else { // LinkedList
            LinkedList<Article> base = new LinkedList<>(raw);
            LinkedList<Article> listById = sorted ? Sorts.sortedLinkedCopy(base, Comparators.BY_ID_ASC) : base;
            LinkedList<Article> listByTitle = sorted ? Sorts.sortedLinkedCopy(base, Comparators.BY_TITLE_ASC) : base;

            if (algo.equalsIgnoreCase("none")) {
                System.out.println("[DRY RUN] LinkedList ready. Sorted=" + sorted + ", N=" + base.size());
                if (sorted && (dev || true)) {
                    boolean okId = Checks.isSorted(listById, Comparators.BY_ID_ASC);
                    boolean okTitle = Checks.isSorted(listByTitle, Comparators.BY_TITLE_ASC);
                    System.out.println("Sorted checks → BY_ID_ASC=" + okId + ", BY_TITLE_ASC=" + okTitle);

                    Checks.printHeadTail(listById, a2 -> a2.id, k);
                    Checks.printHeadTail(listByTitle, a2 -> a2.title, k);

                    if (dump.equalsIgnoreCase("ids") || dump.equalsIgnoreCase("both")) {
                        Dump.writeHeadTailEvery(listById, a2 -> Integer.toString(a2.id), k, step,
                                Paths.get("results/linked_sorted_ids_sample.txt"));
                        if (full) {
                            Dump.writeFull(listById, a2 -> Integer.toString(a2.id),
                                    Paths.get("results/linked_sorted_ids_full.txt"));
                        }
                    }
                    if (dump.equalsIgnoreCase("titles") || dump.equalsIgnoreCase("both")) {
                        Dump.writeHeadTailEvery(listByTitle, a2 -> a2.title, k, step,
                                Paths.get("results/linked_sorted_titles_sample.txt"));
                        if (full) {
                            Dump.writeFull(listByTitle, a2 -> a2.title,
                                    Paths.get("results/linked_sorted_titles_full.txt"));
                        }
                    }
                }
                return;
            }

            // Real runs
            LinkedListSearch impl = loadLinkedAlgo(algo);
            KeyPicker kp = new KeyPicker(base);
            try (BenchmarkCSV out = new BenchmarkCSV(Paths.get("results/linked_results.csv"))) {
                for (int t = 1; t <= trials; t++) {
                    Metrics m = new Metrics();
                    SearchResult r;
                    if (key.equalsIgnoreCase("id")) {
                        int kId = (t % 4 == 0) ? kp.absentId() : kp.presentId();
                        r = impl.byId(listById, kId, m);
                    } else {
                        String kTitle = (t % 4 == 0) ? kp.absentTitle() : kp.presentTitle();
                        r = impl.byTitle(listByTitle, kTitle, m);
                    }
                    out.write(impl.name(), "LinkedList", key, base.size(), t, r);
                }
            }
        }

        System.out.println("Done.");
    }

    // ---- Reflection-based loaders (so teammates can add files without touching Cli) ----

    private static ArrayListSearch loadArrayAlgo(String shortName) {
        String className = switch (shortName.toLowerCase()) {
            case "linear" -> "algo.array.LinearSearchArrayList";
            case "binary" -> "algo.array.BinarySearchArrayList";
            case "jump" -> "algo.array.JumpSearchArrayList";
            case "fibo", "fibonacci" -> "algo.array.FibonacciSearchArrayList";
            case "interp", "interpolation" -> "algo.array.InterpolationSearchArrayList";
            default -> throw new IllegalArgumentException("Unknown algo: " + shortName);
        };
        try {
            Class<?> c = Class.forName(className);
            return (ArrayListSearch) c.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Algorithm not implemented yet: " + className);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + className + ": " + e, e);
        }
    }

    private static LinkedListSearch loadLinkedAlgo(String shortName) {
        String className = switch (shortName.toLowerCase()) {
            case "linear" -> "algo.linked.LinearSearchLinkedList";
            case "binary" -> "algo.linked.BinarySearchLinkedList";
            case "jump" -> "algo.linked.JumpSearchLinkedList";
            case "fibo", "fibonacci" -> "algo.linked.FibonacciSearchLinkedList";
            case "interp", "interpolation" -> "algo.linked.InterpolationSearchLinkedList";
            default -> throw new IllegalArgumentException("Unknown algo: " + shortName);
        };
        try {
            Class<?> c = Class.forName(className);
            return (LinkedListSearch) c.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Algorithm not implemented yet: " + className);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + className + ": " + e, e);
        }
    }

    // ---- Args parser ----
    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (String s : args) {
            if (s.startsWith("--") && s.contains("=")) {
                int i = s.indexOf('=');
                map.put(s.substring(2, i), s.substring(i + 1));
            }
        }
        return map;
    }
}
