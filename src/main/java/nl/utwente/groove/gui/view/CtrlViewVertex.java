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

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.control.graph.ControlNode;
import nl.utwente.groove.util.AIGenerated;

/**
 * Vertex cell of a control graph view.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
public interface CtrlViewVertex extends ViewVertex<@NonNull ControlGraph> {
    @Override
    ControlNode getNode();
}
