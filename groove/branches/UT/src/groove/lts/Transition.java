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
 * $Id: Transition.java,v 1.1.1.1 2007-03-20 10:05:25 kastenberg Exp $
 */
package groove.lts;

/**
 * Interface of a transition in an {@link LTS}.
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:25 $
 */
public interface Transition extends groove.graph.BinaryEdge {
    // the general interface adds no functionality
	/** Specialises the return type. */
	State source();
	/** Specialises the return type. */
	State target();
}
