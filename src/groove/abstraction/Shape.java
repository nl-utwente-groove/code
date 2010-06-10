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
package groove.abstraction;

import groove.graph.DefaultGraph;

import java.util.HashMap;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class Shape extends DefaultGraph {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final EquivRelation<ShapeNode> equivRel;
    private final HashMap<ShapeNode,Multiplicity> nodeMultMap;
    private final HashMap<ShapeEdge,Multiplicity> outEdgeMultMap;
    private final HashMap<ShapeEdge,Multiplicity> inEdgeMultMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public Shape() {
        super();
        this.equivRel = new EquivRelation<ShapeNode>();
        this.nodeMultMap = new HashMap<ShapeNode,Multiplicity>();
        this.outEdgeMultMap = new HashMap<ShapeEdge,Multiplicity>();
        this.inEdgeMultMap = new HashMap<ShapeEdge,Multiplicity>();
    }

}
