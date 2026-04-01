package com.example.tif_gr31.utils;

import com.example.tif_gr31.models.Recomendacion;
import java.util.ArrayList;
import java.util.List;

public class
RecomendacionesData {
    public static List<Recomendacion> getRecomendaciones() {
        List<Recomendacion> lista = new ArrayList<>();
        
        // PERDER PESO
        lista.add(new Recomendacion(1, "⚠️", "Consumo muy elevado", 
            "Estás consumiendo mucho más de lo recomendado para tu objetivo. Esto hace que perder peso sea más difícil. Te sugerimos:\n• Reducí las porciones en las comidas principales\n• Elegí alimentos más bajos en calorías\n• Aumentá la actividad física\nRecuerda: pequeños cambios consistentes tienen más impacto que cambios drásticos."));
        
        lista.add(new Recomendacion(2, "📈", "Ajustes necesarios", 
            "Estás un poco por encima de tu objetivo calórico. ¡Estás cerca! Con pequeños ajustes puedes lograrlo:\n• Reduce snacks entre comidas\n• Elige bebidas sin calorías\n• Aumenta el tamaño de las porciones de verduras\nCon consistencia verás cambios en poco tiempo."));
        
        lista.add(new Recomendacion(3, "✅", "¡Excelente progreso!", 
            "¡Excelente! Estás consumiendo exactamente lo que necesitas para tu objetivo. Mantén esta consistencia y verás resultados.\n• Seguí registrando tus comidas\n• Mantén los mismos hábitos que funcionan\n• Celebra los pequeños logros\nTu dedicación está dando frutos."));
        
        lista.add(new Recomendacion(4, "⚠️", "Consumo muy bajo", 
            "Estás consumiendo muy pocas calorías. Esto puede ser contraproducente:\n• Asegúrate de comer lo suficiente para mantener tu metabolismo activo\n• Una restricción extrema puede llevar a carencias nutricionales\n• Come alimentos nutritivos y balanceados\nConsulta con un profesional si tienes dudas sobre tu alimentación."));

        // MANTENER PESO
        lista.add(new Recomendacion(5, "✅", "Mantenimiento ideal", 
            "¡Perfecto! Estás manteniendo tu peso estable. Tu consumo está en el rango ideal:\n• Continuá registrando tus comidas para mantener el equilibrio\n• Intenta variar los alimentos para una dieta más completa\n• Mantén la actividad física\nVas por el camino correcto."));
        
        lista.add(new Recomendacion(6, "📊", "Consumo inconsistente", 
            "Notamos que tu consumo varía mucho entre días. Esto puede afectar tu objetivo:\n• Intenta ser más consistente con tus comidas\n• Planifica tus comidas con anticipación\n• Come en horarios similares cada día\n• Busca un equilibrio que sea sostenible para ti\nLa consistencia es más importante que la perfección."));
        
        lista.add(new Recomendacion(7, "📈", "Ligero exceso", 
            "Estás consumiendo más calorías de lo recomendado para mantener. Podrías empezar a ganar peso:\n• Reduce ligeramente las porciones\n• Reemplaza snacks calóricos por opciones más ligeras\n• Mantén la actividad física regular\nAjusta poco a poco para no sentir cambios drásticos."));

        // GANAR MASA
        lista.add(new Recomendacion(8, "💪", "Superávit óptimo", 
            "¡Excelente! Estás en superávit calórico, perfecto para ganar masa muscular:\n• Asegúrate de hacer entrenamiento de fuerza regularmente\n• Consume suficiente proteína (al menos 1.6g por kg de peso corporal)\n• Mantén la consistencia en comidas y ejercicio\n• Descansa adecuadamente para la recuperación\nCombina nutrición + entrenamiento = resultados garantizados."));
        
        lista.add(new Recomendacion(9, "💪", "Buen camino", 
            "Estás en superávit calórico, lo que es bueno para ganar masa. Podrías optimizar más:\n• Aumenta ligeramente las porciones para estar más en superávit\n• Prioriza alimentos ricos en proteína\n• Mantén un programa de entrenamiento consistente\n• Considera registrar también tu nivel de actividad física\nUn pequeño ajuste puede acelerar tus resultados."));
        
        lista.add(new Recomendacion(10, "⚠️", "Consumo insuficiente", 
            "Estás consumiendo menos calorías de las necesarias para ganar masa muscular:\n• Aumenta las porciones de tus comidas\n• Agrega más alimentos ricos en proteína\n• Incluye snacks nutritivos entre comidas\n• Consume bebidas calóricas (leche, batidos)\nSin superávit calórico, es muy difícil ganar masa. Aumenta tu consumo."));

        // GENERALES
        lista.add(new Recomendacion(11, "📝", "Faltan registros", 
            "Notamos que no registras tus comidas todos los días. La consistencia es importante:\n• Intenta registrar todos tus alimentos, aunque sea de forma aproximada\n• Dedica 2 minutos cada comida para registrar\n• Esto te ayudará a ver patrones y mejorar\nSin datos consistentes, es difícil hacer seguimiento real."));
        
        lista.add(new Recomendacion(12, "👋", "¡Bienvenido a Kcal!", 
            "Para poder darte recomendaciones personalizadas necesitamos más datos:\n• Registra tus comidas durante al menos una semana\n• Sé lo más honesto posible en los registros\n• No hay registros 'malos', solo información valiosa\nEn una semana podremos mostrarte recomendaciones más precisas."));
        
        lista.add(new Recomendacion(13, "⚠️", "Alerta de salud", 
            "Detectamos que estás consumiendo muy pocas calorías:\n• Es recomendable consumir al menos 1200-1500 kcal diarias\n• Una restricción muy severa puede afectar tu metabolismo y salud\n• Si tienes un objetivo específico, consulta con un profesional\nTu salud es lo más importante. Come conscientemente."));
        
        lista.add(new Recomendacion(14, "🎉", "¡Semana perfecta!", 
            "¡Felicidades! Completaste una semana registrando tus comidas y manteniendo tu objetivo:\n• Esto demuestra compromiso y dedicación\n• La consistencia es lo que genera cambios reales\n• Ahora es más fácil, ¡sigue así!\nTe estamos viendo progresar. ¡Sigue adelante!"));
        
        lista.add(new Recomendacion(15, "💧", "Hidratación", 
            "Pequeño recordatorio: No olvides mantenerte hidratado:\n• Bebe al menos 2-3 litros de agua al día\n• El agua ayuda a la digestión y acelera el metabolismo\n• Muchas veces confundimos sed con hambre\n• Agua con limón, té sin azúcar, también cuentan\nUna buena hidratación es parte fundamental de una vida saludable."));

        return lista;
    }
}
