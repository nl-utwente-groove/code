package groove.gui.action;

import groove.gui.ControlPanel;
import groove.gui.Icons;
import groove.gui.Options;

import java.io.IOException;

/** Action to save the currently edited control program. */
public class SaveControlAction extends ControlAction {
    /** Constructs a new action, for a given control panel. */
    public SaveControlAction(ControlPanel panel) {
        super(panel, Options.SAVE_CONTROL_ACTION_NAME, Icons.SAVE_ICON);
        putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        if (getPanel().isDirty()) {
            String name = getModel().getControl().getName();
            result = doSave(name);
            getPanel().stopEditing(false);
        }
        return result;
    }

    /**
     * Saves the control program under a given name.
     */
    public boolean doSave(String name) {
        boolean result = false;
        try {
            result =
                getModel().doAddControl(name,
                    getPanel().getControlTextArea().getText());
        } catch (IOException exc) {
            showErrorDialog("Error storing control program " + name, exc);
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getPanel().isEditing());
    }
}