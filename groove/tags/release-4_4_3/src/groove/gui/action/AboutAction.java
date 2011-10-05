package groove.gui.action;

import groove.gui.Options;
import groove.util.Version;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * Action for displaying an about box.
 */
public class AboutAction extends AbstractAction {
    /** Constructs an instance of the action. */
    public AboutAction() {
        super(Options.ABOUT_ACTION_NAME);
    }

    public void actionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(null, Version.getAboutHTML(), "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
}