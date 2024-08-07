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
package nl.utwente.groove.io.external.format;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter;
import nl.utwente.groove.io.external.PortException;

/**
 * Class that implements saving graphs as raster (JPEG or PNG (Portable Network Graphics)) images.
 * Loading in this format is unsupported.
 *
 * @author Arend Rensink
 */
public class RasterExporter extends AbstractExporter {
    private RasterExporter() {
        super(Exporter.ExportKind.JGRAPH);
        addFormat(FileType.PNG, "png");
        addFormat(FileType.JPG, "jpg");
    }

    private void addFormat(FileType fileType, String descr) {
        register(fileType);
        this.formats.put(fileType, descr);
    }

    private final Map<FileType,String> formats = new EnumMap<>(FileType.class);

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        JGraph<?> jGraph = exportable.jGraph();
        if (jGraph == null) {
            throw new PortException(String
                .format("'%s' does not contain a rasterable image and hence cannot be exported to %s",
                        exportable.qualName(), fileType.getExtension()));
        }
        BufferedImage image = jGraph.toImage();
        if (image == null) {
            throw new PortException("Cannot export blank image");
        }
        try {
            ImageIO.write(image, this.formats.get(fileType), file);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }

    /** Returns the singleton instance of this class. */
    public static final RasterExporter getInstance() {
        return instance;
    }

    private static final RasterExporter instance = new RasterExporter();
}
