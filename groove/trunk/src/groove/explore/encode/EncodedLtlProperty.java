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

import groove.gui.Simulator;
import groove.trans.GraphGrammar;
import groove.verify.FormulaParser;
import groove.verify.ParseException;
import groove.view.FormatException;

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
    public EncodedTypeEditor<String,String> createEditor(Simulator simulator) {
        return new StringEditor<String>("", 20);
    }

    @Override
    public String parse(GraphGrammar rules, String source)
        throws FormatException {
        try {
            FormulaParser.parse(source).toLtlFormula();
            return source;
        } catch (ParseException e) {
            throw new FormatException("Error in LTL formula '%s': %s", source,
                e.getMessage());
        }
    }
}
