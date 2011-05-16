package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.StoredGrammarView;

/**
 * The 'default exploration' action (class).
 */
public class DefaultExplorationAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public DefaultExplorationAction(Simulator simulator) {
        super(simulator, Options.DEFAULT_EXPLORATION_ACTION_NAME,
            Icons.FORWARD_ICON);
        putValue(ACCELERATOR_KEY, Options.DEFAULT_EXPLORATION_KEY);
    }

    @Override
    protected boolean doAction() {
        getSimulator().doRunExploration(getSimulator().getDefaultExploration(),
            true);
        return false;
    }

    @Override
    public void refresh() {
        StoredGrammarView grammar = getModel().getGrammar();
        setEnabled(grammar != null && grammar.getStartGraphView() != null
            && grammar.getErrors().isEmpty());
    }
}