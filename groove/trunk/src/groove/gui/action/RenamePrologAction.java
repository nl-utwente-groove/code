package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/**
 * Action to rename the currently displayed control program.
 */
public class RenamePrologAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public RenamePrologAction(Simulator simulator) {
        super(simulator, Options.RENAME_PROLOG_ACTION_NAME, Icons.RENAME_ICON);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        String oldName = getModel().getProlog().getName();
        if (getPrologDisplay().cancelEditing(oldName, true)) {
            String newName =
                askNewControlName("Select prolog program name", oldName, false);
            if (newName != null) {
                try {
                    result = getModel().doRenameProlog(oldName, newName);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while renaming prolog program '%s' into '%s'",
                        oldName, newName));
                }
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getProlog() != null
            && getModel().getStore().isModifiable());
    }
}