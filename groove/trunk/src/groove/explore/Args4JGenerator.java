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
import groove.grammar.Grammar;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.model.GrammarModel;
import groove.io.FileType;
import groove.lts.GTS;
import groove.util.Groove;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * New command-line Generator class, using the Agrs4J library. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class Args4JGenerator {
    /**
     * Constructs the generator and processes the command-line arguments.
     * @throws Exception if any error was found in the command-line arguments
     */
    public Args4JGenerator(String... args) throws Exception {
        this.options = new GeneratorOptions();
        this.parser = new GrooveCmdLineParser(this.options);
        try {
            this.parser.parseArgument(args);
        } catch (Throwable e) {
            // only throw the exception if the help option is not invoked
            // because if it is invoked then only the help message will be printed
            if (!this.options.isHelp()) {
                throw new Exception(String.format("Error: %s%n%s",
                    e.getMessage(), getUsageLine()));
            }
        }
    }

    /**
     * Runs the state space generation process.
     * @throws Exception if anything goes wrong during generation.
     */
    public GTS run() throws Exception {
        if (this.options.isHelp()) {
            printHelp();
            return null;
        } else {
            return generate();
        }
    }

    /**
     * The processing phase of state space generation.
     */
    private GTS generate() throws Exception {
        GrammarModel grammarModel = computeGrammarModel();
        Exploration exploration = computeExploration(grammarModel);
        Grammar grammar = grammarModel.toGrammar();
        grammar.setFixed();
        GTS gts = new GTS(grammar);
        if (!this.options.getVerbosity().isLow()) {
            gts.addLTSListener(new GenerateProgressMonitor());
        }
        for (ExplorationReporter reporter : getReporters()) {
            reporter.start(exploration, gts);
        }
        exploration.play(gts, null);
        gts.setResult(exploration.getResult());
        for (ExplorationReporter reporter : getReporters()) {
            reporter.stop();
        }
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
            strategy = enumerator.parseCommandline(this.options.getAcceptor());
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

    private String getUsageLine() {
        StringBuilder result = new StringBuilder();
        OutputStream stream = new ByteArrayOutputStream();
        this.parser.printSingleLineUsage(stream);
        result.append("Usage: \"Generator");
        result.append(stream.toString());
        result.append("\"");
        return result.toString();
    }

    private void printHelp() {
        System.out.println(getUsageLine());
        System.out.println();
        this.parser.setUsageWidth(100);
        this.parser.printUsage(System.out);
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
     * @param args generator options, grammar and start graph name
     */
    static public void main(String[] args) {
        try {
            generate(args);
        } catch (Throwable e) {
            System.err.println("Generator failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter, and returns the generated transition system.
     * @param args generator options, grammar and start graph name
     * @return the generated transition system
     * @throws Exception if any error occurred that prevented the GTS from being fully generated
     */
    static public GTS generate(String[] args) throws Exception {
        staticGTS = new Args4JGenerator(args).run();
        return staticGTS;
    }

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static GTS staticGTS;

}
