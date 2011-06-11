package groove.gui.action;

import static groove.trans.ResourceKind.HOST;
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
            // we're editing a state
            AspectGraph graph = getStateDisplay().getStateTab().getGraph();
            final String newGraphName = askNewName(HOST, graph.getName(), true);
            if (newGraphName != null) {
                final AspectGraph newGraph = graph.rename(newGraphName);
                try {
                    getSimulatorModel().doAddGraph(HOST, newGraph);
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
            for (String name : getSimulatorModel().getSelectSet(
                getResourceKind())) {
                getDisplay().startEditResource(name);
            }
        }
    }

    @Override
    public void refresh() {
        boolean enabled =
            getSimulatorModel().isSelected(getResourceKind()) || isForState();
        setEnabled(enabled);
        if (getResourceKind() == HOST) {
            String name =
                isForState() ? Options.EDIT_STATE_ACTION_NAME
                        : getEditActionName();
            putValue(NAME, name);
            putValue(SHORT_DESCRIPTION, name);
        }
    }

    private boolean isForState() {
        return getResourceKind() == HOST && getSimulatorModel().hasState()
            && !getSimulatorModel().isSelected(HOST);
    }
}