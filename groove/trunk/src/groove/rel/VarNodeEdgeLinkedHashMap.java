/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.rel;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An implementation of VarNodeEdgeMap that
 * relies on {@link LinkedHashMap}s for the variable, node and edge maps.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VarNodeEdgeLinkedHashMap extends RuleToStateHashMap {
    /**
     * Constructs an empty map.
     */
    public VarNodeEdgeLinkedHashMap() {
        // empty constructor
    }

    @Override
    protected Map<LabelVar,Label> createValuation() {
        return new LinkedHashMap<LabelVar,Label>();
    }

    @Override
    protected Map<RuleEdge,Edge> createEdgeMap() {
        return new LinkedHashMap<RuleEdge,Edge>();
    }

    @Override
    protected Map<RuleNode,Node> createNodeMap() {
        return new LinkedHashMap<RuleNode,Node>();
    }
}
