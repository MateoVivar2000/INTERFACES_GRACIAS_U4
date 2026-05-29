package controlador;

import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import vista.ventana;
import modelo.*;
import servicio.ContactoJsonService;

/**
 * Controlador principal de la ventana.
 *
 * UNIDAD 4 — CAMBIOS RESPECTO A VERSIÓN ANTERIOR:
 *  - Se inyecta ContactoJsonService para manejo de JSON (nuevo en U4).
 *  - Se conectan los nuevos botones btn_exportarJson y btn_importarJson.
 *  - Se agrega exportarJSONConcurrente() con SwingWorker (mismo patrón
 *    que exportarCSVConcurrente(), reutilizando la barra de progreso).
 *  - Se agrega importarDesdeJSON() con opción de reemplazar/combinar.
 *  - Toda la lógica original (hilos, SwingWorker, búsqueda, CRUD) se
 *    conserva intacta para no romper funcionalidad previa.
 */
public class logica_ventana implements ActionListener, MouseListener, KeyListener {

    private ventana delegado;
    private List<persona> contactos;
    private TableRowSorter<DefaultTableModel> sorter;

    // Pool de un solo hilo para exportaciones (igual que antes)
    private final ExecutorService exportadorPool = Executors.newSingleThreadExecutor();

    // Bandera volatile para bloquear edición simultánea (igual que antes)
    private volatile boolean contactoBloqueado = false;

    // ── NUEVO U4: Servicio de JSON (Gson) ────────────────────────────────────
    private final ContactoJsonService jsonService = new ContactoJsonService();

