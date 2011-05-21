/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.io;

import groove.graph.GraphRole;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of file types supported by Groove.
 * Each element of the enumeration has an associated file filter. 
 * 
 * @author Eduardo Zambon
 */
public enum FileType {
    // Native Groove files.
    /** GXL (Graph eXchange Language) files. */
    GXL(".gxl", "GXL files"),
    /** GPR (Graph Production Rule) files. */
    RULE(".gpr", "Groove production rules"),
    /** GST (Graph STate) files. */
    STATE(".gst", "Groove state graphs"),
    /** GTP (Graph TYpe) files. */
    TYPE(".gty", "Groove type graphs"),
    /** GPS (Graph Production System) files. */
    GRAMMAR(".gps", "Groove production systems"),
    /** Graph Layout files. */
    LAYOUT(".gl", "Groove old layout files"),
    /** Control files. */
    CONTROL(".gcp", "Groove control files"),
    /** Property files. */
    PROPERTY(".properties", "Groove property files"),

    // Text files.
    /** Log files. */
    LOG(".log", "Log files"),
    /** Simple text files. */
    TEXT(".txt", "Simple text files"),
    /** Prolog files. */
    PROLOG1(".pro", "Prolog files"),
    /** Prolog files. */
    PROLOG2(".pl", "Prolog files"),

    // Compressed files.
    /** ZIP files. */
    ZIP(".zip", "ZIP files"),
    /** JAR files. */
    JAR(".jar", "JAR files"),

    // External file formats.
    /** CADP .aut files. */
    AUT(".aut", "CADP .aut files"),
    /** DIMACS .col files. */
    COL(".col", "DIMACS graph format"),
    /**  EPS (Embedded PostScript) files. */
    EPS(".eps", "EPS image files"),
    /**  FSM (Finite State Machine) files. */
    FSM(".fsm", "FSM layout files"),
    /** JPEG files. */
    JPG(".jpg", "JPEG image files"),
    /** PNG (Portable Network Graphics) files. */
    PNG(".png", "PNG image files"),
    /** LaTeX Tikz files. */
    TIKZ(".tikz", "LaTeX Tikz files"),
    /** KTH file format, used by Marieke et al. */
    KTH(".kth", "Simple KTH files");

    /** Enumeration of importable native files. */
    public static final EnumSet<FileType> importNative = EnumSet.of(RULE,
        STATE, TYPE, CONTROL);

    // Composite enumerations.

    private static final Map<EnumSet<FileType>,String> compositeDescriptions =
        new HashMap<EnumSet<FileType>,String>();

    /** Returns the description associated with the given enum of file types. */
    private static String getDescription(EnumSet<FileType> fileTypes) {
        return compositeDescriptions.get(fileTypes);
    }

    /** Enum of graph files. */
    public static final EnumSet<FileType> graphs = EnumSet.of(STATE, RULE,
        TYPE, GXL);
    static {
        compositeDescriptions.put(graphs, String.format(
            "Graph files (*%s, *%s, *%s, *%s)", STATE.getExtension(),
            RULE.getExtension(), TYPE.getExtension(), GXL.getExtension()));
    }

    /** Enum of grammar files. */
    public static final EnumSet<FileType> grammars = EnumSet.of(GRAMMAR, ZIP,
        JAR);
    static {
        compositeDescriptions.put(
            grammars,
            String.format("Grammar files (*%s, *%s, *%s)",
                GRAMMAR.getExtension(), ZIP.getExtension(), JAR.getExtension()));
    }

    /** Enum of Prolog files. */
    public static final EnumSet<FileType> prolog = EnumSet.of(PROLOG1, PROLOG2);
    static {
        compositeDescriptions.put(prolog, String.format(
            "Prolog files (*%s, *%s)", PROLOG1.getExtension(),
            PROLOG2.getExtension()));
    }

    /** Enum of host graphs. */
    public static final EnumSet<FileType> hosts = EnumSet.of(STATE, GXL);
    static {
        compositeDescriptions.put(
            hosts,
            String.format("Host graphs (*%s, *%s)", STATE.getExtension(),
                GXL.getExtension()));
    }

    // Fields and methods.

    private final String extension;
    private final String description;

