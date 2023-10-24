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
package nl.utwente.groove.transform.oracle;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.grammar.UnitPar.RulePar;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Interface to provide values for unbound variable nodes during matching.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public interface ValueOracle {
    /** Returns a value for a given variable node.
     * @throws FormatException if no correct value was provided
     */
    public Constant getValue(HostGraph host, RuleEvent event, RulePar par) throws FormatException;

    /** Closes all resources associated with this oracle. */
    public default void close() {
        // does nothing
    }

    /** Returns the kind of this oracle. */
    public ValueOracleKind getKind();
}
