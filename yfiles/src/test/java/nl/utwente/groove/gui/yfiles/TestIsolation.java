/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.yfiles;

import java.util.prefs.Preferences;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import nl.utwente.groove.util.AIGenerated;

/**
 * Cuts the test JVM off from the GROOVE installation of whoever runs the tests: the
 * preferences are kept in memory (see {@link InMemoryPreferencesFactory}). Without this,
 * a test run reads and writes the developer's real preferences.
 * <p>
 * This is governed by a system property that is read once, at the first use of what it
 * governs: {@link Preferences} caches its factory. Setting it from a test class is
 * therefore too late as soon as another test class ran first. Hence this listener, which the JUnit platform picks up through
 * {@link java.util.ServiceLoader} (see {@code META-INF/services}) and calls when it opens
 * the launcher session, before any test class is loaded -- whichever runner is used, so
 * also for an ad-hoc {@code Run as > JUnit test} in Eclipse, which passes no VM arguments
 * of its own. A property that already has a value is left alone, so a runner can still
 * dictate one.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public class TestIsolation implements LauncherSessionListener {
    // the inherited method's parameter is unconstrained
    @Override
    @NonNullByDefault({})
    public void launcherSessionOpened(LauncherSession session) {
        setIfUnset(PREFS_FACTORY_PROPERTY, InMemoryPreferencesFactory.class.getName());
    }

    /** Sets a system property, unless it has a value already. */
    private static void setIfUnset(String key, String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    /** System property from which {@link Preferences} takes its factory. */
    private static final String PREFS_FACTORY_PROPERTY = "java.util.prefs.PreferencesFactory";
}
