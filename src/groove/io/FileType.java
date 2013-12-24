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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enumeration of file types supported by Groove.
 * Each element of the enumeration has an associated file filter. 
 * 
 * @author Eduardo Zambon
 */
public enum FileType {
    // Native Groove files.
    /** GXL (Graph eXchange Language) files. */
    GXL("GXL files", ".gxl"),
    /** GPR (Graph Production Rule) files. */
    RULE("Groove production rules", ".gpr"),
    /** GST (Graph STate) files. */
    STATE("Groove state graphs", ".gst"),
    /** GTP (Graph TYpe) files. */
    TYPE("Groove type graphs", ".gty"),
    /** GPS (Graph Production System) files. */
    GRAMMAR("Groove production systems", ".gps"),
    /** Graph Layout files. */
    LAYOUT("Groove layout files (deprecated)", ".gl"),
    /** Control files. */
    CONTROL("Groove control files", ".gcp"),
    /** Property files. */
    PROPERTY("Groove property files", ".properties"),

    // Text files.
    /** Log files. */
    LOG("Log files", ".log"),
    /** Plain text files. */
    TEXT("Simple text files", ".txt"),
    /** Prolog files. */
    PROLOG1("Prolog files", ".pro"),
    /** Prolog files. */
    PROLOG2("Prolog files", ".pl"),
    /** Configuration files. */
    CONFIG("Configuration files", ".xml"),
    /** Groovy files. */
    GROOVY("Groovy files", ".groovy"),

    // External file formats.
    /** CADP .aut files. */
    AUT("CADP automata files", ".aut"),
    /** DIMACS .col files. */
    COL("DIMACS graph format", ".col"),
    /** Comma-separated value files. */
    CSV("Comma-separated values", ".csv"),
    /** GraphViz .dot type graph files. */
    DOT_META("GraphViz type graphs", ".viz"),
    /** GraphViz .dot instance graph files. */
    DOT_MODEL("GraphViz instance graphs", ".viz"),
    /** ECore meta-models. */
    ECORE_META("ECore meta-models", ".ecore"),
    /** ECore instance models. */
    ECORE_MODEL("ECore instance models", ".xmi"),
    /**  EPS (Embedded PostScript) files. */
    EPS("EPS image files", ".eps"),
    /**  FSM (Finite State Machine) files. */
    FSM("FSM layout files", ".fsm"),
    /** GXL type graph files. */
    GXL_META("GXL type graphs", ".gxl"),
    /** GXL instance graph files. */
    GXL_MODEL("GXL instance graphs", ".gxl"),
    /** JAR files. */
    JAR("JAR files", ".jar"),
    /** JPEG files. */
    JPG("JPEG image files", ".jpg"),
    /** PDF file format, export graphs */
    PDF("Adobe PDF documents", ".pdf"),
    /** PNG (Portable Network Graphics) files. */
    PNG("PNG image files", ".png"),
    /** LaTeX TikZ files. */
    TIKZ("LaTeX TikZ files", ".tikz"),
    /** KTH file format, used by Marieke et al. */
    KTH("Simple KTH files", ".kth"),
    /** ZIP files. */
    ZIP("ZIP files", ".zip"),

    // Composed file types
    /** Arbitrary grammar files, including compressed grammars. */
    GRAMMARS("Grammar files", GRAMMAR, ZIP, JAR),
    /** Arbitrary graph files. */
    GRAPHS("Graph files", STATE, RULE, TYPE, GXL),
    /** Arbitrary host graph formats ({@link #STATE} or {@link #GXL}). */
    HOSTS("Host graphs", STATE, GXL),
    /** Arbitrary Prolog files ({@link #PROLOG1} or {@link #PROLOG2}). */
    PROLOG("Prolog files", PROLOG1, PROLOG2), ;

    /** Constructs a singular file type. */
    private FileType(String description, String extension) {
        assert description != null && description.length() > 0 : String.format(
            "Badly formatted file type description: %s", description);
        assert extension != null && extension.length() > 1
            && extension.charAt(0) == SEPARATOR : String.format(
            "Badly formatted file type extension: %s", extension);
        this.extension = extension;
        this.description = description;
    }

    /** 
     * Constructs a composed file type from a 
     * series of predefined file sub-types.
     * The first of the sub-types will be the primary file type.
     */
    private FileType(String description, FileType... subTypes) {
        this(description, subTypes[0].getExtension());
        this.subTypes = subTypes;
    }

    /** Returns the primary extension of this file type,
     * with {@link #SEPARATOR} prefix. */
    public String getExtension() {
        return this.extension;
    }

    /** 
     * Returns the primary extension of this file type, without {@link #SEPARATOR} prefix.
     * @see #getExtension() 
     */
    public String getExtensionName() {
        return this.extension.substring(1);
    }

    /** Returns the list of all file extensions for this file type. */
    public List<String> getExtensions() {
        List<String> result = this.extensions;
        if (result == null) {
            if (isMultiple()) {
                result = new ArrayList<String>();
                for (FileType subType : this.subTypes) {
                    result.add(subType.getExtension());
                }
                result = Collections.unmodifiableList(result);
            } else {
                result = Collections.singletonList(getExtension());
            }
            this.extensions = result;
        }
        return result;
    }

