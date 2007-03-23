// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: Profiler.java,v 1.3 2007-03-23 15:42:57 rensink Exp $
 */
package groove.util;

import groove.graph.match.Matcher;
import groove.graph.match.DefaultMatcher;
import groove.graph.match.vf1.VFMatcher;
import groove.graph.match.vf2.VF2Matcher;
import groove.graph.match.ullman.UllmanMatcher;
import groove.graph.match.ullman2.Ullman2Matcher;
import groove.graph.Morphism;
import groove.graph.DefaultMorphism;
import groove.graph.NodeEdgeMap;
import groove.graph.Node;

import java.util.*;

/**
 * A class that profiles a number of algorithms on the graph database found at amalfi.dis.unina.it/graph/db
 * and repoarts the results.
 *
 * Currently it is geared towards correctness verification
 * @author J. ter Hove
 */
public class Profiler extends CommandLineTool {

    /** Usage message for the profiler. */
    static public final String USAGE_MESSAGE = "Usage: Profiler <graphs-dir> <algorithm> \n currently supported: \n" +
                                               "\t default - original algorithm (working)\n" +
                                               "\t ullman  - original ullman (not working)\n" +
                                               "\t ullman2 - new ullman (working) \n" +
                                               "\t vf      - vf-1 (1/2 working) \n" +
                                               "\t vf2     - vf-2 (not working) \n";

    /** String describing the selected algorithm **/
    private String        algorithm  = "";
    
    /** Wrapper object for accessing graphs from the database **/
    private GraphDatabase gdb        = null;

    /**
     * Goes through a number of routines that are designed to test the matching subsytem for
     * perfomance
     * @param args command line arguments
     */
    static public void main(String[] args) {
        new Profiler(new LinkedList<String>(Arrays.asList(args))).start();
    }

    /**
     * Constructs the profiler. In particular, initializes the command line option classes.
     */
    public Profiler(List<String> argsList) {
        super(argsList);
    }

    /**
     */
    public void start() {
    	processArguments();

        startProfileDatabase();
        //startDebug();
    }

    private void startDebug() {       
        MatchingPair mp = new MatchingPair(MatchingTestCases.SimpleTestCase2A(), MatchingTestCases.SimpleTestCase1B(), 4);
        //MatchingPair mp = new MatchingPair(MatchingTestCases.SimpleTestCase4A(), MatchingTestCases.SimpleTestCase1B(), 2);
        //MatchingPair mp = new MatchingPair(MatchingTestCases.SimpleTestCase3A(), MatchingTestCases.SimpleTestCase1B(), 8);
        //MatchingPair mp = new MatchingPair(MatchingTestCases.SimpleTestCase5A(), MatchingTestCases.SimpleTestCase1B(), 8);
        countMatchings(mp, 0);
    }

    /**
     * This functions runs a selected algorithm over the graphs contained in the database directory
     * the profiler was pointed to with the command line argument.
     */
    private void startProfileDatabase() {
        for( String gtr : gdb.records() ) {
            int pp = 0; // used for pretty printing
            System.out.println("Reading " + gtr);
            GraphDatabaseIterator it = gdb.read(gtr);
            while( it.hasNext() ) {
                MatchingPair gp = it.next();

                // hack for outofmemory
                if( gp == null )
                    continue;

                pp = countMatchings(gp, pp);
            }
            System.out.println("");
        }
    }

    private int countMatchings(MatchingPair gp, int prettyprint) {
        int injective = 0, all = 0;

        Matcher m = makeMatcher(gp);
        Iterator<? extends NodeEdgeMap> iter = m.getRefinementIter();
        while( iter.hasNext() ) {
            NodeEdgeMap nem = (NodeEdgeMap)iter.next();

            // detect injectivity by collapsing equivalent nodes (uses hashmap magic)
            Set<Node> nodeValues = new HashSet<Node>(nem.nodeMap().values());
            if(nem.nodeMap().size() == nodeValues.size())
                    injective++;

            all++;
        }

        if( injective == gp.matchings ) {
            prettyprint++;
            System.out.print('.');
            if( prettyprint == 79 ) {
                prettyprint = 0;
                System.out.println();
            }
        } else {
            System.out.println("\n Error in " );
            System.out.println(gp.pattern);
            System.out.println(gp.model);
            System.out.println(" all:" + all + " injective:" + injective + " (" + gp.matchings + ")");
        }
        System.gc();
        return prettyprint;
    }

    private Matcher makeMatcher(MatchingPair gp) {
        Morphism m = new DefaultMorphism(gp.pattern, gp.model);

        if( algorithm.equals("default") ) {
            return new DefaultMatcher(m);
        } else if ( algorithm.equals("ullman") ) {
            return new UllmanMatcher(m);
        } else if ( algorithm.equals("ullman2") ) {
            return new Ullman2Matcher(m);
        } else if ( algorithm.equals("vf1") ) {
            return new VFMatcher(m);
       } else if ( algorithm.equals("vf2") ) {
            return new VF2Matcher(m);
        } else {
            System.err.println("No implementation for selected algorithm " + algorithm);
            System.exit(-1);
        }
        return null;
    }

    /**
     * Goes through the list of command line arguments and tries to find command line options. The
     * options and their parameters are subsequently removed from the argument list. If an option
     * cannot be parsed, the method prints an error message and terminates the program.
     */
    public void processArguments() {
        List<String> argsList = getArgs();
        if( argsList.size() < 2 ) {
            System.out.println(getUsageMessage());
            System.exit(-1);
        }
        gdb        = new GraphDatabase(argsList.get(0));
        algorithm  = argsList.get(1);
    }

    /** Prints a report of the run on the standard output. */
    protected void report() {
        startLog();
        Reporter.report();
        endLog();
    }

    /**
     * This implementation returns <tt>{@link #USAGE_MESSAGE}</tt>.
     */
    protected String getUsageMessage() {
        return USAGE_MESSAGE;
    }
    
}