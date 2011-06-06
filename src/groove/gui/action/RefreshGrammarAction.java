package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/**
 * Action for refreshing the rule system. Reloads the current rule system
 * and start graph.
 */
public class RefreshGrammarAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public RefreshGrammarAction(Simulator simulator) {
        super(simulator, Options.REFRESH_GRAMMAR_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.REFRESH_KEY);
        simulator.addAccelerator(this);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (confirmStopSimulation()) {
            try {
                result = getSimulatorModel().doRefreshGrammar();
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while refreshing grammar from "
                    + getGrammarStore().getLocation());
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarModel() != null);
    }
}