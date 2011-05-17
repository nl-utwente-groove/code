package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.util.Groove;

import java.io.IOException;

/** Action to create and start editing a new control program. */
public class NewControlAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public NewControlAction(ControlPanel panel) {
        super(panel, Options.NEW_CONTROL_ACTION_NAME, Icons.NEW_ICON);
    }

    @Override
    protected boolean doAction() {
        if (getPanel().stopEditing(true)) {
            String newName =
                askNewControlName("Select control program name",
                    Groove.DEFAULT_CONTROL_NAME, true);
            try {
                if (newName != null) {
                    getModel().doAddControl(newName, "");
                    getPanel().setDirty(true);
                    getPanel().startEditing();
                }
            } catch (IOException exc) {
                showErrorDialog(
                    "Error creating new control program " + newName, exc);
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null);
    }
}