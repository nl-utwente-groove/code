/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: NotCondition.java,v 1.6 2007-11-29 12:52:08 rensink Exp $
 */
package groove.trans;

import groove.graph.GraphShape;
import groove.graph.LabelStore;
import groove.rel.RuleToStateMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * A negative graph condition, which tests against the existence of a graph
 * structure. A negative condition has no sub-conditions and returns an (empty)
 * match if and only if the matched graph does not exist.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NotCondition extends AbstractCondition<CompositeMatch> {
    /**
     * Creates a negative condition that attempts to match a given graph, with
     * given root map.
     */
    public NotCondition(RuleGraph target, RuleGraphMap rootMap,
            LabelStore labelStore, SystemProperties properties) {
        super(null, target, rootMap, labelStore, properties);
    }

    /**
     * Creates a NAC over a default context and an initially empty target
     * pattern.
     */
    public NotCondition(RuleGraph pattern, SystemProperties properties,
            LabelStore labelStore) {
        this(pattern, new RuleGraphMap(), labelStore, properties);
    }

    /**
     * Adding sub-conditions is not allowed and will give rise to an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    final public void addSubCondition(Condition condition) {
        throw new UnsupportedOperationException();
    }

    @Override
    Iterator<CompositeMatch> computeMatchIter(final GraphShape host,
            Iterator<RuleToStateMap> matchMapIter) {
        Iterator<CompositeMatch> result = null;
        if (matchMapIter.hasNext()) {
            result = Collections.<CompositeMatch>emptySet().iterator();
        } else {
            result = this.WRAPPED_EMPTY_MATCH.iterator();
        }
        return result;
    }

    /** Constant empty match. */
    private final Set<CompositeMatch> WRAPPED_EMPTY_MATCH =
        Collections.singleton(new CompositeMatch());
}
