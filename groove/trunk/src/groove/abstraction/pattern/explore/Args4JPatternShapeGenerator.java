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

import groove.abstraction.neigh.explore.ShapeGenerator;
import groove.abstraction.pattern.PatternAbsParam;
import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PSTS;
import groove.abstraction.pattern.trans.PatternGraphGrammar;
import groove.explore.Args4JGenerator;

import java.io.PrintStream;
import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

/**
 * Counterpart of {@link Args4JGenerator} for pattern shape state space exploration.
 * See also {@link ShapeGenerator}.
 * 
 * @author Eduardo Zambon
 */
public final class Args4JPatternShapeGenerator extends
        Args4JPatternGraphGenerator {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public Args4JPatternShapeGenerator(String... args) throws CmdLineException {
        super("PatternShapeGenerator", args);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    /* Specialises the return type. */
    @Override
    public PSTS run() throws Exception {
        if (this.threeWay) {
            PatternAbsParam.getInstance().setUseThreeValues(true);
        }
        if (this.nodeMult > 0) {
            PatternAbsParam.getInstance().setNodeMultBound(this.nodeMult);
        }
        if (this.edgeMult > 0) {
            PatternAbsParam.getInstance().setNodeMultBound(this.edgeMult);
        }
        return (PSTS) super.run();
    }

    /** Writes an exploration prelude to stdout. */
    @Override
    protected void announce(PGTS pgts) {
        super.announce(pgts);
        if (!getVerbosity().isLow()) {
            PrintStream out = System.out;
            PatternAbsParam params = PatternAbsParam.getInstance();
            out.print("Node bound:\t" + params.getNodeMultBound()
                + "\tEdge bound:\t" + params.getEdgeMultBound());
            if (params.isUseThreeValues()) {
                out.println("\tLIMITING MULTIPLICITIES TO 0, 1 and 0+");
            } else {
                out.println();
            }
            out.println("Timestamp:\t" + new Date());
        }
    }

    /** Writes output accordingly to options given to the generator. */
    @Override
    protected void report(PGTS pgts) {
        PSTS psts = (PSTS) pgts;
        PrintStream out = System.out;
        out.println(String.format(
            "\nPSTS: States: %d -- %d subsumed (%d discarded) / Transitions: %d (%d subsumed)\n",
            psts.getStateCount(), psts.getSubsumedStatesCount(),
            psts.openStateCount(), psts.getTransitionCount(),
            psts.getSubsumedTransitionsCount()));
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    protected PGTS computeGTS(PatternGraphGrammar grammar) {
        return new PSTS(grammar);
    }

    @Option(name = "-n", metaVar = MultiplicityHandler.VAR,
            usage = MultiplicityHandler.NODE_USAGE,
            handler = MultiplicityHandler.class)
    private int nodeMult;
    @Option(name = "-m", metaVar = MultiplicityHandler.VAR,
            usage = MultiplicityHandler.EDGE_USAGE,
            handler = MultiplicityHandler.class)
    private int edgeMult;
    @Option(
            name = "-t",
            usage = "Limit the possible multiplicity values to three: 0, 1, or 0+.")
    private boolean threeWay;

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
        try {
            generate(args);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                e.printStackTrace();
            } else {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Loads a graph grammar, and returns the generated transition system.
     * @param args generator options, grammar and start graph name
     * @return the generated transition system
     * @throws Exception if any error occurred that prevented the GTS from being fully generated
     */
    static public PGTS generate(String[] args) throws Exception {
        staticPSTS = new Args4JPatternShapeGenerator(args).run();
        return staticPSTS;
    }

    /** Returns the most recently generated GTS. */
    static public PSTS getSGTS() {
        return staticPSTS;
    }

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static PSTS staticPSTS;

    /** Option handler for (positive) multiplicity values. */
    public static class MultiplicityHandler extends
            OneArgumentOptionHandler<Integer> {
        /**
         * Required constructor.
         */
        public MultiplicityHandler(CmdLineParser parser, OptionDef option,
                Setter<? super Integer> setter) {
            super(parser, option, setter);
        }

        @Override
        protected Integer parse(String argument) throws NumberFormatException,
            CmdLineException {
            int result = Integer.parseInt(argument);
            if (result < 1) {
                throw new NumberFormatException();
            }
            return result;
        }

        /** Meta-variable of the multiplicity option. */
        public static final String VAR = "val";
        /** Usage message for node multiplicity. */
        public static final String NODE_USAGE =
            "Set the node multiplicity bound to the given value."
                + "Argument <val> must be greater than zero (default value is 1).";
        /** Usage message for edge multiplicity. */
        public static final String EDGE_USAGE =
            "Set the edge multiplicity bound to the given value."
                + "Argument <val> must be greater than zero (default value is 1).";
    }

}
