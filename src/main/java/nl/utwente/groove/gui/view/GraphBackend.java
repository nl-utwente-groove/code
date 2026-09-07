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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.Options;
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
     * Returns the name of this backend: a short lower-case key, stable across
     * releases, by which the backend is ranked (see {@link #RANKING}) and
     * chosen by the user (see {@link Options#GRAPH_BACKEND_OPTION}).
     */
    String getName();

    /** Returns the name of this backend as shown to the user. */
    default String getDisplayName() {
        return getName();
    }

    /**
     * Returns the backend selected for this run.
     * The backends available are discovered once through the {@link ServiceLoader};
     * a provider that fails to instantiate is logged and skipped. Among the available
     * backends the one named by the user preference {@link Options#GRAPH_BACKEND_OPTION}
     * is selected if it is available, otherwise the one ranking first in {@link #RANKING};
     * backends not in the ranking come last, in discovery order. There is no runtime
     * switching: a changed preference takes effect at the next start.
     * @throws IllegalStateException if no backend is available
     */
    static GraphBackend instance() {
        return Instance.INSTANCE;
    }

    /** Returns the backends available in this run, in discovery order. */
    static List<GraphBackend> available() {
        return Instance.AVAILABLE;
    }

    /**
     * Selects a backend among a non-empty list of available ones: the one with the
     * preferred name if there is one, otherwise the first in the {@link #RANKING}.
     * @param available the available backends, in discovery order
     * @param preferred the name of the preferred backend; {@code null} if there is none
     */
    static GraphBackend select(List<GraphBackend> available, @Nullable String preferred) {
        if (available.isEmpty()) {
            throw Exceptions.illegalState("No graph backend available");
        }
        for (var backend : available) {
            if (backend.getName().equals(preferred)) {
                return backend;
            }
        }
        return available
            .stream()
            .min(Comparator.comparingInt(Instance::rank))
            .orElseThrow();
    }

    /** Name of the JGraph backend, the one every distribution has. */
    String JGRAPH = "jgraph";
    /** Name of the yFiles backend, present only in the yFiles edition. */
    String YFILES = "yfiles";
    /**
     * Backend names in order of preference: the yFiles backend is the target of the
     * migration, so it is used whenever it is available and the user has not chosen
     * otherwise.
     */
    List<String> RANKING = List.of(YFILES, JGRAPH);

    /** Lazy holder of the available and the selected backend instances. */
    final class Instance {
        private Instance() {
            // not to be instantiated
        }

        /** Declared before {@link #INSTANCE}, whose initialisation logs. */
        private static final Logger LOGGER = Log.getLogger("gui.backend");

        static final List<GraphBackend> AVAILABLE = discover();

        static final GraphBackend INSTANCE = create();

        private static List<GraphBackend> discover() {
            List<GraphBackend> result = new ArrayList<>();
            for (var provider : ServiceLoader.load(GraphBackend.class).stream().toList()) {
                try {
                    result.add(provider.get());
                } catch (ServiceConfigurationError exc) {
                    LOGGER
                        .log(Level.WARNING, "Graph backend {0} unavailable: {1}",
                             provider.type().getName(), exc);
                }
            }
            return List.copyOf(result);
        }

        private static GraphBackend create() {
            String preferred = Options.userPrefs.get(Options.GRAPH_BACKEND_OPTION, null);
            var result = select(AVAILABLE, preferred);
            LOGGER
                .log(Level.DEBUG, "Graph backend: {0} (available: {1}, preferred: {2})",
                     result.getName(), AVAILABLE.stream().map(GraphBackend::getName).toList(),
                     preferred);
            return result;
        }

        /** Returns the position of a backend in the ranking; unranked backends come last. */
        private static int rank(GraphBackend backend) {
            int result = RANKING.indexOf(backend.getName());
            return result < 0
                ? RANKING.size()
                : result;
        }
    }
}
