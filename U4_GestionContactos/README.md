# Gestión de Contactos — Unidad 4: Gestión de Dependencias
**Universidad Politécnica Salesiana — Programación de Interfaces**
Autor: Vivar Mateo

---

## ¿Qué cambió respecto a la versión anterior?

Este proyecto es la **migración de `gui_contactos` (unidades anteriores) a Maven**, con las adiciones de la Unidad 4:

| Elemento | Antes (U1–U3) | Ahora (U4) |
|---|---|---|
| Gestión de dependencias | JARs locales en classpath | Maven `pom.xml` |
| Look & Feel | Default Java Swing | **FlatLaf 3.2.5** (tema oscuro) |
| Persistencia | Solo CSV (`datosContactos.csv`) | CSV + **JSON con Gson 2.10.1** |
| Exportar | Solo CSV con SwingWorker | CSV + **JSON con SwingWorker** |
| Importar | No existía | **Importar desde JSON** (reemplazar/combinar) |
| MigLayout | JAR local en carpeta raíz | Maven Central `com.miglayout:11.4.2` |

---

## Estructura del proyecto Maven

```
U4_GestionContactos/
├── pom.xml                          ← Dependencias y build Maven
├── contacts_data/
│   └── contactos_ejemplo.json       ← Archivo de prueba para importar
└── src/main/
    ├── java/
    │   ├── modelo/
    │   │   ├── persona.java          ← Modelo (compatible con Gson)
    │   │   └── personaDAO.java       ← DAO CSV (conservado de U3)
    │   ├── servicio/
    │   │   └── ContactoJsonService.java  ← NUEVO: JSON con Gson
    │   ├── controlador/
    │   │   ├── logica_ventana.java   ← Controlador (+ botones JSON)
    │   │   └── Lenguaje.java         ← Multiidioma (sin cambios)
    │   └── vista/
    │       └── ventana.java          ← UI (+ FlatLaf + botones JSON)
    └── resources/
        ├── idiomas/                  ← Archivos .properties
        └── IMAGENES/                 ← Íconos originales
```

---

## Dependencias auditadas (`pom.xml`)

| Librería | Versión | Propósito | Seguridad |
|---|---|---|---|
| **FlatLaf** | 3.2.5 | Look & Feel moderno Swing | Apache 2.0, sin CVEs, 3.5k ⭐ GitHub |
| **Gson** | 2.10.1 | Serialización JSON | Apache 2.0, Google, CVE-2022-1471 NO aplica |
| **MigLayout** | 11.4.2 | Layout manager (ya era usado) | BSD, misma versión que JARs originales |
| **JUnit 5** | 5.10.0 | Pruebas (solo `test`) | Estándar industria |

**Exclusión de dependencia transitiva:** `error_prone_annotations` de Gson se excluye en el `pom.xml` porque solo es necesaria al compilar el código fuente de Google, no en runtime de nuestra aplicación.

---

## Cómo abrir en NetBeans

1. `File → Open Project` → seleccionar carpeta `U4_GestionContactos/`
2. NetBeans detecta automáticamente que es un proyecto Maven
3. Clic derecho → **Build** (descarga dependencias)
4. Clic derecho → **Run**

---

## Cómo probar la importación JSON

1. Ejecutar la aplicación
2. Ir a la pestaña **"Estadísticas"**
3. Clic en **📥 IMPORTAR DESDE JSON (Gson)**
4. Seleccionar `contacts_data/contactos_ejemplo.json`
5. Elegir "No" para combinar con los existentes

---

## Nombre del fichero de entrega
`Vivar_Mateo_ProgInterfacesG_U4`
