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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Log;

/**
 * Factory for the canvases of one graph-visualisation backend.
 * A controller obtains its canvas from the backend selected at start-up,
 * see {@link #instance()}; the canvas attaches itself to the controller
 * during construction (see {@link GraphViewController#attachCanvas}).
 * <p>
 * Backends are services: each backend module declares its implementation
 * with {@code provides} (and in {@code META-INF/services}, for the class path),
 * so what is on the module path is what is available.
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
     * The backends available are discovered once through the {@link ServiceLoader};
     * the first that can be instantiated is used, a provider that fails is logged
     * and skipped. There is no runtime switching.
     * @throws IllegalStateException if no backend is available
     */
    static GraphBackend instance() {
        return Instance.INSTANCE;
    }

    /** Lazy holder of the selected backend instance. */
    final class Instance {
        private Instance() {
            // not to be instantiated
        }

        /** Declared before {@link #INSTANCE}, whose initialisation logs. */
        private static final Logger LOGGER = Log.getLogger("gui.backend");

        static final GraphBackend INSTANCE = create();

        private static GraphBackend create() {
            for (var provider : ServiceLoader.load(GraphBackend.class).stream().toList()) {
                try {
                    var result = provider.get();
                    LOGGER.log(Level.DEBUG, "Graph backend: {0}", result.getClass().getName());
                    return result;
                } catch (ServiceConfigurationError exc) {
                    LOGGER
                        .log(Level.WARNING, "Graph backend {0} unavailable: {1}",
                             provider.type().getName(), exc);
                }
            }
            throw Exceptions.illegalState("No graph backend available");
        }
    }
}
