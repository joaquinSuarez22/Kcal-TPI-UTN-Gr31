# Documentación Técnica: Proyecto Kcal (TIF Gr31)

**Descripción General:**
Kcal es una plataforma de seguimiento nutricional inteligente que permite registrar comidas y monitorear el balance de calorías y macronutrientes en tiempo real. Utiliza algoritmos personalizados y una base de datos local para ofrecer estadísticas detalladas y recomendaciones que ayudan al usuario a alcanzar sus objetivos de salud de forma consciente.

---

## 0. Tecnologías y Lenguajes Utilizados
La aplicación se ha desarrollado utilizando un stack tecnológico moderno para Android:

- **Java 11**: Lenguaje principal utilizado para toda la lógica de negocio, controladores de actividades y modelos de datos.
- **XML (Extensible Markup Language)**: Utilizado para la definición de interfaces de usuario (layouts), recursos de diseño, menús y configuración del Manifiesto.
- **JSON (JavaScript Object Notation)**: Formato empleado para el intercambio de datos y para la configuración interna de los servicios de Google (Firebase).
- **Groovy / Kotlin DSL**: Lenguajes utilizados en los scripts de automatización de compilación de **Gradle**.
- **Firebase Firestore**: Base de datos NoSQL orientada a documentos para el almacenamiento en la nube.
- **Firebase Auth**: Servicio de autenticación gestionada.
- **CSV Local**: Base de datos offline (`INCAPTCA2009.csv`) para búsqueda rápida de alimentos en español.

---

## 1. Identidad Visual y Diseño (Branding)
El estilo visual de Kcal combina minimalismo moderno con energía orgánica para transmitir salud y bienestar.

### Paleta de Colores
- **Verde Principal (`#1C7C54`)**: Salud, naturaleza y equilibrio. Usado en botones y elementos activos.
- **Verde Claro (`#DCFCE7`)**: Fondos de acento y estados suaves.
- **Fondo General (`#DEF4C6`)**: Identidad visual fresca y limpia.
- **Texto y Secundario (`#334155`)**: Gris azulado para máxima legibilidad y elegancia profesional.
- **Acento (`#73E2A7`)**: Detalles visuales vibrantes.

### Concepto de Diseño (Mockups)
Para la presentación visual y materiales de marketing, se adopta un estilo híbrido:
- **Clean Mockup + Editorial**: Fondos minimalistas (blanco/verde lima) con tipografía bold y títulos directos.
- **Connected Story**: Las capturas de pantalla están hiladas por elementos gráficos (líneas o texto) que fluyen entre una diapositiva y otra.
- **Dynamic Stack**: Uso de capas y sombras para dar profundidad a las capturas de la aplicación, mezclando mockups de teléfonos reales con ilustraciones planas.

---

## 2. Arquitectura de Red y Almacenamiento
La aplicación prioriza la disponibilidad de datos y la velocidad de respuesta.

- **Búsqueda Híbrida/Local**: Implementada en `CsvFoodHelper.java`. Utiliza el archivo `assets/INCAPTCA2009.csv` como fuente principal de datos. Esto permite búsquedas instantáneas en español y funcionamiento sin conexión a internet.
- **Persistencia en la Nube**: Los registros de comidas se sincronizan con **Cloud Firestore** bajo la estructura de subcolecciones `usuarios/{uid}/comidas/`, permitiendo que el historial sea accesible desde cualquier dispositivo.
- **Consumo de API (Opcional)**: El código cuenta con la integración de **Retrofit 2** para la API de USDA (FoodData Central), permitiendo expandir la base de datos a futuro.

---

## 3. Actividades y Lógica de Interfaz (`com.example.tif_gr31.activities`)

### Flujo de Acceso y Sesión
- **`MainActivity.java`**: Enrutador lógico basado en el estado de Firebase Auth.
- **`LoginActivity.java`**: Autenticación gestionada.
- **`RegistroActivity.java`**: Creación de cuenta e inicialización de perfil NoSQL.
- **`RecuperarActivityActivity.java`**: Flujo de restablecimiento de contraseña vía email.

### Funcionalidades Principales
- **`InicioActivity.java` (Dashboard)**: Resumen diario con cálculo de calorías restantes y progreso de macronutrientes (Reparto 50/20/30).
- **`RegistrarComidaActivity.java`**: Búsqueda optimizada (300ms debounce), calculadora de peso dinámica y soporte para comidas con múltiples ingredientes.
- **`HistorialActivity.java`**: Listado cronológico con sistema de **filtrado inteligente** (Día, Semana, Mes) y selector de **fecha específica** (Calendario en español).
- **`EstadisticasActivity.java`**: Visualización de datos mediante gráficos de barras dinámicos generados por código.
- **`RecomendacionesActivity.java`**: Sistema experto que analiza el promedio semanal vs. el objetivo calculado.
- **`PerfilActivity.java`**: Implementación del algoritmo de **Harris-Benedict** para el cálculo de la TMB según datos biométricos.

---

## 4. Modelos de Datos (`com.example.tif_gr31.models`)
- **`Usuario.java`**: Mapeo del perfil y cuenta.
- **`Comida.java`**: Estructura de ingesta.
- **`Recomendacion.java`**: Contenido estático del sistema experto.

---

## 5. Utilidades (`com.example.tif_gr31.utils`)
- **`FloatingNavigationHelper.java`**: Gestión de la barra de navegación "píldora" con animaciones y transiciones fluidas.
- **`CsvFoodHelper.java`**: Lógica de lectura y búsqueda en el archivo de activos locales.
