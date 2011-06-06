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
        getControlDisplay().addRefreshable(this);
    }

    @Override
    public boolean execute() {
        String name = getSimulatorModel().getSelected(getResourceKind());
        switch (getResourceKind()) {
        case CONTROL:
            getControlDisplay().startEditing();
            break;
        case PROLOG:
            getPrologDisplay().createEditor(name);
            break;
        case HOST:
            if (getSimulatorModel().hasHost()) {
                getStateDisplay().doEdit(
                    getSimulatorModel().getHost().getSource());
            } else {
                AspectGraph graph =
                    getStateDisplay().getStatePanel().getJModel().getGraph();
                // find out if we're editing a host graph or a state
                String newGraphName =
                    askNewName(ResourceKind.HOST, graph.getName(), true);
                if (newGraphName != null) {
                    final AspectGraph newGraph = graph.rename(newGraphName);
                    try {
                        getSimulatorModel().doAddGraph(ResourceKind.HOST,
                            newGraph);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                getDisplaysPanel().getStateDisplay().doEdit(
                                    newGraph);
                            }
                        });
                    } catch (IOException e) {
                        showErrorDialog(e, "Can't edit state '%s'",
                            graph.getName());
                    }
                }
            }
            break;
        case RULE:
            getDisplaysPanel().getRuleDisplay().doEdit(
                getSimulatorModel().getRule().getSource());
            break;
        case TYPE:
            final AspectGraph initType =
                getSimulatorModel().getType().getSource();
            getDisplaysPanel().getTypeDisplay().doEdit(initType);
            break;
        case PROPERTIES:
        default:
            assert false;
        }
        return false;
    }

    @Override
    public void refresh() {
        boolean enabled =
            getGrammarStore() != null && getGrammarStore().isModifiable()
                && getSimulatorModel().isSelected(getResourceKind());
        switch (getResourceKind()) {
        case CONTROL:
            enabled &= !getControlDisplay().isEditing();
            break;
        case HOST:
            enabled |=
                getSimulatorModel().getState() != null
                    && getGrammarStore().isModifiable();
            putValue(NAME, getSimulatorModel().hasHost() ? getEditActionName()
                    : Options.EDIT_STATE_ACTION_NAME);
        }
        setEnabled(enabled);
    }
}