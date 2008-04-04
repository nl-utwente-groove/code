/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: OptimizedBoundedNestedDFSStrategy.java,v 1.2 2008/02/22 13:02:45 rensink Exp $
 */
package groove.util;

import groove.explore.DefaultScenario;
import groove.explore.GeneratorScenarioHandlerFactory;
import groove.explore.ScenarioHandler;
import groove.explore.strategy.BoundedNestedDFSPocketStrategy;
import groove.explore.strategy.BoundedNestedDFSStrategy;
import groove.explore.strategy.OptimizedBoundedNestedDFSPocketStrategy;
import groove.explore.strategy.OptimizedBoundedNestedDFSStrategy;
import groove.graph.DeltaGraph;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.graph.iso.Bisimulator;
import groove.graph.iso.DefaultIsoChecker;
import groove.io.AspectualViewGps;
import groove.io.ExtensionFilter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.ProductGTS;
import groove.lts.StateGenerator;
import groove.trans.DefaultApplication;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.SPOEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;
import groove.trans.VirtualRuleMatch;
import groove.verify.ModelChecking;
import groove.view.FormatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * A class that takes care of loading in a rule system consisting of a set of individual files
 * containing graph rules, from a given location | presumably the top level directory containing the
 * rule files.
 * @author Harmen Kastenberg
 * @version $Revision: 1.33 $
 */
public class LTLBenchmarker extends CommandLineTool {
    /**
     * Fixed name of the gc log file. If a file with this name is found, and logging is switched on,
     * the gc log is appended to the generator log.
     */
    static public final String GC_LOG_NAME = "gc.log";

    /**
     * Fixed prefix for the identity string.
     */
    static public final String ID_PREFIX = "gts";

    /**
     * Graphs file extension.
     */
    static public final String GRAPH_FILE_EXTENSION = ".graphs";
    /** Error message in case grammar cannot be found. */
    static public final String LOAD_ERROR = "Can't load graph grammar";
    /** Usage message for the generator. */
    static public final String USAGE_MESSAGE = "Usage: LTLBenchmarker <grammar-location> <start-graph-name> <experiment-nr>";

    /** 
     * Value for the output file name to indicate that the name should be computed from the grammar name.
     * @see #getOutputFileName()
     */
    static public final String GRAMMAR_NAME_VAR = "@";
    /** Separator between grammar name and start state name in reporting. */
    static public final char START_STATE_SEPARATOR = '@';

    /** Number of bytes in a kilobyte */
    static private final int BYTES_PER_KB = 1024;

	/** Flag specifying to redo the exploration */
	public static boolean RESTART = false;

	private static String strategyType;
	private static String property;

	private static final String STRATEGY_GRAPH_SIZE = "S";
	private static int initialBound;
	private static int step;
	private static final String STRATEGY_RULE_SET = "R";
	private static String ruleList;
	private static Set<Rule> ruleSet;

//	private static int CURRENT_ITERATION = 0;
	private static final int NR_OF_RUNS = 3;

//	private static String STRATEGY_PATH = "P";

	private static String logFileName;

	public LTLBenchmarker() {
		super(Collections.EMPTY_LIST);
	}

