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
package groove.explore.encode;

import groove.gui.dialog.ExplorationDialog;
import groove.gui.layout.SpringUtilities;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * A <code>StringEditor</code> is an editor for a String value which implements
 * the signature that is described in <code>EncodedTypeEditor</code>.
 * <p>
 * @see EncodedTypeEditor
 * @author Maarten de Mol
 */
public class StringEditor<A> extends EncodedTypeEditor<A,String> {

    private final JTextField editor;
    private final int nr_components;

    /**
     * Build the editor using a comment prefix, an initial value and a display
     * size (number of columns).
     */
    public StringEditor(String syntax, String initValue, int nrColumns) {
        super(new SpringLayout());
        setBackground(ExplorationDialog.INFO_BG_COLOR);
        if (!syntax.equals("")) {
            add(new JLabel("<HTML><FONT color=" + ExplorationDialog.INFO_COLOR
                + "><B>Syntax:</B> " + syntax + "</FONT></HTML>"));
            this.nr_components = 2;
        } else {
            this.nr_components = 1;
        }
        this.editor = new JTextField(initValue, nrColumns);
        this.editor.setBackground(ExplorationDialog.INFO_BOX_BG_COLOR);
        add(this.editor);
        SpringUtilities.makeCompactGrid(this, this.nr_components, 1, 0, 0, 0, 0);
    }

    /**
     * Build the editor without a comment prefix, but with an initial value and
     * a display size (number of columns).
     */
    public StringEditor(String initialValue, int nrColumns) {
        this("", initialValue, nrColumns);
    }

    @Override
    public String getCurrentValue() {
        return this.editor.getText();
    }

    @Override
    public void setCurrentValue(String value) {
        this.editor.setText(value);
    }
}
