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
 * $Id: IsoChecker.java,v 1.2 2007-08-26 07:23:10 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Graph;

/**
 * Interface for strategies that check isomorphism between graphs.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public interface IsoChecker {
	/**
	 * Tests if two graphs are isomorphic.
	 * @param dom First graph to be tested
	 * @param cod Second graph to be tested
	 * @return <code>true</code> if and only if <code>dom</code> and <code>cod</code> are isomorphic
	 */
	public boolean areIsomorphic(Graph dom, Graph cod);
}
