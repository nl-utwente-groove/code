package groove.gui.action;

import static groove.graph.GraphRole.TYPE;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.util.Groove;
import groove.view.aspect.AspectGraph;

/** Action to create and start editing a new type graph. */
public class NewTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public NewTypeAction(Simulator simulator) {
        super(simulator, Options.NEW_TYPE_ACTION_NAME, Icons.NEW_TYPE_ICON);
    }

    @Override
    protected boolean doAction() {
        String typeName =
            askNewTypeName("Select type graph name", Groove.DEFAULT_TYPE_NAME,
                true);
        if (typeName != null) {
            AspectGraph initType = AspectGraph.emptyGraph(typeName, TYPE);
            getSimulator().handleEditGraph(initType, true);
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null);
    }
}