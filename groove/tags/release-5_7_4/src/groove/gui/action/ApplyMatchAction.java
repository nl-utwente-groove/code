package groove.gui.action;

import javax.swing.Action;

import groove.explore.AcceptorValue;
import groove.explore.Exploration;
import groove.explore.ExploreType;
import groove.explore.StrategyValue;
import groove.grammar.model.GrammarModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;

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
        if (getSimulatorModel().hasTransition()) {
            applySelectedTransition();
        } else if (getSimulatorModel().hasMatch()) {
            try {
                applySelectedMatch();
            } catch (InterruptedException exc) {
                // match was interrupted
            }
        } else {
            exploreState();
        }
    }

    /**
     * Applies the transition selected in the simulator model
     */
    private void exploreState() {
        // no match is selected; explore the selected state instead
        getActions().getExploreAction()
            .doExploreState();
    }

    /**
     * Applies the match selected in the simulator model
     * @throws InterruptedException if an oracle input was cancelled
     */
    private void applySelectedMatch() throws InterruptedException {
        GraphState state = getSimulatorModel().getState();
        RuleTransition trans;
        MatchResult match = getSimulatorModel().getMatch();
        if (match.hasTransitionFrom(state)) {
            trans = match.getTransition();
        } else {
            trans = state.applyMatch(match);
        }
        GraphState target = trans.target();
        if (target.isRealState() || getLtsDisplay().getJGraph()
            .isShowRecipeSteps()) {
            getSimulatorModel().doSetStateAndMatch(target, trans);
        } else {
            Exploration e = getActions().getExploreAction()
                .explore(target, getStateExploration());
            if (e.getResult()
                .isEmpty()) {
                getSimulatorModel().doSetStateAndMatch(state, null);
            } else {
                getSimulatorModel().doSetStateAndMatch(e.getResult()
                    .getLastState(), trans);
            }
        }
    }

    /**
     *
     */
    private void applySelectedTransition() {
        GraphTransition trans = getSimulatorModel().getTransition();
        getSimulatorModel().doSetStateAndMatch(trans.target(), trans);
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(getSimulatorModel().hasState() && grammar != null && !grammar.hasErrors()
            && grammar.hasRules());
        putValue(Action.SHORT_DESCRIPTION,
            getSimulatorModel().hasMatch() ? Options.APPLY_MATCH_ACTION_NAME
                : Options.EXPLORE_STATE_ACTION_NAME);
    }

    /**
     * Returns the explore-strategy for exploring a single state
     */
    private ExploreType getStateExploration() {
        if (this.stateExploration == null) {
            this.stateExploration = new ExploreType(StrategyValue.STATE, AcceptorValue.ANY, 0);
        }
        return this.stateExploration;
    }

    private ExploreType stateExploration;
}
