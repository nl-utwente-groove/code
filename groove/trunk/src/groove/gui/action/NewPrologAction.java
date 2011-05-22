package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/** Action to create and start editing a new control program. */
public class NewPrologAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewPrologAction(Simulator simulator) {
        super(simulator, Options.NEW_PROLOG_ACTION_NAME, Icons.NEW_ICON);
    }

    @Override
    public boolean execute() {
        if (getControlPanel().stopEditing(true)) {
            String newName =
                askNewControlName("Select Prolog program name",
                    Simulator.NEW_PROLOG_NAME, true);
            try {
                if (newName != null) {
                    getModel().doAddProlog(newName, "");
                }
            } catch (IOException exc) {
                showErrorDialog(exc, "Error creating new Prolog program "
                    + newName);
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null);
    }
}