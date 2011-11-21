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
 * $Id: DefaultLabel.java,v 1.14 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

import groove.algebra.SignatureKind;
import groove.io.HTMLConverter;
import groove.trans.RuleLabel;
import groove.util.ExprParser;
import groove.view.FormatException;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Labels encapsulating node or edge types.
 * These are the labels that occur in host graphs.
 * @author Arend Rensink
 * @version $Revision: 2709 $
 */
public final class TypeLabel extends AbstractLabel {
    /**
     * Constructs a standard implementation of Label on the basis of a given
     * text index. For internal purposes only.
     * @param text the label text
     * @param kind indicator of the type of label (normal, node type or flag).
     */
    TypeLabel(String text, EdgeRole kind) {
        this.text = text;
        this.role = kind;
    }

    public String text() {
        return this.text;
    }

    /** Returns the prefixed text. */
    @Override
    public String toString() {
        return getRole().getPrefix() + text();
    }

    @Override
    public EdgeRole getRole() {
        return this.role;
    }

    /** Indicates if this label stands for a data type. */
    public boolean isDataType() {
        return isNodeType() && SignatureKind.getNames().contains(text());
    }

    /** The label text. */
    private final String text;
    /** The type of label (normal, node type or flag). */
    private final EdgeRole role;

    /** Returns the node type label for a given data signature. */
    static public final TypeLabel getLabel(SignatureKind sigKind) {
        return sigLabelMap.get(sigKind);
    }

    /**
     * Returns a default or node type label, depending on the prefix in the
     * input string.
     * @param prefixedText text of the label, possibly prefixed with a label
     * kind
     * @return a label with label type determined by the prefix
     */
    public static TypeLabel createLabel(String prefixedText) {
        return TypeFactory.instance().createLabel(prefixedText);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label. The returned label is binary.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     */
    public static TypeLabel createBinaryLabel(String text) {
        return TypeFactory.instance().createLabel(EdgeRole.BINARY, text);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string and label kind, while optionally testing if this label is legal. 
     * The string is used as-is, and is guaranteed to
     * equal the text of the resulting label.
     * @param kind kind of label to be created
     * @param text the text of the label; non-null
     * @param test if {@code true}, a {@link groove.view.FormatException} may be thrown
     * if {@code text} does not satisfy the requirements of {@code kind}-labels.
     * @return an existing or new label with the given text and kind; non-null
     * @see #createLabel(EdgeRole, String)
     * @throws FormatException if {@code text} does not satisfy the constraints
     * for labels of kind {@code kind}
     */
    public static TypeLabel createLabel(EdgeRole kind, String text, boolean test)
        throws FormatException {
        if (test && kind != EdgeRole.BINARY && !ExprParser.isIdentifier(text)) {
            throw new FormatException(
                "%s label '%s' is not a valid identifier",
                kind.getDescription(true), text);
        }
        return createLabel(kind, text);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string and label kind. The string is used as-is, and is guaranteed to
     * equal the text of the resulting label.
     * @param kind kind of label to be created
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text and kind; non-null
     * @see #getRole()
     */
    public static TypeLabel createLabel(EdgeRole kind, String text) {
        return TypeFactory.instance().createLabel(kind, text);
    }

    /**
     * Returns a HTML-formatted string for a given label, without the
     * surrounding html-tag. The string is set to bold if the label is a node
     * type and is set to italic if the label is a flag.
     */
    static public String toHtmlString(Label label) {
        String result = HTMLConverter.toHtml(label.text());
        if (label.isNodeType()) {
            result = HTMLConverter.STRONG_TAG.on(result);
        } else if (label.isFlag()) {
            result = HTMLConverter.ITALIC_TAG.on(result);
        } else if (label instanceof RuleLabel) {
            RuleLabel ruleLabel = (RuleLabel) label;
            if (!ruleLabel.isAtom() && !ruleLabel.isSharp()) {
                result = HTMLConverter.ITALIC_TAG.on(result);
            }
        }
        return result;
    }

    /**
     * Returns the text of the label string, prefixed by the node type 
     * or flag aspect if the label is a node type or flag.
     */
    static public String toPrefixedString(Label label) {
        return label.getRole().getPrefix() + label.text();
    }

    static private final Map<SignatureKind,TypeLabel> sigLabelMap =
        new EnumMap<SignatureKind,TypeLabel>(SignatureKind.class);
    static {
        for (SignatureKind sigKind : EnumSet.allOf(SignatureKind.class)) {
            sigLabelMap.put(sigKind, new TypeLabel(sigKind.getName(),
                EdgeRole.NODE_TYPE));
        }
    }

    /** Type label for nodes in an untyped setting. */
    static public final TypeLabel NODE = new TypeLabel("\u03A9",
        EdgeRole.NODE_TYPE);

}