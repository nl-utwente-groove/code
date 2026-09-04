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

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;

/**
 * Edge cell of a transition system view, wrapping one or more transitions.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
public interface LTSViewEdge extends LTSViewCell, ViewEdge<@NonNull GTS> {
    @Override
    GraphTransition getEdge();

    @Override
    Set<? extends GraphTransition> getEdges();
}
