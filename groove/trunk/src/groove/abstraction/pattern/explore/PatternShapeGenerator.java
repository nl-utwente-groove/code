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
package groove.abstraction.pattern.explore;

import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PSTS;
import groove.explore.Generator;

import java.io.PrintStream;

/**
 * Counterpart of {@link Generator} for pattern shape state space exploration.
 * 
 * @author Eduardo Zambon
 */
public final class PatternShapeGenerator extends PatternGraphGenerator {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Usage message for the generator. */
    private static final String USAGE_MESSAGE =
        "Usage: PatternShapeGenerator [options] <grammar> <start-graph-name> <type-graph-name>";

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public PatternShapeGenerator(String... args) {
        super(args);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    /**
     * This implementation returns <tt>{@link #USAGE_MESSAGE}</tt>.
     */
    @Override
    protected String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    /**
     * Returns the GTS that is being generated. The GTS is lazily obtained from
     * the grammar if it had not yet been initialised.
     * @see #getGrammar()
     */
    @Override
    public PGTS getPGTS() {
        if (pgts == null) {
            pgts = new PSTS(getGrammar());
        }
        return pgts;
    }

    /** Writes output accordingly to options given to the generator. */
    @Override
    public void report() {
        PrintStream out = System.out;
        out.println(String.format("\nPSTS: states = %s / transitions = %s",
            getPGTS().getStateCount(), getPGTS().getTransitionCount()));
    }

    // ------------------------------------------------------------------------
    // Main method
    // ------------------------------------------------------------------------

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * @param args generator options, grammar and start graph name
     */
    public static void main(String[] args) {
        new PatternShapeGenerator(args).start();
    }

}
