package nl.utwente.groove.gui.action;

import javax.swing.Action;

import nl.utwente.groove.explore.AcceptorValue;
import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.StrategyValue;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.lts.GraphNextState;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.GraphTransition.Claz;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.lts.RecipeTransition;
import nl.utwente.groove.lts.RuleTransition;

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
        getActions().getExploreAction().doExploreState();
    }

    /**
     * Applies the match selected in the simulator model
     * @throws InterruptedException if an oracle input was cancelled
     */
    private void applySelectedMatch() throws InterruptedException {
        GraphState state = getSimulatorModel().getState();
        MatchResult match = getSimulatorModel().getMatch();
        RuleTransition trans = match.getTransitionFrom(state);
        if (trans == null) {
            trans = state.applyMatch(match);
        }
        GraphState target = trans.target();
        if (target.isPublic() || getLtsDisplay().getJGraph().isShowRecipeSteps()) {
            getSimulatorModel().doSetStateAndMatch(target, trans);
        } else if (target.isInner()) {
            Exploration e = getActions().getExploreAction().explore(target, getStateExploration());
            if (e.isInterrupted()) {
                getSimulatorModel().doSetStateAndMatch(state, null);
            } else {
                // find a recipe transition that just got added that contains trans
                var source = trans.source();
                while (!source.isPublic()) {
                    source = ((GraphNextState) source).source();
                }
                // this must be the initial state of that recipe transition
                RecipeTransition recipeTrans = null;
                for (var outTrans : source.getTransitions(Claz.PUBLIC)) {
                    if (outTrans instanceof RecipeTransition r && r.getSteps().contains(trans)) {
                        recipeTrans = r;
                        break;
                    }
                }
                assert recipeTrans != null;
                getSimulatorModel().doSetStateAndMatch(recipeTrans.target(), recipeTrans);
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
        putValue(Action.SHORT_DESCRIPTION, getSimulatorModel().hasMatch()
            ? Options.APPLY_MATCH_ACTION_NAME
            : Options.EXPLORE_STATE_ACTION_NAME);
    }

    /**
     * Returns the explore-strategy for exploring a single state
     */
    private ExploreType getStateExploration() {
        if (this.stateExploration == null) {
            this.stateExploration = new ExploreType(StrategyValue.STATE, AcceptorValue.NONE, 0);
        }
        return this.stateExploration;
    }

    private ExploreType stateExploration;
}
