package com.example.tif_gr31.utils;

import android.app.Activity;
import android.content.Intent;
import com.example.tif_gr31.R;
import com.example.tif_gr31.activities.EstadisticasActivity;
import com.example.tif_gr31.activities.HistorialActivity;
import com.example.tif_gr31.activities.InicioActivity;
import com.example.tif_gr31.activities.PerfilActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {

    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNav, int currentItemId) {
        bottomNav.setSelectedItemId(currentItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentItemId) return true;

            Intent intent = null;
            if (itemId == R.id.menu_inicio) {
                intent = new Intent(activity, InicioActivity.class);
            } else if (itemId == R.id.menu_historial) {
                intent = new Intent(activity, HistorialActivity.class);
            } else if (itemId == R.id.menu_estadisticas) {
                intent = new Intent(activity, EstadisticasActivity.class);
            } else if (itemId == R.id.menu_perfil) {
                intent = new Intent(activity, PerfilActivity.class);
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
