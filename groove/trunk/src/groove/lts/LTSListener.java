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
 * $Id: LTSListener.java,v 1.2 2008-01-30 09:32:18 iovka Exp $
 */
package groove.lts;

import groove.graph.GraphShapeListener;

/**
 * An extended graph listener, which is also notified of explore actions on an
 * LTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface LTSListener extends GraphShapeListener {
    /**
     * Update method called when a state of the LTS is set to closed, in the
     * course of LTS exploration.
     * @see LTS#isOpen(State)
     */
    public void closeUpdate(LTS graph, State explored);
}
