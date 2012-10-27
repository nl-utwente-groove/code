package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.view.GrammarModel;

import javax.swing.Action;

/**
 * Action for applying the current derivation to the current state.
 */
public class ApplyMatchAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ApplyMatchAction(Simulator simulator) {
        super(simulator, Options.APPLY_MATCH_ACTION_NAME, Icons.GO_NEXT_ICON);
        putValue(Action.ACCELERATOR_KEY, Options.APPLY_KEY);
        simulator.addAccelerator(this);
    }

    @Override
    public void execute() {
        MatchResult match = getSimulatorModel().getMatch();
        if (match == null) {
            // no match is selected; explore the selected state instead
            getActions().getExploreAction().doExploreState();
        } else {
            GraphState state = getSimulatorModel().getState();
            RuleTransition trans;
            if (match.hasRuleTransition()) {
                trans = match.getRuleTransition();
            } else {
                trans = state.applyMatch(match);
            }
            getSimulatorModel().doSetStateAndMatch(trans.target(), trans);
        }
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(getSimulatorModel().hasState() && grammar != null
            && !grammar.hasErrors() && grammar.hasRules());
        putValue(Action.SHORT_DESCRIPTION, getSimulatorModel().hasMatch()
                ? Options.APPLY_MATCH_ACTION_NAME
                : Options.EXPLORE_STATE_ACTION_NAME);
    }
}
