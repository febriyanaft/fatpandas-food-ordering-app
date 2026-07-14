package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

import id.ac.budiluhur.fatpandas.model.OrderItem;

public class ProfileActivity extends AppCompatActivity {

    private CartManager cartManager;

    private TextView tvTotalPesanan;
    private TextView tvTotalFavorit;
    private TextView tvTotalTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_profile);

        cartManager = CartManager.getInstance(this);

        tvTotalPesanan = findViewById(R.id.tvTotalPesanan);
        tvTotalFavorit = findViewById(R.id.tvTotalFavorit);
        tvTotalTransaksi = findViewById(R.id.tvTotalTransaksi);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnThemeToggle = findViewById(R.id.btnThemeToggle);
        
        // Statistik Cards
        View cardPesanan = findViewById(R.id.cardStatPesanan);
        View cardFavorit = findViewById(R.id.cardStatFavorit);
        View cardTransaksi = findViewById(R.id.cardStatTransaksi);

        LinearLayout menuPesanan = findViewById(R.id.menuPesananSaya);
        LinearLayout menuFavorit = findViewById(R.id.menuFavorit);
        LinearLayout menuTentang = findViewById(R.id.menuTentang);
        View btnLogout = findViewById(R.id.btnLogout);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        updateThemeIcon(btnThemeToggle);

        if (btnThemeToggle != null) {
            btnThemeToggle.setOnClickListener(v -> toggleTheme());
        }

        // Click listeners untuk Dashboard Statistik
        if (cardPesanan != null) {
            cardPesanan.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, OrderListActivity.class))
            );
        }

        if (cardFavorit != null) {
            cardFavorit.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class))
            );
        }

        if (cardTransaksi != null) {
            cardTransaksi.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, OrderListActivity.class))
            );
        }

        if (menuPesanan != null) {
            menuPesanan.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, OrderListActivity.class))
            );
        }

        if (menuFavorit != null) {
            menuFavorit.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class))
            );
        }

        if (menuTentang != null) {
            menuTentang.setOnClickListener(v -> showAboutApp());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> handleLogout());
        }

        loadStatistics();
        NavHelper.setup(this, "profil");
    }

    private void loadStatistics() {
        cartManager = CartManager.getInstance(this);
        cartManager.reloadFromDatabase();

        int totalPesanan = cartManager.getOrderHistory().size();
        int totalFavorit = cartManager.getFavoriteItems().size();

        int totalTransaksi = 0;
        for (OrderItem order : cartManager.getOrderHistory()) {
            totalTransaksi += order.getTotalPrice();
        }

        if (tvTotalPesanan != null) {
            tvTotalPesanan.setText(String.valueOf(totalPesanan));
        }

        if (tvTotalFavorit != null) {
            tvTotalFavorit.setText(String.valueOf(totalFavorit));
        }

        if (tvTotalTransaksi != null) {
            tvTotalTransaksi.setText(
                    "Rp " + String.format(Locale.GERMANY, "%,d", totalTransaksi)
            );
        }
    }

    private void handleLogout() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_confirm_order, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View imgAlert = dialogView.findViewById(R.id.imgDialogAlert);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        Button btnBatal = dialogView.findViewById(R.id.btnBatal);
        Button btnYa = dialogView.findViewById(R.id.btnYa);

        if (imgAlert != null) imgAlert.setVisibility(View.GONE);

        if (tvTitle != null) {
            tvTitle.setText("Logout");
        }

        if (tvMessage != null) {
            tvMessage.setText("Apakah Anda yakin ingin keluar dari aplikasi Fat Pandas?");
        }

        if (btnBatal != null) {
            btnBatal.setText("Batal");
            btnBatal.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnYa != null) {
            btnYa.setText("Logout");
            btnYa.setOnClickListener(v -> {
                dialog.dismiss();
                finishAffinity();
            });
        }

        dialog.show();
        resizeDialog(dialog);
    }

    private void updateThemeIcon(ImageView imageView) {
        if (imageView == null) return;

        int currentNightMode =
                getResources().getConfiguration().uiMode
                        & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            imageView.setImageResource(R.drawable.ic_sun);
        } else {
            imageView.setImageResource(R.drawable.ic_moon);
        }
    }

    private void toggleTheme() {
        int currentNightMode =
                getResources().getConfiguration().uiMode
                        & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void showAboutApp() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_confirm_order, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialogAlert);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        View btnBatal = dialogView.findViewById(R.id.btnBatal);
        Button btnOk = dialogView.findViewById(R.id.btnYa);

        if (imgDialog != null) {
            imgDialog.setImageResource(R.drawable.logo);
        }

        if (tvTitle != null) {
            tvTitle.setText("Tentang FatPandas");
        }

        if (tvMessage != null) {
            tvMessage.setText(
                    "Fat Pandas adalah aplikasi pemesanan makanan Nusantara modern.\n\n" +
                            "Versi : 1.0.0"
            );
        }

        if (btnBatal != null) {
            btnBatal.setVisibility(View.GONE);
        }

        if (btnOk != null) {
            btnOk.setText("Tutup");
            btnOk.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
        resizeDialog(dialog);
    }

    private void resizeDialog(AlertDialog dialog) {
        if (dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cartManager = CartManager.getInstance(this);
        cartManager.reloadFromDatabase();

        loadStatistics();

        NavHelper.setup(this, "profil");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}