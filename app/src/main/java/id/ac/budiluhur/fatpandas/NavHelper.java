package id.ac.budiluhur.fatpandas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public class NavHelper {

    private static final String TAB_HOME     = "beranda";
    private static final String TAB_ORDERS   = "pesanan";
    private static final String TAB_CART     = "keranjang";
    private static final String TAB_PROFILE  = "profil";

    public static void setup(Activity activity, String activeTab) {
        View bottomNav = activity.findViewById(R.id.bottomNavContainer);
        if (bottomNav == null) return;

        TextView navBeranda   = bottomNav.findViewById(R.id.navBeranda);
        TextView navPesanan   = bottomNav.findViewById(R.id.navPesanan);
        FrameLayout navKeranjang = bottomNav.findViewById(R.id.navKeranjang);
        TextView tvKeranjang  = bottomNav.findViewById(R.id.tvNavKeranjangLabel);
        TextView navProfil    = bottomNav.findViewById(R.id.navProfil);

        // Set active/inactive states
        setTabActive(navBeranda,  activeTab.equals(TAB_HOME),    activity);
        setTabActive(navPesanan,  activeTab.equals(TAB_ORDERS),  activity);
        setTabActive(tvKeranjang, activeTab.equals(TAB_CART),    activity);
        setTabActive(navProfil,   activeTab.equals(TAB_PROFILE), activity);

        // Initial badge update
        updateBadge(activity);

        // Click listeners
        if (navBeranda != null) navBeranda.setOnClickListener(v -> {
            if (!activeTab.equals(TAB_HOME)) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        });
        if (navPesanan != null) navPesanan.setOnClickListener(v -> {
            if (!activeTab.equals(TAB_ORDERS)) {
                Intent intent = new Intent(activity, OrderListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        });
        if (navKeranjang != null) navKeranjang.setOnClickListener(v -> {
            if (!activeTab.equals(TAB_CART)) {
                Intent intent = new Intent(activity, CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        });
        if (navProfil != null) navProfil.setOnClickListener(v -> {
            if (!activeTab.equals(TAB_PROFILE)) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        });
    }

    public static void updateBadge(Activity activity) {
        View bottomNav = activity.findViewById(R.id.bottomNavContainer);
        if (bottomNav == null) return;
        TextView tvCartBadge  = bottomNav.findViewById(R.id.tvCartBadge);
        if (tvCartBadge != null) {
            int count = CartManager.getInstance().getCartCount();
            if (count > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(count > 9 ? "9+" : String.valueOf(count));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private static void setTabActive(TextView tab, boolean active, Activity activity) {
        if (tab == null) return;
        int color = ContextCompat.getColor(activity, active ? R.color.green : R.color.gray);
        tab.setTextColor(color);
        
        // Update drawable tint programmatically
        Drawable[] drawables = tab.getCompoundDrawablesRelative();
        for (Drawable d : drawables) {
            if (d != null) {
                d.mutate().setTint(color);
            }
        }
    }
}
