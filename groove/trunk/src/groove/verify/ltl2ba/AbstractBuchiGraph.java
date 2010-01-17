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

import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that provides default implementations for common functionality.
 * 
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public abstract class AbstractBuchiGraph implements BuchiGraph
{
    private Set<BuchiLocation> initialLocations;

    private Set<BuchiLocation> acceptingLocations;

    @Override
    public Set<BuchiLocation> initialLocations()
    {
        return this.initialLocations;
    }

    @Override
    public Set<BuchiLocation> acceptingLocations()
    {
        return this.acceptingLocations;
    }

    @Override
    public boolean addInitialLocation(BuchiLocation location)
    {
        if (null == this.initialLocations)
        {
            this.initialLocations = new HashSet<BuchiLocation>();
        }
        return this.initialLocations.add(location);
    }

    @Override
    public boolean addAcceptingLocation(BuchiLocation location)
    {
        if (null == this.acceptingLocations)
        {
            this.acceptingLocations = new HashSet<BuchiLocation>();
        }
        return this.acceptingLocations.add(location);
    }

    @Override
    public boolean addTransition(BuchiTransition transition)
    {
        return transition.source().addTransition(transition);
    }
}
