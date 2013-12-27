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
import groove.io.external.Exporters;
import groove.io.external.Exporter;
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
        for (GraphState state : getGTS().getResultStates()) {
            String stateFilename =
                this.statePattern.replace(PLACEHOLDER, "" + state.getNumber());
            File stateFile = new File(stateFilename);
            Pair<FileType,Exporter> stateFormat =
                Exporters.getAcceptingFormat(state.getGraph(), stateFile);
            if (stateFormat != null) {
                try {
                    stateFormat.two().doExport(new Exportable(state.getGraph()), stateFile,
                        stateFormat.one());
                } catch (PortException e1) {
                    throw new IOException(e1);
                }
            } else {
                if (!FileType.hasAnyExtension(stateFile)) {
                    stateFile = FileType.STATE.addExtension(stateFile);
                }
                Groove.saveGraph(GraphConverter.toAspect(state.getGraph()),
                    stateFile);
            }
        }
        this.logger.append("States saved as %s%n", this.statePattern);
    }

    private final LogReporter logger;
    private final String statePattern;
    /** Placeholder in LTS and state filename patterns to insert further information. */
    static private final String PLACEHOLDER = "#";
}
