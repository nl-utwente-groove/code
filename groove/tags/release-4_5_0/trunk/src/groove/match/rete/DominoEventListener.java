/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.match.rete;

/**
 * Any object that needs to be notified of domino-events
 * should implement this interface.
 * 
 * Objects of type {@link AbstractReteMatch} call notify such listeners when something of
 * domino-nature, i.e. propagation through direct channels rather than the RETE-network's
 * structure, happens to them. 
 * 
 * @author Arash Jalali
 * @version $Revision $
 */
public interface DominoEventListener {
    /**
     * This method is called by the match object referenced by the
     * <code>match</code> argument to notify the listener that it has been deleted.
     * 
     * @param match The match object that is deleted through the domino-deletion scheme 
     */
    void matchRemoved(AbstractReteMatch match);
}
