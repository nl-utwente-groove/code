package groove.gui.action;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.Text;
import groove.gui.Simulator;
import groove.io.store.EditType;

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
        if (getDisplay().saveEditor(oldName, true, false)) {
            String newName = askNewName(oldName, true);
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
            Text oldText = getGrammarStore().getTexts(resourceKind).get(oldName);
            Text newText = oldText.rename(newName);
            result = getActions().getSaveAction(resourceKind).doSaveText(newText);
        } else {
            AspectGraph host = getGrammarStore().getGraphs(resourceKind).get(oldName);
            AspectGraph newHost = host.rename(newName);
            try {
                getSimulatorModel().doAddGraph(resourceKind, newHost, false);
                result = true;
            } catch (IOException exc) {
                showErrorDialog(exc,
                    String.format("Error while copying %s '%s' to '%s'",
                        resourceKind.getDescription(),
                        oldName,
                        newName));
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