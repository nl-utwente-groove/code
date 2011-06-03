package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/**
 * Action to delete the currently displayed control program.
 */
public class DeleteControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public DeleteControlAction(Simulator simulator) {
        super(simulator, Options.DELETE_CONTROL_ACTION_NAME, Icons.DELETE_ICON);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        String controlName = getSimulatorModel().getControl().getName();
        if (confirmBehaviour(Options.DELETE_CONTROL_OPTION,
            String.format("Delete control program '%s'?", controlName))) {
            getControlDisplay().cancelEditing(false);
            try {
                result = getSimulatorModel().doDeleteControl(controlName);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while deleting control program '%s'", controlName));
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