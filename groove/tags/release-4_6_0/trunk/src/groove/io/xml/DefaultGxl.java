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
 * $Id: DefaultGxl.java,v 1.21 2007-12-03 08:55:18 rensink Exp $
 */
package groove.io.xml;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;

/**
 * Class to convert graphs to GXL format and back. Currently the conversion only
 * supports binary edges. This class is implemented using data binding.
 * @author Arend Rensink
 * @version $Revision: 2973 $
 */
public class DefaultGxl extends
        AbstractGxl<DefaultNode,DefaultEdge,DefaultGraph> {

    /** Returns the singleton instance of this class. */
    public static DefaultGxl getInstance() {
        return INSTANCE;
    }

    private DefaultGxl() {
        // Private to avoid object creation. Use getInstance() method.
    }

    /** Marshaller/unmarshaller. */
    static private final DefaultJaxbGxlIO io = DefaultJaxbGxlIO.getInstance();

    private static final DefaultGxl INSTANCE = new DefaultGxl();

    @Override
    protected GxlIO<DefaultNode,DefaultEdge> getIO() {
        return io;
    }

}