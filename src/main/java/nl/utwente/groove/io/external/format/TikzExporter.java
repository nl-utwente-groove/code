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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.external.util.GraphToTikz;

/**
 * Class that implements saving graphs in the Tikz format.
 * Loading in this format is unsupported.
 *
 * @author Eduardo Zambon
 */
public final class TikzExporter extends AbstractExporter {
    private static final TikzExporter instance = new TikzExporter();

    /** Returns the singleton instance of this class. */
    public static final TikzExporter getInstance() {
        return instance;
    }

    private TikzExporter() {
        super(Exporter.ExportKind.JGRAPH);
        register(FileType.TIKZ);
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            GraphToTikz.export(exportable.jGraph(), writer);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }
}
