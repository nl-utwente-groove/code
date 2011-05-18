package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/** Action to save the currently edited control program. */
public class SaveControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public SaveControlAction(Simulator simulator) {
        super(simulator, Options.SAVE_CONTROL_ACTION_NAME, Icons.SAVE_ICON);
        putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        getControlPanel().addRefreshable(this);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        if (getControlPanel().isDirty()) {
            String name = getModel().getControl().getName();
            result = doSave(name);
            getControlPanel().stopEditing(false);
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
                    getControlPanel().getControlTextArea().getText());
        } catch (IOException exc) {
            showErrorDialog("Error storing control program " + name, exc);
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getControlPanel().isEditing());
    }
}