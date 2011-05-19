package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorPanel.TabKind;

/** Action to start editing the currently displayed control program. */
public class EditControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EditControlAction(Simulator simulator) {
        super(simulator, Options.EDIT_CONTROL_ACTION_NAME, Icons.EDIT_ICON);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    @Override
    public boolean execute() {
        getControlPanel().startEditing();
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getControl() != null
            && getControlPanel().isModifiable()
            && !getControlPanel().isEditing());
        if (getPanel().getSelectedTab() == TabKind.CONTROL) {
            getSimulator().getEditMenuItem().setAction(this);
        }
    }
}