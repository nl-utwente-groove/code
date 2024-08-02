/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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

import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Extension of the {@link Element} interface with support for {@link Aspect}s.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface AspectElement extends Element, Fixable {
    /** Returns the aspect graph to which this element belongs. */
    public AspectGraph getGraph();

    /** If */
    default public AspectElement denormalise() {
        if (getGraph() instanceof NormalAspectGraph nag) {
            return nag.normalToSourceMap().get(this);
        } else {
            return this;
        }
    }

    /** Returns the graph role set for this aspect element.
     * Convenience method for {@code getGraph().getRole()}.
     * */
    default GraphRole getGraphRole() {
        return getGraph().getRole();
    }

    /** Checks if the graph role of this aspect element equals a given role.
     * Convenience method for {@code getGraphRole() == role}.
     */
    default boolean hasGraphRole(GraphRole role) {
        return getGraphRole() == role;
    }

    /** Returns the aspect map in this element. */
    public Aspect.Map getAspects();

    /** Tests if this element has an aspect of a given kind.
     * Convenience method for {@code get(kind.getCategory())}.
     */
    default public boolean has(AspectKind kind) {
        var aspect = get(kind.getCategory());
        return aspect != null && aspect.getKind() == kind;
    }

    /** Returns the aspect of a given kind, if any.
     * Convenience method for {@code get(kind.getCategory())}.
     */
    default public @Nullable Aspect get(AspectKind kind) {
        var aspect = get(kind.getCategory());
        return aspect == null || aspect.getKind() != kind
            ? null
            : aspect;
    }

    /** Tests if there is an aspect of a given kind whose content satisfies a given category.
     * @param kind the aspect kind for which we are looking for an aspect
     * @param pred the predicate to be tested for the content of that aspect kind
     * @return {@code true} if an aspect of {@code kind} exists whose content satisfies {@code pred}
     */
    default public boolean hasContent(AspectKind kind, Predicate<AspectContent> pred) {
        var content = getContent(kind);
        return content != null && pred.test(content);
    }

    /**
     * Returns the aspect content set for a given aspect kind, if any.
     * Convenience method for {@code getContent(kind.getCategory())}.
     */
    default public @Nullable AspectContent getContent(AspectKind kind) {
        return has(kind)
            ? getContent(kind.getCategory())
            : null;
    }

    /** Tests if this element has an aspect of a given category.
     * Convenience method for {@link Aspect.Map#containsKey(Object)} called on the result of {@link #getAspects()}
     */
    default public boolean has(AspectKind.Category cat) {
        return getAspects().containsKey(cat);
    }

    /** Tests if there is an aspect of a given category whose kind satisfies a given predicate.
     * @param cat the aspect category for which we are looking for an aspect
     * @param pred the predicate to be tested for the aspect kind
     * @return {@code true} if an aspect of {@code cat} exists whose kind satisfies {@code pred}
     */
    default public boolean has(AspectKind.Category cat, Predicate<AspectKind> pred) {
        var aspect = get(cat);
        return aspect != null && pred.test(aspect.getKind());
    }

    /** Returns the aspect of a given category, if any.
     * Convenience method for {@link Aspect.Map#get(Object)} called on the result of {@link #getAspects()}
     */
    default public @Nullable Aspect get(AspectKind.Category cat) {
        return getAspects().get(cat);
    }

    /** Applies a given function to the aspect of a given category, if there is any.
     * @param <T> the return type of the function
     * @param cat the aspect category for which we are looking for an aspect
     * @param func the function to be applied to the aspect
     * @return {@code func} applied to {@code get(cat)}, or {@code null} if {@code cat} has not been set
     */
    default public <T> @Nullable T get(AspectKind.Category cat, Function<Aspect,T> func) {
        var aspect = get(cat);
        return aspect == null
            ? null
            : func.apply(aspect);
    }

    /**
     * Returns the aspect kind set for a given category, if any.
     * Convenience method for {@code getAspects().get(cat).getKind()},
     * taking potential {@code null}-ness into account.
     * @return the aspect kind set for {@code cat}; or {@code null} if {@code cat} has not been set
     */
    default public @Nullable AspectKind getKind(AspectKind.Category cat) {
        return get(cat, a -> a.getKind());
    }

    /**
     * Returns the aspect content set for a given category, if any.
     * Convenience method for {@code getAspects().get(cat).getContent()},
     * taking potential {@code null}-ness into account.
     * @return the aspect content set for {@code cat}; or {@code null} if {@code cat} has not been set
     */
    default public @Nullable AspectContent getContent(AspectKind.Category cat) {
        return get(cat, Aspect::getContent);
    }

    /** Sets an aspect in this element. */
    public void set(Aspect aspect);

    /** Checks if this element has a sort aspect. */
    default boolean hasSort() {
        return has(Category.SORT);
    }

    /** Returns the sort of this element, if any. */
    default public @Nullable Sort getSort() {
        return get(Category.SORT, a -> a.getKind().getSort());
    }

    /**
     * Indicates if this element has format errors.
     * Convenience methods for {@code !getErrors().isEmpty()}
     * Should only be called after the element has been fixed.
     * @see #getErrors()
     */
    default boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /** Adds a set of format errors to the errors stored in this aspect element,
     * extending the context information with this element. */
    default void addErrors(FormatErrorSet errors) {
        errors.stream().forEach(this::addError);
    }

    /** Adds a single format error to the errors stored in this aspect element,
     * extending the context information with this element. */
    default void addError(String message, Object... pars) {
        addError(new FormatError(message, pars));
    }

    /** Adds a single format error to the errors stored in this aspect element,
     * extending the context information with this element. */
    default void addError(FormatError error) {
        getErrors().add(error.extend(this));
    }

    /**
     * Returns the (non-{@code null}) list of format errors in this element.
     */
    FormatErrorSet getErrors();

    /**
     * Parses the aspect information in this {@link AspectElement}.
     * For edges, the source and target nodes may be assumed to have been parsed already.
     * @return {@code true} if the status of the element was changed by this call.
     */
    default boolean setParsed() {
        boolean result = !hasAtLeast(Status.PARSING);
        if (result) {
            setStatus(Status.PARSING);
            if (!hasErrors()) {
                try {
                    parseAspects();
                } catch (FormatException exc) {
                    addErrors(exc.getErrors());
                }
            }
            setStatus(Status.PARSED);
        }
        return result;
    }

    /** Returns {@code true} if the construction status of this {@link AspectElement}
     * is at least {@link Status#PARSED}
     */
    default boolean isParsed() {
        return hasAtLeast(Status.PARSED);
    }

    /**
     * Parses the aspects of this element, and infers derived information.
     * Callback method from {@link #setParsed()}.
     * @throws FormatException if the aspect categories give rise to conflicts
     */
    void parseAspects() throws FormatException;

    /** Checks the aspects of this {@link AspectElement} through {@link #checkAspects()},
     * and changes the status accordingly.
     * Calls {@link #setParsed()} first, if the element was not yet parsed.
     * @return {@code true} if the status of the element was changed by this call.
     */
    default boolean setChecked() {
        boolean result = !hasAtLeast(Status.CHECKING);
        if (result) {
            setParsed();
            setStatus(Status.CHECKING);
            if (!hasErrors()) {
                try {
                    checkAspects();
                } catch (FormatException exc) {
                    addErrors(exc.getErrors());
                }
            }
            setStatus(Status.CHECKED);
            fixDataStructures();
        }
        return result;
    }

    /** Returns {@code true} if the construction status of this {@link AspectElement}
     * is {@link Status#CHECKED}.
     */
    default boolean isChecked() {
        return hasAtLeast(Status.CHECKED);
    }

    /**
     * Checks combinations of aspects for consistency.
     * Callback method from {@link #setChecked()}.
     * @throws FormatException if the aspect categories give rise to conflicts
     */
    void checkAspects() throws FormatException;

    /** Callback method invoked as the final stage of setFixed. */
    void fixDataStructures();

    @Override
    default boolean setFixed() {
        return setChecked();
    }

    @Override
    default boolean isFixed() {
        return isChecked();
    }

    /** Sets the construction status of this {@link AspectElement}. */
    void setStatus(Status status);

    /** Returns the construction status of this {@link AspectElement}. */
    Status getStatus();

    /** Tests whether the construction status equals a given value.
     * Convenience method for {@code getStatus() == status}.
     */
    default boolean has(Status status) {
        return getStatus() == status;
    }

    /** Tests whether the construction status is at least a given value.
     * Convenience method for {@code getStatus().compareTo(status) <= 0}.
     */
    default boolean hasAtLeast(Status status) {
        return getStatus().compareTo(status) >= 0;
    }

    /** Construction status of an {@link AspectElement}. */
    static enum Status {
        /** Freshly constructed. */
        NEW,
        /** Parsing aspects. */
        PARSING,
        /** Aspects parsed. */
        PARSED,
        /** Checking aspects. */
        CHECKING,
        /** Aspects checked.
         * At this stage, the element is fixed.
         */
        CHECKED,;
    }
}
