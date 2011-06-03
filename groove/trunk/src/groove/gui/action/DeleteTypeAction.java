package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.TypeModel;

import java.io.IOException;
import java.util.Collections;

/**
 * Action to delete the currently displayed type graph.
 */
public class DeleteTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public DeleteTypeAction(Simulator simulator) {
        super(simulator, Options.DELETE_TYPE_ACTION_NAME, Icons.DELETE_ICON);
        putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        TypeModel type = getSimulatorModel().getType();
        String typeName = type.getName();
        if (confirmBehaviour(Options.DELETE_TYPE_OPTION,
            String.format("Delete type graph '%s'?", typeName))
            && getTypeTab().disposeEditors(typeName)) {
            try {
                result =
                    getSimulatorModel().doDeleteTypes(Collections.singleton(typeName));
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while deleting type graph '%s'", typeName));
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