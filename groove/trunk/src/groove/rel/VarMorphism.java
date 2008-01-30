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
 * $Id: VarMorphism.java,v 1.5 2008-01-30 09:32:28 iovka Exp $
 */
package groove.rel;

import groove.graph.Morphism;

/**
 * Morphism type offering support for mapping the variables in the domain to labels of the codomain.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
@Deprecated
public interface VarMorphism extends Morphism, VarMap {
    // join of two interfaces
	/** The element map is now of a more specialised type. */
	VarNodeEdgeMap elementMap();
}
