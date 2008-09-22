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


/** A result that is completed when the number of collected
 * elements is big enough.
 * @author 
 *
 * @param <T>
 */
public class SizedResult<T> extends Result<T> {
	/** Creates a sized result by specifying the number of elements to be collected.
	 * @param size The number of elements to be collected. 
	 */
	public SizedResult(int size) {
		this.size = size;
	}
	
	@Override
	public boolean done() {
		return (elements.size() >= this.size);
	}

	@Override
	public SizedResult<T> getFreshResult() {
		return new SizedResult<T>(this.size);
	}

    /** The number of elements to be collected. */
    protected int size;
}
