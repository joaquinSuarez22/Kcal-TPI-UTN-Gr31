package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class EstadisticasActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView btnShare;
    private MaterialButton btnRecomendaciones;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnRecomendaciones = findViewById(R.id.btnRecomendaciones);
        bottomNav = findViewById(R.id.bottomNav);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (bottomNav != null) {
            NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.menu_estadisticas);
        }
    }
}
