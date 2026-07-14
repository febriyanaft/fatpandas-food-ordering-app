package id.ac.budiluhur.fatpandas;

public class Configuration {
    private static final String URL_HOSTING = "http://103.125.180.35/~wwwgabun/fatpandas/";
    public static final String BASE_URL = URL_HOSTING;

    public static final String URL_GET_ALL_MENU = BASE_URL + "read.php";
    public static final String URL_ADD_ORDER = BASE_URL + "add.php";
    public static final String URL_GET_ORDERS = BASE_URL + "read_orders.php";

    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_ORDER_MENU_NAME = "menu_name";
    public static final String KEY_ORDER_QTY = "qty";
    public static final String KEY_ORDER_TOTAL = "total_price";
    public static final String KEY_ORDER_MEJA = "meja";
    public static final String KEY_ORDER_TYPE = "order_type";
    public static final String KEY_ORDER_PAYMENT = "payment_method";
    public static final String KEY_ORDER_DETAIL = "detail_items";
    public static final String KEY_ORDER_NOTES = "notes";

    public static final String TAG_JSON_ARRAY = "result";
}
