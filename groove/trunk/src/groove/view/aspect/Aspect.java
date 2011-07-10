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

import static groove.view.aspect.AspectKind.UNTYPED;
import groove.algebra.Constant;
import groove.algebra.SignatureKind;
import groove.graph.GraphRole;
import groove.graph.TypeLabel;
import groove.graph.algebra.VariableNode;
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
public class Aspect {
    /** Creates a prototype (i.e., empty) aspect for a given aspect kind. */
    Aspect(AspectKind kind, ContentKind contentKind) {
        this.aspectKind = kind;
        this.contentKind = contentKind;
        this.prototype = true;
        this.content = null;
    }

    /** Creates a new aspect, wrapping either a number or a text. */
    Aspect(AspectKind kind, ContentKind contentKind, Object content) {
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
        Aspect other = (Aspect) obj;
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
     * @param role intended graph role of the new aspect
     * @throws UnsupportedOperationException if this aspect is itself already
     * instantiated
     * @throws FormatException if the text cannot be correctly parsed as content
     * for this aspect
     */
    public Aspect newInstance(String text, GraphRole role)
        throws FormatException {
        if (!this.prototype) {
            throw new UnsupportedOperationException(
                "New aspects can only be created from prototypes");
        }
        return new Aspect(getKind(), this.contentKind,
            this.contentKind.parseContent(text, role));
    }

    /**
     * Returns an aspect obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public Aspect relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        Aspect result = this;
        if (hasContent()) {
            Object newContent =
                this.contentKind.relabel(getContent(), oldLabel, newLabel);
            if (newContent != getContent()) {
                result = new Aspect(getKind(), this.contentKind, newContent);
            }
        }
        return result;
    }

    /** Returns the aspect kind. */
    public AspectKind getKind() {
        return this.aspectKind;
    }

    /** 
     * Indicates if this aspect wraps a text. 
     */
    public boolean hasContent() {
        return this.content != null;
    }

    /** 
     * Returns the text wrapped by this aspect, or {@code null} if it does
     * not wrap a text. 
     */
    public Object getContent() {
        return this.content;
    }

    /** 
     * Returns a string description of the aspect content, 
     * or the empty string if the aspect has no content
     */
    public String getContentString() {
        return this.contentKind.toString(getContent());
    }

    /** 
     * Returns a variable node, with a given number,
     * derived from the content of this aspect.
     * Should only be called for node aspects of data kind.
     * @param nr the number of the node to be constructed
     * @return a variable node
     */
    public VariableNode getVariableNode(int nr) {
        VariableNode result = null;
        if (getKind() == UNTYPED) {
            result = new VariableNode(nr);
        } else if (getKind().isTypedData()) {
            if (hasContent()) {
                result = new VariableNode(nr, (Constant) getContent());
            } else {
                result =
                    new VariableNode(nr,
                        SignatureKind.getKind(getKind().getName()));
            }
        }
        return result;
    }

    /** Indicates that this aspect kind is allowed to appear on edges of a particular graph kind. */
    public boolean isForEdge(GraphRole role) {
        boolean result =
            AspectKind.allowedEdgeKinds.get(role).contains(getKind());
        if (result && getKind().isTypedData()) {
            result = !(getContent() instanceof Constant);
        }
        return result;
    }

    /** Indicates that this aspect kind is allowed to appear on nodes of a particular graph kind. */
    public boolean isForNode(GraphRole role) {
        boolean result =
            AspectKind.allowedNodeKinds.get(role).contains(getKind());
        if (result && getKind().isTypedData()) {
            if (hasContent()) {
                // data aspects with content not allowed in type graphs
                result =
                    getContent() instanceof Constant && role != GraphRole.TYPE;
            } else {
                // data aspects without content not allowed in host graphs
                result = role != GraphRole.HOST;
            }
        }
        return result;
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
    public static Aspect getAspect(String name) {
        return aspectNameMap.get(name);
    }

    /** Mapping from aspect names to canonical aspects (with that name). */
    private final static Map<String,Aspect> aspectNameMap =
        new HashMap<String,Aspect>();

    static {
        for (AspectKind kind : EnumSet.allOf(AspectKind.class)) {
            aspectNameMap.put(kind.getName(), kind.getAspect());
        }
    }
}
