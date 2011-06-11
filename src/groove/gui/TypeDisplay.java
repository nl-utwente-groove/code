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
package groove.gui;

import groove.gui.SimulatorModel.Change;
import groove.io.HTMLConverter;
import groove.trans.ResourceKind;

/**
 * Panel that holds the type display and type graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
final public class TypeDisplay extends ResourceDisplay {
    /**
     * Constructs a panel for a given simulator.
     */
    public TypeDisplay(Simulator simulator) {
        super(simulator, ResourceKind.TYPE);
        installListeners();
    }

    @Override
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.TYPE);
        super.installListeners();
    }

    //
    //    @Override
    //    public void update(SimulatorModel source, SimulatorModel oldModel,
    //            Set<Change> changes) {
    //        super.update(source, oldModel, changes);
    //        if (suspendListening()) {
    //            String type = source.getSelected(ResourceKind.TYPE);
    //            selectResource(type);
    //            activateListening();
    //        }
    //    }

    /** Returns the list of states and host graphs. */
    @Override
    public ResourceList getList() {
        if (this.typeJList == null) {
            this.typeJList = new ResourceList(this);
        }
        return this.typeJList;
    }

    @Override
    protected void decorateLabelText(String name, StringBuilder text) {
        super.decorateLabelText(name, text);
        if (getResource(name).isEnabled()) {
            HTMLConverter.STRONG_TAG.on(text);
            HTMLConverter.HTML_TAG.on(text);
        } else {
            text.insert(0, "(");
            text.append(")");
        }
    }

    /** Production system type list */
    private ResourceList typeJList;
}