    private FileType(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    /** Returns the extension of this file type. A dot "." is prefixed. */
    public String getExtension() {
        return this.extension;
    }

    /** Returns the three letter string for the extension of this file type. */
    public String getExtensionName() {
        return this.extension.substring(1);
    }

    /** Returns the description associated with this file type. */
    public String getDescription() {
        return this.description;
    }

    // ------------------------------------------------------------------------
    // List of all file filters.
    // ------------------------------------------------------------------------

    /**
     * Mapping from extensions to pairs of filters recognising/not recognising
     * directories.
     */
    private static final Map<FileType,ExtensionFilter> simpleFilterMap =
        new HashMap<FileType,ExtensionFilter>();

    private static final Map<EnumSet<FileType>,ExtensionFilter> compositeFilterMap =
        new HashMap<EnumSet<FileType>,ExtensionFilter>();

    // Native Groove files.

    /** Filter for GXL files. */
    public static final ExtensionFilter GXL_FILTER = createFilter(GXL, true);

    /** Filter for rule files. */
    public static final ExtensionFilter RULE_FILTER = createFilter(RULE, true);

    /** Filter for state graph files. */
    public static final ExtensionFilter STATE_FILTER =
        createFilter(STATE, true);

    /** Filter for type graph files. */
    public static final ExtensionFilter TYPE_FILTER = createFilter(TYPE, true);

    /** Filter for uncompressed grammar files (.gps). */
    public static final ExtensionFilter GRAMMAR_FILTER = createFilter(GRAMMAR,
        true);

    /** Filter for layout files. */
    public static final ExtensionFilter LAYOUT_FILTER = createFilter(LAYOUT,
        true);

    /** Filter for control files. */
    public static final ExtensionFilter CONTROL_FILTER = createFilter(CONTROL,
        true);

    /** Filter for property files. */
    public static final ExtensionFilter PROPERTIES_FILTER = createFilter(
        PROPERTY, true);

    // Text files.

    /** Filter for log files. */
    public static final ExtensionFilter LOG_FILTER = createFilter(LOG, true);
    /** Filter for simple text files. */
    public static final ExtensionFilter TEXT_FILTER = createFilter(TEXT, true);

    // See also the composite Prolog filter below.
    /** Filter for Prolog files. */
    public static final ExtensionFilter PROLOG1_FILTER = createFilter(PROLOG1,
        true);
    /** Filter for Prolog files. */
    public static final ExtensionFilter PROLOG2_FILTER = createFilter(PROLOG2,
        true);

    // Compressed files.

    /** Filter for ZIP files. */
    public static final ExtensionFilter ZIP_FILTER = createFilter(ZIP, true);

    /** Filter for JAR files. */
    public static final ExtensionFilter JAR_FILTER = createFilter(JAR, true);

    // External file formats.

    /** Filter for AUT files. */
    public static final ExtensionFilter AUT_FILTER = createFilter(AUT, true);

    /** Filter for COL files. */
    public static final ExtensionFilter COL_FILTER = createFilter(COL, true);

    /** Filter for EPS files. */
    public static final ExtensionFilter EPS_FILTER = createFilter(EPS, true);

    /** Filter for FSM files. */
    public static final ExtensionFilter FSM_FILTER = createFilter(FSM, true);

    /** Filter for JPG files. */
    public static final ExtensionFilter JPG_FILTER = createFilter(JPG, true);

    /** Filter for PNG files. */
    public static final ExtensionFilter PNG_FILTER = createFilter(PNG, true);

    /** Filter for Tikz files. */
    public static final ExtensionFilter TIKZ_FILTER = createFilter(TIKZ, true);

    /** Filter for KTH files. */
    public static final ExtensionFilter KTH_FILTER = createFilter(KTH, true);

    // Composite filters.

    /** Filter for all Groove graphs. */
    public static final ExtensionFilter GRAPHS_FILTER = createFilter(graphs,
        true);

    /** Filter for all grammar files, including compressed grammars. */
    public static final ExtensionFilter GRAMMARS_FILTER = createFilter(
        grammars, true);

    /** Filter for Prolog files. */
    public static final ExtensionFilter PROLOG_FILTER = createFilter(prolog,
        true);

    /** Filter for host graphs. */
    public static final ExtensionFilter HOSTS_FILTER =
        createFilter(hosts, true);

    /**
     * Returns an extension filter with the required properties.
     * @param fileType the file type for which the filter is associated
     * @param acceptDir flag controlling whether directories should be
     *        accepted by the filter.
     * @return a filter with the required properties
     */
    private static ExtensionFilter createFilter(FileType fileType,
            boolean acceptDir) {
        ExtensionFilter result =
            new SimpleExtensionFilter(fileType.getDescription(),
                fileType.getExtension(), acceptDir);
        simpleFilterMap.put(fileType, result);
        return result;
    }

    /**
     * Returns an extension filter with the required properties.
     * @param fileTypes the file type for which the filter is associated
     * @param acceptDir flag controlling whether directories should be
     *        accepted by the filter.
     * @return a filter with the required properties
     */
    private static ExtensionFilter createFilter(EnumSet<FileType> fileTypes,
            boolean acceptDir) {
        ExtensionFilter result =
            new CompositeExtensionFilter(fileTypes, getDescription(fileTypes),
                acceptDir);
        compositeFilterMap.put(fileTypes, result);
        return result;
    }

    /** Returns the extension filter associated with the given file type. */
    public static ExtensionFilter getFilter(FileType fileTypes) {
        return simpleFilterMap.get(fileTypes);
    }

    /** Returns the extension filter associated with the given graph role. */
    public static ExtensionFilter getFilter(GraphRole role) {
        switch (role) {
        case HOST:
            return STATE_FILTER;
        case RULE:
            return RULE_FILTER;
        case TYPE:
            return TYPE_FILTER;
        case LTS:
            return GXL_FILTER;
        default:
            return null;
        }
    }

    /** Returns the extension filter associated with the given file type. */
    public static ExtensionFilter getFilter(EnumSet<FileType> fileTypes) {
        return compositeFilterMap.get(fileTypes);
    }

}
