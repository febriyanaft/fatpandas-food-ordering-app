package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.ac.budiluhur.fatpandas.model.OrderItem;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private View emptyView;
    private CartManager cartManager;

    private TextView btnStatusSemua;
    private TextView btnStatusDiproses;
    private TextView btnStatusSelesai;
    private TextView btnStatusBatal;

    private List<OrderItem> allOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_order_list);

        cartManager = CartManager.getInstance(this);

        rvOrders = findViewById(R.id.rvOrders);
        emptyView = findViewById(R.id.emptyOrders);

        btnStatusSemua = findViewById(R.id.btnStatusSemua);
        btnStatusDiproses = findViewById(R.id.btnStatusDiproses);
        btnStatusSelesai = findViewById(R.id.btnStatusSelesai);
        btnStatusBatal = findViewById(R.id.btnStatusBatal);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        tampilDataPesanan();
        setupFilterButtons();
        setActiveChip(btnStatusSemua);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );
            startActivity(intent);
            finish();
        });

        NavHelper.setup(this, "pesanan");
    }

    @Override
    protected void onResume() {
        super.onResume();

        cartManager.reloadFromDatabase();
        tampilDataPesanan();
        setActiveChip(btnStatusSemua);

        NavHelper.setup(this, "pesanan");
    }

    private void tampilDataPesanan() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configuration.URL_GET_ORDERS,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray result = jsonObject.getJSONArray(Configuration.TAG_JSON_ARRAY);
                        List<OrderItem> listFromMySQL = new ArrayList<>();

                        for (int i = 0; i < result.length(); i++) {
                            JSONObject jo = result.getJSONObject(i);
                            OrderItem item = new OrderItem(
                                    jo.getString("order_id"),
                                    jo.getString("menu_name"),
                                    jo.getInt("qty"),
                                    jo.getInt("total_price"),
                                    jo.getString("time"),
                                    jo.getString("meja"),
                                    jo.getString("order_type"),
                                    jo.getString("payment_method"),
                                    jo.getString("detail_items"),
                                    jo.getString("notes")
                            );
                            item.setStatus(jo.getString("status"));
                            listFromMySQL.add(item);
                        }

                        allOrders = listFromMySQL;
                        updateUI();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        loadFromSQLite();
                    }
                },
                error -> loadFromSQLite());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void loadFromSQLite() {
        allOrders = cartManager.getOrderHistory();
        updateUI();
    }

    private void updateUI() {
        if (allOrders == null || allOrders.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            rvOrders.setAdapter(new OrderAdapter(this, allOrders));
        }
    }

    private void setupFilterButtons() {
        if (btnStatusSemua != null) {
            btnStatusSemua.setOnClickListener(v -> {
                setActiveChip(btnStatusSemua);
                tampilDataPesanan();
            });
        }

        if (btnStatusDiproses != null) {
            btnStatusDiproses.setOnClickListener(v -> {
                setActiveChip(btnStatusDiproses);
                filterStatus("Diproses");
            });
        }

        if (btnStatusSelesai != null) {
            btnStatusSelesai.setOnClickListener(v -> {
                setActiveChip(btnStatusSelesai);
                filterStatus("Selesai");
            });
        }

        if (btnStatusBatal != null) {
            btnStatusBatal.setOnClickListener(v -> {
                setActiveChip(btnStatusBatal);
                filterStatus("Batal");
            });
        }
    }

    private void filterStatus(String status) {
        if (allOrders == null) return;

        List<OrderItem> filtered = new ArrayList<>();

        for (OrderItem order : allOrders) {
            if (order.getStatus() != null &&
                    order.getStatus().equalsIgnoreCase(status)) {
                filtered.add(order);
            }
        }

        if (filtered.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            rvOrders.setAdapter(new OrderAdapter(this, filtered));
        }
    }

    private void setActiveChip(TextView active) {
        TextView[] chips = {
                btnStatusSemua,
                btnStatusDiproses,
                btnStatusSelesai,
                btnStatusBatal
        };

        for (TextView chip : chips) {
            if (chip == null) continue;

            if (chip == active) {
                chip.setBackgroundResource(R.drawable.bg_chip_active);
                chip.setTextColor(getColor(android.R.color.white));
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_inactive);
                chip.setTextColor(getColor(R.color.green));
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}