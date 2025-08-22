package algo.linkedlist;

import metrics.Metrics;
import metrics.SearchResult;
import model.Article;

import java.util.LinkedList;

public interface LinkedListSearch {
    String name();
    SearchResult byId(LinkedList<Article> data, int key, Metrics m);
    SearchResult byTitle(LinkedList<Article> data, String key, Metrics m);
}
