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
 * $Id: AbstractTestOutcome.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.trans;

import groove.graph.Morphism;
import groove.rel.VarMorphism;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract implementation of a {@link GraphTestOutcome}.
 * This only aspect missing (which has to be included in the constructor
 * of the implementing classes) is which of the keys of the map are considered
 * successful and should therefore be included in the {@link #getSuccessKeys()}.
 * A hook is provided in the form of the callback method {@link #isSuccessKey(GraphTestOutcome)}. 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
abstract public class AbstractTestOutcome<Mine,Nested> extends HashMap<Mine,GraphTestOutcome<Nested,Mine>> implements GraphTestOutcome<Mine,Nested> {
    /**
     * Constructs a test outcome from a given test, subject and mappping to be
     * copied to the underlying map.
     * The success keys are computed by iterating over the map and adding the 
     * key to the set of success keys if {@link #isSuccessKey(GraphTestOutcome)}
     * reports success.
     */
    public AbstractTestOutcome(GraphTest test, VarMorphism subject, Map<Mine,? extends GraphTestOutcome<Nested,Mine>> outcome) {
        this.test =test;
        this.subject = subject;
        putAll(outcome);
        for (Map.Entry<Mine,GraphTestOutcome<Nested,Mine>> entry: entrySet()) {
            if (isSuccessKey(entry.getValue())) {
                successKeys.add(entry.getKey());
            }
        }
    }

    public Morphism getSubject() {
        return subject;
    }

    public boolean isSuccess() {
        return !successKeys.isEmpty();
    }

    public Set<Mine> getSuccessKeys() {
        return Collections.unmodifiableSet(successKeys);
    }

    public GraphTest getTest() {
        return test;
    }
    
    public boolean isCondition() {
        return test instanceof GraphCondition;
    }
    
    /**
     * Callback method from the constructor to determine if a given key
     * of this map is successful, given the corresponding image. 
     * @param image the image of the key to be tested
     */
    abstract protected boolean isSuccessKey(GraphTestOutcome<Nested,Mine> image);
    
    /** The test of which this is an outcome. */
    private final GraphTest test;
    /** The subject of the test. */
    private final VarMorphism subject;
    /** The evidence for a successful outcome. */
    private final Set<Mine> successKeys = new HashSet<Mine>();
}