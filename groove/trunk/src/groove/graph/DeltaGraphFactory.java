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
 * $Id: DeltaGraphFactory.java,v 1.2 2007-08-26 07:23:39 rensink Exp $
 */
package groove.graph;

/**
 * Extension of the {@link Graph} interface with a method to create
 * new graphs using a delta applier.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface DeltaGraphFactory<G extends Graph> {
	/** Creates a new graph from this graph by applying a delts to the current graph. */
	G newGraph(G basis, DeltaApplier applier);
}
