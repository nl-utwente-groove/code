package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.aspect.AspectGraph;

/** Action to start editing the currently displayed type graph. */
public class EditTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EditTypeAction(Simulator simulator) {
        super(simulator, Options.EDIT_TYPE_ACTION_NAME, Icons.EDIT_ICON);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    @Override
    public boolean execute() {
        final AspectGraph initType = getModel().getType().getAspectGraph();
        getPanel().editGraph(initType);
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getType() != null
            && getModel().getStore().isModifiable());
        if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
            getSimulator().getEditMenuItem().setAction(this);
        }
    }
}