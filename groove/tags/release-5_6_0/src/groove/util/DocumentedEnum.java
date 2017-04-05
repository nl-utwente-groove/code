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
package groove.util;

import groove.io.HTMLConverter;

/**
 * Interface to provide documentation for a publicly visible enum type.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface DocumentedEnum {
    /** Name by which the used should refer to this value. */
    public String getName();

    /** User-consumable explanation of the purpose of this value. Starts with upper case. */
    public String getExplanation();

    /** Returns a HTML-formatted bulleted-list documentation of an enum class
     * consisting of (for each enum value) its {@link #getName()} and {@link #getExplanation()}
     * values.
     */
    static <T extends Enum<?> & DocumentedEnum> String document(Class<T> claz) {
        StringBuffer result = new StringBuffer();
        HTMLConverter.HTMLTag liTag = HTMLConverter.createHtmlTag("li");
        for (T value : claz.getEnumConstants()) {
            StringBuilder line = new StringBuilder();
            line.append("- ");
            line.append(HTMLConverter.ITALIC_TAG.on(value.getName()));
            line.append(": ");
            line.append(value.getExplanation());
            result.append(liTag.on(line));
        }
        return result.toString();
    }
}
