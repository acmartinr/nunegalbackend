# 🧩 Matchly — Proyecto Técnico (Spring Boot + Maven)

Este proyecto fue desarrollado con fines **técnicos y evaluativos**, como parte de un **test técnico** para demostrar
buenas prácticas de desarrollo backend con **Spring Boot** y **programación reactiva**.
---

## 🚀 Descripción General

**Matchly** es una aplicación backend basada en **Spring Boot (Java)** que implementa un servicio para obtener productos
similares desde un servicio externo.

El proyecto usa **WebFlux**, **Resilience4j** y **Reactor** para manejar peticiones asíncronas, tolerancia a fallos y
composición reactiva de datos.

---

## ⚙️ Tecnologías principales

| Tecnología / Librería | Propósito |
|------------------------|------------|
| **Java 17+** | Lenguaje base del proyecto |
| **Spring Boot** | Framework principal para desarrollo del backend |
| **Spring WebFlux** | Programación reactiva y manejo no bloqueante de peticiones HTTP |
| **Resilience4j** | Circuit breakers, time limiters y tolerancia a fallos |
| **Maven** | Sistema de build y gestión de dependencias |
| **Reactor (Mono / Flux)** | Flujo reactivo de datos |
| **SLF4J + Logback** | Logging y trazabilidad |
| **Jakarta Validation** | Validación de parámetros de entrada |
| **JUnit / Mockito** | Testing unitario y de integración |

### Guía rápida para ejecutar el proyecto con Maven Wrapper (`mvnw`)

Este proyecto está **configurado para poder usar Maven Wrapper**, por lo que **no necesitas tener Maven instalado
globalmente**.  
Solo asegúrate de tener **Java JDK 17 o superior** y seguir los pasos siguientes.

---

### 🧩 1️⃣ Instalar dependencias y compilar el proyecto

Ejecuta en la raíz del proyecto:

```bash
.\mvnw clean install
```

📦 2️⃣ Resolver dependencias

```bash
.\mvnw dependency:resolve
```

▶️ 3️⃣ Ejecutar la aplicación

```bash
.\mvnw spring-boot:run
```

Este comando levanta la aplicación Spring Boot con la configuración por defecto.

Una vez iniciada, podrás ejecutar las llamadas de los endpoints en:

👉 http://localhost:5000

---