package methods;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static utils.Util.applyMd5;

public class Descriptor {

    private static final String initVector = " ";
    private File encrypted;
    private String decrypted;

    public void decodeFile(File file) throws Exception {
        encrypted = file;
        LocalDateTime november = LocalDateTime.of(2020, Month.NOVEMBER, 1, 0, 0, 0);

        while (november.getMonth() == Month.NOVEMBER) {
            long second = november.toEpochSecond(ZoneOffset.UTC);
            String secretKey = new KeyGenerator().generateKey(second);
            decrypted = "decrypted_" + november + ".jpeg";

            byte[] result = applyAES(secretKey);
            convertBytesToImage(result);

            if (isJPEG()) {
                System.out.println("Hallelujah");
                //break;
            }

            november = november.plusSeconds(1);
        }

    }

    public byte[] applyAES(String key) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(encrypted);

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] keyBytes = new byte[16];
            byte[] b = new BASE64Decoder().decodeBuffer(key);
            int len = b.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);

            byte[] keyBytesiv = new byte[16];
            byte[] biv = new BASE64Decoder().decodeBuffer(DigestUtils.md5Hex(initVector));
            int leniv = biv.length;
            if (leniv > keyBytesiv.length)
                leniv = keyBytesiv.length;
            System.arraycopy(biv, 0, keyBytesiv, 0, len);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytesiv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            //wrong key, continue
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("decrypt fail!", e);
        }

        return null;
    }

    private void convertBytesToImage(byte[] arr) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            BufferedImage bImage2 = ImageIO.read(bis);
            if (Objects.isNull(bImage2)) {
                return;
            }

            File file = new File(decrypted);
            if (file.createNewFile()) {
                ImageIO.write(bImage2, "jpeg", file);
            }
        } catch (IOException e) {
            //continue
        }
    }

    private boolean isJPEG() throws Exception {
        File file = new File(decrypted);

        if (file.exists()) {
            DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

            return ins.readInt() == 0xffd8ffe0;
        }
        return false;
    }
}