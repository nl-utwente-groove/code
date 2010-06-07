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
package groove.algebra;

import groove.util.ExprParser;
import groove.view.FormatException;

/**
 * Default implementation of booleans.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StringAlgebra extends StringSignature<String,Boolean> implements Algebra<String> {
    @Override
    public String concat(String arg0, String arg1) {
        return arg0.concat(arg1);
    }

    @Override
    public Boolean eq(String arg0, String arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public Boolean ge(String arg0, String arg1) {
        return arg0.compareTo(arg1) >= 0;
    }

    @Override
    public Boolean gt(String arg0, String arg1) {
        return arg0.compareTo(arg1) > 0;
    }

    @Override
    public Boolean le(String arg0, String arg1) {
        return arg0.compareTo(arg1) <= 0;
    }

    @Override
    public Boolean lt(String arg0, String arg1) {
        return arg0.compareTo(arg1) < 0;
    }

    public String getName() {
        return NAME;
    }

    public String getSymbol(Object value) {
        return ExprParser.toQuoted((String) value, ExprParser.DOUBLE_QUOTE_CHAR);
    }

    public String getValue(String constant) {
        try {
            return ExprParser.toUnquoted(constant, ExprParser.DOUBLE_QUOTE_CHAR);
        } catch (FormatException e) {
            return null;
        }
    }
    
    /** The name of this algebra. */
    static public final String NAME = "string";
}
