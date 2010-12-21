/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: NamedAspectValue.java,v 1.3 2008-01-30 09:31:32 iovka Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Aspect value encoding the nesting level within a nested rule or condition.
 * The nesting level is a string interpreted as an identifier of the level.
 * @author kramor
 * @version $Revision $
 */
public class NamedAspectValue extends AspectValue {
    /**
     * Constructs a new nesting level-containing aspect value.
     * @param name the aspect value name
     * @throws FormatException if <code>name</code> is an already existing
     *         aspect value
     */
    public NamedAspectValue(Aspect aspect, String name) throws FormatException {
        super(aspect, name, true);
    }

    /** Creates a value wrapping a given quantifier level name. */
    private NamedAspectValue(NamedAspectValue original, String level)
        throws FormatException {
        super(original, level);
    }

    @Override
    public NamedAspectValue newValue(String value) throws FormatException {
        if (value.length() != 0) {
            if (!isValidFirstChar(value.charAt(0))) {
                throw new FormatException(
                    "Invalid start character '%c' in name '%s'",
                    value.charAt(0), value);
            }
            for (int i = 1; i < value.length(); i++) {
                char c = value.charAt(i);
                if (!isValidNextChar(c)) {
                    throw new FormatException(
                        "Invalid character '%c' in name '%s'", c, value);
                }
            }
        }
        return new NamedAspectValue(this, value);
    }

    /**
     * Indicates if a given character is allowed in level names. Currently
     * allowed are: letters, digits, currency symbols, underscores and periods.
     * @param c the character to be tested
     */
    public boolean isValidFirstChar(char c) {
        return Character.isJavaIdentifierStart(c);
    }

    /**
     * Indicates if a given character is allowed in level names. Currently
     * allowed are: letters, digits, currency symbols, underscores and periods.
     * @param c the character to be tested
     */
    public boolean isValidNextChar(char c) {
        return Character.isJavaIdentifierPart(c);
    }
}
