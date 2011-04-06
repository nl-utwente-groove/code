/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: Exporter.java,v 1.10 2008-03-04 22:03:36 rensink Exp $
 */
package groove.io.external;

import groove.gui.jgraph.GraphJGraph;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.external.format.AutFormat;
import groove.io.external.format.EpsFormat;
import groove.io.external.format.ExternalFileFormat;
import groove.io.external.format.FsmFormat;
import groove.io.external.format.JpgFormat;
import groove.io.external.format.KthFormat;
import groove.io.external.format.PngFormat;
import groove.io.external.format.TikzFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * Class providing functionality to export a {@link GraphJGraph} to a file in
 * different formats.
 * @author Arend Rensink
 * @version $Revision: 3144 $
 */
public class Exporter {
    /**
     * Returns a file chooser for exporting, lazily creating it first.
     */
    public JFileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser =
                GrooveFileChooser.getFileChooser(this.getFilters());
            this.fileChooser.setFileFilter(this.getDefaultFormat().getFilter());
        }
        return this.fileChooser;
    }

    /**
     * Exports the current state to a given format. The format is deduced from
     * the file name, using known file filters.
     */
    public void export(GraphJGraph jGraph, File file) throws IOException {
        ExternalFileFormat<?> format = this.getAcceptingFormat(file);
        if (format != null) {
            format.save(jGraph, file);
        }
    }

    /**
     * Returns a file format that accepts the file.
     */
    public ExternalFileFormat<?> getAcceptingFormat(File file) {
        ExternalFileFormat<?> result = null;
        for (ExternalFileFormat<?> format : this.getFormatList()) {
            if (format.getFilter().accept(file)) {
                result = format;
                break;
            }
        }
        return result;
    }

    /** Returns the list of file extensions of the supported formats. */
    public List<String> getExtensions() {
        List<String> result = new ArrayList<String>();
        for (ExternalFileFormat<?> format : this.getFormatList()) {
            result.add(format.getFilter().getExtension());
        }
        return result;
    }

    /** Returns the default format. */
    public ExternalFileFormat<?> getDefaultFormat() {
        return PngFormat.getInstance();
    }

    /** Returns the list of extension filters of the supported formats. */
    private List<ExtensionFilter> getFilters() {
        List<ExtensionFilter> result = new ArrayList<ExtensionFilter>();
        for (ExternalFileFormat<?> format : this.getFormatList()) {
            result.add(format.getFilter());
        }
        return result;
    }

    /** Returns the (modifiable) list of currently supported formats. */
    private List<ExternalFileFormat<?>> getFormatList() {
        if (this.formats == null) {
            this.formats = new ArrayList<ExternalFileFormat<?>>();
            this.formats.add(AutFormat.getInstance());
            this.formats.add(FsmFormat.getInstance());
            this.formats.add(JpgFormat.getInstance());
            this.formats.add(PngFormat.getInstance());
            this.formats.add(EpsFormat.getInstance());
            this.formats.add(TikzFormat.getInstance());
            this.formats.add(KthFormat.getInstance());
        }
        return this.formats;
    }

    /** The file chooser of this exporter. */
    private GrooveFileChooser fileChooser;
    /** List of the supported export formats. */
    private List<ExternalFileFormat<?>> formats;

}
