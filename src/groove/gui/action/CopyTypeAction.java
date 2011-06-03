package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
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
    public boolean execute() {
        boolean result = false;
        String oldName = getSimulatorModel().getType().getName();
        String newName =
            askNewName(ResourceKind.TYPE, "Select new type graph name",
                oldName, true);
        if (newName != null) {
            AspectGraph newType =
                getSimulatorModel().getType().getSource().rename(newName);
            try {
                result = getSimulatorModel().doAddType(newType);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while copying type graph '%s' to '%s'", oldName,
                    newName));
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getType() != null
            && getSimulatorModel().getStore().isModifiable());
    }
}