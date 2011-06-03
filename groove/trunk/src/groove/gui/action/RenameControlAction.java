package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

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
            String oldName = getSimulatorModel().getControl().getName();
            String newName =
                askNewName(ResourceKind.CONTROL, "Select control program name",
                    oldName, false);
            if (newName != null) {
                try {
                    result = getSimulatorModel().doRenameControl(oldName, newName);
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
        setEnabled(getSimulatorModel().getControl() != null
            && getSimulatorModel().getStore().isModifiable());
    }
}