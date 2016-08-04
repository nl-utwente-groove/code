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
package groove.grammar.model;

import static groove.grammar.model.ResourceKind.RULE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import groove.grammar.Rule;
import groove.grammar.aspect.AspectGraph;
import groove.graph.GraphInfo;
import groove.util.ChangeCount;
import groove.util.ChangeCount.Tracker;
import groove.util.Status;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

/**
 * General interface for classes that provide part of a graph grammar.
 * A resource model may still contain errors, which could prevent it from
 * being translated to an actual resource.
 * @author Arend Rensink
 * @version $Revision $
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
        this.grammarTracker = grammar == null ? null : grammar.createChangeTracker();
        this.resourceTrackers = new EnumMap<>(ResourceKind.class);
        for (ResourceKind rk : ResourceKind.values()) {
            this.resourceTrackers.put(rk,
                grammar == null ? ChangeCount.DUMMY_TRACKER : grammar.createChangeTracker(rk));
        }
    }

    /** Returns the grammar model to which this resource belongs. */
    public final GrammarModel getGrammar() {
        return this.grammar;
    }

    /** Returns the kind of this resource model. */
    public final ResourceKind getKind() {
        return this.kind;
    }

    /**
     * Tests if this resource model is stale w.r.t. the grammar
     * in any of a set of resource kind.
     */
    public final boolean isStale(ResourceKind... kinds) {
        boolean result = false;
        for (ResourceKind kind : kinds) {
            result |= this.resourceTrackers.get(kind)
                .isStale();
        }
        return result;
    }

    /**
     * Returns the source object for this resource.
     * This is the {@link String} or {@link AspectGraph} in the store
     * from which this model is derived.
     */
    abstract public Object getSource();

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
    final FormatErrorSet createErrors() {
        return new FormatErrorSet();
    }

    /**
     * Synchronises the resource with the model source.
     * After invocation of this method, the status is either
     * {@link Status#DONE} (in which case the resource is built) or {@link Status#ERROR}.
     * @see #getStatus()
     */
    final void synchronise() {
        if (isShouldRebuild()) {// || this.resource == null && this.errors.isEmpty()) {
            notifyWillRebuild();
            this.status = Status.START;
            this.errors.clear();
            try {
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
     * Tests if the grammar has been modified to the degree
     * where the resource should be rebuilt.
     * The method returns {@code true} on its first invocation.
     */
    boolean isShouldRebuild() {
        boolean result = false;
        if (getGrammar() != null) {
            result = this.grammarTracker.isStale();
        }
        return result;
    }

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

    /**
     * Callback method that (re)computes the resource.
     * Called on initialisation and whenever the grammar model has changed.
     */
    abstract R compute() throws FormatException;

    /** Returns the set of error-free, enabled rules. */
    Collection<Rule> getRules() {
        Collection<NamedResourceModel<?>> ruleModels = getGrammar().getResourceSet(RULE);
        Collection<Rule> result = new ArrayList<>(ruleModels.size());
        // set rules
        for (ResourceModel<?> model : ruleModels) {
            RuleModel ruleModel = (RuleModel) model;
            try {
                if (GraphInfo.isEnabled(ruleModel.getSource())) {
                    result.add(ruleModel.toResource());
                }
            } catch (FormatException exc) {
                // do not add this rule
            }
        }
        return result;
    }

    /** The grammar model to which this resource belongs. */
    private final GrammarModel grammar;
    /** The kind of this resource. */
    private final ResourceKind kind;
    /** Status of the construction of the resource. */
    private Status status = Status.START;
    /** The constructed resource, if {@link #status} equals {@link Status#DONE}. */
    private @Nullable R resource;
    /** The errors found during resource construction. */
    private final FormatErrorSet errors = new FormatErrorSet();
    /** Grammar modification tracker. */
    private final Tracker grammarTracker;
    /** Resource modification trackers. */
    private final Map<ResourceKind,Tracker> resourceTrackers;
}
