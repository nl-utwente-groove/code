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

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.CellChange;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.util.QualName;

/** Display tab component showing a graph-based resource. */
final public class AspectViewTab extends AspectTab {
    /**
     * Constructs the instance of this tab for a given simulator and
     * resource kind.
     */
    public AspectViewTab(ResourceDisplay display) {
        super(display);
        setFocusable(false);
        setEnabled(false);
        start();
    }

    @Override
    protected void start() {
        super.start();
        getCanvas().setToolTipEnabled(true);
        getCanvas().getComponent().addMouseListener(new EditMouseListener());
        getCanvas().addCanvasListener(new GraphCanvasListener<@NonNull AspectGraph>() {
            @Override
            public void cellsChanged(GraphCanvas<@NonNull AspectGraph> canvas,
                                     CellChange<@NonNull AspectGraph> change) {
                storeGraph();
            }
        });
    }

    /**
     * Stores the current graph (with its changed layout) back into the grammar.
     * Cell changes during loading are the graph's own layout arriving, and are
     * ignored.
     */
    private void storeGraph() {
        var viewModel = getViewModel();
        if (viewModel == null || viewModel.isLoading()) {
            return;
        }
        try {
            viewModel.syncGraph();
            var graph = viewModel.getGraph();
            assert graph != null; // a model shown on the canvas has a graph
            getSimulatorModel().doAddGraph(getResourceKind(), graph, true);
            loadProperties(viewModel);
        } catch (IOException e1) {
            // do nothing
        }
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setClean() {
        // do nothing
    }

    @Override
    protected void saveResource() {
        // do nothing
    }

    @Override
    public Icon getIcon() {
        return Icons.getMainTabIcon(getResourceKind());
    }

    @Override
    final public boolean isEditor() {
        return false;
    }

    /** Loads the properties of a given model into the properties panel. */
    private void loadProperties(AspectGraphViewModel model) {
        loadProperties(model.getProperties(), model.getGraph());
    }

    @Override
    public boolean setResource(@Nullable QualName name) {
        AspectGraphViewModel model = this.viewModelMap.get(name);
        if (model == null && name != null) {
            AspectGraph graph = getSimulatorModel().getGrammar().getModelGraph(getResourceKind(), name);
            if (graph != null) {
                this.viewModelMap.put(name, model = getCanvas().newViewModel());
                model.loadGraph(graph);
            }
        }
        if (model == null) {
            name = null;
        }
        getCanvas().setViewModel(model);
        if (model != null) {
            loadProperties(model);
        }
        setQualName(name);
        String nameString = name == null
            ? null
            : name.toString();
        getTabLabel().setTitle(nameString);
        var resource = getResource();
        if (resource != null) {
            Color background = getResource().isActive()
                ? Values.ACTIVE_BACKGROUND
                : Values.INACTIVE_BACKGROUND;
            getEditArea().setEnabledBackground(background);
            getLabelTree().setBackground(background);
            var levelTree = getLevelTree();
            if (levelTree != null) {
                levelTree.setBackground(background);
            }
            getPropertiesPanel().setBackground(background);
        }
        updateErrors();
        updatePropertiesNotable();
        return model != null;
    }

    @Override
    public boolean removeResource(QualName name) {
        boolean result = name.equals(getQualName());
        this.viewModelMap.remove(name);
        if (result) {
            setResource(null);
        }
        return result;
    }

    @Override
    public void updateGrammar(GrammarModel grammar) {
        this.viewModelMap.clear();
        setResource(getQualName());
    }

    /** Mapping from resource names to view models. */
    private final Map<QualName,AspectGraphViewModel> viewModelMap = new HashMap<>();
}
