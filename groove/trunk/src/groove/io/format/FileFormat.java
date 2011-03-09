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
package groove.io.format;

import groove.graph.Graph;
import groove.gui.jgraph.GraphJGraph;
import groove.io.ExtensionFilter;

import java.io.File;
import java.io.IOException;

/**
 * @author Eduardo Zambon
 */
public interface FileFormat<G extends Graph<?,?>> {

    public String getDescription();

    public String getExtension();

    public boolean isAcceptDir();

    public ExtensionFilter getFilter();

    public void load(G graph, String fileName) throws IOException;

    public void load(G graph, File file) throws IOException;

    public void save(G graph, String fileName) throws IOException;

    public void save(G graph, File file) throws IOException;

    public void save(GraphJGraph jGraph, File file) throws IOException;

}
