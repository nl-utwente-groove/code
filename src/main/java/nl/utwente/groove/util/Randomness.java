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
package nl.utwente.groove.util;

import java.util.Random;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Registry for seedable randomness: all intentional randomness in GROOVE
 * derives from a single <i>master seed</i>, so that one number reproduces an
 * entire run. Every consumer obtains a fresh generator for a named
 * {@link Purpose} via {@link #newRandom}; the generator's seed is derived by
 * mixing the purpose into the master seed, so consumers cannot perturb each
 * other through the order of their draws.
 * <p>
 * Since consumers obtain their generators at exploration preparation time and
 * the derivation is a pure function of the master seed, a fixed master seed
 * makes <i>every</i> exploration of the same grammar identical.
 * <p>
 * The master seed is resolved in order: an explicit {@link #setMasterSeed}
 * call (e.g., from the {@code -seed} Generator option) takes precedence; next
 * the system property {@value #SEED_PROPERTY}; otherwise a seed is freshly
 * generated and logged, so that any run remains reproducible after the fact.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public final class Randomness {
    private Randomness() {
        // no instances
    }

    /**
     * The purposes randomness is used for, each with its own derived stream.
     */
    public enum Purpose {
        /** Policy randomness: random choices in exploration strategies.
         * Affects which trace is sampled, never the semantics. */
        EXPLORATION,
        /** Semantic randomness: the random value oracle.
         * Affects the content of the GTS. */
        ORACLE;
    }

    /**
     * Returns a fresh random generator for a given purpose, seeded from the
     * master seed. Every call returns a generator drawing the same sequence
     * (for the same purpose and master seed), realising the per-exploration
     * reproducibility described in the class comment.
     */
    public static Random newRandom(Purpose purpose) {
        return new Random(deriveSeed(getMasterSeed(), purpose));
    }

    /**
     * Derives the seed for a given purpose by mixing the purpose name into
     * the master seed (with a splitmix64 finalisation, so that related master
     * seeds still give unrelated streams).
     */
    private static long deriveSeed(long masterSeed, Purpose purpose) {
        long result = masterSeed + purpose.name().hashCode() * GOLDEN_GAMMA;
        result = (result ^ (result >>> 30)) * 0xBF58476D1CE4E5B9L;
        result = (result ^ (result >>> 27)) * 0x94D049BB133111EBL;
        return result ^ (result >>> 31);
    }

    private static final long GOLDEN_GAMMA = 0x9E3779B97F4A7C15L;

    /**
     * Returns the master seed, resolving it on first use: an explicitly set
     * seed or the {@value #SEED_PROPERTY} system property if present,
     * otherwise a freshly generated seed, which is logged (an unrecorded
     * seed would make the run irreproducible).
     */
    public static synchronized long getMasterSeed() {
        Long result = masterSeed;
        if (result == null) {
            String property = System.getProperty(SEED_PROPERTY);
            if (property != null) {
                try {
                    result = Long.parseLong(property.trim());
                } catch (NumberFormatException exc) {
                    System.err
                        .printf("Ignoring unparseable random seed '%s' (in system property %s)%n",
                                property, SEED_PROPERTY);
                }
            }
            if (result == null) {
                result = new Random().nextLong();
                System.out
                    .printf("Using generated random seed %d (pass it via -D%s=... or the "
                        + "Generator -seed option to reproduce this run)%n", result, SEED_PROPERTY);
            }
            masterSeed = result;
        }
        return result;
    }

    /**
     * Explicitly sets the master seed, overriding the system property.
     * Intended for the CLI {@code -seed} option and for tests.
     */
    public static synchronized void setMasterSeed(long seed) {
        masterSeed = seed;
    }

    /** The lazily resolved master seed; {@code null} until first needed. */
    private static @Nullable Long masterSeed;

    /** Name of the system property holding the master seed. */
    public static final String SEED_PROPERTY = "groove.randomSeed";
}
