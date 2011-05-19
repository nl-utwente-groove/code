package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.TypeView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

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
        TypeView type = getModel().getType();
        String typeName = type.getName();
        AspectGraph typeGraph = type.getAspectGraph();
        if (confirmBehaviour(Options.DELETE_TYPE_OPTION,
            String.format("Delete type graph '%s'?", typeName))
            && getPanel().disposeEditors(typeGraph)) {
            try {
                result = getModel().doDeleteType(typeName);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while deleting type graph '%s'", typeName));
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getType() != null
            && getModel().getStore().isModifiable());
    }
}