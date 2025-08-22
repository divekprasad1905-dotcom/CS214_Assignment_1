package util;

import model.Article;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class CsvLoader {
    private CsvLoader() {}

    public static List<Article> load(Path csvPath) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String header = br.readLine();
            if (header == null) throw new IOException("Empty CSV: " + csvPath);

            String[] cols = parseCsvLine(header);
            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < cols.length; i++) idx.put(cols[i].trim(), i);

            require(idx, "ID","TITLE","ABSTRACT",
                    "Computer Science","Physics","Mathematics","Statistics",
                    "Quantitative Biology","Quantitative Finance");

            List<Article> out = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] f = parseCsvLine(line);

                int id = toInt(f, idx.get("ID"));
                String title = safe(f, idx.get("TITLE"));
                String abstr = safe(f, idx.get("ABSTRACT"));
                int cs = toInt(f, idx.get("Computer Science"));
                int ph = toInt(f, idx.get("Physics"));
                int ma = toInt(f, idx.get("Mathematics"));
                int st = toInt(f, idx.get("Statistics"));
                int qb = toInt(f, idx.get("Quantitative Biology"));
                int qf = toInt(f, idx.get("Quantitative Finance"));

                out.add(new Article(id, title, abstr, cs, ph, ma, st, qb, qf));
            }
            return out;
        }
    }

    private static void require(Map<String,Integer> m, String... keys) {
        for (String k : keys)
            if (!m.containsKey(k)) throw new IllegalArgumentException("Missing column: " + k);
    }
    private static String safe(String[] f, int i) { return i < f.length ? (f[i] == null ? "" : f[i]) : ""; }
    private static int toInt(String[] f, int i) {
        try { return Integer.parseInt(safe(f, i).trim()); } catch (Exception e) { return 0; }
    }

    /** Minimal CSV parser: handles quoted fields, commas inside quotes, and escaped quotes (""). */
    public static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"'); i++; // escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }
}
