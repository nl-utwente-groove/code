/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.view;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;

/**
 * Factory for the canvases of one graph-visualisation backend.
 * A controller obtains its canvas from the backend selected at start-up,
 * see {@link #instance()}; the canvas attaches itself to the controller
 * during construction (see {@link GraphViewController#attachCanvas}).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public interface GraphBackend {
    /** Creates a canvas for aspect graphs, for a given controller. */
    AspectGraphCanvas newAspectCanvas(AspectGraphViewController controller);

    /** Creates a canvas for LTSs, for a given controller. */
    LTSGraphCanvas newLTSCanvas(LTSGraphViewController controller);

    /** Creates a canvas for control graphs, for a given controller. */
    CtrlGraphCanvas newCtrlCanvas(CtrlGraphViewController controller);

    /** Creates a canvas for plain graphs, for a given controller. */
    GraphCanvas<Graph> newPlainCanvas(PlainGraphViewController controller);

    /**
     * Returns the backend selected for this run.
     * The selection is read once from the system property {@link #PROPERTY},
     * whose value is one of the keys of {@link #BACKENDS}; the default is
     * {@link #JGRAPH}. There is no runtime switching.
     */
    static GraphBackend instance() {
        return Instance.INSTANCE;
    }

    /** System property selecting the backend; see {@link #instance()}. */
    String PROPERTY = "groove.gui.backend";
    /** Key of the JGraph backend, the default. */
    String JGRAPH = "jgraph";
    /**
     * Map from backend keys to the names of the implementing classes.
     * Held as names rather than as classes so that this package does not depend
     * on any backend package.
     */
    Map<String,String> BACKENDS = Map.of(JGRAPH, "nl.utwente.groove.gui.jgraph.JGraphBackend");

    /** Lazy holder of the selected backend instance. */
    final class Instance {
        private Instance() {
            // not to be instantiated
        }

        static final GraphBackend INSTANCE = create();

        private static GraphBackend create() {
            String key = System.getProperty(PROPERTY);
            if (key == null) {
                key = JGRAPH;
            }
            String className = BACKENDS.get(key);
            if (className == null) {
                throw Exceptions
                    .illegalArg("Unknown graph backend '%s' in system property %s; known: %s",
                                key, PROPERTY, BACKENDS.keySet());
            }
            try {
                var instance = Class.forName(className).getDeclaredConstructor().newInstance();
                return (GraphBackend) instance;
            } catch (ReflectiveOperationException | ClassCastException exc) {
                throw Exceptions
                    .illegalState("Cannot instantiate graph backend '%s' (%s): %s", key,
                                  className, exc);
            }
        }
    }
}
