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

import javax.swing.DefaultComboBoxModel;

/** A list model used by the the combo box proposing a list of choices.
 * The possible choices are exactly the elements of the Choice object
 * given as parameter.
 * @author Iovka Boneva
 */
@Deprecated
@SuppressWarnings("all")
class ChoiceComboBoxModel extends DefaultComboBoxModel {
	
	ChoiceComboBoxModel(Class<? extends Choice> choiceType) {
		for (Choice c : choiceType.getEnumConstants()) {
			addElement(c);
		}
	}
}
