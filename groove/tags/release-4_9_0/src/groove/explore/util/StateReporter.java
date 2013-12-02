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
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.external.Exporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.FormatExporter;
import groove.io.external.PortException;
import groove.lts.GraphState;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Exploration reporter that saves the result states.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateReporter extends ExplorationReporter {
    /**
     * Constructs a state reporter with a given file name pattern.
     */
    public StateReporter(String statePattern) {
        this.statePattern = statePattern;
    }

    @Override
    public void report() throws IOException {
        Collection<? extends GraphState> export =
            getExploration().getResult().getValue();
        for (GraphState state : export) {
            String stateFilename =
                this.statePattern.replace(PLACEHOLDER, "" + state.getNumber());
            File stateFile = new File(stateFilename);
            Format stateFormat =
                Exporter.getAcceptingFormat(state.getGraph(), stateFile);
            if (stateFormat != null) {
                try {
                    ((FormatExporter) stateFormat.getFormatter()).doExport(
                        stateFile, stateFormat,
                        new Exportable(state.getGraph()));
                } catch (PortException e1) {
                    throw new IOException(e1);
                }
            } else {
                ExtensionFilter gstFilter = FileType.STATE_FILTER;
                if (!gstFilter.hasAnyExtension(stateFilename)) {
                    stateFile = new File(gstFilter.addExtension(stateFilename));
                }
                Groove.saveGraph(GraphConverter.toAspect(state.getGraph()),
                    stateFile);
            }
        }
    }

    private final String statePattern;
    /** Placeholder in LTS and state filename patterns to insert further information. */
    static private final String PLACEHOLDER = "#";
}
