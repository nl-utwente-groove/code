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
 * $Id: AbstractXml.java,v 1.1.1.1 2007-03-20 10:05:25 kastenberg Exp $
 */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphFactory;

import java.io.File;
import java.io.IOException;

/**
 * Convenience class that brings down <tt>Xml</tt>'s methods to just two 
 * abstract methods: <tt>marshal(Graph)</tt> and <tt>unmarshal(Document,Graph)</tt>.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public abstract class AbstractXml implements Xml {
//    public void marshal(Graph graph, File file) throws XmlException, IOException {
//        Writer writer = new PrintWriter(new FileWriter(file));
//        marshal(graph, writer);
//        writer.close();
//    }
//
//    public Map unmarshal(File file, Graph graph) throws XmlException, IOException {
//        Reader reader = new BufferedReader(new FileReader(file));
//        Map result = unmarshal(reader, graph);
//        try {
//            reader.close();
//        } catch (IOException e) {
//        }
//        return result;
//    }
//
//    public Graph unmarshal(Reader reader) throws XmlException {
//        Graph result = getGraphFactory().newGraph();
//        unmarshal(reader, result);
//        return result;
//    }

    public Graph unmarshal(File file) throws XmlException, IOException {
        return unmarshal(file, null);
    }

    /**
     * Changes the graph factory used for unmarshalling.
     */
    protected void setGraphFactory(GraphFactory factory) {
        graphFactory = factory;
    }

    /**
     * Returns the graph factory used for unmarshalling.
     */
    protected GraphFactory getGraphFactory() {
        return graphFactory;
    }

    protected GraphFactory graphFactory;
}
