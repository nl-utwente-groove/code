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
 * $Id: CNN.java,v 1.1 2007-11-28 15:35:09 iovka Exp $
 */
package groove.abs;

import groove.graph.Node;

/** Represents a couple of two nodes.
 * @author Iovka Boneva
 * @version $Revision $
 */
class CNN {
	private Node n1; Node n2;
	private CNN(Node n1, Node n2) { this.n1 = n1; this.n2 = n2; }
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof CNN)) { return false; }
		CNN c = (CNN) o;
		return c.n1.equals(this.n1) && c.n2.equals(this.n2);
	}
	
	Node n1() { return n1; }
	Node n2() { return n2; }
	@Override
	public int hashCode () { return n1.hashCode() + n2.hashCode(); }
	@Override
	public String toString () {
		return "(" + n1.toString() + "," + n2.toString() + ")" ;
	}
	/** Convenience method */
	static CNN cnn(Node n1, Node n2) {
		return new CNN(n1,n2);
	}
}