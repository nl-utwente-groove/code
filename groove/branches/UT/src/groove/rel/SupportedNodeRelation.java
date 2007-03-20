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
 * $Id: SupportedNodeRelation.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;

import java.util.Collection;
import java.util.Map;

/**
 * Binary relation over nodes
 * which for each pair of related nodes contains a <i>support</i>, which
 * is a set of graph elements that justifies the relation. 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface SupportedNodeRelation extends NodeRelation {
    /**
     * Yields the set of all graph elements supporting this relation.
     */
    public Collection<Element> getSupport();

    /**
     * Returns the support for the relation between two nodes, if the nodes are in fact related.
     * Returns <tt>null</tt> if the nodes are unrelated.
     * @param pre the inverstigated pre-image 
     * @param post the inverstigated post-image 
     * @return the support for the relation between <tt>pre</tt> and <tt>post</tt>, if any
     */
    public Collection<Element> getSupport(Node pre, Node post);
    
    /**
     * Yields a mapping from each pair of related elements to the set of
     * graph elements supporting that relation.
     */  
    public Map<Edge,Collection<Element>> getSupportMap();
}
