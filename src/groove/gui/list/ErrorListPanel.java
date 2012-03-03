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
package groove.gui.list;

import groove.gui.jgraph.JAttr;

import java.awt.Color;

/** List panel for showing errors. */
public final class ErrorListPanel extends ListPanel {

    /** Default constructor, delegates to super class. */
    public ErrorListPanel(String title) {
        super(title);
    }

    @Override
    protected Color getNormalBackground() {
        return JAttr.ERROR_NORMAL_BACKGROUND;
    }

    @Override
    protected Color getNormalForeground() {
        return JAttr.ERROR_NORMAL_FOREGROUND;
    }

    @Override
    protected Color getFocusBackground() {
        return JAttr.ERROR_FOCUS_BACKGROUND;
    }

    @Override
    protected Color getFocusForeground() {
        return JAttr.ERROR_FOCUS_FOREGROUND;
    }

    @Override
    protected Color getSelectBackground() {
        return JAttr.ERROR_SELECT_BACKGROUND;
    }

    @Override
    protected Color getSelectForeground() {
        return JAttr.ERROR_SELECT_FOREGROUND;
    }

}