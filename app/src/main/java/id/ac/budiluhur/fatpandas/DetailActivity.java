package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import id.ac.budiluhur.fatpandas.model.MenuPandas;

public class DetailActivity extends AppCompatActivity {

    private MenuPandas currentMenu;
    private ImageView btnFavorite;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_detail);

        cartManager = CartManager.getInstance(this);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    getOnBackPressedDispatcher().onBackPressed()
            );
        }

        // Ambil data dari Intent
        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        String allergen = getIntent().getStringExtra("allergen");
        String category = getIntent().getStringExtra("category");

        int price = getIntent().getIntExtra("price", 0);
        int imageRes = getIntent().getIntExtra("image", R.drawable.nasgor);
        
        // Ambil rating dan sold
        double rating = getIntent().getDoubleExtra("rating", 0.0);
        int sold = getIntent().getIntExtra("sold", 0);

        if (name == null) name = "Menu";
        if (desc == null) desc = "";
        if (allergen == null) allergen = "Tidak ada informasi alergen";
        if (category == null) category = "-";

        currentMenu = new MenuPandas(
                name,
                desc,
                allergen,
                price,
                category,
                imageRes,
                rating,
                sold
        );

        ImageView imgDetailMenu = findViewById(R.id.imgDetailMenu);
        TextView tvDetailName = findViewById(R.id.tvDetailName);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDetailDesc = findViewById(R.id.tvDetailDesc);
        TextView tvDetailDescAllergen = findViewById(R.id.tvDetailDescAllergen);
        TextView tvDetailRating = findViewById(R.id.tvDetailRating);

        if (imgDetailMenu != null) {
            imgDetailMenu.setImageResource(imageRes);
        }

        if (tvDetailName != null) {
            tvDetailName.setText(name);
        }

        if (tvDetailPrice != null) {
            String formattedPrice = String.format(Locale.GERMANY, "%,d", price);
            tvDetailPrice.setText(getString(R.string.cart_total_format, formattedPrice));
        }

        if (tvDetailDesc != null) {
            String fullDesc = desc + getString(R.string.detail_desc_suffix);
            tvDetailDesc.setText(fullDesc);
        }

        if (tvDetailDescAllergen != null) {
            tvDetailDescAllergen.setText(allergen);
        }

        if (tvDetailRating != null) {
            String ratingFormatted = String.format(Locale.US, "%.1f (%d)", rating, sold);
            tvDetailRating.setText(ratingFormatted);
        }

        btnFavorite = findViewById(R.id.btnFavorite);

        if (btnFavorite != null) {
            updateFavoriteUI();

            btnFavorite.setOnClickListener(v -> {
                cartManager.toggleFavorite(currentMenu);
                updateFavoriteUI();

                if (cartManager.isFavorite(currentMenu)) {
                    showCenterToast("Menu ditambahkan ke favorit");
                } else {
                    showCenterToast("Menu dihapus dari favorit");
                }
            });
        }

        View btnPesanSekarang = findViewById(R.id.btnPesanSekarang);

        if (btnPesanSekarang != null) {
            btnPesanSekarang.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrderActivity.class);
                intent.putExtra("name", currentMenu.getName());
                intent.putExtra("price", currentMenu.getPrice());
                intent.putExtra("image", currentMenu.getImageRes());
                startActivity(intent);
            });
        }
    }

    private void updateFavoriteUI() {
        if (btnFavorite == null || currentMenu == null) return;

        btnFavorite.setImageResource(R.drawable.ic_favorite_border);

        if (cartManager.isFavorite(currentMenu)) {
            btnFavorite.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.green)
                    )
            );
        } else {
            btnFavorite.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.icon_tint)
                    )
            );
        }
    }

    private void showCenterToast(String message) {
        Toast toast = Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        );

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        cartManager = CartManager.getInstance(this);
        cartManager.reloadFromDatabase();

        updateFavoriteUI();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
