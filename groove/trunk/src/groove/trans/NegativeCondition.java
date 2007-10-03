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
 * $Id: NegativeCondition.java,v 1.1 2007-10-03 16:08:40 rensink Exp $
 */
package groove.trans;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;

/**
 * A negative graph condition, which tests against the existence of a graph structure.
 * A negative condition has no sub-conditions and returns an (empty) match if and only if the matched graph
 * does not exist.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NegativeCondition extends AbstractCondition {
    /**
     * Creates a negative condition that attempts to match a given pattern morphism.
     */
    public NegativeCondition(Morphism pattern, NameLabel name, SystemProperties properties) {
        super(pattern, name, properties);
    }
    
    
    /**
     * Adding sub-conditions is not allowed and will give rise to an exception.
     * @throws UnsupportedOperationException
     */
    @Override
    final public void addSubCondition(GraphCondition condition) {
        throw new UnsupportedOperationException();
    }

    final public Iterable< ? extends Match> getMatches(Graph host, NodeEdgeMap patternMap) {
        final boolean matches = getMatchMapIter(host, patternMap).hasNext();
        return new Iterable<Match>() {
            public Iterator<Match> iterator() {
                if (matches) {
                    return Collections.<Match>emptySet().iterator();
                } else {
                    return WRAPPED_EMPTY_MATCH.iterator();
                }
            }            
        };
    }
    
    /** Constant empty match. */
    static private final Set<Match> WRAPPED_EMPTY_MATCH = Collections.singleton((Match) new CompositeMatch());
}
