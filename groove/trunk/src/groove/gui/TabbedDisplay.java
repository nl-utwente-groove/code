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

import groove.gui.action.ActionStore;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.view.View;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
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
abstract public class TabbedDisplay extends JTabbedPane implements Display,
        SimulatorListener {
    /**
     * Constructs a panel for a given simulator.
     */
    public TabbedDisplay(Simulator simulator) {
        super(BOTTOM);
        this.simulator = simulator;
        setFocusable(false);
        setBorder(new EmptyBorder(0, 0, -4, 0));
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
        addChangeListener(this.tabListener);
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
        removeChangeListener(this.tabListener);
        this.listening = false;
    }

    /** Indicates that the action listeners are currently active. */
    protected boolean isListening() {
        return this.listening;
    }

    @Override
    public JComponent getPanel() {
        return this;
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
            EditorPanel editor = getEditors().get(name);
            if (editor == null) {
                selectMainPanel(name);
                getMainPanel().repaint();
            } else {
                setSelectedComponent(editor);
            }
        }
    }

    /** 
     * Adds an editor panel for the given graph, or selects the 
     * one that already exists.
     */
    public void doEdit(AspectGraph graph) {
        EditorPanel result = getEditors().get(graph.getName());
        if (result == null) {
            result = addEditorPanel(graph);
            if (this.jModelMap.remove(graph.getName()) != null) {
                remove(getMainPanel());
            }
        }
        if (getSelectedComponent() == result) {
            getSimulatorModel().setDisplay(getKind());
        } else {
            setSelectedComponent(result);
        }
    }

    /** Creates and adds an editor panel for the given graph. */
    private EditorPanel addEditorPanel(AspectGraph graph) {
        final EditorPanel result = new EditorPanel(this, graph);
        this.editorMap.put(graph.getName(), result);
        addTab("", result);
        int index = indexOfComponent(result);
        setTabComponentAt(index, result.getTabComponent());
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
    public Map<String,EditorPanel> getEditors() {
        return this.editorMap;
    }

    /** 
     * Attempts to disposes the editor for certain aspect graphs, if any.
     * This is done in response to a change in the graph outside the editor.
     * @param graphs the graphs that are about to be changed and whose editor 
     * therefore needs to be disposed
     * @return {@code true} if the operation was not cancelled
     */
    public boolean disposeEditors(AspectGraph... graphs) {
        boolean result = true;
        Map<String,EditorPanel> editors = getEditors();
        for (AspectGraph graph : graphs) {
            EditorPanel editor = editors.get(graph.getName());
            if (editor != null) {
                result = editor.doCancel();
                if (!result) {
                    break;
                }
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
        for (EditorPanel editor : new ArrayList<EditorPanel>(
            getEditors().values())) {
            result = editor.doCancel();
            if (!result) {
                break;
            }
        }
        return result;
    }

    @Override
    public void removeTabAt(int index) {
        // removes the editor panel from the map
        boolean isIndexSelected = getSelectedIndex() == index;
        Component panel = getComponentAt(index);
        super.removeTabAt(index);
        if (panel instanceof EditorPanel) {
            String name = ((EditorPanel) panel).getName();
            this.editorMap.remove(name);
            if (isIndexSelected) {
                selectMainPanel(name);
            }
        }
        // make sure the tab component of the selected tab is enabled
        setTabEnabled(getSelectedIndex(), true);
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

    /**
     * Returns the name of the currently selected component, or {@code null}
     * if none is selected. 
     */
    public String getSelectedName() {
        String result = null;
        Component selection = getSelectedComponent();
        if (selection instanceof EditorPanel) {
            result = ((EditorPanel) selection).getName();
        } else if (selection == getMainPanel()) {
            result = getMainPanel().getGraph().getName();
        }
        return result;
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

    /** 
     * Removes the main panel from the display.
     */
    protected void removeMainPanel() {
        remove(getMainPanel());
    }

    /** 
     * Sets the main panel  to a given (named) graph.
     */
    protected void selectMainPanel(String name) {
        getMainPanel().setJModel(getJModel(name));
        TabLabel tabLabel = getMainPanel().getTabLabel();
        int index = indexOfComponent(getMainPanel());
        if (index < 0) {
            index = getMainPanelIndex();
            add(getMainPanel(), index);
            setTabComponentAt(index, tabLabel);
        }
        tabLabel.setEnabled(true);
        tabLabel.setTitle(getLabelText(name));
        tabLabel.setError(hasError(name));
        setSelectedIndex(index);
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
            result = !getView(name).getErrors().isEmpty();
        }
        return result;
    }

    private AspectJModel getJModel(String name) {
        AspectJModel result = this.jModelMap.get(name);
        if (result == null) {
            this.jModelMap.put(name, result =
                getMainPanel().getJGraph().newModel());
            result.loadGraph(getView(name).getAspectGraph());
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
    protected abstract View<?> getView(String name);

    /** Returns the simulator to which this panel belongs. */
    protected final Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator. */
    protected final SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the action store. */
    protected final ActionStore getActions() {
        return getSimulator().getActions();
    }

    /** The simulator to which this panel belongs. */
    private final Simulator simulator;

    /** Mapping from graph names to editors for those graphs. */
    private final Map<String,EditorPanel> editorMap =
        new HashMap<String,EditorPanel>();
    /** Mapping from names to graph models. */
    private final Map<String,AspectJModel> jModelMap =
        new HashMap<String,AspectJModel>();
    /** Flag indicating that the listeners are currently active. */
    private boolean listening;
    /** Listener that forwards tab changes to the simulator model. */
    private ChangeListener tabListener;
}
