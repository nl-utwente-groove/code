package groove.gui.action;

import groove.gui.EditType;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

/**
 * Action to copy the currently displayed control program.
 */
public class CopyAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public CopyAction(Simulator simulator, ResourceKind kind) {
        super(simulator, EditType.COPY, kind);
    }

    @Override
    public void execute() {
        String oldName = getSimulatorModel().getSelected(getResourceKind());
        if (getDisplay().cancelEditResource(oldName, true)) {
            String newName = askNewName(getResourceKind(), oldName, true);
            if (newName != null) {
                doCopy(oldName, newName);
            }
        }
    }

    /**
     * Renames the resource from one name to the other.
     * @return true if the action succeeded
     */
    private boolean doCopy(String oldName, String newName) {
        boolean result = false;
        ResourceKind resourceKind = getResourceKind();
        if (resourceKind.isTextBased()) {
            String text = getGrammarStore().getTexts(resourceKind).get(oldName);
            result =
                getActions().getSaveAction(resourceKind).doSaveText(newName,
                    text);
        } else {
            AspectGraph host =
                getGrammarStore().getGraphs(resourceKind).get(oldName);
            AspectGraph newHost = host.rename(newName);
            try {
                getSimulatorModel().doAddGraph(resourceKind, newHost);
                result = true;
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while copying %s '%s' to '%s'",
                    resourceKind.getDescription(), oldName, newName));
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getSelectSet(getResourceKind()).size() == 1
            && getGrammarStore().isModifiable());
    }
}