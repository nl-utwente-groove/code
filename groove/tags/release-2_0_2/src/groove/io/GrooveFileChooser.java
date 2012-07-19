// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: GrooveFileChooser.java,v 1.3 2007-09-04 20:59:36 rensink Exp $
 */
package groove.io;

import groove.util.Groove;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileView;

/**
 * A file chooser with a {@link GrooveFileView}, which prevents traversal of 
 * directories if these are selectable by the current file filter.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class GrooveFileChooser extends JFileChooser {
    /** File chooser with initial directory {@link Groove#WORKING_DIR}. */
    public GrooveFileChooser() {
        this(Groove.WORKING_DIR);
    }

    /**
     * File chooser with given initial directory.
     */
    public GrooveFileChooser(File currentDirectory) {
        super(currentDirectory);
        setFileView(createFileView());
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * File chooser with given initial directory.
     */
    public GrooveFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        setFileView(createFileView());
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * Indicates if the file view should allow traversal of directories.
     * @return <tt>false</tt> if the current file filter is a rule system filter
     */
    @Override
    public boolean isTraversable(File file) {
        return super.isTraversable(file) && !(getFileFilter() instanceof ExtensionFilter && ((ExtensionFilter) getFileFilter())
                .acceptExtension(file));
    }

    /** 
     * This implementation adds a file extension, if
     * the file filter used is an {@link ExtensionFilter}.
     */
    @Override
	public File getSelectedFile() {
    	File result = super.getSelectedFile();
    	if (result != null && getFileFilter() instanceof ExtensionFilter) {
    		ExtensionFilter fileFilter = (ExtensionFilter) getFileFilter();
    		String resultName = fileFilter.addExtension(result.getName());
    		result = new File(result.getParentFile(), resultName);
    	}
    	return result;
	}

	/**
     * Factory method for the file view set in this file chooser.
     * @return This implementation returns a {@link GrooveFileView}.
     */
    protected FileView createFileView() {
        return new GrooveFileView();
    }
}