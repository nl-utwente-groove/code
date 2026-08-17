// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
 * $Id$
 */
package nl.utwente.groove.io;

import static nl.utwente.groove.util.io.FileType.GRAMMAR;
import static nl.utwente.groove.util.io.FileType.GXL;
import static nl.utwente.groove.util.io.FileType.STATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.io.graph.GraphIO;
import nl.utwente.groove.io.graph.GxlIO;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.io.FileType;

/**
 * Façade for loading and saving graphs and grammars, with the associated
 * default resource names, and simple timestamped tracing for the GROOVE tools.
 * @version $Revision$
 * @author Arend Rensink
 */
@NonNullByDefault
public class Groove {
    /** Default name for the start graph. */
    public static final String DEFAULT_START_GRAPH_NAME = "start";
    /** Default name for control files. */
    public static final String DEFAULT_CONTROL_NAME = "control";
    /** Default name for the type graph */
    public static final String DEFAULT_TYPE_NAME = "type";
    /** Default name for property files. */
    public static final String PROPERTY_NAME = "system";

    /**
     * Attempts to load in a graph from a given <tt>.gst</tt> file and return
     * it. Tries out the <tt>.gxl</tt> and <tt>.gst</tt> extensions if the
     * filename has no extension.
     * @param filename the name of the file to load the graph from
     * @return the graph contained in <code>filename</code>, or
     *         <code>null</code> if no file with this name can be found
     * @throws IOException if <code>filename</code> does not exist or is wrongly
     *         formatted
     */
    static public PlainGraph loadGraph(String filename) throws IOException {
        // attempt to find the intended file
        File file = new File(filename);
        if (GXL.hasExtension(file) || STATE.hasExtension(file)) {
            file = new File(GXL.addExtension(filename));
            if (!file.exists()) {
                file = new File(STATE.addExtension(filename));
            }
        }
        return loadGraph(file);
    }

    /**
     * Attempts to load in a graph from a file.
     * @param file file to load the graph from
     * @return the graph contained in <code>file</code>, or <code>null</code> if
     *         the file does not exist
     * @throws IOException if <code>file</code> cannot be parsed as a graph
     */
    static public PlainGraph loadGraph(File file) throws IOException {
        GraphIO<?> io = null;
        var type = FileType.getType(file);
        if (type != null) {
            var typeIO = GraphIO.instance(type);
            if (typeIO != null && typeIO.canLoad()) {
                io = typeIO;
            }
        }
        if (io == null) {
            io = GxlIO.instance();
        }
        try (var stream = new FileInputStream(file);) {
            return io.loadPlainGraph(stream);
        }
    }

    /**
     * Attempts to save a graph to a file with a given name. Adds the
     * <tt>.gxl</tt> extension if the file has no extension.
     * @param graph the graph to be saved
     * @param filename the intended filename
     * @throws IOException if saving ran into problems
     */
    static public File saveGraph(Graph graph, String filename) throws IOException {
        if (!STATE.hasExtension(filename)) {
            filename = GXL.addExtension(filename);
        }
        File file = new File(filename);
        saveGraph(graph, file);
        return file;
    }

    /**
     * Attempts to save a graph to a given file.
     * @param graph the graph to be saved
     * @param file the intended file
     * @throws IOException if saving ran into problems
     */
    static public void saveGraph(Graph graph, File file) throws IOException {
        GxlIO.instance().saveGraph(graph, file);
    }

    /**
     * Attempts to load in a graph grammar from a given <tt>.gps</tt> directory,
     * and returns it. Adds the <tt>.gps</tt> extension if the file has no
     * extension.
     * @param dirname the name of the directory to load the graph grammar from
     * @throws IOException if <code>dirname</code> does not exist or is wrongly
     *         formatted
     */
    static public GrammarModel loadGrammar(String dirname) throws IOException {
        File dir = new File(GRAMMAR.addExtension(dirname));
        return SystemStore.newGrammar(dir);
    }

    /**
     * Gives the current time as a number-formatted string with given
     * parameters.
     * @param lossfactor the multiple of milliseconds by which time should be
     *        measured; i.e. a value of 10 means measure by centiseconds, 100
     *        means by deciseconds
     * @param modulo the multiple of the measured time unit (after taking loss
     *        into account) above which time should be cut off
     * @param fraction the fraction of the measured time that should appear
     *        after the decimal point
     */
    public static String currentTime(int lossfactor, int modulo, int fraction) {
        long time = (System.currentTimeMillis() / lossfactor);
        StringBuffer res = new StringBuffer();
        while (modulo > 1) {
            res
                .insert(0, time > 0
                    ? "" + time % 10
                    : "");
            time /= 10;
            fraction /= 10;
            if (fraction == 1) {
                res.insert(0, ".");
            }
            modulo /= 10;
        }
        return res.toString();
    }

    /**
     * Gives the current time as a number-formatted string of the form "ss.cc",
     * where ss are seconds and cc centiseconds.
     */
    public static String currentTime() {
        return currentTime(10, 10000, 100);
    }

    /**
     * Prints a timestamped message.
     */
    public static void message(Object obj) {
        System.out.println(currentTime() + ": " + obj);
    }

    /**
     * Prints a timestamped message regarding the time of starting a given
     * method.
     */
    public static void startMessage(String method) {
        message("Starting " + method);
    }

    /**
     * Prints a timestamped message regarding the time of ending a given method.
     */
    public static void endMessage(String method) {
        message("Ending " + method);
    }

    private Groove() {
        // private constructor to prevent instantiation of this class
    }
}
