package modelo;

/**
 * Modelo de datos del Contacto (persona).
 *
 * UNIDAD 4 — CAMBIOS RESPECTO A VERSIÓN ANTERIOR:
 *  - Clase compatible con Gson para serialización/deserialización JSON.
 *    Gson usa los campos directamente, por lo que no requiere anotaciones.
 *  - Se agrega constructor vacío (requerido por Gson al deserializar).
 *  - Se mantiene toda la lógica original (datosContacto, formatoLista, toArray).
 *  - Compatibilidad total con personaDAO (CSV) y el nuevo ContactoJsonService (JSON).
 */
public class persona {

    // Declaración de variables privadas — mismo diseño de unidades anteriores
    private String nombre, telefono, email, categoria;
    private boolean favorito;

    // Constructor vacío — REQUERIDO por Gson para deserializar JSON → objeto
    public persona() {
        super();
        this.nombre    = "";
        this.telefono  = "";
        this.email     = "";
        this.categoria = "";
        this.favorito  = false;
    }

    // Constructor completo — igual que versión anterior
    public persona(String nombre, String telefono, String email,
                   String categoria, boolean favorito) {
        super();
        this.nombre    = nombre;
        this.telefono  = telefono;
        this.email     = email;
        this.categoria = categoria;
        this.favorito  = favorito;
    }

    // ── Getters y Setters (sin cambios) ──────────────────────────────────────
    public String getNombre()              { return nombre; }
    public void   setNombre(String n)      { this.nombre = n; }
    public String getTelefono()            { return telefono; }
    public void   setTelefono(String t)    { this.telefono = t; }
    public String getEmail()               { return email; }
    public void   setEmail(String e)       { this.email = e; }
    public String getCategoria()           { return categoria; }
    public void   setCategoria(String c)   { this.categoria = c; }
    public boolean isFavorito()            { return favorito; }
    public void    setFavorito(boolean f)  { this.favorito = f; }

    // Método para proveer datos a la JTable (sin cambios)
    public Object[] toArray() {
        return new Object[]{nombre, telefono, email, categoria, favorito ? "Sí" : "No"};
    }

    // Método para almacenar en CSV (sin cambios — mantiene compatibilidad)
    public String datosContacto() {
        return String.format("%s;%s;%s;%s;%s", nombre, telefono, email, categoria, favorito);
    }

    // Formato para lista (sin cambios)
    public String formatoLista() {
        return String.format("%-40s%-40s%-40s%-40s", nombre, telefono, email, categoria);
    }
}
