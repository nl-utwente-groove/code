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
 * $Id: NotCondition.java,v 1.3 2007-10-06 11:27:50 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultMorphism;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.rel.VarNodeEdgeMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * A negative graph condition, which tests against the existence of a graph structure.
 * A negative condition has no sub-conditions and returns an (empty) match if and only if the matched graph
 * does not exist.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NotCondition extends AbstractCondition<CompositeMatch> {
    /**
     * Creates a negative condition that attempts to match a given pattern morphism.
     */
    public NotCondition(Morphism pattern, SystemProperties properties) {
        super(pattern.cod(), pattern.elementMap(), null, properties);
    }

    /**
     * Creates a NAC over a default context and an initially empty target pattern.
     */
    public NotCondition(Graph pattern, SystemProperties properties) {
        this(new DefaultMorphism(pattern, pattern.newGraph()), properties);
    }

    /**
     * Adding sub-conditions is not allowed and will give rise to an exception.
     * @throws UnsupportedOperationException
     */
    @Override
    final public void addSubCondition(Condition condition) {
        throw new UnsupportedOperationException();
    }

	@Override
    Iterator<CompositeMatch> getMatchIter(final Graph host, Iterator<VarNodeEdgeMap> matchMapIter) {
        Iterator<CompositeMatch> result = null;
        if (matchMapIter.hasNext()) {
        	result = Collections.<CompositeMatch>emptySet().iterator();
        } else {
        	result = WRAPPED_EMPTY_MATCH.iterator();
        }
        return result;
    }
    
    /** Constant empty match. */
    private final Set<CompositeMatch> WRAPPED_EMPTY_MATCH = Collections.singleton(new CompositeMatch());
}
