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
 * $Id: FreeLabelParser.java,v 1.3 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.view.FormatException;

/**
 * Parser that turns a string into a default label, without (un)quoting or
 * (un)escaping.
 */
public class FreeLabelParser implements LabelParser {
    /**
     * Empty constructor with limited visibility, for creating the singleton
     * instance.
     */
    protected FreeLabelParser() {
        // Empty
    }

    /**
     * Calls {@link #isCorrect(String)} to test the string for correctness. If
     * this succeeds, returns a {@link DefaultLabel} with <code>text</code> as
     * label text. If it fails, throws an exception determined by
     * {@link #getExceptionText(String)}.
     * @throws FormatException if <code>text</code> is not correctly formatted.
     *         The message of the exception should make clear what the mismatch
     *         is.
     */
    final public Label parse(String text) throws FormatException {
        if (!isCorrect(text)) {
            throw new FormatException(getExceptionText(text));
        }
        return DefaultLabel.createLabel(text);
    }

    /**
     * Callback method to test if a given label text adheres to the formatting
     * standards of this class. To be overridden by subclasses; this
     * implementation is empty.
     * @param text the string to be tested
     * @return <code>true</code> if the label text is correct.
     */
    protected boolean isCorrect(String text) {
        return true;
    }

    /**
     * Returns the text of the exception to be thrown in case the label format
     * is incorrect.
     */
    protected String getExceptionText(String text) {
        return String.format("Incorrectly formatted label %s", text);
    }

    /** This implementation returns a default label for the label text. */
    final public DefaultLabel unparse(Label label) {
        String text = label.text();
        assert isCorrect(text);
        return DefaultLabel.createLabel(text);
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static FreeLabelParser getInstance() {
        return instance;
    }

    /** Singleton instance of this class. */
    static private final FreeLabelParser instance = new FreeLabelParser();
}