/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package groove.gui.chscenar;

import java.awt.Component;

import groove.trans.Rule;
import groove.trans.GraphGrammar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/** A cell renderer for rules.
 *
 * @author Iovka Boneva
 */
class RuleCellRenderer extends BasicComboBoxRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		RuleCellRenderer result = 
			(RuleCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		result.setText(((Rule) value).getName().toString());
		return result;
	}
}
