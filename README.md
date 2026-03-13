# Kcal - TPI UTN GR31

Kcal es una aplicación Android desarrollada como **Trabajo Práctico Integrador (TPI)** para la **Universidad Tecnológica Nacional (UTN)**.

La aplicación permite registrar comidas diarias y calcular automáticamente **calorías y macronutrientes** a partir de los ingredientes consumidos. El objetivo es ayudar al usuario a llevar un **seguimiento simple de su alimentación y progreso nutricional**.

---

# Objetivo del proyecto

El objetivo de la aplicación es permitir que un usuario pueda:

* Registrar los alimentos que consume diariamente
* Calcular automáticamente calorías y macronutrientes
* Consultar su historial de comidas
* Visualizar estadísticas nutricionales
* Mantener un seguimiento simple de su alimentación

---

# Funcionamiento de la app

La app funciona registrando **ingredientes o alimentos consumidos por el usuario**.

Por ejemplo:

El usuario puede registrar que consumió:

* 200g de pollo
* 300g de puré de papa

A partir de esos datos, el sistema calcula automáticamente:

* Calorías
* Proteínas
* Carbohidratos
* Grasas

Esto se logra utilizando una **base de datos o API de alimentos**, que contiene información nutricional de cada ingrediente.

---

# Pantallas de la aplicación

Actualmente la aplicación está organizada en varias pantallas principales.

## Inicio

Pantalla principal de la aplicación.

Funciones:

* Mostrar progreso diario de calorías
* Acceso rápido a las demás secciones
* Navegación principal de la app

## Diario / Historial

Permite ver las comidas registradas durante el día o días anteriores.

Funciones:

* Ver lista de comidas registradas
* Consultar consumo diario
* Revisar registros anteriores

## Registrar comida

Pantalla donde el usuario puede registrar lo que comió.

Funciones:

* Buscar alimento o ingrediente
* Ingresar cantidad en gramos
* Calcular calorías automáticamente
* Guardar registro

## Estadísticas

Permite visualizar el progreso nutricional del usuario.

Funciones:

* Consumo diario de calorías
* Distribución de macronutrientes
* Seguimiento del progreso

## Ajustes / Perfil

Configuración del usuario.

Funciones:

* Datos del usuario
* Configuración de objetivos
* Opciones de la aplicación

---

# Navegación de la aplicación

La app utiliza un **menú inferior reutilizable** presente en todas las pantallas principales.

Opciones del menú:

* Inicio
* Diario
* Estadísticas
* Ajustes

Esto permite navegar rápidamente entre las distintas secciones de la app.

---

# Diseño de la aplicación

Los diseños de las pantallas fueron prototipados utilizando **Stitch** antes de ser implementados en Android Studio.

Paleta de colores utilizada en la aplicación:

Primary
Forest Jade
#1C7C54

Secondary
Deep Evergreen
#1B512D

Accent
Mint Bloom
#73E2A7

Background
Soft Pistachio
#DEF4C6

Estos colores fueron definidos en el archivo:

res/values/colors.xml

---

# Arquitectura del proyecto

El proyecto está organizado utilizando una estructura de paquetes para mantener el código ordenado.

Estructura principal:

app/src/main/java/com/example/tif_gr31

packages:

activities
Contiene las pantallas de la aplicación.

models
Contiene las clases de datos del sistema.

database
Clases relacionadas con almacenamiento de datos.

api
Clases para conexión con APIs externas de alimentos.

utils
Funciones auxiliares utilizadas por la aplicación.

Ejemplo de estructura:

com.example.tif_gr31
├── activities
├── models
├── database
├── api
└── utils

---

# Modelos principales

Actualmente el sistema utiliza los siguientes modelos:

Usuario
Representa a un usuario de la aplicación.

Comida
Representa un alimento o ingrediente consumido.

Estas clases contienen información como:

* nombre
* calorías
* proteínas
* otros macronutrientes

---

# Tecnologías utilizadas

Android Studio
Java
Gradle
ConstraintLayout
Material Design

La aplicación está desarrollada utilizando **Android nativo con Java**.

---

# Uso de GitHub

El proyecto utiliza GitHub para control de versiones.

Repositorio:
Kcal-TPI-UTN-Gr31

---

# Archivos que se suben al repositorio

Los archivos necesarios para ejecutar el proyecto son:

app
gradle
gradlew
gradlew.bat
build.gradle
settings.gradle
gradle.properties
.gitignore
README.md

Estos archivos contienen el código fuente y la configuración necesaria para compilar la aplicación.

---

# Archivos que NO se suben al repositorio

Existen archivos que se generan automáticamente en cada computadora y **no deben subirse al repositorio**.

Estos incluyen:

.gradle
build
app/build
.idea
local.properties

Estos archivos son generados automáticamente por **Android Studio y Gradle** cuando se abre el proyecto.

---

# Cómo ejecutar el proyecto

1. Clonar el repositorio

git clone https://github.com/usuario/Kcal-TPI-UTN-Gr31

2. Abrir el proyecto en Android Studio

3. Ejecutar Gradle Sync

4. Ejecutar la aplicación en un emulador o dispositivo Android

Android Studio generará automáticamente los archivos necesarios para compilar la aplicación.

---

# Estado actual del proyecto

El proyecto se encuentra actualmente en desarrollo.

En esta etapa se están implementando:

* Diseño de las pantallas
* Navegación entre Activities
* Registro de comidas
* Integración con base de datos o API de alimentos

---

# Autores

Proyecto desarrollado por el grupo GR31 para la UTN.
