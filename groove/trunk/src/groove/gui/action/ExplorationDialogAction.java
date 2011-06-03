package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.ExplorationDialog;
import groove.view.GrammarModel;


/** Action to open the Exploration Dialog. */
public class ExplorationDialogAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ExplorationDialogAction(Simulator simulator) {
        super(simulator, Options.EXPLORATION_DIALOG_ACTION_NAME, null);
    }

    @Override
    public boolean execute() {
        new ExplorationDialog(getSimulator(),
            getFrame());
        return false;
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(grammar != null && grammar.getStartGraphModel() != null
            && grammar.getErrors().isEmpty());
    }
}