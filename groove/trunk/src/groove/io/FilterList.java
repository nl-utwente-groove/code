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
 * List of all file filters.
 * 
 * @author Eduardo Zambon
 */
public final class FilterList {

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
    public static final ExtensionFilter GXL_FILTER = createFilter(FileType.GXL,
        true);

    /** Filter for rule files. */
    public static final ExtensionFilter RULE_FILTER = createFilter(
        FileType.RULE, true);

    /** Filter for state graph files. */
    public static final ExtensionFilter STATE_FILTER = createFilter(
        FileType.STATE, true);

    /** Filter for type graph files. */
    public static final ExtensionFilter TYPE_FILTER = createFilter(
        FileType.TYPE, true);

    /** Filter for grammar files. */
    public static final ExtensionFilter GRAMMAR_FILTER = createFilter(
        FileType.GRAMMAR, true);

    /** Filter for layout files. */
    public static final ExtensionFilter LAYOUT_FILTER = createFilter(
        FileType.LAYOUT, true);

    /** Filter for control files. */
    public static final ExtensionFilter CONTROL_FILTER = createFilter(
        FileType.CONTROL, true);

    /** Filter for property files. */
    public static final ExtensionFilter PROPERTIES_FILTER = createFilter(
        FileType.PROPERTY, true);

    // Text files.

    /** Filter for log files. */
    public static final ExtensionFilter LOG_FILTER = createFilter(FileType.LOG,
        true);
    /** Filter for simple text files. */
    public static final ExtensionFilter TEXT_FILTER = createFilter(
        FileType.TEXT, true);

    // Compressed files.

    /** Filter for ZIP files. */
    public static final ExtensionFilter ZIP_FILTER = createFilter(FileType.ZIP,
        true);

    /** Filter for JAR files. */
    public static final ExtensionFilter JAR_FILTER = createFilter(FileType.JAR,
        true);

    // External file formats.

    /** Filter for AUT files. */
    public static final ExtensionFilter AUT_FILTER = createFilter(FileType.AUT,
        true);

    /** Filter for COL files. */
    public static final ExtensionFilter COL_FILTER = createFilter(FileType.COL,
        true);

    /** Filter for EPS files. */
    public static final ExtensionFilter EPS_FILTER = createFilter(FileType.EPS,
        true);

    /** Filter for FSM files. */
    public static final ExtensionFilter FSM_FILTER = createFilter(FileType.FSM,
        true);

    /** Filter for JPG files. */
    public static final ExtensionFilter JPG_FILTER = createFilter(FileType.JPG,
        true);

    /** Filter for PNG files. */
    public static final ExtensionFilter PNG_FILTER = createFilter(FileType.PNG,
        true);

    /** Filter for Tikz files. */
    public static final ExtensionFilter TIKZ_FILTER = createFilter(
        FileType.TIKZ, true);

    /** Filter for KTH files. */
    public static final ExtensionFilter KTH_FILTER = createFilter(FileType.KTH,
        true);

    // Composite filters.

    /** Filter for all Groove graphs. */
    public static final ExtensionFilter GRAPHS_FILTER = createFilter(
        FileType.graphs, true);

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
            new CompositeExtensionFilter(fileTypes, acceptDir);
        compositeFilterMap.put(fileTypes, result);
        return result;
    }

    /** Returns the extension filter associated with the given file type. */
    public static ExtensionFilter getFilter(FileType fileTypes) {
        return simpleFilterMap.get(fileTypes);
    }

    /** Returns the extension filter associated with the given file type. */
    public static ExtensionFilter getFilter(EnumSet<FileType> fileTypes) {
        return compositeFilterMap.get(fileTypes);
    }

}
