package id.ac.budiluhur.fatpandas.model;

import java.util.Locale;

public class OrderItem {

    private final String orderId;
    private final String menuName;
    private final int qty;
    private final int totalPrice;
    private final String time;

    private String status;
    private final String meja;
    private final String orderType;
    private final String paymentMethod;

    private final String notes;
    private final String detailItems;

    public OrderItem(
            String orderId,
            String menuName,
            int qty,
            int totalPrice,
            String time,
            String meja,
            String orderType,
            String paymentMethod,
            String detailItems,
            String notes
    ) {

        this.orderId = orderId;
        this.menuName = menuName;
        this.qty = qty;
        this.totalPrice = totalPrice;
        this.time = time;

        this.meja = meja;
        this.orderType = orderType;
        this.paymentMethod = paymentMethod;

        this.detailItems = detailItems;
        this.notes = notes;

        this.status = "Diproses";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getQty() {
        return qty;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getMeja() {
        return meja;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public String getDetailItems() {
        return detailItems;
    }

    public String getFormattedTotal() {
        return "Rp " + String.format(Locale.GERMANY, "%,d", totalPrice);
    }

    @Override
    public String toString() {
        return orderId +
                " | " +
                menuName +
                " | " +
                getFormattedTotal();
    }
}