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
package groove.io;

import groove.view.StoredGrammarView;

/**
 * Interface that associates actions with a certain file filter, for example
 * the ones that allow saving a grammar under an old version.
 * @author Eduardo Zambon
 * @version $Revision $
 */
public abstract class FileFilterAction {

    /**
     * Test if the grammar given can be modified.
     * @param grammar the grammar to be checked.
     * @return true is the pass, false otherwise.
     */
    public boolean test(StoredGrammarView grammar) {
        return true;
    }

    /**
     * Perform changes on the given grammar.
     * @param grammar the grammar to be modified.
     */
    public void modify(StoredGrammarView grammar) {
        // Empty body.
    }

    /**
     * @return the message to be displayed in the error dialog if the test fails.
     */
    public abstract String text();
}