    public logica_ventana(ventana delegado) {
        this.delegado = delegado;
        Lenguaje.definirIdioma("es", "EC");
        delegado.actualizarTextos();
        cargarDatosTabla();

        // Botones originales
        this.delegado.btn_add.addActionListener(this);
        this.delegado.btn_modificar.addActionListener(this);
        this.delegado.btn_eliminar.addActionListener(this);
        this.delegado.btn_exportar.addActionListener(this);

        // ── NUEVO U4: Botones de JSON ──
        this.delegado.btn_exportarJson.addActionListener(this);
        this.delegado.btn_importarJson.addActionListener(this);

        this.delegado.txt_telefono.addKeyListener(this);
        this.delegado.tabla_contactos.addMouseListener(this);

        // Búsqueda concurrente con SwingWorker (igual que antes)
        this.delegado.txt_buscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ejecutarBusquedaConcurrente(delegado.txt_buscar.getText().trim());
            }
        });
    }

    // ── Búsqueda concurrente con SwingWorker (sin cambios) ────────────────────
    private void ejecutarBusquedaConcurrente(String texto) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(50);
                return null;
            }
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    if (texto.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
                    }
                });
            }
        }.execute();
    }

    // ── Cargar tabla desde CSV (sin cambios) ──────────────────────────────────
    private void cargarDatosTabla() {
        try {
            contactos = new personaDAO(new persona()).leerArchivo();
            DefaultTableModel modelo = (DefaultTableModel) delegado.modelo_tabla;
            modelo.setRowCount(0);
            for (persona p : contactos) {
                modelo.addRow(new Object[]{
                    p.getNombre(), p.getTelefono(), p.getEmail(), p.getCategoria()
                });
            }
            sorter = new TableRowSorter<>(modelo);
            delegado.tabla_contactos.setRowSorter(sorter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Guardar contacto con validación en hilo (sin cambios) ─────────────────
    private void guardarContactoConValidacion() {
        String nom  = delegado.txt_nombres.getText().trim();
        String tel  = delegado.txt_telefono.getText().trim();
        String mail = delegado.txt_email.getText().trim();
        String cat  = delegado.cmb_categoria.getSelectedItem().toString();

        if (nom.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(delegado, "Nombre y telefono son obligatorios.");
            return;
        }

        delegado.btn_add.setEnabled(false);
        delegado.btn_add.setText("Validando...");

        Thread hiloValidacion = new Thread(() -> {
            boolean duplicado = false;
            synchronized (contactos) {
                for (persona p : contactos) {
                    if (p.getNombre().equalsIgnoreCase(nom) || p.getTelefono().equals(tel)) {
                        duplicado = true;
                        break;
                    }
                }
            }
            final boolean esDuplicado = duplicado;
            SwingUtilities.invokeLater(() -> {
                delegado.btn_add.setEnabled(true);
                delegado.btn_add.setText("");
                if (esDuplicado) {
                    mostrarNotificacion("El contacto ya existe (nombre o telefono duplicado).", false);
                } else {
                    new personaDAO(new persona(nom, tel, mail, cat, false)).escribirArchivo();
                    cargarDatosTabla();
                    limpiarcampos();
                    mostrarNotificacion("Contacto guardado con exito.", true);
                }
            });
        });
        hiloValidacion.setName("Hilo-Validacion-Contacto");
        hiloValidacion.start();
    }

    // ── Notificaciones (sin cambios) ──────────────────────────────────────────
    private void mostrarNotificacion(String mensaje, boolean exito) {
        int tipo = exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(delegado, mensaje,
                exito ? "Exito" : "Advertencia", tipo);
    }

    // ── Exportar CSV concurrente (sin cambios) ─────────────────────────────────
    private void exportarCSVConcurrente() {
        List<persona> copiaContactos;
        synchronized (contactos) {
            copiaContactos = new ArrayList<>(contactos);
        }
        delegado.btn_exportar.setEnabled(false);
        delegado.barra_progreso.setValue(0);

        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                int total = Math.max(copiaContactos.size(), 1);
                for (int i = 0; i <= total; i++) {
                    Thread.sleep(40);
                    publish((int) ((i / (double) total) * 100));
                }
                new personaDAO(new persona()).exportarCSV(
                        copiaContactos, "contacts_data/exportacion_U4.csv");
                return null;
            }
            @Override
            protected void process(List<Integer> chunks) {
                SwingUtilities.invokeLater(() ->
                        delegado.barra_progreso.setValue(chunks.get(chunks.size() - 1)));
            }
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    delegado.barra_progreso.setValue(100);
                    delegado.btn_exportar.setEnabled(true);
                    mostrarNotificacion("CSV exportado: contacts_data/exportacion_U4.csv", true);
                    delegado.barra_progreso.setValue(0);
                });
            }
        }.execute();
    }

    // ── NUEVO U4: Exportar JSON concurrente con SwingWorker ──────────────────
    private void exportarJSONConcurrente() {
        // Selector de archivo
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar contactos a JSON");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON (*.json)", "json"));
        chooser.setSelectedFile(new java.io.File("contacts_data/contactos_exportados.json"));

        if (chooser.showSaveDialog(delegado) != JFileChooser.APPROVE_OPTION) return;

        String ruta = chooser.getSelectedFile().getAbsolutePath();
        if (!ruta.endsWith(".json")) ruta += ".json";
        final String rutaFinal = ruta;

        List<persona> copiaContactos;
        synchronized (contactos) {
            copiaContactos = new ArrayList<>(contactos);
        }

        delegado.btn_exportarJson.setEnabled(false);
        delegado.barra_progreso.setValue(0);

        // SwingWorker para no bloquear la UI durante la escritura
        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                int total = Math.max(copiaContactos.size(), 1);
                for (int i = 0; i <= total; i++) {
                    Thread.sleep(35);
                    publish((int) ((i / (double) total) * 100));
                }
                // Gson serializa la lista completa al archivo JSON
                jsonService.exportarJSON(copiaContactos, rutaFinal);
                return null;
            }
            @Override
            protected void process(List<Integer> chunks) {
                SwingUtilities.invokeLater(() ->
                        delegado.barra_progreso.setValue(chunks.get(chunks.size() - 1)));
            }
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    delegado.barra_progreso.setValue(100);
                    delegado.btn_exportarJson.setEnabled(true);
                    mostrarNotificacion(
                        "✅ JSON exportado (" + copiaContactos.size() + " contactos):\n" + rutaFinal, true);
                    delegado.barra_progreso.setValue(0);
                });
            }
        }.execute();
    }

    // ── NUEVO U4: Importar desde JSON ─────────────────────────────────────────
    private void importarDesdeJSON() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Importar contactos desde JSON");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON (*.json)", "json"));

        if (chooser.showOpenDialog(delegado) != JFileChooser.APPROVE_OPTION) return;

        String ruta = chooser.getSelectedFile().getAbsolutePath();

        try {
            // Gson deserializa el archivo JSON → List<persona>
            List<persona> importados = jsonService.importarJSON(ruta);

            if (importados.isEmpty()) {
                mostrarNotificacion("El archivo JSON está vacío o no tiene contactos válidos.", false);
                return;
            }

            // Preguntar modo de importación al usuario
            int opcion = JOptionPane.showConfirmDialog(delegado,
                "Se importaron " + importados.size() + " contactos desde JSON.\n\n" +
                "¿Deseas REEMPLAZAR los contactos actuales?\n" +
                "  • SÍ  → reemplaza toda la lista\n" +
                "  • NO  → agrega los importados a los existentes",
                "Modo de importación", JOptionPane.YES_NO_CANCEL_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                // Reemplazar: sobrescribir CSV y lista
                synchronized (contactos) {
                    contactos = importados;
                }
                new personaDAO(new persona()).guardarListaCompleta(contactos);

            } else if (opcion == JOptionPane.NO_OPTION) {
                // Combinar: agregar sin duplicar por teléfono
                synchronized (contactos) {
                    for (persona imp : importados) {
                        boolean existe = contactos.stream()
                            .anyMatch(c -> c.getTelefono().equals(imp.getTelefono()));
                        if (!existe) {
                            contactos.add(imp);
                            new personaDAO(imp).escribirArchivo();
                        }
                    }
                }
            } else {
                return; // Cancelado
            }

            cargarDatosTabla();
            mostrarNotificacion(
                "✅ Importación completada. Total en lista: " + contactos.size() + " contactos.", true);

        } catch (IOException ex) {
            mostrarNotificacion(
                "❌ Error al importar JSON:\n" + ex.getMessage() +
                "\n\nVerifica que el archivo tenga el formato correcto.", false);
        }
    }

    // ── CRUD y helpers (sin cambios) ──────────────────────────────────────────
    private void limpiarcampos() {
        delegado.txt_nombres.setText(null);
        delegado.txt_telefono.setText(null);
        delegado.txt_email.setText(null);
        delegado.cmb_categoria.setSelectedIndex(0);
    }

    private void eliminarContacto() {
        int filaSeleccionada = delegado.tabla_contactos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(delegado, "Seleccione un contacto primero");
            return;
        }
        int filaModelo = delegado.tabla_contactos.convertRowIndexToModel(filaSeleccionada);
        synchronized (contactos) {
            contactos.remove(filaModelo);
        }
        new personaDAO(new persona()).guardarListaCompleta(contactos);
        cargarDatosTabla();
    }

    private void ejecutarModificacion() {
        int filaVisual = delegado.tabla_contactos.getSelectedRow();
        if (filaVisual == -1) {
            JOptionPane.showMessageDialog(delegado, "Selecciona un contacto de la tabla primero.");
            return;
        }
        if (contactoBloqueado) {
            JOptionPane.showMessageDialog(delegado, "El contacto esta siendo editado. Espere.");
            return;
        }
        contactoBloqueado = true;
        int filaModelo = delegado.tabla_contactos.convertRowIndexToModel(filaVisual);
        synchronized (contactos) {
            persona p = contactos.get(filaModelo);
            p.setNombre(delegado.txt_nombres.getText());
            p.setTelefono(delegado.txt_telefono.getText());
            p.setEmail(delegado.txt_email.getText());
            p.setCategoria(delegado.cmb_categoria.getSelectedItem().toString());
        }
        new personaDAO(new persona()).guardarListaCompleta(contactos);
        cargarDatosTabla();
        limpiarcampos();
        contactoBloqueado = false;
        SwingUtilities.invokeLater(() ->
                mostrarNotificacion("Contacto actualizado correctamente.", true));
    }

    // ── actionPerformed: despacha eventos de todos los botones ────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == delegado.btn_exportar) {
            exportarCSVConcurrente();
        } else if (e.getSource() == delegado.btn_add) {
            guardarContactoConValidacion();
        } else if (e.getSource() == delegado.btn_eliminar) {
            int r = JOptionPane.showConfirmDialog(delegado,
                    "Desea eliminar este contacto?", "Confirmar eliminacion",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                eliminarContacto();
                SwingUtilities.invokeLater(() ->
                        mostrarNotificacion("Contacto eliminado correctamente.", true));
            }
        } else if (e.getSource() == delegado.btn_modificar) {
            ejecutarModificacion();
        }
        // ── NUEVO U4 ──
        else if (e.getSource() == delegado.btn_exportarJson) {
            exportarJSONConcurrente();
        } else if (e.getSource() == delegado.btn_importarJson) {
            importarDesdeJSON();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            JOptionPane.showMessageDialog(delegado, "Guardando...");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
            delegado.menu_contextual.show(e.getComponent(), e.getX(), e.getY());
            int fila = delegado.tabla_contactos.getSelectedRow();
            if (fila != -1) {
                delegado.txt_nombres.setText(getDatoTabla("NOMBRE", fila));
                delegado.txt_telefono.setText(getDatoTabla("TELÉFONO", fila));
                delegado.txt_email.setText(getDatoTabla("EMAIL", fila));
                delegado.cmb_categoria.setSelectedItem(getDatoTabla("CATEGORÍA", fila));
            }
        }
    }

    private String getDatoTabla(String nombreColumna, int fila) {
        int index = delegado.tabla_contactos.getColumnModel().getColumnIndex(nombreColumna);
        return delegado.tabla_contactos.getValueAt(fila, index).toString();
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
