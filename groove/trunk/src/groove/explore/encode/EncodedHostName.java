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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encoding of a host name.
 * The property is returned as a string, but parsed for correctness as a
 * host name.
 * <p>
 * @see EncodedType
 * @author Vincent de Bruijn
 */
public class EncodedHostName implements EncodedType<String,String> {
    /**
     * Default constructor. Creates local store only.
     */
    public EncodedHostName() {
        // empty
    }

    @Override
    public EncodedTypeEditor<String,String> createEditor(GrammarModel grammar) {
        return new StringEditor<>(grammar, "", 20);
    }

    @Override
    public String parse(Grammar rules, String source)
        throws FormatException {
        Pattern pattern =
            Pattern.compile("^https?\\://[a-zA-Z0-9\\-\\.]+(\\.[a-zA-Z]{2,3})?\\:[0-9]{4}(/\\S*)?$");
        Matcher matcher = pattern.matcher(source);
        if (!matcher.find()) {
            throw new FormatException("Bad host name '%s': %s", source,
                "Should match pattern: " + pattern.toString());
        } else {
            return source;
        }
    }
}
