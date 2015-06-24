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

import groove.explore.ExploreResult;
import groove.graph.multi.MultiGraph;
import groove.io.FileType;
import groove.io.external.Exportable;
import groove.io.external.Exporter;
import groove.io.external.Exporters;
import groove.io.external.PortException;
import groove.lts.Filter;
import groove.lts.GTS;
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
    /** Constructs a new LTS reporter, for a given output file name pattern,
     * a set of format flags and an LTS filter.
     * @param filter determines which part of the LTS should be included
     */
    public LTSReporter(String filePattern, LTSLabels labels, LogReporter logger, Filter filter) {
        this.filePattern = filePattern;
        this.labels = labels == null ? LTSLabels.DEFAULT : labels;
        this.filter = filter;
        this.logger = logger;
    }

    @Override
    public void report() throws IOException {
        File outFile =
            exportLTS(getGTS(),
                this.filePattern,
                this.labels,
                this.filter,
                getExploration().getResult());
        this.logger.append("LTS saved as %s%n", outFile.getPath());
    }

    private final LogReporter logger;
    private final String filePattern;
    private final Filter filter;
    private final LTSLabels labels;

    /**
     * Saves a LTS as a plain graph under a given file name,
     * with options to label particular special states.
     * @param lts the LTS to be saved
     * @param filePattern string  to derive the file name and format from
     * @param labels options to label particular special states
     * @param filter determines which part of the LTS should be included
     * @param answer if non-{@code null}, the result that should be saved.
     * Only used if {@code filter} equals {@link Filter#RESULT}
     * @return the output file name
     * @throws IOException if any error occurred during export
     */
    static public File exportLTS(GTS lts, String filePattern, LTSLabels labels, Filter filter,
        ExploreResult answer) throws IOException {
        // Create the LTS view to be exported.
        MultiGraph ltsGraph = lts.toPlainGraph(labels, filter, answer);
        // Export GTS.
        String ltsName;
        File dir = new File(filePattern);
        if (dir.isDirectory()) {
            ltsName = PLACEHOLDER;
        } else {
            ltsName = dir.getName();
            dir = dir.getParentFile();
        }
        ltsName = ltsName.replace(PLACEHOLDER, lts.getGrammar().getId());
        File outFile = new File(dir, ltsName);
        Pair<FileType,Exporter> gtsFormat = Exporters.getAcceptingFormat(ltsName);
        if (gtsFormat != null) {
            try {
                gtsFormat.two().doExport(new Exportable(ltsGraph), outFile, gtsFormat.one());
            } catch (PortException e1) {
                throw new IOException(e1);
            }
        } else {
            if (!FileType.hasAnyExtension(outFile)) {
                outFile = FileType.GXL.addExtension(outFile);
            }
            Groove.saveGraph(ltsGraph, outFile);
        }
        return outFile;
    }

    /** Placeholder in LTS and state filename patterns to insert further information. */
    static private final String PLACEHOLDER = "#";
}
