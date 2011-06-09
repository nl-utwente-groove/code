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
    public void execute() {
        ResourceKind resource = getResourceKind();
        final String newName =
            askNewName(resource, Options.getNewResourceName(resource), true);
        if (newName != null) {
            try {
                if (resource.isGraphBased()) {
                    final AspectGraph newGraph =
                        AspectGraph.emptyGraph(newName, resource.getGraphRole());
                    getSimulatorModel().doAddGraph(resource, newGraph);
                } else {
                    getSimulatorModel().doAddText(getResourceKind(), newName,
                        "");
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getDisplay().startEditResource(newName);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e, "Error creating new %s '%s'",
                    resource.getDescription(), newName);
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null
            && getGrammarStore().isModifiable());
    }
}