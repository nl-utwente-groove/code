/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import nl.utwente.groove.util.AIGenerated;

/**
 * In-memory {@link PreferencesFactory} for GUI tests: keeps all preference
 * data in the JVM, so tests that exercise the Simulator (which reads and
 * writes user preferences through {@code Options} and {@code UserSettings})
 * can never touch the developer's real preference store.
 * <p>
 * Install by setting the system property
 * {@code java.util.prefs.PreferencesFactory} to this class's name
 * <i>before</i> anything touches {@link Preferences}; the factory is
 * captured once, on first use of that class.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class InMemoryPreferencesFactory implements PreferencesFactory {
    @Override
    public Preferences systemRoot() {
        return SYSTEM_ROOT;
    }

    @Override
    public Preferences userRoot() {
        return USER_ROOT;
    }

    private static final Preferences SYSTEM_ROOT = new InMemoryPreferences();
    private static final Preferences USER_ROOT = new InMemoryPreferences();

    /** Map-backed preferences node; all spi operations are in-memory. */
    private static class InMemoryPreferences extends AbstractPreferences {
        /** Constructs a root node. */
        InMemoryPreferences() {
            super(null, "");
        }

        /** Constructs a child node. */
        InMemoryPreferences(InMemoryPreferences parent, String name) {
            super(parent, name);
        }

        @Override
        protected void putSpi(String key, String value) {
            this.values.put(key, value);
        }

        @Override
        protected String getSpi(String key) {
            return this.values.get(key);
        }

        @Override
        protected void removeSpi(String key) {
            this.values.remove(key);
        }

        @Override
        protected void removeNodeSpi() {
            this.values.clear();
        }

        @Override
        protected String[] keysSpi() {
            return this.values.keySet().toArray(new String[0]);
        }

        @Override
        protected String[] childrenNamesSpi() {
            return this.children.keySet().toArray(new String[0]);
        }

        @Override
        protected AbstractPreferences childSpi(String name) {
            return this.children.computeIfAbsent(name, n -> new InMemoryPreferences(this, n));
        }

        @Override
        protected void syncSpi() {
            // in-memory: nothing to sync
        }

        @Override
        protected void flushSpi() {
            // in-memory: nothing to flush
        }

        private final Map<String,String> values = new HashMap<>();
        private final Map<String,InMemoryPreferences> children = new HashMap<>();
    }
}
