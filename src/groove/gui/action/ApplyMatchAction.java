package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

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
        if (getSimulatorModel().hasMatch()) {
            getSimulatorModel().doApplyMatch();
        } else {
            getSimulatorModel().doExploreState();
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().hasState());
        putValue(Action.SHORT_DESCRIPTION, getSimulatorModel().hasMatch()
                ? Options.APPLY_MATCH_ACTION_NAME
                : Options.EXPLORE_STATE_ACTION_NAME);
    }
}
