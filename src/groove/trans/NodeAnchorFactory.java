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
 * $Id: NodeAnchorFactory.java,v 1.3 2008-01-30 09:32:39 iovka Exp $
 */
package groove.trans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.rel.VarSupport;

/**
 * Morphism specialization that includes the possibility to 
 * simulate regular expressions, and the ability to generate a 
 * minimal representation of the morphism in the form of an 
 * array of node images, as well as the ability to reconstruct
 * itself out of such a  minimal representation.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class NodeAnchorFactory implements AnchorFactory {
    /**
     * Returns a prototype matching, to be used as
     * an anchor and matching factory in a production rule.
     */
    static public AnchorFactory getPrototype() {
        return prototype;
    }

    /** A fixed matching prototype, to be used as return value in <tt>getPrototype()</tt>. */
    static private NodeAnchorFactory prototype = new NodeAnchorFactory();

    /** This implementation returns an array consisting of the nodes in the rule's left hand side. */
    public Element[] newAnchors(Rule rule) {
        Set<Element> anchors = new HashSet<Element>(rule.lhs().nodeSet());
        // set of edge ends that may be removed because the edges themselves are anchors
        Set<Node> removableEnds = new HashSet<Node>();
        for (Edge lhsVarEdge: VarSupport.getSimpleVarEdges(rule.lhs())) {
            anchors.add(lhsVarEdge);
            // if we have the edge in the anchors, its end nodes need not be there
            removableEnds.addAll(Arrays.asList(lhsVarEdge.ends()));
        }
        anchors.removeAll(removableEnds);
        return anchors.toArray(new Element[0]);
    }
}