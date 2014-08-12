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
package groove.explore.config;

import groove.util.parse.ParsableKey;
import groove.util.parse.Parser;

/**
 * Supertype for the discriminators of {@link Setting}s
 * @author Arend Rensink
 * @version $Revision $
 */
public interface SettingKey extends ParsableKey<SettingContent> {
    /** Returns the type of content that this key expects. */
    public Class<? extends SettingContent> getContentType();

    /** Returns the default setting for this key, if any.
     * Convenience method for {@code createSetting(getDefaultValue())}.
     * @see #createSetting(SettingContent)
     * @see #getDefaultValue()
     */
    public SettingList getDefaultSetting();

    /** Convenience method for {@code createValue(null)}
     * @throws IllegalArgumentException if {@code null} does not satisfy {@link Parser#isValue}
     */
    public Setting<?,?> createSettting() throws IllegalArgumentException;

    /**
     * Creates an exploration value of this kind, and a given content.
     * @param content exploration content; should be compatible with this kind
     * @throws IllegalArgumentException if {@code content} does not satisfy {@link Parser#isValue}
     */
    public Setting<?,?> createSetting(SettingContent content) throws IllegalArgumentException;
}
