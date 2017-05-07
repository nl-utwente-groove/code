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
 * $Id: RandomValueOracle.java 5908 2017-04-22 16:34:31Z rensink $
 */
package groove.transform.oracle;

import groove.grammar.GrammarProperties;

/**
 * Factory for a {@link RandomOracle}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RandomOracleFactory implements ValueOracleFactory {
    /** Constructor for the singleton instance. */
    private RandomOracleFactory() {
        this.seed = 0;
        this.hasSeed = false;
    }

    /** Constructor for a seeded instance. */
    private RandomOracleFactory(long seed) {
        this.seed = seed;
        this.hasSeed = true;
    }

    @Override
    public RandomOracle instance(GrammarProperties properties) {
        return new RandomOracle(hasSeed(), getSeed());
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

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.RANDOM;
    }

    /** Returns the a seeded instance of this class. */
    public final static RandomOracleFactory instance(long seed) {
        return new RandomOracleFactory(seed);
    }

    /** Returns the singleton instance of this class. */
    public final static RandomOracleFactory instance() {
        return instance;
    }

    private static final RandomOracleFactory instance = new RandomOracleFactory();
}
