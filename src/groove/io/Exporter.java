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
package groove.io;

import groove.gui.jgraph.GraphJGraph;
import groove.io.format.AutFormat;
import groove.io.format.EpsFormat;
import groove.io.format.FileFormat;
import groove.io.format.FsmFormat;
import groove.io.format.JpgFormat;
import groove.io.format.KthFormat;
import groove.io.format.PngFormat;
import groove.io.format.TikzFormat;

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
            this.fileChooser = new GrooveFileChooser();
            this.fileChooser.setAcceptAllFileFilterUsed(false);
            for (FileFormat<?> format : this.getFormatList()) {
                this.fileChooser.addChoosableFileFilter(format.getFilter());
            }
            this.fileChooser.setFileFilter(this.getDefaultFormat().getFilter());
        }
        return this.fileChooser;
    }

    /**
     * Exports the current state to a given format. The format is deduced from
     * the file name, using known file filters.
     */
    public void export(GraphJGraph jGraph, File file) throws IOException {
        FileFormat<?> format = this.getAcceptingFormat(file);
        if (format != null) {
            format.save(jGraph, file);
        }
    }

    /**
     * Returns a file format that accepts the file.
     */
    public FileFormat<?> getAcceptingFormat(File file) {
        FileFormat<?> result = null;
        for (FileFormat<?> format : this.getFormatList()) {
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
        for (FileFormat<?> format : this.getFormatList()) {
            result.add(format.getFilter().getExtension());
        }
        return result;
    }

    /** Returns the default format. */
    public FileFormat<?> getDefaultFormat() {
        return PngFormat.getInstance();
    }

    /** Returns the (modifiable) list of currently supported formats. */
    private List<FileFormat<?>> getFormatList() {
        if (this.formats == null) {
            this.formats = new ArrayList<FileFormat<?>>();
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
    private List<FileFormat<?>> formats;

}
