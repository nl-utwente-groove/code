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
 * $Id: AbstractMatchStrategy.java,v 1.3 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.trans.HostGraph;
import groove.trans.RuleToHostMap;
import groove.util.Property;
import groove.util.Visitor;
import groove.util.Visitor.Collector;
import groove.util.Visitor.Finder;

import java.util.ArrayList;
import java.util.List;

/**
 * Class providing basic functionality for match strategies. The only method
 * left to be implemented is
 * {@link #getMatchIter(HostGraph host, RuleToHostMap seedMap)}.
 * @param <R> the result type of the match
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractMatchStrategy<R> implements MatchStrategy<R> {
    @Override
    public R find(HostGraph host, RuleToHostMap seedMap, Property<R> property) {
        Finder<R> finder = this.finder.newInstance(property);
        R result = traverse(host, seedMap, finder);
        finder.dispose();
        return result;
    }

    @Override
    public List<R> findAll(HostGraph host, RuleToHostMap seedMap,
            Property<R> property) {
        List<R> result = new ArrayList<R>();
        Collector<R,List<R>> collector =
            this.collector.newInstance(result, property);
        traverse(host, seedMap, collector);
        collector.dispose();
        return result;
    }

    /** Reusable finder for {@link #find(HostGraph, RuleToHostMap, Property)}. */
    private final Finder<R> finder = Visitor.newFinder(null);
    /** Reusable collector for {@link #findAll(HostGraph, RuleToHostMap, Property)}. */
    private final Collector<R,List<R>> collector =
        Visitor.newCollector(null);
}
