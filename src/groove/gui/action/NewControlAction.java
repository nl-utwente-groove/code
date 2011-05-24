package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.util.Groove;

import java.io.IOException;

/** Action to create and start editing a new control program. */
public class NewControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewControlAction(Simulator simulator) {
        super(simulator, Options.NEW_CONTROL_ACTION_NAME, Icons.NEW_ICON);
    }

    @Override
    public boolean execute() {
        if (getControlPanel().cancelEditing(true)) {
            String newName =
                askNewControlName("Select control program name",
                    Groove.DEFAULT_CONTROL_NAME, true);
            try {
                if (newName != null) {
                    getModel().doAddControl(newName, "");
                    getControlPanel().setDirty(true);
                    getControlPanel().startEditing();
                }
            } catch (IOException exc) {
                showErrorDialog(
                    exc, "Error creating new control program " + newName);
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null);
    }
}