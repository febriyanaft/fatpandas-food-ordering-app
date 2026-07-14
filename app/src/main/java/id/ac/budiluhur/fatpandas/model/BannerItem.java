package id.ac.budiluhur.fatpandas.model;

public class BannerItem {
    private final String title;
    private final String subtitle;
    private final int imageRes;

    public BannerItem(String title, String subtitle, int imageRes) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageRes = imageRes;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getImageRes() { return imageRes; }
}
