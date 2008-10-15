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
 * $Id: ViewXml.java,v 1.2 2008-01-30 09:33:42 iovka Exp $
 */
package groove.io;

import groove.view.View;

import java.io.File;
import java.io.IOException;

/**
 * Interface for the conversion of views to and from XML documents. To be
 * implemented for particular XML formats.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface ViewXml<V extends View<?>> {
    /**
     * Writes a view to a file, in XML format.
     * @param view the graph to be marshalled
     * @param file the output file
     * @throws IOException if an error occurred during file output
     */
    public void marshal(V view, File file) throws IOException;

    /**
     * Converts an XML formatted file into a graph, and returns the graph.
     * Convenience method for <code>unmarshal(file, null)</code>.
     * @param file the file to be read from
     * @return the unmarshalled graph
     * @throws IOException if an error occurred during file input
     */
    public V unmarshal(File file) throws IOException;
}
