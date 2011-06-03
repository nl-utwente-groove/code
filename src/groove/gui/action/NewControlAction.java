package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.util.Groove;

import java.io.IOException;

/** Action to create and start editing a new control program. */
public class NewControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewControlAction(Simulator simulator) {
        super(simulator, Options.NEW_CONTROL_ACTION_NAME,
            Icons.NEW_CONTROL_ICON);
    }

    @Override
    public boolean execute() {
        if (getControlDisplay().cancelEditing(true)) {
            String newName =
                askNewName(ResourceKind.CONTROL, "Select control program name",
                    Groove.DEFAULT_CONTROL_NAME, true);
            try {
                if (newName != null) {
                    getSimulatorModel().doAddControl(newName, "");
                    getControlDisplay().startEditing();
                }
            } catch (IOException exc) {
                showErrorDialog(exc, "Error creating new control program "
                    + newName);
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null);
    }
}