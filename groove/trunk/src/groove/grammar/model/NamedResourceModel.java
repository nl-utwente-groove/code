/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.grammar.model;

import groove.grammar.QualName;

/**
 * Singular resource model, with a qualified name.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class NamedResourceModel<R> extends ResourceModel<R> {
    /**
     * Creates a named resource model of a given kind.
     * @param grammar the grammar to which this resource belongs; may be {@code null}
     * if the resource is being considered outside the context of a grammar
     * @param kind the kind of resource
     * @param name the name of the resource; must be unique for the resource kind
     */
    protected NamedResourceModel(GrammarModel grammar, ResourceKind kind, QualName name) {
        super(grammar, kind);
        this.name = name;
    }

    /** Returns the qualified name of the underlying model. */
    public final QualName getQualName() {
        return this.name;
    }

    /**
     * Returns the (non-<code>null</code>) last part of the name of the underlying model.
     * This equals the full name if that is not hierarchical.
     * @see #getQualName()
     */
    public String getLastName() {
        return getQualName().last();
    }

    /** The name of this resource. */
    private final QualName name;

    /**
     * Indicates if this resource is currently enabled for use in the grammar.
     * For non-composite resource models, this is the case if and only if
     * the name is active in the grammar.
     */
    public boolean isEnabled() {
        return getGrammar() == null || getGrammar().getActiveNames(getKind())
            .contains(getQualName());
    }
}
