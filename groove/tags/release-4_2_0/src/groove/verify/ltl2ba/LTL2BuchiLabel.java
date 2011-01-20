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
package groove.verify.ltl2ba;

import groove.verify.BuchiLabel;

import java.util.Set;

import rwth.i2.ltl2ba4j.model.IGraphProposition;

/**
 * BuchiLabel implementation specific for {@link LTL2BuchiGraph}s.
 */
public class LTL2BuchiLabel implements BuchiLabel
{
    private Set<IGraphProposition> labels;

    /**
     * Creates a new BuchiLabel of type LTL2BuchiLabel.
     * @param labels the set of {@link IGraphProposition}s to use as label
     */
    public LTL2BuchiLabel(Set<IGraphProposition> labels)
    {
        this.labels = labels;
    }

    /**
     * Returns the set of {@link IGraphProposition}s on this label.
     * @return the set of GraphPrositions on this label
     */
    public Set<IGraphProposition> getLabels()
    {
        return this.labels;
    }

    @Override
    public String toString()
    {
        return getLabels().toString();
    }
}
