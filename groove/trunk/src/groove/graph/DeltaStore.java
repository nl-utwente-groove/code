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
/**
 * 
 */
package groove.graph;

import java.util.Set;

/**
 * Delta target that collects the addition and removal information 
 * and can play it back later, in the role of delta applier.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class DeltaStore extends DefaultDeltaApplier implements DeltaTarget, DeltaApplier {
	/**
	 * Created an initially empty delta store.
	 */
	public DeltaStore() {
		this(null, null, null, null);
	}

	/**
	 * Creates a copy of a given delta store.
	 */
	public DeltaStore(DeltaStore basis) {
		this(basis.getAddedNodeSet(), basis.getRemovedNodeSet(), basis.getAddedEdgeSet(), basis.getRemovedEdgeSet());
	}

	/**
	 * Creates a delta store based on explicitly given added and removed sets.
	 * The sets are copied.
	 */
	protected DeltaStore(Set<Node> addedNodeSet, Set<Node> removedNodeSet, Set<Edge> addedEdgeSet, Set<Edge> removedEdgeSet) {
		super(addedNodeSet, removedNodeSet, addedEdgeSet, removedEdgeSet);
	}
	
	/** Creates a delta store by applying a delta applier to an empty initial store. */
	public DeltaStore(DeltaApplier basis) {
		this();
		basis.applyDelta(this);
	}
	
	public boolean addEdge(Edge elem) {
		if (!getRemovedEdgeSet().remove(elem)) {
			assert !getAddedEdgeSet().contains(elem) : "Added edge set "
					+ getAddedEdgeSet() + " already contains " + elem;
			return getAddedEdgeSet().add(elem);
		} else {
			return true;
		}
	}

	public boolean addNode(Node elem) {
		if (!getRemovedNodeSet().remove(elem)) {
			assert !getAddedNodeSet().contains(elem) : "Added node set "
					+ getAddedNodeSet() + " already contains " + elem;
			return getAddedNodeSet().add(elem);
		} else {
			return true;
		}
	}

	public boolean removeEdge(Edge elem) {
		if (!getAddedEdgeSet().remove(elem)) {
//			assert !removedEdgeSet.contains(elem) : "Removed edge set "
//					+ removedEdgeSet + " already contains " + elem;
			return getRemovedEdgeSet().add(elem);
		} else {
			return true;
		}
	}

	public boolean removeNode(Node elem) {
		if (!getAddedNodeSet().remove(elem)) {
//			assert !removedNodeSet.contains(elem) : "Removed node set "
//					+ removedNodeSet + " already contains " + elem;
			return getRemovedNodeSet().add(elem);
		} else {
			return true;
		}
	}
}