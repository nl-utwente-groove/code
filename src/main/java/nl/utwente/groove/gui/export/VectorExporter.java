/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.export;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.gui.export.util.GraphToEPS;
import nl.utwente.groove.gui.export.util.GraphToPDF;
import nl.utwente.groove.gui.export.util.GraphToSVG;
import nl.utwente.groove.gui.export.util.GraphToVector;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.util.io.FileType;

/**
 * Class that implements saving graphs as vectorised EPS (Embedded PostScript) or PDF images.
 * Loading in this format is unsupported.
 *
 * @author Arend Rensink / Harold Bruintjes
 */
@NonNullByDefault
public class VectorExporter extends AbstractExporter {
    /** Private constructor for the singleton instance. */
    private VectorExporter() {
        super(Exporter.ExportKind.CANVAS);
        addFormat(FileType.EPS, new GraphToEPS());
        addFormat(FileType.PDF, new GraphToPDF());
        addFormat(FileType.SVG, new GraphToSVG());
    }

    private void addFormat(FileType fileType, GraphToVector format) {
        register(fileType);
        this.formats.put(fileType, format);
    }

    @Override
    public boolean exports(Exportable exportable) {
        return exportable instanceof CanvasExportable;
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        if (!(exportable instanceof CanvasExportable canvasExportable)) {
            throw new PortException(String
                .format("'%s' does not contain a rendered graph and hence cannot be exported to %s",
                        exportable.qualName(), fileType.getExtension()));
        }
        var format = this.formats.get(fileType);
        assert format != null; // the format map holds an entry for every registered file type
        format.renderGraph(canvasExportable.canvas(), file);
    }

    private final Map<FileType,GraphToVector> formats = new EnumMap<>(FileType.class);

    /** Returns the singleton instance of this class. */
    public static final VectorExporter getInstance() {
        return instance;
    }

    private static final VectorExporter instance = new VectorExporter();
}
