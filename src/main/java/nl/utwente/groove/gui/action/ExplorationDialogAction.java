package nl.utwente.groove.gui.action;

import static nl.utwente.groove.grammar.GrammarKey.EXPLORATION;

import javax.swing.Action;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.ExplorationDialog;
import nl.utwente.groove.util.parse.FormatException;

/** Action to open the Exploration Dialog. */
public class ExplorationDialogAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ExplorationDialogAction(Simulator simulator) {
        super(simulator, Options.EXPLORATION_DIALOG_ACTION_NAME, Icons.COMPASS_ICON);
    }

    @Override
    public void execute() {
        new ExplorationDialog(getSimulator(), getFrame());
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(grammar != null && grammar.hasRules());
        if (grammar != null) {
            var error = false;
            var properties = grammar.getProperties();
            try {
                var exploreType = properties.parseProperty(EXPLORATION);
                error = !EXPLORATION.check(grammar, exploreType).isEmpty();
            } catch (FormatException e) {
                error = true;
            }
            putValue(Action.SMALL_ICON, error
                ? Icons.COMPASS_ERROR_ICON
                : Icons.COMPASS_ICON);
        }
    }
}