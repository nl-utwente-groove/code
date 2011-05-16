package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action for quitting the simulator.
 * @see Simulator#doQuit()
 */
public class QuitAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public QuitAction(Simulator simulator) {
        super(simulator, Options.QUIT_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.QUIT_KEY);
    }

    @Override
    protected boolean doAction() {
        getSimulator().doQuit();
        return false;
    }
}