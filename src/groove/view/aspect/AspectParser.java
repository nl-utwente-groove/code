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

import groove.algebra.Algebras;
import groove.graph.DefaultLabel;
import groove.graph.GraphRole;
import groove.graph.LabelKind;
import groove.util.ExprParser;
import groove.view.FormatException;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision: 2929 $
 */
public class AspectParser {
    /** Creates an aspect parser for a particular graph role. */
    private AspectParser(GraphRole role) {
        this.role = role;
    }

    /**
     * Converts a plain label to an aspect label.
     * @param label the plain label to start from
     * @return an aspect label, in which the aspect prefixes of {@code label}
     * have been parsed into aspect values.
     */
    public AspectLabel parse(DefaultLabel label) {
        AspectLabel result = new AspectLabel(this.role);
        try {
            parse(label.text(), result);
        } catch (FormatException exc) {
            result.addError("%s in '%s'", exc.getMessage(), label);
        }
        result.setFixed();
        return result;
    }

    /**
     * Recursively parses a string into an aspect label passed in as a parameter.
     * @param text the text to be parsed
     * @param result the aspect label to receive the result
     * @throws FormatException if there were parse errors in {@code text}
     */
    private void parse(String text, AspectLabel result) throws FormatException {
        Aspect value = null;
        String valueText = nextValue(text);
        if (valueText != null) {
            text = text.substring(valueText.length() + 1);
            // parse the value text into a value
            String contentText;
            int assignIndex = valueText.indexOf(ASSIGN);
            if (assignIndex < 0) {
                contentText = null;
            } else {
                contentText =
                    valueText.substring(assignIndex + ASSIGN.length());
                valueText = valueText.substring(0, assignIndex);
            }
            value = parseValue(valueText, contentText);
            // test if this should be a data constant
            if (value.getKind().isTypedData() && text.length() > 0) {
                String signature = value.getKind().getName();
                String sigForConstant = Algebras.getSigNameFor(text);
                if (signature.equals(sigForConstant)) {
                    value = value.newInstance(text);
                    text = "";
                } else if (sigForConstant != null) {
                    throw new FormatException("Value %s belongs to type %s",
                        text, signature);
                } else if (!ExprParser.isIdentifier(text)) {
                    throw new FormatException(
                        "Type '%s' does not have value %s", signature, text);
                }
            }
        }
        if (value != null) {
            result.addAspect(value);
        }
        if (value == null || value.getKind().isLast() || text.length() == 0) {
            result.setInnerText(text);
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
        int result = text.indexOf(SEPARATOR);
        if (result > 0) {
            if (!Character.isLetter(text.charAt(0))
                || !LabelKind.parse(text).two().equals(text)) {
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
     * @param name string description of a new {@link Aspect}
     * @param contentText string description for the new value's content;
     * may be {@code null}
     * @return the resulting aspect value
     * @throws FormatException if <code>valueText</code> is not a valid
     *         {@link Aspect}, or the presence of content is not as it
     *         should be.
     */
    private Aspect parseValue(String name, String contentText)
        throws FormatException {
        Aspect value = Aspect.getAspect(name);
        if (value == null) {
            throw new FormatException(
                String.format(
                    "Unknown aspect value '%s' (precede label text with ':' to avoid aspect parsing)",
                    name));
        } else if (contentText != null) {
            // use the value as a factory to get a correct instance
            value = value.newInstance(contentText);
        }
        return value;
    }

    /** The graph role of this aspect parser. */
    private final GraphRole role;

    /** Separator between aspect name and associated content. */
    static public final String ASSIGN = "=";

    /** Separator between aspect prefix and main label text. */
    static public final String SEPARATOR = ":";

    /** Yields a predefined label parser for a given graph role. */
    public static AspectParser getInstance(GraphRole role) {
        return instances.get(role);
    }

    static private final Map<GraphRole,AspectParser> instances =
        new EnumMap<GraphRole,AspectParser>(GraphRole.class);

    static {
        for (GraphRole role : EnumSet.allOf(GraphRole.class)) {
            instances.put(role, new AspectParser(role));
        }
    }
}
