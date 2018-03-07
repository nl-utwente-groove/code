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
package groove.io.graph;

import java.io.File;
import java.net.URL;

import groove.grammar.Action;
import groove.grammar.Rule;
import groove.io.FileType;
import groove.util.Groove;

/**
 * Encoding of a rule name plus priority as a string
 * @author Arend Rensink
 * @version $Revision$
 */
public class PriorityFileName {
    /** Default priority value, copied from {@link Rule#DEFAULT_PRIORITY}. */
    static public final int DEFAULT_PRIORITY = Action.DEFAULT_PRIORITY;
    /** Separator between priority and actual file name. */
    static public final String SEPARATOR = ".";

    /**
     * Parses a string as <tt>priority.ruleName</tt>. If there is no period in
     * the string, the priority is assumed to be 0.
     * @throws NumberFormatException if the part before the period cannot be
     *         parsed as an integer, or yields a negative number.
     */
    public PriorityFileName(String fullName) {
        int separatorPos = fullName.indexOf(SEPARATOR);
        if (separatorPos <= 0) {
            this.priority = DEFAULT_PRIORITY;
            this.explicitPriority = false;
        } else {
            int startNumber;
            boolean validStartNumber;
            try {
                startNumber = Integer.valueOf(fullName.substring(0, separatorPos));
                validStartNumber = true;
            } catch (NumberFormatException nfe) {
                startNumber = DEFAULT_PRIORITY;
                validStartNumber = false;
                separatorPos = -1;
            }
            this.priority = startNumber;
            this.explicitPriority = validStartNumber;
            if (this.priority < 0) {
                throw new NumberFormatException("Invalid rule priority " + this.priority);
            }
        }
        this.ruleName = fullName.substring(separatorPos + 1);
    }

    /**
     * Parses the name part of a file as <tt>priority.actualName.extension</tt>.
     * If there is no period in the string, or the part before the period is not
     * an integer, the priority is assumed to be 0.
     */
    public PriorityFileName(File file) {
        this(FileType.getPureName(file));
        this.extension = file.getName()
            .substring(this.ruleName.length());
    }

    /**
     * Convenience method to create a PriorityFileName for a URL.
     */
    public PriorityFileName(URL url) {
        this(Groove.toFile(url));
    }

    /**
     * Creates a file name from a given rule name and (possibly explicit)
     * priority.
     */
    public PriorityFileName(String ruleName, int priority, boolean explicitPriority) {
        this.ruleName = ruleName;
        this.priority = priority;
        this.explicitPriority = explicitPriority;
    }

    /**
     * Creates a file name from a given rule name and priority. The priority is
     * taken to be explicit if and only if it does not equal
     * {@link #DEFAULT_PRIORITY}.
     */
    PriorityFileName(String ruleName, int priority) {
        this(ruleName, priority, priority != DEFAULT_PRIORITY);
    }

    /**
     * Returns the priority.
     */
    public Integer getPriority() {
        return Integer.valueOf(this.priority);
    }

    /** Indicates if the file name has an explicit priority. */
    public boolean hasPriority() {
        return this.explicitPriority;
    }

    /**
     * Returns the rule name.
     */
    public String getActualName() {
        return this.ruleName;
    }

    /**
     * Returns the extension, in case this object was constructed by
     * {@link #PriorityFileName(String)}.
     * @return an extension (including the separator), or <code>null</code> if
     *         this object was not constructed so as to include an extension.
     */
    public String getExtension() {
        return this.extension;
    }

    /**
     * Returns the string from which this object was created.
     */
    @Override
    public String toString() {
        if (this.explicitPriority) {
            return "" + this.priority + SEPARATOR + this.ruleName;
        } else {
            return this.ruleName;
        }
    }

    /**
     * Flag that indicates if the string from which this object was created
     * contained an explicit priority.
     */
    private final boolean explicitPriority;
    /**
     * The priority in the filename, or {@link Rule#DEFAULT_PRIORITY} if no
     * explicit priority was incorporated.
     */
    private final int priority;
    /** The actual rule name, minus directory, priority and extension. */
    private final String ruleName;
    /**
     * The file name extension, in case this object was created by
     * {@link #PriorityFileName(File)}.
     */
    private String extension;
}
