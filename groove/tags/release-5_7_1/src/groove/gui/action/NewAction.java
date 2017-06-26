package groove.gui.action;

import java.io.IOException;

import javax.swing.SwingUtilities;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.EditType;

/** Action to create and start editing a new control program. */
public class NewAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.CREATE, resource);
    }

    @Override
    public void execute() {
        ResourceKind resource = getResourceKind();
        final QualName newName = askNewName(Options.getNewResourceName(resource), true);
        if (newName != null) {
            try {
                if (resource.isGraphBased()) {
                    final AspectGraph newGraph =
                        AspectGraph.emptyGraph(newName.toString(), resource.getGraphRole());
                    getSimulatorModel().doAddGraph(resource, newGraph, false);
                } else {
                    getSimulatorModel().doAddText(getResourceKind(), newName, "");
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getDisplay().startEditResource(newName);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e,
                    "Error creating new %s '%s'",
                    resource.getDescription(),
                    newName);
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null);
    }
}