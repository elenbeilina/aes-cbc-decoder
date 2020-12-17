package constants;

import javax.swing.*;
import java.awt.*;

public class Parameters {

    public static final String PROGRAM_NAME = "aes-cbc-decoder";
    public static final String PROGRAM_AUTHOR = "Elena Beilina";

    public static int MAINFRAME_WIDTH = 550;
    public static int MAINFRAME_HEIGHT = 550;

    public static void showErrorMessage(Component parentComponent, String message, String errorMessage) {
        JOptionPane.showMessageDialog(parentComponent, message + "\n\nError message:\n\n" + "<html><textarea cols=20 rows=2>" + errorMessage.replaceAll("[\n\r]", " ") + "</textarea></html>", Parameters.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE);
    }

    public static void showInformationMessage(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, Parameters.PROGRAM_NAME, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showAbout(Component parentComponent) {
        JOptionPane.showMessageDialog(parentComponent,
                "<html><b>" +
                        "<h3>" +
                        Parameters.PROGRAM_NAME + " by " + Parameters.PROGRAM_AUTHOR +
                        "</h3>" +
                        "</b></html>\n\n",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

}
