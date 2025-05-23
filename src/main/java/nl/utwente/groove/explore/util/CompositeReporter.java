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
package nl.utwente.groove.explore.util;

import static nl.utwente.groove.explore.util.ExplorationReporter.time;

import java.io.IOException;
import java.util.ArrayList;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.lts.GTS;

/**
 * Collection of reporters, acting like a single reporter.
 * @author Arend Rensink
 * @version $Revision$
 */
public class CompositeReporter extends ArrayList<ExplorationReporter>
    implements ExplorationReporter {
    @Override
    public void start(Exploration exploration, GTS gts) {
        for (ExplorationReporter reporter : this) {
            reporter.start(exploration, gts);
        }
    }

    @Override
    public void stop(GTS gts) {
        for (ExplorationReporter reporter : this) {
            reporter.stop(gts);
        }
    }

    @Override
    public void abort(GTS gts) {
        for (ExplorationReporter reporter : this) {
            reporter.abort(gts);
        }
    }

    @Override
    public void report() throws IOException {
        for (ExplorationReporter reporter : this) {
            time(reporter.getClass().getSimpleName());
            reporter.report();
        }
        time("Done reporting");
    }
}
