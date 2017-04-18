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

/** Oracle returning the default value for the appropriate type. */
public class DefaultValueOracle implements ValueOracle {
    /** Constructor for the singleton instance. */
    private DefaultValueOracle() {
        // empty
    }

    @Override
    public Constant getValue(HostGraph graph, RuleEvent event, RulePar par) {
        return par.getType()
            .getSort()
            .getDefaultValue();
    }

    @Override
    public Kind getKind() {
        return Kind.DEFAULT;
    }

    /** Returns the singleton instance of this class. */
    public final static DefaultValueOracle instance() {
        return instance;
    }

    private static final DefaultValueOracle instance = new DefaultValueOracle();
}
