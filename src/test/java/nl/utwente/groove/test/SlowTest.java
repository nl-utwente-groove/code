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

/**
 * Marker interface for slow (long-running) tests, used with
 * {@link org.junit.experimental.categories.Category}.
 * Tests in this category are excluded from the default Maven test run;
 * include them by running with {@code -Dexcluded.test.groups=}.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface SlowTest {
    // marker interface, empty by design
}