	/**
     * Attempts to load a graph grammar from a given location provided as a paramter with either
     * default start state or a start state provided as a second parameter.
     * @param args the first argument is the grammar location name; if provided, the second argument
     *        is the start graph filename.
     */
    static public void main(String[] args) {
    	if (args.length <= 0) {
    		printUsageMessage();
    		return;
    	}
        List<String> arguments = new LinkedList<String>(Arrays.asList(args));

        int experiment = Integer.parseInt(arguments.remove(0));
        int flag = Integer.parseInt(arguments.remove(0));

        if (experiment == 1) {
    		logFileName = "experiment-1.txt";
    		log("\nRestart\n");
        	for (int i = 0; i < NR_OF_RUNS; i++) {
        		LTLBenchmarker benchmarker = new LTLBenchmarker();
        		// boolean indicates whether to regenerate
        		ModelChecking.resetIteration();
        		benchmarker.experiment1(true);
        	}
    		log("\nReuse\n");
        	for (int i = 0; i < NR_OF_RUNS; i++) {
        		LTLBenchmarker benchmarker = new LTLBenchmarker();
        		// boolean indicates whether to regenerate
        		ModelChecking.resetIteration();
        		benchmarker.experiment1(false);
        	}
        } else if (experiment == 2) {
    		logFileName = "experiment-2.txt";
    		log("\nRestart\n");
        	for (int i = 0; i < NR_OF_RUNS; i++) {
        		LTLBenchmarker benchmarker = new LTLBenchmarker();
        		// boolean indicates whether to regenerate
        		ModelChecking.resetIteration();
        		benchmarker.experiment2(true);
        	}
    		log("\nReuse\n");
        	for (int i = 0; i < NR_OF_RUNS; i++) {
        		LTLBenchmarker benchmarker = new LTLBenchmarker();
        		// boolean indicates whether to regenerate
        		ModelChecking.resetIteration();
        		benchmarker.experiment2(false);
        	}
        } else if (experiment == 3) {
    		logFileName = "experiment-3.txt";
    		log("\nNo pocket states\n");
        	for (int i = 0; i < NR_OF_RUNS; i++) {
        		LTLBenchmarker benchmarker = new LTLBenchmarker();
        		// boolean indicates whether to regenerate
        		ModelChecking.resetIteration();
        		benchmarker.experiment3(false);
        	}
    		log("\nPocket states\n");
        	for (int i = 0; i < NR_OF_RUNS; i++) {
        		LTLBenchmarker benchmarker = new LTLBenchmarker();
        		// boolean indicates whether to regenerate
        		ModelChecking.resetIteration();
        		benchmarker.experiment2(true);
        	}
        } else if (experiment == 4) {
    		logFileName = "experiment-mutex.txt";
        	grammarLocation = "C:\\local\\groove\\samples\\mutex.gps";
        	LTLBenchmarker benchmarker = new LTLBenchmarker();
        	// boolean indicates whether to regenerate
        	if (flag == 0)
        		benchmarker.experimentMutex(false);
        	if (flag == 1)
        		benchmarker.experimentMutex(true);
        } else if (experiment == 5) {
    		logFileName = "experiment-CEB.txt";
        	grammarLocation = "C:\\local\\groove\\samples\\circular-extensible-buffer.gps";
        	LTLBenchmarker benchmarker = new LTLBenchmarker();
        	// boolean indicates whether to regenerate
        	ModelChecking.resetIteration();
        	if (flag == 0)
        		benchmarker.experimentCEB(false);
        	if (flag == 1)
        		benchmarker.experimentCEB(true);
        } else if (experiment == 6) {
        	grammarLocation = "C:\\local\\groove\\samples\\leader-election.gps";
        	ModelChecking.MAX_ITERATIONS = 3;
        	ModelChecking.START_FROM_BORDER_STATES = true;
        	LTLBenchmarker benchmarker = new LTLBenchmarker();
        	// boolean indicates whether to mark pocket states
        	if (flag == 0) {
            	logFileName = "experiment-LEP-correct-3-no-pocket.txt";
        		benchmarker.experimentLEP(false);
        	}
        	if (flag == 1) {
            	logFileName = "experiment-LEP-correct-3-pocket.txt";
        		benchmarker.experimentLEP(true);
        	}
        } else if (experiment == 7) {
        	grammarLocation = "C:\\local\\groove\\samples\\leader-election-faulty-5.gps";
        	ModelChecking.MAX_ITERATIONS = 5;
        	ModelChecking.START_FROM_BORDER_STATES = true;
        	LTLBenchmarker benchmarker = new LTLBenchmarker();
        	// boolean indicates whether to mark pocket states
        	if (flag == 0) {
            	logFileName = "experiment-LEP-faulty-5-no-pocket.txt";
        		benchmarker.experimentLEP(false);
        	}
        	if (flag == 1) {
            	logFileName = "experiment-LEP-faulty-5-pocket.txt";
        		benchmarker.experimentLEP(true);
        	}
        } else if (experiment == 8) {
        	grammarLocation = "C:\\local\\groove\\samples\\leader-election-faulty-6.gps";
        	ModelChecking.MAX_ITERATIONS = 5;
        	ModelChecking.START_FROM_BORDER_STATES = true;
        	LTLBenchmarker benchmarker = new LTLBenchmarker();
        	// boolean indicates whether to mark pocket states
        	if (flag == 0) {
            	logFileName = "experiment-LEP-faulty-6-no-pocket.txt";
        		benchmarker.experimentLEP(false);
        	}
        	if (flag == 1) {
            	logFileName = "experiment-LEP-faulty-6-pocket.txt";
        		benchmarker.experimentLEP(true);
        	}
        } else if (experiment == 9) {
        	grammarLocation = "C:\\local\\groove\\samples\\leader-election-faulty-7.gps";
        	ModelChecking.MAX_ITERATIONS = 5;
        	ModelChecking.START_FROM_BORDER_STATES = true;
        	LTLBenchmarker benchmarker = new LTLBenchmarker();
        	// boolean indicates whether to mark pocket states
        	if (flag == 0) {
            	logFileName = "experiment-LEP-faulty-7-no-pocket.txt";
        		benchmarker.experimentLEP(false);
        	}
        	if (flag == 1) {
            	logFileName = "experiment-LEP-faulty-7-pocket.txt";
        		benchmarker.experimentLEP(true);
        	}
        } else if (experiment == 10) {
        	grammarLocation = "C:\\local\\groove\\samples\\append.gps";
//        	ModelChecking.MAX_ITERATIONS = 20;
        	ModelChecking.START_FROM_BORDER_STATES = true;
        	LTLBenchmarker benchmarker = new LTLBenchmarker();
        	// boolean indicates whether to mark pocket states
        	if (flag == 0) {
            	logFileName = "experiment-append-no-pocket.txt";
        		benchmarker.experimentAppend(false);
        	}
        	if (flag == 1) {
            	logFileName = "experiment-append-pocket.txt";
        		benchmarker.experimentAppend(true);
        	}
        }
    }

