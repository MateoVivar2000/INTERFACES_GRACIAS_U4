package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Contactos — persistencia en archivo CSV.
 *
 * UNIDAD 4 — SIN CAMBIOS FUNCIONALES.
 * Esta clase se mantiene igual a la versión anterior para conservar
 * compatibilidad con los datos CSV existentes. El nuevo manejo de JSON
 * se delega al servicio ContactoJsonService (ver paquete servicio/).
 *
 * Única adaptación: la ruta del archivo usa la carpeta "contacts_data/"
 * relativa al directorio de ejecución del proyecto Maven, en lugar de
 * la ruta absoluta "c:/gestionContactos" que era dependiente del sistema.
 */
public class personaDAO {

    private File archivo;
    private persona persona;

    // PUNTO 5: lock estático compartido entre instancias (igual que antes)
    private static final Object lock = new Object();

    // Carpeta de datos relativa al directorio de trabajo (portable entre OS)
    private static final String CARPETA_DATOS = "contacts_data";

    public personaDAO(persona persona) {
        this.persona = persona;
        archivo = new File(CARPETA_DATOS);
        prepararArchivo();
    }

    private void prepararArchivo() {
        if (!archivo.exists()) {
            archivo.mkdir();
        }
        archivo = new File(archivo.getAbsolutePath(), "datosContactos.csv");
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
                escribir("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // PUNTO 3 y 5: escritura sincronizada (igual que antes)
    public void escribirArchivo() {
        synchronized (lock) {
            escribir(persona.datosContacto());
        }
    }

    private void escribir(String contenido) {
        try (FileWriter fw = new FileWriter(archivo, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<persona> leerArchivo() throws IOException {
        List<persona> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean saltarEncabezado = true;
            while ((linea = br.readLine()) != null) {
                if (saltarEncabezado) { saltarEncabezado = false; continue; }
                String[] d = linea.split(";");
                if (d.length >= 5) {
                    lista.add(new persona(d[0], d[1], d[2], d[3], Boolean.parseBoolean(d[4])));
                }
            }
        }
        return lista;
    }

    // PUNTO 5: sobrescritura sincronizada (igual que antes)
    public void guardarListaCompleta(List<persona> lista) {
        synchronized (lock) {
            try (FileWriter fw = new FileWriter(archivo, false);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
                for (persona p : lista) {
                    pw.println(p.datosContacto());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // PUNTO 3: exportar CSV concurrente (igual que antes, ruta ahora relativa)
    public void exportarCSV(List<persona> lista, String rutaDestino) throws IOException {
        synchronized (lock) {
            File destino = new File(rutaDestino);
            destino.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(destino, false);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
                for (persona p : lista) {
                    pw.println(p.datosContacto());
                }
            }
        }
    }
}
