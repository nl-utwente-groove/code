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
package groove.abstraction.neigh.io.xml;

import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.grammar.type.TypeGraph;
import groove.graph.GGraph;
import groove.io.xml.AbstractGxl;
import groove.io.xml.GxlIO;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Class to convert shapes to GXL format and back.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeGxl extends
        AbstractGxl<ShapeNode,ShapeEdge,GGraph<ShapeNode,ShapeEdge>> {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Marshaller/unmarshaller. */
    private final ShapeJaxbGxlIO io;

    private static final ShapeGxl INSTANCE = new ShapeGxl(null);

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns an untyped instance of this class. */
    public static ShapeGxl getInstance() {
        return INSTANCE;
    }

    /** Returns a class instance using a given type graph to create shapes. */
    public static ShapeGxl getInstance(TypeGraph typeGraph) {
        return new ShapeGxl(typeGraph);
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private ShapeGxl(TypeGraph typeGraph) {
        // Private to avoid object creation. Use getInstance() method.
        this.io = ShapeJaxbGxlIO.getInstance(typeGraph);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    protected GxlIO<ShapeNode,ShapeEdge,GGraph<ShapeNode,ShapeEdge>> getIO() {
        return this.io;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Loads a shape from the given URL. */
    public Shape unmarshalShape(URL url) throws IOException {
        return Shape.upcast(this.unmarshalGraph(url));
    }

    /** Loads a shape from the given file. */
    public Shape unmarshalShape(File file) throws IOException {
        return Shape.upcast(this.unmarshalGraph(file));
    }

    /** Saves the given shape into the given file. */
    public void marshalShape(Shape shape, File file) throws IOException {
        this.marshalGraph(shape.downcast(), file);
    }

    /** Tries to load a shape from the given file. May return null. */
    public Shape loadShape(File file) {
        Shape result = null;
        try {
            result = this.unmarshalShape(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Tries to save the given shape to the given file. May fail silently. */
    public void saveShape(Shape shape, File file) {
        try {
            this.marshalShape(shape, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}