/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import groove.automaton.NodeRelation;
import groove.automaton.RegExpr;
import groove.automaton.RelationCalculator;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.gui.Options;
import groove.gui.dialog.FormulaDialog;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.LTSJCell;
import groove.gui.jgraph.LTSJGraph;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.util.parse.FormatException;

/**
 * Menu to control the visibility of nodes and edges in a jgraph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ShowHideMenu<G extends Graph> extends JMenu {
    /**
     * Show mode for a {@link ShowHideAction}: involved cells are set to
     * visible.
     */
    static protected final int ADD_MODE = 0;

    /**
     * Show mode for a {@link ShowHideAction}: involved cells are hidden.
     */
    static protected final int HIDE_MODE = 1;

    /**
     * Show mode for a {@link ShowHideAction}: involved cells are set to
     * visible, all others are hidden.
     */
    static protected final int ONLY_MODE = 2;

    /**
     * Prefix for the action name in {@link #HIDE_MODE}.
     */
    static public final String HIDE_MODE_NAME = "Hide";

    /**
     * Prefix for the action name in {@link #ADD_MODE}.
     */
    static public final String ADD_MODE_NAME = "Add";

    /**
     * Prefix for the action name in {@link #ONLY_MODE}.
     */
    static protected final String ONLY_MODE_NAME = "Show";
    /** Name of the action to process all elements. */
    static public final String ALL_ACTION_NAME = "All";
    /** Name of the action to process only the selected elements. */
    static public final String SELECTED_ACTION_NAME = "Selected";
    /** Name of the action to invert the hidden elements. */
    static public final String INVERT_ACTION_NAME = "Inverse";
    /** Name of the action to process the context of a given element. */
    static public final String CONTEXT_ACTION_NAME = "Context";
    /**
     * Name of the action to process elements according to a regular expression.
     */
    static public final String REGEXPR_ACTION_NAME = "Pattern...";
    /** Name of the action to process elements from a file. */
    static public final String FILE_ACTION_NAME = "All From File...";
    /** Name of the action to process elements by label. */
    static public final String LABEL_MENU_NAME = "Label";
    /** Highlight trace to start state name */
    public static final String TRACE_ACTION_NAME = "Trace From Start State";

    /**
     * Returns the name for a show mode.
     */
    static public String getModeName(int showMode) {
        switch (showMode) {
        case ADD_MODE:
            return ADD_MODE_NAME;
        case ONLY_MODE:
            return ONLY_MODE_NAME;
        default:
            return HIDE_MODE_NAME;
        }
    }

    /**
     * Constructs a display control menu, which either shows or hides nodes and
     * edges based on selection or labels.
     * @param jgraph the underlying jgraph of which the display should be
     *        controlled
     */
    public ShowHideMenu(JGraph<G> jgraph) {
        super(Options.SHOW_HIDE_MENU_NAME);
        setMnemonic(MENU_MNEMONIC);
        this.jgraph = jgraph;
        fillOutMenu(getPopupMenu());
    }

    /** Fills a given menu with actions to show and hide elements. */
    protected void fillOutMenu(JPopupMenu menu) {
        // show actions
        menu.add(createAllAction(ONLY_MODE));
        menu.add(createSelectedAction(ONLY_MODE));
        menu.add(createShowRegExprAction(ONLY_MODE));
        menu.add(createContextAction(ADD_MODE));
        if (this.jgraph instanceof LTSJGraph) {
            menu.add(createTraceAction(ONLY_MODE));
        }
        menu.add(createFromFileAction(ONLY_MODE));
        // add actions
        menu.addSeparator();
        menu.add(createSelectedAction(ADD_MODE));
        menu.add(createAddRegExprAction(ADD_MODE));
        menu.add(createContextAction(ADD_MODE));
        menu.add(createLabelMenu(ADD_MODE));
        menu.addSeparator();
        // hide actions
        menu.add(createAllAction(HIDE_MODE));
        menu.add(createSelectedAction(HIDE_MODE));
        menu.add(createAddRegExprAction(HIDE_MODE));
        menu.add(createLabelMenu(HIDE_MODE));
    }

    /**
     * Factory method for <tt>AllAction</tt>s.
     */
    protected ShowHideAction<G> createAllAction(int showMode) {
        return new AllAction<>(this.jgraph, showMode);
    }

    /**
     * Factory method for <tt>InvertAction</tt>s.
     */
    protected ShowHideAction<G> createInvertAction(int showMode) {
        return new InvertAction<>(this.jgraph, showMode);
    }

    /**
     * Factory method for <tt>RegExprAction</tt>s.
     */
    protected ShowHideAction<G> createAddRegExprAction(int showMode) {
        return new RegExprAction<>(this.jgraph, showMode);
    }

    /**
     * Factory method for <tt>RegExprAction</tt>s.
     */
    protected ShowHideAction<G> createShowRegExprAction(int showMode) {
        return new RegExprAction<>(this.jgraph, showMode);
    }

    /**
     * Factory method for <tt>ContextAction</tt>s.
     */
    protected ShowHideAction<G> createContextAction(int showMode) {
        return new ContextAction<>(this.jgraph, showMode);
    }

    /**
     * Factory method for {@link ShowHideMenu.SelectedAction}s.
     */
    protected ShowHideAction<G> createSelectedAction(int showMode) {
        return new SelectedAction<>(this.jgraph, showMode);
    }

    /**
     * Factory method for {@link ShowHideMenu.FromFileAction}s.
     */
    protected ShowHideAction<G> createFromFileAction(int showMode) {
        return new FromFileAction<>(this.jgraph, showMode);
    }

    /**
     * Factory method for {@link ShowHideMenu.TraceAction}s.
     */
    protected ShowHideAction<GTS> createTraceAction(int showMode) {
        return new TraceAction((LTSJGraph) this.jgraph, showMode);
    }

    /**
     * Factory method for <tt>LabelAction</tt>s.
     */
    protected ShowHideAction<G> createLabelAction(int showMode, Label label) {
        return new LabelAction<>(this.jgraph, showMode, label);
    }

    /**
     * Factory method for the label sub-menu. To be overiden, e.g., if the cell
     * labels should be adapted.
     * @param showMode {@link #ADD_MODE} if the menu is for inclusion;
     *        {@link #HIDE_MODE} if it is for exclusion; {@link #ONLY_MODE} if
     *        it is for exclusive highlighting;
     * @return a new label menu
     * @see LabelMenu
     */
    protected JMenu createLabelMenu(int showMode) {
        return new LabelMenu(showMode);
    }

    /** Returns the JGraph for which this menu works. */
    JGraph<G> getJGraph() {
        return this.jgraph;
    }

    /** The JGraph upon which this menu works. */
    private final JGraph<G> jgraph;

    /** Mnemonic key for the {@link AllAction} */
    private static int ALL_MNEMONIC = KeyEvent.VK_A;
    /** Mnemonic key for the {@link SelectedAction} */
    private static int EMPHASIZED_MNEMONIC = KeyEvent.VK_E;
    /** Mnemonic key for the {@link ContextAction} */
    private static int CONTEXT_MNEMONIC = KeyEvent.VK_C;
    /** Mnemonic key for the {@link RegExprAction} */
    private static int REG_EXPR_MNEMONIC = KeyEvent.VK_P;
    /** Mnemonic key for the {@link FromFileAction} */
    private static int FILE_MNEMONIC = KeyEvent.VK_F;
    /** Mnemonic key for the {@link TraceAction} */
    private static int TRACE_MNEMONIC = KeyEvent.VK_T;
    /** Mnemonic key for the menu. */
    private static int MENU_MNEMONIC = KeyEvent.VK_S;

    /**
     * Abstract class that supports showing and hiding actions based on two
     * criteria:
     * <ul>
     * <li>A method {@link groove.gui.menu.ShowHideMenu.ShowHideAction#isInvolved(JCell)} to signal that a
     * certain cell is involved in the attempt to show or hide it;
     * <li>A show mode, which can be {@link #ADD_MODE} (the involved cells are
     * set to visible), {@link #HIDE_MODE} (the involved cells are hidden) or
     * {@link #ONLY_MODE} (the involved cells are shown and all others are
     * hidden).
     * </ul>
     */
    static abstract protected class ShowHideAction<G extends Graph> extends AbstractAction {
        /**
         * Constructs a nameless action.
         * @param jgraph the jgraph upon which this action works
         * @param showMode the show mode: one of {@link #ADD_MODE},
         *        {@link #HIDE_MODE} or {@link #ONLY_MODE}
         */
        protected ShowHideAction(JGraph<G> jgraph, int showMode, String name) {
            super(getModeName(showMode) + " " + name);
            this.jgraph = jgraph;
            this.showMode = showMode;
        }

        /**
         * Walks over the set of jgraph roots; for every root, if this action is
         * involved with it, show or hide it as determined by
         * <tt>isHidden(cell)</tt>.
         * @see #isInvolved
         * @see #isHiding
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Set<JCell<G>> hiddenCells = new HashSet<>();
            Set<JCell<G>> shownCells = new HashSet<>();
            for (JCell<G> jCell : this.jgraph.getModel()
                .getRoots()) {
                if (isHiding(jCell)) {
                    hiddenCells.add(jCell);
                } else if (isShowing(jCell)) {
                    shownCells.add(jCell);
                }
            }
            // if the main function is showing, hide first and then show
            if (getShowMode() != HIDE_MODE) {
                setHidden(hiddenCells, true);
                setHidden(shownCells, false);
            } else {
                // otherwise, show first and then hide
                setHidden(shownCells, false);
                setHidden(hiddenCells, true);
            }
            this.jgraph.repaint();
        }

        /**
         * Returns the show mode of this action.
         */
        public int getShowMode() {
            return this.showMode;
        }

        /**
         * Indicates whether (according to this action) a given cell should be
         * hidden. This is the case if the cell is involved (according to
         * {@link #isInvolved(JCell)}) and the show mode of this action is
         * {@link #HIDE_MODE}, or it is not involved and the show mode is
         * {@link #ONLY_MODE}.
         * @param jCell the cell for which the indication is given
         * @return <tt>true</tt> if (according to this action) <tt>cell</tt>
         *         should be hidden
         * @see #isInvolved(JCell)
         * @see #getShowMode()
         */
        protected boolean isHiding(JCell<G> jCell) {
            boolean involved = isInvolved(jCell);
            return (involved && getShowMode() == HIDE_MODE)
                || (!involved && getShowMode() == ONLY_MODE);
        }

        /**
         * Indicates whether (according to this action) a given cell should be
         * hidden. This is the case if the cell is involved (according to
         * {@link #isInvolved(JCell)}) and the show mode of this action is
         * {@link #ADD_MODE} or {@link #ONLY_MODE}.
         * @param jCell the cell for which the indication is given
         * @return <tt>true</tt> if (according to this action) <tt>cell</tt>
         *         should be hidden
         * @see #isInvolved(JCell)
         * @see #getShowMode()
         */
        protected boolean isShowing(JCell<G> jCell) {
            return isInvolved(jCell) && getShowMode() != HIDE_MODE;
        }

        /**
         * Convenience method to changes a set of jcells to hidden or visible in
         * the underlying jgraph.
         * @param cells the jcells to be changed
         * @param hidden <tt>true</tt> if the cells are to be changed to hidden
         */
        protected final void setHidden(Set<JCell<G>> cells, boolean hidden) {
            this.jgraph.changeGrayedOut(cells, hidden);
        }

        /**
         * Indicates whether a given jgraph cell is involved in this show/hide
         * action.
         * @param jCell the jgraph cell for which the involvement is to be
         *        decided
         * @return <tt>true</tt> if <tt>cell</tt> should be shown/hidden by this
         *         action
         */
        abstract protected boolean isInvolved(JCell<G> jCell);

        /** The jgraph upon which this menu works. */
        protected final JGraph<G> jgraph;

        /**
         * The show mode of this action.
         * @invariant <tt>showMode in ADD_MODE, HIDE_MODE, ONLY_MODE</tt>
         */
        protected final int showMode;
    }

    /**
     * Action that shows/hide all nodes and edges in the graph.
     */
    static protected class AllAction<G extends Graph> extends ShowHideAction<G> {
        /**
         * Constructs an instance of the action for a given j-graph, either for
         * showing or for hiding.
         * @param jgraph the underlying j-graph
         * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or
         *        {@link #ONLY_MODE}
         */
        protected AllAction(JGraph<G> jgraph, int showMode) {
            super(jgraph, showMode, ALL_ACTION_NAME);
            putValue(MNEMONIC_KEY, ALL_MNEMONIC);
        }

        /**
         * All cells are involved in this action.
         * @return <tt>true</tt> always
         */
        @Override
        protected boolean isInvolved(JCell<G> cell) {
            return true;
        }
    }

    /**
     * Action that inverts the shown/hidden nodes and edges in the graph.
     */
    static protected class InvertAction<G extends Graph> extends ShowHideAction<G> {
        /**
         * Constructs an instance of the action for a given j-graph, either for
         * showing or for hiding.
         * @param jgraph the underlying j-graph
         * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or
         *        {@link #ONLY_MODE}
         */
        protected InvertAction(JGraph<G> jgraph, int showMode) {
            super(jgraph, showMode, INVERT_ACTION_NAME);
        }

        /**
         * All cells are involved in this action.
         * @return <tt>true</tt> always
         */
        @Override
        protected boolean isInvolved(JCell<G> cell) {
            return !cell.isGrayedOut();
        }
    }

    /**
     * Action that shows all incident edges of non-hidden nodes, or hides all
     * endpoints of hidden edges.
     */
    static protected class ContextAction<G extends Graph> extends ShowHideAction<G> {
        /**
         * Constructs an instance of the action for a given j-graph, either for
         * showing or for hiding.
         * @param jgraph the underlying j-graph
         * @param showMode one of {@link #ADD_MODE} or
         *        {@link #ONLY_MODE}
         */
        protected ContextAction(JGraph<G> jgraph, int showMode) {
            super(jgraph, showMode, CONTEXT_ACTION_NAME);
            assert showMode != HIDE_MODE : "Hiding not defined for context";
            putValue(MNEMONIC_KEY, CONTEXT_MNEMONIC);
        }

        @Override
        protected boolean isInvolved(JCell<G> cell) {
            boolean result = false;
            if (cell instanceof JEdge) {
                JEdge<G> edge = (JEdge<G>) cell;
                JCell<G> sourceVertex = edge.getSourceVertex();
                assert sourceVertex != null; // model has been initialised by now
                JCell<G> targetVertex = edge.getTargetVertex();
                assert targetVertex != null; // model has been initialised by now
                Object[] selectedCellArray = this.jgraph.getSelectionCells();
                if (selectedCellArray.length == 0) {
                    result = !sourceVertex.isGrayedOut() || !targetVertex.isGrayedOut();
                } else {
                    Set<Object> selectedCells =
                        new HashSet<>(Arrays.asList(selectedCellArray));
                    result = selectedCells.contains(sourceVertex)
                        || selectedCells.contains(targetVertex);
                }
            }
            return result;
        }
    }

    /**
     * Action that shows/hides all nodes and edges with a given label.
     */
    static protected class LabelAction<G extends Graph> extends ShowHideAction<G> {
        /**
         * Creates a <tt>LabelAction</tt> that tests for an explicitly given
         * label.
         * @param jgraph the jgraph upon which this action works
         * @param showMode the show mode for this action
         * @param label the label on which this action should test; may not be
         *        <tt>null</tt>
         * @throws IllegalArgumentException if <tt>cell</tt> does not give rise
         *         to a valid label, i.e., <tt>getLabel(cell) == null</tt>
         */
        protected LabelAction(JGraph<G> jgraph, int showMode, Label label)
            throws IllegalArgumentException {
            super(jgraph, showMode, "");
            putValue(NAME, label.text()
                .length() == 0 ? Options.EMPTY_LABEL_TEXT
                    : HTMLConverter.HTML_TAG.on(label.toLine()
                        .toHTMLString()));
            this.label = label;
        }

        /**
         * Returns <tt>true</tt> if the property that <tt>cell</tt> contains the
         * label of this action equals the inclusion condition of this action.
         */
        @Override
        protected boolean isInvolved(JCell<G> cell) {
            // return getLabel(cell) != null && getLabel(cell).equals(label) ==
            // include;
            return cell.getKeys()
                .contains(this.label);
        }

        /**
         * The label on which this action selects.
         */
        private final Label label;
    }

    /**
     * Action that shows/hides elements on the basis of a regular expression
     * over edge labels.
     */
    static protected class RegExprAction<G extends Graph> extends ShowHideAction<G> {
        /**
         * Constructs an instance of the action for a given j-graph, either for
         * showing or for hiding.
         * @param jgraph the underlying j-graph
         * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or
         *        {@link #ONLY_MODE}
         */
        protected RegExprAction(JGraph<G> jgraph, int showMode) {
            super(jgraph, showMode, REGEXPR_ACTION_NAME);
            putValue(MNEMONIC_KEY, REG_EXPR_MNEMONIC);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Graph graph = this.jgraph.getModel()
                .getGraph();
            String exprText = exprDialog.showDialog(null);
            if (exprText != null) {
                try {
                    RegExpr expr = RegExpr.parse(exprText);
                    if (expr != null) {
                        if (this.calculator == null || this.calculator.getGraph() != graph) {
                            if (this.calculator != null) {
                                this.calculator.stopListening();
                            }
                            this.calculator = new RelationCalculator(graph, new NodeRelation());
                            this.calculator.startListening();
                        }
                        NodeRelation rel = expr.apply(this.calculator);
                        this.elementSet = rel.getSupport();
                    }
                    super.actionPerformed(evt);
                } catch (FormatException exc) {
                    JOptionPane.showMessageDialog(null,
                        "Error in regular expression '" + exprText + "': " + exc.getMessage());
                }
            }
        }

        @Override
        protected boolean isInvolved(JCell<G> cell) {
            Set<? extends Edge> edgesInCell = cell.getEdges();
            boolean edgeFound = false;
            Iterator<? extends Edge> edgeInCellIter = edgesInCell.iterator();
            while (!edgeFound && edgeInCellIter.hasNext()) {
                edgeFound = this.elementSet.contains(edgeInCellIter.next());
            }
            return edgeFound;
        }

        /**
         * The set of graph elements calculated as the result of the regular
         * expression.
         */
        private Collection<Element> elementSet;
        /**
         * The currently used relation factory in the regular expression
         * calculator.
         */
        private RelationCalculator calculator;

        private static FormulaDialog exprDialog =
            FormulaDialog.createStringDialog("Regular Expression: ");
    }

    /**
     * Show/hide action based on the currently selected cells. The action adds
     * the selection to the shown or hidden cells
     * @author Arend Rensink
     * @version $Revision$
     */
    static protected class SelectedAction<G extends Graph> extends ShowHideAction<G> {
        /**
         * Constructs an instance of the action for a given j-graph, either for
         * showing or for hiding.
         * @param jgraph the underlying j-graph
         * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or
         *        {@link #ONLY_MODE}
         */
        public SelectedAction(JGraph<G> jgraph, int showMode) {
            super(jgraph, showMode, SELECTED_ACTION_NAME);
            putValue(MNEMONIC_KEY, EMPHASIZED_MNEMONIC);
        }

        /**
         * This implementation returns the emphasis status of the cell in the
         * model.
         */
        @Override
        protected boolean isInvolved(JCell<G> jCell) {
            return this.jgraph.getSelectionModel()
                .isCellSelected(jCell);
        }
    }

    /**
     * Show/hide action based on a set of labels read from a text file. The text
     * file format is one label per line.
     * @author Eduardo Zambon
     */
    static protected class FromFileAction<G extends Graph> extends ShowHideAction<G> {
        /**
         * Constructs an instance of the action for a given j-graph.
         * @param jgraph the underlying j-graph
         * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or
         *        {@link #ONLY_MODE}
         */
        public FromFileAction(JGraph<G> jgraph, int showMode) {
            super(jgraph, showMode, FILE_ACTION_NAME);
            putValue(MNEMONIC_KEY, FILE_MNEMONIC);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            GrooveFileChooser fileChooser = GrooveFileChooser.getInstance(FileType.TEXT);
            int result = fileChooser.showOpenDialog(this.jgraph);
            if (result == JFileChooser.APPROVE_OPTION) {
                File labelsFile = fileChooser.getSelectedFile();
                String fileLine;
                Set<String> labels = new HashSet<>();
                try (BufferedReader in = new BufferedReader(new FileReader(labelsFile))) {
                    if (!in.ready()) {
                        throw new IOException();
                    }
                    while ((fileLine = in.readLine()) != null) {
                        labels.add(fileLine);
                    }
                } catch (IOException e) {
                    // Well, bad things can happen... :P Carry on.
                }
                this.labels = labels;
                super.actionPerformed(evt);
            }
        }

        /**
         * A cell is involved if it contains a label that is on the list of
         * labels read from the file.
         */
        @Override
        protected boolean isInvolved(JCell<G> jCell) {
            boolean result = false;
            for (Label label : jCell.getKeys()) {
                result = this.labels.contains(label.text());
                if (result) {
                    break;
                }
            }
            return result;
        }

        private Set<String> labels;
    }

    /**
     * Show/hide action based on a trace from start state to current state.
     * @author Eduardo Zambon
     */
    static protected class TraceAction extends ShowHideAction<GTS> {
        /**
         * Constructs an instance of the action for a given j-graph.
         * @param jgraph the underlying j-graph
         * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or
         *        {@link #ONLY_MODE}
         */
        public TraceAction(LTSJGraph jgraph, int showMode) {
            super(jgraph, showMode, TRACE_ACTION_NAME);
            putValue(MNEMONIC_KEY, TRACE_MNEMONIC);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            LTSJGraph jGraph = (LTSJGraph) this.jgraph;
            this.trace = jGraph.findTraces(Collections.singleton(jGraph.getActiveState()));
            if (jGraph.getModel() != null) {
                super.actionPerformed(evt);
            }
        }

        @Override
        protected boolean isInvolved(JCell<GTS> jCell) {
            return this.trace.contains(jCell);
        }

        private Set<LTSJCell> trace;
    }

    /**
     * A menu that creates, when it is selected, sub-items for all the labels
     * currently in the graph. The sub-items are <tt>LabelAction</tt> instances.
     * They are enabled only if the given action could effect a change upon
     * cells with that label. There are three modes by which cells are selected:
     * <i>only </i>, <i>also </i> or <i>except </i>.
     * <ul>
     * <li><i>Only </i> involves all cells: it shows/hides thse with the correct
     * label and hdes/shows the others.
     * <li><i>Also </i> involves only cells with the given label; they are
     * shown/hidden
     * <li><i>Except </i> involves only cells <i>not </i> with the given label;
     * they are shwn/hidden
     * </ul>
     */
    protected class LabelMenu extends JMenu {
        /**
         * Constructs an instance of the action, either for showing or for
         * hiding.
         * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or
         *        {@link #ONLY_MODE}
         */
        protected LabelMenu(int showMode) {
            super(getModeName(showMode) + " " + LABEL_MENU_NAME);
            this.showMode = showMode;
        }

        /**
         * This action builds the menu on-the-fly. It iterates ove the roots of
         * the jgraph, adding a <tt>LabelAction</tt> for every label of every
         * jcell thus found.
         */
        @Override
        public void menuSelectionChanged(boolean isIncluded) {
            if (isIncluded) {
                // now (re-)fill the menu
                removeAll();
                for (Label labelAction : getJGraph().getLabelTree()
                    .getLabels()) {
                    add(new LabelAction<>(getJGraph(), this.showMode, labelAction));
                }
            }
            super.menuSelectionChanged(isIncluded);
        }

        /**
         * Indicates whether the menu is for inclusion or exclusion.
         */
        private final int showMode;
    }

}
