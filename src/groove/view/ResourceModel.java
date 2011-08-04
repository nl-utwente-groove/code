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
 * $Id: View.java,v 1.4 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;

import groove.trans.ResourceKind;
import groove.util.Status;
import groove.view.aspect.AspectGraph;

import java.util.Collection;
import java.util.TreeSet;

/**
 * General interface for classes that provide part of a graph grammar. 
 * A resource model may still contain errors, which could prevent it from
 * being translated to an actual resource.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class ResourceModel<R> {
    /** Creates a named resource model of a given kind. */
    public ResourceModel(GrammarModel grammar, ResourceKind kind, String name) {
        this.grammar = grammar;
        this.kind = kind;
        this.name = name;
        this.lastModCount = -1;
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
     * Returns the (non-<code>null</code>) full name of the underlying model.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Returns the (non-<code>null</code>) last part of the name of the underlying model.
     * This equals the name if names cannot be hierarchical.
     * @see #getName()
     */
    public String getLastName() {
        return this.name;
    }

    /** 
     * Returns the source object for this resource.
     * This is the {@link String} or {@link AspectGraph} in the store
     * from which this model is derived.
     */
    abstract public Object getSource();

    /** 
     * Indicates if this resource is currently enabled for use in the grammar.
     * Enabledness means different things for different resource kinds.
     */
    abstract public boolean isEnabled();

    /**
     * Constructs the resource from the model. This can only be successful if there are no
     * syntax errors reported by {@link #getErrors()}.
     * @throws FormatException if there are syntax errors in the model that
     *         prevent it from being translated to a resource
     */
    public final R toResource() throws FormatException {
        synchronise();
        if (this.status == Status.ERROR) {
            throw new FormatException(getErrors());
        }
        return this.resource;
    }

    /**
     * Retrieves the list of syntax errors in this model. Conversion to a resource
     * can only be successful if this list is empty.
     * @return a non-<code>null</code>, possibly empty list of syntax errors
     * @see #toResource()
     */
    public final Collection<FormatError> getErrors() {
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
    final Collection<FormatError> createErrors() {
        return new TreeSet<FormatError>();
    }

    /** 
     * Synchronises the resource with the model source.
     * After invocation of this method, the status is either
     * {@link Status#DONE} or {@link Status#ERROR}.
     * @see #getStatus() 
     */
    final void synchronise() {
        testGrammarModified();
        if (this.status == Status.START) {
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
     * Tests if the grammar has been modified since last call of this method.
     * The method returns {@code true} on its first invocation.
     */
    private boolean testGrammarModified() {
        boolean result = false;
        if (getGrammar() != null) {
            int modCount = getGrammar().getModificationCount();
            result = (modCount != this.lastModCount);
            if (result) {
                this.lastModCount = modCount;
                this.status = Status.START;
                notifyGrammarModified();
            }
        }
        return result;
    }

    /**
     * Callback method invoked to signal that a grammar modification
     * has been detected. This allows subclasses to reset their internal
     * structures.
     */
    void notifyGrammarModified() {
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
    final R getResource() {
        synchronise();
        return this.resource;
    }

    /** 
     * Callback method that (re)computes the resource.
     * Called on initialisation and whenever the grammar model has changed.
     */
    abstract R compute() throws FormatException;

    /** The grammar model to which this resource belongs. */
    private final GrammarModel grammar;
    /** The kind of this resource. */
    private final ResourceKind kind;
    /** The name of this resource. */
    private final String name;
    /** Status of the construction of the resource. */
    private Status status = Status.START;
    /** The constructed resource, if {@link #status} equals {@link Status#DONE}. */
    private R resource;
    /** The errors found during resource construction. */
    private final Collection<FormatError> errors = createErrors();
    /** Grammar modification count at the last invocation of #isGrammarModified(). */
    private int lastModCount;
}
