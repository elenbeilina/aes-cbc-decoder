package frames;

import actions.*;
import components.ImagePanel;
import exceptions.DecodeException;
import exceptions.EncodeException;
import methods.Descriptor;
import methods.Encryptor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import static constants.Parameters.*;
import static utils.Util.*;


public class MainFrame extends JFrame {

    private ImagePanel coverImagePanel;
    private ImagePanel imageToHidePanel;
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
            loadImageFile(fileChooser.getSelectedFile(), coverImagePanel);
        }
    }

    public void openHideImage() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadImageFile(fileChooser.getSelectedFile(), imageToHidePanel);
        }
    }

    public void encryptImage(Encryptor encryptor) {
        String s = JOptionPane.showInputDialog(this, "Embedded text", PROGRAM_NAME, JOptionPane.INFORMATION_MESSAGE);
        if (s == null) {
            return;
        }

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                encryptor.hideTheMessage(coverImagePanel.getCoverImage(), s);
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
            ImageIO.write(coverImagePanel.getCoverImage(), extension, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        coverImagePanel.loadImage(file);
    }

    public void decryptImage(Descriptor descriptor) {
        try {
            String s = descriptor.decodeTheMessage(coverImagePanel.getCoverImage());
        } catch (DecodeException e) {
            showErrorMessage(this, MESSAGE_DECRYPTION_ERROR, e.getMessage());
        } catch (NullPointerException e) {
            showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
        }
    }

    public void exit() {
        this.dispose();
    }

    private void createComponents() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new OpenCoverImageAction(this));
        fileMenu.add(new OpenSecretImageAction(this));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(this));

        JMenu encryptionMenu = new JMenu("Encryption");
        encryptionMenu.add(new LSBMethodEncryptionAction(this));

        JMenu decryptionMenu = new JMenu("Decryption");
        decryptionMenu.add(new LSBMethodDecryptionAction(this));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new AboutAction(this));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(encryptionMenu);
        menuBar.add(decryptionMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.addChoosableFileFilter(new FileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);

        coverImagePanel = new ImagePanel("Cover image: ");
        imageToHidePanel = new ImagePanel("Image to hide: ");

        this.add(coverImagePanel, BorderLayout.WEST);
        this.add(imageToHidePanel, BorderLayout.EAST);
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
