/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.view;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.util.AIGenerated;

/**
 * The cells of a graph view carrying a given label.
 * This is what a label filter offers the graph view to build its
 * show/hide menu from.
 * @param <G> the type of graphs shown in the graph view
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public record LabelledCells<G extends Graph>(Label label, Set<ViewCell<G>> cells) {
    // empty
}
