package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

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
        String oldName = getSimulatorModel().getProlog().getName();
        if (getPrologDisplay().cancelEditing(oldName, true)) {
            String newName =
                askNewName(ResourceKind.PROLOG,
                    "Select new prolog program name", oldName, true);
            if (newName != null) {
                getActions().getSavePrologAction().doSave(newName,
                    getSimulatorModel().getProlog().getProgram());
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getProlog() != null
            && getSimulatorModel().getStore().isModifiable());
    }
}