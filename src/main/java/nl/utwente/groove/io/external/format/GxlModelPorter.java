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

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.conceptual.InstanceModel;
import nl.utwente.groove.io.conceptual.TypeModel;
import nl.utwente.groove.io.conceptual.lang.ExportableResource;
import nl.utwente.groove.io.conceptual.lang.ImportException;
import nl.utwente.groove.io.conceptual.lang.gxl.GxlResource;
import nl.utwente.groove.io.conceptual.lang.gxl.GxlToInstance;
import nl.utwente.groove.io.conceptual.lang.gxl.GxlToType;
import nl.utwente.groove.io.conceptual.lang.gxl.InstanceToGxl;
import nl.utwente.groove.io.conceptual.lang.gxl.TypeToGxl;
import nl.utwente.groove.io.external.ModelPorter;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.util.Pair;

/** Importer and exporter models in GXL format. */
public class GxlModelPorter extends ModelPorter {
    private GxlModelPorter() {
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
    protected ExportableResource getResource(File file, boolean isHost, TypeModel tm,
                                             InstanceModel im) throws PortException {
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
    public static final GxlModelPorter instance() {
        return instance;
    }

    private static final GxlModelPorter instance = new GxlModelPorter();
}
