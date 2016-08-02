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

import java.io.File;

import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.io.FileType;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.ExportableResource;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.graphviz.GraphvizResource;
import groove.io.conceptual.lang.graphviz.GraphvizToInstance;
import groove.io.conceptual.lang.graphviz.InstanceToGraphviz;
import groove.io.conceptual.lang.graphviz.TypeToGraphviz;
import groove.io.external.ConceptualPorter;
import groove.io.external.PortException;
import groove.util.Pair;

/** Importer and exporter for the DOT format. */
public class DotPorter extends ConceptualPorter {
    private DotPorter() {
        super(FileType.DOT_META, FileType.DOT_MODEL);
    }

    @Override
    protected Pair<TypeModel,InstanceModel> importInstanceModel(File file, GrammarModel grammar)
        throws ImportException {
        GraphvizToInstance gtg = new GraphvizToInstance(file.getAbsolutePath());

        InstanceModel im = gtg.getInstanceModel(QualName.name("DOT"));
        return Pair.newPair(im.getTypeModel(), im);
    }

    @Override
    protected Pair<TypeModel,InstanceModel> importTypeModel(File file, GrammarModel grammar)
        throws ImportException {
        return null;
    }

    @Override
    protected ExportableResource getResource(File file, boolean isHost, TypeModel tm,
        InstanceModel im) throws PortException {
        GraphvizResource result = new GraphvizResource(file, file);
        TypeToGraphviz ttg = new TypeToGraphviz(result);
        ttg.addTypeModel(tm);

        if (isHost) {
            InstanceToGraphviz itg = new InstanceToGraphviz(ttg);
            itg.addInstanceModel(im);
        }
        return result;
    }

    /** Returns the singleton instance of this class. */
    public static final DotPorter instance() {
        return instance;
    }

    private static final DotPorter instance = new DotPorter();
}
