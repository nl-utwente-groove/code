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
 * $Id: BinaryEdge.java,v 1.1.1.1 2007-03-20 10:05:33 kastenberg Exp $
 */
package groove.graph;

/**
 * Interface of a directed graph edge, with source node, label and target node.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface BinaryEdge extends Edge { 
	/** The number of ends of a binary edge. */
	static public final int END_COUNT = 2;
	
    /**
     * Returns the target node of this edge.
     * The target node has index {@link #TARGET_INDEX}.
     * @return the target node of this edge 
     * @see #TARGET_INDEX
     * @see #end(int)
     * @ensure <tt>result != null && result == ends(TARGET_INDEX)</tt>
    */
    public Node target();
}
