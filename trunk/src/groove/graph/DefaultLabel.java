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
    DefaultLabel(String text, int index) {
        this.index = index;
        this.text = text;
    }

    public String text() {
        return this.text;
    }

    // ------------------------- OBJECT OVERRIDES ---------------------

    /* A LabelTree may mix labels of different kinds, therefore it is 
     * better for now to keep to the default notion of equality
    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        // test that inequality of objects implies inequality of content
        assert result == super.equals(obj) : String.format(
            "Distinct label objects of type %s and %s for label %s",
            this.getClass().getName(), obj.getClass().getName(), text());
        return result;
    }
    */

    /**
     * Returns the index of this default label.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Index of the text making up this label.
     * @invariant <tt>text != null</tt>
     */
    private final int index;
    /** The text of this label. */
    private final String text;

    /**
     * Returns the unique representative of a {@link DefaultLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label. The returned label is binary.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     */
    public static DefaultLabel createLabel(String text) {
        return factory.createLabel(text);
    }

    /**
     * Generates a previously non-existent label. The label generated is of the
     * form "L"+index, where the index increases for every next fresh label.
     */
    public static DefaultLabel createFreshLabel() {
        return factory.createFreshLabel();
    }

    /**
     * The internal translation table from label indices to strings.
     */
    static private final DefaultFactory factory = DefaultFactory.instance();
}