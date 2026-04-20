# Guía de Presentación: App Kcal (TIF Gr31)

Este documento contiene la estructura sugerida, los textos para las diapositivas y los puntos clave para hablar frente a los profesores durante la defensa del proyecto.

---

## Estructura de Diapositivas (Canva)

### Slide 1: Portada
*   **Título:** Kcal - Gestión Nutricional Inteligente
*   **Subtítulo:** Trabajo Integrador Final - Programación en Dispositivos Móviles
*   **Contenido:** 
    *   Nombre de los integrantes.
    *   Logo de la app.
    *   Universidad Tecnológica Nacional (UTN).
*   **Punto clave para hablar:** Saludar, presentar el nombre del proyecto y explicar que Kcal es una solución integral para personas que buscan no solo contar calorías, sino entender su balance nutricional real.

### Slide 2: El Problema y la Solución
*   **Título:** ¿Por qué Kcal?
*   **Contenido:**
    *   **Problema:** La dificultad de llevar un registro manual y la falta de precisión en los datos nutricionales.
    *   **Solución:** Una herramienta móvil persistente que consume datos oficiales y ofrece feedback personalizado.
*   **Punto clave para hablar:** Mencionar que muchas apps son complejas o no son precisas. Kcal simplifica el proceso usando una API internacional (USDA) y cálculos metabólicos probados.

### Slide 3: Stack Tecnológico
*   **Título:** Tecnologías Utilizadas
*   **Contenido:**
    *   **Lenguaje:** Java 11.
    *   **Backend:** Firebase (Authentication & Firestore).
    *   **Red:** Retrofit 2 + GSON.
    *   **Datos:** API de USDA (FoodData Central).
    *   **Diseño:** Google Material Design.
*   **Punto clave para hablar:** Explicar que se eligió Java por su robustez y Firebase por su capacidad de sincronización en tiempo real y persistencia NoSQL.

### Slide 4: Arquitectura del Sistema
*   **Título:** Arquitectura y Patrones
*   **Contenido:**
    *   **Patrón Singleton:** Para la gestión de la instancia de Retrofit (`ApiClient`).
    *   **Modelado NoSQL:** Documentos de usuario con subcolecciones de comidas.
    *   **Estandarización:** Uso de un archivo central de documentación técnica para coherencia del equipo.
*   **Punto clave para hablar:** Destacar cómo organizamos los datos en Firestore para que el historial sea escalable y fácil de consultar por fechas.

### Slide 5: Funcionalidad: Dashboard Principal
*   **Título:** Panel de Control (Dashboard)
*   **Contenido:**
    *   Cálculo dinámico de calorías restantes.
    *   Progreso visual de Macronutrientes (Carbos, Proteínas, Grasas).
    *   Acceso rápido por categorías de comida (Desayuno, Almuerzo, etc.).
*   **Punto clave para hablar:** Mostrar la pantalla de Inicio. Explicar que el dashboard se reinicia cada día y busca los datos de Firestore en tiempo real para dar feedback inmediato al usuario.

### Slide 6: Registro de Comida Avanzado
*   **Título:** Búsqueda y Registro Inteligente
*   **Contenido:**
    *   **Debounce Search:** Optimización de llamadas a la API (espera de 600ms).
    *   **Calculadora Dinámica:** Nutrientes proporcionales al peso ingresado.
    *   **Multi-ingrediente:** Posibilidad de armar una comida con varios elementos antes de guardar.
*   **Punto clave para hablar:** Mencionar el "Debounce" como un detalle técnico de calidad para no saturar el servidor y mejorar la experiencia de usuario (UX).

### Slide 7: Historial y Filtrado
*   **Título:** Historial y Control de Datos
*   **Contenido:**
    *   Visualización cronológica de registros.
    *   **Filtrado por Periodos:** Vista por Día, Semana o Mes.
    *   **Filtro por Calendario:** Selector de fecha específica en español.
*   **Punto clave para hablar:** Explicar el desafío técnico de filtrar datos en Firestore usando `whereGreaterThanOrEqualTo` y cómo implementamos el `DatePickerDialog` para mayor flexibilidad.

### Slide 8: Análisis y Recomendaciones
*   **Título:** El "Cerebro" de la App
*   **Contenido:**
    *   **Estadísticas:** Gráficos de barras dinámicos y distribución porcentual.
    *   **Sistema Experto:** Recomendaciones basadas en el promedio semanal vs. objetivos.
    *   **Algoritmo:** Fórmula de Harris-Benedict para el gasto energético.
*   **Punto clave para hablar:** Este es el valor agregado. La app no solo guarda datos, sino que los analiza y le dice al usuario "vas por buen camino" o "estás consumiendo poco para tu objetivo de ganar músculo".

### Slide 9: Experiencia de Usuario (UX)
*   **Título:** Diseño y Navegación
*   **Contenido:**
    *   **Barra Flotante:** Navegación personalizada tipo "píldora".
    *   **Animaciones:** Transiciones fluidas y feedback visual de botones.
    *   **Accesibilidad:** Textos claros y paleta de colores coherente (Verdes/Neutros).
*   **Punto clave para hablar:** Resaltar el `FloatingNavigationHelper`, que es una solución propia para que la app se sienta moderna y diferente a las plantillas estándar de Android.

### Slide 10: Conclusiones
*   **Título:** Conclusiones y Futuro
*   **Contenido:**
    *   Integración exitosa de múltiples servicios (API, DB, Auth).
    *   Código documentado y mantenible.
    *   Próximos pasos: Integración con sensores (podómetro) y modo offline.
*   **Punto clave para hablar:** Finalizar agradeciendo el tiempo y mencionando qué fue lo que más aprendieron durante el desarrollo del proyecto (ej: manejo de asincronismo con Retrofit).

---

## Consejos para la presentación:
1.  **Demo en vivo:** Si es posible, tengan el celular o el emulador listo para mostrar el registro de una comida real.
2.  **Código fuente:** Estén preparados para mostrar el `ApiClient` o la lógica de filtrado del `HistorialActivity` si preguntan algo técnico.
3.  **Seguridad:** Recuerden mencionar que las contraseñas están protegidas por Firebase Auth.
