package nl.utwente.groove.gui.action;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JColorChooser;
import javax.swing.JDialog;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJVertex;
import nl.utwente.groove.gui.jgraph.JGraph;

/**
 * Action for selecting a colour for a type node.
 */
public class SelectColorAction extends SimulatorAction implements GraphSelectionListener {
    /** Constructs an instance of the action. */
    public SelectColorAction(Simulator simulator) {
        super(simulator, Options.SELECT_COLOR_ACTION_NAME, null);
        putValue(SHORT_DESCRIPTION, Options.SELECT_COLOR_ACTION_NAME);
        refresh();
        this.chooser = new JColorChooser();
    }

    /** Checks if in a given JGraph a type label is selected. */
    private void checkJGraph(AspectJGraph jGraph) {
        this.graph = null;
        this.nodes.clear();
        Object[] selection = jGraph.getSelectionCells();
        if (selection != null) {
            this.graph = jGraph.getGraph();
            var selectionStream = Arrays.stream(selection);
            // find the relevant nodes
            selectionStream
                .filter(c -> c instanceof AspectJVertex)
                .map(v -> ((AspectJVertex) v).getNode())
                .forEach(this.nodes::add);
        }
        refresh();
    }

    @Override
    public void execute() {
        Color initColour = this.nodes.stream().findFirst().get().getColor();
        if (initColour != null) {
            this.chooser.setColor(initColour);
        }
        JDialog dialog = JColorChooser
            .createDialog(getFrame(), "Choose colour for type", false, this.chooser,
                          e -> setColour(SelectColorAction.this.chooser.getColor()), null);
        dialog.setVisible(true);
    }

    private void setColour(Color newColour) {
        Aspect colourAspect = null;
        if (!newColour.equals(Color.black)) {
            colourAspect = AspectKind.COLOR.newAspect(newColour);
        }
        var hostGraph = this.graph;
        assert hostGraph != null;
        var newHostGraph = hostGraph.colour(this.nodes, colourAspect);
        if (newHostGraph != hostGraph) {
            try {
                getSimulatorModel()
                    .doAddGraph(ResourceKind.toResource(this.graph.getRole()), newHostGraph, false);
            } catch (IOException exc) {
                showErrorDialog(exc, String
                    .format("Error while saving host graph '%s'", hostGraph.getName()));
            }
        }
    }

    /** Sets {@link #nodes} based on the {@link JGraph} selection. */
    @Override
    public void valueChanged(GraphSelectionEvent e) {
        checkJGraph((AspectJGraph) e.getSource());
    }

    @Override
    public void refresh() {
        super.setEnabled(!this.nodes.isEmpty());
    }

    /** The graph to be changed. */
    private AspectGraph graph;
    /** The selected nodes to be changed */
    private Set<AspectNode> nodes = new HashSet<>();

    private final JColorChooser chooser;
}