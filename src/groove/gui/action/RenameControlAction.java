package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/**
 * Action to rename the currently displayed control program.
 */
public class RenameControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public RenameControlAction(Simulator simulator) {
        super(simulator, Options.RENAME_CONTROL_ACTION_NAME, Icons.RENAME_ICON);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (getControlDisplay().cancelEditing(true)) {
            String oldName = getModel().getControl().getName();
            String newName =
                askNewControlName("Select control program name", oldName, false);
            if (newName != null) {
                try {
                    result = getModel().doRenameControl(oldName, newName);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while renaming control program '%s' into '%s'",
                        oldName, newName));
                }
            }
        }
        return result;
    }
    
    @Override
    public void refresh() {
        setEnabled(getModel().getControl() != null
            && getModel().getStore().isModifiable());
    }
}