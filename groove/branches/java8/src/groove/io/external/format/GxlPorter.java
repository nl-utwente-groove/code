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
import groove.io.conceptual.Design;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.lang.Export;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.gxl.DesignToGxl;
import groove.io.conceptual.lang.gxl.GlossaryToGxl;
import groove.io.conceptual.lang.gxl.GxlExport;
import groove.io.conceptual.lang.gxl.GxlToDesign;
import groove.io.conceptual.lang.gxl.GxlToGlossary;
import groove.io.external.ConceptualPorter;
import groove.io.external.PortException;
import groove.util.Pair;

import java.nio.file.Path;

/** Importer and exporter for the GXL format. */
public class GxlPorter extends ConceptualPorter {
    private GxlPorter() {
        super(FileType.GXL_META, FileType.GXL_MODEL);
    }

    @Override
    protected Pair<Glossary,Design> importGlossary(Path file, GrammarModel grammar)
        throws ImportException {
        GxlToGlossary g2g = new GxlToGlossary(file.toString(), false);
        Glossary glos = g2g.getGlossary();
        return Pair.newPair(glos, null);
    }

    @Override
    protected Pair<Glossary,Design> importDesign(Path file, GrammarModel grammar)
        throws ImportException {
        GxlToGlossary g2g = new GxlToGlossary(file.toString(), false);
        GxlToDesign g2d = new GxlToDesign(g2g, file.toString());

        Glossary glos = g2g.getGlossary();
        Design design = g2d.getDesign();
        return Pair.newPair(glos, design);
    }

    @Override
    protected Export getExport(Path file, boolean isHost, Glossary tm, Design im)
        throws PortException {
        // Use same file for both instance and type, so type gets included with instance
        GxlExport result = new GxlExport(file, file);
        GlossaryToGxl ttg = new GlossaryToGxl(result, tm);
        ttg.build();

        if (isHost) {
            new DesignToGxl(im, ttg).build();
        }
        return result;
    }

    /** Returns the singleton instance of this class. */
    public static final GxlPorter instance() {
        return instance;
    }

    private static final GxlPorter instance = new GxlPorter();
}
