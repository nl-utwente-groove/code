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
package groove.gui.dialog;

import groove.util.Property;

/** Interface for property keys. */
public interface PropertyKey {
    /** Property key name. */
    public String getName();

    /** Short description for user consumption. */
    public String getDescription();

    /** Default value for the property; non-{@code null} but possibly the empty string. */
    public String getDefaultValue();

    /** Indicates if this is a system key. */
    public boolean isSystem();

    /** Format property for legal values of this key. */
    public Property<String> getFormat();
}
