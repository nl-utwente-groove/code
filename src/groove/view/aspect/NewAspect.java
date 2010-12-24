/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
 * $Id$
 */
package groove.view.aspect;

import groove.view.FormatException;
import groove.view.aspect.AspectKind.ContentKind;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Parsed aspect, as used in an aspect graph to represent features
 * of the models. An aspect consists of an aspect kind and an optional
 * content field.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NewAspect {
    /** Creates a prototype (i.e., empty) aspect for a given aspect kind. */
    NewAspect(AspectKind kind, ContentKind contentKind) {
        this.aspectKind = kind;
        this.contentKind = contentKind;
        this.prototype = true;
        this.content = null;
    }

    /** Creates a new aspect, wrapping either a number or a text. */
    private NewAspect(AspectKind kind, ContentKind contentKind, Object content) {
        this.aspectKind = kind;
        this.contentKind = contentKind;
        this.content = content;
        this.prototype = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((this.aspectKind == null) ? 0 : this.aspectKind.hashCode());
        result =
            prime * result
                + ((this.content == null) ? 0 : this.content.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NewAspect other = (NewAspect) obj;
        if (this.aspectKind != other.aspectKind) {
            return false;
        }
        if (this.content == null) {
            if (other.content != null) {
                return false;
            }
        } else if (!this.content.equals(other.content)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.contentKind.toString(getKind(), getContent());
    }

    /**
     * Creates a new aspect, of the same kind of this one, but
     * wrapping content derived from given (non-{@code null} text.
     * @throws UnsupportedOperationException if this aspect is itself already
     * instantiated
     * @throws FormatException if the text cannot be correctly parsed as content
     * for this aspect
     */
    public NewAspect newInstance(String text) throws FormatException {
        if (!this.prototype) {
            throw new UnsupportedOperationException(
                "New aspects can only be created from prototypes");
        }
        return new NewAspect(this.aspectKind, this.contentKind,
            this.contentKind.parseContent(text));
    }

    /** Returns the aspect kind. */
    public AspectKind getKind() {
        return this.aspectKind;
    }

    /** 
     * Indicates if this aspect wraps a text. 
     */
    public boolean hasContent() throws UnsupportedOperationException {
        return this.content != null;
    }

    /** 
     * Returns the text wrapped by this aspect, or {@code null} if it does
     * not wrap a text. 
     */
    public Object getContent() throws UnsupportedOperationException {
        return this.content;
    }

    /** Flag indicating that this aspect is a prototype. */
    private final boolean prototype;
    /** Aspect kind of this aspect. */
    private final AspectKind aspectKind;
    /** Content kind of this aspect. */
    private final ContentKind contentKind;
    /** Content wrapped in this aspect; {@code null} if this is a prototype. */
    private final Object content;

    /** Returns the prototypical aspect for a given aspect name. */
    public static NewAspect getAspect(String name) {
        return aspectNameMap.get(name);
    }

    /** Mapping from aspect names to canonical aspects (with that name). */
    private final static Map<String,NewAspect> aspectNameMap =
        new HashMap<String,NewAspect>();

    static {
        for (AspectKind kind : EnumSet.allOf(AspectKind.class)) {
            aspectNameMap.put(kind.getName(), kind.getAspect());
        }
    }
}
