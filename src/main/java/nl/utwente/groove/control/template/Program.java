/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.control.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Compiled control program, consisting of a main template
 * and a map from procedure names to procedures, each of which has its own template.
 * Programs are built from control fragments by the control compiler.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Program {
    /**
     * Constructs a program from its compiled parts.
     * @param mainName the name of the fragment containing the main body
     * @param template the main template
     * @param procs the procedures of the program, whose templates have been set
     * @param properties the property actions to be checked at each steady state
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public Program(QualName mainName, Template template, Map<QualName,Procedure> procs,
                   List<Action> properties) {
        this.mainName = mainName;
        this.template = template;
        this.procs = new LinkedHashMap<>(procs);
        this.properties = new ArrayList<>(properties);
    }

    /**
     * Returns the name of the fragment containing the main body.
     */
    public QualName getMainName() {
        return this.mainName;
    }

    /** The name of the sub-program containing the main body. */
    private final QualName mainName;

    /** Returns an unmodifiable view on the map from names to procedures
     * defined in this program.
     */
    public Map<QualName,Procedure> getProcs() {
        return Collections.unmodifiableMap(this.procs);
    }

    /** Returns the procedure for a given name, if any. */
    public @Nullable Procedure getProc(QualName name) {
        return this.procs.get(name);
    }

    private final Map<QualName,Procedure> procs;

    /** Indicates if there are properties to be checked at each steady state. */
    public boolean hasProperties() {
        return !getProperties().isEmpty();
    }

    /** Returns the list of property actions to be checked at each steady state. */
    public List<Action> getProperties() {
        return this.properties;
    }

    /** The property actions to be checked at each steady state. */
    private final List<Action> properties;

    /** Returns the main template of this program. */
    public Template getTemplate() {
        return this.template;
    }

    private final Template template;
}
