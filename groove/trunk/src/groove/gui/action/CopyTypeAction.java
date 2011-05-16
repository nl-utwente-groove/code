package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

/**
 * Action to copy the currently displayed type graph.
 */
public class CopyTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public CopyTypeAction(Simulator simulator) {
        super(simulator, Options.COPY_TYPE_ACTION_NAME, Icons.COPY_ICON);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        String oldName = getModel().getType().getName();
        String newName =
            askNewTypeName("Select new type graph name", oldName, true);
        if (newName != null) {
            AspectGraph newType =
                getModel().getType().getAspectGraph().rename(newName);
            try {
                result = getModel().doAddType(newType);
            } catch (IOException exc) {
                showErrorDialog(String.format(
                    "Error while copying type graph '%s' to '%s'", oldName,
                    newName), exc);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getType() != null);
        if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
            getSimulator().getCopyMenuItem().setAction(this);
        }
    }
}