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
 * $Id: DefaultConditionMatch.java,v 1.1 2007-10-02 16:14:57 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Node;
import groove.rel.VarNodeEdgeMap;

import java.util.Collection;

/**
 * Match wrapping an element map, from the object that has been matched
 * to the elements of the host graph in which it has been matched. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultConditionMatch implements Match {
    /** Constructs a match for a given {@link SPORule}. */
    public DefaultConditionMatch(VarNodeEdgeMap elementMap) {
        this.map = elementMap;
    }

    public Collection< ? extends Edge> getEdgeValues() {
        return getElementMap().edgeMap().values();
    }

    public Collection< ? extends Node> getNodeValues() {
        return getElementMap().nodeMap().values();
    }
    
    /** Returns the element map constituting the map. */
    public VarNodeEdgeMap getElementMap() {
        return map;
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DefaultConditionMatch
                && ((DefaultConditionMatch) obj).getElementMap().equals(getElementMap());
    }

    @Override
    public int hashCode() {
        // pre-compute the value, if not yet done
        if (hashCode == 0) {
            hashCode = computeHashCode();
            if (hashCode == 0) {
                hashCode = 1;
            }
        }
        return hashCode;
    }
    
    /** Computes a value for the hash code. */
    protected int computeHashCode() {
        return getElementMap().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Nodes %s, edges %s", getElementMap().nodeMap(), getElementMap().edgeMap());
    }

    /** The map constituting the match. */
    private final VarNodeEdgeMap map;
    /** The (pre-computed) hash code of this match. */
    private int hashCode;
}
