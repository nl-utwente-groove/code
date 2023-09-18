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
 * $Id$
 */
package nl.utwente.groove.grammar.aspect;

import nl.utwente.groove.graph.Element;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Extension of the {@link Element} interface with support for {@link Aspect}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface AspectElement extends Element, Fixable {
    /** Returns the aspect graph to which this element belongs. */
    public AspectGraph getGraph();

    /**
     * Returns the main aspect of this element, if any.
     * At all times, the return value is guaranteed to be valid for the kind of graph.
     * When the graph is fixed, the return value is guaranteed to be non-{@code null}.
     */
    Aspect getAspect();

    /**
     * Tests if the element has a non-{@code null} main aspect.
     * @see #getAspect()
     */
    boolean hasAspect();

    /**
     * Returns the main aspect kind of this element, if any.
     * At all times, the return value is guaranteed to be valid for the kind of graph.
     * The return value is guaranteed to be non-{@code null}.
     * Convenience method for {@code getType().getKind()}.
     * @see #getAspect()
     */
    AspectKind getKind();

    /**
     * Indicates if this element has an attribute-related aspect.
     * @see #getAttrAspect()
     */
    boolean hasAttrAspect();

    /**
     * Returns the attribute-related aspect of this element, if any.
     */
    Aspect getAttrAspect();

    /**
     * Returns the kind of attribute-related aspect for this element, or {@link AspectKind#DEFAULT}.
     * The return value is guaranteed to be valid for the kind of graph,
     * and if not {@link AspectKind#DEFAULT}, to satisfy {@link AspectKind#isAttrKind()}
     * @see #getAttrAspect()
     */
    AspectKind getAttrKind();

    /**
     * Indicates if this element has format errors.
     * Convenience methods for {@code !getErrors().isEmpty()}
     * Should only be called after the element has been fixed.
     * @see #getErrors()
     */
    boolean hasErrors();

    /**
     * Returns the (non-{@code null}) list of format errors in this element.
     */
    FormatErrorSet getErrors();

    /**
     * Parses the syntactic information in this {@link AspectElement}.
     * @return {@code true} if the status of the element was changed by this call.
     */
    boolean setParsed();

    /** Returns {@code true} if the construction status of this {@link AspectElement}
     * is at least {@link Status#PARSED}
     */
    default boolean isParsed() {
        return getStatus() != Status.NEW;
    }

    /** Sets the typing of this {@link AspectElement}.
     * Calls {@link #setParsed()} first, if the element was not yet parsed.
     * @return {@code true} if the status of the element was changed by this call.
     */
    boolean setTyped();

    /** Returns {@code true} if the construction status of this {@link AspectElement}
     * is {@link Status#TYPED}.
     */
    default boolean isTyped() {
        return getStatus() == Status.TYPED;
    }

    @Override
    default boolean setFixed() {
        return setTyped();
    }

    @Override
    default boolean isFixed() {
        return isTyped();
    }

    /** Returns the construction status of this {@link AspectElement}. */
    Status getStatus();

    /** Construction status of an {@link AspectElement}. */
    static enum Status {
        /** Freshly constructed. */
        NEW,
        /** Aspects fixed. */
        PARSED,
        /** Expressions typed.
         * At this stage, the edge is fixed.
         */
        TYPED,;
    }
}
