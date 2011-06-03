package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

import java.io.IOException;

/**
 * Action to rename the currently displayed type graph.
 */
public class RenameTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public RenameTypeAction(Simulator simulator) {
        super(simulator, Options.RENAME_TYPE_ACTION_NAME, Icons.RENAME_ICON);
        putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        String oldName = getSimulatorModel().getType().getName();
        String newName =
            askNewName(ResourceKind.TYPE, "Select new type graph name",
                oldName, false);
        if (newName != null && !oldName.equals(newName)) {
            if (getTypeTab().disposeEditors(oldName)) {
                try {
                    result = getSimulatorModel().doRenameType(oldName, newName);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while renaming type graph '%s' into '%s'",
                        oldName, newName));
                }
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getType() != null
            && getSimulatorModel().getStore().isModifiable());
    }
}