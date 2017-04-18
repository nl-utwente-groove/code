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
package groove.match;

import groove.algebra.Constant;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.host.HostGraph;
import groove.transform.RuleEvent;
import groove.util.DocumentedEnum;
import groove.util.Exceptions;

/** Interface to provide values for unbound variable nodes during matching. */
public interface ValueOracle {
    /** Returns a value for a given variable node. */
    public Constant getValue(HostGraph graph, RuleEvent event, RulePar par);

    /** Returns the kind of this oracle. */
    public Kind getKind();

    /** Kind of oracle. */
    public static enum Kind implements DocumentedEnum {
        /** No oracle. */
        NONE,
        /** Default value oracle. */
        DEFAULT,
        /** Random value oracle. */
        RANDOM,
        /** User dialog input. */
        DIALOG,;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        @Override
        public String getExplanation() {
            switch (this) {
            case DEFAULT:
                return "Returns the default value of the type";
            case DIALOG:
                return "Asks the user to input a value of the type";
            case NONE:
                return "No oracle";
            case RANDOM:
                return "Returns a randum value of the type";
            default:
                throw Exceptions.UNREACHABLE;
            }
        }
    }
}
