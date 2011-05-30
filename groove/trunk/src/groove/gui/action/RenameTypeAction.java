package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.TypeView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

/**
 * Action to rename the currently displayed type graph.
 */
public class RenameTypeAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public RenameTypeAction(Simulator simulator) {
        super(simulator, Options.RENAME_TYPE_ACTION_NAME, Icons.RENAME_ICON);
        putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        TypeView type = getModel().getType();
        String oldName = type.getName();
        String newName =
            askNewTypeName("Select new type graph name", oldName, false);
        if (newName != null && !oldName.equals(newName)) {
            AspectGraph typeGraph = type.getAspectGraph();
            if (getTypeTab().disposeEditors(typeGraph)) {
                try {
                    result = getModel().doRenameType(typeGraph, newName);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while renaming type graph '%s' into '%s'",
                        oldName, newName));
                }
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