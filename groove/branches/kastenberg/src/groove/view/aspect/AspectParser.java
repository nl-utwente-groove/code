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
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.rel.RegExpr;
import groove.util.ExprParser;
import groove.view.FormatException;
import groove.view.aspect.AttributeAspect.ConstantAspectValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectParser {
    /**
     * Constructs a parser for a given aspect graph.
     */
    public AspectParser(GraphShape graph) {
        this.rule = GraphInfo.hasRuleRole(graph);
        this.convertToCurly = this.rule && GraphInfo.getVersion(graph) == null;
    }

    /**
     * Extracts the aspect information from a plain label text.
     * @param text the text to start from
     * @return an object containing information about the aspect value, the
     *         possible end marker, and the possible actual label text present
     *         in <code>plainText</code>
     * @throws FormatException if <code>plainText</code> contains an apparent
     *         aspect value that is not recognised by
     *         {@link AspectValue#getValue(String)}.
     */
    public AspectMap parse(String text) throws FormatException {
        List<AspectValue> aspectValues = new ArrayList<AspectValue>();
        boolean end = false;
        boolean edgeOnly = false;
        int nextIndex = text.indexOf(VALUE_SEPARATOR);
        while (!end && nextIndex >= 0) {
            // look for the next aspect value between prevIndex and nextIndex
            String valueText = text.substring(0, nextIndex);
            try {
                String contentText;
                int assignIndex = valueText.indexOf(CONTENT_ASSIGN);
                if (assignIndex < 0) {
                    contentText = null;
                } else {
                    contentText =
                        valueText.substring(assignIndex
                            + CONTENT_ASSIGN.length());
                    valueText = valueText.substring(0, assignIndex);
                }
                AspectValue value = parseValue(valueText, contentText);
                aspectValues.add(value);
                end = value.isLast();
                edgeOnly |= !value.isNodeValue();
            } catch (FormatException exc) {
                throw new FormatException("%s in '%s'", exc.getMessage(), text);
            }
            text = text.substring(nextIndex + 1);
            nextIndex = text.indexOf(VALUE_SEPARATOR);
        }
        if (edgeOnly || text.length() > 0) {
            if (this.convertToCurly) {
                text = toCurly(text);
            }
        } else if (aspectValues.isEmpty()) {
            throw new FormatException(
                "Empty label not allowed (prefix with ':')");
        } else {
            text = null;
        }
        AspectMap result = new AspectMap(this.rule);
        for (AspectValue value : aspectValues) {
            // test if this should be a data constant
            if (AttributeAspect.isDataValue(value) && text != null) {
                String signature = value.getName();
                try {
                    if (AlgebraRegister.isConstant(signature, text)) {
                        value = ((ConstantAspectValue) value).newValue(text);
                        text = null;
                    } else if (!ExprParser.isIdentifier(text)) {
                        throw new FormatException(
                            "Signature '%s' does not have constant %s",
                            signature, text);
                    }
                } catch (UnknownSymbolException e) {
                    assert false : String.format(
                        "Method called for unknown signature '%s'", signature);
                    // do nothing
                }
            }
            result.addDeclaredValue(value);
        }
        result.setText(text);
        return result;
    }

    /**
     * Converts a given text to curly-bracketed regular expression format, if it
     * is an unquoted regular expression. If it is a quoted atom, removes the
     * quotes.
     */
    private String toCurly(String text) {
        try {
            RegExpr expr = RegExpr.parse(text);
            if (expr.isAtom()) {
                text = expr.getAtomText();
            } else if (expr.isEmpty()) {
                // do nothing
            } else if (expr.isNeg()) {
                text =
                    RegExpr.NEG_OPERATOR
                        + toCurly(expr.getNegOperand().toString());
            } else {
                text = ExprParser.LCURLY_CHAR + text + ExprParser.RCURLY_CHAR;
            }
        } catch (FormatException exc) {
            // the text should be treated as an atom; do nothing
        }
        return text;
    }

    /**
     * Returns the aspect value obtained by parsing a given value and content
     * text.
     * @param valueText string description of a new {@link AspectValue}
     * @param contentText string description for the new value's content;
     *        <code>null</code> if the aspect value is not a
     *        {@link ContentAspectValue}.
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
            if (value instanceof ContentAspectValue<?>) {
                // use the value as a factory to get a correct instance
                value = ((ContentAspectValue<?>) value).newValue(contentText);
            } else {
                throw new FormatException(String.format(
                    "Aspect value '%s' cannot have content", valueText));
            }
        }
        return value;
    }

    /** Indicates that this parser works for a rule. */
    private final boolean rule;
    /**
     * Indicates that label text should be converted to curly-bracketed format.
     */
    private final boolean convertToCurly;

    /**
     * Turns an aspect value into a string that can be read by
     * {@link #parse(String)}.
     */
    static public String toString(AspectValue value) {
        String result;
        if (value instanceof ConstantAspectValue) {
            result = value.getName() + VALUE_SEPARATOR;
            String content = ((ConstantAspectValue) value).getContent();
            if (content != null) {
                result += content;
            }
        } else {
            result = value.toString() + VALUE_SEPARATOR;
        }
        return result;
    }

    /**
     * Converts a collection of aspect values plus an actual label text into a
     * string that can be parsed back.
     */
    static public StringBuilder toString(Collection<AspectValue> values,
            StringBuilder labelText) {
        StringBuilder result = new StringBuilder();
        for (AspectValue value : values) {
            result.append(AspectParser.toString(value));
        }
        //        if (labelText.length() == 0
        //            || labelText.indexOf("" + VALUE_SEPARATOR) >= 0) {
        //            result.append(VALUE_SEPARATOR);
        //        }
        result.append(labelText);
        return result;
    }
}
