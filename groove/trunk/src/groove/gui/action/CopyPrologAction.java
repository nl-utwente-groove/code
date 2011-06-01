package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action to copy the currently displayed prolog program.
 */
public class CopyPrologAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public CopyPrologAction(Simulator simulator) {
        super(simulator, Options.COPY_PROLOG_ACTION_NAME, Icons.COPY_ICON);
    }

    @Override
    public boolean execute() {
        String oldName = getModel().getProlog().getName();
        if (getPrologDisplay().cancelEditing(oldName, true)) {
            String newName =
                askNewPrologName("Select new prolog program name", oldName,
                    true);
            if (newName != null) {
                getActions().getSavePrologAction().doSave(newName,
                    getModel().getProlog().getProgram());
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getProlog() != null
            && getModel().getStore().isModifiable());
    }
}