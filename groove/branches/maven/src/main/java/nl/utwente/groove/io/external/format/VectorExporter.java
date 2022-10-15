/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package nl.utwente.groove.io.external.format;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.external.util.GraphToVector;
import nl.utwente.groove.util.Groove;

/**
 * Class that implements saving graphs as vectorised EPS (Embedded PostScript) or PDF images.
 * Loading in this format is unsupported.
 *
 * @author Arend Rensink / Harold Bruintjes
 */
public class VectorExporter extends AbstractExporter {
    /** Private constructor for the singleton instance. */
    private VectorExporter() {
        super(Kind.JGRAPH);
        addFormat(FileType.PDF, Groove.GROOVE_BASE + ".io.external.util.GraphToPDF");
        addFormat(FileType.EPS, Groove.GROOVE_BASE + ".io.external.util.GraphToEPS");
    }

    private void addFormat(FileType fileType, String vectorClassName) {
        register(fileType);
        this.formats.put(fileType, getGraphToVector(vectorClassName));
    }

    private GraphToVector getGraphToVector(String vectorClassName) {
        GraphToVector result = null;
        try {
            @SuppressWarnings("unchecked") Class<GraphToVector> cls =
                (Class<GraphToVector>) Class.forName(vectorClassName);
            result = cls.getConstructor()
                .newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
            | SecurityException e) {
            // Just return
        }
        return result;
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        JGraph<?> jGraph = exportable.getJGraph();
        this.formats.get(fileType)
            .renderGraph(jGraph, file);
    }

    private final Map<FileType,GraphToVector> formats = new EnumMap<>(FileType.class);

    /** Returns the singleton instance of this class. */
    public static final VectorExporter getInstance() {
        return instance;
    }

    private static final VectorExporter instance = new VectorExporter();
}
