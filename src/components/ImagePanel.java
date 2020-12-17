package components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static utils.Util.convertImage;

public class ImagePanel extends JPanel {
    private final JLabel imageLabel;
    private final JLabel imageName;
    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public ImagePanel(String name) {
        imageName = new JLabel();
        imageName.setText(name);
        setHeaderString("");

        JPanel p1 = new JPanel();
        p1.add(imageName);
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        imageLabel = new JLabel();
        p2.add(imageLabel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(p2, BorderLayout.CENTER);
        add(p1, BorderLayout.NORTH);
    }

    public void loadImage(File f) throws IOException {
        if (f == null) {
            imageLabel.setIcon(null);
            return;
        }
        BufferedImage loadedImage = ImageIO.read(f);
        image = convertImage(loadedImage);
        imageLabel.setIcon(new ImageIcon(loadedImage));
        setHeaderString(f.getName());
    }

    public void setHeaderString(String s) {
        imageName.setText(s);
    }
}
