package algo.array;

import metrics.Metrics;
import metrics.SearchResult;
import model.Article;

import java.util.ArrayList;

public interface ArrayListSearch {
    String name();
    SearchResult byId(ArrayList<Article> data, int key, Metrics m);
    SearchResult byTitle(ArrayList<Article> data, String key, Metrics m);
}
