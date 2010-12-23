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
 * $Id: AspectParser.java,v 1.16 2008-03-13 14:41:55 rensink Exp $
 */
package groove.view.aspect;

import static groove.view.aspect.Aspect.CONTENT_ASSIGN;
import static groove.view.aspect.Aspect.VALUE_SEPARATOR;
import groove.algebra.AlgebraRegister;
import groove.algebra.UnknownSymbolException;
import groove.graph.DefaultLabel;
import groove.graph.TypeLabel;
import groove.util.ExprParser;
import groove.view.FormatException;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision: 2929 $
 */
public class AspectParser {
    /**
     * Converts a plain label to an aspect label.
     * @param label the plain label to start from
     * @return an aspect label, in which the aspect prefixes of {@code label}
     * have been parsed into aspect values.
     * @throws FormatException if there were parse errors in {@code label}
     */
    public AspectLabel parse(DefaultLabel label) throws FormatException {
        AspectLabel result = new AspectLabel();
        try {
            parse(label.text(), result);
        } catch (FormatException exc) {
            throw new FormatException("%s in '%s'", exc.getMessage(), label);
        }
        return result;
    }

    /**
     * Recursively parses a string into an aspect label passed in as a parameter.
     * @param text the text to be parsed
     * @param result the aspect label to receive the result
     * @throws FormatException if there were parse errors in {@code text}
     */
    private void parse(String text, AspectLabel result) throws FormatException {
        AspectValue value = null;
        String valueText = nextValue(text);
        if (valueText != null) {
            text = text.substring(valueText.length() + 1);
            // parse the value text into a value
            String contentText;
            int assignIndex = valueText.indexOf(CONTENT_ASSIGN);
            if (assignIndex < 0) {
                contentText = null;
            } else {
                contentText =
                    valueText.substring(assignIndex + CONTENT_ASSIGN.length());
                valueText = valueText.substring(0, assignIndex);
            }
            value = parseValue(valueText, contentText);
            // test if this should be a data constant
            if (AttributeAspect.isDataValue(value) && text.length() > 0) {
                String signature = value.getName();
                try {
                    if (AlgebraRegister.isConstant(signature, text)) {
                        value = value.newValue(text);
                        text = "";
                    } else if (!ExprParser.isIdentifier(text)) {
                        throw new FormatException(
                            "Signature '%s' does not have constant %s",
                            signature, text);
                    }
                } catch (UnknownSymbolException e) {
                    // this can't happen, as the data values are valid signatures
                    assert false : String.format(
                        "Method called for unknown signature '%s'", signature);
                }
            }
        }
        if (value != null) {
            result.addAspect(value);
        }
        if (value == null || value.isLast() || text.length() == 0) {
            result.setInnerText(text);
            result.setFixed();
        } else {
            // recursively call the method with the remainder of the text
            parse(text, result);
        }
    }

    /** 
     * Determines the next aspect value.
     * @return the next aspect value in {@code text},
     * or {@code null} if there is no next aspect value 
     */
    private String nextValue(String text) {
        int result = text.indexOf(VALUE_SEPARATOR);
        if (result > 0) {
            if (!Character.isLetter(text.charAt(0))
                || TypeLabel.getPrefix(text) != null) {
                result = -1;
            }
        }
        if (result < 0) {
            return null;
        } else {
            return text.substring(0, result);
        }
    }

    /**
     * Returns the aspect value obtained by parsing a given value and content
     * text.
     * @param valueText string description of a new {@link AspectValue}
     * @param contentText string description for the new value's content;
     * may be {@code null}
     * @return the resulting aspect value
     * @throws FormatException if <code>valueText</code> is not a valid
     *         {@link AspectValue}, or the presence of content is not as it
     *         should be.
     */
    private AspectValue parseValue(String valueText, String contentText)
        throws FormatException {
        AspectValue value = AspectValue.getValue(valueText);
        if (value == null) {
            throw new FormatException(
                String.format(
                    "Unknown aspect value '%s' (precede label text with ':' to avoid aspect parsing)",
                    valueText));
        } else if (contentText != null) {
            // use the value as a factory to get a correct instance
            value = value.newValue(contentText);
        }
        return value;
    }

    /** Yields a predefined label for a given graph role. */
    public static AspectParser getInstance() {
        return instance;
    }

    static private final AspectParser instance = new AspectParser();
}
