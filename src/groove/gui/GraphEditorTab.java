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
 * $Id: EditorDialog.java,v 1.15 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.CancelEditAction;
import groove.gui.action.SaveGraphAction;
import groove.view.GrammarModel;
import groove.view.GraphBasedModel;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphEditorTab extends EditorTab implements SimulatorListener {
    /**
     * Constructs an instance of the dialog, for a given graph or rule.
     * @param parent the component on which this panel is placed
     * @param graph the input graph for the editor
     */
    public GraphEditorTab(final GraphDisplay parent, final AspectGraph graph) {
        super(parent);
        final Simulator simulator = parent.getSimulator();
        this.graph = graph;
        this.editor =
            new Editor(null, simulator.getOptions(),
                simulator.getModel().getGrammar().getProperties()) {
                @Override
                protected void updateTitle() {
                    updateDirty();
                }

                @Override
                protected void updateStatus() {
                    super.updateStatus();
                    TabLabel tab = getTabLabel();
                    tab.setError(!getModel().getErrorMap().isEmpty());
                }

                @Override
                JToolBar createToolBar() {
                    JToolBar result = GraphEditorTab.this.createToolBar();
                    addModeButtons(result);
                    addUndoButtons(result);
                    addCopyPasteButtons(result);
                    addGridButtons(result);
                    return result;
                }

                @Override
                public GraphRole getRole() {
                    return graph.getRole();
                }
            };
        setFocusCycleRoot(true);
        setName(graph.getName());
        simulator.getModel().addListener(this, Change.GRAMMAR);
    }

    /** Starts the editor with the graph passed in at construction time. */
    public void start() {
        this.editor.setTypeView(getSimulatorModel().getGrammar().getTypeModel());
        this.editor.setGraph(getGraph(), true);
        this.graph = null;
        setLayout(new BorderLayout());
        JSplitPane mainPanel = this.editor.getMainPanel();
        mainPanel.setBorder(null);
        add(mainPanel);
    }

    /** Returns the resulting aspect graph of the editor. */
    public AspectGraph getGraph() {
        return this.graph == null ? getEditor().getGraph() : this.graph;
    }

    /** Returns the editor instance of this panel. */
    public Editor getEditor() {
        return this.editor;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        assert changes.contains(Change.GRAMMAR);
        GrammarModel grammar = source.getGrammar();
        if (grammar == oldModel.getGrammar()) {
            // test if the graph being edited is still in the grammar;
            // if not, silently dispose it - it's too late to do anything else!
            AspectGraph graph = getGraph();
            GraphBasedModel<?> resource = null;
            switch (graph.getRole()) {
            case HOST:
                resource = grammar.getHostModel(graph.getName());
                break;
            case RULE:
                resource = grammar.getRuleModel(graph.getName());
                break;
            case TYPE:
                resource = grammar.getTypeModel(graph.getName());
                break;
            default:
                assert false;
            }
            if (resource != null) {
                this.editor.setTypeView(grammar.getTypeModel());
                // check if the properties have changed
                GraphProperties properties =
                    GraphInfo.getProperties(resource.getSource(), false);
                if (properties != null
                    && !properties.equals(GraphInfo.getProperties(getGraph(),
                        false))) {
                    AspectGraph newGraph = getGraph().clone();
                    GraphInfo.setProperties(newGraph, properties);
                    newGraph.setFixed();
                    change(newGraph);
                }
            } else {
                dispose();
            }
        } else {
            dispose();
        }
    }

    @Override
    protected boolean hasErrors() {
        return !this.editor.getModel().getErrorMap().isEmpty();
    }

    /** Indicates if the editor has unsaved changes. */
    @Override
    public boolean isDirty() {
        return getEditor().isDirty();
    }

    @Override
    public void setClean() {
        getEditor().setDirty(false);
    }

    /** Indicates if the editor is currently saving changes. */
    public boolean isSaving() {
        return this.saving;
    }

    /** Changes the edited graph. */
    public void change(AspectGraph newGraph) {
        assert newGraph.getName().equals(getGraph().getName())
            && newGraph.getRole() == getGraph().getRole();
        getEditor().setGraph(newGraph, false);
    }

    /** Renames the edited graph. */
    public void rename(String newName) {
        AspectGraph newGraph = getGraph().clone();
        newGraph.setName(newName);
        newGraph.setFixed();
        getEditor().setGraph(newGraph, false);
        setName(newName);
    }

    @Override
    protected void saveResource() {
        getSaveAction().doSave(getGraph());
    }

    /** Disposes the editor, by removing it as a listener and simulator panel component. */
    @Override
    public void dispose() {
        getSaveAction().dispose();
        getDisplay().getDisplayPanel().remove(this);
        getSimulatorModel().removeListener(this);
    }

    /** Creates and returns the cancel action. */
    @Override
    protected CancelEditAction getCancelAction() {
        return getSimulator().getActions().getCancelEditAction(
            getResourceKind());
    }

    /** Creates and returns the save action. */
    @Override
    public SaveGraphAction getSaveAction() {
        if (this.saveAction == null) {
            this.saveAction = new SaveGraphAction(this);
        }
        return this.saveAction;
    }

    /** Graph being edited.
     * This holds the graph as long as the editor is not yet initialised,
     * and then is set to {@code null}.
     * Use {@link #getGraph()} to access the graph.
     */
    private AspectGraph graph;
    private SaveGraphAction saveAction;
    /** The editor wrapped in the panel. */
    private final Editor editor;
    /** Flag indicating that the editor is in the process of saving. */
    private boolean saving;
}