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

import static groove.io.ExtensionList.CONTROL_EXTENSION;
import static groove.io.ExtensionList.GXL_EXTENSION;
import static groove.io.ExtensionList.JAR_EXTENSION;
import static groove.io.ExtensionList.LAYOUT_EXTENSION;
import static groove.io.ExtensionList.PROPERTY_EXTENSION;
import static groove.io.ExtensionList.RULE_EXTENSION;
import static groove.io.ExtensionList.RULE_SYSTEM_EXTENSION;
import static groove.io.ExtensionList.STATE_EXTENSION;
import static groove.io.ExtensionList.TYPE_EXTENSION;
import static groove.io.ExtensionList.ZIP_EXTENSION;
import groove.util.Duo;

import java.util.HashMap;
import java.util.Map;

/**
 * List of all file filters.
 * @author Eduardo Zambon
 */
public final class FilterList {

    /**
     * Mapping from extensions to pairs of filters recognising/not recognising
     * directories.
     */
    private static final Map<String,Duo<ExtensionFilter>> extensionFilterMap =
        new HashMap<String,Duo<ExtensionFilter>>();

    /** Filter for grammar files. */
    public static final ExtensionFilter GRAMMAR_FILTER = getFilter(
        "Groove production systems", RULE_SYSTEM_EXTENSION, true);

    /** Filter for rule files. */
    public static final ExtensionFilter RULE_FILTER = getFilter(
        "Groove production rules", RULE_EXTENSION, false);

    /** Filter for state graph files. */
    public static final ExtensionFilter STATE_FILTER = getFilter(
        "Groove state graphs", STATE_EXTENSION, false);

    /** Filter for type graph files. */
    public static final ExtensionFilter TYPE_FILTER = getFilter(
        "Groove type graphs", TYPE_EXTENSION, false);

    /** Filter for property files. */
    public static final ExtensionFilter PROPERTIES_FILTER = getFilter(
        "Groove property files", PROPERTY_EXTENSION, false);

    /** Filter for control files. */
    public static final ExtensionFilter CONTROL_FILTER = getFilter(
        "Groove control files", CONTROL_EXTENSION, false);

    /** Filter for control files. */
    public static final ExtensionFilter LAYOUT_FILTER = getFilter(
        "Groove old layout files", LAYOUT_EXTENSION, false);

    /** Filter for GXL files. */
    public static final ExtensionFilter GXL_FILTER = getFilter("GXL files",
        GXL_EXTENSION, false);

    /** Filter for JAR files. */
    public static final ExtensionFilter JAR_FILTER = getFilter("JAR files",
        JAR_EXTENSION, false);

    /** Filter for ZIP files. */
    public static final ExtensionFilter ZIP_FILTER = getFilter("ZIP files",
        ZIP_EXTENSION, false);

    /**
     * Returns an extension filter with the required properties.
     * @param description general description of the filter
     * @param extension the extension to be filtered
     * @param acceptDirectories flag controlling whether directories should be
     *        accepted by the filter.
     * @return a filter with the required properties
     */
    public static ExtensionFilter getFilter(String description,
            String extension, boolean acceptDirectories) {
        Duo<ExtensionFilter> result = extensionFilterMap.get(extension);
        if (result == null) {
            ExtensionFilter first =
                new ExtensionFilter(description, extension, false);
            ExtensionFilter second =
                new ExtensionFilter(description, extension, true);
            result = new Duo<ExtensionFilter>(first, second);
            extensionFilterMap.put(extension, result);
        }
        return acceptDirectories ? result.two() : result.one();
    }

}
