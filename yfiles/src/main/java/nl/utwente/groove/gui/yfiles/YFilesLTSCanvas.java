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

import static nl.utwente.groove.gui.view.ViewOptions.SHOW_ABSENT_STATES_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_ANCHORS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_CALL_NESTING_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_CONTROL_STATE_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_INVARIANTS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_RECIPE_STEPS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_STATE_IDS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_STATE_STATUS_OPTION;
import static nl.utwente.groove.gui.view.ViewOptions.SHOW_SYSTEM_STATE_PROPERTIES_OPTION;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.LTSGraphCanvas;
import nl.utwente.groove.gui.view.LTSGraphViewController;
import nl.utwente.groove.gui.view.LTSGraphViewModel;
import nl.utwente.groove.gui.view.OptionRefreshListener;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;

/**
 * The yFiles canvas for labelled transition systems.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class YFilesLTSCanvas extends YFilesCanvas<GTS> implements LTSGraphCanvas {
    /** Creates a canvas as the canvas of a given controller. */
    public YFilesLTSCanvas(LTSGraphViewController controller) {
        super(controller);
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

    /* Options that change which states and transitions are shown reload the model. */
    @Override
    public @Nullable OptionRefreshListener getRefreshListener(String option) {
        return switch (option) {
        case SHOW_RECIPE_STEPS_OPTION -> new OptionRefreshListener(this) {
            @Override
            protected void doRefresh() {
                GTS gts = getGraph();
                if (gts != null && (gts.hasTransientStates() || gts.hasInternalSteps())) {
                    reloadModel();
                }
            }
        };
        case SHOW_ABSENT_STATES_OPTION -> new OptionRefreshListener(this) {
            @Override
            protected void doRefresh() {
                GTS gts = getGraph();
                if (gts != null && gts.hasAbsentStates()) {
                    reloadModel();
                }
            }
        };
        case SHOW_SYSTEM_STATE_PROPERTIES_OPTION -> new OptionRefreshListener(this) {
            @Override
            protected void doRefresh() {
                reloadModel();
            }
        };
        default -> super.getRefreshListener(option);
        };
    }

    /** Reloads the shown model from its transition system, without moving laid-out states. */
    private void reloadModel() {
        var model = getViewModel();
        if (model == null) {
            return;
        }
        model.setLayoutable(false);
        var lts = model.getGraph();
        assert lts != null; // a shown model has a graph
        model.loadGraph(lts);
        var controller = getController();
        if (controller.getFilter() != Filter.NONE) {
            controller.refreshFiltering();
        }
        refreshAll(true);
        controller.refreshActive();
        controller.doLayout(true);
        controller.scrollToActive();
    }

    /* The active state and transition belong to the model shown, so they are reset. */
    @Override
    public void setViewModel(@Nullable GraphViewModel<GTS> model) {
        getController().resetActive();
        super.setViewModel(model);
    }

    @Override
    public LTSGraphViewController getController() {
        return (LTSGraphViewController) super.getController();
    }

    @Override
    public @Nullable LTSGraphViewModel getViewModel() {
        return (LTSGraphViewModel) super.getViewModel();
    }

    @Override
    public LTSGraphViewModel getNonNullViewModel() {
        return (LTSGraphViewModel) super.getNonNullViewModel();
    }

    @Override
    public LTSGraphViewModel newViewModel() {
        return (LTSGraphViewModel) super.newViewModel();
    }

    @Override
    protected LTSGraphViewModel createViewModel(YFilesCellStore<GTS> store) {
        return new LTSGraphViewModel(getController(), store);
    }

    @Override
    public int setStateBound(int bound) {
        return getNonNullViewModel().setStateBound(bound);
    }

    @Override
    public int getStateBound() {
        return getNonNullViewModel().getStateBound();
    }

    @Override
    public boolean addElements(Collection<? extends GraphState> states,
                               Collection<? extends GraphTransition> transitions,
                               boolean replace) {
        return getNonNullViewModel().addElements(states, transitions, replace);
    }
}
