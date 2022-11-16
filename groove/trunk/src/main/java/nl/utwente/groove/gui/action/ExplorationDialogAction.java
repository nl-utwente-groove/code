package nl.utwente.groove.gui.action;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.ExplorationDialog;

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