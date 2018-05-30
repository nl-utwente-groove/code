package groove.gui.action;

import java.io.IOException;

import groove.grammar.QualName;
import groove.grammar.model.ResourceKind;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.EditType;

/**
 * Action to rename the currently displayed resource.
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
        QualName oldName = getSimulatorModel().getSelected(resource);
        if (getDisplay().saveEditor(oldName, true, true)) {
            QualName newName = askNewName(oldName.toString(), false);
            if (newName != null && !newName.equals(oldName)) {
                try {
                    getSimulatorModel().doRename(resource, oldName, newName);
                } catch (IOException exc) {
                    showErrorDialog(exc,
                        String.format("Error while renaming %s '%s' into '%s'",
                            resource.getDescription(),
                            oldName,
                            newName));
                }
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getSelectSet(getResourceKind())
            .size() == 1);
    }
}