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
package groove.gui;

import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.LabelStore;
import groove.gui.JTypeNameList.CheckBoxListModel;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.TypeJGraph;
import groove.io.SystemStore;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.type.TypeReconstructor;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.StoredGrammarView;
import groove.view.TypeView;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypePanel extends JGraphPanel<TypeJGraph> implements
        SimulationListener {
    /**
     * Constructor for this TypePanel Creates a new TypePanel instance and
     * instantiates all necessary variables.
     * @param simulator The simulator this type panel belongs to.
     */
    public TypePanel(final Simulator simulator) {
        super(new TypeJGraph(simulator), true, true, simulator.getOptions());
        this.simulator = simulator;
        add(createToolbar(), BorderLayout.NORTH);
        simulator.addSimulationListener(this);
        setEnabled(false);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
    }

    private JToolBar createToolbar() {
        JToolBar result = new JToolBar();
        result.setFloatable(false);
        result.add(createButton(getNewAction()));
        result.add(createButton(getEditAction()));
        result.add(createButton(getCopyAction()));
        result.add(createButton(getDeleteAction()));
        result.add(createButton(getRenameAction()));
        result.addSeparator();
        result.add(new JLabel("Type Graphs: "));
        result.add(getNameListPane());
        result.addSeparator();
        result.add(createButton(getUncheckAllAction()));
        result.add(createButton(getCheckAllAction()));
        result.addSeparator();
        result.add(createButton(new CreateAction()));
        return result;
    }

    /**
     * Creates a button around an action that is resized in case the action
     * doesn't have an icon.
     */
    private JButton createButton(Action action) {
        JButton result = new JButton(action);
        if (action.getValue(Action.SMALL_ICON) == null) {
            result.setMargin(new Insets(4, 2, 4, 2));
        } else {
            result.setHideActionText(true);
        }
        return result;
    }

    /** Does nothing (according to contract, the grammar has already been set). */
    public synchronized void startSimulationUpdate(GTS gts) {
        // nothing happens
    }

    public synchronized void setStateUpdate(GraphState state) {
        // nothing happens
    }

    public synchronized void setTransitionUpdate(GraphTransition trans) {
        // nothing happens
    }

    public void setMatchUpdate(RuleMatch match) {
        // nothing happens
    }

    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        // nothing happens
    }

    public synchronized void setRuleUpdate(RuleName rule) {
        // nothing happens
    }

    /**
     * This method is executed when the grammar in the Simulator is updated. It
     * basically executes one of the following 3 actions: - If no valid grammar
     * is loaded, all fields are disabled. - If a grammar is loaded for which a
     * type graph is saved already, this saved type graph is loaded. - If a
     * grammar is loaded for which no saved type graph exists, all fields are
     * disabled, except the "create type graph" button, which can be used to
     * compute a new type graph.
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        LabelStore labelStore = null;
        this.typeJModelMap.clear();
        if (grammar != null) {
            labelStore = grammar.getLabelStore();
            this.getNameList().refresh();
        }
        this.jGraph.setLabelStore(labelStore);
        displayType();
    }

    /**
     * Displays a type graph inside the typeGraphPanel.
     * @param typeGraph The type graph to be displayed.
     */
    public void displayTypeGraph(Graph typeGraph) {
        GraphInfo.setName(typeGraph, "Type graph");
        this.jGraph.setModel(GraphJModel.newInstance(typeGraph, getOptions()));
        setEnabled(true);
    }

    /**
     * Invokes the editor and saves the resulting type.
     * @param type the graph to be edited
     * @param fresh if <code>true</code>, the name for the edited type should be
     *        fresh
     */
    private void handleEditType(final Graph type, final boolean fresh) {
        final String oldSelectedType = getSelectedType();
        EditorDialog dialog =
            new EditorDialog(getSimulator().getFrame(), getOptions(), type,
                null) {
                @Override
                public void finish() {
                    String typeName =
                        getSimulator().askNewTypeName("Select type graph name",
                            GraphInfo.getName(type), fresh);
                    if (typeName != null) {
                        AspectGraph newType = getAspectGraph();
                        GraphInfo.setName(newType, typeName);
                        getSimulator().doAddType(newType);
                        if (!oldSelectedType.equals(typeName)) {
                            getNameListModel().addType(typeName, false, false);
                            getNameListModel().selectMostAppropriateType();
                        }
                    }
                }
            };
        dialog.start();
    }

    /**
     * Saves the current checked type graphs as a list in the system properties.
     */
    public void doSaveProperties() {
        List<String> checkedTypes = getNameListModel().getCheckedTypes();
        try {
            getGrammarView().getTypeViews(checkedTypes).toModel();
            SystemProperties oldProperties = getGrammarView().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setTypeNames(checkedTypes);
            getSimulator().doSaveProperties(newProperties);
        } catch (FormatException e) {
            // Does nothing.
        }
    }

    /**
     * Convenience method to test if the current grammar has a type graph by a
     * given name. Equivalent to
     * <code>getGrammarView().getTypeNames().contains(typeName)</code>
     */
    private boolean grammarHasType(String typeName) {
        return getGrammarView().getTypeNames().contains(typeName);
    }

    /**
     * Selects a type graph to be viewed.
     * @param name the type graph to be viewed; either <code>null</code> if
     *        there is no type graph in the current grammar, or an existing name
     *        in the type graph names of the current grammar.
     */
    public void setSelectedType(String name) {
        this.getNameListModel().setSelectedType(name);
    }

    /**
     * Indicates the currently selected type graph name
     * @return either <code>null</code> or an existing type graph name
     */
    private final String getSelectedType() {
        return this.getNameListModel().getSelectedType();
    }

    /** Convenience method to indicate if a type graph name has been selected. */
    protected final boolean isTypeSelected() {
        return getSelectedType() != null;
    }

    /**
     * Returns a graph model for a given type view. The graph model is retrieved
     * from {@link #typeJModelMap}; if there is no image for the requested state
     * then one is created.
     */
    private AspectJModel getTypeJModel(TypeView graph) {
        AspectJModel result = this.typeJModelMap.get(graph);
        if (result == null) {
            result = AspectJModel.newInstance(graph, getOptions());
            this.typeJModelMap.put(graph, result);
        }
        return result;
    }

    /**
     * Contains graph models for the production system's rules.
     * @invariant ruleJModels: RuleName --> RuleJModel
     */
    private final Map<TypeView,AspectJModel> typeJModelMap =
        new HashMap<TypeView,AspectJModel>();

    /**
     * Registers a refreshable.
     * @see #refreshActions()
     */
    protected void addRefreshable(Refreshable refreshable) {
        this.refreshables.add(refreshable);
    }

    /** Refreshes all registered actions. */
    public void refreshActions() {
        for (Refreshable refreshable : this.refreshables) {
            refreshable.refresh();
        }
    }

    /** Sets the model according to the currently selected type. */
    public void displayType() {
        AspectJModel newModel =
            isTypeSelected() ? getTypeJModel(getGrammarView().getTypeView(
                getSelectedType())) : AspectJModel.EMPTY_ASPECT_JMODEL;
        if (newModel != getJModel()) {
            this.jGraph.setModel(newModel);
        }
        if (newModel == AspectJModel.EMPTY_ASPECT_JMODEL) {
            setEnabled(false);
        } else {
            setEnabled(getNameListModel().isSelectedChecked());
        }
        refreshActions();
        refreshStatus();
    }

    /** List of registered refreshables. */
    private final List<Refreshable> refreshables = new ArrayList<Refreshable>();

    /** Indicates if the currently loaded grammar is modifiable. */
    private boolean isModifiable() {
        SystemStore store = getSimulator().getGrammarStore();
        return store != null && store.isModifiable();
    }

    /**
     * Returns the current grammar view. Convenience method for
     * <code>getSimulator().getGrammarView()</code>.
     */
    protected StoredGrammarView getGrammarView() {
        return getSimulator().getGrammarView();
    }

    /** Returns the simulator object. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** The simulator to which this panel belongs. */
    private final Simulator simulator;

    /** Display name of this panel. */
    public static final String FRAME_NAME = "Type graph";

    /**
     * Interface for objects that need to refresh their own status when actions
     * on the type panel occur.
     */
    protected interface Refreshable {
        /**
         * Callback method to give the implementing object a chance to refresh
         * its status.
         */
        public void refresh();
    }

    /** Lazily creates and returns the list displaying the type names. */
    private JTypeNameList getNameList() {
        if (this.nameList == null) {
            this.nameList = new JTypeNameList(this);
        }
        return this.nameList;
    }

    private CheckBoxListModel getNameListModel() {
        return this.getNameList().getModel();
    }

    /** Name list of type graphs. */
    private JTypeNameList nameList;

    /** Lazily creates and returns the pane displaying the type names. */
    private TypeNamesPane getNameListPane() {
        if (this.nameListPane == null) {
            this.nameListPane = new TypeNamesPane(getNameList());
        }
        return this.nameListPane;
    }

    /** Name list of type graphs. */
    private TypeNamesPane nameListPane;

    private class TypeNamesPane extends JScrollPane {
        TypeNamesPane(JTypeNameList nameList) {
            super(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.setViewportView(nameList);
            this.setMinimumSize(JTypeNameList.MIN_DIMENSIONS);
            this.setMaximumSize(JTypeNameList.MAX_DIMENSIONS);
            this.setPreferredSize(JTypeNameList.MAX_DIMENSIONS);
        }
    }

    /** Abstract superclass for actions that can refresh their own status. */
    private abstract class RefreshableAction extends AbstractAction implements
            Refreshable {
        public RefreshableAction(String name, Icon icon) {
            super(name, icon);
            putValue(SHORT_DESCRIPTION, name);
            setEnabled(false);
            addRefreshable(this);
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CopyAction}.
     */
    private CopyAction getCopyAction() {
        if (this.copyAction == null) {
            this.copyAction = new CopyAction();
        }
        return this.copyAction;
    }

    /** Singular instance of the {@link CopyAction}. */
    private CopyAction copyAction;

    /**
     * Action to copy the currently displayed type graph.
     */
    private class CopyAction extends RefreshableAction {
        public CopyAction() {
            super(Options.COPY_TYPE_ACTION_NAME, Groove.COPY_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            String oldName = getSelectedType();
            String newName =
                getSimulator().askNewTypeName("Select new type graph name",
                    oldName, true);
            if (newName != null) {
                TypeView oldTypeView = getGrammarView().getTypeView(oldName);
                AspectGraph newType = oldTypeView.getView().clone();
                GraphInfo.setName(newType, newName);
                getSimulator().doAddType(newType);
                getNameListModel().addType(newName, false, false);
                getNameListModel().selectMostAppropriateType();
            }
        }

        @Override
        public void refresh() {
            setEnabled(isTypeSelected() && grammarHasType(getSelectedType()));
            if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
                getSimulator().getCopyMenuItem().setAction(this);
            }
        }
    }

    /**
     * Action listener class for the "create type graph" button
     * @author Frank van Es
     * @version $Revision $
     */
    class CreateAction extends RefreshableAction {
        public CreateAction() {
            super("Compute type graph", null);
        }

        @Override
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarView().getStartGraphView() != null);
        }

        /**
         * This method is executed when the "create type graph" button is
         * clicked. Then a new type graph for the current grammar is being
         * computed; the type graph will be displayed and saved inside the graph
         * grammar directory.
         */
        public void actionPerformed(ActionEvent e) {
            try {
                Graph typeGraph =
                    TypeReconstructor.reconstruct(getGrammarView().toGrammar());
                displayTypeGraph(typeGraph);
            } catch (FormatException fe) {
                System.err.printf("Graph format error: %s", fe.getMessage());
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link DeleteAction}.
     */
    private DeleteAction getDeleteAction() {
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction();
        }
        return this.deleteAction;
    }

    /** Singular instance of the {@link DeleteAction}. */
    private DeleteAction deleteAction;

    /**
     * Action to delete the currently displayed type graph.
     */
    private class DeleteAction extends RefreshableAction {
        public DeleteAction() {
            super(Options.DELETE_TYPE_ACTION_NAME, Groove.DELETE_ICON);
            putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            String typeName = getSelectedType();
            if (getSimulator().confirmBehaviour(Options.DELETE_TYPE_OPTION,
                String.format("Delete type graph '%s'?", typeName))) {
                getNameListModel().selectMostAppropriateType();
                getNameListModel().removeType(typeName, true);
                getSimulator().doDeleteType(typeName);
            }
        }

        @Override
        public void refresh() {
            setEnabled(isTypeSelected() && grammarHasType(getSelectedType()));
            if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
                getSimulator().getDeleteMenuItem().setAction(this);
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link UncheckAllAction}.
     */
    private UncheckAllAction getUncheckAllAction() {
        if (this.uncheckAllAction == null) {
            this.uncheckAllAction = new UncheckAllAction();
        }
        return this.uncheckAllAction;
    }

    /** Singular instance of the {@link CheckAllAction}. */
    private UncheckAllAction uncheckAllAction;

    /** Action to disable the currently displayed type graph. */
    private class UncheckAllAction extends RefreshableAction {
        public UncheckAllAction() {
            super("Uncheck all type graphs", Groove.DISABLE_ICON);
        }

        public void actionPerformed(ActionEvent arg0) {
            getNameListModel().uncheckAll();
            doSaveProperties();
        }

        @Override
        public void refresh() {
            setEnabled(!getNameListModel().isAllUnchecked());
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CheckAllAction}.
     */
    private CheckAllAction getCheckAllAction() {
        if (this.checkAllAction == null) {
            this.checkAllAction = new CheckAllAction();
        }
        return this.checkAllAction;
    }

    /** Singular instance of the {@link CheckAllAction}. */
    private CheckAllAction checkAllAction;

    /** Action to enable the currently displayed type graph. */
    private class CheckAllAction extends RefreshableAction {
        public CheckAllAction() {
            super("Check all type graphs", Groove.ENABLE_ICON);
        }

        public void actionPerformed(ActionEvent evt) {
            getNameListModel().checkAll();
            doSaveProperties();
        }

        @Override
        public void refresh() {
            setEnabled(!getNameListModel().isAllChecked());
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link EditAction}.
     */
    EditAction getEditAction() {
        if (this.editAction == null) {
            this.editAction = new EditAction();
        }
        return this.editAction;
    }

    /** Singular instance of the {@link EditAction}. */
    private EditAction editAction;

    /** Action to start editing the currently displayed type graph. */
    private class EditAction extends RefreshableAction {
        public EditAction() {
            super(Options.EDIT_TYPE_ACTION_NAME, Groove.EDIT_ICON);
            putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            final Graph initType =
                getGrammarView().getTypeView(getSelectedType()).getView().toPlainGraph();
            handleEditType(initType, false);
        }

        @Override
        public void refresh() {
            setEnabled(isTypeSelected() && isModifiable());
            if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
                getSimulator().getEditMenuItem().setAction(this);
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    NewAction getNewAction() {
        if (this.newAction == null) {
            this.newAction = new NewAction();
        }
        return this.newAction;
    }

    /** Singular instance of the {@link NewAction}. */
    private NewAction newAction;

    /** Action to create and start editing a new type graph. */
    private class NewAction extends RefreshableAction {
        public NewAction() {
            super(Options.NEW_TYPE_ACTION_NAME, Groove.NEW_TYPE_ICON);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Graph initType = GraphFactory.getInstance().newGraph();
            GraphInfo.setTypeRole(initType);
            GraphInfo.setName(initType, Groove.DEFAULT_TYPE_NAME);
            handleEditType(initType, true);
        }

        @Override
        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link RenameAction}.
     */
    private RenameAction getRenameAction() {
        if (this.renameAction == null) {
            this.renameAction = new RenameAction();
        }
        return this.renameAction;
    }

    /** Singular instance of the {@link RenameAction}. */
    private RenameAction renameAction;

    /**
     * Action to rename the currently displayed type graph.
     */
    private class RenameAction extends RefreshableAction {
        public RenameAction() {
            super(Options.RENAME_TYPE_ACTION_NAME, Groove.RENAME_ICON);
            putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            String oldName = getSelectedType();
            String newName =
                getSimulator().askNewTypeName("Select new type graph name",
                    oldName, false);
            if (newName != null && !oldName.equals(newName)) {
                TypeView type = getGrammarView().getTypeView(oldName);
                boolean checked =
                    getNameListModel().getElementByName(oldName).checked;
                getNameListModel().removeType(oldName, true);
                if (getSimulator().doRenameType(type.getView(), newName)) {
                    getNameListModel().checkType(newName, checked);
                    getNameListModel().setSelectedType(newName);
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(isTypeSelected() && grammarHasType(getSelectedType()));
            if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
                getSimulator().getRenameMenuItem().setAction(this);
            }
        }
    }
}
