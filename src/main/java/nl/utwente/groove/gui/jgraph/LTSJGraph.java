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
package nl.utwente.groove.gui.jgraph;

import static nl.utwente.groove.gui.Options.SHOW_ABSENT_STATES_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_ANCHORS_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_CALL_NESTING_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_CONTROL_STATE_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_INVARIANTS_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_RECIPE_STEPS_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_STATE_IDS_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_STATE_STATUS_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_SYSTEM_STATE_PROPERTIES_OPTION;

import java.awt.geom.Dimension2D;
import java.io.Serializable;
import java.util.Collection;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jgraph.graph.GraphModel;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.view.LTSGraphCanvas;
import nl.utwente.groove.gui.view.LTSGraphViewController;
import nl.utwente.groove.gui.view.LTSGraphViewModel;
import nl.utwente.groove.gui.view.OptionRefreshListener;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.collect.Matrix;
import nl.utwente.groove.util.line.MatrixFormat;
import nl.utwente.groove.gui.view.ViewVertex;

/**
 * Implementation of MyJGraph that provides the proper popup menu. To construct
 * an instance, setupPopupMenu() should be called after all global final
 * variables have been set.
 */
public class LTSJGraph extends JGraph<@NonNull GTS> implements LTSGraphCanvas, Serializable {
    /** Constructs an instance of the j-graph as the canvas of a given controller. */
    public LTSJGraph(LTSGraphViewController controller) {
        super(controller);
        // turn off double buffering to improve performance
        setDoubleBuffered(false);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        addOptionListener(SHOW_STATE_IDS_OPTION);
        addOptionListener(SHOW_STATE_STATUS_OPTION);
        addOptionListener(SHOW_CALL_NESTING_OPTION);
        addOptionListener(SHOW_CONTROL_STATE_OPTION);
        addOptionListener(SHOW_SYSTEM_STATE_PROPERTIES_OPTION);
        addOptionListener(SHOW_INVARIANTS_OPTION);
        addOptionListener(SHOW_ABSENT_STATES_OPTION);
        addOptionListener(SHOW_RECIPE_STEPS_OPTION);
        addOptionListener(SHOW_ANCHORS_OPTION);
    }

    @Override
    public OptionRefreshListener getRefreshListener(String option) {
        return switch (option) {
        case SHOW_RECIPE_STEPS_OPTION -> new OptionRefreshListener(this) {
            @Override
            protected void doRefresh() {
                GTS gts = getGraph();
                if (gts != null && (gts.hasTransientStates() || gts.hasInternalSteps())) {
                    reloadJModel();
                }
            }
        };
        case SHOW_ABSENT_STATES_OPTION -> new OptionRefreshListener(this) {
            @Override
            protected void doRefresh() {
                GTS gts = getGraph();
                if (gts != null && gts.hasAbsentStates()) {
                    reloadJModel();
                }
            }
        };
        case SHOW_SYSTEM_STATE_PROPERTIES_OPTION -> new OptionRefreshListener(this) {
            @Override
            protected void doRefresh() {
                reloadJModel();
            }
        };
        default -> super.getRefreshListener(option);
        };
    }

    /** Reloads the graph in the {@link JModel}, after
     * a view option has changed.
     */
    private void reloadJModel() {
        var jModel = getModel();
        assert jModel != null;
        jModel.setLayoutable(false);
        var lts = jModel.getGraph();
        assert lts != null;
        jModel.loadGraph(lts);
        var controller = getController();
        if (controller.getFilter() != Filter.NONE) {
            controller.refreshFiltering();
        }
        refreshAll(true);
        controller.refreshActive();
        doLayout(true);
        controller.scrollToActive();
    }

    @Override
    public void setModel(GraphModel model) {
        // reset the active state and transition
        if (hasController()) { // not the case during construction
            getController().resetActive();
        }
        super.setModel(model);
    }

    /** Specialises the return type to a {@link LTSJModel}. */
    @Override
    public LTSJModel getModel() {
        return (LTSJModel) super.getModel();
    }

    /* Specialises the return type to a {@link LTSJModel}. */
    @Override
    public LTSJModel getNonNullModel() {
        return (LTSJModel) super.getNonNullModel();
    }

    /* Specialises the return type. */
    @Override
    public LTSGraphViewController getController() {
        return (LTSGraphViewController) super.getController();
    }

    @Override
    public @Nullable LTSGraphViewModel getViewModel() {
        var model = getModel();
        return model == null
            ? null
            : model.getViewModel();
    }

    @Override
    public LTSGraphViewModel newViewModel() {
        return ((LTSJModel) newModel()).getViewModel();
    }

    @Override
    public int setStateBound(int bound) {
        return getNonNullModel().setStateBound(bound);
    }

    @Override
    public int getStateBound() {
        return getNonNullModel().getStateBound();
    }

    @Override
    public boolean addElements(Collection<? extends GraphState> states,
                               Collection<? extends GraphTransition> transitions,
                               boolean replace) {
        return getNonNullModel().addElements(states, transitions, replace);
    }

    @Override
    Dimension2D computePreferredSize(JVertexView view) {
        Dimension2D result;
        if (FAST_SIZE) {
            ViewVertex<?> vertex = view.getCell();
            var label = vertex.getVisuals().getLabel();
            var matrix = label.toBuilder(MatrixFormat.instance());
            result = this.sizeMatrix.lookup(matrix.getWidth(), matrix.getHeight());
            if (result == null) {
                result = super.computePreferredSize(view);
                this.sizeMatrix.store(matrix.getWidth(), matrix.getHeight(), result);
            }
        } else {
            result = super.computePreferredSize(view);
        }
        return result;
    }

    private final Matrix<Dimension2D> sizeMatrix = new Matrix<>();

    @Override
    protected JGraphFactory<@NonNull GTS> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<@NonNull GTS> {
        public MyFactory() {
            super(LTSJGraph.this);
        }

        /* The node is expected to be a non-null GraphState. */
        @Override
        public LTSJVertex newJVertex(Node node) {
            assert node instanceof GraphState;
            return LTSJVertex.newInstance();
        }

        /* The edge is expected to be a non-null GraphTransition. */
        @Override
        public LTSJEdge newJEdge(Edge edge) {
            assert edge instanceof GraphTransition;
            return LTSJEdge.newInstance();
        }

        @Override
        public LTSJModel newModel() {
            return new LTSJModel((LTSJGraph) getJGraph());
        }
    }

    /** Flag indicating if the label size computation should be fast and sloppy. */
    static private final boolean FAST_SIZE = false;
}