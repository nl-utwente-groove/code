package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/** Action to set a new start graph. */
public class SetStartGraphAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SetStartGraphAction(Simulator simulator) {
        super(simulator, Options.START_GRAPH_ACTION_NAME, Icons.START_ICON);
    }

    @Override
    protected boolean doAction() {
        String selection = getModel().getHost().getName();
        return getModel().doSetStartGraph(selection);
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getHost() != null);
    }
}