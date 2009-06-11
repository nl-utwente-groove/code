/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AbstrLTSJModel.java,v 1.1 2007-11-28 16:08:18 iovka Exp $
 */
package groove.gui.jgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.AttributeMap;

import groove.abs.lts.AGTS;
import groove.abs.lts.AbstrGraphTransition;
import groove.graph.Edge;
import groove.gui.Options;
import groove.lts.LTS;
import groove.lts.State;
import groove.lts.Transition;

/** Extends an {@link LTSJModel} with a set of active transitions */
public class AbstrLTSJModel extends LTSJModel {

    /** @require lts should be of type AGTS */
    AbstrLTSJModel(LTS lts, Options options) {
        super(lts, options);
        assert lts instanceof AGTS : "Wrong type : " + lts
            + " should be of type AGTS.";
    }

    private AbstrLTSJModel() {
        super();
        // empty
    }

    @Override
    protected AttributeMap createJEdgeAttr(Set<? extends Edge> edgeSet) {
        AttributeMap result = groove.gui.jgraph.JAttr.LTS_EDGE_ATTR.clone();
        if (this.activeTransitionsSet != null
            && this.activeTransitionsSet.containsAll(edgeSet)) {
            result.applyMap(groove.gui.jgraph.JAttr.LTS_EDGE_ACTIVE_CHANGE);
        }
        // if (activeTransition != null && edgeSet.contains(activeTransition)) {
        // result.applyMap(groove.gui.jgraph.JAttrLTS_EDGE_ACTIVE_CHANGE);
        // }
        return result;
    }

    @Override
    /** @require trans should be of type AbstrGraphTransition */
    public void setActive(State state, Transition trans) {
        assert trans == null || trans instanceof AbstrGraphTransition : "The transition should be of type AbstrGraphTransition";
        Set<JCell> changedCells = new HashSet<JCell>();
        Collection<Transition> previousActiveTransitions =
            this.activeTransitionsSet;

        if (previousActiveTransitions == null
            || !previousActiveTransitions.contains(trans)) {
            if (trans == null) {
                this.activeTransitionsSet = null;
            } else {
                this.activeTransitionsSet =
                    ((AGTS) getGraph()).getEquivalentTransitions((AbstrGraphTransition) trans);
                for (Transition t : this.activeTransitionsSet) {
                    JCell jCell = getJCell(t);
                    assert jCell != null : String.format(
                        "No image for %s in jModel", t);
                    changedCells.add(jCell);
                }
            }

            if (previousActiveTransitions != null) {
                for (Transition t : previousActiveTransitions) {
                    JCell jCell = getJCell(t);
                    assert jCell != null : String.format(
                        "No image for %s in jModel", t);
                    changedCells.add(jCell);
                }
            }
        }

        State previousState = getActiveState();
        if (state != previousState) {
            setterActiveState(state);
            if (state != null) {
                changedCells.add(getJCell(state));
            }
            if (previousState != null) {
                changedCells.add(getJCell(previousState));
            }
        }
        if (!changedCells.isEmpty()) {
            refresh(changedCells);
        }
    }

    static public AbstrLTSJModel newInstance(LTS lts, Options options) {
        if (lts == null) {
            return AbstrLTSJModel.EMPTY_ABSTR_LTS_JMODEL;
        }
        AbstrLTSJModel result = new AbstrLTSJModel(lts, options);
        result.reload();
        return result;
    }

    /**
     * Replaces the activeTransition field, as a set of transitions may be
     * active All active transitions have the same source and the same rule
     * event component
     */
    Collection<Transition> activeTransitionsSet;

    @Override
    /**
     * This implementation returns a random transition between the active
     * transitions, if any.
     */
    public Transition getActiveTransition() {
        if (this.activeTransitionsSet != null
            && !this.activeTransitionsSet.isEmpty()) {
            return this.activeTransitionsSet.iterator().next();
        }
        return null;
    }

    /** Dummy abstract LTS JModel */
    static public final AbstrLTSJModel EMPTY_ABSTR_LTS_JMODEL =
        new AbstrLTSJModel();
}
