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

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.ResourceModel;
import groove.grammar.model.TextBasedModel;
import groove.graph.plain.PlainGraph;
import groove.io.FileType;
import groove.io.external.AbstractFormatExporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.FormatImporter;
import groove.io.external.FormatPorter;
import groove.io.external.PortException;
import groove.io.xml.LayedOutXml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Import and export resources native to GROOVE, such as type and host graphs, and control programs
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class NativePorter extends AbstractFormatExporter implements
        FormatImporter {
    private NativePorter() {
        addFormat(ResourceKind.TYPE, FileType.TYPE);
        addFormat(ResourceKind.HOST, FileType.STATE, FileType.GXL);
        addFormat(ResourceKind.RULE, FileType.RULE);
        addFormat(ResourceKind.CONTROL, FileType.CONTROL);
        addFormat(ResourceKind.PROLOG, FileType.PROLOG1, FileType.PROLOG2);
        addFormat(ResourceKind.GROOVY, FileType.GROOVY);
        addFormat(ResourceKind.CONFIG, FileType.CONFIG);
    }

    private void addFormat(ResourceKind kind, FileType... fileTypes) {
        this.formats.add(new ResourceFormat(this, kind, fileTypes));
    }

    @Override
    public Kind getFormatKind() {
        return Kind.RESOURCE;
    }

    @Override
    public Collection<? extends Format> getSupportedFormats() {
        return this.formats;
    }

    @Override
    public Set<Resource> doImport(File file, Format format, GrammarModel grammar)
        throws PortException {
        ResourceKind kind = ((ResourceFormat) format).getKind();
        Resource result;
        try {
            String name = format.stripExtension(file.getName());
            if (kind.isGraphBased()) {
                // read graph from file
                PlainGraph plainGraph =
                    LayedOutXml.getInstance().unmarshalGraph(file);
                plainGraph.setRole(kind.getGraphRole());
                plainGraph.setName(name);
                result =
                    new Resource(kind, name,
                        AspectGraph.newInstance(plainGraph));
            } else {
                String program = groove.io.Util.readFileToString(file);
                result = new Resource(kind, name, program);
            }
        } catch (IOException e) {
            throw new PortException(e);
        }
        return Collections.singleton(result);
    }

    @Override
    public Set<Resource> doImport(String name, InputStream stream,
            Format format, GrammarModel grammar) throws PortException {
        ResourceKind kind = ((ResourceFormat) format).getKind();
        if (kind.isGraphBased()) {
            throw new PortException("Cannot import from stream");
        }

        Resource result;
        try {
            String resource = groove.io.Util.readInputStreamToString(stream);
            result = new Resource(kind, name, resource);
        } catch (IOException e) {
            throw new PortException(e);
        }
        return Collections.singleton(result);
    }

    @Override
    public void doExport(File file, Format format, Exportable exportable)
        throws PortException {
        ResourceModel<?> model = exportable.getModel();
        ResourceKind kind = model.getKind();
        if (kind.isGraphBased()) {
            GraphBasedModel<?> graphModel = (GraphBasedModel<?>) model;
            AspectGraph graph = graphModel.getSource();
            try {
                LayedOutXml.getInstance().marshalGraph(graph.toPlainGraph(),
                    file);
            } catch (IOException e) {
                throw new PortException(e);
            }
        } else {
            TextBasedModel<?> textModel = (TextBasedModel<?>) model;
            Writer writer = null;
            try {
                writer = new FileWriter(file);
                writer.write(textModel.getSource());
            } catch (IOException e) {
                throw new PortException(e);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    throw new PortException(e);
                }
            }
        }
    }

    private final List<ResourceFormat> formats =
        new ArrayList<ResourceFormat>();

    /** Returns the singleton instance of this class. */
    public static final NativePorter getInstance() {
        return instance;
    }

    private static final NativePorter instance = new NativePorter();

    /** Format for one of the resource kinds of grammars. */
    public class ResourceFormat extends Format {
        private final ResourceKind kind;

        /**
         * Constructs a new resource format, for a given resource kind
         * and supporting a list of file types.
         */
        protected ResourceFormat(FormatPorter formatter, ResourceKind kind,
                FileType... types) {
            super(formatter, types);

            this.kind = kind;
        }

        /** The resource kind of this format. */
        public ResourceKind getKind() {
            return this.kind;
        }

    }
}
