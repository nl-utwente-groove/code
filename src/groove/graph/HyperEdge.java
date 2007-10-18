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
 * $Id: HyperEdge.java,v 1.10 2007-10-18 14:57:41 rensink Exp $
 */
package groove.graph;

/**
 * General edge implementation.
 * Don't use it if efficiency is a concern!
 * @author Arend Rensink
 * @version $Revision: 1.10 $
 * @deprecated Hyperedges are no longer (planned to be) supported; use {@link UnaryEdge} or {@link BinaryEdge} instead.
 */
@Deprecated 
public class HyperEdge extends AbstractEdge<Node,Label> {
    /**
     * Constructs an edge with given ends and label.
     */
    public HyperEdge(Node[] ends, Label label) {
    	super(ends[SOURCE_INDEX], label);
//        this.label = label;
        this.ends = new Node[ends.length];
        System.arraycopy(ends, 0, this.ends, 0, ends.length);
        setMaxEndCount(ends.length);
    }
    
    public Node[] ends() {
    	Node[] result = new Node[ends.length];
    	System.arraycopy(ends, 0, result, 0, ends.length);
    	return result;
    }
//
//    @Deprecated
//    public Edge imageFor(GenericNodeEdgeMap elementMap) {
//    	return imageFor((NodeEdgeMap)elementMap);
//    }
//    
//    private Edge imageFor(NodeEdgeMap elementMap) {
//        Node[] endImages = new Node[endCount()];
//        for (int i = 0; i < endImages.length; i++) {
//            endImages[i] = elementMap.getNode(ends[i]);
//            if (endImages[i] == null) {
//                return null;
//            }
//        }
//        return newEdge(endImages);
//    }
//    
//    /**
//     * Constructs a new edge with the given ends and the label of this edge.
//     * Convenience method for <code>noeWedge(ends, label())</code>.
//     */
//    public Edge newEdge(Node[] ends) {
//        return newEdge(ends, label);
//    }
//
//    /**
//     * Constructs a new edge with the given ends and label.
//     */
//    public Edge newEdge(Node[] ends, Label label) {
//        return new HyperEdge(ends, label);
//    }
    
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("" + source());
        result.append(" --" + label() + " -> [");
        for (int i = 1; i < endCount(); i++) {
            result.append(end(i));
            if (i < endCount() - 1) {
                result.append(",");
            }
        }
        result.append("]");
        return result.toString();
    }
    
    /** The array of end nodes of this hyperedge. */
    private final Node[] ends;
}