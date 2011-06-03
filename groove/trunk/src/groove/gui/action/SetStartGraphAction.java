package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/** Action to set a new start graph. */
public class SetStartGraphAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SetStartGraphAction(Simulator simulator) {
        super(simulator, Options.START_GRAPH_ACTION_NAME, Icons.ENABLE_ICON);
    }

    @Override
    public boolean execute() {
        String selection = getSimulatorModel().getHost().getName();
        return getSimulatorModel().doSetStartGraph(selection);
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getHost() != null
            && !getSimulatorModel().getHost().equals(
                getSimulatorModel().getGrammar().getStartGraphModel()));
    }
}