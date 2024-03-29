package nl.utwente.groove.gui.action;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;

/**
 * Action for setting the initial state of the LTS as current state.
 * @see GTS#startState()
 * @see SimulatorModel#setState(GraphState)
 */
public class GotoStartStateAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public GotoStartStateAction(Simulator simulator) {
        super(simulator, Options.GOTO_START_STATE_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.GOTO_START_STATE_KEY);
        getSimulator().addAccelerator(this);
    }

    @Override
    public void execute() {
        getSimulatorModel().setState(getSimulatorModel().getGTS().startState());
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGTS() != null);
    }
}