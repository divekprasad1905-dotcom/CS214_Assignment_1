package util;

import model.Article;
import java.util.Comparator;

public final class Comparators {
    public static final Comparator<Article> BY_ID_ASC =
            (a, b) -> Integer.compare(a.id, b.id);

    public static final Comparator<Article> BY_TITLE_ASC =
            (a, b) -> a.title.compareToIgnoreCase(b.title);

    private Comparators() {}
}
