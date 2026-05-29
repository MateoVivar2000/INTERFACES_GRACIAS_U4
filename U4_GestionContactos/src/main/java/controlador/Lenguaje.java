package controlador;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controlador de idioma — igual a la versión original.
 * Carga el archivo de propiedades correspondiente al idioma indicado
 * desde la carpeta resources/idiomas/.
 */
public class Lenguaje {

    private static ResourceBundle bundle;

    public static void definirIdioma(String idioma, String pais) {
        Locale local = new Locale(idioma, pais);
        bundle = ResourceBundle.getBundle("idiomas.mensajes", local);
    }

    public static String get(String llave) {
        return bundle.getString(llave);
    }
}
