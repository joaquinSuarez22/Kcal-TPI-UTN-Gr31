# Guía de Presentación: App Kcal (TIF Gr31) - Versión Final

Esta es la estructura optimizada para tu presentación en Canva. He reorganizado el orden para que la explicación fluya de forma lógica (Problema -> Solución -> Implementación -> Demostración técnica) e incluido consejos visuales para tus mockups.

---

## Estructura Sugerida de Diapositivas

### 1. Carátula
*   **Título:** Kcal - Gestión Nutricional Inteligente.
*   **Subtítulo:** Trabajo Integrador Final - Programación en Dispositivos Móviles.
*   **Visual:** Logo de la app y logo de la UTN.
*   **Hablar:** Presentación formal y bienvenida.

### 2. Integrantes
*   **Contenido:** Nombres de los integrantes.
*   **Visual:** Iconos representativos o foto grupal.

### 3. La Problemática Actual
*   **Título:** El desafío de la nutrición consciente.
*   **Contenido:** 
    *   Dificultad para llevar un registro constante.
    *   Falta de datos locales (la mayoría de las apps están en inglés).
    *   Barreras por falta de conexión a internet constante.
*   **Hablar:** "Mucha gente abandona sus dietas porque registrar lo que comen es difícil, está en otro idioma o las apps no funcionan sin internet."

### 4. Nuestra Solución: Kcal
*   **Título:** Kcal: Tu aliado nutricional.
*   **Contenido:** Una app móvil simple, accesible y con base de datos local (offline) optimizada para nuestra región.
*   **Hablar:** Definir la app como un ecosistema que automatiza el seguimiento y funciona en cualquier momento.

### 5. Objetivos del Proyecto
*   **Contenido:**
    *   Proveer una herramienta offline para búsqueda de alimentos.
    *   Automatizar el cálculo de requerimientos energéticos.
    *   Ofrecer feedback inteligente mediante un sistema experto.

### 6. Características Principales (Píldoras)
*   **Contenido:** 
    1. Aplicación móvil simple y accesible 📱
    2. Cálculo automático de calorías 🔢
    3. Control diario de la alimentación 🥗
    4. Registro de comidas fácil y rápido ⚡
    5. Recomendaciones para mejorar hábitos 💡
    6. Sincronización y Seguridad ☁️
*   **Hablar:** Recorrer los 6 pilares que hacen a la app robusta y completa.

### 7. Plataforma y Stack Tecnológico
*   **Título:** El "Motor" de Kcal.
*   **Contenido:** 
    *   **Lenguaje:** Java 11.
    *   **Base de Datos:** Firebase Firestore (Cloud) + CSV Local (Offline).
    *   **Arquitectura:** Singleton, RecyclerViews y Gráficos Dinámicos.
*   **Hablar:** Destacar que usamos un enfoque híbrido de datos para mayor velocidad.

### 8. Creación de Perfil y Objetivos (Pantalla Perfil)
*   **Título:** Personalización Metabólica.
*   **Contenido:** Algoritmo **Harris-Benedict**.
*   **Visual:** Mockup de `PerfilActivity`.
*   **Hablar:** "Calculamos la TMB del usuario y ajustamos sus calorías según su objetivo (Bajar peso, Mantener o Ganar músculo)."

### 9. Dashboard e Inicio (Pantalla de Inicio)
*   **Título:** Control en tiempo real.
*   **Contenido:** Calorías restantes y balance de Macronutrientes.
*   **Visual:** Mockup de `InicioActivity` con los círculos de progreso.

### 10. Registro Híbrido y Base de Datos CSV (Pantalla Registrar Comida)
*   **Título:** Registro instantáneo en español.
*   **Contenido:** 
    *   Base de datos local **INCAPTCA2009**.
    *   Búsqueda offline de alimentos.
    *   Cálculo dinámico por gramos.
*   **Visual:** Mockup de la búsqueda con resultados inmediatos.
*   **Hablar:** "Priorizamos la velocidad: la búsqueda es local, en español y extremadamente rápida."

### 11. Historial y Calendario Inteligente
*   **Título:** Tu historial bajo control.
*   **Contenido:** 
    *   Filtros rápidos (Día, Semana, Mes).
    *   Búsqueda por calendario (DatePicker en español).
*   **Visual:** Mockup de `HistorialActivity` con el selector de fecha.
*   **Hablar:** "El usuario puede auditar cualquier día pasado con solo dos toques."

### 12. Visualización y Recomendaciones (Estadísticas)
*   **Título:** Análisis y Sistema Experto.
*   **Contenido:** 
    *   Gráficos de evolución diaria por código.
    *   Consejos personalizados basados en promedios semanales.
*   **Visual:** Mockup de la pantalla de Estadísticas.
*   **Hablar:** "La app analiza la tendencia y le da al usuario consejos reales: 'vas bien', 'comé más proteína', etc."

### 13. Conclusiones y Q&A
*   **Título:** ¡Muchas gracias!
*   **Contenido:** Espacio para preguntas y demo en vivo.

---

## Guía de Estilo Visual para Mockups (Canva)

Para que tu presentación impacte, usa estos estilos en tus imágenes:

1.  **Connected Story (Slides 3-6):** Usa 3 teléfonos inclinados con una línea verde (`#1C7C54`) que fluya entre ellos, conectando las pantallas.
2.  **Clean Mockup (Slides 8-11):** Fondo blanco puro, el teléfono en el centro y títulos en **Bold** arriba. Muy minimalista.
3.  **Dynamic Stack (Slide 12):** Varias pantallas "flotando" una encima de otra con sombras suaves para mostrar la profundidad de los datos.

**Colores Clave:**
*   **Verde Principal:** `#1C7C54` (Botones e Iconos)
*   **Verde Fondo:** `#DEF4C6` (Fondos de Slides)
*   **Gris Texto:** `#334155` (Lectura)
