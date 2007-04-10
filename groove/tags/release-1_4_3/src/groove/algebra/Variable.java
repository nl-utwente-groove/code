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
 * $Id: Variable.java,v 1.2 2007-03-30 15:50:31 rensink Exp $
 */
package groove.algebra;

import groove.graph.algebra.AlgebraConstants;

/**
 * Class representing a variable that can be used when specifing a rule
 * for attributed graphs.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $ $Date: 2007-03-30 15:50:31 $
 */
public class Variable extends DefaultConstant {
    public Variable() {
        symbol = "";
        type = AlgebraConstants.NO_TYPE;
    }

    @Override
    public String toString() {
        return "empty";
    }
}