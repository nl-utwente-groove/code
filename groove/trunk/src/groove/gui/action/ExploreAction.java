package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.HTMLConverter;
import groove.view.StoredGrammarView;

import javax.swing.Action;

/**
 * The 'default exploration' action (class).
 */
public class ExploreAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public ExploreAction(Simulator simulator) {
        super(simulator, Options.DEFAULT_EXPLORATION_ACTION_NAME,
            Icons.FORWARD_ICON);
        putValue(ACCELERATOR_KEY, Options.DEFAULT_EXPLORATION_KEY);
    }

    @Override
    protected boolean doAction() {
        getSimulator().doRunExploration(getModel().getExploration(),
            true, true);
        return false;
    }

    @Override
    public void refresh() {
        StoredGrammarView grammar = getModel().getGrammar();
        setEnabled(grammar != null && grammar.getStartGraphView() != null
            && grammar.getErrors().isEmpty());
        String toolTipText =
            HTMLConverter.HTML_TAG.on(String.format(
                "%s (%s)",
                Options.DEFAULT_EXPLORATION_ACTION_NAME,
                HTMLConverter.STRONG_TAG.on(getModel().getExploration().getIdentifier())));
        putValue(Action.SHORT_DESCRIPTION, toolTipText);
    }
}