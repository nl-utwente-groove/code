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
package nl.utwente.groove.test.explore;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Randomness;

/**
 * Shared helpers for the exploration tests: loading the sample grammars and
 * exploring a fresh GTS with a parsed configuration under a fixed master
 * seed. Tests with additional needs (an exploration trace, seed variation)
 * build on {@link ExploreOutcome} or on {@link Randomness} directly.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public class ExploreTestSupport {
    private ExploreTestSupport() {
        // no instances
    }

    /** Location of the sample grammars used by the exploration tests. */
    public static final String INPUT_DIR = "junit/samples";

    /** Loads a sample grammar by name. */
    public static Grammar loadGrammar(String name) throws Exception {
        return Groove.loadGrammar(INPUT_DIR + "/" + name).toGrammar();
    }

    /**
     * Explores a fresh GTS over a given grammar with a given (parsed)
     * configuration, under a fixed master seed (relevant for the random
     * orders), and returns the played exploration.
     */
    public static Exploration explore(Grammar grammar, String config) throws Exception {
        Randomness.setMasterSeed(42);
        GTS gts = new GTS(grammar);
        return ExploreTypeConverter
            .toExploreType(ExploreConfig.parse(config))
            .newExploration(gts, null)
            .play();
    }
}
