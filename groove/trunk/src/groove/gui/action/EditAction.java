package groove.gui.action;

import groove.gui.EditType;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.SwingUtilities;

/** Action to start editing the currently displayed control program. */
public class EditAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EditAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.MODIFY, resource);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    @Override
    public void execute() {
        if (isForState()) {
            AspectGraph graph = getStateDisplay().getStateTab().getGraph();
            // find out if we're editing a host graph or a state
            final String newGraphName =
                askNewName(ResourceKind.HOST, graph.getName(), true);
            if (newGraphName != null) {
                final AspectGraph newGraph = graph.rename(newGraphName);
                try {
                    getSimulatorModel().doAddGraph(ResourceKind.HOST, newGraph);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getDisplaysPanel().getStateDisplay().startEditResource(
                                newGraphName);
                        }
                    });
                } catch (IOException e) {
                    showErrorDialog(e, "Can't edit state '%s'", graph.getName());
                }
            }
        } else {
            String name = getSimulatorModel().getSelected(getResourceKind());
            getDisplay().startEditResource(name);
        }
    }

    @Override
    public void refresh() {
        boolean enabled =
            getSimulatorModel().isSelected(getResourceKind()) || isForState();
        setEnabled(enabled);
        if (getResourceKind() == ResourceKind.HOST) {
            String name =
                isForState() ? Options.EDIT_STATE_ACTION_NAME
                        : getEditActionName();
            putValue(NAME, name);
            putValue(SHORT_DESCRIPTION, name);
        }
    }

    private boolean isForState() {
        return getResourceKind() == ResourceKind.HOST
            && getSimulatorModel().hasState() && !getSimulatorModel().hasHost();
    }
}