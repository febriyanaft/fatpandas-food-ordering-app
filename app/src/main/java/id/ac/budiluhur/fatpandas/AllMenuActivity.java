package id.ac.budiluhur.fatpandas;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.ac.budiluhur.fatpandas.model.MenuPandas;

public class AllMenuActivity extends AppCompatActivity {

    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_all_menu);

        cartManager = CartManager.getInstance(this);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    getOnBackPressedDispatcher().onBackPressed()
            );
        }

        RecyclerView rv = findViewById(R.id.rvAllMenu);

        List<MenuPandas> allMenus = getAllMenus();

        Collections.sort(
                allMenus,
                (m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName())
        );

        MenuAdapter adapter = new MenuAdapter(this, allMenus);

        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(adapter);
        }

        NavHelper.setup(this, "all_menu");
    }

    private List<MenuPandas> getAllMenus() {
        List<MenuPandas> allMenus = new ArrayList<>();

        // Menu Terlaris
        allMenus.add(new MenuPandas(
                "Matcha Latte",
                "Perpaduan matcha premium dengan susu segar",
                "Susu, Kafein",
                25000,
                "Minuman",
                R.drawable.matcha,
                4.9,
                120
        ));

        allMenus.add(new MenuPandas(
                "Ayam Crispy Pandas",
                "Ayam crispy renyah dengan saus spesial Fat Pandas",
                "Gluten",
                32000,
                "Makanan",
                R.drawable.ayam,
                4.8,
                245
        ));

        allMenus.add(new MenuPandas(
                "Nasi Goreng Pandas",
                "Nasi goreng khas dengan topping ayam crispy",
                "Telur, Gluten, Kedelai",
                30000,
                "Makanan",
                R.drawable.nasgor,
                4.8,
                180
        ));

        // Menu Populer
        allMenus.add(new MenuPandas(
                "Chocolate Drink",
                "Cokelat premium yang creamy dan nikmat",
                "Susu",
                22000,
                "Minuman",
                R.drawable.choco,
                4.7,
                65
        ));

        allMenus.add(new MenuPandas(
                "Cheesecake Pandas",
                "Kue keju lembut dengan rasa premium",
                "Susu, Telur, Gluten",
                30000,
                "Cemilan",
                R.drawable.ciskek,
                4.8,
                78
        ));

        allMenus.add(new MenuPandas(
                "Latte Pandas",
                "Kopi susu dengan foam yang lembut",
                "Susu, Kafein",
                23000,
                "Minuman",
                R.drawable.latte,
                4.6,
                54
        ));

        allMenus.add(new MenuPandas(
                "Ricebowl Beef",
                "Nasi hangat dengan daging sapi pilihan",
                "Gluten, Kedelai",
                35000,
                "Makanan",
                R.drawable.ricebowl2,
                4.5,
                42
        ));

        // Menu Biasa
        allMenus.add(new MenuPandas(
                "Thai Tea Pandas",
                "Teh Thailand autentik dengan susu pilihan",
                "Susu, Kafein",
                18000,
                "Minuman",
                R.drawable.thaitea,
                4.4,
                38
        ));

        allMenus.add(new MenuPandas(
                "Mie Goreng Pandas",
                "Mie goreng bumbu rempah autentik",
                "Gluten, Telur, Kedelai",
                25000,
                "Makanan",
                R.drawable.miegoreng,
                4.3,
                29
        ));

        allMenus.add(new MenuPandas(
                "Rollcake Pandas",
                "Rollcake cokelat lumer yang nikmat",
                "Susu, Telur, Gluten",
                28000,
                "Cemilan",
                R.drawable.cake,
                4.4,
                31
        ));

        allMenus.add(new MenuPandas(
                "Ricebowl Chicken",
                "Nasi hangat dengan ayam saus spesial",
                "Gluten, Kedelai",
                28000,
                "Makanan",
                R.drawable.ricebowl,
                4.3,
                25
        ));

        allMenus.add(new MenuPandas(
                "Ice Drink Pandas",
                "Minuman segar pelepas dahaga",
                "Tidak ada",
                15000,
                "Minuman",
                R.drawable.ice,
                4.2,
                19
        ));

        // Menu Kurang Laris
        allMenus.add(new MenuPandas(
                "Es Teh Pandas",
                "Teh segar dengan gula aren khas Pandas",
                "Kafein",
                12000,
                "Minuman",
                R.drawable.es_teh,
                4.0,
                12
        ));

        allMenus.add(new MenuPandas(
                "Pisang Goreng",
                "Pisang goreng manis dan renyah",
                "Gluten",
                15000,
                "Cemilan",
                R.drawable.pisang,
                4.1,
                8
        ));

        allMenus.add(new MenuPandas(
                "Roti Bakar Pandas",
                "Roti bakar dengan aneka topping manis",
                "Gluten, Susu",
                20000,
                "Cemilan",
                R.drawable.rotibakar,
                3.9,
                6
        ));

        allMenus.add(new MenuPandas(
                "Tiramisu Pandas",
                "Dessert kopi khas Italia yang menggoda",
                "Susu, Telur, Kafein",
                28000,
                "Cemilan",
                R.drawable.tiramisu,
                4.2,
                15
        ));

        allMenus.add(new MenuPandas(
                "Mie Kuah Pandas",
                "Mie kuah hangat dengan kaldu gurih",
                "Gluten, Telur",
                25000,
                "Makanan",
                R.drawable.miekuah,
                4.1,
                11
        ));

        allMenus.add(new MenuPandas(
                "Mix Platter Pandas",
                "Aneka gorengan, kentang, dan saus spesial",
                "Gluten",
                35000,
                "Makanan",
                R.drawable.fries,
                4.4,
                18
        ));

        return allMenus;
    }

    @Override
    protected void onResume() {
        super.onResume();

        cartManager = CartManager.getInstance(this);
        cartManager.reloadFromDatabase();

        NavHelper.setup(this, "all_menu");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
