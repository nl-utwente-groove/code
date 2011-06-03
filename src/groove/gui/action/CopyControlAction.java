package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

/**
 * Action to copy the currently displayed control program.
 */
public class CopyControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public CopyControlAction(Simulator simulator) {
        super(simulator, Options.COPY_CONTROL_ACTION_NAME, Icons.COPY_ICON);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (getControlDisplay().cancelEditing(true)) {
            String oldName = getSimulatorModel().getControl().getName();
            String newName =
                askNewName(ResourceKind.CONTROL,
                    "Select new control program name", oldName, true);
            if (newName != null) {
                result = getActions().getSaveControlAction().doSave(newName);
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