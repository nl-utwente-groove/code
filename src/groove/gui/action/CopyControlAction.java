package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action to copy the currently displayed control program.
 */
public class CopyControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public CopyControlAction(Simulator simulator) {
        super(simulator, Options.COPY_CONTROL_ACTION_NAME, Icons.COPY_ICON);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        if (getControlPanel().stopEditing(true)) {
            String oldName = getModel().getControl().getName();
            String newName =
                askNewControlName("Select new control program name", oldName,
                    true);
            if (newName != null) {
                result = getActions().getSaveControlAction().doAction();
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getControl() != null);
        if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
            getSimulator().getCopyMenuItem().setAction(this);
        }
    }
}