package groove.gui.action;

import groove.grammar.model.ResourceKind;
import groove.grammar.model.ResourceModel;
import groove.grammar.model.RuleModel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.EditType;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/** Action to enable or disable resources. */
public class EnableAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EnableAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.ENABLE, resource);
        if (resource == ResourceKind.HOST) {
            putValue(NAME, Options.START_GRAPH_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.START_GRAPH_ACTION_NAME);
        }
    }

    @Override
    public void execute() {
        ResourceKind resource = getResourceKind();
        Set<String> names = getSimulatorModel().getSelectSet(resource);
        boolean proceed = true;
        for (String name : names) {
            if (!getDisplay().saveEditor(name, true, false)) {
                proceed = false;
                break;
            }
        }
        if (proceed) {
            try {
                getSimulatorModel().doEnable(resource, names);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during %s enabling", getResourceKind().getDescription());
            }
        }
    }

    @Override
    public void refresh() {
        ResourceKind kind = getResourceKind();
        String name = getSimulatorModel().getSelected(kind);
        Optional<? extends ResourceModel<?>> resource =
            getSimulatorModel().getResource(kind);
        boolean isEnabling = resource.map(r -> !r.isEnabled()).orElse(true);
        boolean enabled =
            kind.isEnableable() && name != null && getGrammarStore().isModifiable();
        if (enabled && getResourceKind() == ResourceKind.RULE) {
            enabled = resource.map(r -> !((RuleModel) r).hasRecipes()).orElse(false);
        }
        String description = Options.getEnableName(kind, isEnabling);
        putValue(NAME, description);
        putValue(SHORT_DESCRIPTION, description);
        setEnabled(enabled);
    }
}