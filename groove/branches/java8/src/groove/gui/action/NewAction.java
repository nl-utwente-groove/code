package groove.gui.action;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.Resource;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.Text;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.EditType;

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
        ResourceKind resourceKind = getResourceKind();
        final String newName = askNewName(Options.getNewResourceName(resourceKind), true);
        if (newName != null) {
            try {
                Resource resource;
                if (resourceKind.isGraphBased()) {
                    resource = AspectGraph.emptyGraph(newName, resourceKind.getGraphRole());
                } else {
                    resource = new Text(resourceKind, newName, "");
                }
                getSimulatorModel().doAdd(resource, false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getDisplay().startEditResource(newName);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e,
                    "Error creating new %s '%s'",
                    resourceKind.getDescription(),
                    newName);
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null && getGrammarStore().isModifiable());
    }
}