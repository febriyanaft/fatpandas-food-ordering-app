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

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final Context context;
    private List<MenuPandas> menuList;

    public MenuAdapter(Context context, List<MenuPandas> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    public void updateList(List<MenuPandas> newList) {
        this.menuList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        MenuPandas menu = menuList.get(position);

        holder.tvName.setText(menu.getName());
        holder.tvDesc.setText(menu.getDescription());
        holder.tvPrice.setText(menu.getFormattedPrice());
        holder.imgMenu.setImageResource(menu.getImageRes());

        holder.tvRating.setText(menu.getFormattedRating());
        holder.tvSold.setText("(" + menu.getSold() + " terjual)");

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, DetailActivity.class);

            intent.putExtra("name", menu.getName());
            intent.putExtra("desc", menu.getDescription());
            intent.putExtra("allergen", menu.getAllergen());
            intent.putExtra("price", menu.getPrice());
            intent.putExtra("image", menu.getImageRes());
            intent.putExtra("category", menu.getCategory());
            intent.putExtra("rating", menu.getRating());
            intent.putExtra("sold", menu.getSold());

            context.startActivity(intent);
        });

        holder.btnAdd.setOnClickListener(v -> {

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
        return menuList == null ? 0 : menuList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgMenu;
        ImageView btnAdd;

        TextView tvName;
        TextView tvDesc;
        TextView tvPrice;
        TextView tvRating;
        TextView tvSold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMenu = itemView.findViewById(R.id.imgMenu);
            btnAdd = itemView.findViewById(R.id.btnAdd);

            tvName = itemView.findViewById(R.id.tvMenuName);
            tvDesc = itemView.findViewById(R.id.tvMenuDesc);
            tvPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvRating = itemView.findViewById(R.id.tvMenuRating);
            tvSold = itemView.findViewById(R.id.tvMenuSold);
        }
    }
}
