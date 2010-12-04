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
 * $Id$
 */
package groove.match.rete;

import groove.graph.Edge;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteEdgeMatch implements ReteMatchUnit {

    private Edge hostElement;
    private Edge reteElement;

    /**
     * Makes an edge match unit from two allegedly matching edges.
     * the labels should be equals
     * 
     * It's important to note that the name for the parameter <code>reteEdge</code>
     * should not be mistaken with an edge in the RETE network. The <code>reteEdge</code>
     * is a graph edge pattern represented by some {@link EdgeCheckerNode} 
     * in a RETE network, which can be retrieved by calling 
     * the {@link EdgeCheckerNode#getEdge()} method.   
     * 
     * @param hostEdge
     * @param reteEdge
     */
    public ReteEdgeMatch(Edge hostEdge, Edge reteEdge) {
        assert hostEdge.label().equals(reteEdge.label());
        this.hostElement = hostEdge;
        this.reteElement = reteEdge;
    }

    @Override
    public Edge getHostElement() {
        return this.hostElement;
    }

    @Override
    public Edge getReteElement() {
        return this.reteElement;
    }

    /**
     * @param m
     * @return
     */
    public boolean equals(ReteEdgeMatch m) {
        return (m != null) && this.getHostElement().equals(m.getHostElement())
            && this.getReteElement().equals(m.getReteElement());
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ReteEdgeMatch) && this.equals((ReteEdgeMatch) o);
    }

    @Override
    public int compareTo(ReteMatchUnit u) {
        int result = this.getReteElement().compareTo(u.getReteElement());
        if (result == 0) {
            result = this.getHostElement().compareTo(u.getHostElement());
        }
        return result;
    }

    @Override
    public boolean equals(ReteMatchUnit m) {
        return (this == m) || ((m != null) && (this.compareTo(m) == 0));
    }

    @Override
    public String toString() {
        return this.reteElement.toString() + "==" + this.hostElement.toString();
    }
}
