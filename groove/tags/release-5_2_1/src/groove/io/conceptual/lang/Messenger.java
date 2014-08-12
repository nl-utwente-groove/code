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
package groove.io.conceptual.lang;

import java.util.List;

/**
 * Collects various Messages which may be generated upon some operation on
 * the class implementing this interface. The list of messages will not be cleared
 * unless clearMessages() is explicitly called.
 * @author s0141844
 * @version $Revision $
 */
public interface Messenger {
    /**
     * Returns a List of messages currently collected. List is guaranteed to be in chronological order
     * @return List of messages
     */
    public List<Message> getMessages();

    /**
     * Clears all collected messages, such that getMessages() returns an empty list.
     */
    public void clearMessages();
}
