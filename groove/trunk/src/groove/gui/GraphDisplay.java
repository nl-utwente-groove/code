/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.gui;

import groove.graph.GraphRole;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.trans.ResourceKind;
import groove.view.FormatException;
import groove.view.GrammarModel;
import groove.view.GraphBasedModel;
import groove.view.TypeModel;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

/**
 * Simulator tab that itself is a tabbed panel.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class GraphDisplay extends TabbedResourceDisplay implements
        SimulatorListener {
    /**
     * Constructs a panel for a given simulator and (graph-based) resource kind.
     */
    public GraphDisplay(Simulator simulator, ResourceKind resource) {
        super(simulator, resource);
        assert resource.isGraphBased();
    }

    @Override
    public GraphDisplayPanel getDisplayPanel() {
        if (this.displayPanel == null) {
            this.displayPanel = new GraphDisplayPanel();
        }
        return this.displayPanel;
    }

    @Override
    protected JTabbedPane getTabPane() {
        // In this display class, the tab pane is the same as the display panel
        return getDisplayPanel();
    }

    @Override
    abstract public GraphTab getMainTab();

    /** 
     * Switches to the display of a given (named) resource,
     * either in an open editor or in the main tab.
     */
    public void setSelected(String name) {
        if (name == null) {
            removeMainTab();
        } else {
            GraphEditorPanel editor = getEditors().get(name);
            if (editor == null) {
                selectMainTab(name);
                getMainTab().repaint();
            } else {
                getTabPane().setSelectedComponent(editor);
            }
        }
    }

    /** 
     * Adds an editor panel for the given graph, or selects the 
     * one that already exists.
     */
    @Override
    public void createEditor(String name) {
        GraphEditorPanel result = getEditors().get(name);
        if (result == null) {
            result =
                addEditorPanel(getSimulatorModel().getStore().getGraphs(
                    getResource()).get(name));
            if (getMainTab().removeResource(name)) {
                getTabPane().remove(getMainTab().getComponent());
            }
        }
        if (getTabPane().getSelectedComponent() == result) {
            getSimulatorModel().setDisplay(getKind());
        } else {
            getTabPane().setSelectedComponent(result);
        }
    }

    /** Creates and adds an editor panel for the given graph. */
    private GraphEditorPanel addEditorPanel(AspectGraph graph) {
        final GraphEditorPanel result = new GraphEditorPanel(this, graph);
        this.editorMap.put(graph.getName(), result);
        getTabPane().addTab("", result);
        int index = getTabPane().indexOfComponent(result);
        getTabPane().setTabComponentAt(index, result.getTabLabel());
        // start the editor only after it has been added
        result.start();
        // make sure the list keeps track of dirty editors
        // this has to be done after starting the editor, as otherwise the
        // JModel will change
        result.getEditor().getModel().addGraphModelListener(
            new GraphModelListener() {
                @Override
                public void graphChanged(GraphModelEvent e) {
                    getListPanel().revalidate();
                    getListPanel().repaint();
                }
            });
        return result;
    }

    /** Returns a list of all editor panels currently displayed. */
    public Map<String,GraphEditorPanel> getEditors() {
        return this.editorMap;
    }

    @Override
    public boolean cancelEditing(String name, boolean confirm) {
        boolean result = true;
        GraphEditorPanel editor = getEditors().get(name);
        if (editor != null) {
            result = editor.cancelEditing(true);
        }
        return result;
    }

    /** 
     * Attempts to disposes the editor for certain aspect graphs, if any.
     * This is done in response to a change in the graph outside the editor.
     * @param names the graphs that are about to be changed and whose editor 
     * therefore needs to be disposed
     * @return {@code true} if the operation was not cancelled
     */
    public boolean disposeEditors(String... names) {
        boolean result = true;
        for (String name : names) {
            result = cancelEditing(name, true);
            if (!result) {
                break;
            }
        }
        return result;
    }

    /** 
     * Attempts to save the dirty editors, asking the user what should happen.
     * Disposes the editors if the action is not cancelled.
     * @return {@code true} if the operation was not cancelled
     */
    public boolean disposeAllEditors() {
        boolean result = true;
        for (GraphEditorPanel editor : new ArrayList<GraphEditorPanel>(
            getEditors().values())) {
            result = editor.cancelEditing(true);
            if (!result) {
                break;
            }
        }
        return result;
    }

    /**
     * Returns the name of the currently selected component, or {@code null}
     * if none is selected. 
     */
    public String getSelectedName() {
        String result = null;
        Component selection = getDisplayPanel().getSelectedComponent();
        if (selection instanceof GraphEditorPanel) {
            result = ((GraphEditorPanel) selection).getName();
        } else if (selection == getMainTab()) {
            result = getMainTab().getName();
        }
        return result;
    }

    /** 
     * Removes the main panel from the display.
     */
    protected void removeMainTab() {
        getTabPane().remove(getMainTab().getComponent());
    }

    /** 
     * Sets the main panel  to a given (named) graph.
     */
    protected void selectMainTab(String name) {
        getMainTab().setResource(name);
        TabLabel tabLabel = getMainTab().getTabLabel();
        int index = getTabPane().indexOfComponent(getMainTab().getComponent());
        if (index < 0) {
            index = getMainTabIndex();
            getTabPane().add(getMainTab().getComponent(), index);
            getTabPane().setTabComponentAt(index, tabLabel);
        }
        tabLabel.setEnabled(true);
        tabLabel.setTitle(getLabelText(name));
        tabLabel.setError(hasError(name));
        getDisplayPanel().setSelectedIndex(index);
    }

    /**
     * Callback method to construct the label for a given (named) graph
     * that should be used in the label list.
     */
    final protected Icon getListIcon(String name) {
        Icon result;
        if (this.editorMap.containsKey(name)) {
            result = Icons.EDIT_ICON;
        } else {
            result = getKind().getListIcon();
        }
        return result;
    }

    /**
     * Callback method to construct the string description for a 
     * given (named) graph that should be used in the label list and
     * tab component.
     */
    protected String getLabelText(String name) {
        StringBuilder result = new StringBuilder();
        if (this.editorMap.containsKey(name)) {
            result.append(this.editorMap.get(name).getTitle());
        } else {
            result.append(name);
        }
        decorateLabelText(name, result);
        return result.toString();
    }

    /** 
     * Adds HTML formatting to the label text for the main display.
     * Callback method from {@link #getLabelText(String)}.
     * @param name the name of the displayed object. This determines the
     * decoration
     * @param text the text to be decorated
     */
    protected void decorateLabelText(String name, StringBuilder text) {
        // empty
    }

    /** Indicates if a given (named) graph has errors. */
    final protected boolean hasError(String name) {
        boolean result;
        if (this.editorMap.containsKey(name)) {
            result = this.editorMap.get(name).hasErrors();
        } else {
            result = getResource(name).hasErrors();
        }
        return result;
    }

    /** Index of the pain panel. This returns {@code 0} by default. */
    protected int getMainTabIndex() {
        return 0;
    }

    /** Retrieves the graph for a given name from the grammar,
     * according to the graph role represented by this display.
     */
    protected abstract GraphBasedModel<?> getResource(String name);

    private GraphDisplayPanel displayPanel;
    /** Mapping from graph names to editors for those graphs. */
    private final Map<String,GraphEditorPanel> editorMap =
        new HashMap<String,GraphEditorPanel>();

    class GraphDisplayPanel extends JTabbedPane implements Panel {
        /** Constructs an instance of the panel. */
        public GraphDisplayPanel() {
            super(BOTTOM);
            setFocusable(false);
            setBorder(new EmptyBorder(0, 0, -4, 0));
        }

        @Override
        public void removeTabAt(int index) {
            // removes the editor panel from the map
            boolean isIndexSelected = getSelectedIndex() == index;
            Component panel = getComponentAt(index);
            super.removeTabAt(index);
            if (panel instanceof GraphEditorPanel) {
                String name = ((GraphEditorPanel) panel).getName();
                GraphDisplay.this.editorMap.remove(name);
                if (isIndexSelected) {
                    selectMainTab(name);
                }
            }
            // make sure the tab component of the selected tab is enabled
            setTabEnabled(getSelectedIndex(), true);
            getListPanel().repaint();
        }

        @Override
        public void setSelectedIndex(int index) {
            int selectedIndex = getSelectedIndex();
            if (index == selectedIndex) {
                if (isListening()) {
                    selectionChanged();
                }
            } else {
                if (selectedIndex >= 0) {
                    setTabEnabled(selectedIndex, false);
                }
                super.setSelectedIndex(index);
                setTabEnabled(index, true);
            }
        }

        /** Changes the enabled status of the tab component at a given index. */
        private void setTabEnabled(int index, boolean enabled) {
            if (index >= 0) {
                Component label = getTabComponentAt(index);
                if (label != null) {
                    label.setEnabled(enabled);
                }
            }
        }

        @Override
        public Display getDisplay() {
            return GraphDisplay.this;
        }
    }

    abstract public static class GraphTab extends JGraphPanel<AspectJGraph>
            implements Tab {
        public GraphTab(Simulator simulator, GraphRole role) {
            super(new AspectJGraph(simulator, role), false);
            this.resourceKind = ResourceKind.toResource(role);
            this.simulatorModel = simulator.getModel();
            setFocusable(false);
            setEnabled(false);
        }

        @Override
        public void setResource(String name) {
            AspectJModel jModel = this.jModelMap.get(name);
            if (jModel == null) {
                this.jModelMap.put(name, jModel = getJGraph().newModel());
                AspectGraph graph =
                    this.simulatorModel.getStore().getGraphs(this.resourceKind).get(
                        name);
                jModel.loadGraph(graph);
            }
            setJModel(jModel);
        }

        public boolean removeResource(String name) {
            return this.jModelMap.remove(name) != null;
        }

        @Override
        protected final TabLabel createTabLabel() {
            return new TabLabel(this, Icons.getMainTabIcon(this.resourceKind),
                "");
        }

        @Override
        protected final JToolBar createToolBar() {
            return null;
        }

        @Override
        public Component getComponent() {
            return this;
        }

        public void updateGrammar(GrammarModel grammar) {
            this.jModelMap.clear();
            if (grammar == null) {
                getJGraph().setType(null, null);
            } else {
                // set either the type or the label store of the associated JGraph
                try {
                    TypeGraph type = grammar.getTypeModel().toResource();
                    Map<String,Set<TypeLabel>> labelsMap =
                        new HashMap<String,Set<TypeLabel>>();
                    for (String typeName : grammar.getTypeNames()) {
                        TypeModel typeModel = grammar.getTypeModel(typeName);
                        // the view may be null if type names
                        // overlap modulo upper/lowercase
                        if (typeModel != null && typeModel.isEnabled()) {
                            labelsMap.put(typeName, typeModel.getLabels());
                        }
                    }
                    getJGraph().setType(type, labelsMap);
                } catch (FormatException e) {
                    getJGraph().setLabelStore(grammar.getLabelStore());
                }
            }
            refreshStatus();
        }

        private final SimulatorModel simulatorModel;
        private final ResourceKind resourceKind;
        /** Mapping from resource names to aspect models. */
        private final Map<String,AspectJModel> jModelMap =
            new HashMap<String,AspectJModel>();
    }
}
