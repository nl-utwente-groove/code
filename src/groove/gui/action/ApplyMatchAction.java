package groove.gui.action;

import groove.grammar.model.GrammarModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;

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
        if (getSimulatorModel().hasTransition()) {
            GraphTransition trans = getSimulatorModel().getTransition();
            getSimulatorModel().doSetStateAndMatch(trans.target(), trans);
        } else if (getSimulatorModel().hasMatch()) {
            GraphState state = getSimulatorModel().getState();
            RuleTransition trans;
            if (match.hasTransitionFrom(state)) {
                trans = match.getTransition();
            } else {
                trans = state.applyMatch(match);
            }
            getSimulatorModel().doSetStateAndMatch(trans.target(), trans);
        } else {
            // no match is selected; explore the selected state instead
            getActions().getExploreAction().doExploreState();
        }
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(getSimulatorModel().hasState() && grammar != null && !grammar.hasErrors()
            && grammar.hasRules());
        putValue(Action.SHORT_DESCRIPTION, getSimulatorModel().hasMatch()
                ? Options.APPLY_MATCH_ACTION_NAME : Options.EXPLORE_STATE_ACTION_NAME);
    }
}
