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
import groove.io.GrooveFileChooser;
import groove.io.conceptual.Design;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.lang.Export;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.ecore.DesignToEcore;
import groove.io.conceptual.lang.ecore.EcoreExport;
import groove.io.conceptual.lang.ecore.EcoreToDesign;
import groove.io.conceptual.lang.ecore.EcoreToGlossary;
import groove.io.conceptual.lang.ecore.GlossaryToEcore;
import groove.io.external.ConceptualPorter;
import groove.io.external.PortException;
import groove.util.Pair;

import java.io.File;
import java.nio.file.Path;

import javax.swing.JFileChooser;

/** Importer and exporter for the ECORE format. */
public class EcorePorter extends ConceptualPorter {
    private EcorePorter() {
        super(FileType.ECORE_META, FileType.ECORE_MODEL);
    }

    @Override
    protected Pair<Glossary,Design> importGlossary(Path file, GrammarModel grammar)
        throws ImportException {
        EcoreToGlossary e2g = new EcoreToGlossary(file.toFile());
        Glossary tm = e2g.getGlossary();
        return Pair.newPair(tm, null);
    }

    @Override
    protected Pair<Glossary,Design> importDesign(Path file, GrammarModel grammar)
        throws ImportException {
        //Request ecore type model file
        int approve = getECoreChooser().showDialog(null, "Import Ecore type model");
        if (approve != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File typeFile = getECoreChooser().getSelectedFile();

        EcoreToGlossary e2g = new EcoreToGlossary(typeFile);
        Glossary glos = e2g.getGlossary();
        Design design = new EcoreToDesign(e2g, file.toString()).build().getDesign();
        return Pair.newPair(glos, design);
    }

    /** Returns the file chooser for the ECore meta-model. */
    public GrooveFileChooser getECoreChooser() {
        return GrooveFileChooser.getInstance(FileType.ECORE_META);
    }

    @Override
    protected Export getExport(Path file, boolean isHost, Glossary tm, Design im)
        throws PortException {
        Path typeFile = file;
        Path instanceFile = file;
        if (isHost) {
            //Request ecore type model file to specify schemaLocation
            //This is optional and may be commented out, but leave typeFile to null when doing so
            int approve = getECoreChooser().showDialog(null, "Pick ECore metamodel");
            if (approve == JFileChooser.APPROVE_OPTION) {
                typeFile = getECoreChooser().getSelectedFile().toPath();
            } else {
                typeFile = null;
            }
        } else {
            instanceFile = null;
        }

        EcoreExport result = new EcoreExport(typeFile, instanceFile);
        GlossaryToEcore g2e = new GlossaryToEcore(tm, result);
        g2e.build();

        if (isHost) {
            new DesignToEcore(im, g2e).build();
        }
        return result;
    }

    /** Returns the singleton instance of this class. */
    public static final EcorePorter instance() {
        return instance;
    }

    private static final EcorePorter instance = new EcorePorter();

}
