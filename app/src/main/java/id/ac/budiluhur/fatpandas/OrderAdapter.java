package id.ac.budiluhur.fatpandas;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.ac.budiluhur.fatpandas.model.OrderItem;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final Context context;
    private final List<OrderItem> orders;

    public OrderAdapter(Context context, List<OrderItem> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        OrderItem order = orders.get(position);

        holder.tvId.setText(order.getOrderId());
        holder.tvName.setText(order.getMenuName());
        holder.tvQty.setText(order.getQty() + "x item");
        holder.tvTotal.setText(order.getFormattedTotal());
        holder.tvTime.setText(order.getTime());
        holder.tvStatus.setText(order.getStatus());

        updateStatusStyle(holder.tvStatus, order.getStatus());

        holder.itemView.setOnClickListener(v -> showDetailDialog(order));
    }

    private void updateStatusStyle(TextView tvStatus, String status) {
        if (status == null) return;
        
        switch (status.toLowerCase()) {
            case "selesai":
                tvStatus.setBackgroundResource(R.drawable.bg_status_green);
                break;
            case "batal":
                tvStatus.setBackgroundResource(R.drawable.bg_status_red);
                break;
            default:
                tvStatus.setBackgroundResource(R.drawable.bg_status);
                break;
        }
    }

    private void showDetailDialog(OrderItem order) {
        String detailItems;

        if (order.getDetailItems() == null || order.getDetailItems().trim().isEmpty()) {
            detailItems = order.getMenuName();
        } else {
            detailItems = order.getDetailItems();
        }

        String notes;

        if (order.getNotes() == null || order.getNotes().trim().isEmpty()) {
            notes = "-";
        } else {
            notes = order.getNotes();
        }

        String message =
                "ID Pesanan: " + order.getOrderId() + "  |  Waktu: " + order.getTime() + "\n" +
                        "────────────────────\n" +
                        "Menu: " + order.getMenuName() + " (" + order.getQty() + "x)\n" +
                        "Total: " + order.getFormattedTotal() + "\n" +
                        "Tipe: " + order.getOrderType() + " | Meja: " + order.getMeja() + "\n" +
                        "Bayar: " + order.getPaymentMethod() + " | Status: " + order.getStatus() + "\n" +
                        "────────────────────\n" +
                        "Detail:\n" + detailItems + "\n" +
                        "Catatan: " + notes;

        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_confirm_order, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
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
        View btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        if (btnClose != null) {
            btnClose.setVisibility(View.VISIBLE);
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        if (imgDialog != null) {
            imgDialog.setImageResource(R.drawable.logo);
        }

        if (tvTitle != null) {
            tvTitle.setText("Detail Pesanan");
        }

        if (tvMessage != null) {
            tvMessage.setText(message);
            tvMessage.setGravity(Gravity.START);
        }

        if (btnBatal != null) {
            btnBatal.setVisibility(View.GONE);
        }

        if (btnOk != null) {
            btnOk.setText("Tutup");
            btnOk.setOnClickListener(v -> dialog.dismiss());
        }

        if (order.getStatus().equalsIgnoreCase("Diproses")) {
            if (btnBatal != null) {
                btnBatal.setVisibility(View.VISIBLE);
                if (btnBatal instanceof Button) {
                    ((Button) btnBatal).setText("Batalkan");
                    btnBatal.setOnClickListener(v -> {
                        updateStatus(order, "Batal");
                        dialog.dismiss();
                    });
                }
            }
            if (btnOk != null) {
                btnOk.setText("Selesaikan");
                btnOk.setOnClickListener(v -> {
                    updateStatus(order, "Selesai");
                    dialog.dismiss();
                });
            }
        }

        dialog.show();

        if (dialog.getWindow() != null) {
            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }

    private void updateStatus(OrderItem order, String newStatus) {
        // Update di SQLite lokal
        DatabaseHelper db = new DatabaseHelper(context);
        db.updateOrderStatus(order.getOrderId(), newStatus);
        
        // Update di MySQL via Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.BASE_URL + "update.php",
                response -> {
                    order.setStatus(newStatus);
                    notifyDataSetChanged();
                },
                error -> {
                    // Jika gagal ke server, tetap update di lokal tapi beri tahu user
                    order.setStatus(newStatus);
                    notifyDataSetChanged();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", order.getOrderId());
                params.put("status", newStatus);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvName;
        TextView tvQty;
        TextView tvTotal;
        TextView tvTime;
        TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvId = itemView.findViewById(R.id.tvOrderId);
            tvName = itemView.findViewById(R.id.tvOrderMenuName);
            tvQty = itemView.findViewById(R.id.tvOrderQty);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvTime = itemView.findViewById(R.id.tvOrderTime);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}