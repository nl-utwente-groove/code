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

import groove.grammar.aspect.AspectEdge;
import groove.grammar.type.Multiplicity;
import groove.graph.GraphRole;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJVertex;

/**
 * Value of the edge source or target label (typically the outgoing/incoming multiplicity).
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeEndLabelValue extends AspectValue<String> {
    /** Creates an label refresher for either the source or the target label. */
    public EdgeEndLabelValue(boolean source) {
        this.source = source;
    }

    @Override
    protected String getForJVertex(AspectJVertex jVertex) {
        return null;
    }

    @Override
    protected String getForJEdge(AspectJEdge jEdge) {
        String result = null;
        AspectEdge edge = jEdge.getEdge();
        // the edge could be null, if we're in the process of adding a JEdge
        if (edge != null && edge.getGraphRole() == GraphRole.TYPE) {
            Multiplicity mult =
                this.source ? edge.getOutMult() : edge.getInMult();
            if (mult != null) {
                result = mult.toString();
            }
        }
        return result;
    }

    private final boolean source;
}
