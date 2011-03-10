/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.io.external;

import groove.io.GrooveFileChooser;
import groove.io.external.format.AutFormat;
import groove.io.external.format.ColFormat;
import groove.io.external.format.ExternalFileFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * Class providing functionality to import a graph from a file in
 * different formats.
 * 
 * @author Eduardo Zambon
 */
public class Importer {
    /**
     * Returns a file chooser for importing, lazily creating it first.
     */
    public JFileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser = new GrooveFileChooser();
            this.fileChooser.setAcceptAllFileFilterUsed(false);
            for (ExternalFileFormat<?> format : this.getFormatList()) {
                this.fileChooser.addChoosableFileFilter(format.getFilter());
            }
            this.fileChooser.setFileFilter(this.getDefaultFormat().getFilter());
        }
        return this.fileChooser;
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
        return AutFormat.getInstance();
    }

    /** Returns the (modifiable) list of currently supported formats. */
    private List<ExternalFileFormat<?>> getFormatList() {
        if (this.formats == null) {
            this.formats = new ArrayList<ExternalFileFormat<?>>();
            this.formats.add(AutFormat.getInstance());
            this.formats.add(ColFormat.getInstance());
        }
        return this.formats;
    }

    /** The file chooser of this exporter. */
    private GrooveFileChooser fileChooser;
    /** List of the supported import formats. */
    private List<ExternalFileFormat<?>> formats;
}
