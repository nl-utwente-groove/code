/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id: ExploreResult.java 6072 2021-07-14 18:23:50Z rensink $
 */
package nl.utwente.groove.lts;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.AOverlay;

/**
 * A subset of the nodes and edges in a given graph.
 */
@NonNullByDefault
public class GTSOverlay extends AOverlay<GTS,GraphState,GraphTransition> {
    /**
     * Creates a fresh, empty result for a given graph.
     */
    public GTSOverlay(GTS gts) {
        super(gts);
    }
}
