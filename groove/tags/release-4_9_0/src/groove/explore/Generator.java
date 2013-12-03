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
package groove.explore;

import groove.explore.encode.Serialized;
import groove.explore.encode.TemplateList;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.explore.util.ExplorationReporter;
import groove.explore.util.GenerateProgressListener;
import groove.grammar.Grammar;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.model.GrammarModel;
import groove.io.FileType;
import groove.lts.GTS;
import groove.util.Groove;
import groove.util.cli.GrooveCmdLineParser;
import groove.util.cli.GrooveCmdLineTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.kohsuke.args4j.CmdLineException;

/**
 * New command-line Generator class, using the Agrs4J library. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class Generator {
    /**
     * Constructs the generator and processes the command-line arguments.
     * @throws CmdLineException if any error was found in the command-line arguments
     */
    public Generator(String... args) throws CmdLineException {
        this.options = new GeneratorOptions();
        this.parser = new GrooveCmdLineParser("Generator", this.options);
        this.parser.parseArgument(args);
    }

    /**
     * Runs the state space generation process.
     * @throws Exception if anything goes wrong during generation.
     */
    public GTS start() throws Exception {
        if (this.options.isHelp()) {
            this.parser.printHelp();
            return null;
        } else {
            return run();
        }
    }

    /**
     * The processing phase of state space generation.
     */
    private GTS run() throws Exception {
        GrammarModel grammarModel = computeGrammarModel();
        Grammar grammar = grammarModel.toGrammar();
        grammar.setFixed();
        GTS gts = new GTS(grammar);
        if (!getVerbosity().isLow()) {
            gts.addLTSListener(new GenerateProgressListener());
        }
        Exploration exploration = computeExploration(grammarModel);
        for (ExplorationReporter reporter : getReporters()) {
            reporter.start(exploration, gts);
        }
        exploration.play(gts, null);
        gts.setResult(exploration.getResult());
        for (ExplorationReporter reporter : getReporters()) {
            reporter.report();
        }
        return gts;
    }

    /**
     * Creates and returns a grammar model, using the options
     * to find the location and other settings.
     */
    private GrammarModel computeGrammarModel() throws Exception {
        GrammarModel result;
        Observer loadObserver = new Observer() {
            public void update(Observable o, Object arg) {
                if (!getVerbosity().isLow()) {
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

        File grammarFile = this.options.getGrammar();
        result = GrammarModel.newInstance(grammarFile);
        if (this.options.getStartGraphs() != null) {
            List<String> startGraphs =
                new ArrayList<String>(this.options.getStartGraphs());
            AspectGraph externalStartGraph =
                computeStartGraph(grammarFile, startGraphs);
            if (externalStartGraph != null) {
                result.setStartGraph(externalStartGraph);
            }
        }
        result.getStore().addObserver(loadObserver);
        return result;
    }

    private AspectGraph computeStartGraph(File grammarFile,
            List<String> startGraphs) throws IOException {
        AspectGraph result = null;
        if (!startGraphs.isEmpty()) {
            List<AspectGraph> graphs = new ArrayList<AspectGraph>();
            for (String startGraphName : startGraphs) {
                startGraphName =
                    FileType.STATE_FILTER.addExtension(startGraphName);
                File startGraphFile = new File(grammarFile, startGraphName);
                if (!startGraphFile.exists()) {
                    startGraphFile = new File(startGraphName);
                }
                if (!startGraphFile.exists()) {
                    throw new IOException("Can't find start graph "
                        + startGraphName);
                }
                graphs.add(GraphConverter.toAspect(Groove.loadGraph(startGraphFile)));
            }
            result = AspectGraph.mergeGraphs(graphs);
        }
        return result;
    }

    /**
     * Compute the exploration out of the command line options.
     * Uses the default exploration for components that were not specified.
     */
    private Exploration computeExploration(GrammarModel grammarModel) {
        Exploration result = grammarModel.getDefaultExploration();
        if (result == null) {
            result = new Exploration();
        }

        Serialized strategy;
        if (this.options.hasStrategy()) {
            TemplateList<Strategy> enumerator =
                StrategyEnumerator.newInstance();
            strategy = enumerator.parseCommandline(this.options.getStrategy());
        } else {
            strategy = result.getStrategy();
        }

        Serialized acceptor;
        if (this.options.hasAcceptor()) {
            TemplateList<Acceptor> enumerator =
                AcceptorEnumerator.newInstance();
            acceptor = enumerator.parseCommandline(this.options.getAcceptor());
        } else {
            acceptor = result.getAcceptor();
        }

        int nrResults = this.options.getResultCount();

        return new Exploration(strategy, acceptor, nrResults);
    }

    /** Convenience methods to return the verbosity level from the generator options. */
    private Verbosity getVerbosity() {
        return this.options.getVerbosity();
    }

    private List<ExplorationReporter> getReporters() {
        return this.options.getReporters();
    }

    /** The command-line parser. */
    private final GrooveCmdLineParser parser;
    /** The generator options. */
    private final GeneratorOptions options;

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * This will always exit with {@link System#exit(int)}; see
     * {@link #execute(String[])} for programmatic use.
     * @param args generator options, grammar and start graph name
     */
    static public void main(String[] args) {
        GrooveCmdLineTool.tryExecute(Generator.class, args);
    }

    /**
     * Loads a graph grammar, and returns the generated transition system.
     * @param args generator options and arguments
     * @return the generated transition system
     * @throws Exception if any error occurred that prevented the GTS from being fully generated
     */
    static public GTS execute(String[] args) throws Exception {
        staticGTS = new Generator(args).start();
        return staticGTS;
    }

    /** Returns the most recently generated GTS. */
    static public GTS getGts() {
        return staticGTS;
    }

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static GTS staticGTS;
}
