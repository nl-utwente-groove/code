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
 * $Id: FilteredDeltaTarget.java,v 1.3 2008-01-30 09:32:51 iovka Exp $
 */
package groove.graph;

import static groove.graph.DeltaApplier.*;

/**
 * A delta target that passes on calls of <code>add...</code> and <code>remove...</code>
 * to a nested inner target, filtered by the
 * application mode (which is typically one of {@link DeltaApplier#NODES_ONLY}
 * or {@link DeltaApplier#EDGES_ONLY}.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class FilteredDeltaTarget implements DeltaTarget {
	/**
	 * Builds a filtered target from a given inner target and filtering mode.
	 * @param inner the inner target
	 * @param mode the mode, typically {@link DeltaApplier#NODES_ONLY}
	 * or {@link DeltaApplier#EDGES_ONLY}.
	 */
	public FilteredDeltaTarget(DeltaTarget inner, int mode) {
		this.inner = inner;
		this.mode = mode;
	}
	public boolean addEdge(Edge elem) {
		if (getMode() == NODES_ONLY) {
			return false;
		} else {
			return getInner().addEdge(elem);
		}
	}

	public boolean addNode(Node elem) {
		if (getMode() == EDGES_ONLY) {
			return false;
		} else {
			return getInner().addNode(elem);
		}
	}

	public boolean removeEdge(Edge elem) {
		if (getMode() == NODES_ONLY) {
			return false;
		} else {
			return getInner().removeEdge(elem);
		}
	}

	public boolean removeNode(Node elem) {
		if (getMode() == EDGES_ONLY) {
			return false;
		} else {
			return getInner().removeNode(elem);
		}
	}
	
	/**
	 * Returns the filtering mode of this target.
	 */
	final protected int getMode() {
		return mode;
	}
	
	/**
	 * Returns the nested target.
	 */
	protected DeltaTarget getInner() {
		return inner;
	}
	
	/** The nested target. */
	private final DeltaTarget inner;
	/** The filtering mode of this target. */
	private final int mode;
}