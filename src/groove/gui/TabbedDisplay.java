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

import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.view.GraphBasedModel;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

/**
 * Simulator tab that itself is a tabbed panel.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class TabbedDisplay extends ResourceDisplay implements
        SimulatorListener {
    /**
     * Constructs a panel for a given simulator.
     */
    public TabbedDisplay(Simulator simulator, DisplayKind kind) {
        super(simulator, kind);
    }

    /** Installs all listeners to this display. */
    protected void installListeners() {
        this.tabListener = new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                if (isListening()) {
                    suspendListeners();
                    selectionChanged();
                    activateListeners();
                }
                getListPanel().repaint();
            }
        };
        activateListeners();
    }

    /** Callback method that is invoked when the tab selection has changed. */
    abstract protected void selectionChanged();

    /** 
     * Activates those listeners that may give rise to a circular dependency.
     * This is only allowed if listening is currently suspended.
     * @see #isListening()
     * @see #suspendListeners()
     */
    protected void activateListeners() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        getPanel().addChangeListener(this.tabListener);
        this.listening = true;
    }

    /** 
     * Suspends those listeners that may give rise to a circular dependency.
     * This is only allowed if listening is currently active.
     * @see #isListening()
     * @see #activateListeners() 
     */
    protected void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        getPanel().removeChangeListener(this.tabListener);
        this.listening = false;
    }

    /** Indicates that the action listeners are currently active. */
    protected boolean isListening() {
        return this.listening;
    }

    @Override
    public MyTabbedPane getPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new MyTabbedPane();
        }
        return this.mainPanel;
    }

    /** Callback to obtain the main panel of this display. */
    abstract public JGraphPanel<AspectJGraph> getMainPanel();

    /** 
     * Selects an editor tab with a given name, or the main tab if there
     * is no such editor.
     */
    public void setSelectedTab(String name) {
        if (name == null) {
            removeMainPanel();
        } else {
            GraphEditorPanel editor = getEditors().get(name);
            if (editor == null) {
                selectMainPanel(name);
                getMainPanel().repaint();
            } else {
                getPanel().setSelectedComponent(editor);
            }
        }
    }

    /** 
     * Adds an editor panel for the given graph, or selects the 
     * one that already exists.
     */
    public void doEdit(AspectGraph graph) {
        GraphEditorPanel result = getEditors().get(graph.getName());
        if (result == null) {
            result = addEditorPanel(graph);
            if (this.jModelMap.remove(graph.getName()) != null) {
                getPanel().remove(getMainPanel());
            }
        }
        if (getPanel().getSelectedComponent() == result) {
            getSimulatorModel().setDisplay(getKind());
        } else {
            getPanel().setSelectedComponent(result);
        }
    }

    /** Creates and adds an editor panel for the given graph. */
    private GraphEditorPanel addEditorPanel(AspectGraph graph) {
        final GraphEditorPanel result = new GraphEditorPanel(this, graph);
        this.editorMap.put(graph.getName(), result);
        getPanel().addTab("", result);
        int index = getPanel().indexOfComponent(result);
        getPanel().setTabComponentAt(index, result.getTabLabel());
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
        Component selection = getPanel().getSelectedComponent();
        if (selection instanceof GraphEditorPanel) {
            result = ((GraphEditorPanel) selection).getName();
        } else if (selection == getMainPanel()) {
            result = getMainPanel().getGraph().getName();
        }
        return result;
    }

    /** 
     * Removes the main panel from the display.
     */
    protected void removeMainPanel() {
        getPanel().remove(getMainPanel());
    }

    /** 
     * Sets the main panel  to a given (named) graph.
     */
    protected void selectMainPanel(String name) {
        getMainPanel().setJModel(getJModel(name));
        TabLabel tabLabel = getMainPanel().getTabLabel();
        int index = getPanel().indexOfComponent(getMainPanel());
        if (index < 0) {
            index = getMainPanelIndex();
            getPanel().add(getMainPanel(), index);
            getPanel().setTabComponentAt(index, tabLabel);
        }
        tabLabel.setEnabled(true);
        tabLabel.setTitle(getLabelText(name));
        tabLabel.setError(hasError(name));
        getPanel().setSelectedIndex(index);
    }

    /** Clears the mapping from names to aspect models.
     * Should be called whenever the grammar changes, before
     * {@link #getJModel(String)} is called again.
     */
    final protected void clearJModelMap() {
        this.jModelMap.clear();
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
            result = !getResource(name).getErrors().isEmpty();
        }
        return result;
    }

    private AspectJModel getJModel(String name) {
        AspectJModel result = this.jModelMap.get(name);
        if (result == null) {
            this.jModelMap.put(name, result =
                getMainPanel().getJGraph().newModel());
            result.loadGraph(getResource(name).getSource());
        }
        return result;
    }

    /** Index of the pain panel. This returns {@code 0} by default. */
    protected int getMainPanelIndex() {
        return 0;
    }

    /** Retrieves the graph for a given name from the grammar,
     * according to the graph role represented by this display.
     */
    protected abstract GraphBasedModel<?> getResource(String name);

    private MyTabbedPane mainPanel;
    /** Mapping from graph names to editors for those graphs. */
    private final Map<String,GraphEditorPanel> editorMap =
        new HashMap<String,GraphEditorPanel>();
    /** Mapping from names to graph models. */
    private final Map<String,AspectJModel> jModelMap =
        new HashMap<String,AspectJModel>();
    /** Flag indicating that the listeners are currently active. */
    private boolean listening;
    /** Listener that forwards tab changes to the simulator model. */
    private ChangeListener tabListener;

    class MyTabbedPane extends JTabbedPane implements Panel {
        /** Constructs an instance of the panel. */
        public MyTabbedPane() {
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
                TabbedDisplay.this.editorMap.remove(name);
                if (isIndexSelected) {
                    selectMainPanel(name);
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
            return TabbedDisplay.this;
        }
    }
}
