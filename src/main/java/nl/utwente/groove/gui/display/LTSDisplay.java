/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.display;

import static nl.utwente.groove.gui.SimulatorModel.Change.GRAMMAR;
import static nl.utwente.groove.gui.SimulatorModel.Change.GTS;
import static nl.utwente.groove.gui.SimulatorModel.Change.MATCH;
import static nl.utwente.groove.gui.SimulatorModel.Change.STATE;
import static nl.utwente.groove.gui.SimulatorModel.Change.TRACE;
import static nl.utwente.groove.gui.jgraph.JGraphMode.PAN_MODE;
import static nl.utwente.groove.gui.jgraph.JGraphMode.SELECT_MODE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.jgraph.JAttr;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JGraphMode;
import nl.utwente.groove.gui.jgraph.LTSJEdge;
import nl.utwente.groove.gui.jgraph.LTSJGraph;
import nl.utwente.groove.gui.jgraph.LTSJModel;
import nl.utwente.groove.gui.jgraph.LTSJVertex;
import nl.utwente.groove.gui.list.ErrorListPanel;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.Status.Flag;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 *
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class LTSDisplay extends Display implements SimulatorListener {
    /** Creates a LTS panel for a given simulator. */
    public LTSDisplay(Simulator simulator) {
        super(simulator, DisplayKind.LTS);
        setStateBound(100);
    }

    @Override
    protected void buildDisplay() {
        setLayout(new BorderLayout());
        JToolBar toolBar = Options.createToolBar();
        fillToolBar(toolBar);
        add(toolBar, BorderLayout.NORTH);
        add(getMainPanel());
    }

    @Override
    protected void installListeners() {
        getJGraph().addMouseListener(new MyMouseListener());
        getSimulatorModel().addListener(this, GRAMMAR, GTS, TRACE, STATE, MATCH);
    }

    @Override
    protected ListPanel createListPanel() {
        return null;
    }

    @Override
    protected JTree createList() {
        return null;
    }

    @Override
    protected JToolBar createListToolBar() {
        return null;
    }

    @Override
    protected JComponent createInfoPanel() {
        var labelTree = getLabelTree();
        final TitledPanel result = new TitledPanel("Transition labels", labelTree, null, true);
        result.setEnabledBackground(JAttr.STATE_BACKGROUND);
        getJGraph().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("background") && evt.getNewValue() != null) {
                    result.setEnabledBackground((Color) evt.getNewValue());
                }
            }
        });
        return result;
    }

    private void fillToolBar(JToolBar result) {
        result.removeAll();
        result.add(getActions().getExplorationDialogAction());
        result.addSeparator();
        result.add(getActions().getStartSimulationAction());
        result.add(getActions().getApplyMatchAction());
        result.add(getActions().getAnimateAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        result.addSeparator();
        result.add(getFilterPanel());
        result.add(getBoundSpinnerPanel());
        result.add(Box.createGlue());
    }

    private JPanel getFilterPanel() {
        if (this.filterPanel == null) {
            final JPanel result = this.filterPanel = new JPanel();
            result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
            result.add(Box.createRigidArea(new Dimension(5, 0)));
            result.add(new JLabel("Filter: "));
            result.add(Box.createRigidArea(new Dimension(5, 0)));
            result.add(getFilterChooser());
            result.setBorder(null);
        }
        return this.filterPanel;
    }

    private JPanel filterPanel;

    private JComboBox<Filter> getFilterChooser() {
        if (this.filterChooser == null) {
            this.filterListening = true;
            final JComboBox<Filter> result = this.filterChooser = new JComboBox<>(Filter.values());
            result.setMaximumSize(new Dimension(result.getPreferredSize().width, 1000));
            result.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (LTSDisplay.this.filterListening) {
                        doFilterLTS();
                    }
                }
            });
        }
        return this.filterChooser;
    }

    /** Adds or removes an item for {@link Filter#RESULT} to the filter chooser. */
    private void setFilterResultItem(boolean hasResults) {
        JComboBox<Filter> chooser = getFilterChooser();
        if (hasResults != (chooser.getItemCount() == Filter.values().length)) {
            this.filterListening = false;
            boolean resultSelected = chooser.getSelectedIndex() == Filter.RESULT.ordinal();
            if (hasResults) {
                chooser.addItem(Filter.RESULT);
            } else {
                chooser.removeItemAt(Filter.RESULT.ordinal());
            }
            if (resultSelected) {
                chooser.setSelectedIndex(Filter.NONE.ordinal());
            }
            getJGraph().setFilter(Filter.NONE);
            this.filterListening = true;
        }
    }

    private boolean filterListening;

    /** Returns the currently selected filter value. */
    public Filter getFilter() {
        return (Filter) getFilterChooser().getSelectedItem();
    }

    private JComboBox<Filter> filterChooser;

    private JPanel getBoundSpinnerPanel() {
        JPanel result = this.boundSpinnerPanel;
        if (result == null) {
            result = new JPanel();
            result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
            result.add(Box.createRigidArea(new Dimension(5, 0)));
            result.add(new JLabel("Show states up to"));
            result.add(Box.createRigidArea(new Dimension(5, 0)));
            result.add(getBoundSpinner());
            result.add(Box.createGlue());
            this.boundSpinnerPanel = result;
        }
        return this.boundSpinnerPanel;
    }

    private JPanel boundSpinnerPanel;

    private JSpinner getBoundSpinner() {
        if (this.boundSpinner == null) {
            this.boundSpinner = new JSpinner(getBoundSpinnerModel());
            this.boundSpinner.setMaximumSize(new Dimension(10, 100));
            this.boundSpinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (getJModel() != null) {
                        int newBound = getStateBound();
                        int oldBound = getJModel().setStateBound(newBound);
                        if (oldBound != newBound) {
                            if (getJModel().reloadGraph()) {
                                getJGraph().refreshFiltering();
                                getJGraph().refreshActive();
                                getJGraph().refreshAllCells();
                                getJGraph().doLayout(false);
                                getJGraph().scrollToActive();
                            }
                            refreshBackground();
                        }
                    }
                }
            });
        }
        return this.boundSpinner;
    }

    private JSpinner boundSpinner;

    private SpinnerNumberModel getBoundSpinnerModel() {
        if (this.boundSpinnerModel == null) {
            this.boundSpinnerModel = new SpinnerNumberModel();
            this.boundSpinnerModel.setMinimum(100);
            this.boundSpinnerModel.setMaximum(100000);
            this.boundSpinnerModel.setStepSize(100);
            this.boundSpinnerModel.setValue(100);
        }
        return this.boundSpinnerModel;
    }

    private SpinnerNumberModel boundSpinnerModel;

    /** Sets the maximum state number to be displayed. */
    public void setStateBound(int bound) {
        getBoundSpinnerModel().setValue(bound);
    }

    /** Retrieves the maximum state number to be displayed. */
    public int getStateBound() {
        return (Integer) getBoundSpinnerModel().getValue();
    }

    /* Also changes the enabled status of the spinner. */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getBoundSpinner().setEnabled(enabled);
        refreshBackground();
    }

    /**
     * Shows a given counterexample by emphasising the states in the LTS panel.
     * Returns a message to be displayed in a dialog.
     * @param counterExamples the collection of states that do not satisfy the
     *        property verified
     * @param showTransitions flag to indicate that the canonical incoming transition
     * should also be highlighted.
     */
    public void emphasiseStates(Collection<GraphState> counterExamples, boolean showTransitions) {
        if (getJModel() == null || counterExamples.isEmpty()) {
            return;
        }
        Set<JCell<@NonNull GTS>> jCells = new HashSet<>();
        Iterator<GraphState> stateIter = counterExamples.iterator();
        GraphState current = stateIter.next();
        while (current != null) {
            jCells.add(getJModel().getJCellForNode(current));
            GraphState next = stateIter.hasNext()
                ? stateIter.next()
                : null;
            if (next != null && showTransitions) {
                for (GraphTransition trans : current
                    .getTransitions(getJGraph().getTransitionClass())) {
                    if (trans.target() == next) {
                        jCells.add(getJModel().getJCellForEdge(trans));
                        break;
                    }
                }
            }
            current = next;
        }
        getJGraph().setSelectionCells(jCells.toArray());
    }

    /** Creates a panel consisting of the error panel and the status bar. */
    private JSplitPane getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel
                = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getGraphPanel(), getErrorPanel());
            this.mainPanel.setDividerSize(1);
            this.mainPanel.setContinuousLayout(true);
            this.mainPanel.setResizeWeight(0.9);
            this.mainPanel.resetToPreferredSizes();
            this.mainPanel.setBorder(null);
        }
        return this.mainPanel;
    }

    /** Panel containing the LTS graph panel and error panel. */
    private JSplitPane mainPanel;

    /** Returns the LTS graph panel on this display. */
    public LTSGraphPanel getGraphPanel() {
        LTSGraphPanel result = this.graphPanel;
        if (result == null) {
            result = this.graphPanel = new LTSGraphPanel(getJGraph());
            result.initialise();
        }
        return result;
    }

    private LTSGraphPanel graphPanel;

    /** Lazily creates and returns the error panel. */
    private nl.utwente.groove.gui.list.ListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            this.errorPanel = new ErrorListPanel("State errors");
            this.errorPanel.addSelectionListener(createErrorListener());
        }
        return this.errorPanel;
    }

    /** Panel displaying format error messages. */
    private nl.utwente.groove.gui.list.ListPanel errorPanel;

    private PropertyChangeListener createErrorListener() {
        return evt -> {
            if (evt.getNewValue() instanceof FormatError error) {
                getSimulatorModel().setState(error.getState());
                getSimulatorModel().setDisplay(DisplayKind.STATE);
                var stateDisplay = (StateDisplay) getSimulator()
                    .getDisplaysPanel()
                    .getDisplay(DisplayKind.STATE);
                stateDisplay.selectError(error);
            }
        };
    }

    /**
     * Displays a list of errors, or hides the error panel if the list is empty.
     */
    final private void updateErrors() {
        FormatErrorSet errors;
        GTS gts = getJModel() == null
            ? null
            : getJModel().getGraph();
        if (gts == null) {
            errors = new FormatErrorSet();
        } else {
            errors = gts.getErrors();
        }
        getErrorPanel().setEntries(errors.get());
        if (getErrorPanel().isVisible()) {
            getMainPanel().setBottomComponent(getErrorPanel());
            getMainPanel().setDividerSize(1);
            getMainPanel().resetToPreferredSizes();
        } else {
            getMainPanel().remove(getErrorPanel());
            getMainPanel().setDividerSize(0);
        }
    }

    /** Returns the LTS' JGraph. */
    public LTSJGraph getJGraph() {
        LTSJGraph result = this.jGraph;
        if (result == null) {
            result = this.jGraph = new LTSJGraph(getSimulator()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (getSimulatorModel().hasAbsentState()) {
                        JAttr.paintHatch(this, g);
                    }
                }
            };
            result.setLabelTree(getLabelTree());
            //result.addProgressObserver(new ProgressObserver());
        }
        return result;
    }

    private LTSJGraph jGraph;

    /** Returns the model of the LTS' JGraph. */
    public LTSJModel getJModel() {
        return getJGraph().getModel();
    }

    private LabelTree<@NonNull GTS> getLabelTree() {
        var result = this.labelTree;
        if (result == null) {
            result = this.labelTree = new LabelTree<>(getJGraph(), true) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (getSimulatorModel().hasAbsentState()) {
                        JAttr.paintHatch(this, g);
                    }
                }
            };
        }
        return result;
    }

    /** The tree component showing (and allowing filtering of) the transitions in the LTS. */
    private LabelTree<@NonNull GTS> labelTree;

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (changes.contains(GTS) || changes.contains(GRAMMAR) || changes.contains(TRACE)) {
            GTS gts = source.getGTS();
            if (gts == null) {
                getJGraph().setModel(null);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GrammarModel grammar = getSimulatorModel().getGrammar();
                        if (grammar != null && grammar.getErrors().isEmpty()) {
                            getActions().getStartSimulationAction().execute();
                        }
                    }
                });
            } else {
                LTSJModel ltsModel;
                boolean isNew = gts != oldModel.getGTS();
                if (isNew) {
                    ltsModel = (LTSJModel) getJGraph().newModel();
                    getJGraph().setFilter(getFilter());
                    ltsModel.setStateBound(getStateBound());
                    ltsModel.loadGraph(gts);
                    getJGraph().setModel(ltsModel);
                } else {
                    ltsModel = getJModel();
                    ltsModel.loadGraph(gts);
                    //ltsModel.refreshVisuals();
                }
                GraphState state = source.getState();
                GraphTransition transition = source.getTransition();
                getJGraph().setActive(state, transition);
                getJGraph().doLayout(isNew);
                setEnabled(true);
                getJGraph().scrollToActive();
                setFilterResultItem(source.hasExploreResult());
                updateStatus(gts);
            }
            if (gts != oldModel.getGTS()) {
                if (oldModel.getGTS() != null) {
                    oldModel.getGTS().removeLTSListener(this.ltsListener);
                }
                if (gts != null) {
                    gts.addLTSListener(this.ltsListener);
                    updateStatus(gts);
                }
            }
            updateErrors();
        }
        if (changes.contains(STATE) || changes.contains(MATCH)) {
            if (getJModel() != null) {
                GraphState state = source.getState();
                var error = state != null && state.isError();
                var internal = state != null && state.isInner();
                getJGraph().setBackground(JAttr.getStateBackground(error, internal));
                GraphTransition transition = source.getTransition();
                if (getJGraph().setActive(state, transition)) {
                    getJGraph().doLayout(false);
                }
                getJGraph().scrollToActive();
            }
        }
    }

    /**
     * Toggles the filtering of the LTS display.
     */
    public void doFilterLTS() {
        if (getJGraph().setFilter(getFilter())) {
            boolean layout = getJGraph().refreshFiltering();
            layout |= getJGraph().refreshActive();
            getJGraph().refreshAllCells();
            if (layout) {
                getJGraph().doLayout(false);
            }
            setEnabled(true);
            getJGraph().scrollToActive();
        }
    }

    /**
     * The LTS listener permanently associated with this display.
     */
    private final MyLTSListener ltsListener = new MyLTSListener();

    /**
     * Refreshes the background colour, based on the question whether the LTS is
     * filtered or incompletely displayed.
     */
    public void refreshBackground() {
        Color background = getJGraph().isComplete()
            ? JAttr.STATE_BACKGROUND
            : JAttr.FILTER_BACKGROUND;
        getGraphPanel().setEnabledBackground(background);
        ((NumberEditor) getBoundSpinner().getEditor())
            .getTextField()
            .setBackground(isEnabled()
                ? background
                : null);
    }

    @Override
    public void doRepeat() {
        var jGraph = getJGraph();
        if (jGraph != null) {
            jGraph.scrollToNextSelectedRoot();
        }
    }

    /** Returns an LTS display for a given simulator. */
    public static LTSDisplay newInstance(Simulator simulator) {
        LTSDisplay result = new LTSDisplay(simulator);
        result.buildDisplay();
        return result;
    }

    /**
     * Listener that makes sure the panel status gets updated when the LYS is
     * extended.
     */
    private class MyLTSListener implements GTSListener {
        /** Empty constructor with the correct visibility. */
        MyLTSListener() {
            // empty
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            assert gts == getSimulatorModel().getGTS() : "I want to listen only to my lts";
            updateStatus(gts);
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            assert gts == getSimulatorModel().getGTS() : "I want to listen only to my lts";
            updateStatus(gts);
        }

        /**
         * If a state is closed, its background should be reset.
         */
        @Override
        public void statusUpdate(GTS gts, GraphState closed, int change) {
            assert gts == getSimulatorModel().getGTS() : "I want to listen only to my lts";
            if (Flag.ERROR.test(change)) {
                updateErrors();
            }
            updateStatus(gts);
        }
    }

    /**
     * Writes a line to the status bar.
     */
    private void updateStatus(GTS gts) {
        StringBuilder text = new StringBuilder();
        if (gts == null) {
            text.append("No start state loaded");
        } else {
            int stateCount = gts.getStateCount();
            text.append("Currently explored: ");
            text.append(stateCount);
            text.append(" states");
            boolean brackets = false;
            if (gts.hasOpenStates()) {
                if (brackets) {
                    text.append(", ");
                } else {
                    text.append(" (");
                    brackets = true;
                }
                text.append(gts.getOpenStateCount() + " open");
            }
            if (gts.hasFinalStates()) {
                if (brackets) {
                    text.append(", ");
                } else {
                    text.append(" (");
                    brackets = true;
                }
                text.append(gts.getFinalStateCount() + " final");
            }
            if (getSimulatorModel().hasExploreResult()) {
                if (brackets) {
                    text.append(", ");
                } else {
                    text.append(" (");
                    brackets = true;
                }
                int c = getSimulatorModel().getExploreResult().size();
                text.append(c + " result");
            }
            if (gts.hasErrorStates()) {
                if (brackets) {
                    text.append(", ");
                } else {
                    text.append(" (");
                    brackets = true;
                }
                text.append(gts.getErrorStateCount() + " error");
            }
            if (brackets) {
                text.append(")");
            }
            text.append(", ");
            text.append(gts.getTransitionCount());
            text.append(" transitions");
        }
        getGraphPanel().getStatusLabel().setText(text.toString());
    }

    /**
     * Mouse listener that creates the popup menu and switches the view to the
     * rule panel on double-clicks.
     */
    private class MyMouseListener extends DismissDelayer {
        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            super(LTSDisplay.this);
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (getJGraph().getMode() == SELECT_MODE && evt.getButton() == MouseEvent.BUTTON1) {
                if (!isEnabled() && getActions().getStartSimulationAction().isEnabled()) {
                    getActions().getStartSimulationAction().execute();
                } else {
                    // scale from screen to model
                    java.awt.Point loc = evt.getPoint();
                    // find cell in model coordinates
                    var cell = getJGraph().getFirstCellForLocation(loc.x, loc.y);
                    var ctrl = (evt.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                    if (cell instanceof LTSJEdge) {
                        GraphTransition trans = ((LTSJEdge) cell).getEdge();
                        getSimulatorModel().setTransition(trans);
                        if (ctrl) {
                            getSimulatorModel().setDisplay(DisplayKind.STATE);
                        }
                    } else if (cell instanceof LTSJVertex) {
                        GraphState node = ((LTSJVertex) cell).getNode();
                        getSimulatorModel().setState(node);
                        if (evt.getClickCount() == 2) {
                            getActions().getExploreAction().doExploreState();
                        } else if (ctrl) {
                            getSimulatorModel().setDisplay(DisplayKind.STATE);
                        }
                    }
                }
            }
        }
    }

    /**
     * Window that displays and controls the LTS.
     * @author Arend Rensink
     * @version $Revision$
     */
    public class LTSGraphPanel extends JGraphPanel<GTS> {
        /** Creates a LTS panel for a given simulator. */
        public LTSGraphPanel(LTSJGraph jGraph) {
            super(jGraph);
            getJGraph().setToolTipEnabled(true);
            setEnabledBackground(JAttr.STATE_BACKGROUND);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            getJGraph().getModeAction(SELECT_MODE).setEnabled(enabled);
            getJGraph().getModeAction(PAN_MODE).setEnabled(enabled);
            if (enabled) {
                getJGraph().getModeButton(SELECT_MODE).doClick();
            }
            LTSDisplay.this.setEnabled(enabled);
        }
    }
}
