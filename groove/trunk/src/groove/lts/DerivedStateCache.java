// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: DerivedStateCache.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $
 */
package groove.lts;

import groove.graph.DeltaGraph;

/**
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class DerivedStateCache extends DefaultStateCache {
	/** Constructs a new cache for a given derived state graph. */
    public DerivedStateCache(DerivedGraphState graph) {
        super(graph);
    }

	/**
	 * This implementation returns a measure based on the footprint size of the state's rule.
	 */
	protected int computeFreezeDecrement(DeltaGraph graph) {
		if (graph instanceof DerivedGraphState) {
			return 4 * ((DerivedGraphState) graph).getRule().anchor().length;
		} else {
			return super.computeFreezeDecrement(graph);
		}
	}
	
    /**
     * A derived state is never truly modifiable, but may appear so
     * during the time it is still being fixed. For the purpose of the cache, we 
     * treat it as fixed always.
     */
	protected void initModifiableCache() {
		initFixedCache();
	}
}