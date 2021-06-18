package groove.gui.action;

import groove.grammar.model.GrammarModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.ExplorationDialog;

/** Action to open the Exploration Dialog. */
public class ExplorationDialogAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ExplorationDialogAction(Simulator simulator) {
        super(simulator, Options.EXPLORATION_DIALOG_ACTION_NAME,
            Icons.COMPASS_ICON);
    }

    @Override
    public void execute() {
        new ExplorationDialog(getSimulator(), getFrame());
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(grammar != null && !grammar.hasErrors()
            && grammar.hasRules());
    }
}