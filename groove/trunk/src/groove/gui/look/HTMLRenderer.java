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
package groove.gui.look;

import groove.gui.look.Line.Style;
import groove.io.HTMLConverter;

import java.awt.Color;

/**
 * HTML renderer for lines.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HTMLRenderer extends LineRenderer {
    @Override
    protected String getLineBreak() {
        return HTMLConverter.HTML_LINEBREAK;
    }

    @Override
    public StringBuilder applyColored(Color color, StringBuilder subline) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StringBuilder applyStyled(Style style, StringBuilder subline) {
        HTMLConverter.HTMLTag tag;
        switch (style) {
        case BOLD:
            tag = HTMLConverter.STRONG_TAG;
            break;
        case ITALIC:
            tag = HTMLConverter.ITALIC_TAG;
            break;
        case STRIKE:
            tag = HTMLConverter.STRIKETHROUGH_TAG;
            break;
        default:
            assert false;
            tag = null;
        }
        return tag.on(subline);
    }

    @Override
    public StringBuilder applyAtomic(String text) {
        return HTMLConverter.toHtml(new StringBuilder(text));
    }
}
