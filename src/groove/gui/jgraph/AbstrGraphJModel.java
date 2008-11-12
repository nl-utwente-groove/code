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
 * $Id: AbstrGraphJModel.java,v 1.2 2008-02-05 13:28:04 rensink Exp $
 */
package groove.gui.jgraph;

import groove.abs.AbstrGraph;
import groove.gui.Options;

/** The abstract graph version of a GraphJModel */
public class AbstrGraphJModel extends GraphJModel {

    /**
     * Constructs a new JModel with default edge attributes, and with node
     * attributes specifying that self-edges should be displayed as edges.
     * @param graph The underlying graph.
     * @param options
     */
    public AbstrGraphJModel(AbstrGraph graph, Options options) {
        super(graph, options);
        // options.setSelected(Options.VERTEX_LABEL_OPTION, true);
    }

    /** Creates a new instance of this class */
    public static AbstrGraphJModel newInstance(AbstrGraph graph, Options options) {
        AbstrGraphJModel result = new AbstrGraphJModel(graph, options);
        result.reload();
        return result;
    }

}
