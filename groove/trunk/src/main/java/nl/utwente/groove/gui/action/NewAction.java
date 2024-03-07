package nl.utwente.groove.gui.action;

import java.io.IOException;

import javax.swing.SwingUtilities;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.io.store.EditType;

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
                    final AspectGraph newGraph = AspectGraph
                        .emptyGraph(newName.toString(), resource.getGraphRole(),
                                    getGrammarModel().getProperties().isHasParallelEdges());
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
                showErrorDialog(e, "Error creating new %s '%s'", resource.getDescription(),
                                newName);
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null);
    }
}