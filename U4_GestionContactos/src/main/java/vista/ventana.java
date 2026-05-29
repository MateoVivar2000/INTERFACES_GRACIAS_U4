package vista;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import controlador.logica_ventana;
import controlador.Lenguaje;

/**
 * Vista principal — Gestión de Contactos.
 *
 * UNIDAD 4 — CAMBIOS RESPECTO A VERSIÓN ANTERIOR:
 *
 *  1. FlatLaf aplicado en main() antes de crear cualquier componente Swing:
 *       FlatDarkLaf.setup() → tema oscuro moderno en toda la aplicación.
 *     Esta es la integración de la dependencia com.formdev:flatlaf:3.2.5.
 *
 *  2. Dos botones nuevos en la pestaña "Estadísticas":
 *       btn_exportarJson → llama a exportarJSONConcurrente() en logica_ventana
 *       btn_importarJson → llama a importarDesdeJSON() en logica_ventana
 *
 *  3. Layout actualizado en la pestaña Estadísticas para alojar los nuevos
 *     botones junto a la barra de progreso existente.
 *
 *  Todo lo demás (panelContactos, tabla, campos, menú contextual) es idéntico
 *  a la versión de unidades anteriores.
 */
public class ventana extends JFrame {

    // ── Componentes originales (públicos para acceso desde logica_ventana) ────
    public JPanel contentPane;
    public JTextField txt_nombres, txt_telefono, txt_email, txt_buscar;
    public JCheckBox chb_favorito;
    public JComboBox<String> cmb_categoria;
    public JButton btn_add, btn_modificar, btn_eliminar, btn_exportar;
    public JTable tabla_contactos;
    public DefaultTableModel modelo_tabla;
    public JProgressBar barra_progreso;
    public JPopupMenu menu_contextual;

    // ── NUEVO U4: Botones de importar/exportar JSON ───────────────────────────
    public JButton btn_exportarJson;
    public JButton btn_importarJson;

    // ── Actualizar textos desde el ResourceBundle de idiomas ──────────────────
    public void actualizarTextos() {
        setTitle(Lenguaje.get("titulo"));
        btn_add.setText(Lenguaje.get("btn_agregar"));
        btn_modificar.setText(Lenguaje.get("btn_editar"));
        btn_eliminar.setText(Lenguaje.get("btn_borrar"));
    }

