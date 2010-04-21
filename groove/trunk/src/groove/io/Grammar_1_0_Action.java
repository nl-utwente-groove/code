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

import groove.util.Version;
import groove.view.StoredGrammarView;

/**
 * Implementation of the action associated with saving as grammar version 1.0.
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class Grammar_1_0_Action extends FileFilterAction {

    @Override
    public boolean test(StoredGrammarView grammar) {
        return grammar.getLabelStore().hasUnaryLabels();
    }

    @Override
    public void modify(StoredGrammarView grammarView) {
        grammarView.getProperties().setGrammarVersion(
            Version.GRAMMAR_VERSION_1_0);
    }

    @Override
    public String text() {
        return "The current grammar contains types.\n"
            + "Typing is not supported in grammar version 1.0.\n"
            + "It is not possible to perform the save operation.";
    }

}