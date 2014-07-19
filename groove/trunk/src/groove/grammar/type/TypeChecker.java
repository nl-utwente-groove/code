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
package groove.grammar.type;

import groove.grammar.host.HostGraph;
import groove.util.parse.FormatErrorSet;

/**
 * Checker for type constraints that cannot be statically prevented.
 * @author rensink
 * @version $Revision $
 */
public interface TypeChecker {
    /** Returns the type graph specifying the constraints being checked. */
    TypeGraph getTypeGraph();

    /** Indicates whether this checker actually performs any test. */
    boolean isTrivial();

    /**
     * Checks a given host graph for violations, and returns the errors
     * found.
     */
    FormatErrorSet check(HostGraph graph);
}
