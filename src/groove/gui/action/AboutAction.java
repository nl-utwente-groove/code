package groove.gui.action;

import groove.gui.Options;
import groove.gui.dialog.VersionDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

/**
 * Action for displaying an about box.
 */
public class AboutAction extends AbstractAction {
    /** Constructs an instance of the action. */
    public AboutAction(JFrame frame) {
        super(Options.ABOUT_ACTION_NAME);
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        VersionDialog.showAbout(this.frame);
    }

    private final JFrame frame;
}