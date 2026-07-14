package id.ac.budiluhur.fatpandas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import id.ac.budiluhur.fatpandas.model.CartItem;
import id.ac.budiluhur.fatpandas.model.MenuPandas;
import id.ac.budiluhur.fatpandas.model.OrderItem;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fatpandas_2412500221.db";
    private static final int DB_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createCartTable(db);
        createFavoritesTable(db);
        createOrdersTable(db);
    }

    private void createCartTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "price INTEGER," +
                "qty INTEGER," +
                "image_res INTEGER," +
                "selected INTEGER DEFAULT 0)");
    }

    private void createFavoritesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "description TEXT," +
                "allergen TEXT," +
                "price INTEGER," +
                "category TEXT," +
                "image_res INTEGER," +
                "rating REAL DEFAULT 0.0," +
                "sold INTEGER DEFAULT 0)");
    }

    private void createOrdersTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id TEXT UNIQUE," +
                "menu_name TEXT," +
                "qty INTEGER," +
                "total_price INTEGER," +
                "time TEXT," +
                "meja TEXT," +
                "order_type TEXT," +
                "payment_method TEXT," +
                "detail_items TEXT," +
                "notes TEXT," +
                "status TEXT DEFAULT 'Diproses')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    public void addOrUpdateCart(CartItem item) {
        if (item == null) return;

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT qty FROM cart WHERE name=?",
                new String[]{item.getName()}
        );

        if (cursor.moveToFirst()) {
            int oldQty = cursor.getInt(0);
            int newQty = oldQty + item.getQty();

            ContentValues values = new ContentValues();
            values.put("qty", newQty);
            values.put("price", item.getPrice());
            values.put("image_res", item.getImageRes());
            values.put("selected", item.isSelected() ? 1 : 0);

            db.update(
                    "cart",
                    values,
                    "name=?",
                    new String[]{item.getName()}
            );
        } else {
            ContentValues values = new ContentValues();
            values.put("name", item.getName());
            values.put("price", item.getPrice());
            values.put("qty", item.getQty());
            values.put("image_res", item.getImageRes());
            values.put("selected", item.isSelected() ? 1 : 0);

            db.insert("cart", null, values);
        }

        cursor.close();
    }

    public void updateCartItem(CartItem item) {
        if (item == null) return;

        ContentValues values = new ContentValues();
        values.put("price", item.getPrice());
        values.put("qty", item.getQty());
        values.put("image_res", item.getImageRes());
        values.put("selected", item.isSelected() ? 1 : 0);

        getWritableDatabase().update(
                "cart",
                values,
                "name=?",
                new String[]{item.getName()}
        );
    }

    public void deleteCartByName(String name) {
        if (name == null) return;

        getWritableDatabase().delete(
                "cart",
                "name=?",
                new String[]{name}
        );
    }

    public void clearCart() {
        getWritableDatabase().delete("cart", null, null);
    }

    public List<CartItem> getCartItems() {
        List<CartItem> list = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name, price, qty, image_res, selected FROM cart ORDER BY id ASC",
                null
        );

        while (cursor.moveToNext()) {
            CartItem item = new CartItem(
                    cursor.getString(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3)
            );

            item.setSelected(cursor.getInt(4) == 1);
            list.add(item);
        }

        cursor.close();
        return list;
    }

    public void saveFavorite(MenuPandas menu) {
        if (menu == null) return;

        ContentValues values = new ContentValues();
        values.put("name", menu.getName());
        values.put("description", menu.getDescription());
        values.put("allergen", menu.getAllergen());
        values.put("price", menu.getPrice());
        values.put("category", menu.getCategory());
        values.put("image_res", menu.getImageRes());
        values.put("rating", menu.getRating());
        values.put("sold", menu.getSold());

        getWritableDatabase().insertWithOnConflict(
                "favorites",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public void deleteFavorite(String name) {
        if (name == null) return;

        getWritableDatabase().delete(
                "favorites",
                "name=?",
                new String[]{name}
        );
    }

    public List<MenuPandas> getFavorites() {
        List<MenuPandas> list = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name, description, allergen, price, category, image_res, rating, sold FROM favorites ORDER BY name ASC",
                null
        );

        while (cursor.moveToNext()) {
            MenuPandas menu = new MenuPandas(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getDouble(6),
                    cursor.getInt(7)
            );

            list.add(menu);
        }

        cursor.close();
        return list;
    }

    public void insertOrder(OrderItem order) {
        if (order == null) return;

        ContentValues values = new ContentValues();
        values.put("order_id", order.getOrderId());
        values.put("menu_name", order.getMenuName());
        values.put("qty", order.getQty());
        values.put("total_price", order.getTotalPrice());
        values.put("time", order.getTime());
        values.put("meja", order.getMeja());
        values.put("order_type", order.getOrderType());
        values.put("payment_method", order.getPaymentMethod());
        values.put("detail_items", order.getDetailItems());
        values.put("notes", order.getNotes());

        getWritableDatabase().insertWithOnConflict(
                "orders",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<OrderItem> getOrders() {
        List<OrderItem> list = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT order_id, menu_name, qty, total_price, time, meja, order_type, payment_method, detail_items, notes, status FROM orders ORDER BY id DESC",
                null
        );

        while (cursor.moveToNext()) {
            OrderItem order = new OrderItem(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9)
            );
            order.setStatus(cursor.getString(10));
            list.add(order);
        }

        cursor.close();
        return list;
    }

    public void updateOrderStatus(String orderId, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        getWritableDatabase().update("orders", values, "order_id=?", new String[]{orderId});
    }

    public int getTotalOrderCount() {
        int total = 0;

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM orders",
                null
        );

        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        return total;
    }

    public int getTotalOrderPrice() {
        int total = 0;

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT SUM(total_price) FROM orders",
                null
        );

        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        return total;
    }

    public int getFavoriteCount() {
        int total = 0;

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM favorites",
                null
        );

        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        return total;
    }
}
