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
 * $Id $
 */

package groove.match;

import groove.graph.Graph;
import groove.graph.NodeEdgeMap;
import groove.graph.iso.PartitionMap;

import java.util.List;

/**
 * Match strategy that constructs isomorphisms, on the basis of graph certificates.
 * There is room for optimisation, in the following respects:
 * <ul>
 * <li> The search plan does not take multiplicity of certificates into account. It would speed up the actual search 
 * to start with singular certificates, but the construction of the search plan would suffer. Since iso search
 * plans will probably be used only once, it is not clear where the balance lies.
 * <li> There is no provision for using pre-matched or identical elements. If a large part of the graph
 * remains unchanged throughout the transformation, it will be very beneficial to take this into account.
 * </ul>
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class IsoMatchStrategy extends SearchPlanStrategy {
    public IsoMatchStrategy(List<SearchItem> plan) {
        super(plan, true);
    }

    @Override
    protected Search createSearch(Graph target, NodeEdgeMap preMatch) {
        // TODO Auto-generated method stub
        return new IsoSearch(this, target, preMatch);
    }


    /**
     * Search subclass for isomorphism checking.
     */
    public class IsoSearch extends Search {
        public IsoSearch(IsoMatchStrategy strategy, Graph target, NodeEdgeMap preMatch) {
            super(strategy, target, preMatch);
        }

        PartitionMap getPartitionMap() {
            if (partitionMap == null) {
                partitionMap = getTarget().getCertifier().getPartitionMap();
            }
            return partitionMap;
        }
        
        private PartitionMap partitionMap;
    }
}
