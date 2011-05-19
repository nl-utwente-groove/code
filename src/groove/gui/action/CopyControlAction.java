package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorPanel.TabKind;

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
        if (getControlPanel().stopEditing(true)) {
            String oldName = getModel().getControl().getName();
            String newName =
                askNewControlName("Select new control program name", oldName,
                    true);
            if (newName != null) {
                result = getActions().getSaveControlAction().execute();
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getControl() != null);
        if (getPanel().getSelectedTab() == TabKind.CONTROL) {
            getSimulator().getCopyMenuItem().setAction(this);
        }
    }
}