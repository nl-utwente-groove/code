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

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.gui.view.LTSGraphViewModel;
import nl.utwente.groove.lts.GTS;

/**
 * JGraph model adapter of an {@link LTSGraphViewModel}.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class LTSJModel extends JModel<@NonNull GTS> {
    /** Creates a new model for a given LTS JGraph. */
    LTSJModel(LTSJGraph jGraph) {
        super(jGraph);
    }

    @Override
    public LTSGraphViewModel getViewModel() {
        return (LTSGraphViewModel) super.getViewModel();
    }

    /* Specialises the return type. */
    @Override
    public LTSJGraph getJGraph() {
        return (LTSJGraph) super.getJGraph();
    }

    /**
     * Possibly extends the model with additional states from the underlying GTS.
     * This can be more efficient than reloading, e.g., if the state bound has increased.
     */
    public boolean reloadGraph() {
        return getViewModel().reloadGraph();
    }

    /**
     * Sets the maximum state number to be added.
     * @return the previous bound
     */
    public int setStateBound(int bound) {
        return getViewModel().setStateBound(bound);
    }

    /** Returns the maximum state number to be displayed. */
    public int getStateBound() {
        return getViewModel().getStateBound();
    }

    /** Indicates if the model is set to exploring mode. */
    public boolean isExploring() {
        return getViewModel().isExploring();
    }

    /** Sets or resets the exploring mode. */
    public void setExploring(boolean exploring) {
        getViewModel().setExploring(exploring);
    }

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = LTSGraphViewModel.DEFAULT_LTS_NAME;
}
