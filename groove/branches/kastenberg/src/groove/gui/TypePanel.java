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

import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.LabelStore;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.StateJGraph;
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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypePanel extends JGraphPanel<StateJGraph> implements
        SimulationListener {
    /**
     * Constructor for this TypePanel Creates a new TypePanel instance and
     * instantiates all necessary variables.
     * @param simulator The simulator this type panel belongs to.
     */
    public TypePanel(final Simulator simulator) {
        super(new StateJGraph(simulator), true, true, simulator.getOptions());
        this.simulator = simulator;
        add(createToolbar(), BorderLayout.NORTH);
        simulator.addSimulationListener(this);
        setEnabled(false);
    }

    private JToolBar createToolbar() {
        JToolBar result = new JToolBar();
        result.add(createButton(getNewAction()));
        result.add(createButton(getEditAction()));
        result.add(createButton(getCopyAction()));
        result.add(createButton(getDeleteAction()));
        result.add(createButton(getRenameAction()));
        result.addSeparator();
        result.add(new JLabel("Name: "));
        result.add(getNameField());
        result.addSeparator();
        result.add(createButton(getDisableAction()));
        result.add(createButton(getEnableAction()));
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
        AspectJModel model = AspectJModel.EMPTY_ASPECT_JMODEL;
        String setTypeName = null;
        this.typeJModelMap.clear();
        if (grammar != null) {
            if (isTypeSelected()
                && grammar.getTypeNames().contains(getSelectedType())) {
                setTypeName = getSelectedType();
            } else if (grammar.getTypeName().length() > 0) {
                setTypeName = grammar.getTypeName();
            } else if (!grammar.getTypeNames().isEmpty()) {
                setTypeName = grammar.getTypeNames().iterator().next();
            }
            if (setTypeName != null) {
                model = getTypeJModel(grammar.getTypeView(setTypeName));
            }
            labelStore = grammar.getLabelStore();
        }
        setSelectedType(setTypeName);
        this.jGraph.setLabelStore(labelStore);
        // reset the display
        this.jGraph.setModel(model);
        refreshAll();
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
        EditorDialog dialog =
            new EditorDialog(getSimulator().getFrame(), getOptions(), type) {
                @Override
                public void finish() {
                    String typeName =
                        getSimulator().askNewTypeName("Select type graph name",
                            GraphInfo.getName(type), fresh);
                    if (typeName != null) {
                        AspectGraph newType = toAspectGraph();
                        GraphInfo.setName(newType, typeName);
                        if (getSimulator().doAddType(newType)) {
                            setSelectedType(typeName);
                            refreshAll();
                        }
                    }
                }
            };
        dialog.start();
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
    private void setSelectedType(String name) {
        this.selectedType = name;
    }

    /**
     * Indicates the currently selected type graph name
     * @return either <code>null</code> or an existing type graph name
     */
    private final String getSelectedType() {
        return this.selectedType;
    }

    /** Convenience method to indicate if a type graph name has been selected. */
    private final boolean isTypeSelected() {
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

    /** Name of the currently visible type graph. */
    private String selectedType;

    /**
     * Registers a refreshable.
     * @see #refreshAll()
     */
    private void addRefreshable(Refreshable refreshable) {
        this.refreshables.add(refreshable);
    }

    /** Refreshes all registered refreshables. */
    public void refreshAll() {
        for (Refreshable refreshable : this.refreshables) {
            refreshable.refresh();
        }
        this.jGraph.setModel(isTypeSelected()
                ? getTypeJModel(getGrammarView().getTypeView(getSelectedType()))
                : AspectJModel.EMPTY_ASPECT_JMODEL);
        setEnabled(isTypeSelected()
            && getSelectedType().equals(getGrammarView().getTypeName()));
        refresh();
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
    private StoredGrammarView getGrammarView() {
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
    private interface Refreshable {
        /**
         * Callback method to give the implementing object a chance to refresh
         * its status.
         */
        public void refresh();
    }

    /** Lazily creates and returns the field displaying the type name. */
    private JComboBox getNameField() {
        if (this.nameField == null) {
            this.nameField = new TypeNameField();
            this.nameField.setBorder(BorderFactory.createLoweredBevelBorder());
            this.nameField.setMaximumSize(new Dimension(150, 24));
        }
        return this.nameField;
    }

    /** Name field of the type graph. */
    private JComboBox nameField;

    private class TypeNameField extends JComboBox implements Refreshable {
        public TypeNameField() {
            setBorder(BorderFactory.createLoweredBevelBorder());
            setMaximumSize(new Dimension(150, 24));
            setEnabled(false);
            setEditable(false);
            this.selectionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (getSelectedItem() != null) {
                        setSelectedType((String) getSelectedItem());
                        refreshAll();
                    }
                }
            };
            addActionListener(this.selectionListener);
            addRefreshable(this);
        }

        @Override
        public void refresh() {
            removeActionListener(this.selectionListener);
            this.removeAllItems();
            if (getGrammarView() == null) {
                setEnabled(false);
            } else {
                Set<String> names =
                    new TreeSet<String>(getGrammarView().getTypeNames());
                if (isTypeSelected()) {
                    names.add(getSelectedType());
                }
                for (String typeName : names) {
                    addItem(typeName);
                }
                setSelectedItem(getSelectedType());
                setEnabled(getItemCount() > 0);
            }
            addActionListener(this.selectionListener);
        }

        private final ActionListener selectionListener;
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
                setSelectedType(newName);
                refreshAll();
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
            addRefreshable(this);
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
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            String typeName = getSelectedType();
            if (getSimulator().confirmBehaviour(Options.DELETE_TYPE_OPTION,
                String.format("Delete type graph '%s'?", typeName))) {
                int itemNr = getNameField().getSelectedIndex() + 1;
                if (itemNr == getNameField().getItemCount()) {
                    itemNr -= 2;
                }
                String newName =
                    itemNr >= 0 ? (String) getNameField().getItemAt(itemNr)
                            : null;
                doDelete(typeName);
                setSelectedType(newName);
                refreshAll();
            }
        }

        /** Deletes a given type graph from the grammar. */
        public void doDelete(String typeName) {
            getSimulator().doDeleteType(typeName);
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
     * {@link NewAction}.
     */
    private DisableAction getDisableAction() {
        if (this.disableAction == null) {
            this.disableAction = new DisableAction();
        }
        return this.disableAction;
    }

    /** Singular instance of the {@link EnableAction}. */
    private DisableAction disableAction;

    /** Action to disable the currently displayed type graph. */
    private class DisableAction extends RefreshableAction {
        public DisableAction() {
            super(Options.DISABLE_TYPE_ACTION_NAME, Groove.DISABLE_ICON);
        }

        public void actionPerformed(ActionEvent arg0) {
            SystemProperties oldProperties = getGrammarView().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setTypeName("");
            getSimulator().doSaveProperties(newProperties);
        }

        @Override
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarView().getTypeName() != null
                && !getGrammarView().getTypeName().isEmpty());
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private EnableAction getEnableAction() {
        if (this.enableAction == null) {
            this.enableAction = new EnableAction();
        }
        return this.enableAction;
    }

    /** Singular instance of the {@link EnableAction}. */
    private EnableAction enableAction;

    /** Action to enable the currently displayed type graph. */
    private class EnableAction extends RefreshableAction {
        public EnableAction() {
            super(Options.ENABLE_TYPE_ACTION_NAME, Groove.ENABLE_ICON);
        }

        public void actionPerformed(ActionEvent evt) {
            doEnable(getSelectedType());
        }

        /** Enables a type graph with a given name. */
        public void doEnable(String typeName) {
            SystemProperties oldProperties = getGrammarView().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setTypeName(typeName);
            getSimulator().doSaveProperties(newProperties);
        }

        @Override
        public void refresh() {
            setEnabled(isTypeSelected()
                && !getSelectedType().equals(getGrammarView().getTypeName()));
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private EditAction getEditAction() {
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
                getSimulator().doRenameType(type.getView(), newName);
                if (oldName.equals(getGrammarView().getTypeName())) {
                    getEnableAction().doEnable(newName);
                }
                setSelectedType(newName);
                refreshAll();
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
