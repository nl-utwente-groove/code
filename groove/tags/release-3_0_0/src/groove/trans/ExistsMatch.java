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
 * $Id: ExistsMatch.java,v 1.4 2008-01-30 09:32:39 iovka Exp $
 */
package groove.trans;

import groove.rel.VarNodeEdgeMap;

/**
 * Match of an existential condition.
 * The match wraps an element map, from the object that has been matched
 * to the elements of the host graph in which it has been matched. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExistsMatch extends CompositeMatch {
    /** Constructs a match for a given {@link SPORule}. */
    public ExistsMatch(VarNodeEdgeMap matchMap) {
        super(matchMap);
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ExistsMatch
                && super.equals(obj);
    }
    
    /** Computes a value for the hash code. */
    @Override
    protected int computeHashCode() {
        return super.computeHashCode();
    }

    @Override
    public String toString() {
        return String.format("Nodes %s, edges %s", getElementMap().nodeMap(), getElementMap().edgeMap());
    }
}