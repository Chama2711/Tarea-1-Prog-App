package soap;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Vincula la consulta privada de notas a un inicio de sesión de estudiante. */
final class TokenNotas {
    private static final String ALGORITMO = "HmacSHA256";
    private static final Duration VIGENCIA = Duration.ofHours(12);
    private static final SecretKeySpec CLAVE = crearClave();

    private TokenNotas() { }

    static String emitir(String nick) {
        String identificador = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(nick.getBytes(StandardCharsets.UTF_8));
        String datos = identificador + "." + Instant.now().plus(VIGENCIA).getEpochSecond();
        return datos + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(firmar(datos));
    }

    static String estudiante(String token) {
        if (token != null && token.length() <= 2048) {
            String[] partes = token.split("\\.", -1);
            if (partes.length == 3) {
                try {
                    byte[] firma = Base64.getUrlDecoder().decode(partes[2]);
                    if (MessageDigest.isEqual(firmar(partes[0] + "." + partes[1]), firma)
                            && Long.parseLong(partes[1]) > Instant.now().getEpochSecond()) {
                        return new String(Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8);
                    }
                } catch (IllegalArgumentException error) {
                    throw new SecurityException("La sesión para consultar notas no es válida.", error);
                }
            }
        }
        throw new SecurityException("La sesión para consultar notas no es válida.");
    }

    private static SecretKeySpec crearClave() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return new SecretKeySpec(bytes, ALGORITMO);
    }

    private static byte[] firmar(String datos) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(CLAVE);
            return mac.doFinal(datos.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException error) {
            throw new IllegalStateException("No se pudo validar la sesión de notas.", error);
        }
    }
}
