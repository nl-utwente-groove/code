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
 * $Id: MinimalAnchorFactory.java,v 1.3 2007-08-22 09:19:44 kastenberg Exp $
 */
package groove.trans;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.nesting.rule.NestedRule;

/**
 * In this implementation, the anchors are the minimal set of nodes and edges 
 * needed to reconstruct the transformation, but not necessarily the entire 
 * matching: only mergers, eraser nodes and edges (the later only if they are 
 * not incident to an eraser node) and the incident nodes of creator edges are stored.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class MinimalAnchorFactory implements AnchorFactory {
	/**
	 * Returns the singleton instance of this class.
	 */
    static public AnchorFactory getInstance() {
        return prototype;
    }

    /** The singleton instance of this class. */
    static private MinimalAnchorFactory prototype = new MinimalAnchorFactory();

    /** Private empty constructor to make this a singleton class. */
    private MinimalAnchorFactory() {
    	// empty constructor
    }

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>,
     * and that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been 
     * initialised already.
     * @require <tt>rule instanceof SPORule</tt>
     */
    public Element[] newAnchors(Rule generalRule) {
    	// JHK: NestedRule
        NestedRule rule = (NestedRule) generalRule;
        Set<Element> anchors = new LinkedHashSet<Element>(Arrays.asList(rule.getEraserNodes()));
        Set<? extends Node> creatorNodes = rule.getCreatorGraph().nodeSet();
        for (Map.Entry<Node,Node> ruleMorphNodeEntry: rule.getMorphism().nodeMap().entrySet()) {
        	if (creatorNodes.contains(ruleMorphNodeEntry.getValue())) {
        		anchors.add(ruleMorphNodeEntry.getKey());
        	}
        }
        // set of endpoints that we will remove again
        Set<Node> removableEnds = new HashSet<Node>();
        for (Edge lhsVarEdge: rule.getVarEdges()) {
            anchors.add(lhsVarEdge);
            // if we have the edge in the anchors, its end nodes need not be there
            removableEnds.addAll(Arrays.asList(lhsVarEdge.ends()));
        }
        Edge[] eraserEdges = rule.getEraserEdges();
        for (Edge eraserEdge: eraserEdges) {
            Collection<Node> eraserEdgeEnds = Arrays.asList(eraserEdge.ends()); 
            if (!anchors.containsAll(eraserEdgeEnds)) {
                anchors.add(eraserEdge);
                // if we have the edge in the anchors, its end nodes need not be there
                removableEnds.addAll(eraserEdgeEnds);
            }
        }
        anchors.addAll(rule.getMergeMap().keySet());
        anchors.removeAll(removableEnds);
        return anchors.toArray(new Element[0]);
    }
}
