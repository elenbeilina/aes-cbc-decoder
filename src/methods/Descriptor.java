package methods;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

public class Descriptor {

    private static final String initVector = " ";
    private File encrypted;
    private static final String decrypted = "decrypted.";

    public void decodeFile(File file) throws Exception {
        encrypted = file;
        LocalDateTime november = LocalDateTime.of(2020, Month.NOVEMBER, 1, 0, 0, 0);

        while (november.getMonth() == Month.NOVEMBER) {
            long second = november.toEpochSecond(ZoneOffset.UTC);
            byte[] secretKey = generateKey(second);

            applyAES(secretKey);

            if (isJPEG()) {
                System.out.println("Hallelujah");
                break;
            }

            november = november.plusSeconds(1);
        }

    }

    private byte[] generateKey(long seconds) {
        return null;
    }

    public void applyAES(byte[] key) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(encrypted);

        try {
            IvParameterSpec iv = new IvParameterSpec(applyMd5(initVector.getBytes(StandardCharsets.UTF_8)));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(data));
            FileUtils.writeByteArrayToFile(new File(decrypted), original);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    private Boolean isJPEG() throws Exception {
        File file = new File(decrypted);

        try (DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            return ins.readInt() == 0xffd8ffe0;
        }
    }

    private byte[] applyMd5(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        return md5.digest(bytes);
    }
}