    /** Returns the description associated with this file type. */
    public String getDescription() {
        return this.description;
    }

    /** Returns the extension filter associated with this file type. */
    public ExtensionFilter getFilter() {
        if (this.filter == null) {
            this.filter = new ExtensionFilter(this);
        }
        return this.filter;
    }

    /**
     * Strips the extension of this file type from a filename, if the extension is in fact there.
     * @param filename the filename to be stripped
     */
    public String stripExtension(String filename) {
        String result = filename;
        if (isMultiple()) {
            for (FileType subType : this.subTypes) {
                if (subType.hasExtension(filename)) {
                    result = subType.stripExtension(filename);
                    break;
                }
            }
        } else if (filename.endsWith(this.getExtension())) {
            result =
                filename.substring(0, filename.length()
                    - getExtension().length());
        }
        return result;
    }

    /**
     * Strips the extension of this file type from a file, if the extension is in fact there.
     * @param file the file to be stripped
     */
    public File stripExtension(File file) {
        return new File(file.getParentFile(), stripExtension(file.getName()));
    }

    /**
     * Adds the (primary) extension of this file type to filename, if there is no extension.
     * @param filename the filename to be provided with an extension
     */
    public String addExtension(String filename) {
        String result = filename;
        if (!hasExtension(filename)) {
            result = filename + getExtension();
        }
        return result;
    }

    /**
     * Adds the (primary) extension of this file type to a file, if there is no extension.
     * @param file the file to be provided with an extension
     */
    public File addExtension(File file) {
        return new File(file.getParentFile(), addExtension(file.getName()));
    }

    /**
     * Tests if a given filename has the extension of this file type.
     * @param filename the filename to be tested
     * @return <code>true</code> if <code>filename</code> has the extension
     *         of this filter
     */
    public boolean hasExtension(String filename) {
        boolean result = false;
        if (isMultiple()) {
            for (FileType subType : this.subTypes) {
                if (subType.hasExtension(filename)) {
                    result = true;
                    break;
                }
            }
        } else {
            result = filename.endsWith(this.getExtension());
        }
        return result;
    }

    /**
     * Tests if a given file has the extension of this file type.
     * @param file the file to be tested
     * @return <code>true</code> if <code>file</code> has the extension
     *         of this filter
     */
    public boolean hasExtension(File file) {
        return hasExtension(file.getName());
    }

    /** Tests if this is a file type with multiple extensions. */
    private boolean isMultiple() {
        return this.subTypes != null;
    }

    // Fields and methods.

    /** The primary file extension. */
    private final String extension;
    /** Description of the file type. */
    private final String description;
    /** Set of sub-file types, if this is a collective file type. */
    private FileType[] subTypes;
    /** List of all file extensions. */
    private List<String> extensions;
    /** Extension filter for this file type. */
    private ExtensionFilter filter;

    /** Returns the extension filter associated with the given graph role. */
    public static ExtensionFilter getFilter(GraphRole role) {
        switch (role) {
        case HOST:
            return FileType.STATE.getFilter();
        case RULE:
            return FileType.RULE.getFilter();
        case TYPE:
            return FileType.TYPE.getFilter();
        case LTS:
            return FileType.GXL.getFilter();
        default:
            return null;
        }
    }

    /**
     * Returns the extension part of a file name. The extension is taken to be
     * the part from the last #SEPARATOR occurrence (inclusive).
     * @param file the file to obtain the name from
     * @return the extension part of <code>file.getName()</code>
     * @see File#getName()
     */
    static public String getExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf(SEPARATOR));
    }

    /**
     * Returns the name part of a file name, without extension. The extension is
     * taken to be the part from the last #SEPARATOR occurrence (inclusive).
     * @param file the file to obtain the name from
     * @return the name part of <code>file.getName()</code>, without the
     *         extension
     * @see File#getName()
     */
    static public String getPureName(File file) {
        return getPureName(file.getName());
    }

    /**
     * Returns the name part of a file name, without extension. The extension is
     * taken to be the part from the last #SEPARATOR occurrence (inclusive).
     * @param filename the filename to be stripped
     * @return the name part of <code>file.getName()</code>, without the
     *         extension
     */
    static public String getPureName(String filename) {
        int index = filename.lastIndexOf(SEPARATOR);
        if (index < 0) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * Tests if a given filename has any extension.
     * @param filename the filename to be tested
     * @return <code>true</code> if <code>filename</code> has an extension
     * (not necessarily of this filter).
     */
    public static boolean hasAnyExtension(String filename) {
        return hasAnyExtension(new File(filename));
    }

    /**
     * Tests if a given file has any extension.
     * @param file the filename to be tested
     * @return <code>true</code> if <code>file</code> has an extension
     * (not necessarily of this filter).
     */
    public static boolean hasAnyExtension(File file) {
        return file.getName().indexOf(SEPARATOR) >= 0;
    }

    /**
     * Separator character between filename and extension.
     */
    static public final char SEPARATOR = '.';
}
