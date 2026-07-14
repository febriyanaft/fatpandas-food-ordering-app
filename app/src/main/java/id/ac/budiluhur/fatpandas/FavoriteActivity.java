package id.ac.budiluhur.fatpandas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import id.ac.budiluhur.fatpandas.model.MenuPandas;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorite;
    private TextView tvEmpty;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_favorite);

        cartManager = CartManager.getInstance(this);

        rvFavorite = findViewById(R.id.rvFavorite);
        tvEmpty = findViewById(R.id.tvEmptyFavorite);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        tampilFavorite();

        NavHelper.setup(this, "profil");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartManager.reloadFromDatabase();
        tampilFavorite();
        NavHelper.setup(this, "profil");
    }

    private void tampilFavorite() {
        List<MenuPandas> favorites = cartManager.getFavoriteItems();

        Collections.sort(
                favorites,
                (m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName())
        );

        if (favorites.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFavorite.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvFavorite.setVisibility(View.VISIBLE);

            MenuAdapter adapter = new MenuAdapter(this, favorites);
            rvFavorite.setLayoutManager(new LinearLayoutManager(this));
            rvFavorite.setAdapter(adapter);
        }
    }
}