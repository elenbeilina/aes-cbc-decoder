package methods;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Objects;

public class Descriptor {

    private byte[] dataWithZeros;
    private IvParameterSpec ivSpec;

    public void decodeFile(File file) throws Exception {
        LocalDateTime start = LocalDateTime.now();

        prepareZeroPaddingData(file);
        prepareIV();

        File decryptedFile = new File("decrypted");

        for (int i = 0; i < 10; i++) {
            LocalDateTime november = LocalDateTime.of(2020, Month.NOVEMBER, 1, 0, 0, 0);

            while (november.getMonth() == Month.NOVEMBER) {
                long second = november.toEpochSecond(ZoneOffset.UTC);
                String secretKey = new KeyGenerator().generateKey(second);

                byte[] result = applyAES(secretKey);
                if (Objects.isNull(result)) {
                    november = november.plusSeconds(1);
                    continue;
                }

                FileUtils.writeByteArrayToFile(decryptedFile, result);

                if (isJPEG(decryptedFile) || isJPEG(result)) {
                    System.out.println("Hallelujah");
                    break;
                }

                november = november.plusSeconds(1);
            }
        }

        System.out.println(Duration.between(start, LocalDateTime.now()).toMinutes());
    }

    private void prepareZeroPaddingData(File encrypted) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        byte[] data = FileUtils.readFileToByteArray(encrypted);

        int blockSize = Cipher.getInstance("AES/CBC/NoPadding").getBlockSize();

        int plaintextLength = data.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }

        dataWithZeros = new byte[plaintextLength];
        System.arraycopy(data, 0, dataWithZeros, 0, data.length);
    }

    private void prepareIV() throws IOException {
        byte[] ivBytes = convertTo16Bytes(DigestUtils.md5Hex(" "));

        ivSpec = new IvParameterSpec(ivBytes);
    }

    public byte[] applyAES(String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

            byte[] keyBytes = convertTo16Bytes(key);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] result = cipher.doFinal(dataWithZeros);

            return removeZeroBytes(result);

        } catch (BadPaddingException e) {
            //wrong key, continue
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("decrypt fail!", e);
        }

        return null;
    }

    private byte[] removeZeroBytes(byte[] arr) {
        // Remove zero bytes at the end.
        int lastLength = arr.length;
        for (int i = arr.length - 1; i > arr.length - 16; i--) {
            if (arr[i] == (byte) 0) {
                lastLength--;
            } else {
                break;
            }
        }

        byte[] result = new byte[lastLength];
        System.arraycopy(arr, 0, result, 0, lastLength);

        return result;
    }

    private byte[] convertTo16Bytes(String value) throws IOException {
        byte[] bytes = new byte[16];
        byte[] b = new BASE64Decoder().decodeBuffer(value);
        int len = b.length;
        if (len > bytes.length)
            len = bytes.length;
        System.arraycopy(b, 0, bytes, 0, len);

        return bytes;
    }

    private boolean isJPEG(File file) throws Exception {
        DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        return ins.readInt() == 0xffd8ffe0;
    }

    private boolean isJPEG(byte[] result) throws Exception {
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(result));
        String mimeType = URLConnection.guessContentTypeFromStream(is);

        if (Objects.nonNull(mimeType)) {
            System.out.println(mimeType);
        }

        return Objects.nonNull(mimeType) && mimeType.equals("image/jpeg");
    }
}