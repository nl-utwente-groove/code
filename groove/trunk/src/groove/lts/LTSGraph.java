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
 * $Id: LTSGraph.java,v 1.2 2008-01-30 09:32:18 iovka Exp $
 */
package groove.lts;

import groove.graph.DefaultLabel;
import groove.graph.NodeSetEdgeSetGraph;

/**
 * Conversion class to add some special-purpose edges to an LTS
 * before saving it as an ordinary graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LTSGraph extends NodeSetEdgeSetGraph {
	/** Constructs a graph from a given LTS. */
    public LTSGraph(LTS lts) {
    	for (State state: lts.nodeSet()) {
            addNode(state);
            if (lts.isFinal(state)) {
                addEdge(state, DefaultLabel.createLabel(LTS.FINAL_LABEL_TEXT), state);
            } else if (lts.startState().equals(state)) {
                addEdge(state, DefaultLabel.createLabel(LTS.START_LABEL_TEXT), state);
            } else if (!state.isClosed()) {
                addEdge(state, DefaultLabel.createLabel(LTS.OPEN_LABEL_TEXT), state);
            }
        }
        addEdgeSet(lts.edgeSet());
    }
}