package nl.utwente.groove.gui.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.display.GraphTab;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.gui.tree.TypeTree.TypeTreeNode;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Action for selecting a colour for a type node.
 */
public class SelectColorAction extends SimulatorAction
    implements GraphSelectionListener, TreeSelectionListener {
    /** Constructs an instance of the action. */
    public SelectColorAction(Simulator simulator) {
        super(simulator, Options.SELECT_COLOR_ACTION_NAME, null);
        putValue(SHORT_DESCRIPTION, Options.SELECT_COLOR_ACTION_NAME);
        addAsListener(getHostDisplay());
        addAsListener(getRuleDisplay());
        addAsListener(getTypeDisplay());
        refresh();
        this.chooser = new JColorChooser();
    }

    /** Adds this action as a listener to the JGraph and label tree of a
     * given JGraphPanel.
     */
    private void addAsListener(ResourceDisplay display) {
        assert display.getResourceKind().isGraphBased();
        JGraph<?> jGraph = ((GraphTab) display.getMainTab()).getJGraph();
        jGraph.addGraphSelectionListener(this);
        if (this.label == null) {
            checkJGraph(jGraph);
        }
        LabelTree<?> labelTree = jGraph.getLabelTree();
        labelTree.addTreeSelectionListener(this);
        if (this.label == null) {
            checkLabelTree(labelTree);
        }
    }

    /** Checks if in a given JGraph a type label is selected. */
    private void checkJGraph(JGraph<?> jGraph) {
        this.label = null;
        Object[] selection = jGraph.getSelectionCells();
        if (selection != null) {
            choose: for (Object cell : selection) {
                for (Label entry : ((JCell<?>) cell).getKeys()) {
                    if (entry instanceof TypeNode) {
                        this.label = ((TypeNode) entry).label();
                        break choose;
                    }
                }
            }
        }
        refresh();
    }

    /** Checks if in a given {@link LabelTree} a type label is selected. */
    private void checkLabelTree(LabelTree<?> tree) {
        this.label = null;
        TreePath[] selection = tree.getSelectionPaths();
        if (selection != null) {
            for (TreePath path : selection) {
                Object treeNode = path.getLastPathComponent();
                if (treeNode instanceof TypeTreeNode n && n.getEntry().isForNode()) {
                    this.label = n.getEntry().getType().label();
                    break;
                }
            }
        }
        refresh();
    }

    @Override
    public void execute() {
        TypeNode typeNode = getGrammarModel().getTypeGraph().getNode(this.label);
        assert typeNode != null; // ensured by the label
        Color initColour = typeNode.getColor();
        if (initColour != null) {
            this.chooser.setColor(initColour);
        }
        JDialog dialog = JColorChooser
            .createDialog(getFrame(), "Choose colour for type", false, this.chooser,
                          new ActionListener() {
                              @Override
                              public void actionPerformed(ActionEvent e) {
                                  setColour(SelectColorAction.this.chooser.getColor());
                              }
                          }, null);
        dialog.setVisible(true);
    }

    private void setColour(Color newColour) {
        Aspect colourAspect = null;
        if (!newColour.equals(Color.black)) {
            String colourString = String
                .format("%s,%s,%s", newColour.getRed(), newColour.getGreen(), newColour.getBlue());
            try {
                colourAspect
                    = AspectKind.COLOR.getAspect().newInstance(colourString, GraphRole.TYPE);
            } catch (FormatException e) {
                // this can't happen, as the colour string is constructed correctly
                throw Exceptions.UNREACHABLE;
            }
        }
        for (AspectGraph typeGraph : getGrammarStore().getGraphs(ResourceKind.TYPE).values()) {
            AspectGraph newTypeGraph = typeGraph.colour(this.label, colourAspect);
            if (newTypeGraph != typeGraph) {
                try {
                    getSimulatorModel().doAddGraph(ResourceKind.TYPE, newTypeGraph, false);
                } catch (IOException exc) {
                    showErrorDialog(exc, String
                        .format("Error while saving type graph '%s'", typeGraph.getName()));
                }
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        checkLabelTree((LabelTree<?>) e.getSource());
    }

    /** Sets {@link #label} based on the {@link JGraph} selection. */
    @Override
    public void valueChanged(GraphSelectionEvent e) {
        checkJGraph((JGraph<?>) e.getSource());
    }

    @Override
    public void refresh() {
        super.setEnabled(this.label != null);
    }

    /** The label for which a colour is chosen; may be {@code null}. */
    private TypeLabel label;

    private final JColorChooser chooser;
}