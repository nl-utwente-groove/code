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
package groove.explore.encode;

import groove.grammar.Grammar;
import groove.grammar.model.GrammarModel;
import groove.util.parse.FormatException;
import groove.verify.Formula;
import groove.verify.Logic;

/**
 * Encoding of an LTL property.
 * The property is returned as a string, but parsed for correctness as an
 * LTL property.
 * <p>
 * @see EncodedType
 * @author Arend Rensink
 */
public class EncodedLtlProperty implements EncodedType<String,String> {
    /**
     * Default constructor. Creates local store only.
     */
    public EncodedLtlProperty() {
        // empty
    }

    @Override
    public EncodedTypeEditor<String,String> createEditor(GrammarModel grammar) {
        return new StringEditor<>(grammar, "", 20);
    }

    @Override
    public String parse(Grammar rules, String source) throws FormatException {
        try {
            Formula.parse(Logic.LTL, source);
            return source;
        } catch (FormatException e) {
            throw new FormatException("Error in LTL formula '%s': %s", source, e.getMessage());
        }
    }
}
