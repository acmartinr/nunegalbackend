package com.nunegal.Matchly.util;

/**
 * Utilidades para manejo de Strings.
 */
public final class StringUtil {

    /**
     * Comprueba si un String es nulo o vacío (tras trim).
     *
     * @param str Cadena a comprobar
     * @return true si es null, vacío o solo contiene espacios
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
