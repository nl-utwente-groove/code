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
 * $Id: DefaultBoundedModelCheckingStrategy.java,v 1.1 2008/03/04 14:44:25 kastenberg Exp $
 */
package groove.explore.strategy;

/**
 * This class provides some default implementations for a bounded model checking
 * strategy, such as setting the boundary and collecting the boundary graphs.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1 $
 */
public abstract class DefaultBoundedModelCheckingStrategy<T> extends DefaultModelCheckingStrategy implements BoundedModelCheckingStrategy {

	public boolean finished() {
		return true;
	}

	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}

	public Boundary getBoundary() {
		return boundary;
	}

	/**
	 * Sets the state from which to start the next iteration.
	 */
	protected abstract void setNextStartState();

	/**
	 * The boundary to be used.
	 */
	private Boundary boundary;
}
