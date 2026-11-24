/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.gui.yfiles;

import static nl.utwente.groove.gui.view.ViewOptions.SHOW_ASPECTS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_VALUE_NODES_OPTION;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.accessibility.AccessibleState;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectViewEdge;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.OptionRefreshListener;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewOptions;
import nl.utwente.groove.util.AIGenerated;

/**
 * The yFiles canvas for aspect graphs (host graphs, rules, type graphs and states).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class YFilesAspectCanvas extends YFilesCanvas<AspectGraph> implements AspectGraphCanvas {
    /** Creates a canvas as the canvas of a given controller. */
    public YFilesAspectCanvas(AspectGraphViewController controller) {
        super(controller);
        setEditable(controller.isEditing());
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        addOptionListener(SHOW_ASPECTS_OPTION);
        addOptionListener(SHOW_VALUE_NODES_OPTION);
    }

    @Override
    public AspectGraphViewController getController() {
        return (AspectGraphViewController) super.getController();
    }

    @Override
    public @Nullable AspectGraphViewModel getViewModel() {
        return (AspectGraphViewModel) super.getViewModel();
    }

    @Override
    public AspectGraphViewModel newViewModel() {
        return (AspectGraphViewModel) super.newViewModel();
    }

    @Override
    protected AspectGraphViewModel createViewModel(YFilesCellStore<AspectGraph> store) {
        var result = new AspectGraphViewModel(getController(), store);
        result.setGrammar(getGrammar());
        return result;
    }

    /** Returns the grammar of the controller. */
    private GrammarModel getGrammar() {
        GrammarModel result = getController().getGrammar();
        assert result != null : "Can't create aspect canvases without grammar model";
        return result;
    }

    @Override
    public AspectGraphViewModel showGraph(AspectGraph graph) {
        return (AspectGraphViewModel) super.showGraph(graph);
    }

    @Override
    public GraphBasedModel<?> getResourceModel() {
        return getNonNullViewModel().getResourceModel();
    }

    @Override
    public TypeGraph getTypeGraph() {
        return getNonNullViewModel().getTypeGraph();
    }

    /*
     * The rebuilds of the model's graph reach the canvas listeners as graph changes,
     * as they do from the JGraph backend: the label tree rebuilds itself on them.
     */
    @Override
    public void setViewModel(@Nullable GraphViewModel<AspectGraph> model) {
        if (getViewModel() instanceof AspectGraphViewModel oldModel) {
            oldModel.removeGraphChangeListener(this.graphChangeListener);
        }
        super.setViewModel(model);
        if (model instanceof AspectGraphViewModel newModel) {
            newModel.addGraphChangeListener(this.graphChangeListener);
        }
    }

    /** Forwards the graph rebuilds of the current model to the canvas listeners. */
    private final PropertyChangeListener graphChangeListener = evt -> notifyGraphChanged();

    @Override
    public AspectGraphViewModel getNonNullViewModel() {
        return AspectGraphCanvas.super.getNonNullViewModel();
    }

    @Override
    public GraphViewMode getDefaultMode() {
        return getController().isEditing()
            ? GraphViewMode.EDIT_MODE
            : super.getDefaultMode();
    }

    /* Errors on edges that are shown as labels of their source are selected as that source. */
    @Override
    public void selectElements(Collection<? extends Element> elems) {
        var model = getNonNullViewModel();
        var errorCells = new LinkedHashSet<AspectViewCell>();
        for (var elem : elems) {
            AspectViewCell errorCell = model.getCell(elem);
            if (errorCell == null && elem instanceof Edge e) {
                errorCell = model.getCell(e.source());
            } else if (errorCell instanceof AspectViewEdge e && e.isSourceLabel()) {
                errorCell = e.getSourceVertex();
            }
            if (errorCell != null) {
                errorCells.add(errorCell);
            }
        }
        if (!errorCells.isEmpty()) {
            List<ViewCell<AspectGraph>> cells = new ArrayList<>();
            for (var cell : errorCells) {
                cells.add(cell);
            }
            select(cells);
        }
    }

    /* Merging bidirectional edges changes the cells, so the model is rebuilt rather than refreshed. */
    @Override
    public @Nullable OptionRefreshListener getRefreshListener(String option) {
        if (option.equals(ViewOptions.SHOW_BIDIRECTIONAL_EDGES_OPTION)) {
            return new RebuildListener();
        }
        return super.getRefreshListener(option);
    }

    /** Listener rebuilding the view model from its graph when an option changes. */
    private class RebuildListener extends OptionRefreshListener {
        /** Creates a listener for this canvas. */
        RebuildListener() {
            super(YFilesAspectCanvas.this);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (isEnabled()) {
                rebuild();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(AccessibleState.ENABLED.toDisplayString())
                && isEnabled()) {
                rebuild();
            }
        }

        /**
         * Replaces the view model by a fresh one loaded from the same graph, carrying over
         * whether it is being edited. Does nothing if there is no model or graph to rebuild from.
         */
        private void rebuild() {
            var oldModel = getViewModel();
            var graph = oldModel == null
                ? null
                : oldModel.getGraph();
            if (oldModel == null || graph == null) {
                return;
            }
            var newModel = newViewModel();
            newModel.setBeingEdited(oldModel.isBeingEdited());
            newModel.loadGraph(graph);
            setViewModel(newModel);
        }
    }
}