    /**
     * Experiment 1:
     * - boundary by graph-size
     * - no reuse
     * - initial = 5
     * - step = 2
     * - max iterations
     */
    public void experiment1(boolean restart) {
    	RESTART = restart;
    	grammarLocation = "C:\\local\\groove\\samples\\circular-extensible-buffer.gps";
    	startStateName = "start";
    	property = "[](put -> <> get)";
//    	logFileName = "results.txt";
    	strategyType = "S";
//    	ModelChecking.MAX_ITERATIONS = 20;
//    	ModelChecking.MAX_ITERATIONS = 40;
    	ModelChecking.MAX_ITERATIONS = 60;
    	ModelChecking.MARK_POCKET_STATES = false;
    	ModelChecking.START_FROM_BORDER_STATES = false;
    	initialBound = 5;
    	step = 2;

        try {
        	long total = 0;
        	do {
        		ModelChecking.nextIteration();
        		System.gc();
        		resetStrategy();
        		resetGTS();
        		init();
        		generate();
        		report();
        		initialBound += step;
        		total += endTime - startTime;
        	} while (!finishedIterations());
        	exit(total);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getProductGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Experiment 1:
     * - boundary by graph-size
     * - no reuse
     * - initial = 5
     * - step = 2
     * - max iterations
     */
    public void experiment2(boolean restart) {
    	grammarLocation = "C:\\local\\groove\\samples\\circular-extensible-buffer.gps";
    	startStateName = "start";
    	property = "[](put -> <> get)";
//    	logFileName = "results.txt";
    	RESTART = restart;
    	strategyType = "S";
//    	ModelChecking.MAX_ITERATIONS = 20;
//    	ModelChecking.MAX_ITERATIONS = 40;
    	ModelChecking.MAX_ITERATIONS = 60;
    	ModelChecking.MARK_POCKET_STATES = false;
    	ModelChecking.START_FROM_BORDER_STATES = false;
    	initialBound = 5;
    	step = 3;
        try {
        	long total = 0;
        	do {
        		ModelChecking.nextIteration();
        		System.gc();
        		resetStrategy();
        		resetGTS();
        		init();
        		generate();
        		report();
        		initialBound += step;
        		total += endTime - startTime;
        	} while (!finishedIterations());
    		exit(total);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getProductGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Experiment 3:
     * - boundary by rule-set
     * - reuse
     * - continue from border states
     * - max iterations
     */
    public void experiment3(boolean pocket) {
    	grammarLocation = "C:\\local\\groove\\samples\\leader-election.gps";
    	startStateName = "start";
    	property = "[](init -> (<> leader))";
//    	logFileName = "results.txt";
    	RESTART = false;
    	strategyType = "R";
    	ruleList = "new-process";
    	ModelChecking.MAX_ITERATIONS = 5;
//    	ModelChecking.MAX_ITERATIONS = 40;
//    	ModelChecking.MAX_ITERATIONS = 60;
    	ModelChecking.MARK_POCKET_STATES = pocket;
    	ModelChecking.START_FROM_BORDER_STATES = true;
//    	strategyType = "S";
//    	initialBound = 10;
//    	step = 2;
        try {
        	long total = 0;
        	do {
        		ModelChecking.nextIteration();
        		System.gc();
        		resetStrategy();
        		resetGTS();
        		init();
        		generate();
        		report();
        		initialBound += step;
        		total += endTime - startTime;
        	} while (!finishedIterations() && getStrategy().getResult().isEmpty());
    		exit(total);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getProductGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Experiment 4:
     * - boundary by rule-set
     * - reuse
     * - continue from border states
     * - max iterations
     */
    public void experimentMutex(boolean pocket) {
    	grammarLocation = "C:\\local\\groove\\samples\\mutex.gps";
    	startStateName = "start";
    	property = "[](new || mount)";
    	RESTART = false;
    	strategyType = "R";
    	ruleList = "new,mount";
    	ModelChecking.MAX_ITERATIONS = 20;
    	ModelChecking.MARK_POCKET_STATES = pocket;
    	ModelChecking.START_FROM_BORDER_STATES = true;
        try {
        	long total = 0;
        	do {
        		ModelChecking.nextIteration();
        		System.gc();
        		resetStrategy();
        		resetGTS();
        		init();
        		generate();
        		report();
        		initialBound += step;
        		total += endTime - startTime;
        	} while (!finishedIterations());
    		exit(total);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getProductGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Experiment 5:
     * - boundary by rule-set
     * - reuse
     * - continue from border states
     * - max iterations
     */
    public void experimentCEB(boolean pocket) {
    	grammarLocation = "C:\\local\\groove\\samples\\circular-extensible-buffer.gps";
    	startStateName = "start";
    	property = "[](extend -> <> gap)";
    	logFileName = "results.txt";
    	RESTART = false;
    	strategyType = "R";
    	ruleList = "extend-correct";
//    	ModelChecking.MAX_ITERATIONS = 20;
//    	ModelChecking.MAX_ITERATIONS = 40;
    	ModelChecking.MAX_ITERATIONS = 60;
    	ModelChecking.MARK_POCKET_STATES = pocket;
    	ModelChecking.START_FROM_BORDER_STATES = true;
        try {
        	long total = 0;
        	do {
        		ModelChecking.nextIteration();
        		System.gc();
        		resetStrategy();
        		resetGTS();
        		init();
        		generate();
        		report();
        		initialBound += step;
        		total += endTime - startTime;
        	} while (!finishedIterations());
    		exit(total);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getProductGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Experiment 6:
     * - boundary by rule-set
     * - reuse
     * - continue from border states
     * - no pocket detection
     * - max iterations = 6
     */
    public void experimentLEP(boolean pocket) {
    	startStateName = "start";
    	property = "[](init -> (<> leader))";
    	RESTART = false;
    	strategyType = "R";
    	ruleList = "new-process";
    	ModelChecking.MAX_ITERATIONS = 5;
    	ModelChecking.MARK_POCKET_STATES = pocket;
    	ModelChecking.START_FROM_BORDER_STATES = true;
        try {
        	long total = 0;
        	do {
        		ModelChecking.nextIteration();
        		System.gc();
        		resetStrategy();
        		resetGTS();
        		init();
        		generate();
        		report();
        		initialBound += step;
        		total += endTime - startTime;
        	} while (!finishedIterations() && getStrategy().getResult().isEmpty());
    		exit(total);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getProductGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Experiment Append
     */
    public void experimentAppend(boolean pocket) {
//    	startStateName = "start-12";
    	startStateName = "append-4-list-8";
    	property = "[]((next U append) -> (<> return))";
    	RESTART = false;
    	strategyType = "R";
    	ruleList = "shorten";
    	ModelChecking.MAX_ITERATIONS = 3;
    	ModelChecking.MARK_POCKET_STATES = pocket;
    	ModelChecking.START_FROM_BORDER_STATES = true;
    	try {
    		ModelChecking.nextIteration();
    		init();
    		generate();
    		report();
    		initialBound += step;
    		exit(endTime - startTime);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getProductGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    private boolean finishedIterations() {
    	return ModelChecking.CURRENT_ITERATION > ModelChecking.MAX_ITERATIONS - 1;
    }

    private void reset() {
    	System.gc();
    	gts = null;
    	strategy = null;
    }

    private void resetStrategy() {
    	strategy = null;
    }

    private void resetGTS() {
    	gts = null;
    }
    
    /**
     * Sets the grammar to be used for state space generation.
     * @param grammarLocation the file name of the grammar (with or without file name extension)
     */
    public static void setGrammarLocation(List<String> arguments) {
        grammarLocation = ruleSystemFilter.addExtension(arguments.remove(0));
    }

    /**
     * Sets the start graph to be used for state space generation.
     * @param startGraphName the name of the start graph (without file name extension)
     */
    public static void setStartGraph(List<String> arguments) {
        startStateName = arguments.remove(0);
    }

    public static void setLogFileName(List<String> arguments) {
    	logFileName = arguments.remove(0);
    }

    public Set<Rule> ruleSet() {
    	if (ruleSet == null) {
    		computeRuleSet();
    	}
    	return ruleSet;
    }

    private void computeRuleSet() {
    	assert (ruleList != null) : "no list of rules available";
    	ruleSet = new HashSet<Rule>();
    	String[] rules = ruleList.split(",");
    	for (String nextRule: rules) {
    		Rule rule = getGrammar().getRule(nextRule);
    		ruleSet.add(rule);
    	}
    }

    public String property() {
		return property;
	}

	public static void setProperty(List<String> arguments) {
		property = arguments.remove(0);
	}

	public static void setStrategyType(List<String> arguments) {
		strategyType = arguments.remove(0);
		if (strategyType.equals(STRATEGY_GRAPH_SIZE)) {
			initialBound = Integer.parseInt(arguments.remove(0));
			step = Integer.parseInt(arguments.remove(0));
		} else if (strategyType.equals(STRATEGY_RULE_SET)) {
			ruleList = arguments.remove(0);
		} else if (Integer.parseInt(strategyType) == 6) {
			ruleList = arguments.remove(0);
		}
	}

	/**
     * This implementation lazily creates and returns the id of this generator run.
     */
    @Override
    protected String getId() {
    	if (id == null) {
    		id = computeId();
    	}
        return id;
    }

    /** Computes an ID from the grammar location and the time. */
    protected String computeId() {
        return ID_PREFIX + ID_SEPARATOR + getGrammarName() + ID_SEPARATOR + super.getId();
    }
    
    /**
     * Returns the GTS that is being generated.
     * The GTS is lazily obtained from the grammar if it had not yet been initialised.
     * @see #getGrammar()
     */
    public ProductGTS getProductGTS() {
        if (productGTS == null) {
            productGTS = new ProductGTS(getGrammar());
        }
    	return productGTS;
    }

    public GTS getGTS() {
        if (gts == null) {
            gts = new GTS(getGrammar());
        }
    	return gts;
    }

    /**
     * Returns the grammar used for generating the state space.
     * The grammar is lazily loaded in.
     * The method throws an error and returns <code>null</code> if the grammar
     * could not be loaded.
     */
    public GraphGrammar getGrammar() {
        if (grammar == null) {
            computeGrammar();
        }
        return grammar;
    }

    /** Loads in and returns a grammar. */
    private void computeGrammar() {
        Observer loadObserver = new Observer() {
            public void update(Observable o, Object arg) {
                if (getVerbosity() > LOW_VERBOSITY) {
                    if (arg instanceof String) {
                        System.out.printf("%s .", arg);
                    } else if (arg == null) {
                        System.out.println(" done");
                    } else {
                        System.out.print(".");
                    }
                }
            }
        };
        loader.addObserver(loadObserver);
        try {
            grammar = loader.unmarshal(new File(grammarLocation), startStateName).toGrammar();
            grammar.setFixed();
        } catch (IOException exc) {
            printError("Can't load grammar: " + exc.getMessage());
        } catch (FormatException exc) {
            printError("Grammar format error: " + exc.getMessage());
        }
        loader.deleteObserver(loadObserver);
    }
    
    /** 
     * Returns the exploration strategy set for the generator.
     * The strategy is lazily retrieved from the command line options,
     * or set to {@link FullStrategy} if no strategy was specified.
     */
    protected ScenarioHandler getStrategy() {
        if (strategy == null) {
            strategy = computeStrategy();
        }
        return strategy;
    }


    /** 
     * Callback factory method to construct the exploration strategy.
     * The strategy is computed from the command line options,
     * or set to {@link FullStrategy} if no strategy was specified.
     */
    protected ScenarioHandler computeStrategy() {
    	ScenarioHandler result;
		if (strategyType.equals(STRATEGY_GRAPH_SIZE)) {
			if (ModelChecking.MARK_POCKET_STATES) {
				result = GeneratorScenarioHandlerFactory.getBoundedModelCheckingScenario(new BoundedNestedDFSPocketStrategy(), "Bounded model checking based on graph sizes", "bounded", initialBound, step , property());
			} else {
				result = GeneratorScenarioHandlerFactory.getBoundedModelCheckingScenario(new BoundedNestedDFSStrategy(), "Bounded model checking based on graph sizes", "bounded", initialBound, step , property());
			}
//			result = GeneratorScenarioHandlerFactory.getBoundedModelCheckingScenario(new OptimizedBoundedNestedDFSStrategy(), "Bounded model checking based on graph sizes", "bounded", initialBound, step , property());
//			result = GeneratorScenarioHandlerFactory.getBoundedModelCheckingScenario(new OptimizedBoundedNestedDFSPocketStrategy(), "Bounded model checking based on graph sizes", "bounded", initialBound, step , property());
		} else if (strategyType.equals(STRATEGY_RULE_SET)) {
			if (ModelChecking.MARK_POCKET_STATES) {
				result = GeneratorScenarioHandlerFactory.getBoundedModelCheckingScenario(new OptimizedBoundedNestedDFSPocketStrategy(), "Bounded model checking based on rule set", "bounded", ruleSet(), property());
			} else {
				result = GeneratorScenarioHandlerFactory.getBoundedModelCheckingScenario(new OptimizedBoundedNestedDFSStrategy(), "Bounded model checking based on rule set", "bounded", ruleSet(), property());
			}
		} else {
			result = null;
		}
		return result;
    }

    /**
     * The initialization phase of state space generation.
     */
    protected void init() {
        getStrategy().setGTS(getGTS());
    }

    /**
     * The processing phase of state space generation. Called from <tt>{@link #start}</tt>.
     */
    protected Collection<? extends Object> generate() {
    	Collection<? extends Object> result;
        final Runtime runTime = Runtime.getRuntime();
        runTime.runFinalization();
        runTime.gc();
        startUsedMemory = runTime.totalMemory() - runTime.freeMemory();
        if (getVerbosity() > LOW_VERBOSITY) {
            System.out.print("Grammar: " + grammarLocation);
            System.out.println("; start graph: "
                    + (startStateName == null ? "default" : startStateName));
            System.out.println("Exploration: " + getStrategy());
            getGTS().addGraphListener(new GenerateProgressMonitor(getGTS(), getStrategy()));
        }
        if (getVerbosity() == HIGH_VERBOSITY) {
        	getProductGTS().addGraphListener(getStatisticsListener());
        }
        startTime = System.currentTimeMillis();
        try {
//        	getStrategy().setState(getProductGTS().startState());
        	getStrategy().playScenario();
        	result = getStrategy().getResult();
        } catch (InterruptedException exc) {
            result = null;
        }
        endTime = System.currentTimeMillis();
        if (getVerbosity() > LOW_VERBOSITY) {
            System.out.println("");
            System.out.println("");
        }
        return result;
    }

    /** Prints a report of the run on the standard output. */
    protected void report() {
        startLog();
        if (getVerbosity() == HIGH_VERBOSITY) {
            Reporter.report();
            if (isLogging()) {
                Reporter.report(getLogWriter());
            }
            println();
            println("-------------------------------------------------------------------");
        }
        if (getVerbosity() > LOW_VERBOSITY) {
            println("Grammar:\t" + grammarLocation);
            println("Start graph:\t" + (startStateName == null ? "default" : startStateName));
            println("Exploration:\t" + getStrategy().toString());
            println("Timestamp:\t" + invocationTime);
            final Runtime runTime = Runtime.getRuntime();
            // clear all caches to see all available memory
//            for (State state: getProductGTS().nodeSet()) {
//                if (state instanceof AbstractCacheHolder) {
//                    ((AbstractCacheHolder<?>) state).clearCache();
//                }
//            }
            // the following is to make sure that the graph reference queue gets flushed
            new DeltaGraph().nodeSet();
            System.runFinalization();
            System.gc();
            long usedMemory = runTime.totalMemory() - runTime.freeMemory();
            println();
            print("Statistics:");
            reportLTS();
            if (getVerbosity() == HIGH_VERBOSITY && Groove.GATHER_STATISTICS) {
                reportGraphStatistics();
                reportTransitionStatistics();
                reportIsomorphism();
                reportGraphElementStatistics();
                reportCacheStatistics();
            }
            if (getOutputFileName() != null) {
                String outFileName = gxlFilter.addExtension(getOutputFileName());
                println();
                println("LTS stored in: \t" + outFileName);
            }
            println();
            reportTime();
            reportSpace(usedMemory - startUsedMemory);
        }
        // transfer the garbage collector log (if any) to the log file (if any)
        if (isLogging()) {
            File gcLogFile = new File(GC_LOG_NAME);
            if (gcLogFile.exists()) {
                try {
                    BufferedReader gcLog = new BufferedReader(new FileReader(gcLogFile));
                    List<String> gcList = new ArrayList<String>();
                    String nextLine = gcLog.readLine();
                    while (nextLine != null) {
                        gcList.add(nextLine);
                        nextLine = gcLog.readLine();
                    }
                    for (int i = 1; i < gcList.size() - 2; i++) {
                        getLogWriter().println(gcList.get(i));
                    }
                } catch (FileNotFoundException exc) {
                    System.err.println("Error while opening GC log");
                } catch (IOException exc) {
                    System.err.println("Error while reading from GC log");
                }
            }
        }
        endLog();
    }

    /**
     * Reports data on the LTS generated.
     */
    private void reportLTS() {
//        println("\tStates:\t" + getGTS().nodeCount());
//        println("\tTransitions:\t" + getGTS().edgeCount());
        println("\tStates:\t" + getStrategy().getProductGTS().nodeCount());
        println("\tTransitions:\t" + getStrategy().getProductGTS().edgeCount());
    }

    /**
     * Gives some statistics regarding the graphs and deltas. 
     */
    private void reportGraphStatistics() {
//        printf("\tGraphs:\tModifiable:\t%d%n", groove.graph.AbstractGraph.getModifiableGraphCount());
//        printf("\t\tFrozen:\t%d%n", AbstractGraphState.getFrozenGraphCount());
////        printf("\t\tFraction:\t%s%n", percentage(DeltaGraph.getFrozenFraction()));
//        printf("\t\tBytes/state:\t%.1f%n", getProduGTS().getBytesPerState());
    }

    /**
     * Gives some statistics regarding the generated transitions. 
     */
    private void reportTransitionStatistics() {
        printf("\tTransitions:\tAliased:\t%d%n", VirtualRuleMatch.getAliasCount());
        printf("\t\tConfluent:\t%d%n", StateGenerator.getConfluentDiamondCount());
        printf("\t\tEvents:\t%d%n", SystemRecord.getEventCount());
        printf("\tCoanchor reuse:\t%d/%d%n", SPOEvent.getCoanchorImageOverlap(), SPOEvent.getCoanchorImageCount());
    }

    /**
     * Reports on the cache usage.
     */
    private void reportCacheStatistics() {
        println("\tCaches:\tCreated:\t" + CacheReference.getCreateCount());
        println("\t\tCleared:\t" + CacheReference.getClearCount());
        println("\t\tCollected:\t" + CacheReference.getCollectCount());
        println("\t\tReconstructed:\t" + CacheReference.getIncarnationCount());
        println("\t\tDistribution:\t" + getCacheReconstructionDistribution());
    }

    /**
     * Reports on the graph data. 
     */
    private void reportGraphElementStatistics() {
        printf("\tDefault nodes:\t%d%n", groove.graph.DefaultNode.getNodeCount());
        printf("\tDefault labels:\t%d%n", groove.graph.DefaultLabel.getLabelCount());
        printf("\tFresh nodes:\t%d%n", DefaultApplication.getFreshNodeCount());
        printf("\tFresh edges:\t%d%n", groove.graph.DefaultEdge.getEdgeCount());
        double nodeAvg = (double) getStatisticsListener().getNodeCount() / getProductGTS().nodeCount();
        printf("\tAverage:\tNodes:\t%3.1f%n", nodeAvg);
        double edgeAvg = (double) getStatisticsListener().getEdgeCount() / getProductGTS().nodeCount();
        printf("\t\tEdges:\t%3.1f%n", edgeAvg);
//        println("\t\tDelta:\t" + groove.graph.DeltaGraph.getDeltaElementAvg());
//        println("\tAnchor images:\t" + DefaultGraphTransition.getAnchorImageCount());
    }

    /**
     * Reports statistics on isomorphism checking.
     */
    private void reportIsomorphism() {
        int predicted = DefaultIsoChecker.getTotalCheckCount();
        int distinctCount = DefaultIsoChecker.getDistinctSizeCount() + DefaultIsoChecker.getDistinctCertsCount() + DefaultIsoChecker.getDistinctSimCount();
        int equalGraphCount = DefaultIsoChecker.getEqualGraphsCount();
        int equalCertsCount = DefaultIsoChecker.getEqualCertsCount();
        int equalSimCount = DefaultIsoChecker.getEqualSimCount();
        int intCertOverlap = DefaultIsoChecker.getIntCertOverlap();
        println("\tIsomorphism:\tPredicted:\t"+predicted);
        println("\t\tFalse positives:\t"+distinctCount);
        println("\t\tFraction:\t"+percentage((double) distinctCount / predicted));
        println("\t\tInt cert overlap:\t"+intCertOverlap);
        println("\t\tEqual graphs:\t"+equalGraphCount);
        println("\t\tEqual certificates:\t"+equalCertsCount);
        println("\t\tEqual simulation:\t"+equalSimCount);
        println("\t\tIterations:\t"+Bisimulator.getIterateCount());
    }

    /** Returns a string describing the distribution of cache reconstruction counts. */
    private String getCacheReconstructionDistribution() {
    	List<Integer> sizes = new ArrayList<Integer>();
    	boolean finished = false;
    	for (int incarnation = 1; !finished; incarnation++) {
    		int size = CacheReference.getFrequency(incarnation);
    		finished = size == 0;
    		if (! finished) {
    			sizes.add(size);
    		}
    	}
    	return Groove.toString(sizes.toArray());
    }
    
    /**
     * Reports on the time usage, for any verbosity but low.
     */
    private void reportTime() {
        // timing figures
        long total = (endTime - startTime);
        long matching = SPORule.getMatchingTime();
        long running = DefaultScenario.getRunningTime();
        long overhead = total - running;
        long isoChecking = DefaultIsoChecker.getTotalTime();
        long building = StateGenerator.getGenerateTime() - isoChecking;
        long measuring = Reporter.getTotalTime();

        // this calculation incorporates only transforming RuleMatches into RuleApplications
        // long transforming = DefaultScenario.getTransformingTime();// - matching - building - measuring;

        // bit weird maybe, but transforming is considered everything besides the calculation 
        // of matches, isomorphisms, adding to GTS, and reporter-duty: i.e. it's the "overhead" of the scenario 
        long transforming = running - matching - isoChecking - building - measuring; 
//        long checktotal = matching+isoChecking+building+measuring+transforming;
        
        println("Time (ms):\t" + total);
        
//        println("Running:\t"+running);
//        println("TotalComputed:\t"+checktotal);
//        println("TotalDiff:\t"+(checktotal-total));

        
        println("\tMatching:\t" + matching + "\t" + percentage(matching / (double) total));
        println("\tTransforming:\t" + transforming + "\t"
                + percentage(transforming / (double) total));
        println("\tIso checking:\t" + isoChecking + "\t" + percentage(isoChecking / (double) total));
        if (getVerbosity() == HIGH_VERBOSITY) {
            long certifying = DefaultIsoChecker.getCertifyingTime();
            long equalCheck = DefaultIsoChecker.getEqualCheckTime();
            long certCheck = DefaultIsoChecker.getCertCheckTime();
            long simCheck = DefaultIsoChecker.getSimCheckTime();
            println("\t\tCertifying:\t" + certifying + "\t" + percentage(certifying / (double) isoChecking));
            println("\t\tEquals check:\t" + equalCheck + "\t" + percentage(equalCheck / (double) isoChecking));
            println("\t\tCert check:\t" + certCheck + "\t" + percentage(certCheck / (double) isoChecking));
            println("\t\tSim check:\t" + simCheck + "\t" + percentage(simCheck / (double) isoChecking));
        }
        println("\tBuilding GTS:\t" + building + "\t" + percentage(building / (double) total));
        println("\tMeasuring:\t" + measuring + "\t" + percentage(measuring / (double) total));
        println("\tInitialization:\t" + overhead + "\t" + percentage(overhead / (double) total));
        println("");
    }

    /**
     * Reports on the time usage, for any verbosity but low.
     * @param usedMemory the final memory after generation, cache clearing and garbage collection
     */
    private void reportSpace(long usedMemory) {
        println("Space (kB):\t" + (usedMemory / BYTES_PER_KB));
    }

    /**
     * The finalization phase of state space generation. Called from <tt>{@link #start}</tt>.
     */
    protected void exit(long total) throws IOException, FormatException {
    	if (getStrategy().getResult().size() > 0) {
    		System.out.println("Counter-example found.");
    	} else {
    		System.out.println("No counter-example found.");
    	}
    	// write statistics to log-file
    	
    	File logFile = new File(logFileName);
    	FileWriter logFileWriter = new FileWriter(logFileName, true);
    	BufferedWriter writer = new BufferedWriter(logFileWriter);

    	writer.newLine();
        int stateCount = getStrategy().getProductGTS().nodeCount();
        int stateCountSystem = getGTS().nodeCount();
        int transitionCount = getStrategy().getProductGTS().edgeCount();
        int transitionCountSystem = getGTS().edgeCount();
//        long total = (endTime - startTime);
        long matching = SPORule.getMatchingTime();
    	String results = stateCount + " (" + stateCountSystem + ") " + transitionCount + " (" + transitionCountSystem + ") " + total + " " + (percentage(matching / (double) total));
//    	writer.write("\n" + experiment + "\n");
    	writer.write(results);
    	writer.close();
    }

    public static void log(String message) {
    	try {
        	File logFile = new File(logFileName);
        	FileWriter logFileWriter = new FileWriter(logFileName, true);
        	BufferedWriter writer = new BufferedWriter(logFileWriter);
        	
        	writer.write(message);
        	writer.close();
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
    }

    /** 
     * This implementation expands any occurrence of {@link #GRAMMAR_NAME_VAR}
     * into the grammar name.
     */
    @Override
    protected String getOutputFileName() {
        String result = super.getOutputFileName();
        if (result != null) {
            String grammarNameRegExpr = GRAMMAR_NAME_VAR;
            result = result.replaceAll(grammarNameRegExpr, getGrammarName());
        }
        return result;
    }


    /**
	 * Factory method for the grammar loader to be used by state space
	 * generation. 
	 */
    protected AspectualViewGps createGrammarLoader() {
        return new AspectualViewGps();
//        return new GpsGrammar(new UntypedGxl(graphFactory), SPORuleFactory.getInstance());
    }

    /**
     * Convenience method to derive the name of a grammar from its location. Strips the directory
     * and extension.
     */
    protected String getGrammarName() {
        StringBuilder result = new StringBuilder(new File(ruleSystemFilter.stripExtension(grammarLocation)).getName());
        if (startStateName != null) {
            result.append(START_STATE_SEPARATOR);
            result.append(startStateName);
        }
        return result.toString();
    }
    
    /** This implementation returns <tt>getId()</tt>. */
    @Override
    protected String getLogFileName() {
        return getId();
    }

    /**
     * This implementation returns <tt>{@link #USAGE_MESSAGE}</tt>.
     */
    @Override
    protected String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    private StatisticsListener getStatisticsListener() {
    	if (statisticsListener == null) {
    		statisticsListener = new StatisticsListener();
    	}
    	return statisticsListener;
    }
    /**
     * Returns a string representation of a double as a percentage.
     */
    private String percentage(double fraction) {
        int percentage = (int) (fraction * 1000 + 0.5);
        String result = "" + (percentage / 10) + "." + (percentage % 10) + "%";
        if (result.length() == 4) {
            return " " + result;
        } else {
            return result;
        }
    }

    /**
     * The identity for this genrator, constructed from grammar name and time of invocation.
     */
    private String id;

    /**
     * The time stamp of the moment at which exploration was started.
     */
    private long startTime;

    /**
     * The time stamp of the moment at which exploration was ended.
     */
    private long endTime;

    /**
     * The amunt of memory used at the moment at which exploration was started.
     */
    private long startUsedMemory;

    /**
     * The GTS that is being constructed. We make it static to enable mamory profiling.
     */
    protected static ProductGTS productGTS;

    /**
     * The GTS that is being constructed. We make it static to enable mamory profiling.
     */
    protected GTS gts;

    /**
     * The strategy to be used for the state space generation.
     */
    private ScenarioHandler strategy;
    /** String describing the location where the grammar is to be found. */
    private static String grammarLocation;
    /** String describing the start graph within the grammar. */
    private static String startStateName;
    /** The graph grammar used for the generation. */
    private GraphGrammar grammar;
    /** Statistics listener to the GTS. */
    private StatisticsListener statisticsListener;
    
    /** File filter for rule systems. */
    protected static final ExtensionFilter ruleSystemFilter = Groove.createRuleSystemFilter();
    /** File filter for graph files (GXL). */
    protected static final ExtensionFilter gxlFilter = Groove.createGxlFilter();
    /** File filter for graph state files (GST). */
    protected static final ExtensionFilter gstFilter = Groove.createStateFilter();
    /** File filter for graph files (GXL or GST). */
    protected static final ExtensionFilter graphFilter = new ExtensionFilter("Serialized graph files",
            GRAPH_FILE_EXTENSION);

    /**
     * The grammar loader.
     */
    protected final AspectualViewGps loader = createGrammarLoader();

    /** Listener to an LTS that counts the nodes and edges of the states. */
    private static class StatisticsListener extends GraphAdapter {
    	/** Empty constructor with the correct visibility. */
		StatisticsListener() {
			// Auto-generated constructor stub
		}
		
		@Override
		public void addUpdate(GraphShape graph, Node node) {
			GraphState state = (GraphState) node;
			nodeCount += state.getGraph().nodeCount();
			edgeCount += state.getGraph().edgeCount();
		}
    	
		/** Returns the number of nodes in the added states. */
		public int getNodeCount() {
			return nodeCount;
		}
    	
		/** Returns the number of edges in the added states. */
		public int getEdgeCount() {
			return edgeCount;
		}
		
		private int nodeCount;
		private int edgeCount;
    }

    public static void printUsageMessage() {
    	System.out.println("Usage: <grammar-location> <start-graph> strategy property");
    }
}
