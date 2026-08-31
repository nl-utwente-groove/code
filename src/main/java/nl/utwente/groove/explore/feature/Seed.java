/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore.feature;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Randomness;

/**
 * Feature values for {@link ExploreKey#SEED}: the master random seed (see
 * {@link Randomness}) governing the randomised exploration features and the
 * unseeded random value oracle. An explicit seed makes such a run
 * reproducible; in particular, the seed recorded in a saved LTS (gh #897) can
 * be fed back through this key to reproduce the run that produced it.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public enum Seed implements Setting.Kind {
    /** Keep the master seed as resolved outside the configuration. */
    AUTO("auto",
        "The master seed is left as it is: the -seed option or the groove.randomSeed"
            + " system property if given, a freshly generated (and reported) seed otherwise",
        Setting.ContentType.NULL),
    /** Explicit seed value. */
    VALUE("value",
        "The given value becomes the master seed for the rest of the session,"
            + " making the randomised features of this and subsequent explorations reproducible",
        Setting.ContentType.LONG),;

    private Seed(String name, String explanation, Setting.ContentType contentType) {
        this.name = name;
        this.explanation = explanation;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Setting.ContentType contentType() {
        return this.contentType;
    }

    private final Setting.ContentType contentType;
}
