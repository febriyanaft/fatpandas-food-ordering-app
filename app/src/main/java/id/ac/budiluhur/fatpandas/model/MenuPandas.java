package id.ac.budiluhur.fatpandas.model;

import java.util.Locale;

public class MenuPandas {

    private final String name;
    private final String description;
    private final String allergen;
    private final int price;
    private final String category;
    private final int imageRes;
    private final double rating;
    private final int sold;

    public MenuPandas(
            String name,
            String description,
            String allergen,
            int price,
            String category,
            int imageRes,
            double rating,
            int sold
    ) {
        this.name = name;
        this.description = description;
        this.allergen = allergen;
        this.price = price;
        this.category = category;
        this.imageRes = imageRes;
        this.rating = rating;
        this.sold = sold;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAllergen() {
        return allergen;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getImageRes() {
        return imageRes;
    }

    public double getRating() {
        return rating;
    }

    public String getFormattedRating() {
        return String.format(Locale.US, "%.1f", rating);
    }

    public int getSold() {
        return sold;
    }

    public String getFormattedPrice() {
        return "Rp " + String.format(Locale.GERMANY, "%,d", price);
    }

    @Override
    public String toString() {
        return name + " - " + getFormattedPrice();
    }
}
