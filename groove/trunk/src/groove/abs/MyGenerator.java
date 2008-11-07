/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.abs;

import groove.abs.lts.AGTS;
import groove.explore.Scenario;
import groove.explore.ScenarioFactory;
import groove.explore.result.Acceptor;
import groove.explore.strategy.BranchingStrategy;
import groove.io.FileGps;
import groove.lts.LTSGraph;
import groove.trans.GraphGrammar;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Performs a full abstract simulation of a grammar given as parameter and saves
 * the resulting LTS. Also requires abstraction options as parameters.
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public class MyGenerator {

    private GraphGrammar grammar;
    private AGTS gts;
    private final Abstraction.Parameters parameters;
    private long startTime;
    private long endTime;

    private MyGenerator(Abstraction.Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Loads a grammar from a given grammar location and a start graph.
     * 
     * @param grammarFile
     * @param startGraph
     */
    private void loadGrammar(String grammarFile, String startGraph) {
        try {
            this.grammar =
                (new FileGps(false)).unmarshal(new File(grammarFile), startGraph).toGrammar();
        } catch (FormatException e1) {
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
    }

    private void exploreGrammar() {
        this.gts = new AGTS(this.grammar, this.parameters);

        Scenario scenario =
            ScenarioFactory.getScenario(new BranchingStrategy(),
                new Acceptor(), "Explores the full state space.",
                "Full exploration (branching, aliasing)");
        scenario.prepare(this.gts);
        this.startTime = System.currentTimeMillis();
        scenario.play();
        this.endTime = System.currentTimeMillis();
        if (scenario.isInterrupted()) {
            new Exception().printStackTrace();
        }
    }

    private void saveResult(AGTS gts, String outputFile, String grammarName) {

        String stamp = Long.toString(System.currentTimeMillis());
        outputFile = outputFile + stamp;

        try {
            if (!Groove.exportGraph(new LTSGraph(gts), outputFile + ".gxl")) {
                File outputFileInfo = new File(outputFile + ".info");
                BufferedWriter writer =
                    new BufferedWriter(new FileWriter(outputFileInfo));
                writer.write("Grammar     : " + grammarName + "\n");
                writer.write("States      : "
                    + Integer.toString(gts.nodeCount()) + "\n");
                writer.write("Transitions : "
                    + Integer.toString(gts.edgeCount()) + "\n");
                writer.write("Inv. trans. : "
                    + Integer.toString(gts.invalidTransitionsCount()) + "\n");
                writer.write("Time        : "
                    + Long.toString(this.endTime - this.startTime) + "\n");
                writer.write("\n");
                writer.write("Radius      : "
                    + Integer.toString(this.parameters.radius) + "\n");
                writer.write("Precision   : "
                    + Integer.toString(this.parameters.precision) + "\n");
                writer.write("Max inc.    : "
                    + Integer.toString(this.parameters.maxIncidence) + "\n");
                Groove.saveGraph(new LTSGraph(gts), outputFile + ".gxl");
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void generate(String grammarFile, String startGraph,
            String outputFile) {
        loadGrammar(grammarFile, startGraph);
        StatisticsThread statThread = new StatisticsThread();
        statThread.start();
        exploreGrammar();
        statThread.interrupt();
        saveResult(this.gts, outputFile, grammarFile);
    }

    class StatisticsThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(10000);
                    System.out.println("States "
                        + MyGenerator.this.gts.nodeCount() + " ("
                        + MyGenerator.this.gts.openStateCount() + " open)"
                        + ", Transitions " + MyGenerator.this.gts.edgeCount());
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

    /**
     * Takes a graph grammar as unique parameter
     * 
     * @param args
     */
    public static void main(String args[]) {

        String usage =
            "Usage : MyGenerator <grammar> <startGraph> -r<radius> -p<precision> -m<max_incidence> -o<output_file_name>.";
        if (args.length != 6) {
            System.err.println("Error 0");
            System.err.println(usage);
            System.exit(1);
        }

        String grammarFile = args[0];
        String startGraph = args[1];

        String radiusParam = args[2];
        String precisionParam = args[3];
        String maxIncidenceParam = args[4];
        String outputFile = args[5];

        if (!radiusParam.startsWith("-r") || !precisionParam.startsWith("-p")
            || !maxIncidenceParam.startsWith("-m")
            || !outputFile.startsWith("-o")) {
            System.err.println("Error 1");
            System.err.println(usage);
            System.exit(1);
        }

        int radius = Integer.parseInt(radiusParam.substring(2));
        int precision = Integer.parseInt(precisionParam.substring(2));
        int maxIncidence = Integer.parseInt(maxIncidenceParam.substring(2));
        outputFile = outputFile.substring(2);

        Abstraction.Parameters parameters =
            new Abstraction.Parameters(radius, precision, maxIncidence);

        MyGenerator generator = new MyGenerator(parameters);
        generator.generate(grammarFile, startGraph, outputFile);
    }
}
