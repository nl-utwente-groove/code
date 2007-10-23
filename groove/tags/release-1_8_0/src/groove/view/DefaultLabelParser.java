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
 * $Id: DefaultLabelParser.java,v 1.3 2007-08-26 07:24:10 rensink Exp $
 */
package groove.view;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

import groove.graph.DefaultLabel;
import groove.graph.Label;

/** 
 * Parser that turns a string into a default label,
 * after testing the string for correct formatting using a 
 * callback method that can be overridden by subclasses. 
 */
public class DefaultLabelParser implements LabelParser {
	public Label parse(String text) throws FormatException {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(QUOTE_CHAR+text));
        tokenizer.quoteChar(QUOTE_CHAR);
        try {
            tokenizer.nextToken();
        } catch (IOException exc) {
            assert false;
        }
        text = tokenizer.sval;
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

    /** This implementation just takes the label text. */
    public String unparse(Label label) {
        return label.text();
    }

    /** Dummy quote character for parsing the string. */
    static private final char QUOTE_CHAR = '\u0000';
}
