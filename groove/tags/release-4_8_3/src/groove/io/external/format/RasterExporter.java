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

import groove.gui.jgraph.JGraph;
import groove.io.FileType;
import groove.io.external.AbstractFormatExporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.PortException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/** 
 * Class that implements saving graphs as raster (JPEG or PNG (Portable Network Graphics)) images.
 * Loading in this format is unsupported.
 * 
 * @author Arend Rensink 
 */
public class RasterExporter extends AbstractFormatExporter {
    private RasterExporter() {
        Format pngformat = new Format(this, FileType.PNG);
        Format jpgformat = new Format(this, FileType.JPG);
        this.formats = new LinkedHashMap<Format,String>();
        this.formats.put(pngformat, "png");
        this.formats.put(jpgformat, "jpg");
    }

    @Override
    public Kind getFormatKind() {
        return Kind.JGRAPH;
    }

    @Override
    public Collection<? extends Format> getSupportedFormats() {
        return this.formats.keySet();
    }

    @Override
    public void doExport(File file, Format format, Exportable exportable)
        throws PortException {
        JGraph<?> jGraph = exportable.getJGraph();
        BufferedImage image = jGraph.toImage();
        if (image == null) {
            throw new PortException("Cannot export blank image");
        }
        try {
            ImageIO.write(image, this.formats.get(format), file);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }

    private final Map<Format,String> formats;

    /** Returns the singleton instance of this class. */
    public static final RasterExporter getInstance() {
        return instance;
    }

    private static final RasterExporter instance = new RasterExporter();
}
