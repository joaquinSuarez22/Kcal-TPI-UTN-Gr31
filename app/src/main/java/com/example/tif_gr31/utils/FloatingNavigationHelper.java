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

/**
 * Ayudante para gestionar la barra de navegación flotante personalizada.
 * Maneja la lógica de navegación entre actividades principales, resaltado visual
 * del ítem activo y animaciones de transición.
 */
public class FloatingNavigationHelper {

    // Duración de la animación de escala al interactuar con los botones
    private static final int ANIM_DURATION = 150;

    /**
     * Configura la barra de navegación en la actividad proporcionada.
     * Busca los botones por ID y asigna los listeners de clic para navegar.
     * 
     * @param activity      La actividad donde se encuentra la barra.
     * @param currentItemId El ID del recurso (R.id.nav_...) que debe aparecer como activo.
     */
    public static void setupFloatingNavigation(Activity activity, int currentItemId) {
        
        // Referencias a los contenedores de los botones del menú
        LinearLayout btnInicio = activity.findViewById(R.id.nav_inicio);
        LinearLayout btnHistorial = activity.findViewById(R.id.nav_historial);
        LinearLayout btnEstadisticas = activity.findViewById(R.id.nav_estadisticas);
        LinearLayout btnPerfil = activity.findViewById(R.id.nav_perfil);

        // Establecer visualmente qué botón es el activo actualmente
        setActiveButton(activity, currentItemId);

        // Asignación de acciones de navegación
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

    /**
     * Realiza la transición a la actividad destino.
     * Utiliza flags para evitar crear múltiples instancias de la misma actividad
     * y elimina las animaciones de transición por defecto de Android para una sensación más fluida.
     */
    private static void navigateTo(Activity activity, Class<?> targetActivity) {
        // Evita recargar la actividad si ya estamos en ella
        if (activity.getClass() == targetActivity) {
            return;
        }

        Intent intent = new Intent(activity, targetActivity);
        // REORDER_TO_FRONT recupera la instancia existente si está en la pila
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        
        // Elimina el parpadeo de transición entre actividades
        activity.overridePendingTransition(0, 0);
    }

    /**
     * Gestiona el estado visual de todos los botones de la barra.
     */
    private static void setActiveButton(Activity activity, int itemId) {
        LinearLayout btnInicio = activity.findViewById(R.id.nav_inicio);
        LinearLayout btnHistorial = activity.findViewById(R.id.nav_historial);
        LinearLayout btnEstadisticas = activity.findViewById(R.id.nav_estadisticas);
        LinearLayout btnPerfil = activity.findViewById(R.id.nav_perfil);

        // Primero reseteamos todos los botones al estado inactivo
        setButtonInactive(activity, btnInicio);
        setButtonInactive(activity, btnHistorial);
        setButtonInactive(activity, btnEstadisticas);
        setButtonInactive(activity, btnPerfil);

        // Luego activamos solo el que corresponde a la pantalla actual
        if (itemId == R.id.nav_inicio) setButtonActive(activity, btnInicio);
        else if (itemId == R.id.nav_historial) setButtonActive(activity, btnHistorial);
        else if (itemId == R.id.nav_estadisticas) setButtonActive(activity, btnEstadisticas);
        else if (itemId == R.id.nav_perfil) setButtonActive(activity, btnPerfil);
    }

    /**
     * Aplica el estilo visual de "Botón Activo":
     * - Fondo redondeado con color destacado.
     * - Cambio de color en icono y texto.
     * - Pequeña animación de escala (agrandamiento).
     */
    private static void setButtonActive(Activity activity, LinearLayout button) {
        if (button == null) return;

        int colorActive = ContextCompat.getColor(activity, R.color.nav_active);
        int colorBgActive = ContextCompat.getColor(activity, R.color.nav_bg_active);

        // Crear dinámicamente el fondo redondeado (píldora)
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(32 * activity.getResources().getDisplayMetrics().density); // 32dp de radio
        shape.setColor(colorBgActive);
        
        button.setBackground(shape);

        // Cambia el color de todos los hijos (ImageView y TextView)
        for (int i = 0; i < button.getChildCount(); i++) {
            View child = button.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(colorActive);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(colorActive);
            }
        }

        // Animación de escala para resaltar
        button.animate().scaleX(1.1f).scaleY(1.1f).setDuration(ANIM_DURATION).start();
    }

    /**
     * Aplica el estilo visual de "Botón Inactivo":
     * - Quita el fondo.
     * - Colores grises/neutros para icono y texto.
     * - Retorna a la escala original (1.0).
     */
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
