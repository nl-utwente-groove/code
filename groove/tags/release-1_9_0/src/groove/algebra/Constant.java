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
 * $Id: Constant.java,v 1.2 2007-07-21 20:07:43 rensink Exp $
 */
package groove.algebra;


/**
 * Interface extending the {@link groove.algebra.Operation}-interface
 * without adding more functionality.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $ $Date: 2007-07-21 20:07:43 $
 */
public interface Constant extends Operation {
    /** 
     * Returns the value of this constant.
     * @return the value of this constaint; may be <code>null</code> if the constant is a {@link Variable}. 
     */
	public Object getValue();
}