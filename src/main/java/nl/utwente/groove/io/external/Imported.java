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
package nl.utwente.groove.io.external;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;

/**
 * An imported grammar resource: either a graph or text (not both).
 * Simply union for both types.
 * @author Harold Bruintjes
 * @version $Revision$
 */
public record Imported(QualName qualName, ResourceKind kind, AspectGraph graph, String text) {
    /** Constructs a graph-based resource. */
    public Imported(ResourceKind kind, AspectGraph resource) {
        this(resource.getQualName(), kind, resource, null);
    }

    /** Constructs a text-based resource. */
    public Imported(ResourceKind kind, QualName name, String resource) {
        this(name, kind, null, resource);
    }

    /** Indicates if this is a graph-based resource. */
    public boolean isGraph() {
        return graph() != null;
    }
}