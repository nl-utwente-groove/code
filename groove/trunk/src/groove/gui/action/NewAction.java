package groove.gui.action;

import groove.gui.EditType;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.SwingUtilities;

/** Action to create and start editing a new control program. */
public class NewAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.CREATE, resource);
    }

    @Override
    public boolean execute() {
        ResourceKind resource = getResourceKind();
        final String newName =
            askNewName(resource, Options.getNewResourceName(resource), true);
        if (newName != null) {
            try {
                switch (resource) {
                case CONTROL:
                    if (getControlDisplay().cancelEditing(true)) {
                        getSimulatorModel().doAddControl(newName, "");
                        getControlDisplay().startEditing();
                    }
                    break;
                case HOST:
                case RULE:
                case TYPE:
                    final AspectGraph newGraph =
                        AspectGraph.emptyGraph(newName, resource.getGraphRole());
                    getSimulatorModel().doAddGraph(resource, newGraph);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getDisplay().createEditor(newName);
                        }
                    });
                    break;
                case PROLOG:
                    getSimulatorModel().doAddProlog(newName, "");
                    getPrologDisplay().createEditor(newName);
                    break;
                case PROPERTIES:
                default:
                    assert false;
                }
            } catch (IOException e) {
                showErrorDialog(e, "Error creating new %s '%s'",
                    resource.getDescription(), newName);
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null
            && getGrammarStore().isModifiable());
    }
}