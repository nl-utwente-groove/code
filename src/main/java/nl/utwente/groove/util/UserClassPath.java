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
 */
package nl.utwente.groove.util;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The installation-level user class path: directories and jars, outside any
 * grammar, from which the classes named in a grammar's user operations property
 * are loaded. The value is kept in the user preferences, so that it is set once
 * per installation (through the Simulator's options or {@link #setStored}), and
 * is overridden for a single run by the system property {@link #PROPERTY}, which
 * the command-line tools set from their {@code --user-classpath} option.
 * @author Arend Rensink
 */
@AIGenerated("Claude Fable 5.1, 2026-10")
@NonNullByDefault
public final class UserClassPath {
    private UserClassPath() {
        // no instances
    }

    /** Returns the effective user class path: the value of the system property
     * {@link #PROPERTY} if it is set, otherwise the stored preference.
     */
    public static List<Path> get() {
        String override = System.getProperty(PROPERTY);
        return override == null
            ? getStored()
            : parse(override);
    }

    /** Returns the user class path stored in the preferences. */
    public static List<Path> getStored() {
        return parse(prefs().get(KEY, ""));
    }

    /** Stores a user class path in the preferences; an empty list removes the preference. */
    public static void setStored(List<Path> entries) {
        if (entries.isEmpty()) {
            prefs().remove(KEY);
        } else {
            prefs().put(KEY, format(entries));
        }
    }

    /** Parses a class path string into its entries: the non-blank segments between
     * {@link File#pathSeparator}s, trimmed.
     */
    public static List<Path> parse(@Nullable String text) {
        var result = new ArrayList<Path>();
        if (text != null) {
            for (var entry : text.split(File.pathSeparator)) {
                var trimmed = entry.trim();
                if (!trimmed.isEmpty()) {
                    result.add(Path.of(trimmed));
                }
            }
        }
        return result;
    }

    /** Formats a list of entries as a class path string, inverse to {@link #parse}. */
    public static String format(List<Path> entries) {
        return entries.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
    }

    private static Preferences prefs() {
        return Preferences.userNodeForPackage(UserClassPath.class);
    }

    /** System property that overrides the stored user class path for one run. */
    public static final String PROPERTY = "groove.user.classpath";
    /** Preference key under which the user class path is stored. */
    private static final String KEY = "userClassPath";
}
