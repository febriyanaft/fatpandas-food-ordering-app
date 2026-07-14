package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import id.ac.budiluhur.fatpandas.model.BannerItem;
import id.ac.budiluhur.fatpandas.model.CartItem;
import id.ac.budiluhur.fatpandas.model.MenuPandas;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private MenuAdapter adapter;
    private CartManager cartManager;
    private final List<MenuPandas> allMenus = new ArrayList<>();

    private TextView btnAll;
    private TextView btnDrink;
    private TextView btnFood;
    private TextView btnDessert;

    private EditText etSearch;
    private String currentCategory = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        setupLogo();

        cartManager = CartManager.getInstance(this);

        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        RecyclerView rvPopularMenu = findViewById(R.id.rvPopularMenu);

        btnAll = findViewById(R.id.btnAll);
        btnDrink = findViewById(R.id.btnDrink);
        btnFood = findViewById(R.id.btnFood);
        btnDessert = findViewById(R.id.btnDessert);
        etSearch = findViewById(R.id.etSearch);

        setupMenuData();
        setupBanner();
        setupPopularMenu(rvPopularMenu);

        adapter = new MenuAdapter(this, new ArrayList<>(allMenus));

        if (rvMenu != null) {
            rvMenu.setLayoutManager(new LinearLayoutManager(this));
            rvMenu.setNestedScrollingEnabled(false);
            rvMenu.setOverScrollMode(View.OVER_SCROLL_NEVER);
            rvMenu.setAdapter(adapter);
        }

        setActive(btnAll);
        applyFilter();

        ImageView btnMenuBurger = findViewById(R.id.btnMenuBurger);

        if (btnMenuBurger != null) {
            btnMenuBurger.setOnClickListener(this::showSideMenu);
        }

        View btnNotification = findViewById(R.id.btnNotification);

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotifications());
        }

        setupCategoryButtons();
        setupSearchFeature();

        TextView tvSeeAll = findViewById(R.id.tvSeeAll);
        if (tvSeeAll != null) {
            tvSeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AllMenuActivity.class);
                startActivity(intent);
            });
        }

        NavHelper.setup(this, "beranda");
    }

    private void setupLogo() {
        ImageView imgLogo = findViewById(R.id.imgLogo);

        if (imgLogo == null) return;

        int nightMode =
                getResources().getConfiguration().uiMode
                        & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            imgLogo.setImageResource(R.drawable.darkmode);
        } else {
            imgLogo.setImageResource(R.drawable.tittle);
        }
    }

    private void setupCategoryButtons() {
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> {
                currentCategory = "all";
                clearSearch();
                applyFilter();
                setActive(btnAll);
            });
        }

        if (btnDrink != null) {
            btnDrink.setOnClickListener(v -> {
                currentCategory = "Minuman";
                clearSearch();
                applyFilter();
                setActive(btnDrink);
            });
        }

        if (btnFood != null) {
            btnFood.setOnClickListener(v -> {
                currentCategory = "Makanan";
                clearSearch();
                applyFilter();
                setActive(btnFood);
            });
        }

        if (btnDessert != null) {
            btnDessert.setOnClickListener(v -> {
                currentCategory = "Cemilan";
                clearSearch();
                applyFilter();
                setActive(btnDessert);
            });
        }
    }

    private void setupSearchFeature() {
        if (etSearch == null) return;

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void clearSearch() {
        if (etSearch != null) {
            etSearch.setText("");
        }
    }

    private void setupBanner() {
        ViewPager2 bannerViewPager = findViewById(R.id.bannerViewPager);
        LinearLayout dotIndicator = findViewById(R.id.dotIndicator);

        if (bannerViewPager == null || dotIndicator == null) return;

        dotIndicator.removeAllViews();

        List<BannerItem> bannerItems = new ArrayList<>();

        bannerItems.add(
                new BannerItem(
                        "Good Food\nGood Mood",
                        "Nikmati makanan lezat\nala Fat Pandas!",
                        R.drawable.logo
                )
        );

        bannerItems.add(
                new BannerItem(
                        "Promo Spesial\nDiskon 20%",
                        "Hanya untuk menu\nAyam Crispy Pandas!",
                        R.drawable.ndas
                )
        );

        bannerItems.add(
                new BannerItem(
                        "Menu Baru\nCemilan Lezat",
                        "Cobain sensasi baru\nCheesecake Pandas!",
                        R.drawable.nays
                )
        );

        BannerAdapter bannerAdapter = new BannerAdapter(bannerItems);
        bannerAdapter.setOnOrderClickListener(() -> {
            View headerMenu = findViewById(R.id.headerMenu);
            androidx.core.widget.NestedScrollView homeScroll = findViewById(R.id.homeScroll);
            if (headerMenu != null && homeScroll != null) {
                homeScroll.smoothScrollTo(0, headerMenu.getTop());
            }
        });
        bannerViewPager.setAdapter(bannerAdapter);

        bannerViewPager.setCurrentItem(0, false);

        bannerViewPager.post(() -> {
            if (bannerItems.size() > 0) {
                int middle = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % bannerItems.size());
                bannerViewPager.setCurrentItem(middle, false);
            }
        });

        ImageView[] dots = new ImageView[bannerItems.size()];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.dot_indicator);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(8, 0, 8, 0);
            dotIndicator.addView(dots[i], params);
        }

        if (dots.length > 0) {
            dots[0].setSelected(true);
        }

        bannerViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        int realPosition = position % bannerItems.size();

                        for (int i = 0; i < dots.length; i++) {
                            dots[i].setSelected(i == realPosition);
                        }
                    }
                }
        );
    }

    private void setupPopularMenu(RecyclerView rvPopularMenu) {
        if (rvPopularMenu == null) return;

        List<MenuPandas> popularMenus = new ArrayList<>();

        MenuPandas ayam = findMenuByName("Ayam Crispy Pandas");
        MenuPandas matcha = findMenuByName("Matcha Latte");
        MenuPandas cheesecake = findMenuByName("Cheesecake Pandas");
        MenuPandas nasgor = findMenuByName("Nasi Goreng Pandas");

        if (ayam != null) popularMenus.add(ayam);
        if (matcha != null) popularMenus.add(matcha);
        if (cheesecake != null) popularMenus.add(cheesecake);
        if (nasgor != null) popularMenus.add(nasgor);

        rvPopularMenu.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvPopularMenu.setNestedScrollingEnabled(false);
        rvPopularMenu.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvPopularMenu.setAdapter(
                new PopularMenuAdapter(
                        this,
                        popularMenus
                )
        );
    }

    private void setupMenuData() {
        setupMenuDataOffline();

        // Ambil data dari MySQL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.URL_GET_ALL_MENU,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray result = jsonObject.getJSONArray(Configuration.TAG_JSON_ARRAY);
                        
                        allMenus.clear();
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject jo = result.getJSONObject(i);

                            String imageName = jo.getString("image_name");
                            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                            if (resId == 0) resId = R.drawable.logo;

                            double rating = jo.optDouble("rating", 4.0 + (Math.random() * 1.0));
                            int sold = jo.optInt("sold", 10 + (int)(Math.random() * 100));

                            allMenus.add(new MenuPandas(
                                    jo.getString("name"),
                                    jo.getString("description"),
                                    "Tidak ada", 
                                    jo.getInt("price"),
                                    jo.getString("category"),
                                    resId,
                                    rating,
                                    sold
                            ));
                        }
                        
                        applyFilter();
                        RecyclerView rvPopularMenu = findViewById(R.id.rvPopularMenu);
                        setupPopularMenu(rvPopularMenu);
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                        setupMenuDataOffline(); 
                    }
                },
                error -> setupMenuDataOffline()); 

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void setupMenuDataOffline() {
        allMenus.clear();
        // Menu Terlaris/Populer
        allMenus.add(new MenuPandas("Matcha Latte", "Perpaduan matcha premium", "Susu", 25000, "Minuman", R.drawable.matcha, 4.9, 120));
        allMenus.add(new MenuPandas("Ayam Crispy Pandas", "Ayam crispy renyah", "Gluten", 32000, "Makanan", R.drawable.ayam, 4.8, 245));
        allMenus.add(new MenuPandas("Nasi Goreng Pandas", "Nasi goreng khas", "Telur", 30000, "Makanan", R.drawable.nasgor, 4.8, 180));
        allMenus.add(new MenuPandas("Chocolate Drink", "Cokelat premium yang creamy", "Susu", 22000, "Minuman", R.drawable.choco, 4.7, 65));
        
        // Menu Lainnya
        allMenus.add(new MenuPandas("Cheesecake Pandas", "Kue keju lembut", "Susu", 30000, "Cemilan", R.drawable.ciskek, 4.8, 78));
        allMenus.add(new MenuPandas("Latte Pandas", "Kopi susu dengan foam lembut", "Susu, Kafein", 23000, "Minuman", R.drawable.latte, 4.6, 54));
        allMenus.add(new MenuPandas("Ricebowl Beef", "Nasi hangat daging sapi", "Gluten", 35000, "Makanan", R.drawable.ricebowl2, 4.5, 42));
        allMenus.add(new MenuPandas("Thai Tea Pandas", "Teh Thailand autentik", "Susu", 18000, "Minuman", R.drawable.thaitea, 4.4, 38));
        allMenus.add(new MenuPandas("Mie Goreng Pandas", "Mie goreng bumbu rempah", "Gluten", 25000, "Makanan", R.drawable.miegoreng, 4.3, 29));
        allMenus.add(new MenuPandas("Rollcake Pandas", "Rollcake cokelat lumer", "Susu", 28000, "Cemilan", R.drawable.cake, 4.4, 31));
        allMenus.add(new MenuPandas("Ricebowl Chicken", "Nasi ayam saus spesial", "Gluten", 28000, "Makanan", R.drawable.ricebowl, 4.3, 25));
        allMenus.add(new MenuPandas("Ice Drink Pandas", "Minuman segar", "Tidak ada", 15000, "Minuman", R.drawable.ice, 4.2, 19));
        allMenus.add(new MenuPandas("Es Teh Pandas", "Teh segar gula aren", "Kafein", 12000, "Minuman", R.drawable.es_teh, 4.0, 12));
        allMenus.add(new MenuPandas("Pisang Goreng", "Pisang goreng manis", "Gluten", 15000, "Cemilan", R.drawable.pisang, 4.1, 8));
        allMenus.add(new MenuPandas("Roti Bakar Pandas", "Roti bakar aneka topping", "Gluten", 20000, "Cemilan", R.drawable.rotibakar, 3.9, 6));
        allMenus.add(new MenuPandas("Tiramisu Pandas", "Dessert kopi Italia", "Susu", 28000, "Cemilan", R.drawable.tiramisu, 4.2, 15));
        allMenus.add(new MenuPandas("Mie Kuah Pandas", "Mie kuah kaldu gurih", "Gluten", 25000, "Makanan", R.drawable.miekuah, 4.1, 11));
        allMenus.add(new MenuPandas("Mix Platter Pandas", "Aneka gorengan & kentang", "Gluten", 35000, "Makanan", R.drawable.fries, 4.4, 18));

        applyFilter();
    }

    private void applyFilter() {
        if (adapter == null) return;

        String query = "";

        if (etSearch != null) {
            query =
                    etSearch.getText()
                            .toString()
                            .trim()
                            .toLowerCase(Locale.ROOT);
        }

        List<MenuPandas> result = new ArrayList<>();

        for (MenuPandas menu : allMenus) {
            boolean matchCategory =
                    currentCategory.equalsIgnoreCase("all")
                            || menu.getCategory().equalsIgnoreCase(currentCategory);

            boolean matchSearch =
                    query.isEmpty()
                            || menu.getName().toLowerCase(Locale.ROOT).contains(query)
                            || menu.getDescription().toLowerCase(Locale.ROOT).contains(query)
                            || menu.getCategory().toLowerCase(Locale.ROOT).contains(query);

            if (matchCategory && matchSearch) {
                result.add(menu);
            }
        }

        Collections.sort(
                result,
                (m1, m2) ->
                        m1.getName().compareToIgnoreCase(m2.getName())
        );

        adapter.updateList(result);
    }

    private void showSideMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);

        int currentNightMode =
                getResources().getConfiguration().uiMode
                        & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        String themeText =
                currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
                        ? "Mode Terang"
                        : "Mode Gelap";

        popup.getMenu().add("Informasi Toko");
        popup.getMenu().add("Promo Spesial");
        popup.getMenu().add("Rekomendasi Menu");
        popup.getMenu().add("Tambah Promo ke Cart");
        popup.getMenu().add(themeText);
        popup.getMenu().add("Bantuan & Dukungan");
        popup.getMenu().add("Keluar");

        popup.setOnMenuItemClickListener(item -> {
            String title = Objects.toString(item.getTitle(), "");

            switch (title) {
                case "Informasi Toko":
                    showStoreInfo();
                    break;

                case "Promo Spesial":
                    showPromos();
                    break;

                case "Rekomendasi Menu":
                    showRecommendation();
                    break;

                case "Tambah Promo ke Cart":
                    addPromoMenuToCart();
                    break;

                case "Mode Terang":
                case "Mode Gelap":
                    toggleTheme();
                    break;

                case "Bantuan & Dukungan":
                    showHelp();
                    break;

                case "Keluar":
                    finishAffinity();
                    break;
            }

            return true;
        });

        popup.show();
    }

    private void showStoreInfo() {
        showSimpleDialog(
                R.drawable.info,
                "Informasi Toko",
                "📍 Alamat: Jl. Raya Ciledug No. 99, Jakarta Selatan\n\n" +
                        "⏰ Jam Operasional:\n" +
                        "Senin - Jumat: 10:00 - 21:00\n" +
                        "Sabtu - Minggu: 09:00 - 22:00",
                "Oke"
        );
    }

    private void showPromos() {
        showSimpleDialog(
                R.drawable.ndas,
                "Promo Spesial 🎁",
                "🔥 Promo Bulan Ini:\n\n" +
                        "1. Diskon 20% untuk semua Makanan!\n" +
                        "2. Buy 1 Get 1 Free untuk Thai Tea.\n" +
                        "3. Gratis Rollcake untuk transaksi > 150rb.",
                "Tutup"
        );
    }

    private void showRecommendation() {
        MenuPandas recommendation;

        if (currentCategory.equalsIgnoreCase("Minuman")) {
            recommendation = findMenuByName("Matcha Latte");
        } else if (currentCategory.equalsIgnoreCase("Makanan")) {
            recommendation = findMenuByName("Ayam Crispy Pandas");
        } else if (currentCategory.equalsIgnoreCase("Cemilan")) {
            recommendation = findMenuByName("Cheesecake Pandas");
        } else {
            recommendation = findMenuByName("Ayam Crispy Pandas");
        }

        if (recommendation == null && !allMenus.isEmpty()) {
            recommendation = allMenus.get(0);
        }

        if (recommendation == null) return;

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_confirm_order, null);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
        }

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialogAlert);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        Button btnBatal = dialogView.findViewById(R.id.btnBatal);
        Button btnTambah = dialogView.findViewById(R.id.btnYa);

        if (imgDialog != null) {
            imgDialog.setImageResource(recommendation.getImageRes());
        }

        if (tvTitle != null) {
            tvTitle.setText("Rekomendasi Menu");
        }

        if (tvMessage != null) {
            tvMessage.setText(
                    recommendation.getName() + "\n\n" +
                            recommendation.getDescription() + "\n\n" +
                            "Harga: " + recommendation.getFormattedPrice()
            );
        }

        if (btnBatal != null) {
            btnBatal.setText("Tutup");
            btnBatal.setOnClickListener(v -> dialog.dismiss());
        }

        MenuPandas finalRecommendation = recommendation;

        if (btnTambah != null) {
            btnTambah.setText("Tambah");
            btnTambah.setOnClickListener(v -> {
                dialog.dismiss();

                cartManager.addItem(
                        new CartItem(
                                finalRecommendation.getName(),
                                finalRecommendation.getPrice(),
                                1,
                                finalRecommendation.getImageRes()
                        )
                );

                NavHelper.updateBadge(this);
                showCenterToast(
                        finalRecommendation.getName() + " masuk ke cart"
                );
            });
        }

        dialog.show();
        resizeDialog(dialog);
    }

    private MenuPandas findMenuByName(String name) {
        for (MenuPandas menu : allMenus) {
            if (menu.getName().equalsIgnoreCase(name)) {
                return menu;
            }
        }

        return null;
    }

    private void addPromoMenuToCart() {
        MenuPandas promoMenu = findMenuByName("Ayam Crispy Pandas");

        if (promoMenu == null) {
            showCenterToast("Menu promo tidak ditemukan");
            return;
        }

        cartManager.addItem(
                new CartItem(
                        promoMenu.getName(),
                        promoMenu.getPrice(),
                        1,
                        promoMenu.getImageRes()
                )
        );

        NavHelper.updateBadge(this);
        showCenterToast("Promo Ayam Crispy Pandas masuk ke cart");
    }

    private void showHelp() {
        showSimpleDialog(
                R.drawable.dukungan,
                "Bantuan & Dukungan",
                "Punya kendala atau pertanyaan?\n\n" +
                        "💬 WhatsApp: 0812-3456-7890\n" +
                        "📧 Email: support@fatpandas.com",
                "Tutup"
        );
    }

    private void showNotifications() {
        showSimpleDialog(
                R.drawable.notif,
                "Notifikasi 💌",
                "• Promo Ayam Crispy Pandas Diskon 20%!\n" +
                        "• Cek menu baru: Cheesecake Pandas!",
                "Tandai Dibaca"
        );

        View btnNotif = findViewById(R.id.btnNotification);

        if (btnNotif instanceof FrameLayout) {
            FrameLayout container = (FrameLayout) btnNotif;

            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);

                if (!(child instanceof ImageView)) {
                    child.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showSimpleDialog(
            int imageRes,
            String title,
            String message,
            String buttonText
    ) {
        View dialogView =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_confirm_order,
                                null
                        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
        }

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialogAlert);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        View btnBatal = dialogView.findViewById(R.id.btnBatal);
        Button btnOk = dialogView.findViewById(R.id.btnYa);

        if (imgDialog != null) {
            imgDialog.setImageResource(imageRes);
        }

        if (tvTitle != null) {
            tvTitle.setText(title);
        }

        if (tvMessage != null) {
            tvMessage.setText(message);
        }

        if (btnBatal != null) {
            btnBatal.setVisibility(View.GONE);
        }

        if (btnOk != null) {
            btnOk.setText(buttonText);
            btnOk.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
        resizeDialog(dialog);
    }

    private void resizeDialog(AlertDialog dialog) {
        if (dialog.getWindow() != null) {
            int width =
                    (int)
                            (
                                    getResources()
                                            .getDisplayMetrics()
                                            .widthPixels
                                            * 0.90
                            );

            dialog.getWindow()
                    .setLayout(
                            width,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
        }
    }

    private void toggleTheme() {
        int currentNightMode =
                getResources().getConfiguration().uiMode
                        & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );
        }
    }

    private void setActive(TextView active) {
        for (TextView button : new TextView[]{
                btnAll,
                btnDrink,
                btnFood,
                btnDessert
        }) {
            if (button == null) continue;

            if (button == active) {
                button.setBackgroundResource(R.drawable.bg_chip_active);
                button.setTextColor(
                        ContextCompat.getColor(
                                this,
                                android.R.color.white
                        )
                );
            } else {
                button.setBackgroundResource(R.drawable.bg_chip_inactive);
                button.setTextColor(
                        ContextCompat.getColor(
                                this,
                                R.color.green
                        )
                );
            }
        }
    }

    private void showCenterToast(String message) {
        Toast toast =
                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                );

        toast.setGravity(
                Gravity.CENTER,
                0,
                0
        );

        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupLogo();

        cartManager = CartManager.getInstance(this);
        cartManager.reloadFromDatabase();

        NavHelper.setup(this, "beranda");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocus = getCurrentFocus();

            if (currentFocus instanceof EditText) {
                Rect outRect = new Rect();
                currentFocus.getGlobalVisibleRect(outRect);

                if (!outRect.contains(
                        (int) event.getRawX(),
                        (int) event.getRawY()
                )) {
                    currentFocus.clearFocus();

                    InputMethodManager imm =
                            (InputMethodManager)
                                    getSystemService(INPUT_METHOD_SERVICE);

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(
                                currentFocus.getWindowToken(),
                                0
                        );
                    }
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }
}
