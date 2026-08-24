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

package nl.utwente.groove.test;

import nl.utwente.groove.util.AIGenerated;

/**
 * Marker for GUI tests: tests that open real Swing windows and drive them
 * through a UI robot, and therefore need a display.
 * Like {@link SlowTest}, tests in this category are excluded from the default
 * Maven test run (see the {@code excluded.test.groups} pom property); unlike
 * {@link SlowTest}, they additionally skip themselves in headless environments,
 * so a full run with {@code -Dexcluded.test.groups=} stays safe on displayless
 * CI. Run everything except the GUI tests with
 * {@code -Dexcluded.test.groups=nl.utwente.groove.test.GuiTest}.
 * <p>
 * JUnit 4 (vintage) tests use this class with
 * {@link org.junit.experimental.categories.Category}; JUnit 5 (Jupiter) tests
 * use {@link #TAG} with {@code @Tag}. Both are matched by the same
 * {@code excluded.test.groups} value, since the vintage engine exposes
 * categories as tags named by their fully qualified class name.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public interface GuiTest {
    /** This category's tag name for JUnit 5 tests: the qualified class name. */
    String TAG = "nl.utwente.groove.test.GuiTest";
}
