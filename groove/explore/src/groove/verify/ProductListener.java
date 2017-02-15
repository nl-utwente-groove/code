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
 * $Id$
 */
package groove.verify;

/**
 * A listener to the exploration of product systems.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface ProductListener {
    /**
     * Signals that a product state has been added to a product system.
     * @param gts the product system that has been updated
     * @param state the state that has been added
     */
    default void addUpdate(ProductStateSet gts, ProductState state) {
        // default empty implementation
    }

    /**
     * Update method called when a state of a product system is set to closed,
     * in the course of exploration.
     */
    default public void closeUpdate(ProductStateSet gts, ProductState state) {
        // default empty implementation
    }
}
