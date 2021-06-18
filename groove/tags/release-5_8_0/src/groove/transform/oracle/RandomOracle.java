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
package groove.transform.oracle;

import java.util.Random;

import org.eclipse.jdt.annotation.NonNullByDefault;

import groove.algebra.BoolSignature;
import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.host.HostGraph;
import groove.transform.RuleEvent;
import groove.util.Exceptions;
import groove.util.parse.FormatException;

/**
 * Oracle returning a random value for the appropriate type.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class RandomOracle implements ValueOracle {
    /** Constructor for an optionally seeded oracle. */
    RandomOracle(boolean hasSeed, long seed) {
        this.hasSeed = hasSeed;
        this.seed = seed;
        this.random = hasSeed ? new Random(seed) : new Random();
    }

    /** Indicates if this random value oracle is seeded. */
    public boolean hasSeed() {
        return this.hasSeed;
    }

    /** Returns the seed of this random value oracle, or {@code 0} if
     * the oracle is not seeded. */
    public long getSeed() {
        return this.seed;
    }

    private final boolean hasSeed;
    private final long seed;
    private final Random random;

    @Override
    public Constant getValue(HostGraph host, RuleEvent event, RulePar par) throws FormatException {
        Sort sort = par.getType()
            .getSort();
        Constant result;
        switch (sort) {
        case BOOL:
            result = this.random.nextBoolean() ? BoolSignature.TRUE : BoolSignature.FALSE;
            break;
        case INT:
            result = Constant.instance((this.random.nextInt()));
            break;
        case REAL:
            result = Constant.instance(this.random.nextDouble());
            break;
        case STRING:
            StringBuffer text = new StringBuffer();
            int length = this.random.nextInt(10);
            for (int i = 0; i < length; i++) {
                text.append((char) ('0' + this.random.nextInt(36)));
            }
            result = Constant.instance(text.toString());
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.RANDOM;
    }
}
