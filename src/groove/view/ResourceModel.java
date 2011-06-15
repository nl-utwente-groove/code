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
import groove.view.aspect.AspectGraph;

import java.util.List;

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

    /**
     * Tests if the grammar has been modified since last call of this method.
     * The method returns {@code true} on its first invocation.
     */
    public final boolean isGrammarModified() {
        boolean result = false;
        if (getGrammar() != null) {
            int modCount = getGrammar().getModificationCount();
            result = (modCount != this.lastModCount);
            if (result) {
                this.lastModCount = modCount;
            }
        }
        return result;
    }

    /** Returns the kind of this resource model. */
    public final ResourceKind getKind() {
        return this.kind;
    }

    /**
     * Returns the (non-<code>null</code>) name of the underlying model.
     */
    public final String getName() {
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
    public abstract R toResource() throws FormatException;

    /**
     * Retrieves the list of syntax errors in this model. Conversion to a resource
     * can only be successful if this list is empty.
     * @return a non-<code>null</code>, possibly empty list of syntax errors
     * @see #toResource()
     */
    public abstract List<FormatError> getErrors();

    /** 
     * Indicates that there are errors in the model.
     * Convenience method for {@code !getErrors().isEmpty()}.
     */
    public final boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /** The grammar model to which this resource belongs. */
    private final GrammarModel grammar;
    /** The kind of this resource. */
    private final ResourceKind kind;
    /** The name of this resource. */
    private final String name;
    /** Grammar modification count at the last invocation of #isGrammarModified(). */
    private int lastModCount;
}
