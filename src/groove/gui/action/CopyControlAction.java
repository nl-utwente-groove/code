package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;

/**
 * Action to copy the currently displayed control program.
 */
public class CopyControlAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public CopyControlAction(ControlPanel panel) {
        super(panel, Options.COPY_CONTROL_ACTION_NAME, Icons.COPY_ICON);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        if (getPanel().stopEditing(true)) {
            String oldName = getModel().getControl().getName();
            String newName =
                askNewControlName("Select new control program name", oldName,
                    true);
            if (newName != null) {
                result = getPanel().getSaveControlAction().doAction();
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