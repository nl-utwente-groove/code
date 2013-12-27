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

import groove.grammar.model.GrammarModel;
import groove.io.FileType;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.ExportableResource;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.gxl.GxlResource;
import groove.io.conceptual.lang.gxl.GxlToInstance;
import groove.io.conceptual.lang.gxl.GxlToType;
import groove.io.conceptual.lang.gxl.InstanceToGxl;
import groove.io.conceptual.lang.gxl.TypeToGxl;
import groove.io.external.ConceptualPorter;
import groove.io.external.PortException;
import groove.util.Pair;

import java.io.File;

/** Importer and exporter for the GXL format. */
public class GxlPorter extends ConceptualPorter {
    private GxlPorter() {
        super(FileType.GXL_META, FileType.GXL_MODEL);
    }

    @Override
    protected Pair<TypeModel,InstanceModel> importTypeModel(File file,
            GrammarModel grammar) throws ImportException {
        GxlToType gtt = new GxlToType(file.toString(), false);
        TypeModel tm = gtt.getTypeModel();
        return Pair.newPair(tm, null);
    }

    @Override
    protected Pair<TypeModel,InstanceModel> importInstanceModel(File file,
            GrammarModel grammar) throws ImportException {
        GxlToType gtt = new GxlToType(file.toString(), false);
        GxlToInstance gti = new GxlToInstance(gtt, file.toString());

        TypeModel tm = gtt.getTypeModel();
        InstanceModel im = gti.getInstanceModel();
        return Pair.newPair(tm, im);
    }

    @Override
    protected ExportableResource getResource(File file, boolean isHost,
            TypeModel tm, InstanceModel im) throws PortException {
        // Use same file for both instance and type, so type gets included with instance
        GxlResource result = new GxlResource(file, file);
        TypeToGxl ttg = new TypeToGxl(result);
        ttg.addTypeModel(tm);

        if (isHost) {
            InstanceToGxl itg = new InstanceToGxl(ttg);
            itg.addInstanceModel(im);
        }
        return result;
    }

    /** Returns the singleton instance of this class. */
    public static final GxlPorter instance() {
        return instance;
    }

    private static final GxlPorter instance = new GxlPorter();
}
