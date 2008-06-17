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
 * $Id: NumberLabelParser.java,v 1.3 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;


/** 
 * Parser that turns a string into a default label,
 * after testing the string for correct formatting using a 
 * callback method that can be overridden by subclasses. 
 */
public class NumberLabelParser extends FreeLabelParser {
    /** Empty constructor for the singleton instance. */
    private NumberLabelParser() {
        // Empty
    }
    
    @Override
    protected String getExceptionText(String text) {
        try {
            Integer.parseInt(text);
            // if this succeeds, the problem was a negative number
            return String.format("String '%s' is a negative number", text);
        } catch (NumberFormatException exc) {
            return String.format("String '%s' cannot be parsed as a number", text);
        }
    }

    @Override
    protected boolean isCorrect(String text) {
        try {
            return Integer.parseInt(text) >= 0;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static NumberLabelParser getInstance() {
        return instance;
    }

    /** The singleton instance of this parser. */
    private static final NumberLabelParser instance = new NumberLabelParser();
}
