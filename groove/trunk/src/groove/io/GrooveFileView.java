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
 * $Id$
 */
package groove.io;

import static groove.io.FileType.CONTROL;
import static groove.io.FileType.GRAMMAR;
import static groove.io.FileType.GXL;
import static groove.io.FileType.JAR;
import static groove.io.FileType.PROLOG;
import static groove.io.FileType.RULE;
import static groove.io.FileType.STATE;
import static groove.io.FileType.TYPE;
import static groove.io.FileType.ZIP;
import groove.gui.Icons;

import java.io.File;

import javax.swing.Icon;

/**
 * Implements a file view that displays the correct icons and descriptions for
 * graph files, rules files and grammar directories.
 * @version $Date: 2008-01-30 09:33:42 $ $Revision$
 * @author Arend Rensink
 */
public class GrooveFileView extends javax.swing.filechooser.FileView {
    /**
     * Constructs a standard file view, in which production system directories
     * can be traversed.
     */
    GrooveFileView() {
        setGpsTraversable(true);
    }

    /**
     * Returns an appropriate icon for graph files, rules files and graph
     * production system folders.
     * @param f the file to be tested
     * @require <tt>f != null</tt>
     */
    @Override
    public Icon getIcon(File f) {
        if (isGpsFolder(f)) {
            return Icons.GPS_FOLDER_ICON;
        } else if (isCompressedGpsFolder(f)) {
            return Icons.GPS_COMPRESSED_FOLDER_ICON;
        } else if (isGraphFile(f)) {
            return Icons.GRAPH_FILE_ICON;
        } else if (isRuleFile(f)) {
            return Icons.RULE_FILE_ICON;
        } else if (isTypeFile(f)) {
            return Icons.TYPE_FILE_ICON;
        } else if (isControlFile(f)) {
            return Icons.CONTROL_FILE_ICON;
        } else if (isPrologFile(f)) {
            return Icons.PROLOG_FILE_ICON;
        } else {
            return null;
        }
    }

    /**
     * Returns an appropriate description for files recognised by the groove
     * tools.
     * @param f the file to be described
     * @require <tt>f != null</tt>
     */
    @Override
    public String getDescription(File f) {
        if (isControlFile(f)) {
            return "A control program";
        } else if (isRuleFile(f)) {
            return "A graph production rule";
        } else if (isTypeFile(f)) {
            return "A graph type";
        } else if (isStateFile(f)) {
            return "A state graph";
        } else if (isGpsFolder(f)) {
            return "A graph production system";
        } else if (isGxlFile(f)) {
            return "A GXL-formatted graph";
        } else {
            return null;
        }
    }

    /**
     * Returns an appropriate type description for files recognized by the
     * groove tools.
     * @param f the file to be described
     * @require <tt>f != null</tt>
     */
    @Override
    public String getTypeDescription(File f) {
        return getDescription(f);
    }

    /**
     * Overrides the superclass implementation so that directories deemed to
     * represent entire production systems are not traversable, unless indicated
     * otherwise by <tt>isGpsTraversable()</tt>.
     * @param f the file of which the traversability is to be determined
     * @see #isGpsTraversable()
     */
    @Override
    public Boolean isTraversable(File f) {
        Boolean superTraversable = super.isTraversable(f);
        if (isGpsTraversable()) {
            return superTraversable;
        } else {
            return Boolean.valueOf((superTraversable == null || superTraversable.booleanValue())
                && !isGpsFolder(f));
        }
    }

    /**
     * Indicates if production system directories are traversable.
     * @see #isTraversable(File)
     */
    public boolean isGpsTraversable() {
        return this.gpsTraversable;
    }

    /**
     * Changes the traversability of production system directories.
     * @param gpsTraversable indicates if production system directories should
     *        henceforth be traversable
     * @ensure <tt>isGpsTraversable() == gpsTraversable</tt>
     */
    public void setGpsTraversable(boolean gpsTraversable) {
        this.gpsTraversable = gpsTraversable;
    }

    private boolean gpsTraversable;

    /**
     * Tests whether a given file is a control program.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a control program file
     */
    static protected boolean isControlFile(File f) {
        return CONTROL.hasExtension(f);
    }

    /**
     * Tests whether a given file is a graph file recognised by this program.
     * This is the case if it is either a gxl file or a state file.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a graph file
     * @require <tt>f != null</tt>
     */
    static protected boolean isGraphFile(File f) {
        return isStateFile(f) || isGxlFile(f);
    }

    /**
     * Tests whether a given file is a state file recognised by this program.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a state file
     * @require <tt>f != null</tt>
     */
    static protected boolean isStateFile(File f) {
        return STATE.hasExtension(f);
    }

    /**
     * Tests whether a given file is a gxl file recognised by this program.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a gxl file
     * @require <tt>f != null</tt>
     */
    static protected boolean isGxlFile(File f) {
        return GXL.hasExtension(f);
    }

    /**
     * Tests whether a given file is a prolog program.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a prolog program file
     */
    static protected boolean isPrologFile(File f) {
        return PROLOG.hasExtension(f);
    }

    /**
     * Tests whether a given file is a production rule file.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a production rule file
     * @require <tt>f != null</tt>
     */
    static protected boolean isRuleFile(File f) {
        return RULE.hasExtension(f);
    }

    /**
     * Tests whether a given file is a graph type file.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a graph type file
     */
    static protected boolean isTypeFile(File f) {
        return TYPE.hasExtension(f);
    }

    /**
     * Tests whether a given file is a GPS (graph production system) folder,
     * i.e., a folder containing rule files.
     * @param f the file to be tested
     * @return <tt>true</tt> if <code>f</code> is a GPS folder
     * @require <tt>f != null</tt>
     */
    static protected boolean isGpsFolder(File f) {
        return GRAMMAR.hasExtension(f);
    }

    /** Tests whether a given file is a compressed GPS. */
    static protected boolean isCompressedGpsFolder(File f) {
        return ZIP.hasExtension(f) || JAR.hasExtension(f);
    }
}
