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
 * $Id: Deriver.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.trans;

import groove.graph.Graph;

import java.util.Iterator;
import java.util.Set;

/**
 * Class that wraps the algorithm to explore rule applications
 * for a given graph.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:19 $
 */
public interface Deriver {
    /**
     * Returns a set of {@link RuleApplication}s for a given graph,
     * according to the derivation strategy of this deriver.
     * @param graph the host graph to derive the applications for
     * @return a set of rule applicatons.
     */
	public Set<RuleApplication> getDerivations(Graph graph);
	
    /**
     * Returns an iterator over the {@link RuleApplication}s for a given graph,
     * according to the derivation strategy of this deriver.
     * The iterator may operate lazily; it is guaranteed to return every application
     * at most once, so that it behaves functionally the same as <code>getDerivations(graph).iterator()</code>
     * @param graph the host graph to derive the applications for
     * @return an iterator over the rule applicatons for <code>graph</code>
     * @see #getDerivations(Graph)
     */
	public Iterator<RuleApplication> getDerivationIter(Graph graph);
}
