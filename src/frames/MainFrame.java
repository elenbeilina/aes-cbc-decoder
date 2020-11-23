package frames;

import actions.*;
import components.Block;
import components.ImagePanel;
import exceptions.DecodeException;
import exceptions.EncodeException;
import methods.Descriptor;
import methods.Encryptor;
import methods.Protector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static constants.Parameters.*;
import static utils.Util.*;


public class MainFrame extends JFrame {

    private ImagePanel imagePanel;
    private JFileChooser fileChooser;

    public MainFrame() {
        setSize(MAINFRAME_WIDTH, MAINFRAME_HEIGHT);
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(scrSize.width / 2 - MAINFRAME_WIDTH / 2, scrSize.height / 2 - MAINFRAME_HEIGHT / 2);
        setTitle(PROGRAM_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowEventsHandler());

        createComponents();
    }

    public void openCoverImage() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadImageFile(fileChooser.getSelectedFile(), imagePanel);
        }
    }

    public void encryptImage(Encryptor encryptor) {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = imagePanel.getImage();
                encryptor.encryptHash(image, new Block(0, 0, image.getWidth(), image.getHeight()), "00000000000000000000000100000000000000000000000010000000000000000000000");
                loadImage();
                showInformationMessage(this, MESSAGE_ENCRYPTION_COMPLETED);
            } catch (EncodeException e) {
                showErrorMessage(this, MESSAGE_ENCRYPTION_ERROR, e.getMessage());
            } catch (NullPointerException e) {
                showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImage() throws IOException {
        File file = FileFilter.addExtension(fileChooser.getSelectedFile());

        String name = file.getName();
        String extension = name.substring(name.lastIndexOf('.') + 1);
        try {
            ImageIO.write(imagePanel.getImage(), extension, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imagePanel.loadImage(file);
    }

    public void decryptImage(Descriptor descriptor) {
        try {
            String phash = descriptor.decodeTheImage(imagePanel.getImage());
            showInformationMessage(this, phash);
        } catch (DecodeException e) {
            showErrorMessage(this, MESSAGE_DECRYPTION_ERROR, e.getMessage());
        } catch (NullPointerException e) {
            showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void protectImage(Protector protector) throws IOException {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            protector.protectImage(imagePanel.getImage());
            loadImage();
            showInformationMessage(this, MESSAGE_ENCRYPTION_COMPLETED);
        }
    }

    public void authenticateImage(Protector protector) {
        repaint();
        String distance = JOptionPane.showInputDialog(this, "Enter distance", PROGRAM_NAME, JOptionPane.INFORMATION_MESSAGE);
        protector.authenticatingImage(imagePanel.getImage(), Integer.valueOf(distance));
        imagePanel.loadImage(imagePanel.getImage());
    }

    public void exit() {
        this.dispose();
    }

    private void createComponents() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new OpenImageAction(this));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(this));

        JMenu lsbMenu = new JMenu("LSB");
        lsbMenu.add(new LSBMethodEncryptionAction(this));
        lsbMenu.add(new LSBMethodDecryptionAction(this));

        JMenu protectorMenu = new JMenu("Protector");
        protectorMenu.add(new ImageProtectAction(this));
        protectorMenu.add(new ImageAuthenticateAction(this));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new AboutAction(this));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(protectorMenu);
        menuBar.add(lsbMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.addChoosableFileFilter(new FileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);

        imagePanel = new ImagePanel("Image: ");

        this.add(imagePanel, BorderLayout.WEST);
    }

    private void loadImageFile(File f, ImagePanel panel) {
        try {
            panel.loadImage(f);
        } catch (IOException e) {
            showErrorMessage(this, MESSAGE_IO_ERROR, e.getMessage());
        } catch (Exception e) {
            showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
        }

    }


    private class WindowEventsHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            MainFrame.this.exit();
        }
    }
}
