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
import java.io.IOException;

import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.graph.GxlIO;

/**
 * Import and export resources native to GROOVE, such as type and host graphs, and control programs
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class NativeGraphExporter extends AbstractExporter {
    private NativeGraphExporter() {
        super(ExportKind.GRAPH);
        register(ResourceKind.TYPE);
        register(ResourceKind.HOST);
        register(ResourceKind.RULE);
    }

    /** Registers a resource kind with its default file type. */
    private void register(ResourceKind kind) {
        register(kind.getFileType());
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        var graph = GraphConverter.toAspect(exportable.graph());
        try {
            GxlIO.instance().saveGraph(graph.toPlainGraph(), file);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }

    /** Returns the singleton instance of this class. */
    public static final NativeGraphExporter getInstance() {
        return instance;
    }

    private static final NativeGraphExporter instance = new NativeGraphExporter();
}
