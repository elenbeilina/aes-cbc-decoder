package frames;

import actions.AboutAction;
import actions.DecodeFileAction;
import actions.ExitAction;
import actions.OpenFileAction;
import components.ImagePanel;
import exceptions.DecodeException;
import methods.Descriptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    public void openFile() {
        fileChooser.showOpenDialog(this);
    }

    public void decryptFile(Descriptor descriptor) {
        try {
            File file = descriptor.decodeFile(fileChooser.getSelectedFile());
            loadImageFile(file);
            showInformationMessage(this, MESSAGE_DECRYPTION_COMPLETED);
        } catch (DecodeException e) {
            showErrorMessage(this, MESSAGE_DECRYPTION_ERROR, e.getMessage());
        } catch (NullPointerException e) {
            showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void exit() {
        this.dispose();
    }

    private void createComponents() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new OpenFileAction(this));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(this));

        JMenu decoderMenu = new JMenu("Decoder");
        decoderMenu.add(new DecodeFileAction(this));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new AboutAction(this));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(decoderMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);

        imagePanel = new ImagePanel("Image: ");

        this.add(imagePanel, BorderLayout.WEST);
    }

    private void loadImageFile(File f) {
        try {
            imagePanel.loadImage(f);
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
