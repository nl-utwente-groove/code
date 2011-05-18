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
        String controlName = getControlPanel().getSelectedControl().getName();
        if (confirmBehaviour(Options.DELETE_CONTROL_OPTION,
            String.format("Delete control program '%s'?", controlName))) {
            getControlPanel().stopEditing(false);
            int itemNr = getControlPanel().getNameField().getSelectedIndex() + 1;
            if (itemNr == getControlPanel().getNameField().getItemCount()) {
                itemNr -= 2;
            }
            try {
                result = getModel().doDeleteControl(controlName);
            } catch (IOException exc) {
                showErrorDialog(exc,
                    String.format(
                        "Error while deleting control program '%s'", controlName));
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getControl() != null);
        if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
            getSimulator().getDeleteMenuItem().setAction(this);
        }
    }
}