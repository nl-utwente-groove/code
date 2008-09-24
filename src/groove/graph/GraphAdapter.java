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
 * $Id: GraphAdapter.java,v 1.3 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

/**
 * An abstract implementation of the <tt>GraphListener</tt> interface 
 * which does nothing at each method.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class GraphAdapter implements GraphListener {
	/** Provides an empty default implementation. */
    public void addUpdate(GraphShape graph, Node node) {
    	// empty default implementation
    }

	/** Provides an empty default implementation. */
    public void addUpdate(GraphShape graph, Edge edge) {
    	// empty default implementation
    }

	/** Provides an empty default implementation. */
    public void removeUpdate(GraphShape graph, Node node) {
    	// empty default implementation
    }

	/** Provides an empty default implementation. */
    public void removeUpdate(GraphShape graph, Edge elem) {
    	// empty default implementation
    }

	/** Provides an empty default implementation. */
    public void replaceUpdate(GraphShape graph, Node from, Node to) {
    	// empty default implementation
    }

    /** Provides an empty default implementation. */
    public void replaceUpdate(GraphShape graph, Edge from, Edge to) {
    	// empty default implementation
    }
}
