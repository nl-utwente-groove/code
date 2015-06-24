/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.explore.util;

import groove.grammar.aspect.GraphConverter;
import groove.io.FileType;
import groove.io.external.Exportable;
import groove.io.external.Exporter;
import groove.io.external.Exporters;
import groove.io.external.PortException;
import groove.lts.GraphState;
import groove.util.Groove;
import groove.util.Pair;

import java.io.File;
import java.io.IOException;

/**
 * Exploration reporter that saves the result states.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateReporter extends AExplorationReporter {
    /**
     * Constructs a state reporter with a given file name pattern.
     */
    public StateReporter(String statePattern, LogReporter logger) {
        this.statePattern = statePattern;
        this.logger = logger;
    }

    @Override
    public void report() throws IOException {
        Pair<FileType,Exporter> stateFormat = Exporters.getAcceptingFormat(this.statePattern);
        if (stateFormat == null) {
            this.logger.append("Pattern %s does not specify export format: states saved in native GXL%n",
                this.statePattern);
        } else {
            this.logger.append("States saved as %s%n", stateFormat.one().getDescription());
        }
        for (GraphState state : getExploration().getResult()) {
            File savedFile = exportState(state, this.statePattern);
            this.logger.append("State saved: %s%n", savedFile);
        }
    }

    private final LogReporter logger;
    private final String statePattern;

    /**
     * Exports a given state using a filename derived from a state pattern.
     * @param state the state to be exported
     * @param pattern the filename pattern
     * @throws IOException if anything went wrong during export
     */
    public static File exportState(GraphState state, String pattern) throws IOException {
        String stateFilename = pattern.replace(PLACEHOLDER, "" + state.getNumber());
        File stateFile = new File(stateFilename);
        Pair<FileType,Exporter> stateFormat = Exporters.getAcceptingFormat(stateFilename);
        if (stateFormat != null) {
            try {
                stateFormat.two().doExport(new Exportable(state.getGraph()),
                    stateFile,
                    stateFormat.one());
            } catch (PortException e1) {
                throw new IOException(e1);
            }
        } else {
            if (!FileType.hasAnyExtension(stateFile)) {
                stateFile = FileType.STATE.addExtension(stateFile);
            }
            Groove.saveGraph(GraphConverter.toAspect(state.getGraph()), stateFile);
        }
        return stateFile;
    }

    /** Placeholder in LTS and state filename patterns to insert further information. */
    static private final String PLACEHOLDER = "#";
}
