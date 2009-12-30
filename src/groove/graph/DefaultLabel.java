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

import groove.util.Converter;
import groove.view.aspect.NodeTypeAspect;

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
     * text. The constructed label is not a node type.
     * @param text the text of the label
     * @require <tt>text != null</tt>
     * @ensure <tt>text().equals(text)</tt>
     * @deprecated used only for the groove.util.Analyzer command line tool,
     *             which is no longer maintained
     */
    @Deprecated
    private DefaultLabel(String text) {
        this.index = newLabelIndex(text, false);
        this.hashCode = computeHashCode();
        this.nodeType = false;
    }

    /**
     * Constructs a standard implementation of Label on the basis of a given
     * text index. For internal purposes only.
     * @param index the index of the label text
     * @param nodeType flag indicating if this label stands for a node type
     */
    private DefaultLabel(char index, boolean nodeType) {
        this.index = index;
        this.hashCode = computeHashCode();
        this.nodeType = nodeType;
    }

    public String text() {
        return getText(this.index);
    }

    @Override
    public boolean isNodeType() {
        return this.nodeType;
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
        return isNodeType() ? result ^ NODE_TYPE_MASK : result;
    }

    /** All node type labels are smaller than all standard labels. */
    @Override
    public int compareTo(Label obj) {
        int result = boolToInt(obj.isNodeType()) - boolToInt(isNodeType());
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
    /** Flag indicating if this label stands for a node type. */
    private final boolean nodeType;

    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label. The returned label is not a node type.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     * @see #createLabel(String, boolean)
     */
    public static DefaultLabel createLabel(String text) {
        assert text != null : "Label text of default label should not be null";
        return getLabel(newLabelIndex(text, false));
    }

    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label.
     * @param text the text of the label; non-null
     * @param nodeType flag indicating if the label stands for a node type
     * @return an existing or new label with the given text and node type
     *         property; non-null
     */
    public static DefaultLabel createLabel(String text, boolean nodeType) {
        assert text != null : "Label text of default label should not be null";
        return getLabel(newLabelIndex(text, nodeType));
    }

    /**
     * Returns a default or node type label, depending on the prefix in the
     * input string.
     * @param prefixedText text of the label, possibly prefixed with
     *        {@link NodeTypeAspect#NODE_TYPE_NAME}.
     * @return a label without the prefix, which is a node type if the prefix is
     *         there.
     */
    public static DefaultLabel createTypedLabel(String prefixedText) {
        String typePrefix = NodeTypeAspect.NODE_TYPE.getPrefix();
        boolean isType = prefixedText.startsWith(typePrefix);
        String actualText =
            isType ? prefixedText.substring(typePrefix.length()) : prefixedText;
        return createLabel(actualText, isType);
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
        } while (labelIndex(text, false) < Character.MAX_VALUE);
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
     * type.
     */
    static public String toHtmlString(Label label) {
        if (label.isNodeType()) {
            return Converter.STRONG_TAG.on(label.text());
        } else {
            return label.text();
        }
    }

    /**
     * Returns the text of the label string, prefixed by the node type aspect if
     * the label is a node type.
     */
    static public String toTypedString(Label label) {
        String prefix =
            label.isNodeType() ? NodeTypeAspect.NODE_TYPE.getPrefix() : "";
        return prefix + label.text();
    }

    /**
     * Returns the index of a certain label text, if it is in the list. Returns
     * a special value if the text is not in the list.
     * @param text the label text being looked up
     * @param nodeType flag indicating if the result should be node type label
     * @return the index of <tt>text</tt>, if it is the list;
     *         <tt>Character.MAX_VALUE</tt> otherwise.
     */
    static private char labelIndex(String text, boolean nodeType) {
        Character index = getIndexMap(nodeType).get(text);
        if (index == null) {
            return Character.MAX_VALUE;
        } else {
            return index.charValue();
        }
    }

    /**
     * Modifies the static index map of this class. If the index map is not
     * empty at the time of invocation, an exception is thrown. Intended for
     * serialisation only.
     * @param textList modification to the index map
     * @throws IllegalStateException if the static index map is not empty at the
     *         time of invocation.
     * @deprecated used only for the groove.util.Analyzer command line tool,
     *             which is no longer maintained
     */
    @Deprecated
    static public void putTextList(List<String> textList) {
        if (!DefaultLabel.textList.isEmpty()) {
            throw new IllegalStateException();
        }
        DefaultLabel.textList.addAll(textList);
        for (String text : textList) {
            standardIndexMap.put(text, new Character((char) labelList.size()));
            labelList.add(new DefaultLabel(text));
        }
    }

    /**
     * Returns an index for a certain label text, creating a new entry if
     * required..
     * @param text the label text being looked up
     * @param nodeType flag indicating that the label will be used for node
     *        types
     * @return a valid index for <tt>text</tt>
     * @require <tt>text != null</tt>
     * @ensure <tt>labelText(result).equals(text)</tt>
     */
    static private char newLabelIndex(String text, boolean nodeType) {
        Character index = getIndexMap(nodeType).get(text);
        if (index == null) {
            char result = (char) textList.size();
            textList.add(text);
            labelList.add(new DefaultLabel(result, nodeType));
            getIndexMap(nodeType).put(text, new Character(result));
            return result;
        } else {
            return index.charValue();
        }
    }

    /**
     * Returns the appropriate index map, taking the node type property into
     * account.
     * @param nodeType if <code>true</code>, the node type map is returned.
     */
    static private Map<String,Character> getIndexMap(boolean nodeType) {
        return nodeType ? nodeTypeIndexMap : standardIndexMap;
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

    /** Counter to support the generation of fresh labels. */
    static private int freshLabelIndex;
    /** Mask to distinguish the hash code of node type labels. */
    static private final int NODE_TYPE_MASK = 0xAAAA;
}