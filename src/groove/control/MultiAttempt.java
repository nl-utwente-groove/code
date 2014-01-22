/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.control;

import java.util.ArrayList;

/**
 * Attempt specialisation consisting of a list of other attempts,
 * to be tried successively in the given order.
 * <P> the position type for which this is an attempt
 * <A> the list of attempts of which this consists
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class MultiAttempt<P extends Position<P>,A> extends
        ArrayList<A> implements Attempt<P> {
    // empty
}
