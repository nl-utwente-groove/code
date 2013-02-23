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

import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.grammar.model.FormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class to convert pattern shapes to GXL format and back.
 * 
 * @author Eduardo Zambon
 */
public final class PatternShapeGxl {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Marshaller/unmarshaller. */
    private final PatternShapeJaxbGxlIO io;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public PatternShapeGxl(TypeGraph typeGraph) {
        // Private to avoid object creation. Use getInstance() method.
        this.io = new PatternShapeJaxbGxlIO(typeGraph);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Loads a pattern shape graph from the given file. */
    public PatternShape unmarshalPatternShape(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            PatternShape result = this.io.loadGraph(in);
            assert result.isWellDefined();
            return result;
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tries to load a pattern shape graph from the given file. May return null. */
    public PatternShape loadPatternShape(File file) {
        PatternShape result = null;
        try {
            result = unmarshalPatternShape(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Saves the given shape into the given file. */
    public void marshalPatternShape(PatternShape shape, File file)
        throws IOException {
        OutputStream out = new FileOutputStream(file);
        this.io.saveGraph(shape, out);
    }

    /** Tries to save the given shape to the given file. May fail silently. */
    public void saveShape(PatternShape shape, File file) {
        try {
            marshalPatternShape(shape, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}