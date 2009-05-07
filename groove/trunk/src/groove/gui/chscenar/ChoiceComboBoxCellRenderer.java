/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.gui.chscenar;

import groove.gui.chscenar.Choice;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/** A cell renderer used by the JList. 
 * @author Iovka Boneva
 * */
class ChoiceComboBoxCellRenderer extends BasicComboBoxRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		ChoiceComboBoxCellRenderer comp = (ChoiceComboBoxCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		Choice c = (Choice) value;
		comp.setText(c.shortName());
		comp.setToolTipText(c.description());
		return comp;
	}

}

