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
 * $Id$
 */
package groove.explore.result;


/** 
 * This result does not collect anything and is never done.
 * Thus exploration is done when there's nothing left to explore.
 * To be combined with an {@link EmptyAcceptor}.
 * @author 
 */
public class EmptyResult<T> extends Result<T> {
	@Override
	public boolean done() {
		// this result does not collect anything and is never done
		// thus exploration is done when there's nothing left to explore
		return false;
	}
	
	@Override
	public EmptyResult<T> getFreshResult () {
		return new EmptyResult<T>();
	}
}
