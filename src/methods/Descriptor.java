package methods;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Objects;

public class Descriptor {

    private byte[] data;
    private IvParameterSpec ivSpec;

    public File decodeFile(File file) throws Exception {
        LocalDateTime start = LocalDateTime.now();

        prepareIV();
        data = FileUtils.readFileToByteArray(file);
        File decrypted = null;

        LocalDateTime november = LocalDateTime.of(2020, Month.NOVEMBER, 1, 0, 0, 0);

        while (november.getMonth() == Month.NOVEMBER) {
            long second = november.toEpochSecond(ZoneOffset.UTC);
            String secretKey = new KeyGenerator().generateKey(second);

            byte[] result = applyAES(secretKey);
            if (Objects.isNull(result)) {
                november = november.plusSeconds(1);
                continue;
            }

            if (isJPEG(result)) {
                decrypted = new File("decrypted_" + november);
                FileUtils.writeByteArrayToFile(decrypted, result);

                if(isJPEG(decrypted)){
                    decrypted.createNewFile();
                    break;
                }
            }

            november = november.plusSeconds(1);
        }

        System.out.println(Duration.between(start, LocalDateTime.now()).toMillis());

        return decrypted;
    }

    private void prepareIV() throws DecoderException {
        byte[] ivBytes = Hex.decodeHex(DigestUtils.md5Hex(" ").toCharArray());

        ivSpec = new IvParameterSpec(ivBytes);
    }

    public byte[] applyAES(String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

            byte[] keyBytes = Hex.decodeHex(key.toCharArray());
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            return cipher.doFinal(data);

        } catch (BadPaddingException e) {
            //wrong key, continue
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("decrypt fail!", e);
        }

        return null;
    }

    private boolean isJPEG(File file) throws Exception {
        DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        return ins.readInt() == 0xffd8ffe0;
    }

    private boolean isJPEG(byte[] result) throws Exception {
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(result));
        String mimeType = URLConnection.guessContentTypeFromStream(is);

        return Objects.nonNull(mimeType) && mimeType.equals("image/jpeg");
    }
}