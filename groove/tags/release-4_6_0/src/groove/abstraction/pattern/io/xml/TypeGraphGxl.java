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
package groove.abstraction.pattern.io.xml;

import groove.abstraction.pattern.shape.TypeGraph;

import java.io.File;
import java.io.IOException;

/**
 * Class to load pattern type graphs from GXL files.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraphGxl {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    private static final TypeGraphGxl instance = new TypeGraphGxl();

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns the singleton instance of this class. */
    public static TypeGraphGxl getInstance() {
        return instance;
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Marshaller/unmarshaller. */
    private final TypeGraphJaxbGxlIO io;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private TypeGraphGxl() {
        // Private to avoid object creation. Use getInstance() method.
        this.io = TypeGraphJaxbGxlIO.getInstance();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Loads a pattern type graph from the given file. */
    public TypeGraph unmarshalTypeGraph(File file) throws IOException {
        return this.io.unmarshalTypeGraph(file);
    }

    /** Tries to load a pattern type graph from the given file. May return null. */
    public TypeGraph loadTypeGraph(File file) {
        TypeGraph result = null;
        try {
            result = unmarshalTypeGraph(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}