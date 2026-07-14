package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.ac.budiluhur.fatpandas.model.CartItem;
import id.ac.budiluhur.fatpandas.model.OrderItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private List<CartItem> checkoutItems = new ArrayList<>();
    private TextView tvTotal;
    private RadioGroup rgOrderType, rgPayment;
    private SummaryAdapter adapter;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_order);

        cartManager = CartManager.getInstance(this);

        String singleName = getIntent().getStringExtra("name");

        if (singleName != null) {
            int price = getIntent().getIntExtra("price", 0);
            int img = getIntent().getIntExtra("image", R.drawable.nasgor);
            checkoutItems.add(new CartItem(singleName, price, 1, img));
        } else {
            checkoutItems = new ArrayList<>(cartManager.getSelectedItems());
        }

        if (checkoutItems.isEmpty()) {
            Toast.makeText(this, R.string.toast_cart_empty, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTotal = findViewById(R.id.tvTotal);
        rgOrderType = findViewById(R.id.rgOrderType);
        rgPayment = findViewById(R.id.rgPayment);

        RecyclerView rv = findViewById(R.id.rvOrderItems);
        adapter = new SummaryAdapter(checkoutItems, this::updateTotalPrice);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        updateTotalPrice();

        findViewById(R.id.btnBack).setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        findViewById(R.id.btnLanjutKonfirmasi).setOnClickListener(v -> prosesKonfirmasiPesanan());
    }

    private void prosesKonfirmasiPesanan() {
        EditText etNama = findViewById(R.id.etNama);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etMeja = findViewById(R.id.etMeja);
        EditText etNotes = findViewById(R.id.etCatatan);

        String inputNama = etNama.getText().toString().trim();
        String inputPhone = etPhone.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String meja = etMeja.getText().toString().trim().isEmpty()
                ? "-"
                : etMeja.getText().toString().trim();

        if (inputNama.isEmpty()) {
            etNama.setError("Nama harus diisi");
            etNama.requestFocus();
            return;
        }

        if (inputPhone.isEmpty()) {
            etPhone.setError("No. Telp harus diisi");
            etPhone.requestFocus();
            return;
        }

        int typeId = rgOrderType.getCheckedRadioButtonId();
        int payId = rgPayment.getCheckedRadioButtonId();

        if (typeId == -1) {
            Toast.makeText(this, "Pilih jenis pesanan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (payId == -1) {
            Toast.makeText(this, "Pilih metode pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderType = ((RadioButton) findViewById(typeId)).getText().toString();
        String paymentMethod = ((RadioButton) findViewById(payId)).getText().toString();
        String waktu = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());

        int finalTotal = hitungTotalHarga();
        int totalQty = totalItemsCount();

        tampilDialogKonfirmasi(
                meja,
                notes,
                waktu,
                orderType,
                paymentMethod,
                finalTotal,
                totalQty
        );
    }

    private void tampilDialogKonfirmasi(
            String meja,
            String notes,
            String waktu,
            String orderType,
            String paymentMethod,
            int finalTotal,
            int totalQty
    ) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_confirm_order, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View btnBatalCustom = dialogView.findViewById(R.id.btnBatal);
        View btnYaCustom = dialogView.findViewById(R.id.btnYa);

        if (btnBatalCustom != null) {
            btnBatalCustom.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnYaCustom != null) {
            btnYaCustom.setOnClickListener(v -> {
                dialog.dismiss();

                StringBuilder detailBuilder = new StringBuilder();
                for (CartItem item : checkoutItems) {
                    detailBuilder.append("• ")
                            .append(item.getName())
                            .append(" (")
                            .append(item.getQty())
                            .append("x)\n");
                }

                String orderId = String.format(
                        Locale.getDefault(),
                        "#FP%03d",
                        cartManager.getOrderHistory().size() + 1
                );

                String histName;
                if (checkoutItems.size() > 1) {
                    histName = checkoutItems.get(0).getName()
                            + " +"
                            + (checkoutItems.size() - 1);
                } else {
                    histName = checkoutItems.get(0).getName();
                }

                OrderItem order = new OrderItem(
                        orderId,
                        histName,
                        totalQty,
                        finalTotal,
                        waktu,
                        meja,
                        orderType,
                        paymentMethod,
                        detailBuilder.toString(),
                        notes
                );

                cartManager.addOrder(order);

                // KIRIM DATA KE LOCALHOST (DATABASE MYSQL)
                sendOrderToServer(order);

                if (getIntent().getStringExtra("name") == null) {
                    cartManager.removeCheckedOutItems(new ArrayList<>(checkoutItems));
                }

                Intent intent = new Intent(this, SuccessActivity.class);
                intent.putExtra("name", histName);
                intent.putExtra("qty", totalQty);
                intent.putExtra("total", finalTotal);
                intent.putExtra("meja", meja);
                intent.putExtra("waktu", waktu);
                intent.putExtra("type", orderType);
                intent.putExtra("pay", paymentMethod);
                startActivity(intent);
                finish();
            });
        }

        dialog.show();

        if (dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private int totalItemsCount() {
        int count = 0;
        for (CartItem item : checkoutItems) {
            count += item.getQty();
        }
        return count;
    }

    private int hitungTotalHarga() {
        int total = 0;
        for (CartItem item : checkoutItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    private void updateTotalPrice() {
        int total = hitungTotalHarga();
        String formattedTotal = String.format(Locale.GERMANY, "%,d", total);
        tvTotal.setText(getString(R.string.cart_total_format, formattedTotal));
    }

    private void sendOrderToServer(OrderItem order) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.URL_ADD_ORDER,
                response -> {
                    // Berhasil dikirim ke MySQL
                    Toast.makeText(OrderActivity.this, "Berhasil simpan ke database!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Gagal kirim
                    String errorMsg = "Gagal simpan ke database";
                    if (error.networkResponse != null) {
                        errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null) {
                        errorMsg += ": " + error.getMessage();
                    } else {
                        errorMsg += ": Cek koneksi XAMPP/IP 10.0.2.2";
                    }
                    Toast.makeText(OrderActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Configuration.KEY_ORDER_ID, order.getOrderId());
                params.put(Configuration.KEY_ORDER_MENU_NAME, order.getMenuName());
                params.put(Configuration.KEY_ORDER_QTY, String.valueOf(order.getQty()));
                params.put(Configuration.KEY_ORDER_TOTAL, String.valueOf(order.getTotalPrice()));
                params.put(Configuration.KEY_ORDER_MEJA, order.getMeja());
                params.put(Configuration.KEY_ORDER_TYPE, order.getOrderType());
                params.put(Configuration.KEY_ORDER_PAYMENT, order.getPaymentMethod());
                params.put(Configuration.KEY_ORDER_DETAIL, order.getDetailItems());
                params.put(Configuration.KEY_ORDER_NOTES, order.getNotes());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    static class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.VH> {

        private final List<CartItem> items;
        private final Runnable onQtyChanged;

        SummaryAdapter(List<CartItem> items, Runnable onQtyChanged) {
            this.items = items;
            this.onQtyChanged = onQtyChanged;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_summary, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CartItem item = items.get(position);

            holder.name.setText(item.getName());
            holder.qty.setText(String.valueOf(item.getQty()));
            holder.price.setText(item.getFormattedPrice());
            holder.img.setImageResource(item.getImageRes());

            holder.btnPlus.setOnClickListener(v -> {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos == RecyclerView.NO_POSITION) return;

                item.setQty(item.getQty() + 1);
                holder.qty.setText(String.valueOf(item.getQty()));
                onQtyChanged.run();
            });

            holder.btnMinus.setOnClickListener(v -> {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos == RecyclerView.NO_POSITION) return;

                if (item.getQty() > 1) {
                    item.setQty(item.getQty() - 1);
                    holder.qty.setText(String.valueOf(item.getQty()));
                    onQtyChanged.run();
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView name, qty, price;
            ImageView img, btnPlus, btnMinus;

            VH(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tvSummaryName);
                qty = itemView.findViewById(R.id.tvSummaryQty);
                price = itemView.findViewById(R.id.tvSummaryPrice);
                img = itemView.findViewById(R.id.imgSummary);
                btnPlus = itemView.findViewById(R.id.btnSummaryPlus);
                btnMinus = itemView.findViewById(R.id.btnSummaryMinus);
            }
        }
    }
}