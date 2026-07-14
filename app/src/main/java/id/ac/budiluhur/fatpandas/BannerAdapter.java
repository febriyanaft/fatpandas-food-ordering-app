package id.ac.budiluhur.fatpandas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.ac.budiluhur.fatpandas.model.BannerItem;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<BannerItem> bannerItems;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick();
    }

    public BannerAdapter(List<BannerItem> bannerItems) {
        this.bannerItems = bannerItems;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);

        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BannerViewHolder holder,
            int position
    ) {

        if (bannerItems == null || bannerItems.isEmpty()) {
            return;
        }

        BannerItem item =
                bannerItems.get(position % bannerItems.size());

        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.imgBanner.setImageResource(item.getImageRes());

        holder.btnOrder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick();
            }
        });

        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    @Override
    public int getItemCount() {

        if (bannerItems == null || bannerItems.isEmpty()) {
            return 0;
        }

        return Integer.MAX_VALUE;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvSubtitle;
        TextView btnOrder;
        ImageView imgBanner;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvSubtitle = itemView.findViewById(R.id.tvBannerSubtitle);
            btnOrder = itemView.findViewById(R.id.btnBannerOrder);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }
}