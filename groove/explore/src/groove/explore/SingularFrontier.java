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
package groove.explore;

/**
 * Frontier consisting of a single explore point only
 * @author Arend Rensink
 * @version $Revision $
 */
public class SingularFrontier extends ExploreFrontier {
    /** Constructs a singular frontier. */
    public SingularFrontier() {
        super(0);
    }

    private ExplorePoint point;

    @Override
    public ExplorePoint next() {
        ExplorePoint result = this.point;
        this.point = null;
        return result;
    }

    @Override
    public int size() {
        return this.point == null ? 0 : 1;
    }

    @Override
    protected void addToFrontier(ExplorePoint point) {
        this.point = point;
    }

    @Override
    public void removeLast() {
        this.point = null;
    }
}
