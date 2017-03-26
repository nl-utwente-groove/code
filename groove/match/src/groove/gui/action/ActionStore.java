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
package groove.gui.action;

import groove.explore.StrategyValue;
import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.display.DisplayKind;
import groove.gui.display.StateDisplay;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

/**
 * Program that applies a production system to an initial graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ActionStore implements SimulatorListener {
    /**
     * Constructs a simulator with an empty graph grammar.
     */
    public ActionStore(Simulator simulator) {
        this.simulator = simulator;
        simulator.getModel().addListener(this);
    }

    /** Returns the simulator permanently associated with this action store. */
    public final Simulator getSimulator() {
        return this.simulator;
    }

    private final Simulator simulator;

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        refreshActions();
    }

    /**
     * Adds an element to the set of refreshables.
     */
    public void addRefreshable(Refreshable element) {
        this.refreshables.add(element);
    }

    /** Removes an element from the set of refreshables. */
    public void removeRefreshable(Refreshable element) {
        this.refreshables.remove(element);
    }

    /**
     * Is called after a change to current state, rule or derivation or to the
     * currently selected view panel to allow registered refreshable elements to
     * refresh themselves.
     */
    public void refreshActions() {
        for (Refreshable action : new ArrayList<>(this.refreshables)) {
            action.refresh();
        }
    }

    private final List<Refreshable> refreshables = new ArrayList<>();

    /**
     * Returns the 'default exploration' action that is associated with the
     * simulator.
     */
    public ExploreAction getAnimateAction() {
        // lazily create the action
        if (this.animateAction == null) {
            this.animateAction = new ExploreAction(this.simulator, true);
        }

        return this.animateAction;
    }

    /**
     * The animated exploration action.
     */
    private ExploreAction animateAction;

    /**
     * Returns the transition application action permanently associated with
     * this simulator.
     */
    public ApplyMatchAction getApplyMatchAction() {
        if (this.applyMatchAction == null) {
            this.applyMatchAction = new ApplyMatchAction(this.simulator);
        }
        return this.applyMatchAction;
    }

    /**
     * The transition application action permanently associated with this
     * simulator.
     */
    private ApplyMatchAction applyMatchAction;

    /**
     * Returns the back simulation action permanently associated with this
     * simulator.
     */
    public Action getBackAction() {
        if (this.backAction == null) {
            this.backAction = this.simulator.getSimulationHistory().getBackAction();
        }
        return this.backAction;
    }

    /** The back simulation action permanently associated with this simulator. */
    private Action backAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CancelEditAction} for the given resource kind.
     */
    public CancelEditAction getCancelEditAction(ResourceKind resource) {
        CancelEditAction result = this.cancelEditActionMap.get(resource);
        if (result == null) {
            this.cancelEditActionMap.put(resource, result =
                new CancelEditAction(this.simulator, resource));
            result.refresh();
        }
        return result;
    }

    private final Map<ResourceKind,CancelEditAction> cancelEditActionMap =
        new EnumMap<>(ResourceKind.class);

    /**
     * Returns the CTL formula providing action permanently associated with this
     * simulator.
     * @param full if {@code true}, the action first generates the full state
     * space.
     */
    public Action getCheckCTLAction(boolean full) {
        CheckCTLAction result = full ? this.checkCTLFreshAction : this.checkCTLAsIsAction;
        if (result == null) {
            result = new CheckCTLAction(this.simulator, full);
            if (full) {
                this.checkCTLFreshAction = result;
            } else {
                this.checkCTLAsIsAction = result;
            }
        }
        return result;
    }

    /**
     * Action to check a CTL property on a fully explored state space.
     */
    private CheckCTLAction checkCTLFreshAction;

    /**
     * Action to check a CTL property on the current state space.
     */
    private CheckCTLAction checkCTLAsIsAction;

    /** Returns the copy action appropriate for a given resource kind. */
    public CopyAction getCopyAction(ResourceKind resource) {
        CopyAction result = this.copyActionMap.get(resource);
        if (result == null) {
            this.copyActionMap.put(resource, result = new CopyAction(this.simulator, resource));
        }
        return result;
    }

    private final Map<ResourceKind,CopyAction> copyActionMap =
        new EnumMap<>(ResourceKind.class);

    /** Returns the delete action appropriate for a given resource kind. */
    public SimulatorAction getDeleteAction(ResourceKind resource) {
        SimulatorAction result = this.deleteActionMap.get(resource);
        if (result == null) {
            result = new DeleteAction(this.simulator, resource);
            this.deleteActionMap.put(resource, result);
        }
        return result;
    }

    private final Map<ResourceKind,SimulatorAction> deleteActionMap =
        new EnumMap<>(ResourceKind.class);

    /** Returns the edit action appropriate for a given resource kind. */
    public EditAction getEditAction(ResourceKind resource) {
        EditAction result = this.editActionMap.get(resource);
        if (result == null) {
            result = new EditAction(this.simulator, resource);
            this.editActionMap.put(resource, result);
        }
        return result;
    }

    private final Map<ResourceKind,EditAction> editActionMap =
        new EnumMap<>(ResourceKind.class);

    /** Returns the state edit action. */
    public EditStateAction getEditStateAction() {
        if (this.editStateAction == null) {
            this.editStateAction = new EditStateAction(this.simulator);
        }
        return this.editStateAction;
    }

    private EditStateAction editStateAction;

    /**
     * Returns the properties edit action permanently associated with this
     * simulator.
     */
    public EditRulePropertiesAction getEditRulePropertiesAction() {
        // lazily create the action
        if (this.editRulePropertiesAction == null) {
            this.editRulePropertiesAction = new EditRulePropertiesAction(this.simulator);
        }
        return this.editRulePropertiesAction;
    }

    /**
     * The rule properties edit action permanently associated with this
     * simulator.
     */
    private EditRulePropertiesAction editRulePropertiesAction;

    /** Returns the action to show the system properties of the current grammar. */
    public SimulatorAction getEditSystemPropertiesAction() {
        // lazily create the action
        if (this.editSystemPropertiesAction == null) {
            this.editSystemPropertiesAction = new EditSystemPropertiesAction(this.simulator);
        }
        return this.editSystemPropertiesAction;
    }

    /**
     * The action to show the system properties of the currently selected
     * grammar.
     */
    private EditSystemPropertiesAction editSystemPropertiesAction;

    /** Returns the delete action appropriate for a given resource kind. */
    public EnableAction getEnableAction(ResourceKind resource) {
        EnableAction result = this.enableActionMap.get(resource);
        if (result == null) {
            result = new EnableAction(this.simulator, resource);
            this.enableActionMap.put(resource, result);
        }
        return result;
    }

    private final Map<ResourceKind,EnableAction> enableActionMap =
        new EnumMap<>(ResourceKind.class);

    /**
     * Returns the 'default exploration' action that is associated with the
     * simulator.
     */
    public ExploreAction getExploreAction() {
        // lazily create the action
        if (this.exploreAction == null) {
            this.exploreAction = new ExploreAction(this.simulator, false);
        }

        return this.exploreAction;
    }

    /**
     * The 'default exploration' action (variable).
     */
    private ExploreAction exploreAction;

    /**
     * Returns the 'default exploration' action that is associated with the
     * simulator.
     */
    public CheckLTLAction getCheckLTLAction(StrategyValue strategy, String name) {
        // lazily create the action
        CheckLTLAction result = this.checkLTLMap.get(strategy);
        if (result == null) {
            this.checkLTLMap.put(strategy, result =
                new CheckLTLAction(this.simulator, strategy, name));
        }

        return result;
    }

    /**
     * The 'default exploration' action (variable).
     */
    private Map<StrategyValue,CheckLTLAction> checkLTLMap =
        new EnumMap<>(StrategyValue.class);

    /**
     * Returns the exploration dialog action permanently associated with this
     * simulator.
     */
    public ExplorationDialogAction getExplorationDialogAction() {
        // lazily create the action
        if (this.explorationDialogAction == null) {
            this.explorationDialogAction = new ExplorationDialogAction(this.simulator);
        }
        return this.explorationDialogAction;
    }

    /**
     * The exploration dialog action permanently associated with this simulator.
     */
    private ExplorationDialogAction explorationDialogAction;

    /**
     * Returns the exploration statistics dialog action permanently associated
     * with this simulator.
     */
    public ExplorationStatsDialogAction getExplorationStatsDialogAction() {
        // lazily create the action
        if (this.explorationStatsDialogAction == null) {
            this.explorationStatsDialogAction = new ExplorationStatsDialogAction(this.simulator);
        }
        return this.explorationStatsDialogAction;
    }

    /**
     * The exploration statistics dialog action permanently associated with
     * this simulator.
     */
    private ExplorationStatsDialogAction explorationStatsDialogAction;

    /** Returns the export action appropriate for a given simulator tab kind. */
    public ExportAction getExportAction(DisplayKind kind) {
        if (!this.exportActionMap.containsKey(kind)) {
            ExportAction result = new ExportAction(getSimulator(), kind);
            // also put it in the map when the result is null,
            // so we don't try to compute it again
            this.exportActionMap.put(kind, result);
        }
        return this.exportActionMap.get(kind);
    }

    private final Map<DisplayKind,ExportAction> exportActionMap =
        new EnumMap<>(DisplayKind.class);

    /** Returns the export action appropriate for a given simulator tab kind. */
    public ExportAction getExportStateAction() {
        if (this.exportStateAction == null) {
            StateDisplay display =
                (StateDisplay) this.simulator.getDisplaysPanel().getDisplay(DisplayKind.STATE);
            this.exportStateAction = display.getJGraph().getExportAction();
        }
        return this.exportStateAction;
    }

    private ExportAction exportStateAction;

    /**
     * Returns the forward (= repeat) simulation action permanently associated
     * with this simulator.
     */
    public Action getForwardAction() {
        if (this.forwardAction == null) {
            this.forwardAction = this.simulator.getSimulationHistory().getForwardAction();
        }
        return this.forwardAction;
    }

    /**
     * The forward simulation action permanently associated with this simulator.
     */
    private Action forwardAction;

    /**
     * Returns the action to perform random linear exploration and show the final state.
     */
    public GotoFinalStateAction getGotoFinalStateAction() {
        // lazily create the action
        if (this.gotoFinalStateAction == null) {
            this.gotoFinalStateAction = new GotoFinalStateAction(this.simulator, false);
        }

        return this.gotoFinalStateAction;
    }

    /**
     * Action to perform random linear exploration and show the final state.
     */
    private GotoFinalStateAction gotoFinalStateAction;

    /**
     * Returns the go-to start state action permanently associated with this
     * simulator.
     */
    public GotoStartStateAction getGotoStartStateAction() {
        // lazily create the action
        if (this.gotoStartStateAction == null) {
            this.gotoStartStateAction = new GotoStartStateAction(this.simulator);
        }
        return this.gotoStartStateAction;
    }

    /**
     * The go-to start state action permanently associated with this simulator.
     */
    private GotoStartStateAction gotoStartStateAction;

    /** Returns the import action permanently associated with this simulator. */
    public ImportAction getImportAction() {
        // lazily create the action
        if (this.importAction == null) {
            this.importAction = new ImportAction(this.simulator);
        }
        return this.importAction;
    }

    /** The import action permanently associated with this simulator. */
    private ImportAction importAction;

    /**
     * Returns the grammar load action permanently associated with this
     * simulator.
     */
    public LoadGrammarAction getLoadGrammarAction() {
        // lazily create the action
        if (this.loadGrammarAction == null) {
            this.loadGrammarAction = new LoadGrammarAction(this.simulator);
        }
        return this.loadGrammarAction;
    }

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarAction loadGrammarAction;

    /**
     * Returns the grammar load action permanently associated with this
     * simulator.
     */
    public Action getLoadGrammarFromURLAction() {
        // lazily create the action
        if (this.loadGrammarFromURLAction == null) {
            this.loadGrammarFromURLAction = new LoadGrammarFromURLAction(this.simulator);
        }
        return this.loadGrammarFromURLAction;
    }

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarFromURLAction loadGrammarFromURLAction;

    /** Returns the delete action appropriate for a given resource kind. */
    public SimulatorAction getNewAction(ResourceKind resource) {
        SimulatorAction result = this.newActionMap.get(resource);
        if (result == null) {
            result = new NewAction(this.simulator, resource);
            this.newActionMap.put(resource, result);
        }
        return result;
    }

    private final Map<ResourceKind,SimulatorAction> newActionMap =
        new EnumMap<>(ResourceKind.class);

    /**
     * Returns the rule system creation action permanently associated with this
     * simulator.
     */
    public NewGrammarAction getNewGrammarAction() {
        // lazily create the action
        if (this.newGrammarAction == null) {
            this.newGrammarAction = new NewGrammarAction(this.simulator);
        }
        return this.newGrammarAction;
    }

    /**
     * The rule system creation action permanently associated with this
     * simulator.
     */
    private NewGrammarAction newGrammarAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link PreviewControlAction}.
     */
    public SimulatorAction getPreviewControlAction() {
        if (this.previewControlAction == null) {
            this.previewControlAction = new PreviewControlAction(this.simulator);
        }
        return this.previewControlAction;
    }

    /** Singular instance of the CtrlPreviewAction. */
    private SimulatorAction previewControlAction;

    /** Returns the quit action permanently associated with this simulator. */
    public SimulatorAction getQuitAction() {
        // lazily create the action
        if (this.quitAction == null) {
            this.quitAction = new QuitAction(this.simulator);
        }
        return this.quitAction;
    }

    /**
     * The quit action permanently associated with this simulator.
     */
    private QuitAction quitAction;

    /** Returns the action that displays the first result of a Prolog query. */
    public PrologFirstResultAction getPrologFirstResultAction() {
        // lazily create the action
        if (this.prologFirstResultAction == null) {
            this.prologFirstResultAction = new PrologFirstResultAction(this.simulator);
        }
        return this.prologFirstResultAction;
    }

    /**
     * The quit action permanently associated with this simulator.
     */
    private PrologFirstResultAction prologFirstResultAction;

    /** Returns the action that displays the next result of a Prolog query. */
    public PrologNextResultAction getPrologNextResultAction() {
        // lazily create the action
        if (this.prologNexttResultAction == null) {
            this.prologNexttResultAction = new PrologNextResultAction(this.simulator);
        }
        return this.prologNexttResultAction;
    }

    /**
     * The quit action permanently associated with this simulator.
     */
    private PrologNextResultAction prologNexttResultAction;

    /**
     * Returns the redo action permanently associated with this simulator.
     */
    public RedoSimulatorAction getRedoAction() {
        if (this.redoAction == null) {
            this.redoAction = new RedoSimulatorAction(this.simulator);
        }
        return this.redoAction;
    }

    /**
     * The redo permanently associated with this simulator.
     */
    private RedoSimulatorAction redoAction;

    /**
     * Returns the grammar refresh action permanently associated with this
     * simulator.
     */
    public RefreshGrammarAction getRefreshGrammarAction() {
        // lazily create the action
        if (this.refreshGrammarAction == null) {
            this.refreshGrammarAction = new RefreshGrammarAction(this.simulator);
        }
        return this.refreshGrammarAction;
    }

    /** The grammar refresh action permanently associated with this simulator. */
    private RefreshGrammarAction refreshGrammarAction;

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public FindReplaceAction getFindReplaceAction() {
        // lazily create the action
        if (this.findReplaceAction == null) {
            this.findReplaceAction = new FindReplaceAction(this.simulator);
        }
        return this.findReplaceAction;
    }

    /**
     * The graph renaming action permanently associated with this simulator.
     */
    private FindReplaceAction findReplaceAction;

    /** Returns the delete action appropriate for a given resource kind. */
    public RenameAction getRenameAction(ResourceKind resource) {
        RenameAction result = this.renameActionMap.get(resource);
        if (result == null) {
            result = new RenameAction(this.simulator, resource);
            this.renameActionMap.put(resource, result);
        }
        return result;
    }

    private final Map<ResourceKind,RenameAction> renameActionMap =
        new EnumMap<>(ResourceKind.class);

    /**
     * Returns the renumbering action permanently associated with this
     * simulator.
     */
    public RenumberGrammarAction getRenumberAction() {
        // lazily create the action
        if (this.renumberAction == null) {
            this.renumberAction = new RenumberGrammarAction(this.simulator);
        }
        return this.renumberAction;
    }

    /**
     * The renumbering action permanently associated with this simulator.
     */
    private RenumberGrammarAction renumberAction;

    /**
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveGrammarAction getSaveGrammarAction() {
        // lazily create the action
        if (this.saveGrammarAction == null) {
            this.saveGrammarAction = new SaveGrammarAction(this.simulator);
        }
        return this.saveGrammarAction;
    }

    /**
     * The grammar save action permanently associated with this simulator.
     */
    private SaveGrammarAction saveGrammarAction;

    /** Returns the save action for a given resource kind. */
    public SaveAction getSaveAction(ResourceKind resource) {
        SaveAction result = this.saveActionMap.get(resource);
        if (result == null) {
            this.saveActionMap.put(resource, result =
                new SaveAction(this.simulator, resource, false));
        }
        return result;
    }

    /**
     * Mapping from graph roles to corresponding save actions.
     */
    private Map<ResourceKind,SaveAction> saveActionMap = new EnumMap<>(
        ResourceKind.class);

    /** Returns the save action for a given resource kind. */
    public SaveAction getSaveAsAction(ResourceKind resource) {
        SaveAction result = this.saveAsActionMap.get(resource);
        if (result == null) {
            this.saveAsActionMap.put(resource, result =
                new SaveAction(this.simulator, resource, true));
            result.refresh();
        }
        return result;
    }

    /**
     * Mapping from graph roles to corresponding save actions.
     */
    private Map<ResourceKind,SaveAction> saveAsActionMap = new EnumMap<>(
        ResourceKind.class);

    /** Returns the state save action. */
    public SaveStateAction getSaveStateAction() {
        if (this.saveStateAction == null) {
            this.saveStateAction = new SaveStateAction(this.simulator, false);
        }
        return this.saveStateAction;
    }

    private SaveStateAction saveStateAction;

    /** Returns the state save as action. */
    public SaveStateAction getSaveStateAsAction() {
        if (this.saveStateAsAction == null) {
            this.saveStateAsAction = new SaveStateAction(this.simulator, true);
        }
        return this.saveStateAsAction;
    }

    private SaveStateAction saveStateAsAction;

    /**
     * Returns the Save LTS As action permanently associated with this simulator.
     */
    public SaveLTSAsAction getSaveLTSAsAction() {
        // lazily create the action
        if (this.saveLtsAsAction == null) {
            this.saveLtsAsAction = new SaveLTSAsAction(this.simulator);
        }
        return this.saveLtsAsAction;
    }

    /** The LTS Save As action permanently associated with this simulator. */
    private SaveLTSAsAction saveLtsAsAction;

    /**
     * Returns the colour selection action permanently associated with this simulator.
     */
    public Action getSelectColorAction() {
        if (this.selectColorAction == null) {
            this.selectColorAction = new SelectColorAction(this.simulator);
        }
        return this.selectColorAction;
    }

    /**
     * The colour selection action permanently associated with this simulator.
     */
    private SelectColorAction selectColorAction;

    /**
     * Returns the priority setting action permanently associated with this simulator.
     */
    public SetPriorityAction getSetPriorityAction() {
        if (this.setPriorityAction == null) {
            this.setPriorityAction = new SetPriorityAction(this.simulator);
            this.setPriorityAction.refresh();
        }
        return this.setPriorityAction;
    }

    /**
     * The priority setting action permanently associated with this simulator.
     */
    private SetPriorityAction setPriorityAction;

    /**
     * Returns the priority up- or down-shifting action permanently associated with the simulator.
     */
    public ShiftPriorityAction getShiftPriorityAction(boolean up) {
        ShiftPriorityAction result = up ? this.raisePriorityAction : this.lowerPriorityAction;
        if (result == null) {
            result = new ShiftPriorityAction(this.simulator, up);
            if (up) {
                this.raisePriorityAction = result;
            } else {
                this.lowerPriorityAction = result;
            }
        }
        return result;
    }

    /**
     * The priority raising action permanently associated with the simulator.
     */
    private ShiftPriorityAction raisePriorityAction;

    /**
     * The priority lowering action permanently associated with the simulator.
     */
    private ShiftPriorityAction lowerPriorityAction;

    /**
     * Lazily creates and returns an instance of {@link EnableUniqueAction}.
     */
    public EnableUniqueAction getEnableUniqueAction(ResourceKind resource) {
        EnableUniqueAction result = this.enableUniqueActionMap.get(resource);
        if (result == null) {
            result = new EnableUniqueAction(this.simulator, resource);
            this.enableUniqueActionMap.put(resource, result);
        }
        return result;
    }

    private final Map<ResourceKind,EnableUniqueAction> enableUniqueActionMap =
        new EnumMap<>(ResourceKind.class);

    /**
     * Lazily creates and returns an instance of
     * {@link StartSimulationAction}.
     */
    public SnapToGridAction getSnapToGridAction() {
        // lazily create the action
        if (this.snapToGridAction == null) {
            this.snapToGridAction = new SnapToGridAction(this.simulator);
        }
        return this.snapToGridAction;
    }

    /** The action to start a new simulation. */
    private SnapToGridAction snapToGridAction;

    /**
     * Lazily creates and returns an instance of
     * {@link StartSimulationAction}.
     */
    public StartSimulationAction getStartSimulationAction() {
        // lazily create the action
        if (this.startSimulationAction == null) {
            this.startSimulationAction = new StartSimulationAction(this.simulator);
        }
        return this.startSimulationAction;
    }

    /** The action to start a new simulation. */
    private StartSimulationAction startSimulationAction;

    /**
     * Returns the undo action permanently associated with this simulator.
     */
    public UndoSimulatorAction getUndoAction() {
        if (this.undoAction == null) {
            this.undoAction = new UndoSimulatorAction(this.simulator);
        }
        return this.undoAction;
    }

    /**
     * The undo action permanently associated with this simulator.
     */
    private UndoSimulatorAction undoAction;

    /**
     * The layout dialog action permanently associated with this simulator.
     */
    private LayoutDialogAction layoutDialogAction;

    /**
     * Returns the layout dialog action permanently associated with this simulator.
     */
    public LayoutDialogAction getLayoutDialogAction() {
        // lazily create the action
        if (this.layoutDialogAction == null) {
            this.layoutDialogAction = new LayoutDialogAction(this.simulator);
        }
        return this.layoutDialogAction;
    }

    /**
     * The Groovy execute action permanently associated with this simulator.
     */
    private ExecGroovyAction execGroovyAction;

    /**
     * Returns the Groovy execute action permanently associated with this simulator.
     */
    public ExecGroovyAction getExecGroovyAction() {
        if (this.execGroovyAction == null) {
            this.execGroovyAction = new ExecGroovyAction(this.simulator);
        }
        return this.execGroovyAction;
    }

    /**
     * Method to bypass the lazy initialisation of some actions that are not
     * included in any menu of the simulator and therefore are not added to
     * the refreshables list in time.
     */
    public void initialiseRemainingActions() {
        // none
    }
}
