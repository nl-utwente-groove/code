package nl.utwente.groove.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.dialog.VersionDialog;

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