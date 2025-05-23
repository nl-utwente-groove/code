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

import java.io.File;
import java.io.IOException;

import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.graph.multi.MultiGraph;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter.ExportKind;
import nl.utwente.groove.io.external.Exporters;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Groove;

/**
 * Exploration reporter that saves the LTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LTSReporter extends AExplorationReporter {
    /** Constructs a new LTS reporter, for a given output file name pattern,
     * a set of format flags and an LTS filter.
     * @param filter determines which part of the LTS should be included
     */
    public LTSReporter(String filePattern, LTSLabels labels, LogReporter logger, Filter filter) {
        this.filePattern = filePattern;
        this.labels = labels == null
            ? LTSLabels.DEFAULT
            : labels;
        this.filter = filter;
        this.logger = logger;
    }

    @Override
    public void report() throws IOException {
        File outFile = exportLTS(getGTS(), this.filePattern, this.labels, this.filter,
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
        boolean internal = labels.showRecipes();
        ExplorationReporter.time("Create LTS fragment to be exported");
        var gtsFragment = switch (filter) {
        case NONE -> lts.toFragment(true, internal);
        case SPANNING -> lts.toFragment(false, internal);
        case RESULT -> answer.toFragment(internal);
        };
        ExplorationReporter.time("Turn LTS fragment into plain graph");
        MultiGraph ltsGraph = gtsFragment.toPlainGraph(labels, answer);
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
        var fileType = FileType.getType(outFile);
        var exporter = Exporters.getExporter(ExportKind.GRAPH, fileType);
        if (exporter == null) {
            if (!FileType.hasAnyExtension(outFile)) {
                outFile = FileType.GXL.addExtension(outFile);
            }
            Groove.saveGraph(ltsGraph, outFile);
        } else {
            var exportable = Exportable.graph(ltsGraph);
            if (!exporter.exports(exportable)) {
                exportable = Exportable.graph(gtsFragment);
            }
            if (exporter.exports(exportable)) {
                try {
                    ExplorationReporter.time("Do export");
                    exporter.doExport(exportable, outFile, fileType);
                } catch (PortException e1) {
                    throw new IOException(e1);
                }
            } else {
                throw new IOException("Exporter for %s refuses to process LTS graph");
            }

        }
        return outFile;
    }

    /** Placeholder in LTS and state filename patterns to insert further information. */
    static private final String PLACEHOLDER = "#";
}
