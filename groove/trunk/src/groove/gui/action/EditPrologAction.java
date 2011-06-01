package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/** Action to start editing the currently displayed control program. */
public class EditPrologAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EditPrologAction(Simulator simulator) {
        super(simulator, Options.EDIT_PROLOG_ACTION_NAME,
            Icons.EDIT_PROLOG_ICON);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    @Override
    public boolean execute() {
        getPrologDisplay().createEditor(getModel().getProlog().getName());
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getProlog() != null
            && getModel().getStore().isModifiable());
    }
}