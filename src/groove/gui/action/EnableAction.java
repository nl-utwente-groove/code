package groove.gui.action;

import java.io.IOException;
import java.util.Set;

import groove.grammar.QualName;
import groove.grammar.model.NamedResourceModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.RuleModel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.EditType;

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
        Set<QualName> names = getSimulatorModel().getSelectSet(resource);
        boolean proceed = true;
        for (QualName name : names) {
            if (!getDisplay().saveEditor(name, true, false)) {
                proceed = false;
                break;
            }
        }
        if (proceed) {
            try {
                getSimulatorModel().doEnable(resource, names);
            } catch (IOException exc) {
                showErrorDialog(exc,
                    "Error during %s enabling",
                    getResourceKind().getDescription());
            }
        }
    }

    @Override
    public void refresh() {
        ResourceKind resourceKind = getResourceKind();
        QualName name = getSimulatorModel().getSelected(resourceKind);
        NamedResourceModel<?> resource = getSimulatorModel().getResource(resourceKind);
        boolean isEnabling = resource == null || !resource.isEnabled();
        boolean enabled = resourceKind.isEnableable() && name != null;
        if (enabled && getResourceKind() == ResourceKind.RULE) {
            assert resource != null; // implied by name != null, which is implied by enabled
            enabled = !((RuleModel) resource).hasRecipes();
        }
        String description = Options.getEnableName(resourceKind, isEnabling);
        putValue(NAME, description);
        putValue(SHORT_DESCRIPTION, description);
        setEnabled(enabled);
    }
}