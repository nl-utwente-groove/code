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
 * $Id: ExistentialMatch.java,v 1.1 2007-10-03 16:08:40 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Node;
import groove.rel.VarNodeEdgeMap;

import java.util.Collection;

/**
 * Match of an existential condition.
 * The match wraps an element map, from the object that has been matched
 * to the elements of the host graph in which it has been matched. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExistentialMatch extends CompositeMatch {
    /** Constructs a match for a given {@link SPORule}. */
    public ExistentialMatch(GraphCondition condition, VarNodeEdgeMap matchMap) {
        this.condition = condition;
        this.matchMap = matchMap;
    }

    @Override
    public Collection<Edge> getEdgeValues() {
        Collection<Edge> result = super.getEdgeValues();
        result.addAll(matchMap().edgeMap().values());
        return result;
    }

    @Override
    public Collection<Node> getNodeValues() {
        Collection<Node> result = super.getNodeValues();
        result.addAll(matchMap().nodeMap().values());
        return result;
    }
    
    /** Returns the condition with which this match is associated. */
    public GraphCondition getCondition() {
        return condition;
    }
    
    /** Returns the element map constituting the map. */
    public VarNodeEdgeMap matchMap() {
        return matchMap;
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ExistentialMatch
                && ((ExistentialMatch) obj).matchMap().equals(matchMap());
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
    @Override
    protected int computeHashCode() {
        return getCondition().hashCode() ^ matchMap().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Nodes %s, edges %s", matchMap().nodeMap(), matchMap().edgeMap());
    }

    /** The condition with which this match is associated. */
    private final GraphCondition condition;
    /** The map constituting the match. */
    private final VarNodeEdgeMap matchMap;
    /** The (pre-computed) hash code of this match. */
    private int hashCode;
}
