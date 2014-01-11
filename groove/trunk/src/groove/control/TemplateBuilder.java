/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import groove.control.symbolic.OutEdge;
import groove.control.symbolic.Term;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class for constructing control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TemplateBuilder {
    /** Private constructor for the singleton instance. */
    private TemplateBuilder() {
        // empty
    }

    /** Constructs a template from a symbolic location. */
    public Template build(String name, Term init) {
        // initialise the auxiliary data structures
        Template result = this.template = createTemplate(name);
        Map<Term,Location> locMap = this.locMap = new HashMap<Term,Location>();
        Set<Term> fresh = this.fresh = new HashSet<Term>();
        // set the initial location
        locMap.put(init, result.getStart());
        fresh.add(init);
        // do the following as long as there are fresh locations
        while (!fresh.isEmpty()) {
            Term next = fresh.iterator().next();
            Location source = locMap.get(next);
            fresh.remove(next);
            for (OutEdge edge : next.getOutEdges()) {
                Location target = addLocation(edge.getTarget());
                addEdge(new CtrlEdge(source, target, edge.getCall()));
            }
            Term succTerm = next.getSuccess();
            if (succTerm != null) {
                Location target = addLocation(succTerm);
                addEdge(new CtrlEdge(source, target, true));
            }
            Term failTerm = next.getSuccess();
            if (failTerm != null) {
                Location target = addLocation(failTerm);
                addEdge(new CtrlEdge(source, target, false));
            }
        }
        return result;
    }

    /**
     * Adds an edge to the template under construction.
     */
    private void addEdge(CtrlEdge edge) {
        this.template.addEdge(edge);
    }

    /** 
     * Adds a control location corresponding to a given symbolic state to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param symb the symbolic location to be added
     * @return the fresh or pre-existing control location
     */
    private Location addLocation(Term symb) {
        Location result = this.locMap.get(symb);
        if (result == null) {
            this.fresh.add(symb);
            result = this.template.addLocation(symb.getTransitDepth());
            this.locMap.put(symb, result);
            if (symb.isFinal()) {
                result.setFinal();
            }
        }
        return result;
    }

    /** Template under construction. */
    private Template template;
    /** Mapping from symbolic locations to locations. */
    private Map<Term,Location> locMap;
    /** Unexplored set of symbolic locations. */
    private Set<Term> fresh;

    /** Callback factory method for a template. */
    private Template createTemplate(String name) {
        return new Template(name);
    }

    /** Returns the singleton instance of this class. */
    public static TemplateBuilder instance() {
        return INSTANCE;
    }

    private static final TemplateBuilder INSTANCE = new TemplateBuilder();
}
