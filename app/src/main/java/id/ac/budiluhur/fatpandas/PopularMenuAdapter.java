package id.ac.budiluhur.fatpandas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.ac.budiluhur.fatpandas.model.CartItem;
import id.ac.budiluhur.fatpandas.model.MenuPandas;

public class PopularMenuAdapter extends RecyclerView.Adapter<PopularMenuAdapter.ViewHolder> {

    private final Context context;
    private final List<MenuPandas> popularMenus;

    public PopularMenuAdapter(Context context, List<MenuPandas> popularMenus) {
        this.context = context;
        this.popularMenus = popularMenus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_popular, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        MenuPandas menu = popularMenus.get(position);

        holder.imgPopularMenu.setImageResource(menu.getImageRes());
        holder.tvPopularName.setText(menu.getName());
        holder.tvPopularPrice.setText(menu.getFormattedPrice());

        String ratingText = String.format(java.util.Locale.US, "%.1f (%d terjual)", menu.getRating(), menu.getSold());
        holder.tvPopularRating.setText(ratingText);

        if (position == 0) {
            holder.tvPopularBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvPopularBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", menu.getName());
            intent.putExtra("desc", menu.getDescription());
            intent.putExtra("allergen", menu.getAllergen());
            intent.putExtra("price", menu.getPrice());
            intent.putExtra("image", menu.getImageRes());
            intent.putExtra("category", menu.getCategory());
            intent.putExtra("rating", menu.getRating()); // Kirim rating
            intent.putExtra("sold", menu.getSold());     // Kirim sold
            context.startActivity(intent);
        });

        holder.btnPopularAdd.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(80)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(80)
                    );

            CartManager cartManager = CartManager.getInstance(context);

            CartItem item = new CartItem(
                    menu.getName(),
                    menu.getPrice(),
                    1,
                    menu.getImageRes()
            );

            cartManager.addItem(item);

            Toast toast = Toast.makeText(
                    context,
                    context.getString(
                            R.string.toast_menu_added_format,
                            menu.getName()
                    ),
                    Toast.LENGTH_SHORT
            );

            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            if (context instanceof Activity) {
                NavHelper.updateBadge((Activity) context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularMenus == null ? 0 : popularMenus.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPopularMenu;
        ImageView btnPopularAdd;
        TextView tvPopularName;
        TextView tvPopularPrice;
        TextView tvPopularRating;
        TextView tvPopularBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPopularMenu = itemView.findViewById(R.id.imgPopularMenu);
            btnPopularAdd = itemView.findViewById(R.id.btnPopularAdd);
            tvPopularName = itemView.findViewById(R.id.tvPopularName);
            tvPopularPrice = itemView.findViewById(R.id.tvPopularPrice);
            tvPopularRating = itemView.findViewById(R.id.tvPopularRating);
            tvPopularBadge = itemView.findViewById(R.id.tvPopularBadge);
        }
    }
}