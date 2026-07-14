package id.ac.budiluhur.fatpandas;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import id.ac.budiluhur.fatpandas.model.CartItem;
import id.ac.budiluhur.fatpandas.model.MenuPandas;
import id.ac.budiluhur.fatpandas.model.OrderItem;

public class CartManager {

    private static CartManager instance;
    private DatabaseHelper dbHelper;

    private final List<CartItem> cartItems = new ArrayList<>();
    private final List<OrderItem> orderHistory = new ArrayList<>();
    private final List<MenuPandas> favoriteItems = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public static CartManager getInstance(Context context) {
        CartManager manager = getInstance();
        manager.init(context);
        return manager;
    }

    public void init(Context context) {
        if (context == null) return;

        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context.getApplicationContext());
        }

        reloadFromDatabase();
    }

    public void reloadFromDatabase() {
        if (dbHelper == null) return;

        cartItems.clear();
        cartItems.addAll(dbHelper.getCartItems());

        favoriteItems.clear();
        favoriteItems.addAll(dbHelper.getFavorites());

        orderHistory.clear();
        orderHistory.addAll(dbHelper.getOrders());
    }

    public void addItem(CartItem newItem) {
        if (newItem == null) return;

        for (CartItem item : cartItems) {
            if (item.getName().equals(newItem.getName())) {
                item.setQty(item.getQty() + newItem.getQty());

                if (dbHelper != null) {
                    dbHelper.updateCartItem(item);
                }
                return;
            }
        }

        cartItems.add(newItem);

        if (dbHelper != null) {
            dbHelper.addOrUpdateCart(newItem);
        }
    }

    public void updateItem(CartItem item) {
        if (item == null) return;

        if (dbHelper != null) {
            dbHelper.updateCartItem(item);
        }
    }

    public void removeItem(int index) {
        if (index < 0 || index >= cartItems.size()) return;

        String name = cartItems.get(index).getName();
        cartItems.remove(index);

        if (dbHelper != null) {
            dbHelper.deleteCartByName(name);
        }
    }

    public void removeItemByName(String name) {
        if (name == null) return;

        cartItems.removeIf(item -> item.getName().equals(name));

        if (dbHelper != null) {
            dbHelper.deleteCartByName(name);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int getCartCount() {
        int total = 0;

        for (CartItem item : cartItems) {
            total += item.getQty();
        }

        return total;
    }

    public int getCartTotal() {
        int total = 0;

        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        return total;
    }

    public int getSelectedTotal() {
        int total = 0;
        boolean anySelected = false;

        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getTotalPrice();
                anySelected = true;
            }
        }

        return anySelected ? total : getCartTotal();
    }

    public int getSelectedCount() {
        int count = 0;
        boolean anySelected = false;

        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                count += item.getQty();
                anySelected = true;
            }
        }

        return anySelected ? count : getCartCount();
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> selected = new ArrayList<>();

        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }

        if (selected.isEmpty()) {
            return new ArrayList<>(cartItems);
        }

        return selected;
    }

    public void removeCheckedOutItems(List<CartItem> items) {
        if (items == null || items.isEmpty()) return;

        for (CartItem checkedItem : items) {
            if (checkedItem == null) continue;

            String name = checkedItem.getName();

            cartItems.removeIf(item -> item.getName().equals(name));

            if (dbHelper != null) {
                dbHelper.deleteCartByName(name);
            }
        }
    }

    public void toggleFavorite(MenuPandas menu) {
        if (menu == null) return;

        if (isFavorite(menu)) {
            favoriteItems.removeIf(item -> item.getName().equals(menu.getName()));

            if (dbHelper != null) {
                dbHelper.deleteFavorite(menu.getName());
            }
        } else {
            favoriteItems.add(menu);

            if (dbHelper != null) {
                dbHelper.saveFavorite(menu);
            }
        }
    }

    public boolean isFavorite(MenuPandas menu) {
        if (menu == null) return false;

        for (MenuPandas item : favoriteItems) {
            if (item.getName().equals(menu.getName())) {
                return true;
            }
        }

        return false;
    }

    public List<MenuPandas> getFavoriteItems() {
        return favoriteItems;
    }

    public void addOrder(OrderItem order) {
        if (order == null) return;

        orderHistory.add(0, order);

        if (dbHelper != null) {
            dbHelper.insertOrder(order);
        }
    }

    public List<OrderItem> getOrderHistory() {
        return orderHistory;
    }

    public int getTotalOrderCount() {
        return orderHistory.size();
    }

    public int getTotalOrderPrice() {
        int total = 0;

        for (OrderItem order : orderHistory) {
            total += order.getTotalPrice();
        }

        return total;
    }

    public int getFavoriteCount() {
        return favoriteItems.size();
    }

    public void clearCart() {
        List<CartItem> copy = new ArrayList<>(cartItems);

        for (CartItem item : copy) {
            removeItemByName(item.getName());
        }
    }
}