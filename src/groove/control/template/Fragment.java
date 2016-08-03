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
 * $Id: Program.java 5781 2016-08-02 14:27:32Z rensink $
 */
package groove.control.template;

import groove.control.Procedure;
import groove.control.term.Term;
import groove.grammar.QualName;
import groove.util.parse.FormatException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Control program fragment, consisting of an optional main term
 * and a map from procedure names to procedures.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Fragment {
    /**
     * Constructs an unnamed, initially empty program.
     */
    public Fragment(QualName controlName) {
        this.controlName = controlName;
        this.procs = new LinkedHashMap<>();
    }

    /** Returns the control name of this program fragment. */
    public QualName getControlName() {
        return this.controlName;
    }

    private final QualName controlName;

    /**
     * Sets the main body of this program to a given term.
     * Should only be invoked if the program is not fixed.
     * @param main the main (non-{@code null}) body.
     */
    public void setMain(Term main) {
        assert main != null && this.main == null;
        this.main = main;
    }

    /**
     * Indicates if the program has a non-trivial main body.
     * This is the case if and only if {@link #getMain()} is non-{@code null} and
     * not deadlocked.
     */
    public boolean hasMain() {
        return getMain() != null;
    }

    /**
     * Returns the main block of this program.
     * This is never {@code null} if the program is fixed; however, it
     * may be a deadlocked term, in which case it is not counted as a proper main.
     */
    public Term getMain() {
        return this.main;
    }

    /** The main body of this program, if any. */
    private Term main;

    /**
     * Adds a procedure to this program.
     * @throws FormatException if a procedure with the same name has already been defined
     */
    public void addProc(Procedure proc) throws FormatException {
        assert proc != null;
        Procedure oldProc = this.procs.put(proc.getQualName(), proc);
        if (oldProc != null) {
            throw new FormatException("Duplicate procedure %s in %s and %s", proc.getQualName(),
                oldProc.getControlName(), proc.getControlName());
        }
    }

    /** Returns an unmodifiable view on the map from names to procedures
     * defined in this program.
     * Should only be invoked after the program is fixed.
     */
    public Map<QualName,Procedure> getProcs() {
        return Collections.unmodifiableMap(this.procs);
    }

    /** Returns the procedure for a given name.
     * Should only be invoked after the program is fixed.
     */
    public Procedure getProc(QualName name) {
        return this.procs.get(name);
    }

    /** Map from (qualified) names to procedures defined in this fragment. */
    private final Map<QualName,Procedure> procs;
}
