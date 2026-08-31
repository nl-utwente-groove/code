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

import java.util.Random;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.algebra.BoolSignature;
import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.UnitPar.RulePar;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.Randomness.Purpose;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Oracle returning a random value for the appropriate type.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class RandomOracle implements ValueOracle {
    /** Constructor for an optionally seeded oracle. An unseeded oracle
     * draws from the master-seed registry ({@link Purpose#ORACLE}), so its
     * values remain reproducible via the master seed. The generator is
     * derived lazily, on the first value drawn: the oracle is created with
     * the GTS, before an exploration configuration gets the chance to set
     * the master seed (at realisation), and the late derivation lets the
     * configured seed govern the oracle stream as well. */
    RandomOracle(boolean hasSeed, long seed) {
        this.hasSeed = hasSeed;
        this.seed = seed;
        this.random = Factory.lazy(() -> hasSeed
            ? new Random(seed)
            : Randomness.newRandom(Purpose.ORACLE));
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
    private final Factory<Random> random;

    @Override
    public Constant getValue(HostGraph host, RuleEvent event, RulePar par) throws FormatException {
        Sort sort = par.getType().getSort();
        assert sort != null;
        Random random = this.random.get();
        Constant result;
        switch (sort) {
        case BOOL:
            result = random.nextBoolean()
                ? BoolSignature.TRUE
                : BoolSignature.FALSE;
            break;
        case INT:
            result = Constant.instance((random.nextInt()));
            break;
        case REAL:
            result = Constant.instance(random.nextDouble());
            break;
        case STRING:
            StringBuffer text = new StringBuffer();
            int length = random.nextInt(10);
            for (int i = 0; i < length; i++) {
                text.append((char) ('0' + random.nextInt(36)));
            }
            result = Constant.instance(text.toString());
            break;
        default:
            throw Exceptions.unreachable();
        }
        return result;
    }

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.RANDOM;
    }
}
