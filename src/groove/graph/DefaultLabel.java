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
     */
    private DefaultLabel(char index) {
        this.index = index;
    }

    public String text() {
        return getText(this.index);
    }

    // ------------------------- OBJECT OVERRIDES ---------------------

    /**
     * Overrides the method to use the text index.
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        // test that inequality of objects implies inequality of content
        assert result
            || !(obj instanceof DefaultLabel && text().equals(
                ((Label) obj).text()));
        return result;
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

    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label. The returned label is binary.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     */
    public static DefaultLabel createLabel(String text) {
        return getLabel(newLabelIndex(text));
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
    static private DefaultLabel getLabel(char index) {
        return labelList.get(index);
    }

    /**
     * Returns the index of a certain label text, if it is in the list. Returns
     * a special value if the text is not in the list.
     * @param text the label text being looked up
     * @return the index of <tt>text</tt>, if it is the list;
     *         <tt>Character.MAX_VALUE</tt> otherwise.
     */
    static private char labelIndex(String text) {
        Character index = getIndexMap().get(text);
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
     * @return a valid index for <tt>text</tt>
     * @require <tt>text != null</tt>
     * @ensure <tt>labelText(result).equals(text)</tt>
     */
    static private char newLabelIndex(String text) {
        Character index = getIndexMap().get(text);
        if (index == null) {
            char result = (char) textList.size();
            textList.add(text);
            labelList.add(new DefaultLabel(result));
            getIndexMap().put(text, Character.valueOf(result));
            return result;
        } else {
            return index.charValue();
        }
    }

    /**
     * Returns the appropriate index map for a given label kind.
     */
    static private Map<String,Character> getIndexMap() {
        return standardIndexMap;
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

    /** Counter to support the generation of fresh labels. */
    static private int freshLabelIndex;
}