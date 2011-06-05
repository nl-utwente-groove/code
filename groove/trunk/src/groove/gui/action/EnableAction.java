package groove.gui.action;

import groove.gui.EditType;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

import java.io.IOException;
import java.util.Set;

/** Action to enable the currently displayed control program. */
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
    public boolean execute() {
        boolean result = false;
        ResourceKind resource = getResourceKind();
        Set<String> names = getSimulatorModel().getAllSelected(resource);
        try {
            result |= getSimulatorModel().doEnable(resource, names);
        } catch (IOException exc) {
            showErrorDialog(exc, "Error during %s enabling",
                getResourceKind().getDescription());
        }
        return result;
    }

    @Override
    public void refresh() {
        String name = getSimulatorModel().getSelected(getResourceKind());
        boolean isEnabling =
            name == null || !name.equals(getGrammarModel().getStartGraphName());
        boolean enabled;
        if (getResourceKind() == ResourceKind.HOST) {
            enabled = name != null && isEnabling;
        } else {
            enabled = name != null && getGrammarStore().isModifiable();
            String description =
                Options.getEnableName(getResourceKind(), isEnabling);
            putValue(NAME, description);
            putValue(SHORT_DESCRIPTION, description);
        }
        setEnabled(enabled);
    }
}