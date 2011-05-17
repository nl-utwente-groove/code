package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;

/** Action to start editing the currently displayed control program. */
public class EditControlAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public EditControlAction(ControlPanel panel) {
        super(panel, Options.EDIT_CONTROL_ACTION_NAME, Icons.EDIT_ICON);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    @Override
    protected boolean doAction() {
        getPanel().startEditing();
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getControl() != null
            && getPanel().isModifiable() && !getPanel().isEditing());
        if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
            getSimulator().getEditMenuItem().setAction(this);
        }
    }
}