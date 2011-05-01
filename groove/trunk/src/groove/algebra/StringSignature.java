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
 * $Id$
 */
package groove.algebra;

import groove.util.ExprParser;
import groove.view.FormatException;

/**
 * Signature for strings in graph grammars.
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("hiding")
public abstract class StringSignature<String,Bool> implements Signature {
    /** String concatenation. */
    public abstract String concat(String arg0, String arg1);

    /** Lesser-than comparison. */
    public abstract Bool lt(String arg0, String arg1);

    /** Lesser-or-equal comparison. */
    public abstract Bool le(String arg0, String arg1);

    /** Greater-than comparison. */
    public abstract Bool gt(String arg0, String arg1);

    /** Greater-or-equal comparison. */
    public abstract Bool ge(String arg0, String arg1);

    /** Equality test. */
    public abstract Bool eq(String arg0, String arg1);

    /**
     * Tests if the string value is surrounded with double quotes.
     * @see ExprParser#toUnquoted(java.lang.String, char)
     */
    public final boolean isValue(java.lang.String value) {
        if (value.indexOf(ExprParser.DOUBLE_QUOTE_CHAR) != 0) {
            return false;
        }
        try {
            ExprParser.toUnquoted(value, ExprParser.DOUBLE_QUOTE_CHAR);
            return true;
        } catch (FormatException e) {
            return false;
        }
    }

    /**
     * Conversion of native Java representation of string constants to
     * the corresponding algebra values.
     * @throws IllegalArgumentException if the parameter is not of type {@link java.lang.String}
     */
    final public String getValueFromJava(Object constant) {
        if (!(constant instanceof java.lang.String)) {
            throw new IllegalArgumentException(java.lang.String.format(
                "Native int type is %s, not %s",
                java.lang.String.class.getSimpleName(),
                constant.getClass().getSimpleName()));
        }
        return toValue((java.lang.String) constant);
    }

    /** 
     * Callback method to convert from the native ({@link Integer})
     * representation to the algebra representation.
     */
    protected abstract String toValue(java.lang.String constant);
}
