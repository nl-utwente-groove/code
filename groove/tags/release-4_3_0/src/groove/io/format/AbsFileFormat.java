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
import groove.io.ExtensionFilter;

import java.io.IOException;

/**
 * @author Eduardo Zambon
 */
public abstract class AbsFileFormat implements FileFormat {

    private final String description;
    private final String extension;
    private final boolean acceptDir;
    private final ExtensionFilter filter;

    AbsFileFormat(String description, String extension, boolean acceptDir) {
        this.description = description;
        this.extension = extension;
        this.acceptDir = acceptDir;
        this.filter = new ExtensionFilter(description, extension, acceptDir);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getExtension() {
        return this.extension;
    }

    @Override
    public boolean isAcceptDir() {
        return this.acceptDir;
    }

    @Override
    public ExtensionFilter getFilter() {
        return this.filter;
    }

    @Override
    abstract public Graph<?,?> load(String fileName);

    @Override
    abstract public void save(Graph<?,?> graph, String fileName)
        throws IOException;
}
