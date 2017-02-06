/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui.look;

import groove.graph.Graph;
import groove.gui.jgraph.AspectJCell;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;

/**
 * Refresher for the {@link VisualKey#ERROR} value of a {@link AspectJCell}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ErrorValue implements VisualValue<Boolean> {
    @Override
    public <G extends Graph> Boolean get(JGraph<G> jGraph, JCell<G> cell) {
        return cell.hasErrors();
    }
}
