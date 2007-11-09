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
 * $Id: FreeLabelParser.java,v 1.1 2007-11-09 13:01:09 rensink Exp $
 */
package groove.view;

import groove.graph.DefaultLabel;
import groove.graph.Label;

/** 
 * Parser that turns a string into a default label,
 * without (un)quoting or (un)escaping.
 */
public class FreeLabelParser implements LabelParser {
    /** 
     * Calls {@link #testFormat(String)} to test the string for correctness.
     * If this succeeds, returns a {@link DefaultLabel} with <code>text</code> as label text.
     */ 
	public Label parse(String text) throws FormatException {
//        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(QUOTE_CHAR+text));
//        tokenizer.quoteChar(QUOTE_CHAR);
//        try {
//            tokenizer.nextToken();
//        } catch (IOException exc) {
//            assert false;
//        }
//        String unquoted = ExprParser.toUnquoted(text, ExprParser.SINGLE_QUOTE_CHAR);
//        if (unquoted != null) {
//            text = unquoted;
//        }
		testFormat(text);
		return DefaultLabel.createLabel(text);
	}

	/** 
	 * Callback method to test if a given text adheres to the formatting
	 * standards of this class.
	 * To be overridden by subclasses; this implementation is empty.
	 * @param text the string to be tested
	 * @throws FormatException if <code>text</code> is not correctly
	 * formatted. The message of the exception should make clear what the
	 * mismatch is.
	 */
	protected void testFormat(String text) throws FormatException {
		// empty
	}

    /** This implementation just returns the label text. */
    public String unparse(Label label) {
        String result = label.text();
//        if (!RegExpr.isAtom(result)) {
//            result = RegExpr.atom(result).toString();
//        }
        return result;
    }
//
//    /** Dummy quote character for parsing the string. */
//    static private final char QUOTE_CHAR = '\u0000';
}
