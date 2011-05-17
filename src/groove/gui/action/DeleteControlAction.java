package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;

import java.io.IOException;

/**
 * Action to delete the currently displayed control program.
 */
public class DeleteControlAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public DeleteControlAction(ControlPanel panel) {
        super(panel, Options.DELETE_CONTROL_ACTION_NAME, Icons.DELETE_ICON);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        String controlName = getPanel().getSelectedControl().getName();
        if (confirmBehaviour(Options.DELETE_CONTROL_OPTION,
            String.format("Delete control program '%s'?", controlName))) {
            getPanel().stopEditing(false);
            int itemNr = getPanel().getNameField().getSelectedIndex() + 1;
            if (itemNr == getPanel().getNameField().getItemCount()) {
                itemNr -= 2;
            }
            try {
                result = getModel().doDeleteControl(controlName);
            } catch (IOException exc) {
                showErrorDialog(String.format(
                    "Error while deleting control program '%s'",
                    controlName), exc);
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