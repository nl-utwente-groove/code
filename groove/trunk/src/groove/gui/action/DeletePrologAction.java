package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/**
 * Action to delete the currently displayed control program.
 */
public class DeletePrologAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public DeletePrologAction(Simulator simulator) {
        super(simulator, Options.DELETE_PROLOG_ACTION_NAME, Icons.DELETE_ICON);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        String name = getModel().getProlog().getName();
        if (confirmBehaviour(Options.DELETE_PROLOG_OPTION,
            String.format("Delete prolog program '%s'?", name))) {
            getPrologDisplay().cancelEditing(name, false);
            try {
                result = getModel().doDeleteProlog(name);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while deleting control program '%s'", name));
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