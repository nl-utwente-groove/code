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
import groove.abstraction.pattern.explore.util.PatternShapeMatchApplier.ApplicationMethod;
import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PSTS;
import groove.explore.Generator;
import groove.util.CommandLineOption;

import java.io.PrintStream;

/**
 * Counterpart of {@link Generator} for pattern shape state space exploration.
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
    // Object fields
    // ------------------------------------------------------------------------

    private ApplicationMethod method = ApplicationMethod.MATERIALISATION;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public PatternShapeGenerator(String... args) {
        super(args);
        addOption(new NonBranchOption());
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
    public PGTS getPGTS() {
        if (pgts == null) {
            pgts = new PSTS(getGrammar(), this.method);
        }
        return pgts;
    }

    /** Writes output accordingly to options given to the generator. */
    @Override
    public void report() {
        PrintStream out = System.out;
        PSTS psts = (PSTS) getPGTS();
        out.println(String.format(
            "\nPSTS: States: %d -- %d subsumed (%d discarded) / Transitions: %d (%d subsumed)\n",
            psts.getStateCount(), psts.getSubsumedStatesCount(),
            psts.openStateCount(), psts.getTransitionCount(),
            psts.getSubsumedTransitionsCount()));
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void setRuleApplicationMethod(ApplicationMethod method) {
        this.method = method;
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
    private class NonBranchOption implements CommandLineOption {

        @Override
        public String[] getDescription() {
            return new String[] {"Avoids materialision by performing a less precise rule application."};
        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public String getName() {
            return "b";
        }

        @Override
        public boolean hasParameter() {
            return false;
        }

        @Override
        public void parse(String parameter) {
            PatternShapeGenerator.this.setRuleApplicationMethod(ApplicationMethod.NON_BRANCHING);
        }

    }

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

}
