package util;

import model.Article;
import java.util.*;

public final class Sorts {
    private Sorts() {}

    public static ArrayList<Article> sortedArrayCopy(List<Article> src, Comparator<Article> cmp) {
        ArrayList<Article> copy = new ArrayList<>(src);
        copy.sort(cmp);
        return copy;
    }

    public static LinkedList<Article> sortedLinkedCopy(List<Article> src, Comparator<Article> cmp) {
        ArrayList<Article> tmp = new ArrayList<>(src);
        tmp.sort(cmp);
        return new LinkedList<>(tmp);
    }
}
