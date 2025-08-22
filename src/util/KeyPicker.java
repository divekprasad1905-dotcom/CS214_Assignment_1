package util;

import model.Article;
import java.util.*;

public final class KeyPicker {
    private final Random rng = new Random(42);
    private final int[] ids;
    private final String[] titles;
    private final int maxId;

    public KeyPicker(List<Article> data) {
        this.ids = data.stream().mapToInt(a -> a.id).toArray();
        this.titles = data.stream().map(a -> a.title).toArray(String[]::new);
        this.maxId = Arrays.stream(ids).max().orElse(0);
    }

    public int presentId()         { return ids[rng.nextInt(ids.length)]; }
    public String presentTitle()   { return titles[rng.nextInt(titles.length)]; }

    public int absentId()          { return maxId + 1 + rng.nextInt(1_000_000); }
    public String absentTitle()    { return "___MISSING___" + UUID.randomUUID(); }
}
