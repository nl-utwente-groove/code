package groove.gui.action;

import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.gui.GraphTab;
import groove.gui.LabelTree;
import groove.gui.Options;
import groove.gui.ResourceDisplay;
import groove.gui.Simulator;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.trans.ResourceKind;
import groove.view.FormatException;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;

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

/**
 * Action for selecting a colour for a type node.
 */
public class SelectColorAction extends SimulatorAction implements
        GraphSelectionListener, TreeSelectionListener {
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
        GraphTab mainTab = (GraphTab) display.getMainTab();
        mainTab.getJGraph().addGraphSelectionListener(this);
        if (this.label == null) {
            checkJGraph(mainTab.getJGraph());
        }
        LabelTree labelTree = mainTab.getEditArea().getLabelTree();
        labelTree.addTreeSelectionListener(this);
        if (this.label == null) {
            checkLabelTree(labelTree);
        }
    }

    @Override
    public void execute() {
        Color initColour =
            getGrammarModel().getLabelStore().getColor(this.label);
        if (initColour != null) {
            this.chooser.setColor(initColour);
        }
        JDialog dialog =
            JColorChooser.createDialog(getFrame(), "Choose colour for type",
                false, this.chooser, new ActionListener() {
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
            String colourString =
                String.format("%s,%s,%s", newColour.getRed(),
                    newColour.getGreen(), newColour.getBlue());
            try {
                colourAspect =
                    AspectKind.COLOR.getAspect().newInstance(colourString,
                        GraphRole.TYPE);
            } catch (FormatException e) {
                // this can't happen, as the colour string is constructed correctly
                assert false;
            }
        }
        for (AspectGraph typeGraph : getGrammarStore().getGraphs(
            ResourceKind.TYPE).values()) {
            AspectGraph newTypeGraph =
                typeGraph.colour(this.label, colourAspect);
            if (newTypeGraph != typeGraph) {
                try {
                    getSimulatorModel().doAddGraph(ResourceKind.TYPE,
                        newTypeGraph);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while saving type graph '%s'",
                        typeGraph.getName()));
                }
            }
        }
    }

    /** Sets {@link #label} based on the {@link GraphJGraph} selection. */
    @Override
    public void valueChanged(GraphSelectionEvent e) {
        checkJGraph((GraphJGraph) e.getSource());
    }

    /** Checks if in a given JGraph a type label is selected. */
    private void checkJGraph(GraphJGraph jGraph) {
        this.label = null;
        Object[] selection = jGraph.getSelectionCells();
        if (selection != null) {
            choose: for (Object cell : selection) {
                for (Label label : ((GraphJCell) cell).getListLabels()) {
                    if (label instanceof TypeLabel && label.isNodeType()) {
                        this.label = (TypeLabel) label;
                        break choose;
                    }
                }
            }
        }
        refresh();
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        checkLabelTree((LabelTree) e.getSource());
    }

    /** Checks if in a given {@link LabelTree} a type label is selected. */
    private void checkLabelTree(LabelTree tree) {
        this.label = null;
        TreePath[] selection = tree.getSelectionPaths();
        if (selection != null) {
            for (TreePath path : selection) {
                Label label =
                    ((LabelTree.LabelTreeNode) path.getLastPathComponent()).getLabel();
                if (label instanceof TypeLabel && label.isNodeType()) {
                    this.label = (TypeLabel) label;
                    break;
                }
            }
        }
        refresh();
    }

    @Override
    public void refresh() {
        super.setEnabled(this.label != null
            && getGrammarModel().getActiveTypeModel().isEnabled());
    }

    /** The label for which a colour is chosen; may be {@code null}. */
    private TypeLabel label;

    private final JColorChooser chooser;
}