package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.ExplorationStatsDialog;
import groove.view.StoredGrammarView;

/** Action to open the Exploration Statistics Dialog. */
public class ExplorationStatsDialogAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ExplorationStatsDialogAction(Simulator simulator) {
        super(simulator, Options.EXPLORATION_STATS_DIALOG_ACTION_NAME, null);
    }

    @Override
    protected boolean doAction() {
        new ExplorationStatsDialog(getSimulator(), getFrame());
        return false;
    }

    @Override
    public void refresh() {
        StoredGrammarView grammar = getModel().getGrammar();
        setEnabled(grammar != null && grammar.getStartGraphView() != null
            && grammar.getErrors().isEmpty());
    }
}