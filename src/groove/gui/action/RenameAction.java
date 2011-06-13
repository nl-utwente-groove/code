package groove.gui.action;

import groove.gui.EditType;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

import java.io.IOException;

/**
 * Action to rename the currently displayed control program.
 */
public class RenameAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public RenameAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.RENAME, resource);
        putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
    }

    @Override
    public void execute() {
        ResourceKind resource = getResourceKind();
        String oldName = getSimulatorModel().getSelected(resource);
        boolean resourceEnabled =
            getGrammarModel().getResource(resource, oldName).isEnabled();
        boolean proceed = (!resourceEnabled || confirmStopSimulation());
        if (proceed && getDisplay().cancelEditResource(oldName, true)) {
            String newName = askNewName(oldName, false);
            if (newName != null && !newName.equals(oldName)) {
                try {
                    getSimulatorModel().doRename(resource, oldName, newName);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while renaming %s '%s' into '%s'",
                        resource.getDescription(), oldName, newName));
                }
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getSelectSet(getResourceKind()).size() == 1
            && getSimulatorModel().getStore().isModifiable());
    }
}