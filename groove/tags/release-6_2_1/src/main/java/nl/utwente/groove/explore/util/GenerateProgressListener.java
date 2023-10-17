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
package nl.utwente.groove.explore.util;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExplorationListener;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;

/**
 * Class that implements a visualisation of the progress of a GTS generation
 * process. The monitor should be added as a {@link GTSListener}
 * to the GTS in question.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GenerateProgressListener extends GenerateProgressMonitor
    implements ExplorationListener, GTSListener {
    @Override
    public void start(Exploration exploration, GTS gts) {
        restart();
        gts.addLTSListener(this);
    }

    @Override
    public void stop(GTS gts) {
        gts.removeLTSListener(this);
    }

    @Override
    public void abort(GTS gts) {
        gts.removeLTSListener(this);
    }

    @Override
    public void addUpdate(GTS gts, GraphState state) {
        addState(gts.nodeCount(), gts.edgeCount(), gts.getOpenStateCount());
    }

    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        addTransition(gts.nodeCount(), gts.edgeCount(), gts.getOpenStateCount());
    }
}