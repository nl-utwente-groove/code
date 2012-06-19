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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a standard implementation of the {@link Label} interface. An
 * instance contains just an index into a global list.
 * @author Arend Rensink
 * @version $Revision$
 */
public final class DefaultLabel extends AbstractLabel {
    /**
     * Constructs a standard implementation of Label on the basis of a given
     * text index. For internal purposes only.
     * @param index the index of the label text
     * @param kind indicator of the type of label (normal, node type or flag).
     *        The value is respectively 0, {@link #NODE_TYPE_MASK} or
     *        {@link #FLAG_MASK}.
     */
    private DefaultLabel(char index, int kind) {
        this.index = index;
        this.hashCode = computeHashCode();
        this.kind = kind;
    }

    public String text() {
        return getText(this.index);
    }

    @Override
    public int getKind() {
        return this.kind;
    }

    // ------------------------- OBJECT OVERRIDES ---------------------

    /**
     * Overrides the method to use the text index.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Uses the text index as hash code.
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /** Computes a hash code for this label. */
    private int computeHashCode() {
        int result = text().hashCode() * (this.index + 1);
        int mask;
        switch (this.kind) {
        case NODE_TYPE:
            mask = NODE_TYPE_MASK;
            break;
        case FLAG:
            mask = FLAG_MASK;
            break;
        default:
            mask = 0;
        }
        return result ^ mask;
    }

    @Override
    public int compareTo(Label obj) {
        /* All node type labels are smaller than all others. */
        int result = boolToInt(obj.isNodeType()) - boolToInt(isNodeType());
        /* All flag labels are smaller than all standard labels. */
        if (result == 0) {
            result = boolToInt(obj.isFlag()) - boolToInt(isFlag());
        }
        if (result == 0) {
            result = super.compareTo(obj);
        }
        return result;
    }

    /**
     * Converts a boolean value to an integer value.
     * @return <code>1</code> if <code>bool</code> is <code>true</code>,
     *         <code>0</code> otherwise.
     */
    private int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    /**
     * Returns the index of this default label. The inverse operation is
     * {@link #getLabel(char)}; that is,
     * <tt>getLabel(getIndex()).equals(this)</tt>.
     * @see #getLabel(char)
     */
    public char getIndex() {
        return this.index;
    }

    /**
     * Index of the text making up this label.
     * @invariant <tt>text != null</tt>
     */
    private final char index;
    /** The hash code of this label. */
    private final int hashCode;
    /** The type of label (normal, node type or flag). */
    private final int kind;

    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label. The returned label is binary.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     */
    public static DefaultLabel createLabel(String text) {
        return createLabel(text, BINARY);
    }

    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given
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
    public static DefaultLabel createLabel(String text, int kind, boolean test)
        throws FormatException {
        if (test && kind != BINARY && !ExprParser.isIdentifier(text)) {
            throw new FormatException(
                "%s label '%s' is not a valid identifier",
                kindDescriptors[kind], text);
        }
        return createLabel(text, kind);
    }

    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given
     * string and label kind. The string is used as-is, and is guaranteed to
     * equal the text of the resulting label.
     * @param text the text of the label; non-null
     * @param kind kind of label to be created
     * @return an existing or new label with the given text and kind; non-null
     * @see #getKind()
     */
    public static DefaultLabel createLabel(String text, int kind) {
        assert text != null : "Label text of default label should not be null";
        return getLabel(newLabelIndex(text, kind));
    }

    /**
     * Returns a default or node type label, depending on the prefix in the
     * input string.
     * @param prefixedText text of the label, possibly prefixed with a type
     *        prefix {@link #NODE_TYPE_PREFIX} or {@link #FLAG_PREFIX}
     * @return a label with label type determined by the prefix
     */
    public static DefaultLabel createTypedLabel(String prefixedText) {
        int labelType = BINARY;
        if (prefixedText.startsWith(getPrefix(NODE_TYPE))) {
            labelType = NODE_TYPE;
        } else if (prefixedText.startsWith(getPrefix(FLAG))) {
            labelType = FLAG;
        }
        String actualText =
            prefixedText.substring(getPrefix(labelType).length());
        return createLabel(actualText, labelType);
    }

