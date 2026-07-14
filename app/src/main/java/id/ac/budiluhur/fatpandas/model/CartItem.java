package id.ac.budiluhur.fatpandas.model;

import java.util.Locale;

public class CartItem {

    private final String name;
    private final int price;
    private int qty;
    private final int imageRes;
    private boolean isSelected;

    public CartItem(String name, int price, int qty, int imageRes) {
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.imageRes = imageRes;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    public int getImageRes() {
        return imageRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setQty(int qty) {
        if (qty < 1) {
            this.qty = 1;
        } else {
            this.qty = qty;
        }
    }

    public int getTotalPrice() {
        return price * qty;
    }

    public String getFormattedPrice() {
        return "Rp " + String.format(Locale.GERMANY, "%,d", price);
    }

    public String getFormattedTotal() {
        return "Rp " + String.format(Locale.GERMANY, "%,d", getTotalPrice());
    }

    @Override
    public String toString() {
        return name +
                " x" +
                qty +
                " = Rp " +
                String.format(Locale.GERMANY, "%,d", getTotalPrice());
    }
}