/*
 * EdgeBoundOptionsPanel.java
 *
 * Created on June 6, 2008, 11:22 AM
 */

package groove.gui.chscenar;

import groove.graph.Label;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Iovka Boneva
 */
@Deprecated
@SuppressWarnings("all")
public class EdgeBoundOptionsPanel extends javax.swing.JPanel
                implements ExploreConditionOptionsProvider<Map<Label,Integer>> {

    /** Creates new form EdgeBoundOptionsPanel */
    public EdgeBoundOptionsPanel(Label[] labels) {
        setLabels(labels);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        edgeBoundConstraintField = new javax.swing.JTextField();
        labelComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel l2 = new javax.swing.JLabel();
        l3 = new javax.swing.JLabel();
        boundField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        editConstraintCheckBox = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        setPreferredSize(new java.awt.Dimension(400, 100));

        edgeBoundConstraintField.setEditable(false);
        edgeBoundConstraintField.setToolTipText("Must be a list of the form label=number{,label=number}");

        labelComboBox.setModel(new DefaultComboBoxModel(getLabels()));

        l2.setText("Label");

        l3.setLabelFor(boundField);
        l3.setText("Bound");

        boundField.setColumns(4);

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editConstraintCheckBox.setText("Edit constraint");
        editConstraintCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editConstraintCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(l2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(l3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(boundField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addComponent(addButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(edgeBoundConstraintField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addComponent(editConstraintCheckBox)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(l2)
                    .addComponent(addButton)
                    .addComponent(labelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(l3)
                    .addComponent(boundField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edgeBoundConstraintField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editConstraintCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
    addConstraint();
}//GEN-LAST:event_addButtonActionPerformed

private void editConstraintCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editConstraintCheckBoxActionPerformed
    updateEditableConstraint();
}//GEN-LAST:event_editConstraintCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField boundField;
    private javax.swing.JTextField edgeBoundConstraintField;
    private javax.swing.JCheckBox editConstraintCheckBox;
    private javax.swing.JLabel l3;
    private javax.swing.JComboBox labelComboBox;
    // End of variables declaration//GEN-END:variables

    
    // ----------------------------------------------------------------------
    // PUBLIC METHODS
    // ----------------------------------------------------------------------
    private Label[] labels;
    
    private void setLabels (Label[] labels) {
        this.labels = labels;
    }
    private Label[] getLabels () {
        return labels;
    }
    
    private Map<Label,Integer> getConstructedBound () {
    	
    	throw new UnsupportedOperationException();
    }

    public Map<Label, Integer> getOptions() {
        try {
            return new EdgeBoundConstraintParser().parse(edgeBoundConstraintField.getText());
        } catch (IllegalArgumentException e) {
            // The provided string is not syntactically correct
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // ----------------------------------------------------------------------
    // HELPER METHODS
    // ----------------------------------------------------------------------
    
    /** Adds a new label=bound constraint. */
    private void addConstraint () {
        Label label = (Label) labelComboBox.getSelectedItem();
        String old = edgeBoundConstraintField.getText();
        Integer bound = null;
        try {
            bound = Integer.parseInt(boundField.getText());
            if (bound < 0) {
                bound = null;
                JOptionPane.showMessageDialog(this,
                    "The bound should be a positive value",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "The bound should be a positive value",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (bound != null) {
            String newCouple = label.text() + "=" + bound.toString();
            if (old == null || old.equals("")) {
                edgeBoundConstraintField.setText(newCouple);
            } else {
                edgeBoundConstraintField.setText(old + "," + newCouple);
            }
        }
    }
    
    private void updateEditableConstraint () {
        edgeBoundConstraintField.setEditable(editConstraintCheckBox.isSelected());
    }

}
