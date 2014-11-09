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
package groove.io.conceptual.lang.groove;

import groove.grammar.QualName;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;

/** Collection of utilities for the GROOVE import and export functionality. */
public class GrooveUtil {
    private static final String s_invalidRegex = "[^A-Za-z0-9_-]";
    private static final String s_reservedRegex = "[A-Za-z][0-9]*";
    private static final String s_invalidResourceStartRegex = "[^A-Za-z_].*";
    private static final String s_safeString = "_";

    /**
     * Return a new Id which is guaranteed to be a valid type in a GROOVE graph
     * @param id Potentially unsafe Id
     * @return Safe Id
     */
    public static Id getSafeId(Id id) {
        if (id == Id.ROOT) {
            return id;
        }
        Id ns = getSafeId(id.getNamespace());
        Name name = getSafeName(id.getName());
        return Id.getId(ns, name);
    }

    /**
     * Return a new Name which is guaranteed to be a valid type in a GROOVE graph
     * @param name Potentially unsafe Name
     * @return Safe Name
     */
    public static Name getSafeName(Name name) {
        String safeString = name.toString().replaceAll(s_invalidRegex, s_safeString);
        return Name.getName(safeString);
    }

    /**
     * Return a valid node ID based on given parameter (e.g. not reserved)
     * @param name Name to validate
     * @return Name guaranteed to be valid identifier in instance graph
     */
    public static String getSafeNodeName(String name) {
        if (name.matches(s_reservedRegex)) {
            name = "_" + name;
        }
        name = name.replaceAll(s_invalidRegex, s_safeString);
        return name;
    }

    /**
     * Return a string which is guaranteed to be a valid resource or type name
     * @param idString Potentially unsafe string
     * @return Safe string
     */
    public static String getSafeId(String idString) {
        String safeString = idString.replaceAll(s_invalidRegex, s_safeString);
        if (safeString.matches(s_reservedRegex)) {
            return "_" + safeString;
        }
        return safeString;
    }

    /**
     * Return a valid qualified resource name based on parameter
     * @param resourceString Potentially invalid qualified resource string
     * @return Valid resource name
     */
    public static String getSafeResource(String resourceString) {
        String[] parts = resourceString.split("\\Q" + QualName.SEPARATOR + "\\E");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll(s_invalidRegex, s_safeString);
            if (parts[i].matches(s_invalidResourceStartRegex)) {
                parts[i] = "_" + parts[i];
            }
        }
        StringBuilder buffer = new StringBuilder(resourceString.length());
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                buffer.append(QualName.SEPARATOR);
            }
            buffer.append(parts[i]);
        }
        return buffer.toString();
    }
}
