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

/**
 * Attempt to invoke one or more callable units, with for every
 * unit a successor position.
 * The attempt can succeed or fail; what happens then is 
 * reflected by the {@link #onSuccess()} and 
 * {@link #onFailure()} results.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Attempt<P extends Position<P>> {
    /** Next alternative position in case this attempt succeeds. */
    public P onSuccess();

    /** Next alternative position in case this attempt fails. */
    public P onFailure();
}
