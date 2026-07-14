package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import id.ac.budiluhur.fatpandas.model.CartItem;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private TextView tvTotal;
    private TextView tvItemCount;
    private LinearLayout emptyState;
    private CartAdapter cartAdapter;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance(this);

        rvCart = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvCartTotal);
        tvItemCount = findViewById(R.id.tvCartItemCount);
        emptyState = findViewById(R.id.emptyState);

        View btnBack = findViewById(R.id.btnBack);
        View btnCheckout = findViewById(R.id.btnCheckout);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );
                startActivity(intent);
                finish();
            });
        }

        cartAdapter = new CartAdapter(
                cartManager.getCartItems(),
                this::updateTotals
        );

        if (rvCart != null) {
            rvCart.setLayoutManager(new LinearLayoutManager(this));
            rvCart.setAdapter(cartAdapter);
        }

        updateTotals();

        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                List<CartItem> toCheckout = cartManager.getSelectedItems();

                if (toCheckout.isEmpty()) {
                    Toast toast = Toast.makeText(
                            this,
                            R.string.toast_cart_empty,
                            Toast.LENGTH_SHORT
                    );
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Intent intent = new Intent(this, OrderActivity.class);
                    startActivity(intent);
                }
            });
        }

        NavHelper.setup(this, "keranjang");
    }

    @Override
    protected void onResume() {
        super.onResume();

        cartManager.reloadFromDatabase();

        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }

        updateTotals();

        NavHelper.setup(this, "keranjang");
    }

    private void updateTotals() {
        if (cartManager == null) return;

        int total = cartManager.getSelectedTotal();
        int count = cartManager.getSelectedCount();

        String formattedTotal = String.format(Locale.GERMANY, "%,d", total);

        if (tvTotal != null) {
            tvTotal.setText(getString(R.string.cart_total_format, formattedTotal));
        }

        if (tvItemCount != null) {
            tvItemCount.setText(getString(R.string.cart_item_count_format, count));
        }

        boolean empty = cartManager.getCartItems().isEmpty();

        if (emptyState != null) {
            emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        }

        if (rvCart != null) {
            rvCart.setVisibility(empty ? View.GONE : View.VISIBLE);
        }

        NavHelper.updateBadge(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    static class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

        private final List<CartItem> items;
        private final Runnable onChanged;

        CartAdapter(List<CartItem> items, Runnable onChanged) {
            this.items = items;
            this.onChanged = onChanged;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);

            return new VH(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull VH holder,
                int position
        ) {
            CartItem item = items.get(position);

            holder.img.setImageResource(item.getImageRes());
            holder.tvName.setText(item.getName());
            holder.tvPrice.setText(item.getFormattedPrice());
            holder.tvQty.setText(String.valueOf(item.getQty()));

            holder.cbSelect.setOnCheckedChangeListener(null);
            holder.cbSelect.setChecked(item.isSelected());

            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setSelected(isChecked);
                CartManager.getInstance().updateItem(item);

                if (onChanged != null) {
                    onChanged.run();
                }
            });

            holder.btnPlus.setOnClickListener(v -> {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                item.setQty(item.getQty() + 1);
                CartManager.getInstance().updateItem(item);

                notifyItemChanged(currentPosition);

                if (onChanged != null) {
                    onChanged.run();
                }
            });

            holder.btnMinus.setOnClickListener(v -> {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                if (item.getQty() > 1) {
                    item.setQty(item.getQty() - 1);
                    CartManager.getInstance().updateItem(item);

                    notifyItemChanged(currentPosition);

                    if (onChanged != null) {
                        onChanged.run();
                    }
                } else {
                    showDeleteDialog(holder.itemView.getContext(), currentPosition);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                showDeleteDialog(holder.itemView.getContext(), currentPosition);
            });
        }

        private void showDeleteDialog(
                android.content.Context context,
                int position
        ) {
            View dialogView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_confirm_order, null);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );
            }

            View imgAlert = dialogView.findViewById(R.id.imgDialogAlert);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
            Button btnBatal = dialogView.findViewById(R.id.btnBatal);
            Button btnHapus = dialogView.findViewById(R.id.btnYa);

            if (imgAlert != null) {
                imgAlert.setVisibility(View.GONE);
            }

            if (tvTitle != null) {
                tvTitle.setText(R.string.content_desc_delete);
            }

            if (tvMessage != null) {
                tvMessage.setText(R.string.confirm_delete_msg);
            }

            if (btnBatal != null) {
                btnBatal.setText(R.string.btn_cancel);
                btnBatal.setOnClickListener(v -> dialog.dismiss());
            }

            if (btnHapus != null) {
                btnHapus.setText(R.string.btn_confirm_hapus);

                btnHapus.setOnClickListener(v -> {
                    if (position >= 0 && position < items.size()) {
                        CartManager.getInstance().removeItem(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, items.size());

                        if (onChanged != null) {
                            onChanged.run();
                        }
                    }

                    dialog.dismiss();
                });
            }

            dialog.show();

            if (dialog.getWindow() != null) {
                int width = (int)
                        (context.getResources().getDisplayMetrics().widthPixels * 0.90);

                dialog.getWindow().setLayout(
                        width,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        static class VH extends RecyclerView.ViewHolder {

            ImageView img;
            ImageView btnPlus;
            ImageView btnMinus;
            ImageView btnDelete;

            TextView tvName;
            TextView tvPrice;
            TextView tvQty;

            CheckBox cbSelect;

            VH(@NonNull View itemView) {
                super(itemView);

                cbSelect = itemView.findViewById(R.id.cbSelect);
                img = itemView.findViewById(R.id.imgCartItem);
                tvName = itemView.findViewById(R.id.tvCartName);
                tvPrice = itemView.findViewById(R.id.tvCartPrice);
                tvQty = itemView.findViewById(R.id.tvCartQty);
                btnPlus = itemView.findViewById(R.id.btnCartPlus);
                btnMinus = itemView.findViewById(R.id.btnCartMinus);
                btnDelete = itemView.findViewById(R.id.btnCartDelete);
            }
        }
    }
}