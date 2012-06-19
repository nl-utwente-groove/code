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
 * $Id $
 */
package groove.util;

import groove.explore.ConditionalScenarioHandler;
import groove.explore.DefaultScenario;
import groove.explore.GeneratorScenarioHandlerFactory;
import groove.explore.ScenarioHandler;
import groove.explore.result.EdgeBoundCondition;
import groove.explore.result.ExploreCondition;
import groove.explore.result.InvariantViolatedAcceptor;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.NodeBoundCondition;
import groove.explore.result.SizedResult;
import groove.explore.strategy.BoundedNestedDFSStrategy;
import groove.explore.strategy.BreadthFirstStrategy;
import groove.explore.strategy.ConditionalBreadthFirstStrategy;
import groove.explore.strategy.DepthFirstStrategy2;
import groove.explore.strategy.LinearStrategy;
import groove.graph.DefaultLabel;
import groove.graph.DeltaGraph;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.iso.Bisimulator;
import groove.graph.iso.DefaultIsoChecker;
import groove.io.AspectualViewGps;
import groove.io.ExtensionFilter;
import groove.io.RuleList;
import groove.lts.AbstractGraphState;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTSGraph;
import groove.lts.State;
import groove.lts.StateGenerator;
import groove.trans.DefaultApplication;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.SPOEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;
import groove.trans.VirtualRuleMatch;
import groove.view.FormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

/**
 * A class that takes care of loading in a rule system consisting of a set of individual files
 * containing graph rules, from a given location | presumably the top level directory containing the
 * rule files.
 * @author Arend Rensink
 * @version $Revision: 1.33 $
 */
public class Generator extends CommandLineTool {
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
    static public final String USAGE_MESSAGE = "Usage: Generator [options] <grammar-location> [<start-graph-name> | <start-graphs-dir>]";

    /** 
     * Value for the output file name to indicate that the name should be computed from the grammar name.
     * @see #getOutputFileName()
     */
    static public final String GRAMMAR_NAME_VAR = "@";
    /** Separator between grammar name and start state name in reporting. */
    static public final char START_STATE_SEPARATOR = '@';

    /** Indentation used when printing help. */
    static private final String INDENT = "    ";

    /** Number of bytes in a kilobyte */
    static private final int BYTES_PER_KB = 1024;

    /**
     * Attempts to load a graph grammar from a given location provided as a paramter with either
     * default start state or a start state provided as a second parameter.
     * @param args the first argument is the grammar location name; if provided, the second argument
     *        is the start graph filename.
     */
    static public void main(String[] args) {
        new Generator(new LinkedList<String>(Arrays.asList(args))).start();
    }

    /**
     * Constructs the generator. In particular, initializes the command line option classes.
     */
    public Generator(List<String> argsList) {
        super(argsList);
        addOption(new ExploreOption());
        addOption(new FinalSaveOption());
    }

