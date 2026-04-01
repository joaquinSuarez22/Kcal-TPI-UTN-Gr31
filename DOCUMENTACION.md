# Documentación Técnica: Proyecto Kcal (TIF Gr31)

Este documento proporciona una visión detallada de la arquitectura, componentes y lógica de negocio de la aplicación **Kcal**. La aplicación está diseñada para el seguimiento nutricional personalizado, utilizando **Firebase** para el almacenamiento y la **API de USDA** para la obtención de datos alimenticios.

---

## 0. Tecnologías y Lenguajes Utilizados
La aplicación se ha desarrollado utilizando un stack tecnológico moderno para Android:

- **Java 11**: Lenguaje principal utilizado para toda la lógica de negocio, controladores de actividades y modelos de datos.
- **XML (Extensible Markup Language)**: Utilizado para la definición de interfaces de usuario (layouts), recursos de diseño, menús y configuración del Manifiesto.
- **JSON (JavaScript Object Notation)**: Formato empleado para el intercambio de datos con la API de USDA y para la configuración interna de los servicios de Google (Firebase).
- **Groovy / Kotlin DSL**: Lenguajes utilizados en los scripts de automatización de compilación de **Gradle**.
- **Firebase Firestore**: Base de datos NoSQL orientada a documentos para el almacenamiento en la nube.
- **Firebase Auth**: Servicio de autenticación gestionada.

---

## 1. Arquitectura de Red y API (`com.example.tif_gr31.api`)
La comunicación con servicios externos se gestiona mediante **Retrofit 2**.

- **`ApiClient.java`**: Implementa el patrón **Singleton** para garantizar una única instancia de Retrofit. Configura un `OkHttpClient` con un `HttpLoggingInterceptor` para depuración en desarrollo y utiliza `GsonConverterFactory` para el mapeo automático de JSON a objetos Java.
- **`FoodApiService.java`**: Define el endpoint `foods/search`. Utiliza anotaciones de Retrofit para pasar la `api_key` y el término de búsqueda (`query`) como parámetros de consulta.
- **`FoodProduct.java`**: Clase de mapeo que procesa la respuesta de USDA. Incluye lógica personalizada para navegar por la estructura de nutrientes y extraer valores de Energía (Kcal), Proteínas, Carbohidratos y Grasas.
- **`FoodSearchResponse.java`**: Contenedor de nivel superior para los resultados de la búsqueda.

---

## 2. Actividades y Lógica de Interfaz (`com.example.tif_gr31.activities`)

### Flujo de Acceso y Sesión
- **`MainActivity.java`**: Actividad de entrada que actúa como enrutador lógico. Verifica la existencia de una sesión activa mediante `mAuth.getCurrentUser()`.
- **`LoginActivity.java`**: Gestiona la autenticación de usuarios existentes mediante Firebase Auth.
- **`RegistroActivity.java`**: Crea nuevos usuarios y, simultáneamente, inicializa un documento en la colección `usuarios` de Firestore con una estructura de perfil predeterminada.
- **`RecuperarActivityActivity.java`**: Implementa el flujo de "olvidé mi contraseña" enviando correos electrónicos automáticos desde Firebase.
- **`CerrarSesionActivity.java`**: Pantalla de confirmación que utiliza `FLAG_ACTIVITY_CLEAR_TASK` para limpiar la pila de navegación al salir.

### Funcionalidades Principales
- **`InicioActivity.java` (Dashboard)**: 
    - Calcula en tiempo real las calorías restantes del día.
    - Distribuye el progreso de macronutrientes en base a un reparto calórico estándar (50% Carbos, 20% Proteínas, 30% Grasas).
    - Utiliza `<include>` para reutilizar tarjetas de categorías de comida.
- **`RegistrarComidaActivity.java`**: 
    - Implementa una búsqueda con **Debounce** (600ms) para optimizar el uso de la API.
    - Permite calcular valores nutricionales proporcionales al peso (gramos) ingresado.
    - Soporta el registro de múltiples ingredientes en una sola comida mediante un `ArrayList` de Maps guardado en Firestore.
- **`HistorialActivity.java`**: Muestra una lista cronológica inversa de comidas. Implementa un patrón de "Estado Vacío" si no hay registros.
- **`EstadisticasActivity.java`**: 
    - **Gráfico Dinámico**: Genera visualmente barras de progreso para calorías diarias mediante la manipulación dinámica de vistas.
    - **Análisis de Macros**: Calcula la distribución porcentual real basada en la ingesta total del periodo seleccionado (Día, Semana o Mes).
- **`RecomendacionesActivity.java`**: 
    - **Sistema Experto**: Analiza el promedio de consumo de los últimos 7 días.
    - Compara el consumo real contra el objetivo teórico calculado para determinar excesos o déficits.
- **`PerfilActivity.java`**: 
    - **Fórmula de Harris-Benedict**: Algoritmo utilizado para calcular la Tasa Metabólica Basal (TMB).
    - Aplica factores de actividad física y ajustes por objetivo (Pérdida de peso: -500 kcal, Ganancia muscular: +500 kcal).

---

## 3. Modelos de Datos (`com.example.tif_gr31.models`)
Diseñados para ser compatibles con la serialización de Firestore.

- **`Usuario.java`**: Mapea los datos básicos de la cuenta y vinculación.
- **`Comida.java`**: Estructura base para el intercambio de información de ingesta.
- **`Recomendacion.java`**: Define el contenido estático de los consejos nutricionales.

---

## 4. Utilidades (`com.example.tif_gr31.utils`)
- **`FloatingNavigationHelper.java`**: Lógica personalizada para la barra de navegación tipo "píldora". Gestiona animaciones de escala (`animate().scaleX(1.1f)`) y cambios de color dinámicos para el ítem seleccionado.
- **`RecomendacionesData.java`**: Repositorio centralizado de conocimientos nutricionales para el sistema de sugerencias.

---

## 5. Configuración y Recursos
- **Seguridad**: Se requiere el permiso `android.permission.INTERNET` declarado en el `AndroidManifest.xml`.
- **Base de Datos**: Integración con **Firebase Firestore** mediante el plugin de Google Services.
- **Diseño**: Basado en **Material Components**, utilizando `CardView` para contenedores y `CircularProgressIndicator` para el feedback visual de objetivos.
- **Build System**: Configurado para **Java 11**, gestionando dependencias mediante Gradle.
