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
    private NewAspect(AspectKind kind) {
        this.kind = kind;
        this.prototype = true;
        this.number = -1;
        this.text = null;
    }

    /** Creates a new aspect, wrapping either a number or a text. */
    public NewAspect(AspectKind kind, int nr, String text) {
        this.kind = kind;
        this.number = nr;
        this.text = text;
        this.prototype = false;
    }

    /**
     * Creates a new aspect, of the same kind of this one, but
     * wrapping a given number.
     * @throws UnsupportedOperationException if this aspect is itself already
     * instantiated, or if this aspect kind does not
     * allow instances with numbered content
     */
    public NewAspect newInstance(int nr) throws UnsupportedOperationException {
        if (!this.prototype) {
            throw new UnsupportedOperationException(
                "New aspects can only be created from prototypes");
        }
        checkNumberKind();
        return new NewAspect(this.kind, nr, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((this.kind == null) ? 0 : this.kind.hashCode());
        result = prime * result + this.number;
        result =
            prime * result + ((this.text == null) ? 0 : this.text.hashCode());
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
        if (this.kind != other.kind) {
            return false;
        }
        if (this.number != other.number) {
            return false;
        }
        if (this.text == null) {
            if (other.text != null) {
                return false;
            }
        } else if (!this.text.equals(other.text)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NewAspect [kind=" + this.kind + ", number=" + this.number
            + ", text=" + this.text + "]";
    }

    /**
     * Creates a new aspect, of the same kind of this one, but
     * wrapping a given text.
     * @throws UnsupportedOperationException if this aspect is itself already
     * instantiated, or if this aspect kind does not
     * allow instances with textual content
     */
    public NewAspect newInstance(String text)
        throws UnsupportedOperationException {
        if (!this.prototype) {
            throw new UnsupportedOperationException(
                "New aspects can only be created from prototypes");
        }
        checkTextKind();
        return new NewAspect(this.kind, -1, text);
    }

    /** Returns the aspect kind. */
    public AspectKind kind() {
        return this.kind;
    }

    /** 
     * Indicates if this aspect wraps a number. 
     * @throws UnsupportedOperationException if this aspect kind does not
     * allow instances with numbered content
     */
    public boolean hasNumber() throws UnsupportedOperationException {
        return number() >= 0;
    }

    /** 
     * Returns the number wrapped by this aspect, or {@code -1} if it does
     * not wrap a number. 
     * @throws UnsupportedOperationException if the aspect kind does not
     * allow instances with numbered content
     */
    public int number() throws UnsupportedOperationException {
        checkNumberKind();
        return this.number;
    }

    /** 
     * Indicates if this aspect wraps a text. 
     * @throws UnsupportedOperationException if this aspect kind does not
     * allow instances with textual content
     */
    public boolean hasText() throws UnsupportedOperationException {
        return text() != null;
    }

    /** 
     * Returns the text wrapped by this aspect, or {@code null} if it does
     * not wrap a text. 
     * @throws UnsupportedOperationException if the aspect kind does not
     * allow instances with textual content
     */
    public String text() throws UnsupportedOperationException {
        checkTextKind();
        return this.text;
    }

    /** Throws an exception if this aspect does not allow numbered instances. */
    private void checkNumberKind() throws UnsupportedOperationException {
        if (!(this.kind == AspectKind.ARGUMENT || this.kind == AspectKind.PARAMETER)) {
            throw new UnsupportedOperationException(
                "Numbered instances are not allowed for this aspect");
        }
    }

    /** Throws an exception if this aspect does not allow instances with text content. */
    private void checkTextKind() throws UnsupportedOperationException {
        if (!(this.kind.isRole() || this.kind.isQuantifier())) {
            throw new UnsupportedOperationException(
                "Named instances are not allowed for this aspect");
        }
    }

    /** Flag indicating that this aspect is a prototype. */
    private final boolean prototype;
    /** Aspect kind of this aspect. */
    private final AspectKind kind;
    /** Number wrapped in this aspect; {@code -1} if this is a prototype. */
    private final int number;
    /** Text wrapped in this aspect; {@code null} if this is a prototype. */
    private final String text;

    /** Returns the prototypical aspect for a given aspect name. */
    public static NewAspect getAspect(String name) {
        return aspectKindMap.get(name);
    }

    /** Returns the prototypical aspect for a given aspect kind. */
    public static NewAspect getAspect(AspectKind kind) {
        return aspectKindMap.get(kind);
    }

    /** Mapping from aspect kinds to canonical aspects (of that kind). */
    private final static Map<AspectKind,NewAspect> aspectKindMap =
        new HashMap<AspectKind,NewAspect>();
    /** Mapping from aspect names to canonical aspects (with that name). */
    private final static Map<String,NewAspect> aspectNameMap =
        new HashMap<String,NewAspect>();

    static {
        for (AspectKind kind : EnumSet.allOf(AspectKind.class)) {
            NewAspect aspect = new NewAspect(kind);
            aspectKindMap.put(kind, aspect);
            aspectNameMap.put(kind.getName(), aspect);
        }
    }
}
