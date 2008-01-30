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
/* $Id: Dispenser.java,v 1.2 2008-01-30 09:32:14 iovka Exp $ */
package groove.util;

/**
 * Interface for a number dispenser.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Dispenser {
	/** Returns a number, according to the policy of this dispenser. */
	int getNumber();
}
