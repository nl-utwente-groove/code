package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;

/**
 * Action to cancel editing the currently displayed control program.
 */
public class CancelControlEditAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public CancelControlEditAction(ControlPanel panel) {
        super(panel, Options.CANCEL_EDIT_ACTION_NAME, Icons.CANCEL_ICON);
    }

    @Override
    protected boolean doAction() {
        getPanel().stopEditing(true);
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getPanel().isEditing());
    }
}