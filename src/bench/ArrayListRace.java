package bench;

import common.SearchAlgorithm;
import model.Article; // from your leader
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Supplier;
import algo.array.Registry;

public class ArrayListRace {

    public static final int TRIALS = 30;

    // Helper class to store results
    public static class Stats {
        long best = Long.MAX_VALUE, worst = Long.MIN_VALUE, total = 0;
        void add(long c){ best = Math.min(best,c); worst = Math.max(worst,c); total += c; }
        double mean(){ return total / (double) TRIALS; }
    }

    // Main race method
    public static void run(ArrayList<Article> sortedByTitle,
                           Comparator<Article> byTitle,
                           Supplier<Article> presentPicker,       // returns a key that exists
                           Supplier<Article> guaranteedMissPicker // returns a key that doesn’t exist
    ) throws IOException {

        // Get your 4 algorithms
        List<SearchAlgorithm<Article>> algos = new ArrayList<>();
        for (SearchAlgorithm<?> raw : Registry.registry()) {
            @SuppressWarnings("unchecked")
            SearchAlgorithm<Article> sa = (SearchAlgorithm<Article>) raw;
            algos.add(sa);
        }

        Map<String, Stats> statsMap = new LinkedHashMap<>();

        // Run 30 trials for each algorithm
        for (SearchAlgorithm<Article> algo : algos) {
            Stats s = new Stats();
            for (int i = 0; i < TRIALS; i++) {
                Article key = (i % 2 == 0) ? presentPicker.get() : guaranteedMissPicker.get();
                algo.search(sortedByTitle, key, byTitle);
                s.add(algo.lastgetComparisons());

                System.out.println(algo.name() + " Trial " + (i+1) + " done");
            }
            statsMap.put(algo.name(), s);
        }

        // Export CSV file
        Path outDir = Paths.get("out");
        Files.createDirectories(outDir);
        Path csv = outDir.resolve("arraylist_race_stats.csv");
        try (BufferedWriter w = Files.newBufferedWriter(csv)) {
            w.write("Algorithm,Best,Mean,Worst\n");
            for (var e : statsMap.entrySet()) {
                w.write(String.format("%s,%d,%.2f,%d%n",
                        e.getKey(), e.getValue().best, e.getValue().mean(), e.getValue().worst));
            }
        }
        System.out.println("Wrote: " + csv.toAbsolutePath());

        // Export Bar Chart (PNG)
        var algoNames = new ArrayList<String>();
        var means = new ArrayList<Double>();
        for (var e : statsMap.entrySet()) {
            algoNames.add(e.getKey());
            means.add(e.getValue().mean());
        }

        org.knowm.xchart.CategoryChart chart =
                new org.knowm.xchart.CategoryChartBuilder()
                        .width(800).height(600)
                        .title("ArrayList Search – Mean Comparisons (30 trials)")
                        .xAxisTitle("Algorithm").yAxisTitle("Comparisons").build();

        chart.addSeries("Mean", algoNames, means);

        org.knowm.xchart.BitmapEncoder.saveBitmap(chart,
                outDir.resolve("arraylist_means").toString(),
                org.knowm.xchart.BitmapEncoder.BitmapFormat.PNG);

        System.out.println("Wrote: " + outDir.resolve("arraylist_means.png").toAbsolutePath());
    }

    // 👇 Added so IntelliJ can run this file directly
    public static void main(String[] args) throws IOException {
        // Create dummy Article objects for testing
        ArrayList<Article> articles = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            articles.add(new Article(
                    i,                      // id
                    "Title" + i,            // title
                    "Author" + i,           // author
                    2025,                   // year
                    1,                      // month
                    1,                      // day
                    0,                      // citations
                    0,                      // views
                    0                       // downloads
            ));
        }

        // Comparator: compare Articles by title
        Comparator<Article> byTitle = Comparator.comparing(Article::getTitle);

        // Picker: present (always return an existing article)
        Supplier<Article> presentPicker = () -> {
            int index = new Random().nextInt(articles.size());
            return articles.get(index);
        };

        // Picker: guaranteed miss (title never exists)
        Supplier<Article> missPicker = () -> new Article(
                -1, "ZZZZ_Not_Found", "Nobody",
                0, 0, 0, 0, 0, 0
        );

        // Run the experiment
        run(articles, byTitle, presentPicker, missPicker);
    }
}
