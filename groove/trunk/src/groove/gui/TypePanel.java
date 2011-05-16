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
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.GraphRole;
import groove.gui.JTypeNameList.CheckBoxListModel;
import groove.gui.JTypeNameList.ListItem;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.CopyTypeAction;
import groove.gui.action.DeleteTypeAction;
import groove.gui.action.EditTypeAction;
import groove.gui.action.EnableTypesAction;
import groove.gui.action.NewTypeAction;
import groove.gui.action.RenameTypeAction;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JGraphMode;
import groove.view.FormatException;
import groove.view.StoredGrammarView;
import groove.view.TypeView;

import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypePanel extends JGraphPanel<AspectJGraph> implements
        SimulatorListener {
    /**
     * Constructor for this TypePanel Creates a new TypePanel instance and
     * instantiates all necessary variables.
     * @param simulator The simulator this type panel belongs to.
     */
    public TypePanel(final Simulator simulator) {
        super(new AspectJGraph(simulator, GraphRole.TYPE), true);
        setFocusable(false);
        initialise();
        setEnabled(false);
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = new JToolBar();
        result.add(createButton(getNewTypeAction()));
        result.add(createButton(getEditTypeAction()));
        result.add(getSimulator().getSaveGraphAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        result.addSeparator();
        result.add(createButton(getCopyTypeAction()));
        result.add(createButton(getDeleteTypeAction()));
        result.add(createButton(getRenameTypeAction()));
        result.addSeparator();
        result.add(new JLabel("Type Graphs: "));
        result.add(getNameListPane());
        result.addSeparator();
        result.add(createButton(getDisableTypesAction()));
        result.add(createButton(getEnableTypesAction()));
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

    @Override
    protected void installListeners() {
        super.installListeners();
        getSimulatorModel().addListener(this);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        this.selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    // This event is referencing the previously selected value,
                    // ignore it and wait for the event with the new value.
                    return;
                }
                int index = getNameList().getSelectedIndex();
                if (index >= 0) {
                    ListItem item = getNameListModel().getElementAt(index);
                    setSelectedType(item.dataItem);
                }
            }
        };
        activateListeners();
    }

    /** Activates the listeners that pass their actions to the simulator model. */
    private void activateListeners() {
        getNameList().addListSelectionListener(this.selectionListener);
    }

    /** Suspends the listeners that pass their actions to the simulator model. */
    private void suspendListeners() {
        getNameList().removeListSelectionListener(this.selectionListener);
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR) || changes.contains(Change.TYPE)) {
            StoredGrammarView grammar = source.getGrammar();
            this.typeJModelMap.clear();
            if (grammar != null) {
                getNameList().refreshTypes();
                // set either the type or the label store of the associated JGraph
                if (grammar.getActiveTypeNames().isEmpty()) {
                    getJGraph().setLabelStore(grammar.getLabelStore());
                } else {
                    try {
                        getJGraph().setType(grammar.toModel().getType(), null);
                    } catch (FormatException e) {
                        getJGraph().setLabelStore(grammar.getLabelStore());
                    }
                }
            }
            displayType();
            activateListeners();
        }
    }

    /**
     * Selects a type graph to be viewed.
     * @param name the type graph to be viewed; either <code>null</code> if
     *        there is no type graph in the current grammar, or an existing name
     *        in the type graph names of the current grammar.
     */
    public void setSelectedType(String name) {
        getSimulatorModel().setType(name);
    }

    /**
     * Returns a graph model for a given type view. The graph model is retrieved
     * from {@link #typeJModelMap}; if there is no image for the requested state
     * then one is created.
     */
    private AspectJModel getTypeJModel(TypeView graph) {
        AspectJModel result = this.typeJModelMap.get(graph);
        if (result == null) {
            result = getJGraph().newModel();
            result.loadGraph(graph.getAspectGraph());
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

    /** Sets the model according to the currently selected type. */
    private void displayType() {
        AspectJModel newModel;
        boolean enabled;
        TypeView type = getSimulatorModel().getType();
        if (type != null) {
            newModel = getTypeJModel(type);
            String typeName = type.getName();
            enabled =
                getSimulatorModel().getGrammar().getActiveTypeNames().contains(
                    typeName);
        } else {
            newModel = getJGraph().newModel();
            enabled = false;
        }
        // first set the enabling, then the model
        // in order to get the background right.
        if (newModel != getJModel()) {
            this.jGraph.setModel(newModel);
        }
        setEnabled(enabled);
        refreshStatus();
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

    /** Listener for selection changes in the list of types. */
    private ListSelectionListener selectionListener;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CopyTypeAction}.
     */
    private CopyTypeAction getCopyTypeAction() {
        if (this.copyTypeAction == null) {
            this.copyTypeAction = new CopyTypeAction(getSimulator());
        }
        return this.copyTypeAction;
    }

    /** Singular instance of the CopyTypeAction. */
    private CopyTypeAction copyTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link DeleteTypeAction}.
     */
    private DeleteTypeAction getDeleteTypeAction() {
        if (this.deleteTypeAction == null) {
            this.deleteTypeAction = new DeleteTypeAction(getSimulator());
        }
        return this.deleteTypeAction;
    }

    /** Singular instance of the DeleteTypeAction. */
    private DeleteTypeAction deleteTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link EnableTypesAction}.
     */
    private EnableTypesAction getDisableTypesAction() {
        if (this.disableTypesAction == null) {
            this.disableTypesAction =
                new EnableTypesAction(getSimulator(), false);
        }
        return this.disableTypesAction;
    }

    /** Singular instance of the EnableTypesAction. */
    private EnableTypesAction disableTypesAction;

    /**
     * Lazily creates and returns the appropriate instance of the
     * {@link EnableTypesAction}.
     */
    private EnableTypesAction getEnableTypesAction() {
        if (this.enableTypesAction == null) {
            this.enableTypesAction =
                new EnableTypesAction(getSimulator(), true);
        }
        return this.enableTypesAction;
    }

    /** Singular instance of the CheckAllAction. */
    private EnableTypesAction enableTypesAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link EditTypeAction}.
     */
    EditTypeAction getEditTypeAction() {
        if (this.editTypeAction == null) {
            this.editTypeAction = new EditTypeAction(getSimulator());
        }
        return this.editTypeAction;
    }

    /** Singular instance of the EditTypeAction. */
    private EditTypeAction editTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewTypeAction}.
     */
    NewTypeAction getNewTypeAction() {
        if (this.newTypeAction == null) {
            this.newTypeAction = new NewTypeAction(getSimulator());
        }
        return this.newTypeAction;
    }

    /** Singular instance of the NewTypeAction. */
    private NewTypeAction newTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link RenameTypeAction}.
     */
    private RenameTypeAction getRenameTypeAction() {
        if (this.renameTypeAction == null) {
            this.renameTypeAction = new RenameTypeAction(getSimulator());
        }
        return this.renameTypeAction;
    }

    /** Singular instance of the RenameTypeAction. */
    private RenameTypeAction renameTypeAction;

    /** Display name of this panel. */
    public static final String FRAME_NAME = "Type graph";

    private static class TypeNamesPane extends JScrollPane {
        TypeNamesPane(JTypeNameList nameList) {
            super(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.setViewportView(nameList);
            this.setMinimumSize(JTypeNameList.MIN_DIMENSIONS);
            this.setMaximumSize(JTypeNameList.MAX_DIMENSIONS);
            this.setPreferredSize(JTypeNameList.MAX_DIMENSIONS);
        }
    }
}
