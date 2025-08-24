package model;

public class Article {
    public final int id;
    public final String title;
    public final String abstractText;

    // Domain flags from CSV (0/1)
    public final int cs, physics, math, stats, qbio, qfin;

    public Article(int id, String title, String abstractText,
                   int cs, int physics, int math, int stats, int qbio, int qfin) {
        this.id = id;
        this.title = title == null ? "" : title.trim();
        this.abstractText = abstractText == null ? "" : abstractText.trim();
        this.cs = cs; this.physics = physics; this.math = math;
        this.stats = stats; this.qbio = qbio; this.qfin = qfin;
    }

    public String getTitle() {
        return title;
    }

}
