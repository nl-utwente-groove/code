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

import groove.graph.Node;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteNodeMatch implements ReteMatchUnit {

    private Node hostElement;
    private Node reteElement;

    /**
     * Creates a node match unit from two allegedly matching nodes.
     *   
     * @param hostNode
     * @param reteNode 
     */
    public ReteNodeMatch(Node hostNode, Node reteNode) {
        this.hostElement = hostNode;
        this.reteElement = reteNode;
    }

    @Override
    public Node getHostElement() {
        return this.hostElement;
    }

    @Override
    public Node getReteElement() {
        return this.reteElement;
    }

    public boolean equals(ReteNodeMatch m) {
        return (m != null) && this.getHostElement().equals(m.getHostElement())
            && this.getReteElement().equals(m.getReteElement());
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ReteNodeMatch) && this.equals((ReteNodeMatch) o);
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
