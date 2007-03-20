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
 * $Id: GraphFileHandler.java,v 1.1.1.1 2007-03-20 10:05:26 kastenberg Exp $
 */
package groove.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.List;

import groove.util.Groove;

/**
 * Class to facilitate operations that have to be performed upon a 
 * set of graph files, such as validation or transformation.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class GraphFileHandler {
    /** Lowest verbosity setting. */
    static public final int QUIET_MODE = 0;
    /** Medium verbosity setting. */
    static public final int NORMAL_MODE = 1;
    /** Highest verbosity setting. */
    static public final int VERBOSE_MODE = 2;

    /**
     * Constructs an instance with normal verbosity.
     * @ensure <code>getVerbosity() == NORMAL_MODE</code>
     */
    public GraphFileHandler() {
        this(NORMAL_MODE);
    }

    /**
     * Constructs an instance with given verbosity.
     * @param verbosity the required verbosity mode
     * @require <code>isVerbosity(verbosity)</code>
     * @ensure <code>getVerbosity() == verbosity</code>
     */
    public GraphFileHandler(int verbosity) {
        this.verbosity = verbosity;
    }
    
    /** Sets the verbosity of the treatment. */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /** Returns the verbosity of the treatment. */
    public int getVerbosity() {
        return verbosity;
    }

    /** 
     * Tests if a given number is a valid verbosity mode.
     * This implementation tests if the given verbosity is one of
     * <code>QUIET_MODE</code>, <code>NORMAL_MODE</code> or
     * <code>VERBOSE_MODE</code>.
     * @param verbosity the verbosity value to be validated
     * @return <code>true</code> if <code>verbosity</code> is a valid mode 
     */
    public boolean isVerbosity(int verbosity) {
        return verbosity == QUIET_MODE || verbosity == NORMAL_MODE || verbosity == VERBOSE_MODE;
    }

    /** 
     * Handles a file or directory.
     * Also recursively descends into subdirectories.
     * @param file file or directory to be handled
     */
    public void handle(File file) {
        if (file.isDirectory()) {
            handleDirectory(file);
        } else {
            handleFile(file);
        }
    }

    /** 
     * Handles a list of files and directories,
     * by calling <code>handle(File)</code> upon each element of the list.
     * @param list the list of files and directory to be handled
     */
    public void handle(List list) {
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            File file = (File) iter.next();
            handle(file);
        }
    }

    /** 
     * Handles all the files (recognised by one of the filters) in a directory.
     * Also recursively descends into subdirectories.
     * @param dir directory to be handled
     * @require <tt>file.isDirectory()</tt> 
     */
    public void handleDirectory(File dir) {
        if (verbosity == VERBOSE_MODE)
            System.out.println("Descending into " + dir.getPath());
        for (int i = 0; i < FILTERS.length; i++) {
            ExtensionFilter filter = FILTERS[i];
            if (verbosity == VERBOSE_MODE)
                System.out.println("Checking for " + filter.getExtension() + " files in " + dir.getName());
            File[] subfiles = dir.listFiles(filter);
            for (int j = 0; j < subfiles.length; j++)
                // note that none of the filters accept directories
                handleFile(subfiles[j]);
        }
        // now recursively descend into directories
        File[] subfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for (int j = 0; j < subfiles.length; j++)
            handleDirectory(subfiles[j]);
    }

    /** 
     * Handles a proper file, i.e., not a directory.
     * @param file file to be handled; should not be a directory.
     * @require <tt>! file.isDirectory()</tt> 
     */
    public void handleFile(File file) {
        boolean recognised = false;
        // check which file filter recognises this file
        for (int i = 0; !recognised && i < FILTERS.length; i++) {
            ExtensionFilter filter = FILTERS[i];
            if (filter.accept(file)) {
                recognised = true;
                // test if this is a rule filter
                if (filter.getExtension().equals(Groove.RULE_EXTENSION))
                    handleRule(file);
                else
                    // we don't recognise it as a rule, so it must be an ordinary graph
                    handleGraph(file);
            }
        }
        if (!recognised) {
            // we did not recognise the file; it must be a graph
            handleGraph(file);
        }
    }

    /** 
     * Handles a file supposedly containing a graph.
     * This method just prints a message (if the handler is not in
     * quiet mode); to be overwritten for specialized handling.
     * @param file the file recognized as containing a graph description
     */
    public void handleGraph(File file) {
        if (getVerbosity() != QUIET_MODE) {
            System.out.println("Handling graph file "+file);
        }
    }

    /** 
     * Handles a file supposedly containing a rule.
     * This method just prints a message (if the handler is not in
     * quiet mode); to be overwritten for specialized handling.
     * @param file the file recognized as containing a rule description
     */
    public void handleRule(File file) {
        if (getVerbosity() != QUIET_MODE) {
            System.out.println("Handling rule file "+file);
        }
    }

    protected final ExtensionFilter[] FILTERS =
        { Groove.createGxlFilter(false), Groove.createRuleFilter(false), Groove.createStateFilter(false)};

    /** Verbosity of validation: 0 = quiet, 1 = normal, 2 = verbose */
    protected int verbosity = 1;
}
