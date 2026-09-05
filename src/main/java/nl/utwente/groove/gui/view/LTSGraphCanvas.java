/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
 */
package nl.utwente.groove.gui.view;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;

/**
 * Canvas showing a transition system. The content model of an LTS canvas is built
 * incrementally as the transition system is explored, up to a bound on the number of
 * states.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public interface LTSGraphCanvas extends GraphCanvas<GTS> {
    @Override
    LTSGraphViewController getController();

    @Override
    @Nullable
    LTSGraphViewModel getViewModel();

    @Override
    LTSGraphViewModel newViewModel();

    /**
     * Sets the maximum number of states to be shown.
     * @return the previous bound
     */
    int setStateBound(int bound);

    /** Returns the maximum number of states to be shown. */
    int getStateBound();

    /**
     * Adds states and transitions to the content model, within the state bound.
     * @param replace if {@code true}, the canvas is refreshed afterwards
     * @return {@code true} if the content model changed
     */
    boolean addElements(Collection<? extends GraphState> states,
                        Collection<? extends GraphTransition> transitions, boolean replace);
}
