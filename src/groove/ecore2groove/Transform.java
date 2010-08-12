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
package groove.ecore2groove;

import groove.graph.DefaultGraph;
import groove.graph.GraphInfo;
import groove.io.SystemStore;
import groove.io.SystemStoreFactory;
import groove.trans.RuleName;
import groove.util.Groove;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Class with main method to start the transformation of Ecore to GROOVE
 * or GROOVE to Ecore.
 * @author Stefan Teijgeler
 * @version
 */
public class Transform {

    /**
     * Main method to start transformation
     * @param args command-line arguments
     */
    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            exitTransform();
        } else if (args[args.length - 1].endsWith(".gps")) {
            // If last argument ends with .gps, we transform to GROOVE
            toGROOVE(args);
        } else if (args[0].endsWith(".gps") && args.length == 3) {
            // If first argument ends with .gps we transform from GROOVE
            toEcore(args);
        } else {
            exitTransform();
        }

    }

    /**
     * Method to exit and output how to use the tool 
     */
    private static void exitTransform() {

        System.out.println("Usage: \n"
            + "groove.ecore2groove.jar <Ecore model> "
            + "<Ecore instance models> <grammar>\n"
            + "groove.ecore2groove.jar <grammar> <Ecore model> "
            + "<Ecore instance models location>\n" + "\n"
            + "<Ecore model> - Ecore model\n"
            + "<Ecore instance models> - Any number of "
            + "instances of the specified Ecore model\n"
            + "<grammar> - GROOVE grammar to create or read, "
            + "must be directory that ends with .gps\n"
            + "<Ecore instance models location> - Directory "
            + "where to put generated instance models");
        System.exit(1);

    }

    /**
     * Transforms a GROOVE grammar and an Ecore model to instance models
     * @param args arguments used for transformation
     * @require <tt>args[0] == grammarLocation, args[1] == ecoreModelLocation
     * 				args[2] == instanceDirectory</tt>
     */
    private static void toEcore(String[] args) throws IOException {
        long start = new Date().getTime();

        String grammarLoc = args[0];
        String modelLoc = args[1];
        String instancesLoc = args[2];

        ModelHandler mh = new ModelHandler(modelLoc);

        System.out.println("Loaded " + modelLoc + " ("
            + (new Date().getTime() - start) + " ms)");

        // Create a GROOVE file store
        start = new Date().getTime();

        File f = new File(grammarLoc);
        SystemStore grammar = null;
        if (f.exists() && f.isDirectory() && f.canRead()) {
            grammar = SystemStoreFactory.newStore(f, true);
            grammar.reload(); // reload to initialize
            System.out.println("Loaded " + grammarLoc + " ("
                + (new Date().getTime() - start) + " ms)");
        } else {
            System.out.println(grammarLoc
                + " does not exist, is not a directory or cannot be read!");
            System.exit(1);
        }

        // Make sure the output directory exists and is writable
        File f2 = new File(instancesLoc);
        if (!f2.exists()) {
            if (!f2.mkdir()) {
                System.out.println("Could not create directory " + instancesLoc
                    + "!");
                System.exit(1);
            }
        } else if (!f2.isDirectory() || !f2.canWrite()) {
            System.out.println(instancesLoc
                + " is not directory or is not writable!");
            System.exit(1);
        }

        // Create an instance model for each graph in the grammar that was
        // loaded
        for (String graphName : grammar.getGraphs().keySet()) {
            start = new Date().getTime();
            AspectGraph instanceGraph = grammar.getGraphs().get(graphName);
            // System.out.println(instanceGraph);
            InstanceModelRep im = new InstanceModelRep(mh, instanceGraph);
            mh.saveModel(im.getInstanceModel(), instancesLoc + File.separator
                + graphName);
            System.out.println("Stored: " + instancesLoc + File.separator
                + graphName + " (" + (new Date().getTime() - start) + " ms)");
        }

    }

    /**
     * Transforms an Ecore model and instance models to a GROOVE grammar
     * @param args arguments used for transformation
     * @require <tt>args[0] == ecoreModelLocation
     * 				args[2 -> n-1] == instanceModels
     * 				args[n] == grammarLocation</tt>
     */
    private static void toGROOVE(String[] args) throws IOException {
        long start = new Date().getTime();
        long total;

        String modelLoc = args[0];
        ModelHandler mh = new ModelHandler(modelLoc);
        TypeGraphRep tgr = new TypeGraphRep(mh);
        String modelName = mh.getModelName();

        total = new Date().getTime() - start;

        // Create a file store
        start = new Date().getTime();

        File f = new File(args[args.length - 1]);
        SystemStore grammar = SystemStoreFactory.newStore(f, true);
        grammar.reload(); // reload to initialize

        System.out.println("Loaded or created " + args[args.length - 1] + " ("
            + (new Date().getTime() - start) + " ms)");

        // Get type graphs to store
        AspectGraph atg = tgr.getTypeGraph();
        AspectGraph ecoreatg = tgr.getEcoreTypeGraph();

        // Set info about how to store type graph
        if (modelName.equals("EcoreTypes")) {
            modelName = modelName + "_";
        }
        atg.getInfo().setFile(f + File.separator + modelName + ".gty");
        atg.getInfo().setName(modelName);
        ecoreatg.getInfo().setFile(f + File.separator + "EcoreTypes.gty");
        ecoreatg.getInfo().setName("EcoreTypes");

        // Store type graphs
        grammar.putType(atg);
        grammar.putType(ecoreatg);
        List<String> typeNames = new Vector<String>();
        typeNames.add(modelName);
        typeNames.add("EcoreTypes");
        grammar.getProperties().setTypeNames(typeNames);
        grammar.getProperties().setGrooveVersion("4.0.1+");

        printGraph(atg, "Type graph: " + modelName + " (" + total + " ms)");

        // Delete all former constraints since we are remaking them
        // and old ones need to go
        Set<RuleName> rulesToDelete = new HashSet<RuleName>();
        for (RuleName ruleName : grammar.getRules().keySet()) {
            if (ruleName.text().startsWith("constraint")) {
                rulesToDelete.add(ruleName);
            }
        }
        for (RuleName ruleName : rulesToDelete) {
            grammar.deleteRule(ruleName);
        }

        // Now create the required constraint rules and add them to the grammar
        start = new Date().getTime();
        int number = 0;
        ConstraintRules constraints = new ConstraintRules(mh);
        for (DefaultGraph constraintRule : constraints.getConstraints()) {
            // GraphInfo.setRuleRole(constraintRule);
            AspectGraph arg =
                AspectGraph.getFactory().fromPlainGraph(constraintRule);

            String name = constraints.getName(constraintRule);
            arg.getInfo().setFile(f + File.separator + name + ".gty");
            arg.getInfo().setName(name);

            GraphInfo.setRuleRole(arg);
            arg.getInfo().getProperties(true).setPriority(50);
            grammar.putRule(arg);

            printGraph(arg, "Constraint rule: " + name);
            number++;
        }
        System.out.println(number + " constraint rules took "
            + (new Date().getTime() - start) + " ms");

        // Load instance models and create instance graph representations
        for (int i = 1; i < args.length - 1; i++) {
            start = new Date().getTime();

            String instanceLoc = args[i];
            String instanceName =
                instanceLoc.substring(instanceLoc.lastIndexOf(File.separatorChar) + 1);

            mh.loadInstance(instanceLoc);
            InstanceGraphRep igr = new InstanceGraphRep(mh);

            AspectGraph aig = igr.getInstanceGraph();

            // Set info about how to store the instance graph and then store it
            aig.getInfo().setFile(f + File.separator + instanceName);
            aig.getInfo().setName(instanceName);
            aig.getInfo().setRole(Groove.GRAPH_ROLE);

            grammar.putGraph(aig);

            printGraph(aig, "Instance graph: " + instanceName + " ("
                + (new Date().getTime() - start) + " ms)");
        }
    }

    /**
     * Print information of a graph to System.out with header prefixed
     * @param ag The AspectGraph to print 
     * @param header The header information
     */
    private static void printGraph(AspectGraph ag, String header) {
        System.out.println(header);
        // System.out.println(ag);
        // System.out.println(ag.getInfo() + "\n");
    }

}
