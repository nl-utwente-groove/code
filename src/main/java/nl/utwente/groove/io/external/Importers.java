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
package nl.utwente.groove.io.external;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.format.AutPorter;
import nl.utwente.groove.io.external.format.ColImporter;
import nl.utwente.groove.io.external.format.NativeResourcePorter;

/**
 * Registry of the known {@link Importer}s.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class Importers {
    /** Returns the list of all known importers. */
    public static List<Importer> getImporters() {
        if (importers == null) {
            importers = createImporters();
        }
        return importers;
    }

    private static List<Importer> createImporters() {
        List<Importer> result = new ArrayList<>();
        result.add(NativeResourcePorter.getInstance());
        result.add(AutPorter.instance());
        result.add(ColImporter.getInstance());
        return Collections.unmodifiableList(result);
    }

    /** List of importers */
    private static List<Importer> importers;

    /** Returns the importer for a given file type, if any. */
    public static Importer getImporter(FileType fileType) {
        return getImporterMap().get(fileType);
    }

    /** Returns the set of file types supported by the known importers. */
    public static Set<FileType> getFileTypes() {
        return getImporterMap().keySet();
    }

    /** Returns the mapping from file types to importers supporting them. */
    private static Map<FileType,Importer> getImporterMap() {
        if (importerMap == null) {
            importerMap = createImporterMap();
        }
        return importerMap;
    }

    /** Creates the mapping from file types to importers supporting them. */
    private static Map<FileType,Importer> createImporterMap() {
        Map<FileType,Importer> result = new LinkedHashMap<>();
        for (Importer ri : getImporters()) {
            for (FileType fileType : ri.getFileTypes()) {
                result.put(fileType, ri);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /** Mapping from file types to importers supporting them. */
    private static Map<FileType,Importer> importerMap;
}