    /**
     * Generates a previously non-existent label. The label generated is of the
     * form "L"+index, where the index increases for every next fresh label.
     */
    public static DefaultLabel createFreshLabel() {
        String text;
        do {
            freshLabelIndex++;
            text = "L" + freshLabelIndex;
        } while (labelIndex(text, BINARY) < Character.MAX_VALUE);
        return createLabel(text);
    }

    /**
     * Yields the number of labels created in the course of the program.
     * @return Number of labels created
     */
    public static int getLabelCount() {
        return labelList.size();
    }

    /**
     * Returns the text at a certain index in the global label list.
     * @param index the index of the requested label text
     * @return the label text at <tt>textIndex</tt>
     * @require <tt>index</tt> is a valid label index
     */
    static public String getText(char index) {
        return textList.get(index);
    }

    /**
     * Returns the label at a certain index in the global label list.
     * @param index the index of the requested label text
     * @return the label at <tt>textIndex</tt>
     * @require <tt>index</tt> is a valid label index
     */
    static public DefaultLabel getLabel(char index) {
        return labelList.get(index);
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
    public static DefaultLabel createDataType(Algebra<?> algebra) {
        return createLabel(AlgebraRegister.getSignatureName(algebra), NODE_TYPE);
    }

    /**
     * Tests if a given label is a node type label with a name corresponding to
     * one of the predefined data type signatures.
     * @see AlgebraRegister#getSignatureNames()
     */
    static public boolean isDataType(Label label) {
        return label.isNodeType()
            && AlgebraRegister.getSignatureNames().contains(label.text());
    }

    /**
     * Returns the index of a certain label text, if it is in the list. Returns
     * a special value if the text is not in the list.
     * @param text the label text being looked up
     * @param kind the kind of label to be looked up or created
     * @return the index of <tt>text</tt>, if it is the list;
     *         <tt>Character.MAX_VALUE</tt> otherwise.
     */
    static private char labelIndex(String text, int kind) {
        Character index = getIndexMap(kind).get(text);
        if (index == null) {
            return Character.MAX_VALUE;
        } else {
            return index.charValue();
        }
    }

    /**
     * Returns an index for a certain label text, creating a new entry if
     * required.
     * @param text the label text being looked up
     * @param kind the kind of label to be looked up or created
     * @return a valid index for <tt>text</tt>
     * @require <tt>text != null</tt>
     * @ensure <tt>labelText(result).equals(text)</tt>
     */
    static private char newLabelIndex(String text, int kind) {
        Character index = getIndexMap(kind).get(text);
        if (index == null) {
            char result = (char) textList.size();
            textList.add(text);
            labelList.add(new DefaultLabel(result, kind));
            getIndexMap(kind).put(text, new Character(result));
            return result;
        } else {
            return index.charValue();
        }
    }

    /**
     * Returns the appropriate index map for a given label kind.
     */
    static private Map<String,Character> getIndexMap(int kind) {
        switch (kind) {
        case NODE_TYPE:
            return nodeTypeIndexMap;
        case FLAG:
            return flagIndexMap;
        default:
            return standardIndexMap;
        }
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

    /**
     * The internal translation table from label indices to strings.
     */
    static private final List<String> textList = new ArrayList<String>();
    /**
     * The internal translation table from label indices to labels.
     */
    static private final List<DefaultLabel> labelList =
        new ArrayList<DefaultLabel>();
    /**
     * The internal translation table from strings to standard (non-node type)
     * label indices.
     */
    static private final Map<String,Character> standardIndexMap =
        new HashMap<String,Character>();
    /**
     * The internal translation table from strings to node type label indices.
     */
    static private final Map<String,Character> nodeTypeIndexMap =
        new HashMap<String,Character>();
    /**
     * The internal translation table from strings to flag label indices.
     */
    static private final Map<String,Character> flagIndexMap =
        new HashMap<String,Character>();

    /** Counter to support the generation of fresh labels. */
    static private int freshLabelIndex;
    /** Mask to distinguish (the hash code of) node type labels. */
    static private final int NODE_TYPE_MASK = 0xAAAA;
    /** Mask to distinguish (the hash code of) flag labels. */
    static private final int FLAG_MASK = 0x5555;

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