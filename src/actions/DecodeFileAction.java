package actions;

import frames.MainFrame;
import methods.Descriptor;

import java.awt.event.ActionEvent;

public class DecodeFileAction extends AbstractProgramMenuAction {

    public DecodeFileAction(MainFrame owner) {
        super(owner, "Decode file");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        owner.decryptFile(new Descriptor());
    }
}
