// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: ShowHideMenu.java,v 1.4 2007-04-01 12:50:29 rensink Exp $
 */
package groove.gui;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphAdapter;
import groove.graph.GraphListener;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.rel.RegExpr;
import groove.rel.RelationCalculator;
import groove.rel.SupportedNodeRelation;
import groove.rel.SupportedSetNodeRelation;
import groove.util.FormatException;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

/**
 * Menu to control the visibility of nodes and edges in a jgraph.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class ShowHideMenu extends JMenu {
    /**
     * Show mode for a {@link ShowHideAction}: involved cells are set to visible.
     */
    static protected final int ADD_MODE = 0;

    /**
     * Show mode for a {@link ShowHideAction}: involved cells are hidden.
     */
    static protected final int HIDE_MODE = 1;

    /**
     * Show mode for a {@link ShowHideAction}: involved cells are set to visible, all others are hidden.
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
    /** Name of the action to process the currently selected elements. */
    static public final String SELECTED_ACTION_NAME = "Selected";
    /** Name of the action to process only the emphasised elements. */
    static public final String EMPHASIZED_ACTION_NAME = "Emphasized";
    /** Name of the action to process only the unselected elements. */
    static public final String UNSELECTED_ACTION_NAME = "Deselected";
    /** Name of the action to invert the hidden elements. */
    static public final String INVERT_ACTION_NAME = "Inverse";
    /** Name of the action to process the context of a given element. */
    static public final String CONTEXT_ACTION_NAME = "Context";
    /** Name of the action to process elements according to a regular expression. */
    static public final String REGEXPR_ACTION_NAME = "Pattern...";
    /** Name of the action to process elements by label. */
    static public final String LABEL_MENU_NAME = "Label";

    /**
     * Returns the name for a show mode.
     */
    static public String getModeName(int showMode) {
        switch (showMode) {
        case ADD_MODE : return ADD_MODE_NAME;
        case ONLY_MODE : return ONLY_MODE_NAME;
        default : return HIDE_MODE_NAME;
        }
    }

    /**
     * Constructs a display control menu, which either shows or hides nodes and edges based on
     * selection or labels.
     * @param jgraph the underlying jgraph of which the display should be controlled
     */
    public ShowHideMenu(JGraph jgraph) {
        super(Options.SHOW_HIDE_MENU_NAME);
        this.jgraph = jgraph;
        fillOutMenu(this.getPopupMenu());
    }

    /** Fills a given menu with actions to show and hide elements. */
    protected void fillOutMenu(JPopupMenu menu) {
        // show actions
        menu.add(createAllAction(ONLY_MODE));
        menu.add(createEmphasizedAction(ONLY_MODE));
        menu.add(createSelectedAction(ONLY_MODE, true));
        if (jgraph.getModel() instanceof GraphJModel) {
            menu.add(createShowRegExprAction(ONLY_MODE));
        }
        menu.addSeparator();
        menu.add(createEmphasizedAction(ADD_MODE));
        if (jgraph.getModel() instanceof GraphJModel) {
            menu.add(createAddRegExprAction(ADD_MODE));
        }
        menu.add(createContextAction(ADD_MODE));
        menu.add(createLabelMenu(ADD_MODE));
        menu.addSeparator();
        // hide actions
        menu.add(createAllAction(HIDE_MODE));
        menu.add(createEmphasizedAction(HIDE_MODE));
        menu.add(createSelectedAction(HIDE_MODE, true));
        if (jgraph.getModel() instanceof GraphJModel) {
            menu.add(createAddRegExprAction(HIDE_MODE));
        }
        menu.add(createLabelMenu(HIDE_MODE));
    }

    /**
     * Factory method for <tt>AllAction</tt>s.
     */
    protected ShowHideAction createAllAction(int showMode) {
        return new AllAction(jgraph, showMode);
    }

    /**
     * Factory method for <tt>SelectedAction</tt>s.
     */
    protected ShowHideAction createSelectedAction(int showMode, boolean selected) {
        return new SelectedAction(jgraph, showMode, selected);
    }

    /**
     * Factory method for <tt>InvertAction</tt>s.
     */
    protected ShowHideAction createInvertAction(int showMode) {
        return new InvertAction(jgraph, showMode);
    }

    /**
     * Factory method for <tt>RegExprAction</tt>s.
     */
    protected ShowHideAction createAddRegExprAction(int showMode) {
        return new RegExprAction(jgraph, showMode);
    }

    /**
     * Factory method for <tt>RegExprAction</tt>s.
     */
    protected ShowHideAction createShowRegExprAction(int showMode) {
        return new RegExprAction(jgraph, showMode);
    }

    /**
     * Factory method for <tt>ContextAction</tt>s.
     */
    protected ShowHideAction createContextAction(int showMode) {
        return new ContextAction(jgraph, showMode);
    }

    /**
     * Factory method for {@link ShowHideMenu.EmphasizedAction}s.
     */
    protected ShowHideAction createEmphasizedAction(int showMode) {
        return new EmphasizedAction(jgraph, showMode);
    }

    /**
     * Factory method for <tt>LabelAction</tt>s.
     */
    protected ShowHideAction createLabelAction(int showMode, String label) {
        return new LabelAction(jgraph, showMode, label);
    }

    /**
     * Factory method for the label sub-menu. To be overiden, e.g., if the cell labels should be
     * adapted.
     * @param showMode {@link #ADD_MODE} if the menu is for inclusion; {@link #HIDE_MODE} if it
     *        is for exclusion; {@link #ONLY_MODE} if it is for exclusive highlighting;
     * @return a new label menu
     * @see LabelMenu
     */
    protected JMenu createLabelMenu(int showMode) {
        return new LabelMenu(showMode);
    }

    /**
     * Abstract class that supports showing and hiding actions based on two criteria:
     * <ul>
     * <li>A method {@link ShowHideAction#isInvolved(JCell)} to signal that a certain cell is
     * involved in the attempt to show or hide it;
     * <li>A show mode, which can be {@link #ADD_MODE} (the involved cells are set to visible),
     *  {@link #HIDE_MODE} (the involved cells are hidden) or {@link #ONLY_MODE} (the 
     *  involved cells are shown and all others are hidden).
     * </ul>
     */
    static abstract protected class ShowHideAction extends AbstractAction {
        /**
         * Constructs a nemeless action.
         * @param jgraph the jgraph upon which this action works
         * @param showMode the show mode: one of {@link #ADD_MODE}, {@link #HIDE_MODE}
         * or {@link #ONLY_MODE}
         */
        protected ShowHideAction(JGraph jgraph, int showMode, String name) {
            super(getModeName(showMode)+" "+name);
            this.jgraph = jgraph;
            this.showMode = showMode;
        }

        /**
         * Walks over the set of jgraph roots; for every root, if this action is involved with it,
         * show or hide it as determined by <tt>isHidden(cell)</tt>.
         * @see #isInvolved
         * @see #isHiding
         */
        public void actionPerformed(ActionEvent e) {
            Set<JCell> hiddenCells = new HashSet<JCell>();
            Set<JCell> shownCells = new HashSet<JCell>();
            Object[] roots = jgraph.getRoots();
            for (int i = 0; i < roots.length; i++) {
                JCell jCell = (JCell) roots[i];
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
            jgraph.repaint();
        }

        /**
         * Returns the show mode of this action.
         */
        public int getShowMode() {
            return showMode;
        }

        /**
         * Indicates whether (according to this action) a given cell should be hidden.
         * This is the case if the cell is involved (according to {@link #isInvolved(JCell)})
         * and the show mode of this action is {@link #HIDE_MODE}, or it is not involved
         * and the show mode is {@link #ONLY_MODE}.
         * @param jCell the cell for which the indication is given
         * @return <tt>true</tt> if (according to this action) <tt>cell</tt> should be hidden
         * @see #isInvolved(JCell)
         * @see #getShowMode()
         */
        protected boolean isHiding(JCell jCell) {
            boolean involved = isInvolved(jCell);
            return (involved && getShowMode() == HIDE_MODE) || (!involved && getShowMode() == ONLY_MODE);
        }

        /**
         * Indicates whether (according to this action) a given cell should be hidden.
         * This is the case if the cell is involved (according to {@link #isInvolved(JCell)})
         * and the show mode of this action is {@link #ADD_MODE} or {@link #ONLY_MODE}.
         * @param jCell the cell for which the indication is given
         * @return <tt>true</tt> if (according to this action) <tt>cell</tt> should be hidden
         * @see #isInvolved(JCell)
         * @see #getShowMode()
         */
        protected boolean isShowing(JCell jCell) {
            return isInvolved(jCell) && getShowMode() != HIDE_MODE;
        }

        /**
         * Convenience method to changes a set of jcells to hidden or visible in the underlying jgraph.
         * @param cells the jcells to be changed
         * @param hidden <tt>true</tt> if the cells are to be changed to hidden
         */
        protected final void setHidden(Set<JCell> cells, boolean hidden) {
            jgraph.getModel().setHidden(cells, hidden);
        }

        /**
         * Convenienct method to indicate if a jcell is set to hidden in the underlying jgraph.
         * @param jCell the jcell to be tested
         * @return <tt>true</tt> if jcell is hidden in the underlying jgraph
         */
        protected final boolean isHidden(JCell jCell) {
            return jgraph.getModel().isHidden(jCell);
        }

        /**
         * Indicates whether a given jgraph cell is involved in this show/hide action.
         * @param jCell the jgraph cell for which the involvement is to be decided
         * @return <tt>true</tt> if <tt>cell</tt> should be shown/hidden by this action
         */
        abstract protected boolean isInvolved(JCell jCell);

        /** The jgraph upon which this menu works. */
        protected final JGraph jgraph;

        /** 
         * The show mode of this action.
         * @invariant <tt>showMode in ADD_MODE, HIDE_MODE, ONLY_MODE</tt>
         */
        protected final int showMode;
    }

    /**
     * Action that shows/hide all nodes and edges in the graph.
     */
    static protected class AllAction extends ShowHideAction {
    	/** 
    	 * Constructs an instance of the action for a given j-graph,
    	 * either for showing or for hiding.
    	 * @param jgraph the underlying j-graph
    	 * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or {@link #ONLY_MODE}
    	 */
        protected AllAction(JGraph jgraph, int showMode) {
            super(jgraph, showMode, ALL_ACTION_NAME);
        }

        /**
         * All cells are involved in this action.
         * @return <tt>true</tt> always
         */
        @Override
        protected boolean isInvolved(JCell cell) {
            return true;
        }
    }

    /**
     * Action that inverts the shown/hidden nodes and edges in the graph.
     */
    static protected class InvertAction extends ShowHideAction {
    	/** 
    	 * Constructs an instance of the action for a given j-graph,
    	 * either for showing or for hiding.
    	 * @param jgraph the underlying j-graph
    	 * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or {@link #ONLY_MODE}
    	 */
        protected InvertAction(JGraph jgraph, int showMode) {
            super(jgraph, showMode, INVERT_ACTION_NAME);
        }

        /**
         * All cells are involved in this action.
         * @return <tt>true</tt> always
         */
        @Override
        protected boolean isInvolved(JCell cell) {
            return !isHidden(cell);
        }
    }

    /**
     * Action that shows/hides all selected nodes and edges in the graph.
     */
    static protected class SelectedAction extends ShowHideAction {
    	/** 
    	 * Constructs an instance of the action for a given j-graph,
    	 * either for showing or for hiding and either for the selected
    	 * or for the unselected elements.
    	 * @param jgraph the underlying j-graph
    	 * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or {@link #ONLY_MODE}
    	 * @param selected <code>true</code> if this action instance is for the
    	 * selected elements.
    	 */
        protected SelectedAction(JGraph jgraph, int showMode, boolean selected) {
            super(jgraph, showMode, selected ? SELECTED_ACTION_NAME : UNSELECTED_ACTION_NAME);
            this.selected = selected;
        }

        @Override
        protected boolean isInvolved(JCell cell) {
            return jgraph.isCellSelected(cell) == selected;
        }

        /** Flag indicating if this action is for the selected elements. */
        private final boolean selected;
    }

    /**
     * Action that shows all incident edges of non-hidden nodes, or hides all endpoints of hidden
     * edges.
     */
    static protected class ContextAction extends ShowHideAction {
    	/** 
    	 * Constructs an instance of the action for a given j-graph,
    	 * either for showing or for hiding.
    	 * @param jgraph the underlying j-graph
    	 * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or {@link #ONLY_MODE}
    	 */
        protected ContextAction(JGraph jgraph, int showMode) {
            super(jgraph, showMode, CONTEXT_ACTION_NAME);
        }

        @Override
        protected boolean isInvolved(JCell cell) {
            Object[] selectedCellArray = jgraph.getSelectionCells();
            if (getShowMode() != HIDE_MODE && jgraph.isEdge(cell)) {
                DefaultEdge edge = (DefaultEdge) cell;
                JCell sourcePort = (JCell) ((DefaultPort) edge.getSource()).getParent();
                JCell targetPort = (JCell) ((DefaultPort) edge.getTarget()).getParent();
                boolean result;
                if (selectedCellArray.length == 0) {
                    result = !isHidden(sourcePort) || !isHidden(targetPort);
                } else {
                    Set<Object> selectedCells = new HashSet<Object>(Arrays.asList(selectedCellArray));
                    result = selectedCells.contains(sourcePort) || selectedCells.contains(targetPort);
                }
                return result;
            } else if (getShowMode() == HIDE_MODE && jgraph.isVertex(cell)) {
                DefaultPort port = (DefaultPort) ((DefaultGraphCell) cell).getChildAt(0);
                boolean hasHiddenIncidentEdge = false;
                Iterator<?> edgeIter = port.edges();
                while (!hasHiddenIncidentEdge && edgeIter.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edgeIter.next();
                    hasHiddenIncidentEdge = edge == cell;
                }
                return hasHiddenIncidentEdge;
            } else {
                return false;
            }
        }
    }

    /**
     * Action that shows/hides all nodes and edges with a given label.
     */
    static protected class LabelAction extends ShowHideAction {
        /**
         * Creates a <tt>LabelAction</tt> that tests for an explicitly given label.
         * @param jgraph the jgraph upon which this action works
         * @param showMode the show mode for this action
         * @param label the label on which this action should test; may not be <tt>null</tt>
         * @throws IllegalArgumentException if <tt>cell</tt> does not give rise to a valid label,
         *         i.e., <tt>getLabel(cell) == null</tt>
         */
        protected LabelAction(JGraph jgraph, int showMode, String label)
                throws IllegalArgumentException {
            super(jgraph, showMode, "");
            putValue(NAME, label.length() == 0 ? Options.EMPTY_LABEL_TEXT : label);
            this.label = label;
        }

        /**
         * Returns <tt>true</tt> if the property that <tt>cell</tt> contains the label of this
         * action equals the inclusion condition of this action.
         */
        @Override
        protected boolean isInvolved(JCell cell) {
            // return getLabel(cell) != null && getLabel(cell).equals(label) == include;
            return cell.getLabelSet().contains(label);
        }

        /**
         * The label on which this action selects.
         */
        private final String label;
    }

    /** Action that shows/hides elements on the basis of a regular expression
     * over edge labels.
     */
    static protected class RegExprAction extends ShowHideAction {
        /**
         * Relation factory that makes sure strings are correctly interpreted as edge labels.
         */
        public class Relation extends SupportedSetNodeRelation {
        	/** Constructs a relation for a given graph. */
            protected Relation(GraphShape graph) {
                super(graph);
            }

            @Override
            public Relation newInstance() {
                return new Relation(getGraph());
            }

            /**
             * This implementation uses a mapping from the label text to the edges
             * to make sure the user view of the label is correctly interpreted.
             */
            @Override
            protected Collection<? extends Edge> getRelatedSet(String label) {
                if (textEdgeMap == null) {
                    // since the labels may not be default labels
                    // (as in the case of LTS graphs)
                    // we have to convert the label map so that it maps label text instead
                    Map<Label,? extends Set<? extends Edge>> labelEdgeMap = getGraph().labelEdgeMap(2);
                    textEdgeMap = new HashMap<String,Set<Edge>>();
                    for (Map.Entry<Label,? extends Set<? extends Edge>> labelEdgeEntry: labelEdgeMap.entrySet()) {
                        String labelText = labelEdgeEntry.getKey().text();
                        Set<Edge> textLabelSet = getTextEdgeSet(labelText);
                        textLabelSet.addAll(labelEdgeEntry.getValue());
                    }
                    getGraph().addGraphListener(listener);
                }
                return textEdgeMap.get(label);
            }
            
            /**
             * Removes the graph listener from the underlying graph.
             */
            protected void unregister() {
                getGraph().removeGraphListener(listener);
            }
            
            /**
             * Returns the set associated with a given string from the {@link #textEdgeMap}.
             * Creates the set if necessary; the return value is never <code>null</code>.
             */
            private Set<Edge> getTextEdgeSet(String text) {
                Set<Edge> result = textEdgeMap.get(text);
                if (result == null) {
                    textEdgeMap.put(text, result = new HashSet<Edge>());
                }
                return result;
            }
            
            /** Mapping from label text to edges. */
            private Map<String,Set<Edge>> textEdgeMap;
            /** Graph listener to keep the {@link #textEdgeMap} up-to-date. */
            private GraphListener listener = new GraphAdapter() {
                @Override
                public void addUpdate(GraphShape graph, Edge edge) {
                	getTextEdgeSet(edge.label().text()).add(edge);
                }
            };
        }

    	/** 
    	 * Constructs an instance of the action for a given j-graph,
    	 * either for showing or for hiding.
    	 * @param jgraph the underlying j-graph
    	 * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or {@link #ONLY_MODE}
    	 */
        protected RegExprAction(JGraph jgraph, int showMode) {
            super(jgraph, showMode, REGEXPR_ACTION_NAME);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            GraphShape graph = ((GraphJModel) jgraph.getModel()).graph();
            String exprText = JOptionPane.showInputDialog("Regular Expression: ");
            if (exprText != null) {
                try {
                    RegExpr expr = RegExpr.parse(exprText);
                    if (expr != null) {
                        if (currentRelation == null || currentRelation.getGraph() != graph) {
                            if (currentRelation != null) {
                                currentRelation.unregister();
                            }
                            currentRelation = new Relation(graph);
                        }
                        RelationCalculator calculator = new RelationCalculator(currentRelation);
                        SupportedNodeRelation rel = (SupportedNodeRelation) expr.apply(calculator);
                        elementSet = rel.getSupport();
                    }
                    super.actionPerformed(evt);
                } catch (FormatException exc) {
                    JOptionPane.showMessageDialog(null, "Error in regular expression '" + exprText
                            + "': " + exc.getMessage());
                }
            }
        }

        @Override
        protected boolean isInvolved(JCell cell) {
            Set<? extends Edge> edgesInCell;
            if (cell instanceof GraphJEdge) {
                edgesInCell = ((GraphJEdge) cell).getEdgeSet();
            } else {
                edgesInCell = ((GraphJVertex) cell).getSelfEdgeSet();
            }
            boolean edgeFound = false;
            Iterator<? extends Edge> edgeInCellIter = edgesInCell.iterator();
            while (!edgeFound && edgeInCellIter.hasNext()) {
                edgeFound = elementSet.contains(edgeInCellIter.next());
            }
            return edgeFound;
        }

        /** The set of graph elements calculated as the result of the regular expression. */
        private Collection<Element> elementSet;
        /** The currently used relation factory in the regular expression calculator. */
        private Relation currentRelation;
    }

    /**
     * Show/hide action based on the currently emphasized cells. The action adds the selection to
     * the shown or hidden cells
     * @author Arend Rensink
     * @version $Revision: 1.4 $
     */
    static protected class EmphasizedAction extends ShowHideAction {
    	/** 
    	 * Constructs an instance of the action for a given j-graph,
    	 * either for showing or for hiding.
    	 * @param jgraph the underlying j-graph
    	 * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or {@link #ONLY_MODE}
    	 */
        public EmphasizedAction(JGraph jgraph, int showMode) {
            super(jgraph, showMode, EMPHASIZED_ACTION_NAME);
        }

        /**
         * This implementation returns the emphasis status of the cell in the model.
         */
        @Override
        protected boolean isInvolved(JCell jCell) {
            return jgraph.getModel().isEmphasized(jCell);
        }
    }

    /**
     * A menu that creates, when it is selected, sub-items for all the labels currently in the
     * graph. The sub-items are <tt>LabelAction</tt> instances. They are enabled only if the given
     * action could effect a change upon cells with that label. There are three modes by which cells
     * are selected: <i>only </i>, <i>also </i> or <i>except </i>.
     * <ul>
     * <li><i>Only </i> involves all cells: it shows/hides thse with the correct label and
     * hdes/shows the others.
     * <li><i>Also </i> involves only cells with the given label; they are shown/hidden
     * <li><i>Except </i> involves only cells <i>not </i> with the given label; they are
     * shwn/hidden
     * </ul>
     */
    protected class LabelMenu extends JMenu {
    	/** 
    	 * Constructs an instance of the action,
    	 * either for showing or for hiding.
    	 * @param showMode one of {@link #ADD_MODE}, {@link #HIDE_MODE} or {@link #ONLY_MODE}
    	 */
        protected LabelMenu(int showMode) {
            super(getModeName(showMode) + " " + LABEL_MENU_NAME);
            this.showMode = showMode;
        }

        /**
         * This action builds the menu on-the-fly. It iterates ove the roots of the jgraph, adding a
         * <tt>LabelAction</tt> for every label of every jcell thus found.
         */
        @Override
        public void menuSelectionChanged(boolean isIncluded) {
            if (isIncluded) {
                // now (re-)fill the menu
                removeAll();
                for (String labelAction: jgraph.getLabelList().getLabels()) {
                    add(new LabelAction(jgraph, showMode, labelAction));
                }
            }
            super.menuSelectionChanged(isIncluded);
        }
        /**
         * Indicates whether the menu is for inclusion or exclusion.
         */
        private final int showMode;
    }

    /** The jgraph upon which this menu works. */
    private final JGraph jgraph;
}