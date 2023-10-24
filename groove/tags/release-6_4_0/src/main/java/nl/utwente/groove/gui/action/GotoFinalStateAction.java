package nl.utwente.groove.gui.action;

import java.util.Collection;
import java.util.Iterator;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.lts.GraphState;

/**
 * This action will perform random linear exploration, set the state graph to the final state
 * and apply the layouter to this graph. This can be quite useful if you need a quick way
 * to show an end result of a graph grammar execution.
 * @author Tim Molderez
 */
public class GotoFinalStateAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public GotoFinalStateAction(Simulator simulator, boolean animated) {
        super(simulator, Options.GOTO_FINAL_STATE_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.GOTO_FINAL_STATE_KEY);
        getSimulator().addAccelerator(this);
    }

    @Override
    public void execute() {
        // Backup exploration
        Collection<GraphState> finalStates = getFinalStates();
        if (finalStates.isEmpty()) {
            getActions().getExploreAction().execute();
            finalStates = getFinalStates();
        }
        Iterator<GraphState> it = finalStates.iterator();
        GraphState state = null;
        if (it.hasNext()) {
            GraphState current = getSimulatorModel().getState();
            // look for the current final state within the iterator
            while (state != current && it.hasNext()) {
                state = it.next();
            }
            if (it.hasNext()) {
                state = it.next();
            } else {
                state = finalStates.iterator().next();
            }
        } else {
            // there are no final or result states
            state = getSimulatorModel().getExploreResult().getLastState();
            if (state == null) {
                state = getSimulatorModel().getGTS().startState();
            }
        }
        getSimulatorModel().setState(state);
    }

    /**
     * Returns either the set of final states or, if that is empty,
     * the set of result states of the GTS.
     */
    private Collection<GraphState> getFinalStates() {
        Collection<GraphState> result = getSimulatorModel().getGTS().getFinalStates();
        if (result.isEmpty()) {
            result = getSimulatorModel().getExploreResult().getStates();
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGTS() != null);
    }
}