package com.example.tif_gr31.utils;

import android.app.Activity;
import android.content.Intent;
import com.example.tif_gr31.R;
import com.example.tif_gr31.activities.EstadisticasActivity;
import com.example.tif_gr31.activities.HistorialActivity;
import com.example.tif_gr31.activities.InicioActivity;
import com.example.tif_gr31.activities.PerfilActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Clase de utilidad para la gestión de la navegación estándar.
 * Proporciona métodos para configurar el comportamiento de los componentes de navegación
 * de Material Design (BottomNavigationView).
 */
public class NavigationHelper {

    /**
     * Configura un BottomNavigationView con los listeners necesarios para la navegación entre actividades.
     * 
     * @param activity      Actividad actual desde la que se navega.
     * @param bottomNav     Instancia del BottomNavigationView a configurar.
     * @param currentItemId ID del ítem que debe aparecer seleccionado inicialmente.
     */
    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNav, int currentItemId) {
        // Marca el ítem actual como seleccionado
        bottomNav.setSelectedItemId(currentItemId);

        // Define la lógica de clic para cada ítem del menú
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            // Si el usuario toca el ítem de la pantalla donde ya está, no hace nada
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
                // Trae la actividad al frente si ya existe en la pila, evitando recrearla
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                
                // Desactiva la animación por defecto para una transición más limpia
                activity.overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
