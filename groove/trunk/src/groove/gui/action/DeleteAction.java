package groove.gui.action;

import groove.gui.EditType;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

import java.io.IOException;
import java.util.Set;

/**
 * Action to delete the currently displayed control program.
 */
public class DeleteAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public DeleteAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.DELETE, resource);
    }

    @Override
    public void execute() {
        ResourceKind resource = getResourceKind();
        Set<String> names = getSimulatorModel().getSelectSet(resource);
        boolean enabled = false;
        for (String name : names) {
            enabled |=
                getGrammarModel().getResource(resource, name).isEnabled();
            if (enabled) {
                break;
            }
        }
        String question;
        if (names.size() == 1) {
            String description =
                resource == ResourceKind.HOST && enabled ? "start graph"
                        : resource.getDescription();
            String name = names.iterator().next();
            question = String.format("Delete %s '%s'?", description, name);
        } else {
            String addendum =
                enabled && resource == ResourceKind.HOST
                        ? " (including start graph)" : "";
            question =
                String.format("Delete these %d %ss%s?", names.size(),
                    resource.getDescription(), addendum);
        }
        if (confirmBehaviour(Options.getDeleteOption(resource), question)) {
            boolean cancelEditing = true;
            switch (resource) {
            case HOST:
                cancelEditing =
                    getStateDisplay().cancelEdits(names.toArray(new String[0]));
                break;
            case CONTROL:
            case PROLOG:
                cancelEditing =
                    getDisplay().cancelEditResource(names.iterator().next(),
                        false);
                break;
            case RULE:
                cancelEditing =
                    getRuleDisplay().cancelEdits(names.toArray(new String[0]));
                break;
            case TYPE:
                cancelEditing =
                    getTypeDisplay().cancelEdits(names.toArray(new String[0]));
            }
            if (cancelEditing) {
                try {
                    getSimulatorModel().doDelete(resource, names);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while deleting %s%s", resource.getDescription(),
                        names.size() == 1 ? "" : "s"));
                }
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getSelected(getResourceKind()) != null
            && getSimulatorModel().getStore().isModifiable());
    }
}