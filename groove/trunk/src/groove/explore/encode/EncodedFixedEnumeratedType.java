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

import java.util.Map;

/**
 * <!=========================================================================>
 * An <code>EncodedFixedEnumeratedType</code> is a
 * <code>EncodedEnumeratedType</code> in which the enumeration is fixed (does
 * not depend on the simulator). The generation of the enumeration, as well as
 * associating results with enumeration values, must be implemented manually by
 * the subclass, but the <code>parse()</code> method is provided automatically.
 * <!=========================================================================>
 * @see EncodedEnumeratedType
 * @author Maarten de Mol
 */
public abstract class EncodedFixedEnumeratedType<A> extends
        EncodedEnumeratedType<A> {

    /**
     * Defines the EnumeratedType by generating a fixed Map of options that are
     * available for selection.
     * This method must be overridden by the subclass.
     */
    public abstract Map<String,String> fixedOptions();

    /**
     * Associates options with values. The values must be independent of the
     * current grammar, and may not depend on it in any way.
     */
    public abstract Map<String,A> fixedValues();

    /**
     * Override the variable generation of options by simply returning the
     * fixed generation.
     */
    @Override
    public Map<String,String> generateOptions(GrammarModel grammar) {
        return fixedOptions();
    }

    /**
     * Implement parsing by inspecting the fixed value map.
     */
    @Override
    public A parse(Grammar rules, String source) throws FormatException {
        A value = fixedValues().get(source);
        if (value != null) {
            return value;
        } else {
            StringBuffer msg =
                new StringBuffer("'" + source + "' is not a "
                    + "valid enumeration value. Expected one of:");
            for (String id : fixedValues().keySet()) {
                msg.append(" '" + id + "'");
            }
            msg.append(".");
            throw new FormatException(msg.toString());
        }
    }
}
