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
import static nl.utwente.groove.gui.view.GraphViewMode.PAN_MODE;
import static nl.utwente.groove.gui.view.GraphViewMode.SELECT_MODE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.view.GraphCanvas.Overlay;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.LTSGraphCanvas;
import nl.utwente.groove.gui.view.LTSGraphViewController;
import nl.utwente.groove.gui.view.LTSGraphViewModel;
import nl.utwente.groove.gui.view.LTSViewEdge;
import nl.utwente.groove.gui.view.LTSViewVertex;
import nl.utwente.groove.gui.list.ErrorEntry;
import nl.utwente.groove.gui.list.ErrorListPanel;
import nl.utwente.groove.gui.tree.LTSTree;
import nl.utwente.groove.lts.ExploreResult;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.Status.Flag;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 *
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class LTSDisplay extends Display
    implements SimulatorListener, GraphDisplay<@NonNull GTS> {
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
        getCanvas().getComponent().addMouseListener(new MyMouseListener());
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
        final TitledPanel result = new TitledPanel("LTS labels", labelTree, null, true);
        result.setEnabledBackground(Values.STATE_BACKGROUND);
        getCanvas().getComponent().addPropertyChangeListener(new PropertyChangeListener() {
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
        result.add(getController().getModeButton(GraphViewMode.SELECT_MODE));
        result.add(getController().getModeButton(GraphViewMode.PAN_MODE));
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
            getController().setFilter(Filter.NONE);
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
                    var viewModel = getViewModel();
                    if (viewModel != null) {
                        int newBound = getStateBound();
                        int oldBound = viewModel.setStateBound(newBound);
                        if (oldBound != newBound) {
                            if (viewModel.reloadGraph()) {
                                getController().refreshFiltering();
                                getController().refreshActive();
                                getCanvas().refreshAll(true);
                                getController().doLayout(false);
                                getController().scrollToActive();
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
        var viewModel = getViewModel();
        if (viewModel == null || counterExamples.isEmpty()) {
            return;
        }
        Set<ViewCell<@NonNull GTS>> jCells = new HashSet<>();
        Iterator<GraphState> stateIter = counterExamples.iterator();
        GraphState current = stateIter.next();
        while (current != null) {
            jCells.add(viewModel.getJCellForNode(current));
            GraphState next = stateIter.hasNext()
                ? stateIter.next()
                : null;
            if (next != null && showTransitions) {
                for (GraphTransition trans : current
                    .getTransitions(getController().getTransitionClass())) {
                    if (trans.target() == next) {
                        jCells.add(viewModel.getJCellForEdge(trans));
                        break;
                    }
                }
            }
            current = next;
        }
        getCanvas().select(jCells);
    }

    /**
     * Shows a given exploration result by emphasising its states and
     * transitions in the LTS panel.
     * In contrast to {@link #emphasiseStates}, the transitions of the result
     * are emphasised precisely as recorded, rather than being re-derived from
     * consecutive states.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    public void emphasiseResult(ExploreResult result) {
        var viewModel = getViewModel();
        if (viewModel == null) {
            return;
        }
        Set<ViewCell<@NonNull GTS>> jCells = new HashSet<>();
        for (GraphState state : result.getStates()) {
            var jCell = viewModel.getJCellForNode(state);
            if (jCell != null) {
                jCells.add(jCell);
            }
        }
        for (GraphTransition trans : result.getTransitions()) {
            var jCell = viewModel.getJCellForEdge(trans);
            if (jCell != null) {
                jCells.add(jCell);
            }
        }
        getCanvas().select(jCells);
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
    @Override
    public LTSGraphPanel getGraphPanel() {
        LTSGraphPanel result = this.graphPanel;
        if (result == null) {
            result = this.graphPanel = new LTSGraphPanel(getCanvas());
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
            if (evt.getNewValue() instanceof ErrorEntry entry) {
                var error = entry.getError();
                getSimulatorModel()
                    .setState(error
                        .getContext(GraphState.class)
                        .stream()
                        .findFirst()
                        .orElse(null));
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
        var viewModel = getViewModel();
        GTS gts = viewModel == null
            ? null
            : viewModel.getGraph();
        if (gts == null) {
            errors = new FormatErrorSet();
        } else {
            errors = gts.getErrors();
        }
        getErrorPanel().setEntries(ErrorEntry.wrap(errors.get()));
        if (getErrorPanel().isVisible()) {
            getMainPanel().setBottomComponent(getErrorPanel());
            getMainPanel().setDividerSize(1);
            getMainPanel().resetToPreferredSizes();
        } else {
            getMainPanel().remove(getErrorPanel());
            getMainPanel().setDividerSize(0);
        }
    }

    /** Returns the LTS canvas, created by its controller on first request. */
    @Override
    public LTSGraphCanvas getCanvas() {
        return getController().getCanvas();
    }

    /** Returns the controller of the LTS graph view, creating it on first request. */
    @Override
    public LTSGraphViewController getController() {
        LTSGraphViewController result = this.controller;
        if (result == null) {
            result = this.controller = new LTSGraphViewController(getSimulator());
            result.setLabelTree(getLabelTree());
        }
        return result;
    }

    /** The controller of the LTS graph view. */
    private LTSGraphViewController controller;

    /** Returns the view model currently shown on the LTS canvas, if any. */
    @Override
    public LTSGraphViewModel getViewModel() {
        return getCanvas().getViewModel();
    }

    private LTSTree getLabelTree() {
        var result = this.labelTree;
        if (result == null) {
            result = this.labelTree = new LTSTree(getCanvas());
        }
        return result;
    }

    /** The tree component showing (and allowing filtering of) the transitions in the LTS. */
    private LTSTree labelTree;

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        getCanvas().setOverlay(source.hasAbsentState()
            ? Overlay.HATCHED
            : Overlay.NONE);
        if (changes.contains(GTS) || changes.contains(GRAMMAR) || changes.contains(TRACE)) {
            GTS gts = source.getGTS();
            if (gts == null) {
                getCanvas().setViewModel(null);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GrammarModel grammar = getSimulatorModel().getGrammar();
                        if (grammar != null && !grammar.hasErrors()) {
                            getActions().getStartSimulationAction().execute();
                        }
                    }
                });
            } else {
                LTSGraphViewModel ltsModel;
                boolean isNew = gts != oldModel.getGTS();
                if (isNew) {
                    ltsModel = getCanvas().newViewModel();
                    getController().setFilter(getFilter());
                    ltsModel.setStateBound(getStateBound());
                    ltsModel.loadGraph(gts);
                    getCanvas().setViewModel(ltsModel);
                } else {
                    ltsModel = getViewModel();
                    assert ltsModel != null; // the GTS was shown before, so its model exists
                    ltsModel.loadGraph(gts);
                    //ltsModel.refreshVisuals();
                }
                GraphState state = source.getState();
                GraphTransition transition = source.getTransition();
                getController().setActive(state, transition);
                setFilterResultItem(source.hasExploreResult());
                var lastExploreType = source.getLastExploreType();
                if (changes.contains(GTS) && source.hasExploreResult()
                    && lastExploreType != null && lastExploreType.presentsResultAsTraces()) {
                    // switch the filter to the result view. The decision is
                    // about the run that produced the result, so it keys on
                    // that run's type, not on the saved exploration.
                    // The chooser listener is suppressed and the filter applied
                    // by hand, so that the layout below runs only once, on the
                    // already-filtered graph
                    this.filterListening = false;
                    getFilterChooser().setSelectedItem(Filter.RESULT);
                    this.filterListening = true;
                    if (getController().setFilter(getFilter())) {
                        getController().refreshFiltering();
                        getController().refreshActive();
                        getCanvas().refreshAll(false);
                    }
                }
                getController().doLayout(isNew);
                setEnabled(true);
                getController().scrollToActive();
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
            if (getViewModel() != null) {
                GraphState state = source.getState();
                var error = state != null && state.isError();
                var internal = state != null && state.isInner();
                getCanvas().setBackground(Values.getStateBackground(error, internal));
                GraphTransition transition = source.getTransition();
                if (getController().setActive(state, transition)) {
                    getController().doLayout(false);
                }
                getController().scrollToActive();
            }
        }
    }

    /**
     * Toggles the filtering of the LTS display.
     */
    public void doFilterLTS() {
        if (getController().setFilter(getFilter())) {
            boolean layout = getController().refreshFiltering();
            layout |= getController().refreshActive();
            getCanvas().refreshAll(false);
            if (layout) {
                getController().doLayout(false);
            }
            setEnabled(true);
            getController().scrollToActive();
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
        Color background = getController().isComplete()
            ? Values.STATE_BACKGROUND
            : Values.FILTER_BACKGROUND;
        getGraphPanel().setEnabledBackground(background);
        ((NumberEditor) getBoundSpinner().getEditor())
            .getTextField()
            .setBackground(isEnabled()
                ? background
                : null);
    }

    @Override
    public void doRepeat() {
        getCanvas().scrollToNextSelected();
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
            ResourceProperties.getRandomSeed(gts).ifPresent(seed -> {
                text.append(", random seed ");
                text.append(seed);
            });
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
            if (getCanvas().getMode() == SELECT_MODE && evt.getButton() == MouseEvent.BUTTON1) {
                if (!isEnabled() && getActions().getStartSimulationAction().isEnabled()) {
                    getActions().getStartSimulationAction().execute();
                } else {
                    // scale from screen to model
                    java.awt.Point loc = evt.getPoint();
                    // find cell in model coordinates
                    var cell = getCanvas().getCellAt(loc.x, loc.y);
                    var ctrl = (evt.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                    if (cell instanceof LTSViewEdge) {
                        GraphTransition trans = ((LTSViewEdge) cell).getEdge();
                        getSimulatorModel().setTransition(trans);
                        if (ctrl) {
                            getSimulatorModel().setDisplay(DisplayKind.STATE);
                        }
                    } else if (cell instanceof LTSViewVertex) {
                        GraphState node = ((LTSViewVertex) cell).getNode();
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
    public class LTSGraphPanel extends GraphPanel<@NonNull GTS> {
        /** Creates a LTS panel for a given simulator. */
        public LTSGraphPanel(LTSGraphCanvas canvas) {
            super(canvas);
            getCanvas().setToolTipEnabled(true);
            setEnabledBackground(Values.STATE_BACKGROUND);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            getController().getModeAction(SELECT_MODE).setEnabled(enabled);
            getController().getModeAction(PAN_MODE).setEnabled(enabled);
            if (enabled) {
                getController().getModeButton(SELECT_MODE).doClick();
            }
            LTSDisplay.this.setEnabled(enabled);
        }
    }
}
