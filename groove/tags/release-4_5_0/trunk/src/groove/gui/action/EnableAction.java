package groove.gui.action;

import groove.gui.EditType;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.ResourceModel;

import java.io.IOException;
import java.util.Set;

import javax.swing.Action;

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
                showErrorDialog(exc, "Error during %s enabling",
                    getResourceKind().getDescription());
            }
        }
    }

    @Override
    public void refresh() {
        ResourceKind resourceKind = getResourceKind();
        String name = getSimulatorModel().getSelected(resourceKind);
        ResourceModel<?> resource =
            getSimulatorModel().getResource(resourceKind);
        boolean isEnabling = resource == null || !resource.isEnabled();
        boolean enabled = name != null && getGrammarStore().isModifiable();
        //        if (resourceKind == ResourceKind.HOST) {
        //            enabled &= isEnabling;
        //        } else {
        String description = Options.getEnableName(resourceKind, isEnabling);
        putValue(NAME, description);
        putValue(SHORT_DESCRIPTION, description);
        //        }
        setEnabled(enabled);
    }

    // =======================================================================
    // UniqueEnableAction
    // =======================================================================

    /** 
     * Special enable action, only valid for host graphs, which not only
     * enables the selected graph, but also disables all others.
     */
    public static class UniqueEnableAction extends EnableAction {

        /** Constructs a new action. */
        public UniqueEnableAction(Simulator simulator) {
            super(simulator, ResourceKind.HOST);
            putValue(NAME, this.ACTION_NAME);
            putValue(SHORT_DESCRIPTION, this.HOVER_DESCRIPTION);
            putValue(Action.SMALL_ICON, Icons.ENABLE_UNIQUE_ICON);
        }

        @Override
        public void execute() {
            String name = getSimulatorModel().getSelected(ResourceKind.HOST);
            if (!getDisplay().saveEditor(name, true, false)) {
                return;
            }
            try {
                getSimulatorModel().doEnableStartGraphUniquely(name);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during %s enabling",
                    getResourceKind().getDescription());
            }
        }

        @Override
        public void refresh() {
            boolean enabled = false;
            Set<String> names =
                getSimulatorModel().getSelectSet(ResourceKind.HOST);
            if (names.size() == 1) {
                Set<String> start = getGrammarModel().getStartGraphs();
                if (start.size() > 1) {
                    enabled = true;
                } else {
                    enabled = !start.containsAll(names);
                }
            }
            setEnabled(enabled);
        }

        /** Name of the action on the menu. */
        private final String ACTION_NAME = "Enable This Graph Only";
        /** Hover text for this action. */
        private final String HOVER_DESCRIPTION =
            "Enable this graph, and disable all other graphs";

    }

}