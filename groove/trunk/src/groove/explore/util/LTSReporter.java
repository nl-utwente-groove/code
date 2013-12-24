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

import groove.graph.plain.PlainGraph;
import groove.io.FileType;
import groove.io.external.Exportable;
import groove.io.external.Exporters;
import groove.io.external.Exporter;
import groove.io.external.PortException;
import groove.util.Groove;
import groove.util.Pair;

import java.io.File;
import java.io.IOException;

/**
 * Exploration reporter that saves the LTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSReporter extends AExplorationReporter {
    /** Constructs a new LTS reporter, for a given output file name pattern
     * and set of format flags.
     */
    public LTSReporter(String filePattern, LTSLabels labels, LogReporter logger) {
        this.filePattern = filePattern;
        this.labels = labels == null ? LTSLabels.EMPTY : labels;
        this.logger = logger;
    }

    @Override
    public void report() throws IOException {
        // Create the LTS view to be exported.
        PlainGraph lts = getGTS().toPlainGraph(this.labels);
        // Export GTS.
        String outFilename =
            this.filePattern.replace(PLACEHOLDER, getGTS().getGrammar().getId());
        File outFile = new File(outFilename);
        Pair<FileType,Exporter> gtsFormat =
            Exporters.getAcceptingFormat(lts, outFile);
        if (gtsFormat != null) {
            try {
                gtsFormat.two().doExport(new Exportable(lts), outFile,
                    gtsFormat.one());
            } catch (PortException e1) {
                throw new IOException(e1);
            }
        } else {
            if (!FileType.hasAnyExtension(outFilename)) {
                outFile = new File(FileType.GXL.addExtension(outFilename));
            }
            Groove.saveGraph(lts, outFile);
        }
        this.logger.append("LTS saved as %s%n", outFilename);
    }

    private final LogReporter logger;
    private final String filePattern;
    private final LTSLabels labels;

    /** Placeholder in LTS and state filename patterns to insert further information. */
    static private final String PLACEHOLDER = "#";
}
