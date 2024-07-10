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
package nl.utwente.groove.grammar.model;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.util.ChangeCount;
import nl.utwente.groove.util.ChangeCount.Tracker;
import nl.utwente.groove.util.Status;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * General interface for classes that provide part of a graph grammar.
 * A resource model may still contain errors, which could prevent it from
 * being translated to an actual resource.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class ResourceModel<R> {
    /**
     * Creates a named resource model of a given kind.
     * @param grammar the grammar to which this resource belongs; may be {@code null}
     * if the resource is being considered outside the context of a grammar
     * @param kind the kind of resource
     */
    public ResourceModel(GrammarModel grammar, ResourceKind kind) {
        this.grammar = grammar;
        this.kind = kind;
        this.resourceTrackers = new EnumMap<>(ResourceKind.class);
        this.dependencies = EnumSet.of(kind);
        Arrays.stream(ResourceKind.values()).forEach(this::addTracker);
    }

    /** Returns the grammar model to which this resource belongs. */
    public final GrammarModel getGrammar() {
        return this.grammar;
    }

    /** The grammar model to which this resource belongs. */
    private final GrammarModel grammar;

    /** Returns the kind of this resource model. */
    public final ResourceKind getKind() {
        return this.kind;
    }

    /** The kind of this resource. */
    private final ResourceKind kind;

    /** Registers dependencies of this model on (other) resource kinds.
     * This affects when the resource will be recomputed, viz. when
     * any of the registered dependencies is reported as stale by the corresponding
     * tracker of the grammar model.
     * The "own" resource kind is a dependency by default.
     * @see GrammarModel#createChangeTracker
     */
    protected void setDependencies(ResourceKind... kinds) {
        Arrays.stream(kinds).forEach(this.dependencies::add);
    }

    /**
     * Returns the source object for this resource.
     * This is the {@link String} or {@link AspectGraph} in the store
     * from which this model is derived.
     */
    abstract public Object getSource();

    /** Returns the name of this model. */
    abstract public String getName();

    /**
     * Constructs the resource from the model. This can only be successful if there are no
     * syntax errors reported by {@link #getErrors()}.
     * @throws FormatException if there are syntax errors in the model that
     *         prevent it from being translated to a resource
     */
    public final @NonNull R toResource() throws FormatException {
        synchronise();
        getErrors().throwException();
        assert this.resource != null; // guaranteed by the absence of errors
        return this.resource;
    }

    /**
     * Returns the constructed resource.
     * @return The constructed resource, or {@code null} if there were
     * errors.
     * @see #toResource()
     */
    final @Nullable R getResource() {
        synchronise();
        return this.resource;
    }

    /** The constructed resource, if {@link #status} equals {@link Status#DONE}. */
    private @Nullable R resource;

    /**
     * Synchronises the resource with the model source.
     * After invocation of this method, the status is either
     * {@link Status#DONE} (in which case the resource is built) or {@link Status#ERROR}.
     * @see #getStatus()
     */
    final void synchronise() {
        if (isShouldRebuild()) {// || this.resource == null && this.errors.isEmpty()) {
            if (DEBUG) {
                System.out.println("Building " + getKind() + " " + getName());
            }
            notifyWillRebuild();
            this.status = Status.START;
            this.errors.clear();
            try {
                checkSourceProperties();
                this.resource = compute();
                this.status = Status.DONE;
            } catch (FormatException e) {
                this.resource = null;
                this.errors.addAll(e.getErrors());
                this.status = Status.ERROR;
            }
        }
    }

    /**
     * Callback method to check the resource properties.
     * @throws FormatException if the source conflicts with its own declared properties.
     */
    void checkSourceProperties() throws FormatException {
        // empty
    }

    /**
     * Callback method that (re)computes the resource.
     * Called on initialisation and whenever the grammar model has changed.
     */
    abstract R compute() throws FormatException;

    /**
     * Tests if this resource model is stale w.r.t. the grammar
     * in any of a set of resource kind.
     */
    public final boolean isStale(ResourceKind... kinds) {
        boolean result = false;
        for (ResourceKind kind : kinds) {
            if (getTracker(kind).isStale()) {
                if (DEBUG) {
                    System.out
                        .println("" + getKind() + " " + getName() + " notices that " + kind
                            + " is stale");
                }
                result = true;
            }
        }
        return result;
    }

    /** Adds a tracker for a given resource kind. */
    private void addTracker(ResourceKind kind) {
        this.resourceTrackers
            .put(kind, getGrammar() == null
                ? ChangeCount.DUMMY_TRACKER
                : getGrammar().createChangeTracker(kind));
    }

    /** Returns the tracker of a given kind.
     * @throws IllegalArgumentException if the kind is not a registered dependency
     */
    private @NonNull Tracker getTracker(ResourceKind kind) {
        return this.resourceTrackers.get(kind);
    }

    /** Resource modification trackers. */
    private final Map<ResourceKind,Tracker> resourceTrackers;

    /**
     * Tests if the grammar has been modified to the degree
     * where the resource should be rebuilt.
     * This implementation tests the registered dependencies.
     */
    boolean isShouldRebuild() {
        return isStale(this.dependencies.toArray(new ResourceKind[0]));
    }

    private final Set<ResourceKind> dependencies;

    /**
     * Callback method invoked to signal that the resource is about
     * to be rebuilt, due to grammar modifications. This allows subclasses
     * to reset their internal structures.
     */
    void notifyWillRebuild() {
        // empty
    }

    /** Returns the status of the resource construction. */
    final Status getStatus() {
        return this.status;
    }

    /** Status of the construction of the resource. */
    private Status status = Status.START;

    /**
     * Retrieves the list of syntax errors in this model. Conversion to a resource
     * can only be successful if this list is empty.
     * @return a non-<code>null</code>, possibly empty list of syntax errors
     * @see #toResource()
     */
    public final FormatErrorSet getErrors() {
        synchronise();
        return this.errors;
    }

    /**
     * Indicates that there are errors in the model.
     * Convenience method for {@code !getErrors().isEmpty()}.
     */
    public final boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /** Callback factory method to create an appropriate error collection. */
    protected FormatErrorSet createErrors() {
        return new FormatErrorSet();
    }

    /** The errors found during resource construction. */
    private final FormatErrorSet errors = new FormatErrorSet();

    private static final boolean DEBUG = false;
}
