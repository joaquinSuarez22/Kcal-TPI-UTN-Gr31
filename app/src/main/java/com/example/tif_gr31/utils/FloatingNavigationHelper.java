package com.example.tif_gr31.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.tif_gr31.R;
import com.example.tif_gr31.activities.EstadisticasActivity;
import com.example.tif_gr31.activities.HistorialActivity;
import com.example.tif_gr31.activities.InicioActivity;
import com.example.tif_gr31.activities.PerfilActivity;

public class FloatingNavigationHelper {

    private static final int ANIM_DURATION = 150;

    public static void setupFloatingNavigation(Activity activity, int currentItemId) {
        
        // Referencias a los botones
        LinearLayout btnInicio = activity.findViewById(R.id.nav_inicio);
        LinearLayout btnHistorial = activity.findViewById(R.id.nav_historial);
        LinearLayout btnEstadisticas = activity.findViewById(R.id.nav_estadisticas);
        LinearLayout btnPerfil = activity.findViewById(R.id.nav_perfil);

        // Establecer el botón activo inicialmente
        setActiveButton(activity, currentItemId);

        // Listeners para cada botón
        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> navigateTo(activity, InicioActivity.class));
        }

        if (btnHistorial != null) {
            btnHistorial.setOnClickListener(v -> navigateTo(activity, HistorialActivity.class));
        }

        if (btnEstadisticas != null) {
            btnEstadisticas.setOnClickListener(v -> navigateTo(activity, EstadisticasActivity.class));
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> navigateTo(activity, PerfilActivity.class));
        }
    }

    private static void navigateTo(Activity activity, Class<?> targetActivity) {
        if (activity.getClass() == targetActivity) {
            return;
        }

        Intent intent = new Intent(activity, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    private static void setActiveButton(Activity activity, int itemId) {
        LinearLayout btnInicio = activity.findViewById(R.id.nav_inicio);
        LinearLayout btnHistorial = activity.findViewById(R.id.nav_historial);
        LinearLayout btnEstadisticas = activity.findViewById(R.id.nav_estadisticas);
        LinearLayout btnPerfil = activity.findViewById(R.id.nav_perfil);

        // Resetear todos
        setButtonInactive(activity, btnInicio);
        setButtonInactive(activity, btnHistorial);
        setButtonInactive(activity, btnEstadisticas);
        setButtonInactive(activity, btnPerfil);

        // Activar el seleccionado
        if (itemId == R.id.nav_inicio) setButtonActive(activity, btnInicio);
        else if (itemId == R.id.nav_historial) setButtonActive(activity, btnHistorial);
        else if (itemId == R.id.nav_estadisticas) setButtonActive(activity, btnEstadisticas);
        else if (itemId == R.id.nav_perfil) setButtonActive(activity, btnPerfil);
    }

    private static void setButtonActive(Activity activity, LinearLayout button) {
        if (button == null) return;

        int colorActive = ContextCompat.getColor(activity, R.color.nav_active);
        int colorBgActive = ContextCompat.getColor(activity, R.color.nav_bg_active);

        // Crear un fondo redondeado para el item activo
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(32 * activity.getResources().getDisplayMetrics().density); // 32dp
        shape.setColor(colorBgActive);
        
        button.setBackground(shape);

        for (int i = 0; i < button.getChildCount(); i++) {
            View child = button.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(colorActive);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(colorActive);
            }
        }

        button.animate().scaleX(1.1f).scaleY(1.1f).setDuration(ANIM_DURATION).start();
    }

    private static void setButtonInactive(Activity activity, LinearLayout button) {
        if (button == null) return;

        int colorInactive = ContextCompat.getColor(activity, R.color.nav_inactive);

        button.setBackground(null);

        for (int i = 0; i < button.getChildCount(); i++) {
            View child = button.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(colorInactive);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(colorInactive);
            }
        }

        button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(ANIM_DURATION).start();
    }
}
