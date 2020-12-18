package methods;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private static final String decrypted = "decrypted.jpeg";

    public void decodeFile(File file) throws Exception {
        encrypted = file;
        LocalDateTime november = LocalDateTime.of(2020, Month.NOVEMBER, 1, 0, 0, 0);

        while (november.getMonth() == Month.NOVEMBER) {
            long second = november.toEpochSecond(ZoneOffset.UTC);
            byte[] secretKey = new KeyGenerator().generateKey(second);

            applyAES(secretKey);

            if (isJPEG()) {
                System.out.println("Hallelujah");
                break;
            }

            november = november.plusSeconds(1);
        }

    }

    public void applyAES(byte[] key) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(encrypted);

        try {
            IvParameterSpec iv = new IvParameterSpec(applyMd5(initVector.getBytes(StandardCharsets.UTF_8)));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] base64data = Base64.decodeBase64(data);

            while (base64data.length % 16 != 0) {
                base64data = add0(base64data);
            }

            byte[] original = cipher.doFinal(base64data);
            convertBytesToImage(original);
        } catch (BadPaddingException e){
            //wrong key, continue
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    private void convertBytesToImage(byte[] arr) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        BufferedImage bImage2 = ImageIO.read(bis);
        if(Objects.isNull(bImage2)){
            return;
        }
        ImageIO.write(bImage2, "jpeg", new File(decrypted));
    }

    private byte[] add0(byte[] arr) {
        byte[] destArray = Arrays.copyOf(arr, arr.length + 1);
        destArray[destArray.length - 1] = 0;

        return destArray;
    }

    private boolean isJPEG() throws Exception {
        File file = new File(decrypted);
        DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        return ins.readInt() == 0xffd8ffe0;
    }
}