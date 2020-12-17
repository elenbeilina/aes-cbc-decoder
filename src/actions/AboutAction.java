package actions;

import constants.Parameters;
import frames.MainFrame;

import java.awt.event.ActionEvent;

public class AboutAction extends AbstractProgramMenuAction {
	
	public AboutAction(MainFrame owner) {
		super(owner, "About");
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		Parameters.showAbout(owner);
	}

}
