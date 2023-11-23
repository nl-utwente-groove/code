/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;

/**
 * Interface for a source from which a {@link GrammarModel} and eventually a
 * {@link Grammar} can be built.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface GrammarSource {
    /**
     * Returns the name of this source.
     * @return the name of this store; cannot be <code>null</code> or empty.
     */
    public String getName();

    /** Checks if the store is empty. */
    public boolean isEmpty();

    /**
     * Immutable view on the name-to-aspect graph map of a given graph-based resource kind.
     * @param kind the kind of resource for which the map is requested
     */
    public Map<QualName,@Nullable AspectGraph> getGraphs(ResourceKind kind);

    /**
     * Immutable view on the name-to-text map of a given text-based resource kind.
     * @param kind the kind of resource for which the map is requested
     */
    public Map<QualName,@Nullable String> getTexts(ResourceKind kind);

    /** The system properties object in the store (non-null). */
    public GrammarProperties getProperties();
}
