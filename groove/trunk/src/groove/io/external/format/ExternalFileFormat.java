/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.io.external.format;

import groove.graph.Graph;
import groove.gui.jgraph.JGraph;
import groove.io.ExtensionFilter;

import java.io.File;
import java.io.IOException;

/**
 * A common interface for external (non-native to Groove) file formats.
 * The interface defines load and save methods but formats that can only be
 * imported or exported will throw an UnsupportedOperationException.
 * 
 * @author Eduardo Zambon
 */
public interface ExternalFileFormat<G extends Graph<?,?>> {

    /** Returns the extension filter associated with this format. */
    public ExtensionFilter getFilter();

    /**
     * Loads a graph from a file.
     * @param graph the graph object to load into.
     * @param fileName the file name to read.
     */
    public void load(G graph, String fileName) throws IOException;

    /**
     * Loads a graph from a file.
     * @param graph the graph object to load into.
     * @param file the file object to read.
     */
    public void load(G graph, File file) throws IOException;

    /**
     * Saves a graph into a file.
     * @param graph the graph object to be saved.
     * @param fileName the file name to write.
     */
    public void save(G graph, String fileName) throws IOException;

    /**
     * Saves a graph into a file.
     * @param graph the graph object to be saved.
     * @param file the file object to write.
     */
    public void save(G graph, File file) throws IOException;

    /**
     * Saves a j-graph into a file.
     * @param jGraph the j-graph object to be saved.
     * @param file the file object to write.
     */
    public void save(JGraph<G> jGraph, File file) throws IOException;

}
