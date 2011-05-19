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
        result.add(createButton(getActions().getNewTypeAction()));
        result.add(createButton(getActions().getEditTypeAction()));
        result.add(getActions().getSaveAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        result.addSeparator();
        result.add(createButton(getActions().getCopyTypeAction()));
        result.add(createButton(getActions().getDeleteTypeAction()));
        result.add(createButton(getActions().getRenameTypeAction()));
        result.addSeparator();
        result.add(new JLabel("Type Graphs: "));
        result.add(getNameListPane());
        result.addSeparator();
        result.add(createButton(getActions().getDisableTypesAction()));
        result.add(createButton(getActions().getEnableTypesAction()));
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
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.TYPE);
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
                    getSimulatorModel().setType(item.dataItem);
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
