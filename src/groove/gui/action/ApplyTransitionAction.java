package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;

import javax.swing.Action;

/**
 * Action for applying the current derivation to the current state.
 */
public class ApplyTransitionAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ApplyTransitionAction(Simulator simulator) {
        super(simulator, Options.APPLY_TRANSITION_ACTION_NAME, null);
        putValue(Action.ACCELERATOR_KEY, Options.APPLY_KEY);
        simulator.addAccelerator(this);
    }

    @Override
    public boolean execute() {
        getModel().applyMatch();
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getMatch() != null);
    }
}
