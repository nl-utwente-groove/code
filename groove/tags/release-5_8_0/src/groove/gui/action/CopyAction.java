package groove.gui.action;

import java.io.IOException;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.io.store.EditType;

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
        QualName oldName = getSimulatorModel().getSelected(getResourceKind());
        if (getDisplay().saveEditor(oldName, true, false)) {
            QualName newName = askNewName(oldName.toString(), true);
            if (newName != null) {
                doCopy(oldName, newName);
            }
        }
    }

    /**
     * Renames the resource from one name to the other.
     * @return true if the action succeeded
     */
    private boolean doCopy(QualName oldName, QualName newName) {
        boolean result = false;
        ResourceKind resourceKind = getResourceKind();
        if (resourceKind.isTextBased()) {
            String text = getGrammarStore().getTexts(resourceKind)
                .get(oldName);
            result = getActions().getSaveAction(resourceKind)
                .doSaveText(newName, text);
        } else {
            AspectGraph host = getGrammarStore().getGraphs(resourceKind)
                .get(oldName);
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
        setEnabled(getSimulatorModel().getSelectSet(getResourceKind())
            .size() == 1);
    }
}