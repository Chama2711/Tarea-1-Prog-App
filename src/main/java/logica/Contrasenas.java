package logica;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Almacena contraseñas con PBKDF2 y una sal aleatoria por usuario. */
public final class Contrasenas {
    private static final int ITERACIONES = 600_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Contrasenas() { }

    public static void validar(char[] clave, char[] confirmacion) {
        boolean vacia = true;
        if (clave != null) {
            for (char caracter : clave) {
                if (!Character.isWhitespace(caracter)) {
                    vacia = false;
                    break;
                }
            }
        }
        if (vacia) {
            throw new IllegalArgumentException("Ingrese una contraseña.");
        }
        if (confirmacion == null || !Arrays.equals(clave, confirmacion)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
    }

    public static String generarHash(char[] clave) {
        validar(clave, clave);
        byte[] sal = new byte[16];
        RANDOM.nextBytes(sal);
        return "pbkdf2-sha256$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(sal) + "$"
                + Base64.getEncoder().encodeToString(derivar(clave, sal));
    }

    public static boolean verificar(char[] clave, String hash) {
        if (clave == null || clave.length == 0 || hash == null) {
            return false;
        }
        try {
            String[] partes = hash.split("\\$", -1);
            if (partes.length != 4 || !partes[0].equals("pbkdf2-sha256")
                    || !partes[1].equals(Integer.toString(ITERACIONES))) {
                return false;
            }
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return sal.length == 16 && esperado.length == 32
                    && MessageDigest.isEqual(esperado, derivar(clave, sal));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(char[] clave, byte[] sal) {
        PBEKeySpec especificacion = new PBEKeySpec(clave, sal, ITERACIONES, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacion).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo procesar la contraseña.", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
