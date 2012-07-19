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
 * $Id: Operation.java,v 1.1.1.2 2007-03-20 10:42:39 kastenberg Exp $
 */
package groove.algebra;

import java.util.List;

/**
 * Interface specifying what methods each Operation needs to implement.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:39 $
 */
public interface Operation {

	/**
	 * Apply this operation on the list of operands and return the
	 * result.
	 * @param operands the operands on which this operation operates
	 * @return the resulting {@link groove.algebra.Operation} when applying this
	 * operation on its <tt>operands</tt>
	 */
	public Constant apply(List<Constant> operands);

	/**
	 * @return the String representation of this operation
	 */
	public String symbol();

	/**
	 * @return the arity of this operation
	 */
	public int arity();

	/**
	 * @return the algebra to which this operation belongs
	 */
	public Algebra algebra();

	/**
	 * @return the type of this operation
	 */
	public int type();

	/**
	 * @return the prefix of this operation
	 */
	public String prefix();

	/**
	 * @param algebra
	 */
	public void set(Algebra algebra, String symbol, int arity);
}