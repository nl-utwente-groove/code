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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of file types supported by Groove.
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

    // Composite enumerations.

    private static Map<EnumSet<FileType>,String> compositeDescriptions =
        new HashMap<EnumSet<FileType>,String>();

    /** Returns the description associated with the given enum of file types. */
    public static String getDescription(EnumSet<FileType> fileTypes) {
        return compositeDescriptions.get(fileTypes);
    }

    /** Set of graph files. */
    public static EnumSet<FileType> graphs = EnumSet.of(STATE, RULE, TYPE, GXL);
    static {
        compositeDescriptions.put(graphs, String.format(
            "Graph files (*%s, *%s, *%s, *%s)", STATE.getExtension(),
            RULE.getExtension(), TYPE.getExtension(), GXL.getExtension()));
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

}
