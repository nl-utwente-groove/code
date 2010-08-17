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
public abstract class StringSignature<MyString,Bool> implements Signature {
    /** String concatenation. */
    public abstract MyString concat(MyString arg0, MyString arg1);

    /** Lesser-than comparison. */
    public abstract Bool lt(MyString arg0, MyString arg1);

    /** Lesser-or-equal comparison. */
    public abstract Bool le(MyString arg0, MyString arg1);

    /** Greater-than comparison. */
    public abstract Bool gt(MyString arg0, MyString arg1);

    /** Greater-or-equal comparison. */
    public abstract Bool ge(MyString arg0, MyString arg1);

    /** Equality test. */
    public abstract Bool eq(MyString arg0, MyString arg1);

    /**
     * Tests if the string value is surrounded with double quotes.
     * @see ExprParser#toUnquoted(String, char)
     */
    public final boolean isValue(String value) {
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
}
