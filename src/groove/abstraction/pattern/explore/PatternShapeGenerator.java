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

import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.neigh.explore.ShapeGenerator;
import groove.abstraction.pattern.PatternAbsParam;
import groove.abstraction.pattern.PatternAbstraction;
import groove.abstraction.pattern.lts.PSTS;
import groove.explore.Args4JGenerator;
import groove.util.cli.CommandLineOption;

import java.io.PrintStream;

/**
 * Counterpart of {@link Args4JGenerator} for pattern shape state space exploration.
 * See also {@link ShapeGenerator}.
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
        addOption(new MultiplicityBoundOption(MultKind.NODE_MULT));
        addOption(new MultiplicityBoundOption(MultKind.EDGE_MULT));
        addOption(new ThreeMultValOption());
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
    public PSTS getPGTS() {
        if (pgts == null) {
            pgts = new PSTS(getGrammar());
        }
        return (PSTS) pgts;
    }

    /** Resets the generator. */
    @Override
    protected void reset() {
        PatternAbstraction.initialise();
        pgts = null;
    }

    /** Writes an exploration prelude to stdout. */
    @Override
    protected void prelude() {
        if (!getVerbosity().isLow()) {
            println("\n======================================================\n");
            println("Grammar:\t" + this.grammarLocation);
            println("Start graph:\t"
                + (this.startGraphName == null ? "default"
                        : this.startGraphName));
            println("Type graph:\t" + this.typeGraphName);
            PatternAbsParam params = PatternAbsParam.getInstance();
            print("Node bound:\t" + params.getNodeMultBound()
                + "\tEdge bound:\t" + params.getEdgeMultBound());
            if (params.isUseThreeValues()) {
                println("\tLIMITING MULTIPLICITIES TO 0, 1 and 0+");
            } else {
                println();
            }
            println("Timestamp:\t" + this.invocationTime);
            print("\nProgress:\n\n");
            addProgressMonitor();
        }
    }

    /** Writes output accordingly to options given to the generator. */
    @Override
    public void report() {
        PrintStream out = System.out;
        PSTS psts = getPGTS();
        out.println(String.format(
            "\nPSTS: States: %d -- %d subsumed (%d discarded) / Transitions: %d (%d subsumed)\n",
            psts.getStateCount(), psts.getSubsumedStatesCount(),
            psts.openStateCount(), psts.getTransitionCount(),
            psts.getSubsumedTransitionsCount()));
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

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /**
     * Command line option to specify the use of three values of multiplicity
     * only.
     * 
     * @author Eduardo Zambon
     */
    private class ThreeMultValOption implements CommandLineOption {

        @Override
        public String[] getDescription() {
            return new String[] {"Limit the possible multiplicity values to three: 0, 1, or 0+."};
        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public String getName() {
            return "t";
        }

        @Override
        public boolean hasParameter() {
            return false;
        }

        @Override
        public void parse(String parameter) {
            PatternAbsParam.getInstance().setUseThreeValues(true);
        }

    }

    /**
     * Command line option to specify a multiplicity bound.
     * 
     * @author Eduardo Zambon
     */
    private static class MultiplicityBoundOption implements CommandLineOption {

        final MultKind kind;

        MultiplicityBoundOption(MultKind kind) {
            this.kind = kind;
        }

        @Override
        public String getName() {
            String name = null;
            switch (this.kind) {
            case NODE_MULT:
                name = "n";
                break;
            case EDGE_MULT:
                name = "m";
                break;
            default:
                assert false;
            }
            return name;
        }

        @Override
        public String[] getDescription() {
            String type = null;
            switch (this.kind) {
            case NODE_MULT:
                type = "node";
                break;
            case EDGE_MULT:
                type = "edge";
                break;
            default:
                assert false;
            }
            return new String[] {
                "Set the " + type + " multiplicity bound to "
                    + "the given value.",
                "Argument '" + getParameterName()
                    + "' must be greater than zero (default value is 1)."};
        }

        @Override
        public String getParameterName() {
            return "val";
        }

        @Override
        public boolean hasParameter() {
            return true;
        }

        @Override
        public void parse(String parameter) throws IllegalArgumentException {
            int bound = 0;
            try {
                bound = Integer.parseInt(parameter);
            } catch (NumberFormatException exc) {
                throw new IllegalArgumentException("verbosity value '"
                    + parameter + "' must be numeric");
            }
            if (bound < 1) {
                throw new IllegalArgumentException("'" + parameter
                    + "' bound must be >= 1.");
            }
            switch (this.kind) {
            case NODE_MULT:
                PatternAbsParam.getInstance().setNodeMultBound(bound);
                break;
            case EDGE_MULT:
                PatternAbsParam.getInstance().setEdgeMultBound(bound);
                break;
            default:
                assert false;
            }
        }
    }

}
