package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    public static String MESSAGE_DECRYPTION_ERROR = "Decryption error occurred";
    public static String MESSAGE_UNEXPECTED_ERROR = "Unexpected error occurred";
    public static String MESSAGE_IO_ERROR = "I/O error occurred";

    public static String MESSAGE_DECRYPTION_COMPLETED = "Decryption completed";

    public static BufferedImage convertImage(BufferedImage originalImage) {
        int newImageType = originalImage.getType();

        if (newImageType == BufferedImage.TYPE_INT_RGB
                || newImageType == BufferedImage.TYPE_INT_BGR) {
            newImageType = BufferedImage.TYPE_3BYTE_BGR;
        } else if (newImageType == BufferedImage.TYPE_INT_ARGB ||
                newImageType == BufferedImage.TYPE_CUSTOM) {
            newImageType = BufferedImage.TYPE_4BYTE_ABGR;
        } else if (newImageType == BufferedImage.TYPE_INT_ARGB_PRE) {
            newImageType = BufferedImage.TYPE_4BYTE_ABGR_PRE;
        } else {
            // no need to convert original image
            return originalImage;
        }
        BufferedImage newImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), newImageType);
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static byte[] applyMd5(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        return md5.digest(bytes);
    }
}
