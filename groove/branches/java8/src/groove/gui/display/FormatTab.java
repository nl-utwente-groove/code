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
package groove.gui.display;

import groove.grammar.model.GrammarModel;
import groove.grammar.model.Resource;

import java.util.Observer;

import javax.swing.JComponent;

/**
 * @author rensink
 * @version $Revision $
 */
public class FormatTab extends ResourceTab {

    /**
     * @param display
     */
    public FormatTab(ResourceDisplay display) {
        super(display);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected JComponent getEditArea() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Observer createErrorListener() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected JComponent getUpperInfoPanel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected JComponent getLowerInfoPanel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateGrammar(GrammarModel grammar) {
        // TODO Auto-generated method stub

    }

    @Override
    public Resource getResource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setResource(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeResource(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDirtMinor() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setClean() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveResource() {
        // TODO Auto-generated method stub

    }

}
