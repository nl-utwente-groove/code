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
package groove.gui.chscenar;

/** Describes a possible choice for a strategy, acceptor, result
 * or explore condition. Allows introspection in order to
 * determine what parameters are needed in order to instanciate
 * the corresponding strategy, acceptor, result or explore condition.
 * 
 * @author Iovka Boneva
 */
@Deprecated
@SuppressWarnings("all")
public interface Choice {
	
	/** A unique id. */
	public Object id();
	
	/** A short name, giving a short description. */
	public String shortName();
	
	/** A one-sentence description. */
	public String description();
	
	/** The class implementing the corresponding choice. */
	public Class<?> implementingClass();

	/** The class of the options needed for instanciation. 
	 * @return null if no options are needed.
	 */
	public Class<?> optionsClass();
	
	/** Gets the corresponding instance of a strategy, acceptor, etc. ...
	 * @require <code>options</code> is of type {@link #optionsClass()}
	 * @insure result is of type {@link #implementingClass()}
	 * */
	public Object getInstance (Object options);
}
