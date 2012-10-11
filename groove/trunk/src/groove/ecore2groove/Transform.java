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

import groove.algebra.AlgebraFamily;
import groove.graph.DefaultGraph;
import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;
import groove.trans.ResourceKind;
import groove.trans.SystemProperties;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Class with main method to start the transformation of Ecore to GROOVE
 * or GROOVE to Ecore.
 * @author Stefan Teijgeler
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
        System.out.println("Ecore to GROOVE converter tool.\n"
            + "Usage. There are two usage modes:\n"
            + "- Ecore2Groove <Ecore model> <Ecore instance models> <grammar>\n"
            + "    Converts from Ecore to GROOVE. The arguments are:\n"
            + "      * <Ecore model> - the Ecore model file\n"
            + "      * <Ecore instance models> - any number of instances of the specified Ecore model\n"
            + "      * <grammar> - the GROOVE grammar to be created, must be a directory that ends with .gps\n\n"
            + "- Ecore2Groove <grammar> <Ecore model> <Ecore instance models destination>\n"
            + "    Converts from GROOVE to Ecore. The arguments are:\n"
            + "      * <grammar> - the GROOVE grammar to be read, must be directory that ends with .gps\n"
            + "      * <Ecore model> - the Ecore model file\n"
            + "      * <Ecore instance models destination> - directory where to put generated instance models");
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
        long total = new Date().getTime();

        String grammarLoc = args[0];
        String modelLoc = args[1];
        String instancesLoc = args[2];

        ModelHandler mh = new ModelHandler(modelLoc);
        System.out.println("Loaded Ecore model: " + mh.getModelName() + " ("
            + (new Date().getTime() - start) + " ms)"); // print duration

        // Create a GROOVE file store
        start = new Date().getTime();

        File f = new File(grammarLoc);
        SystemStore grammar = null;
        if (f.exists() && f.isDirectory() && f.canRead()) {
            grammar = SystemStoreFactory.newStore(f, true);
            grammar.reload(); // reload to initialize
        } else {
            System.out.println(grammarLoc
                + " does not exist, is not a directory or cannot be read!");
            System.exit(1);
        }
        System.out.println("Loaded or created graph grammar: " + f.getName()
            + " (" + (new Date().getTime() - start) + " ms)"); //print duration

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
        Map<String,AspectGraph> hostMap = grammar.getGraphs(ResourceKind.HOST);
        for (String graphName : hostMap.keySet()) {
            start = new Date().getTime();
            AspectGraph instanceGraph = hostMap.get(graphName);
            InstanceModelRep im = new InstanceModelRep(mh, instanceGraph);
            mh.saveModel(im.getInstanceModel(), instancesLoc + File.separator
                + graphName);
            System.out.println("Created instance model: " + graphName + " ("
                + (new Date().getTime() - start) + " ms)");
        }

        System.out.println("\nTotal: " + (new Date().getTime() - total) + " ms");
    }

    /**
     * Transforms an Ecore model and instance models to a GROOVE grammar
     * @param args arguments used for transformation
     * @require <tt>args[0] == ecoreModelLocation
     * 				args[2 -> n-1] == instanceModels
     * 				args[n] == grammarLocation</tt>
     */
    private static void toGROOVE(String[] args) throws IOException {
        long total = new Date().getTime();

        // Initialize ModelHandler
        long start = new Date().getTime(); // timing
        String modelLoc = args[0];
        ModelHandler mh = new ModelHandler(modelLoc);
        System.out.println("Loaded Ecore model: " + mh.getModelName() + " ("
            + (new Date().getTime() - start) + " ms)"); // print duration

        // Create TypeGraph
        start = new Date().getTime(); // timing
        String modelName = mh.getModelName();
        // Set info about how to store type graph
        if (modelName.equals("EcoreTypes")) {
            modelName = modelName + "_";
        }
        TypeGraphRep tgr = new TypeGraphRep(modelName, mh);
        System.out.println("Created type graphs: " + modelName + " ("
            + (new Date().getTime() - start) + " ms)"); //print duration

        // Create or load graph grammar
        start = new Date().getTime(); // timing
        File f = new File(args[args.length - 1]);
        SystemStore grammar = SystemStoreFactory.newStore(f, true);
        grammar.reload(); // reload to initialize
        System.out.println("Loaded or created graph grammar: " + f.getName()
            + " (" + (new Date().getTime() - start) + " ms)"); //print duration

        // Get type graphs to store
        AspectGraph atg = AspectGraph.newInstance(tgr.getTypeGraph());
        AspectGraph ecoreatg = AspectGraph.newInstance(tgr.getEcoreTypeGraph());

        atg.getInfo().setFile(f + File.separator + modelName + ".gty");
        ecoreatg.getInfo().setFile(f + File.separator + "EcoreTypes.gty");

        // Store type graphs, but first delete the old ones
        Set<String> typeGraphsToDelete = new HashSet<String>();
        for (String graphName : grammar.getGraphs(ResourceKind.TYPE).keySet()) {
            typeGraphsToDelete.add(graphName);
        }
        for (String graphName : typeGraphsToDelete) {
            grammar.deleteGraphs(ResourceKind.TYPE,
                Collections.singleton(graphName));
        }
        grammar.putGraphs(ResourceKind.TYPE, Arrays.asList(atg, ecoreatg));

        // Set grammar properties
        SystemProperties sp = new SystemProperties();
        List<String> typeNames = new Vector<String>();
        typeNames.add(modelName);
        typeNames.add("EcoreTypes");
        sp.setActiveNames(ResourceKind.TYPE, typeNames);
        if (mh.isBigAlgebra()) {
            sp.setAlgebraFamily(AlgebraFamily.BIG);
        }
        grammar.putProperties(sp);

        // Delete all former constraints since we are remaking them
        // and old ones need to go
        Set<String> rulesToDelete = new HashSet<String>();
        for (String ruleName : grammar.getGraphs(ResourceKind.RULE).keySet()) {
            if (ruleName.startsWith("constraint")) {
                rulesToDelete.add(ruleName);
            }
        }
        grammar.deleteGraphs(ResourceKind.RULE, rulesToDelete);

        // Now create the required constraint rules and add them to the grammar
        start = new Date().getTime();
        int number = 0;
        ConstraintRules constraints = new ConstraintRules(mh);
        System.out.println("Created constraint rules ("
            + (new Date().getTime() - start) + " ms)");

        start = new Date().getTime();
        Set<AspectGraph> rules = new HashSet<AspectGraph>();
        for (DefaultGraph constraintRule : constraints.getConstraints()) {
            AspectGraph arg;
            try {
                arg = AspectGraph.newInstance(constraintRule);
            } catch (Exception e) {
                System.out.println("Error with: " + constraintRule.getName());
                e.printStackTrace();
                continue;
            }
            String name = constraintRule.getName();
            arg.getInfo().setFile(f + File.separator + name + ".gty");
            arg.getInfo().getProperties(true).setPriority(50);
            rules.add(arg);

            number++;
        }
        grammar.putGraphs(ResourceKind.RULE, rules);
        System.out.println("Stored constraint rules: " + number + " ("
            + (new Date().getTime() - start) + " ms)");

        // Load instance models and create instance graph representations
        Set<AspectGraph> graphs = new HashSet<AspectGraph>();
        for (int i = 1; i < args.length - 1; i++) {
            start = new Date().getTime();

            String instanceLoc = args[i];
            String instanceName =
                instanceLoc.substring(instanceLoc.lastIndexOf(File.separatorChar) + 1);

            mh.loadInstance(instanceLoc);
            System.out.println("Loaded instance model: " + instanceName + " ("
                + (new Date().getTime() - start) + " ms)");

            start = new Date().getTime();
            InstanceGraphRep igr = new InstanceGraphRep(instanceName, mh);
            System.out.println("Created instance graph: " + instanceName + " ("
                + (new Date().getTime() - start) + " ms)");

            AspectGraph aig = AspectGraph.newInstance(igr.getInstanceGraph());

            // Set info about how to store the instance graph and then store it
            aig.getInfo().setFile(f + File.separator + instanceName);
            graphs.add(aig);
        }
        grammar.putGraphs(ResourceKind.HOST, graphs);

        System.out.println("\nTotal: " + (new Date().getTime() - total) + " ms");
    }
}
