package groove.gui.action;

import groove.grammar.model.GrammarModel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.ExplorationStatsDialog;

/** Action to open the Exploration Statistics Dialog. */
public class ExplorationStatsDialogAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ExplorationStatsDialogAction(Simulator simulator) {
        super(simulator, Options.EXPLORATION_STATS_DIALOG_ACTION_NAME, null);
    }

    @Override
    public void execute() {
        new ExplorationStatsDialog(getSimulator(), getFrame());
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getGrammarModel();
        setEnabled(grammar != null && grammar.getStartGraphModel() != null
            && grammar.getErrors().isEmpty());
    }
}