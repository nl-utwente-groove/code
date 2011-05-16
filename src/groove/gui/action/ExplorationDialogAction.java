package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.ExplorationDialog;
import groove.view.StoredGrammarView;


/** Action to open the Exploration Dialog. */
public class ExplorationDialogAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ExplorationDialogAction(Simulator simulator) {
        super(simulator, Options.EXPLORATION_DIALOG_ACTION_NAME, null);
    }

    @Override
    protected boolean doAction() {
        new ExplorationDialog(getSimulator(),
            getFrame());
        return false;
    }

    @Override
    public void refresh() {
        StoredGrammarView grammar = getModel().getGrammar();
        setEnabled(grammar != null && grammar.getStartGraphView() != null
            && grammar.getErrors().isEmpty());
    }
}