    /**
     * Starts the state space generation process. Before invoking this method, all relevant
     * parameters should be set. The method successively calls <tt>{@link #init}</tt>,
     * <tt>{@link #generate}</tt> and <tt>{@link #exit}</tt>.
     */
    public void start() {
    	processArguments();
        try {
            init();
            Collection<? extends Object> result = generate();
            report();
            exit(result);
        } catch (java.lang.OutOfMemoryError e) { // added for the contest, to be removed
        	e.printStackTrace();
        	System.out.println("\n\tStates:\t" + getGTS().nodeCount());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Sets the grammar to be used for state space generation.
     * @param grammarLocation the file name of the grammar (with or without file name extension)
     */
    public void setGrammarLocation(String grammarLocation) {
        this.grammarLocation = ruleSystemFilter.addExtension(grammarLocation);
    }

    /**
     * Sets the start graph to be used for state space generation.
     * @param startGraphName the name of the start graph (without file name extension)
     */
    public void setStartGraph(String startGraphName) {
        this.startStateName = startGraphName;
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
     * Goes through the list of command line arguments and tries to find command line options. The
     * options and their parameters are subsequently removed from the argument list. If an option
     * cannot be parsed, the method prints an error message and terminates the program.
     */
    @Override
    public void processArguments() {
        super.processArguments();
        List<String> argsList = getArgs();
        if (argsList.size() > 0) {
            setGrammarLocation(argsList.remove(0));
        }
        if (argsList.size() > 0) {
            setStartGraph(argsList.remove(0));
        }
        if (grammarLocation == null) {
            printError("No grammar location specified");
        }
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
		ExploreOption explore = getActiveOption(ExploreOption.class);
		if (explore == null) {
			result = GeneratorScenarioHandlerFactory.getScenarioHandler(new BreadthFirstStrategy(), "Breadth first full exploration.", "full");
		} else {
			ExploreStrategyParser exploreParser = explore.getParser();
			result = exploreParser.getStrategy();
			if (result instanceof ConditionalScenarioHandler) {
				ConditionalScenarioHandler<?> condResult = (ConditionalScenarioHandler<?>) result;
				String conditionName = exploreParser.getCondition();
				if (condResult.getConditionType().equals(Rule.class)) {
					Rule condition = getGrammar().getRule(new NameLabel(conditionName));
					if (condition == null) {
						printError("Error in exploration strategy: unknown condition " + conditionName);
					} else {
						ExploreCondition<Rule> explCond = new IsRuleApplicableCondition();
						explCond.setCondition(condition);
						((ConditionalScenarioHandler<Rule>) result).setCondition(explCond, conditionName, exploreParser.isNegated());
					}
				} 
			} else if (result instanceof ControlledScenarioHandler) {
				((ControlledScenarioHandler) result).setProgram(exploreParser.getProgram().getRules(getGrammar()), true);
			}
		}
		return result;
    }

    /**
     * Returns the prefix of the filename to save all final states. If <tt>null</tt>, final
     * states will not be saved (default).
     * @see FinalSaveOption
     */
    protected String getFinalSaveName() {
    	FinalSaveOption option = getActiveOption(FinalSaveOption.class);
    	return option == null ? null : option.getFinalSaveName();
    }

    /**
     * The initialization phase of state space generation. Called from <tt>{@link #start}</tt>.
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
        	getGTS().addGraphListener(getStatisticsListener());
        }
        startTime = System.currentTimeMillis();
        try {
        	getStrategy().setState(getGTS().startState());
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
            for (GraphState state: getGTS().nodeSet()) {
                if (state instanceof AbstractCacheHolder) {
                    ((AbstractCacheHolder<?>) state).clearCache();
                }
            }
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
        println("\tStates:\t" + getGTS().nodeCount());
        int spuriousStateCount = getGTS().openStateCount();
        if (spuriousStateCount > 0) {
            println("\tExplored:\t" + (getGTS().nodeCount() - spuriousStateCount));
        }
        println("\tTransitions:\t" + getGTS().edgeCount());
    }

    /**
     * Gives some statistics regarding the graphs and deltas. 
     */
    private void reportGraphStatistics() {
        printf("\tGraphs:\tModifiable:\t%d%n", groove.graph.AbstractGraph.getModifiableGraphCount());
        printf("\t\tFrozen:\t%d%n", AbstractGraphState.getFrozenGraphCount());
//        printf("\t\tFraction:\t%s%n", percentage(DeltaGraph.getFrozenFraction()));
        printf("\t\tBytes/state:\t%.1f%n", getGTS().getBytesPerState());
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
        double nodeAvg = (double) getStatisticsListener().getNodeCount() / getGTS().nodeCount();
        printf("\tAverage:\tNodes:\t%3.1f%n", nodeAvg);
        double edgeAvg = (double) getStatisticsListener().getEdgeCount() / getGTS().nodeCount();
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
    protected void exit(Collection<? extends Object> result) throws IOException, FormatException {
        if (getFinalSaveName() != null) {
        	if (result.isEmpty()) {
        		System.out.println("No resulting graphs");
        	} else {
				for (State finalState : getGTS().getFinalStates()) {
					String outFileName = getFinalSaveName() + "-" + finalState;
					outFileName = gstFilter.addExtension(outFileName);
					Groove.saveGraph(((GraphState) finalState).getGraph(), outFileName);
				}
				System.out.printf("Resulting graphs saved: %s%n", getGTS().getFinalStates());
			}
        }
        if (getOutputFileName() != null) {
            if (getVerbosity() == HIGH_VERBOSITY) {
                print(GraphReporter.createInstance().getReport(getGTS()).toString());
            }
                Groove.saveGraph(new LTSGraph(getGTS()), getOutputFileName());
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
    protected static GTS gts;

    /**
     * The strategy to be used for the state space generation.
     */
    private ScenarioHandler strategy;
    /** String describing the location where the grammar is to be found. */
    private String grammarLocation;
    /** String describing the start graph within the grammar. */
    private String startStateName;
    /** The graph grammar used for the generation. */
    private GraphGrammar grammar;
    /** Statistics listener to the GTS. */
    private StatisticsListener statisticsListener;
    
    /** File filter for rule systems. */
    protected final ExtensionFilter ruleSystemFilter = Groove.createRuleSystemFilter();
    /** File filter for graph files (GXL). */
    protected final ExtensionFilter gxlFilter = Groove.createGxlFilter();
    /** File filter for graph state files (GST). */
    protected final ExtensionFilter gstFilter = Groove.createStateFilter();
    /** File filter for graph files (GXL or GST). */
    protected final ExtensionFilter graphFilter = new ExtensionFilter("Serialized graph files",
            GRAPH_FILE_EXTENSION);

    /**
     * The grammar loader.
     */
    protected final AspectualViewGps loader = createGrammarLoader();
    /**
     * Option to save all final states generated.
     */
    static protected class FinalSaveOption implements CommandLineOption {
    	/** Name of the final save option. */
        static public final String NAME = "f";
        /** Parameter name of the final save option. */
        static public final String PARAMETER_NAME = "file";
    
        public String[] getDescription() {
            return new String[] { "Tells the generator to save all final states found",
                    "using filenames starting with " + PARAMETER_NAME + " followed by a number" };
        }
    
        public String getName() {
            return NAME;
        }
    
        public String getParameterName() {
            return PARAMETER_NAME;
        }
    
        public boolean hasParameter() {
            return true;
        }
        
        /**
         * Stores the parameter as the final save name.
         * @see #getFinalSaveName()
         */
        public void parse(String parameter) throws IllegalArgumentException {
            finalSaveName = parameter;
        }
        
        /** Returns the name specified as a paramter to the command line option. */
        public String getFinalSaveName() {
        	return finalSaveName;
        }

        /** The name spacified as a parameter of the command line option. */
        private String finalSaveName;
    }

    /**
     * Option to control the exploration strategy in the generator.
     */
    static public class ExploreOption implements CommandLineOption {
    	/** Name of the option. */
        static public final String NAME = "x";
        /** Name of the option parameter. */
        static public final String PARAMETER_NAME = "str";

        public String getName() {
            return NAME;
        }

        public boolean hasParameter() {
            return true;
        }
    
        public void parse(String parameter) throws IllegalArgumentException {
            parser.parse(parameter);
//            generator.setStrategy(parser.getStrategy(), parser.getCondition(), parser.isNegated(), parser.getProgram());
        }
        
        /**
         * Parser to extract information about the strategy from the textual parameter.
         */
        public ExploreStrategyParser getParser() {
            return parser;
        }
    
        public String[] getDescription() {
            List<String> result = new LinkedList<String>();
            result.add("Set the exploration strategy. Legal values for '" + getParameterName()
                    + "' are:");
            for (String description: parser.getStrategyDescriptions()) {
                result.add(INDENT + description);
            }
            return result.toArray(new String[0]);
        }
    
        public String getParameterName() {
            return PARAMETER_NAME;
        }
        
        /**
         * The underlying parser for this options.
         */
        private final ExploreStrategyParser parser = new ExploreStrategyParser();
    }

    /**
     * Action class that can parse a string into an exploration strategy and its (conditional)
     * parameters.
     */
    static public class ExploreStrategyParser {
        /** Text that separates the condition from the strategy name. */
        static public final String CONDITION_SEPARATOR = ":";
    
        /** Condition negator. */
        static public final String NEGATION = "!";
    
        /** Constructs a parser that can recognize all implemented exploration strategies. */
        public ExploreStrategyParser() {
        	addStrategy(GeneratorScenarioHandlerFactory.getScenarioHandler(new DepthFirstStrategy2(), "Depth first full exploration.", "barbed"));
        	addStrategy(GeneratorScenarioHandlerFactory.getScenarioHandler(new BreadthFirstStrategy(), "Breadth first full exploration.", "branching"));
        	addStrategy(GeneratorScenarioHandlerFactory.getScenarioHandler(new LinearStrategy(), "Explores one successor of each state until a final state or a loop is reached.", "linear"));
        	addStrategy(GeneratorScenarioHandlerFactory.getScenarioHandler(new BreadthFirstStrategy(), "Bradth first full exploration (same as branching)", "full"));
        	addStrategy(GeneratorScenarioHandlerFactory.getConditionalScenario(new ConditionalBreadthFirstStrategy(), Integer.class, "Only explores states where the node count does not exceed a given bound.", "node-bounded"));
        	addStrategy(GeneratorScenarioHandlerFactory.getConditionalScenario(new ConditionalBreadthFirstStrategy(), Map.class, "Only explores states where the edge counts do not exceed given bounds.", "edge-bounded"));
        	addStrategy(GeneratorScenarioHandlerFactory.getConditionalScenario(new ConditionalBreadthFirstStrategy(), Rule.class, "Explores all states in which the (negated) condition holds.", "bounded"));
        	addStrategy(GeneratorScenarioHandlerFactory.getConditionalScenario(new BreadthFirstStrategy(), Rule.class, new InvariantViolatedAcceptor<Rule>(), new SizedResult<GraphState>(1), "Explores all states until the (negated) invariant is violated. The order of exploration is random.", "invariant"));
        	addStrategy(GeneratorScenarioHandlerFactory.getBoundedModelCheckingScenario(new BoundedNestedDFSStrategy(), "Bounded model checking exploration", "model-checking"));
        	addStrategy(new ControlledScenarioHandler("Performs a depth-firs search controlled by a sequence of rules.", "controlled"));
        }
    
        /**
         * Returns the exploration strategy determined by parsing.
         * @see #parse(String)
         */
        public ScenarioHandler getStrategy() {
            return parsedStrategy;
        }
    
        /**
         * Returns the condition determined by parsing, in case the strategy is conditional. Returns
         * <tt>null</tt> if no condition was specified.
         * @see #parse(String)
         * @see ConditionalExploreStrategy#setCondition(groove.trans.Condition)
         */
        public String getCondition() {
            return parsedCondition;
        }
    
        /**
         * Indicates if the condition was negated, in case of a conditional strategy. Returns
         * <tt>false</tt> if no condition was specified.
         * @see ConditionalExploreStrategy#setNegated(boolean)
         * @see #parse(String)
         * @see #getCondition()
         */
        public boolean isNegated() {
            return parsedNegated;
        }
        
        /**
         * Returns a list of rule names in case the strategy is a {@link ControlledStrategy}.
         */
        public RuleList getProgram() {
            return parsedProgram;
        }
    
        /**
         * Returns a list of descriptions for the strategies recognized by this parser, indicating
         * the supported string format.
         */
        public List<String> getStrategyDescriptions() {
            List<String> result = new ArrayList<String>();
            for (Map.Entry<String,ScenarioHandler> strategyEntry: strategies.entrySet()) {
                ScenarioHandler strategy = strategyEntry.getValue();
                String name = strategyEntry.getKey();
                if (strategy instanceof ConditionalScenarioHandler) {
                	ConditionalScenarioHandler<?> condStrategy = (ConditionalScenarioHandler<?>) strategy;
                	if (condStrategy.getConditionType().equals(Integer.class)) {
                		name += CONDITION_SEPARATOR + "<bound>";
                	} else if (condStrategy.getConditionType().equals(Rule.class)) {
                		name += CONDITION_SEPARATOR + "[" + NEGATION + "]<condition>";
                	} else if (condStrategy.getConditionType().equals(Map.class)) {
                		name += CONDITION_SEPARATOR + "<key=value>{,<key=value>}*";
                	} else {
                		assert true : "Unknown condition type " + condStrategy.getConditionType();
                	}
                }
                result.add(name + " - " + strategy.getDescription());
            }
            return result;
        }
    
        /**
         * Parses a given string specifying an exploration strategy. The result of parsing can be
         * queried by {@link #getStrategy()}, {@link #getCondition()} and {@link #isNegated()}.
         * @param parameter string from which the strategy is to be determined.
         * @throws IllegalArgumentException if <tt>parameter</tt> is not formatted correctly
         */
        public void parse(String parameter) throws IllegalArgumentException {
            parsedStrategy = null;
            Iterator<Map.Entry<String,ScenarioHandler>> strategyIter = strategies.entrySet().iterator();
            while (parsedStrategy == null && strategyIter.hasNext()) {
                Map.Entry<String,ScenarioHandler> strategyEntry = strategyIter.next();
                String strategyName = strategyEntry.getKey();
                if (parameter.startsWith(strategyName)) {
                    parsedStrategy = strategyEntry.getValue();
                    if (parameter.startsWith(strategyName + CONDITION_SEPARATOR)) {
                        parameter = parameter.substring(strategyName.length() + 1);
                    } else {
                        parameter = "";
                    }
                    if (parsedStrategy instanceof ConditionalScenarioHandler) {
                    	ConditionalScenarioHandler<?> condStrategy = (ConditionalScenarioHandler<?>) parsedStrategy;
                    	if (condStrategy.getConditionType().equals(Integer.class)) {
                    		try {
                    			int bound = Integer.parseInt(parameter);
                    			ExploreCondition<Integer> explCond = new NodeBoundCondition();
                    			explCond.setCondition(bound);
                    			((ConditionalScenarioHandler<Integer>) condStrategy).setCondition(explCond, ""+bound, false);
                    		} catch (NumberFormatException exc) {
                    			throw new IllegalArgumentException(parameter
                    					+ " is not a valid node bound");
                    		}
                    	} else if (condStrategy.getConditionType().equals(Rule.class)) {
                    		if (parameter.length() == 0) {
                    			throw new IllegalArgumentException("Strategy " + parameter
                    					+ " does not specify condition; syntax: '" + strategyName
                    					+ CONDITION_SEPARATOR + "<condition rule>'");
                    		}
                    		parsedNegated = parameter.startsWith(NEGATION);
                    		if (parsedNegated) {
                    			parsedCondition = parameter.substring(1);
                    		} else {
                    			parsedCondition = parameter;
                    		}
                    	} else if (condStrategy.getConditionType().equals(Map.class)) {
                            String[] bounds = parameter.split(",");
                            Map<Label, Integer> conditions = new HashMap<Label,Integer>();
                            for (int i = 0; i < bounds.length; i++) {
                                String[] keyValue = bounds[i].split("=");
                                if (keyValue.length != 2) {
                                    throw new IllegalArgumentException("Edge bounds '" + bounds[i]
                                            + "' should be formatted as 'key=value'");
                                }
                                Label key = DefaultLabel.createLabel(keyValue[0]);
                                try {
                                    int value = Integer.parseInt(keyValue[1]);
                                    conditions.put(key, value);
                                } catch (NumberFormatException exc) {
                                    throw new IllegalArgumentException("Value '" + keyValue[1]
                                            + "' in edge bounds '" + bounds[i]
                                            + "' is not a number");
                                }
                            }
                            ExploreCondition<Map<Label,Integer>> explCond = new EdgeBoundCondition();
                            explCond.setCondition(conditions);
                            ((ConditionalScenarioHandler<Map<Label,Integer>>) condStrategy).setCondition(explCond, "", false);
                    	} else {
                    		assert true : "Unknown condition type " + condStrategy.getConditionType();
                    	}
                    	
                    }
                    else if (parsedStrategy instanceof ControlledScenarioHandler) {
                    	try {
                    		parsedProgram = new RuleList(new File(parameter));
                    	} catch (IOException exc) {
                    		throw new IllegalArgumentException(exc.getMessage());
                    	}
                    } 
                }
            }
            if (parsedStrategy == null) {
                throw new IllegalArgumentException(parameter
                        + " is not a supported exploration strategy");
            }
        }
    
        /** Returns the strategy corresponding to a given strategy name. */
        protected ScenarioHandler getStrategy(String name) {
            return strategies.get(name);
        }
    
        /**
         * Adds a given strategy to the available strategies.
         */
        protected void addStrategy(ScenarioHandler strategy) {
            strategies.put(strategy.getName(), strategy);
            maxNameLength = Math.max(maxNameLength, strategy.getName().length());
        }
    
        /** Length of the longest strategy name. */
        private int maxNameLength = 0;
    
        /** A mapping from strategy names (as appearing in the options) to strategies. */
        private final Map<String, ScenarioHandler> strategies = new TreeMap<String, ScenarioHandler>();
    
        /** The strategy determined by the parser. */
        private ScenarioHandler parsedStrategy;
    
        /** String description of the condition for a conditional strategy. */
        private String parsedCondition;

        /** Switch indicating if the condition was negated. */
        private boolean parsedNegated;
        
        /** Switch indicating if the condition was negated. */
        private RuleList parsedProgram;
    }
    
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
}