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

/**
 * Supertype for all values that can be assigned to {@link ExploreKey}s.
 * @param <K> type of the keys for this setting
 * @param <C> type of the content for this setting
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Setting<K extends Enum<K> & SettingKey,C> {
    /** Returns the kind of this setting. */
    public abstract K getKind();

    /**
     * Returns the content of the setting.
     * May be {@code null}, if this is allowed by the setting key.
     */
    public abstract C getContent();
}
