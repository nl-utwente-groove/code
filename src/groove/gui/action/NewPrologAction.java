package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

import java.io.IOException;

/** Action to create and start editing a new control program. */
public class NewPrologAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewPrologAction(Simulator simulator) {
        super(simulator, Options.NEW_PROLOG_ACTION_NAME, Icons.NEW_PROLOG_ICON);
    }

    @Override
    public boolean execute() {
        String newName =
            askNewName(ResourceKind.PROLOG, "Select Prolog program name",
                Simulator.NEW_PROLOG_NAME, true);
        try {
            if (newName != null) {
                getSimulatorModel().doAddProlog(newName, "");
                getPrologDisplay().createEditor(newName);
            }
        } catch (IOException exc) {
            showErrorDialog(exc, "Error creating new Prolog program " + newName);
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null);
    }
}