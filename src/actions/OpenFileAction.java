package actions;

import frames.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class OpenFileAction extends AbstractProgramMenuAction {
	
	public OpenFileAction(MainFrame owner) {
		super(owner, "Choose encrypted file");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		owner.openFile();
	}

}
