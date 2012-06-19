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

import groove.algebra.Algebras;
import groove.trans.RuleLabel;
import groove.util.Converter;
import groove.util.ExprParser;
import groove.view.FormatException;

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
     * @param index the index of the label text
     */
    TypeLabel(String text, EdgeRole kind, int index) {
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
        return isNodeType() && Algebras.getSigNames().contains(text());
    }

    /** The label text. */
    private final String text;
    /** The type of label (normal, node type or flag). */
    private final EdgeRole role;

    /**
     * Returns a default or node type label, depending on the prefix in the
     * input string.
     * @param prefixedText text of the label, possibly prefixed with a label
     * kind
     * @return a label with label type determined by the prefix
     */
    public static TypeLabel createLabel(String prefixedText) {
        return factory.createLabel(prefixedText);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label. The returned label is binary.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     */
    public static TypeLabel createBinaryLabel(String text) {
        return factory.createLabel(EdgeRole.BINARY, text);
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
                "%s label '%s' is not a valid identifier", kind.getDescription(true),
                text);
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
        return factory.createLabel(kind, text);
    }

    /**
     * Yields the number of labels created in the course of the program.
     * @return Number of labels created
     */
    public static int getLabelCount() {
        return factory.getLabelCount();
    }

    /**
     * Returns a HTML-formatted string for a given label, without the
     * surrounding html-tag. The string is set to bold if the label is a node
     * type and is set to italic if the label is a flag.
     */
    static public String toHtmlString(Label label) {
        String result = Converter.toHtml(label.text());
        if (label.isNodeType()) {
            result = Converter.STRONG_TAG.on(result);
        } else if (label.isFlag()) {
            result = Converter.ITALIC_TAG.on(result);
        } else if (label instanceof RuleLabel) {
            RuleLabel ruleLabel = (RuleLabel) label;
            if (!ruleLabel.isAtom() && !ruleLabel.isSharp()) {
                result = Converter.ITALIC_TAG.on(result);
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

    //
    //    /**
    //     * Returns the textual prefix belonging to a given label kind.
    //     * @return the label type corresponding to {@code labelType}, including the
    //     *         {@link #KIND_SEPARATOR} where necessary; {@code null} if {@code
    //     *         labelType} is not a valid label type.
    //     */
    //    public static String getPrefix(int labelKind) {
    //        switch (labelKind) {
    //        case NODE_TYPE:
    //            return NODE_TYPE_PREFIX + KIND_SEPARATOR;
    //        case FLAG:
    //            return FLAG_PREFIX + KIND_SEPARATOR;
    //        case BINARY:
    //            return "";
    //        default:
    //            return null;
    //        }
    //    }
    //
    //    //    /** 
    //     * Splits a given string into a label kind indicator and the remainder
    //     * of the string. The label kind is determined by testing the string for
    //     * the corresponding prefix. The original text equals 
    //     * {@code result.two()+getPrefix(result.one())}.
    //     * @see #getPrefix(int)
    //     * @see #NODE_TYPE_PREFIX
    //     * @see #FLAG_PREFIX
    //     */
    //    static public Pair<Integer,String> splitKind(String text) {
    //        int kind;
    //        if (text.startsWith(getPrefix(NODE_TYPE))) {
    //            kind = NODE_TYPE;
    //        } else if (text.startsWith(getPrefix(FLAG))) {
    //            kind = FLAG;
    //        } else {
    //            kind = BINARY;
    //        }
    //        return new Pair<Integer,String>(kind,
    //            text.substring(getPrefix(kind).length()));
    //    }
    //
    //    /**
    //     * Returns a descriptor for a given label kind.
    //     * A descriptor is a human-readable explanation of the label kind.
    //     */
    //    public static String getDescriptor(int labelKind) {
    //        return kindDescriptors[labelKind];
    //    }

    static private final TypeFactory factory = TypeFactory.instance();
    //    /** Separator between label kind prefix and label text. */
    //    static public final char KIND_SEPARATOR = ':';
    //    /** Prefix indicating that a label is a node type. */
    //    static public final String FLAG_PREFIX = "flag";
    //    /** Prefix indicating that a label is a flag. */
    //    static public final String NODE_TYPE_PREFIX = "type";
    //    /** Array of descriptions for the various label kinds. */
    //    static private final String[] kindDescriptors = new String[] {"Type",
    //        "Flag", "Binary"};

}