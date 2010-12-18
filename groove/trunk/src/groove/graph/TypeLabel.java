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

import groove.algebra.Algebra;
import groove.algebra.AlgebraRegister;
import groove.util.Converter;
import groove.util.ExprParser;
import groove.view.FormatException;
import groove.view.aspect.AspectValue;
import groove.view.aspect.RuleAspect;

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
     *        The value is one of {@link Label#NODE_TYPE}, {@link Label#FLAG} 
     *        or {@link Label#BINARY}.
     * @param index the index of the label text
     */
    TypeLabel(String text, int kind, int index) {
        this.text = text;
        this.kind = kind;
    }

    /* A LabelTree may mix labels of different kinds, therefore it is 
     * better for now to keep to the default notion of equality
    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        assert result == super.equals(obj) : String.format(
            "Distinct label objects of type %s and %s for label %s",
            this.getClass().getName(), obj.getClass().getName(), text());
        return result;
    }
    */

    public String text() {
        return this.text;
    }

    @Override
    public int getKind() {
        return this.kind;
    }

    /** Indicates if this label stands for a data type. */
    public boolean isDataType() {
        return isNodeType()
            && AlgebraRegister.getSignatureNames().contains(text());
    }

    /** The label text. */
    private final String text;
    /** The type of label (normal, node type or flag). */
    private final int kind;

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label. The returned label is binary.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     */
    public static TypeLabel createLabel(String text) {
        return factory.createLabel(text, BINARY);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string and label kind, while optionally testing if this label is legal. 
     * The string is used as-is, and is guaranteed to
     * equal the text of the resulting label.
     * @param text the text of the label; non-null
     * @param kind kind of label to be created
     * @param test if {@code true}, a {@link groove.view.FormatException} may be thrown
     * if {@code text} does not satisfy the requirements of {@code kind}-labels.
     * @return an existing or new label with the given text and kind; non-null
     * @see #createLabel(String, int)
     * @throws FormatException if {@code text} does not satisfy the constraints
     * for labels of kind {@code kind}
     */
    public static TypeLabel createLabel(String text, int kind, boolean test)
        throws FormatException {
        if (test && kind != BINARY && !ExprParser.isIdentifier(text)) {
            throw new FormatException(
                "%s label '%s' is not a valid identifier",
                kindDescriptors[kind], text);
        }
        return createLabel(text, kind);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string and label kind. The string is used as-is, and is guaranteed to
     * equal the text of the resulting label.
     * @param text the text of the label; non-null
     * @param kind kind of label to be created
     * @return an existing or new label with the given text and kind; non-null
     * @see #getKind()
     */
    public static TypeLabel createLabel(String text, int kind) {
        return factory.createLabel(text, kind);
    }

    /**
     * Returns a default or node type label, depending on the prefix in the
     * input string.
     * @param prefixedText text of the label, possibly prefixed with a type
     *        prefix {@link #NODE_TYPE_PREFIX} or {@link #FLAG_PREFIX}
     * @return a label with label type determined by the prefix
     */
    public static TypeLabel createTypedLabel(String prefixedText) {
        return factory.createLabel(prefixedText);
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
        }
        return result;
    }

    /**
     * Returns a HTML-formatted string for a given label in combination
     * with its edge role, without the
     * surrounding html-tag. The string is set to bold if the label is a node
     * type and is set to italic if the label is a flag; colours and
     * prefixes are used to indicate the role.
     */
    public static String toHtmlString(Label edgeLabel, AspectValue edgeRole) {
        String result = toHtmlString(edgeLabel);
        if (RuleAspect.ERASER.equals(edgeRole)) {
            result = Converter.blue.on("- " + result);
        } else if (RuleAspect.CREATOR.equals(edgeRole)) {
            result = Converter.green.on("+ " + result);
        } else if (RuleAspect.EMBARGO.equals(edgeRole)) {
            result = Converter.red.on("! " + result);
        } else if (RuleAspect.REMARK.equals(edgeRole)) {
            result = Converter.remark.on("// " + result);
        }
        return result;
    }

    /**
     * Returns the text of the label string, prefixed by the node type 
     * or flag aspect if the label is a node type or flag.
     */
    static public String toPrefixedString(Label label) {
        return getPrefix(label.getKind()) + label.text();
    }

    /** Creates a data type label for a given algebra. */
    public static TypeLabel createDataType(Algebra<?> algebra) {
        return createLabel(AlgebraRegister.getSignatureName(algebra), NODE_TYPE);
    }

    /**
     * Returns the textual prefix belonging to a given label kind.
     * This method is the inverse of {@link #getPrefixKind(String)}.
     * @return the label type corresponding to {@code labelType}, including the
     *         {@link #KIND_SEPARATOR} where necessary; {@code null} if {@code
     *         labelType} is not a valid label type.
     * @see #getPrefixKind(String)
     */
    public static String getPrefix(int labelKind) {
        switch (labelKind) {
        case NODE_TYPE:
            return NODE_TYPE_PREFIX + KIND_SEPARATOR;
        case FLAG:
            return FLAG_PREFIX + KIND_SEPARATOR;
        case BINARY:
            return "";
        default:
            return null;
        }
    }

    /** 
     * Extracts the label prefix from a given label text.
     * The prefix is either {@link #NODE_TYPE_PREFIX} or 
     * {@link #FLAG_PREFIX}, followed by {@link #KIND_SEPARATOR});
     * or {@code null} if there is no type prefix.
     * @see #getPrefix(int)
     */
    public static String getPrefix(String label) {
        if (label.startsWith(getPrefix(NODE_TYPE))) {
            return getPrefix(NODE_TYPE);
        } else if (label.startsWith(getPrefix(FLAG))) {
            return getPrefix(FLAG);
        } else {
            return null;
        }
    }

    /**
     * Determines the label type, based on a given prefix.
     * This method is the inverse of {@link #getPrefix(int)}.
     * @param prefix the tested prefix; should not include
     *        {@link #KIND_SEPARATOR}
     * @return one of {@link #BINARY}, {@link #NODE_TYPE} or {@link #FLAG}, or a
     *         negative number if {@code prefix} is not a known label type
     * @see #getKind()
     * @see #getPrefix(int)
     * @see #NODE_TYPE_PREFIX
     * @see #FLAG_PREFIX
     */
    static public int getPrefixKind(String prefix) {
        if (prefix.length() == 0) {
            return BINARY;
        } else if (prefix.equals(getPrefix(NODE_TYPE))) {
            return NODE_TYPE;
        } else if (prefix.equals(getPrefix(FLAG))) {
            return FLAG;
        } else {
            return -1;
        }
    }

    /**
     * Returns a descriptor for a given label kind.
     * A descriptor is a human-readable explanation of the label kind.
     */
    public static String getDescriptor(int labelKind) {
        return kindDescriptors[labelKind];
    }

    static private final TypeFactory factory = TypeFactory.instance();
    /** Separator between label kind prefix and label text. */
    static public final char KIND_SEPARATOR = ':';
    /** Prefix indicating that a label is a node type. */
    static public final String FLAG_PREFIX = "flag";
    /** Prefix indicating that a label is a flag. */
    static public final String NODE_TYPE_PREFIX = "type";
    /** Array of descriptions for the various label kinds. */
    static private final String[] kindDescriptors = new String[] {"Type",
        "Flag", "Binary"};

}