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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Control program, consisting of a main template
 * and templates for all procedures.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Program extends DefaultFixable {
    /** Constructs a named, initially empty program. */
    public Program(String name) {
        this.names = new TreeSet<String>();
        this.names.add(name);
    }

    /**
     * Returns the name of this program, which is the concatenation
     * of the names of the constituent fragments, alphabetically ordered.
     */
    public String getName() {
        StringBuilder result = new StringBuilder();
        for (String name : this.names) {
            if (result.length() > 0) {
                result.append('+');
            }
            result.append(name);
        }
        return result.toString();
    }

    private final Set<String> names;

    /** Sets the main template in this program. */
    public void setMain(Template main) {
        assert main != null && this.main == null;
        assert !isFixed();
        this.main = main;
    }

    /**
     * Indicates if the program has a main block.
     * Should only be invoked after the template is fixed.
     */
    public boolean hasMain() {
        return getMain() != null;
    }

    /** Returns the main block of this program, if any.
     * Should only be invoked after the program is fixed.
     * May be {@code null} if this program has no main body.
     */
    public Template getMain() {
        assert isFixed();
        return this.main;
    }

    private Template main;

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
            if (this.main != null) {
                this.main.setFixed();
            }
            for (Procedure proc : this.procs.values()) {
                proc.setFixed();
            }
        }
        return result;
    }

    /** Checks that all calls in the program are resolved. */
    public void checkCalls() {
        if (hasMain()) {
            checkCalls(getMain());
        }
        for (Procedure proc : getProcs().values()) {
            checkCalls(proc.getTemplate());
        }
    }

    /** 
     * Adds all procedures of another program to this one.
     * At most one of the programs may have a main template,
     * and the procedure names may not overlap.
     * This program may not be fixed.
     */
    public void add(Program other) {
        assert !isFixed();
        this.names.addAll(other.names);
        if (hasMain() && other.hasMain()) {
            throw new IllegalArgumentException(
                "Both programs have a main template");
        }
        if (!hasMain()) {
            this.main = other.main;
        }
        for (Procedure proc : other.procs.values()) {
            addProc(proc);
        }
    }

    /** Checks that all calls in a given template are resolved within the program. */
    public void checkCalls(Template template) {
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
