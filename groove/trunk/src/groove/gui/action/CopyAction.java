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
    public boolean execute() {
        boolean result = false;
        String oldName = getSimulatorModel().getSelected(getResourceKind());
        if (getDisplay().cancelEditResource(oldName, true)) {
            String newName = askNewName(getResourceKind(), oldName, true);
            if (newName != null) {
                result = doCopy(oldName, newName);
            }
        }
        return result;
    }

    /**
     * Renames the resource from one name to the other.
     */
    private boolean doCopy(String oldName, String newName) {
        boolean result = false;
        switch (getResourceKind()) {
        case CONTROL:
            result = getActions().getSaveControlAction().doSave(newName);
            break;
        case PROLOG:
            getActions().getSavePrologAction().doSave(newName,
                getSimulatorModel().getProlog().getProgram());
            break;
        case HOST:
            AspectGraph host = getSimulatorModel().getHost().getSource();
            AspectGraph newHost = host.rename(newName);
            try {
                result =
                    getSimulatorModel().doAddGraph(getResourceKind(), newHost);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while copying host graph '%s' to '%s'", oldName,
                    newName));
            }
            break;
        case RULE:
            AspectGraph rule = getSimulatorModel().getRule().getSource();
            AspectGraph newRule = rule.rename(newName);
            try {
                result =
                    getSimulatorModel().doAddGraph(getResourceKind(), newRule);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while copying rule '%s' to '%s'", oldName, newName));
            }
            break;
        case TYPE:
            AspectGraph newType =
                getSimulatorModel().getType().getSource().rename(newName);
            try {
                result =
                    getSimulatorModel().doAddGraph(getResourceKind(), newType);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while copying type graph '%s' to '%s'", oldName,
                    newName));
            }
            break;
        default:
            assert false;
            result = false;
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getCount(getResourceKind()) == 1
            && getGrammarStore().isModifiable());
    }
}