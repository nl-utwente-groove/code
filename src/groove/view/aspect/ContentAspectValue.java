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
 * $Id: ContentAspectValue.java,v 1.10 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import static groove.view.aspect.Aspect.CONTENT_ASSIGN;
import groove.view.FormatException;

/**
 * Specialisation of aspect values that have additional content. The class acts
 * as a factory for its own values (through {@link #newValue(String)}). The
 * content is converted to and from a string value by a parser passed in at
 * construction time.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class ContentAspectValue<C> extends AspectValue {
    /**
     * Creates a new aspect value factory, for a given aspect and with a given
     * name. Instances of the aspect value can be obtained by calling the
     * factory method {@link #newValue(String)}. Throws an exception if an
     * aspect value with the same name exists already.
     * @param aspect the aspect for which this is a value
     * @param name the name of the aspect value.
     * @throws groove.view.FormatException if the value name is already used
     */
    public ContentAspectValue(Aspect aspect, String name)
        throws FormatException {
        super(aspect, name);
        this.content = null;
    }

    /**
     * Constructs a specialisation of a given aspect value with a given content.
     * @param original the aspect value being copied
     * @param content the content of the specialised value
     */
    protected ContentAspectValue(AspectValue original, C content) {
        super(original);
        this.content = content;
    }

    /**
     * Returns the content of this aspect value.
     * @return the content, or <code>null</code> if this instance is to be
     *         used as a factory.
     */
    public final C getContent() {
        return this.content;
    }

    /** 
     * Returns a string description of this aspect value's content.
     * Returns <code>null</code> is the value has no content. 
     */
    public String getContentString() {
        C content = getContent();
        return content == null ? null : content.toString();
    }

    /**
     * Creates a new, specialised instance of this value with content parsed
     * from a given string value.
     * @throws FormatException if <code>value</code> is not correctly
     *         formatted.
     * @throws UnsupportedOperationException if this instance is not a factory.
     */
    abstract public ContentAspectValue<C> newValue(String value)
        throws FormatException;

    /**
     * Returns the name and optional content of the aspect.
     * @see #getName()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        String content = getContentString();
        if (content != null && content.length() != 0) {
            result.append(CONTENT_ASSIGN);
            result.append(content);
        }
        return result.toString();
    }

    /** The (further) content of this value. */
    private final C content;
}
