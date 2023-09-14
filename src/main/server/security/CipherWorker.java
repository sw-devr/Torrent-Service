package main.server.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CipherWorker {

    private static final String PLAIN_KEY = "5f16d1d0-93fb-4092-8325-ae245db1cc42";
    private static final SecretKey SECRET_KEY = new SecretKeySpec(Arrays.copyOf(PLAIN_KEY.getBytes(StandardCharsets.UTF_8), 16), "AES");
    private static final IvParameterSpec ALGORITHM_PARAMETER_SPEC = new IvParameterSpec(Arrays.copyOf("SecretKey1113sdgsg".getBytes(StandardCharsets.UTF_8), 16));

    public static byte[] encrypt(byte[] data) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, ALGORITHM_PARAMETER_SPEC);

            return cipher.doFinal(data);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] data) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, ALGORITHM_PARAMETER_SPEC);

            return cipher.doFinal(data);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
