package methods;

import components.Block;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public class Protector {

    private static final int n = 15;

    public void protectImage(BufferedImage image) {

        long start = new Date().getTime();

        int blockWidth = image.getWidth() / n;
        int blockHeight = image.getHeight() / n;

        int x;
        int y = 0;

        for (int i = 0; i < n; i++) {
            x = 0;
            for (int j = 0; j < n; j++) {
                try {
                    System.out.println("protecting block: " + i + " " + j);

                    BufferedImage subImage = image.getSubimage(x, y, blockWidth, blockHeight);
                    Hamming comparison = new Hamming();
                    String hash = comparison.getHash(subImage);

                    Block block = new Block(x, y, blockWidth, blockHeight);
                    Encryptor encryptor = new Encryptor();
                    encryptor.encryptHash(image, block, hash);

                    x += blockWidth;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            y += blockHeight;
        }

        System.out.println("execution time:" + (new Date().getTime() - start));
    }

    public void authenticatingImage(BufferedImage image, Integer distance){
        int blockWidth = image.getWidth() / n;
        int blockHeight = image.getHeight() / n;

        int x;
        int y = 0;

        long start = new Date().getTime();

        for (int i = 0; i < n; i++) {
            x = 0;
            for (int j = 0; j < n; j++) {
                try {
                    System.out.println("authenticating block: " + i + " " + j);

                    BufferedImage subImage = image.getSubimage(x, y, blockWidth, blockHeight);
                    Hamming comparison = new Hamming();
                    String hash = comparison.getHash(subImage);

                    Descriptor descriptor = new Descriptor();
                    String decryptedHash = descriptor.decodeTheImage(subImage).substring(0,64);

                    int computedDistance = comparison.distance(hash, decryptedHash);

                    //circle bad subBlock
                    if(computedDistance > distance){
                        System.out.println("distance =" + computedDistance + " hash " + decryptedHash);

                        Graphics2D g = (Graphics2D) image.getGraphics();
                        g.setColor(Color.RED);
                        g.setStroke(new BasicStroke(2));
                        g.drawOval(x, y, blockWidth-3, blockHeight-3);
                    }

                    x += blockWidth;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            y += blockHeight;
        }
        System.out.println("execution time:" + (new Date().getTime() - start));
    }
}
