package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_success);

        // Get data from Intent
        String name = getIntent().getStringExtra("name");
        int qty = getIntent().getIntExtra("qty", 1);
        int total = getIntent().getIntExtra("total", 0);
        String meja = getIntent().getStringExtra("meja");
        String waktu = getIntent().getStringExtra("waktu");
        String type = getIntent().getStringExtra("type");
        String pay = getIntent().getStringExtra("pay");

        // Bind Views
        TextView tvMenu = findViewById(R.id.tvSuccessMenu);
        TextView tvQty = findViewById(R.id.tvSuccessQty);
        TextView tvTotal = findViewById(R.id.tvSuccessTotal);
        TextView tvMeja = findViewById(R.id.tvSuccessMeja);
        TextView tvType = findViewById(R.id.tvSuccessType);
        TextView tvPayment = findViewById(R.id.tvSuccessPayment);
        TextView tvWaktu = findViewById(R.id.tvSuccessWaktu);

        // Set values if not null
        if (name != null) tvMenu.setText(name);
        tvQty.setText(String.valueOf(qty));
        
        // Format Currency
        String formattedTotal = NumberFormat.getCurrencyInstance(new Locale("id", "ID"))
                .format(total).replace(",00", "").replace("Rp", "Rp ");
        tvTotal.setText(formattedTotal);
        
        if (meja != null) tvMeja.setText(meja);
        if (type != null) tvType.setText(type);
        if (pay != null) tvPayment.setText(pay);
        if (waktu != null) tvWaktu.setText(waktu);

        View btnBack = findViewById(R.id.btnBack);
        View btnKembali = findViewById(R.id.btnKembali);
        View btnLihatPesanan = findViewById(R.id.btnLihatPesanan);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> kembaliKeBeranda());
        }

        if (btnKembali != null) {
            btnKembali.setOnClickListener(v -> kembaliKeBeranda());
        }

        if (btnLihatPesanan != null) {
            btnLihatPesanan.setOnClickListener(v -> {
                startActivity(new Intent(this, OrderListActivity.class));
                finish();
            });
        }
    }

    private void kembaliKeBeranda() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}