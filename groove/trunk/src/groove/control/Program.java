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
package groove.control;

import groove.control.Switch.Kind;
import groove.util.DefaultFixable;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Control program, consisting of a main template
 * and templates for all procedures.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Program extends DefaultFixable {
    /** Constructs a program with a main template. */
    public Program(Template main) {
        this.main = main;
    }

    /** Returns the main template of this program.
     */
    public Template getMain() {
        return this.main;
    }

    /**
     * Returns the name of this program, which is the same as
     * the name of its main template.
     */
    public String getName() {
        return this.main.getName();
    }

    private final Template main;

    /** Adds a procedure to this program. */
    public void addProc(Procedure proc) {
        assert !isFixed();
        Procedure oldProc = this.procs.put(proc.getFullName(), proc);
        assert oldProc == null : String.format("Procedure %s already defined",
            proc.getFullName());
    }

    /** Returns an unmodifiable view on the map from names to procedures
     * defined in this program.
     * Should only be invoked after the program is fixed. 
     */
    public Map<String,Procedure> getProcs() {
        assert isFixed();
        return Collections.unmodifiableMap(this.procs);
    }

    private final Map<String,Procedure> procs = new TreeMap<String,Procedure>();

    @Override
    public boolean setFixed() {
        assert this.main != null;
        boolean result = super.setFixed();
        if (result) {
            this.main.setFixed();
            for (Procedure proc : this.procs.values()) {
                proc.setFixed();
            }
            checkCalls();
        }
        return result;
    }

    /** Checks that all calls in the main template and procedures are resolved within the program. */
    private void checkCalls() {
        checkCalls(this.main);
        for (Procedure proc : this.procs.values()) {
            checkCalls(proc.getTemplate());
        }
    }

    /** Checks that all calls in the a given template are resolved within the program. */
    private void checkCalls(Template template) {
        for (Switch edge : template.edgeSet()) {
            if (edge.isChoice()) {
                continue;
            }
            Callable unit = edge.getUnit();
            if (unit.getKind() == Kind.RULE) {
                continue;
            }
            if (!this.procs.containsKey(unit.getFullName())) {
                throw new IllegalStateException(String.format(
                    "'%s' called from '%s' is not defined in this program",
                    unit.getFullName(), template.getName()));
            }
        }
    }
}
