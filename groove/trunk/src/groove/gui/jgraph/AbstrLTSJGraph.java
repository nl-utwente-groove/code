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
 * $Id: AbstrLTSJGraph.java,v 1.1 2007-11-28 16:08:18 iovka Exp $
 */
package groove.gui.jgraph;

import groove.gui.Simulator;

public class AbstrLTSJGraph extends LTSJGraph {

    public AbstrLTSJGraph(Simulator simulator) {
        super(simulator, AbstrLTSJModel.EMPTY_ABSTR_LTS_JMODEL);
    }

    /** Specialises the return type to a {@link AbstrLTSJModel}. */
    @Override
    public AbstrLTSJModel getModel() {
        return (AbstrLTSJModel) this.graphModel;
    }

}
