package nl.utwente.groove.gui.action;

import java.io.IOException;
import java.util.Set;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.io.store.EditType;

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
        Set<QualName> names = getSimulatorModel().getSelectSet(resource);
        boolean active = false;
        for (QualName name : names) {
            active |= getGrammarModel().getResource(resource, name).isActive();
            if (active) {
                break;
            }
        }
        String question;
        if (names.size() == 1) {
            String description = resource == ResourceKind.HOST && active
                ? "start graph"
                : resource.getDescription();
            QualName name = names.iterator().next();
            question = String.format("Delete %s '%s'?", description, name);
        } else {
            String addendum = active && resource == ResourceKind.HOST
                ? " (including start graph)"
                : "";
            question = String
                .format("Delete these %d %ss%s?", names.size(), resource.getDescription(),
                        addendum);
        }
        if (confirmBehaviour(Options.DELETE_RESOURCE_OPTION, question)) {
            // we do not ask for editor cancellation,
            // as deleting the resources makes saving edits superfluous anyway
            try {
                getSimulatorModel().doDelete(resource, names);
            } catch (IOException exc) {
                showErrorDialog(exc,
                                String
                                    .format("Error while deleting %s%s", resource.getDescription(),
                                            names.size() == 1
                                                ? ""
                                                : "s"));
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getSelected(getResourceKind()) != null);
    }
}