    // ── Constructor: construye toda la UI ─────────────────────────────────────
    public ventana() {
        setBackground(new Color(240, 240, 240));
        setTitle("GESTION DE CONTACTOS - UPS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 874, 680); // altura ligeramente mayor para nuevos botones

        contentPane = new JPanel();
        contentPane.setBackground(new Color(0, 51, 102));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(255, 204, 0));

        // ════════════════════════════════════════════════════════
        // PESTAÑA 1: Contactos (idéntica a versión anterior)
        // ════════════════════════════════════════════════════════
        JPanel panelContactos = new JPanel();
        panelContactos.setBackground(new Color(255, 255, 255));

        modelo_tabla = new DefaultTableModel(
                new Object[]{"NOMBRE", "TELÉFONO", "EMAIL", "CATEGORÍA"}, 0);

        tabbedPane.addTab("Contactos", panelContactos);

        JLabel lblBusq = new JLabel("BUSCAR CONTACTO:");
        lblBusq.setFont(new Font("Tahoma", Font.BOLD, 13));
        txt_buscar = new JTextField();

        JLabel lblNom = new JLabel("NOMBRES:");
        lblNom.setFont(new Font("Tahoma", Font.BOLD, 13));
        txt_nombres = new JTextField();

        btn_add = new JButton("");
        btn_add.setIcon(new ImageIcon(ventana.class.getResource("/IMAGENES/contacts_1060405 (1).png")));
        btn_add.setBackground(new Color(255, 204, 0));
        btn_add.setForeground(Color.BLACK);
        btn_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });

        btn_modificar = new JButton("");
        btn_modificar.setBackground(new Color(255, 204, 2));
        btn_modificar.setIcon(new ImageIcon(ventana.class.getResource("/IMAGENES/profile_9967510.png")));
        btn_modificar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });

        btn_eliminar = new JButton("");
        btn_eliminar.setBackground(new Color(255, 204, 2));
        btn_eliminar.setIcon(new ImageIcon(ventana.class.getResource("/IMAGENES/garbage_5853731.png")));
        btn_eliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });

        JLabel lblTel = new JLabel("TELEFONO:");
        lblTel.setFont(new Font("Tahoma", Font.BOLD, 13));
        txt_telefono = new JTextField();

        JLabel lblEmail = new JLabel("EMAIL:");
        lblEmail.setFont(new Font("Tahoma", Font.BOLD, 13));
        txt_email = new JTextField();

        tabla_contactos = new JTable(modelo_tabla);
        JScrollPane scrTabla = new JScrollPane(tabla_contactos);

        JLabel lb_Categoria = new JLabel("CATEGORIA");
        lb_Categoria.setFont(new Font("Tahoma", Font.BOLD, 13));

        cmb_categoria = new JComboBox<>();
        cmb_categoria.setModel(new DefaultComboBoxModel<>(
                new String[]{"Familia", "Amigos", "Trabajo", "Favoritos"}));

        // Layout de la pestaña Contactos (idéntico al original)
        GroupLayout gl = new GroupLayout(panelContactos);
        gl.setHorizontalGroup(
            gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl.createSequentialGroup()
                            .addComponent(lblBusq)
                            .addGap(5)
                            .addComponent(txt_buscar, GroupLayout.PREFERRED_SIZE, 567, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl.createSequentialGroup()
                            .addGap(29)
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lblTel, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblNom, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(txt_nombres, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_telefono, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE))
                            .addGap(10)
                            .addComponent(btn_add)
                            .addGap(5)
                            .addComponent(btn_modificar, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn_eliminar))
                        .addComponent(scrTabla, GroupLayout.PREFERRED_SIZE, 839, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl.createSequentialGroup()
                            .addGap(29)
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(gl.createSequentialGroup()
                                    .addComponent(lblEmail, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txt_email, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl.createSequentialGroup()
                                    .addComponent(lb_Categoria)
                                    .addGap(18)
                                    .addComponent(cmb_categoria, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 254, Short.MAX_VALUE)))
                            .addGap(6)))
                    .addContainerGap())
        );
        gl.setVerticalGroup(
            gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(lblBusq, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_buscar, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                    .addGap(5)
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl.createSequentialGroup()
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(gl.createSequentialGroup()
                                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblNom, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txt_nombres, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                                    .addGap(5)
                                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTel)
                                        .addComponent(txt_telefono, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
                                .addComponent(btn_add, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblEmail, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_email, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lb_Categoria, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmb_categoria, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(btn_modificar, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_eliminar, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)))
                    .addGap(30)
                    .addComponent(scrTabla, GroupLayout.PREFERRED_SIZE, 384, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        panelContactos.setLayout(gl);

        // ════════════════════════════════════════════════════════
        // PESTAÑA 2: Estadísticas — ACTUALIZADA con botones JSON
        // ════════════════════════════════════════════════════════
        JPanel PnEstadisticas = new JPanel();
        tabbedPane.addTab("Estadisticas", PnEstadisticas);

        // Botón original: Exportar CSV
        btn_exportar = new JButton("EXPORTAR CONTACTOS A CSV");
        btn_exportar.setBackground(new Color(0, 102, 153));
        btn_exportar.setForeground(Color.WHITE);
        btn_exportar.setFont(new Font("Tahoma", Font.BOLD, 12));

        // ── NUEVO U4: Botón Exportar JSON ──
        btn_exportarJson = new JButton("📤 EXPORTAR A JSON (Gson)");
        btn_exportarJson.setBackground(new Color(40, 167, 69));
        btn_exportarJson.setForeground(Color.WHITE);
        btn_exportarJson.setFont(new Font("Tahoma", Font.BOLD, 12));

        // ── NUEVO U4: Botón Importar JSON ──
        btn_importarJson = new JButton("📥 IMPORTAR DESDE JSON (Gson)");
        btn_importarJson.setBackground(new Color(23, 162, 184));
        btn_importarJson.setForeground(Color.WHITE);
        btn_importarJson.setFont(new Font("Tahoma", Font.BOLD, 12));

        // Barra de progreso original
        barra_progreso = new JProgressBar(0, 100);
        barra_progreso.setStringPainted(true);

        // Etiqueta informativa (nueva)
        JLabel lblInfoJson = new JLabel(
            "<html><b>Unidad 4 — Gestión de Dependencias:</b><br>" +
            "Los botones JSON usan la dependencia <b>Gson 2.10.1</b> de Maven Central.<br>" +
            "El botón CSV usa el DAO original con concurrencia de unidades anteriores.</html>");
        lblInfoJson.setFont(new Font("Tahoma", Font.PLAIN, 11));

        // Layout de la pestaña Estadísticas
        GroupLayout gl2 = new GroupLayout(PnEstadisticas);
        gl2.setAutoCreateGaps(true);
        gl2.setAutoCreateContainerGaps(true);
        gl2.setHorizontalGroup(
            gl2.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lblInfoJson, GroupLayout.PREFERRED_SIZE, 700, GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_exportar,     GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_exportarJson, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_importarJson, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                .addComponent(barra_progreso,   GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
        );
        gl2.setVerticalGroup(
            gl2.createSequentialGroup()
                .addGap(40)
                .addComponent(lblInfoJson)
                .addGap(30)
                .addComponent(btn_exportar,     GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                .addGap(10)
                .addComponent(btn_exportarJson, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                .addGap(10)
                .addComponent(btn_importarJson, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(barra_progreso,   GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        );
        PnEstadisticas.setLayout(gl2);

        // ── Ensamblado final ──────────────────────────────────────────────────
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        menu_contextual = new JPopupMenu();
        menu_contextual.add(new JMenuItem("Eliminar registro"));

        new logica_ventana(this);
    }

    // ── MAIN — Punto de entrada ───────────────────────────────────────────────
    public static void main(String[] args) {

        // ── INTEGRACIÓN FLATLAF (Unidad 4) ──────────────────────────────────
        // FlatDarkLaf.setup() DEBE llamarse ANTES de crear cualquier componente Swing.
        // Esta línea activa la dependencia com.formdev:flatlaf:3.2.5 del pom.xml.
        // Aplica automáticamente un tema oscuro moderno a toda la aplicación.
        try {
            FlatDarkLaf.setup();
            // Personalización adicional del tema
            UIManager.put("Button.arc",         10);
            UIManager.put("Component.arc",       8);
            UIManager.put("TextComponent.arc",   6);
            UIManager.put("Table.alternateRowColor", new Color(45, 50, 60));
            UIManager.put("TabbedPane.selectedBackground", new Color(0, 102, 153));
        } catch (Exception e) {
            // Si FlatLaf no está en el classpath, usa el L&F por defecto
            System.err.println("[FlatLaf] No se pudo cargar: " + e.getMessage());
        }
        // ── FIN INTEGRACIÓN FLATLAF ──────────────────────────────────────────

        // Lanzar la ventana en el EDT (hilo de despacho de Swing)
        EventQueue.invokeLater(() -> {
            try {
                ventana frame = new ventana();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
