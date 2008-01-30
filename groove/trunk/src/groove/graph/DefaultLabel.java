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
 * $Id: DefaultLabel.java,v 1.14 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a standard implementation of the Label interface.
 * An instance contains just an index into a global list.
 * @author Arend Rensink
 * @version $Revision: 1.14 $ 
 */
public final class DefaultLabel extends AbstractLabel {
    /**
     * Constructs a standard implementation of Label on the basis of a given text.
     * @param text the text of the label
     * @require <tt>text != null</tt>
     * @ensure <tt>text().equals(text)</tt>
     */
    private DefaultLabel(String text) {
        this.index = newLabelIndex(text);
        this.hashCode = computeHashCode();
    }
    
    /**
     * Constructs a standard implementation of Label on the basis of a given text index.
     * For internal purposes only.
     * @param index the index of the label text
     * @require <tt>labelText.containsKey(index)</tt>
     * @ensure <tt>this.index == index</tt>
     */
    private DefaultLabel(char index) {
        this.index = index;
        this.hashCode = computeHashCode();
    }

    public String text() {
        return getText(index);
    }

    // ------------------------- OBJECT OVERRIDES ---------------------

    /**
     * Overrides the method to use the text index.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DefaultLabel && ((DefaultLabel) obj).index == index); // || super.equals(obj);
    }

    /**
     * Uses the text index as hash code.
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /** Computes a hash code for this label. */
    private int computeHashCode() {
    	return text().hashCode() * (index + 1);
    }
    
    /**
     * Returns the index of this default label.
     * The inverse operation is {@link #getLabel(char)}; that is, 
     * <tt>getLabel(getIndex()).equals(this)</tt>.
     * @see #getLabel(char)
     */
    public char getIndex() {
        return index;
    }
    
    /**
     * Index of the text making up this label.
     * @invariant <tt>text != null</tt>
     */
    private final char index;
    /** The hash code of this label. */
    private final int hashCode;

    // /**
    // * Constructs a label from a given string.
    // * @param text the string to be parsed
    // * @require <tt>text != null</tt>
    // * @ensure <tt>result.text().equals(text)</tt>
    // * @throws FormatException if the quotes in <tt>text</tt> are not
    // * balanced properly, or an empty label ensues
    // */
    // public static Label parseLabel(String text) throws FormatException {
    // Label res;
    // if (text.indexOf('\"') == 0) {
    // if (text.indexOf('\"', 1) == text.length() - 1)
    // res = getLabel(newLabelIndex(text));
    // else
    // throw new FormatException("Improper quotes in label text: " + text);
    // } else if (text.length() == 0) {
    // throw new FormatException("Empty label text");
    // } else {
    // res = createLabel(text);
    // }
    // return res;
    // }
    // 
    
    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given string. The string is
     * used as-is, and is guaranteed to equal the text of the resulting label.
     * @param text the text of the label
     * @return an existing or new label with the given text
     */
    public static DefaultLabel createLabel(String text) {
        assert text != null : "Label text of default label should not be null";
        return getLabel(newLabelIndex(text));
    }

    /**
     * Generates a previously non-existent label. The label generated is of the form "L"+index,
     * where the index increases for every next fresh label.
     */
    public static DefaultLabel createFreshLabel() {
        freshLabelIndex++;
        String text;
        do {
            text = "L" + freshLabelIndex;
        } while (labelIndex(text) < Character.MAX_VALUE);
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
     * Returns the index of a certain label text, if it is in the list. Returns a special value if
     * the text is not in the list.
     * @param text the label text being looked up
     * @return the index of <tt>text</tt>, if it is the list; <tt>Character.MAX_VALUE</tt>
     *         otherwise.
     * @require <tt>text != null</tt>
     * @ensure <tt>result.equals(Character.MAX_VALUE) || labelText(result).equals(text)</tt>
     */
    static public char labelIndex(String text) {
        Character index = indexMap.get(text);
        if (index == null)
            return Character.MAX_VALUE;
        else
            return index.charValue();
    }

    /**
     * Returns an unmodifiable view upon the static index map of this class. Intended for
     * serialization only.
     */
    static public List<String> getTextList() {
        return Collections.unmodifiableList(textList);
    }

    /**
     * Modifies the static index map of this class. If the index map is not empty at the time of
     * invocation, an exception is thrown. Intended for serialization only.
     * @param textList modification to the index map
     * @throws IllegalStateException if the sttic index map is not empty at the time of invocation.
     */
    static public void putTextList(List<String> textList) {
        if (!DefaultLabel.textList.isEmpty()) {
            throw new IllegalStateException();
        }
        DefaultLabel.textList.addAll(textList);
        for (String text : textList) {
            indexMap.put(text, new Character((char) labelList.size()));
            labelList.add(new DefaultLabel(text));
        }
    }

    /**
     * Returns an index for a certain label text, creating a new entry if required..
     * @param text the label text being looked up
     * @return a valid index for <tt>text</tt>
     * @require <tt>text != null</tt>
     * @ensure <tt>labelText(result).equals(text)</tt>
     */
    static private char newLabelIndex(String text) {
        Character index = indexMap.get(text);
        if (index == null) {
            char result = (char) textList.size();
            textList.add(text);
            labelList.add(new DefaultLabel(result));
            indexMap.put(text, new Character(result));
            return result;
        } else
            return index.charValue();
    }

    /**
     * The internal translation table from label indices to strings.
     * @invariant <tt>textList: String^*</tt>
     */
    static private final List<String> textList = new ArrayList<String>();
    /**
     * The internal translation table from label indices to labels.
     * @invariant <tt>labelList: Label^*</tt> consistent with <tt>labelText</tt>
     */
    static private final List<DefaultLabel> labelList = new ArrayList<DefaultLabel>();
    /**
     * The internal translation table from strings to label indices.
     * @invariant <tt>indexMap: String -> Character</tt> 
     */
    static private final Map<String, Character> indexMap = new HashMap<String, Character>();

    /** Counter to support the generation of fresh labels. */
    static private int freshLabelIndex;
}