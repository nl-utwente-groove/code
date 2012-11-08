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
package groove.io.external.format;

import groove.gui.jgraph.GraphJGraph;
import groove.io.FileType;
import groove.io.external.AbstractFormatExporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.PortException;
import groove.io.external.util.GraphToVector;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** 
 * Class that implements saving graphs as vectorised EPS (Embedded PostScript) or PDF images.
 * Loading in this format is unsupported.
 * 
 * @author Arend Rensink / Harold Bruintjes
 */
public class VectorExporter extends AbstractFormatExporter {
    /** Private constructor for the singleton instance. */
    private VectorExporter() {
        this.formats = new LinkedHashMap<Format,GraphToVector>();

        Format pdfformat = new Format(this, FileType.PDF);
        GraphToVector pdfObject =
            getGraphToVector("groove.io.external.util.GraphToPDF");
        if (pdfObject != null) {
            this.formats.put(pdfformat, pdfObject);
        }

        Format epsformat = new Format(this, FileType.EPS);
        GraphToVector epsObject =
            getGraphToVector("groove.io.external.util.GraphToEPS");
        if (epsObject != null) {
            this.formats.put(epsformat, epsObject);
        }
    }

    @Override
    public Kind getFormatKind() {
        return Kind.JGRAPH;
    }

    private GraphToVector getGraphToVector(String vectorClassName) {
        GraphToVector result = null;
        try {
            @SuppressWarnings("unchecked")
            Class<GraphToVector> cls =
                (Class<GraphToVector>) Class.forName(vectorClassName);
            result = cls.newInstance();
        } catch (ClassNotFoundException e) {
            // Just return
        } catch (InstantiationException e) {
            // Just return
        } catch (IllegalAccessException e) {
            // Just return
        }
        return result;
    }

    @Override
    public Collection<? extends Format> getSupportedFormats() {
        return this.formats.keySet();
    }

    @Override
    public void doExport(File file, Format format, Exportable exportable)
        throws PortException {
        GraphJGraph jGraph = exportable.getJGraph();
        this.formats.get(format).renderGraph(jGraph, file);
    }

    private final Map<Format,GraphToVector> formats;

    /** Returns the singleton instance of this class. */
    public static final VectorExporter getInstance() {
        return instance;
    }

    private static final VectorExporter instance = new VectorExporter();
}
