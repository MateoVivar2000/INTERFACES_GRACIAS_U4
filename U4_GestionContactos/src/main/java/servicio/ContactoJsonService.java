package servicio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import modelo.persona;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de Serialización/Deserialización JSON — NUEVO en Unidad 4.
 *
 * Usa la dependencia Gson (com.google.code.gson:2.10.1) declarada en pom.xml.
 * Permite importar y exportar la lista de contactos en formato JSON estándar,
 * complementando (sin reemplazar) la persistencia CSV del personaDAO original.
 *
 * FUNCIONALIDADES:
 *  - exportarJSON()   → Serializa List<persona> → archivo .json
 *  - importarJSON()   → Deserializa archivo .json → List<persona>
 *  - guardarLocal()   → Persistencia automática en contacts_data/contactos.json
 *  - cargarLocal()    → Carga desde archivo local al arrancar la app
 */
public class ContactoJsonService {

    // Instancia Gson con pretty printing para que el JSON sea legible por humanos
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()       // Indentación automática en el JSON
            .disableHtmlEscaping()     // Evita escapar @ en emails → a@b.com legible
            .serializeNulls()          // Incluye campos null para estructura completa
            .create();

    // Tipo genérico necesario para que Gson deserialice List<persona> correctamente
    private static final Type TIPO_LISTA_PERSONA = new TypeToken<List<persona>>() {}.getType();

    // Archivo de persistencia JSON local (paralelo al CSV de personaDAO)
    private static final String ARCHIVO_JSON_LOCAL = "contacts_data/contactos.json";

    // ── EXPORTAR: Serializar List<persona> → archivo JSON ────────────────────
    /**
     * Exporta la lista completa de contactos al archivo JSON indicado.
     * @param contactos  Lista de contactos a exportar
     * @param rutaArchivo Ruta destino del archivo .json
     * @throws IOException si no se puede escribir el archivo
     */
    public void exportarJSON(List<persona> contactos, String rutaArchivo) throws IOException {
        File destino = new File(rutaArchivo);
        // Crear directorios padre si no existen
        if (destino.getParentFile() != null) {
            destino.getParentFile().mkdirs();
        }
        // Gson serializa la lista completa a JSON con formato legible
        try (Writer writer = new FileWriter(destino)) {
            GSON.toJson(contactos, writer);
        }
    }

    // ── IMPORTAR: Deserializar archivo JSON → List<persona> ──────────────────
    /**
     * Importa contactos desde un archivo JSON externo.
     * El archivo debe tener el mismo formato que genera exportarJSON().
     * @param rutaArchivo Ruta del archivo .json a importar
     * @return Lista de contactos deserializados
     * @throws IOException si el archivo no existe o el formato es inválido
     */
    public List<persona> importarJSON(String rutaArchivo) throws IOException {
        File fuente = new File(rutaArchivo);
        if (!fuente.exists()) {
            throw new FileNotFoundException("Archivo JSON no encontrado: " + rutaArchivo);
        }
        // Gson deserializa el JSON → List<persona> usando el TypeToken
        try (Reader reader = new FileReader(fuente)) {
            List<persona> resultado = GSON.fromJson(reader, TIPO_LISTA_PERSONA);
            return (resultado != null) ? resultado : new ArrayList<>();
        }
    }

    // ── PERSISTENCIA LOCAL ────────────────────────────────────────────────────
    /**
     * Guarda automáticamente en el archivo JSON local.
     * Se llama al cerrar la ventana y después de cada modificación.
     */
    public void guardarLocal(List<persona> contactos) {
        try {
            exportarJSON(contactos, ARCHIVO_JSON_LOCAL);
        } catch (IOException e) {
            System.err.println("[ContactoJsonService] Error al guardar JSON local: " + e.getMessage());
        }
    }

    /**
     * Carga desde el archivo JSON local al iniciar la aplicación.
     * Retorna lista vacía si el archivo no existe (primera ejecución).
     */
    public List<persona> cargarLocal() {
        try {
            return importarJSON(ARCHIVO_JSON_LOCAL);
        } catch (IOException e) {
            // Normal en la primera ejecución, no es un error crítico
            return new ArrayList<>();
        }
    }
}
