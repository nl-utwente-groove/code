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
package groove.io.external;

import groove.graph.Graph;
import groove.gui.Simulator;
import groove.io.external.Exporter.Exportable;

import java.awt.Frame;
import java.io.File;

/** Abstract superclass for {@link FormatExporter}s, containing a few helper methods. */
public abstract class AbstractFormatExporter implements FormatExporter {
    @Override
    public void doExport(File file, Format format, Graph graph)
        throws PortException {
        doExport(file, format, new Exportable(graph));
    }

    /** Returns the parent component for a dialog. */
    protected Frame getParent() {
        return this.simulator == null ? null : this.simulator.getFrame();
    }

    /** Returns the simulator on which this exporter works. */
    protected Simulator getSimulator() {
        return this.simulator;
    }

    @Override
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    private Simulator simulator;
}
