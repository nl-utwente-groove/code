/*
 * ExploreConditionPanel.java
 *
 * Created on June 6, 2008, 12:12 PM
 */

package groove.gui.chscenar;

import groove.explore.result.ExploreCondition;
import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import java.awt.CardLayout;
import java.awt.Component;

/** A panel that offers the possibility to configure
 * an explore condition.
 *
 * @author  Iovka Boneva
 */
public class ExploreConditionSelectionPanel extends javax.swing.JPanel {

    /** Creates new form ExploreConditionPanel */
    public ExploreConditionSelectionPanel(GraphGrammar grammar) {
        setGrammar(grammar);
        initComponents();
        initOptionsPanels();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exploreConditionChoicePanel = new javax.swing.JPanel();
        javax.swing.JLabel l1 = new javax.swing.JLabel();
        exploreConditionComboBox = new javax.swing.JComboBox();
        optionsPanel = new javax.swing.JPanel();
        negatedPanel = new javax.swing.JPanel();
        negatedCheckBox = new javax.swing.JCheckBox();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        exploreConditionChoicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Explore condition type"));
        exploreConditionChoicePanel.setMaximumSize(null);
        exploreConditionChoicePanel.setMinimumSize(null);
        exploreConditionChoicePanel.setPreferredSize(new java.awt.Dimension(400, 70));

        l1.setText("Choose");

        exploreConditionComboBox.setModel(new ChoiceComboBoxModel(ExploreConditionChoice.class));
        exploreConditionComboBox.setRenderer(new ChoiceComboBoxCellRenderer());
        exploreConditionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploreConditionComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout exploreConditionChoicePanelLayout = new javax.swing.GroupLayout(exploreConditionChoicePanel);
        exploreConditionChoicePanel.setLayout(exploreConditionChoicePanelLayout);
        exploreConditionChoicePanelLayout.setHorizontalGroup(
            exploreConditionChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exploreConditionChoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(l1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exploreConditionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(150, Short.MAX_VALUE))
        );
        exploreConditionChoicePanelLayout.setVerticalGroup(
            exploreConditionChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exploreConditionChoicePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(exploreConditionChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exploreConditionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(l1))
                .addContainerGap())
        );

        add(exploreConditionChoicePanel);

        optionsPanel.setMaximumSize(null);
        optionsPanel.setMinimumSize(null);
        optionsPanel.setPreferredSize(new java.awt.Dimension(400, 100));
        optionsPanel.setLayout(new java.awt.CardLayout());
        add(optionsPanel);

        negatedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Negation"));
        negatedPanel.setMaximumSize(null);
        negatedPanel.setMinimumSize(null);
        negatedPanel.setPreferredSize(new java.awt.Dimension(400, 70));

        negatedCheckBox.setText("Negate condition");

        javax.swing.GroupLayout negatedPanelLayout = new javax.swing.GroupLayout(negatedPanel);
        negatedPanel.setLayout(negatedPanelLayout);
        negatedPanelLayout.setHorizontalGroup(
            negatedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(negatedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(negatedCheckBox)
                .addContainerGap(259, Short.MAX_VALUE))
        );
        negatedPanelLayout.setVerticalGroup(
            negatedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(negatedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(negatedCheckBox)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        add(negatedPanel);
    }// </editor-fold>//GEN-END:initComponents

private void exploreConditionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploreConditionComboBoxActionPerformed
    updateExploreConditionChoice();
}//GEN-LAST:event_exploreConditionComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel exploreConditionChoicePanel;
    private javax.swing.JComboBox exploreConditionComboBox;
    private javax.swing.JCheckBox negatedCheckBox;
    private javax.swing.JPanel negatedPanel;
    private javax.swing.JPanel optionsPanel;
    // End of variables declaration//GEN-END:variables

    
    // ----------------------------------------------------------------------
    // PUBLIC METHODS
    // ----------------------------------------------------------------------
    private GraphGrammar grammar;
    
    private void setGrammar (GraphGrammar grammar) {
        this.grammar = grammar;
    }
    private GraphGrammar getGrammar() {
        return grammar;
    }
    
    /** Returns the explore condition options currently
     * constructed using the panel.
     * @return The explore condition options currently constructed, or null
     * if no valid options have been given.
     */
    public ExploreCondition getExploreCondition () {
        ExploreConditionChoice c = getSelectedExploreCondition();
        ExploreConditionOptionsProvider optPanel =
            (ExploreConditionOptionsProvider) getOptionsPanel(c);
        Object options = optPanel.getOptions();
        if (options == null) {
            return null;
        }
        return c.getInstance(new ExploreConditionOptions(getNegated(), options));
    }
     
    
    // ----------------------------------------------------------------------
    // HELPER METHODS
    // ----------------------------------------------------------------------

    private ExploreConditionChoice getSelectedExploreCondition() {
        return (ExploreConditionChoice) exploreConditionComboBox.getSelectedItem();
    }
    private boolean getNegated () {
        return negatedCheckBox.isSelected();
    }
    
    private Component edgeBoundOptionsPanel;
    private Component nodeBoundOptionsPanel;
    private Component ruleApplOptionsPanel;
    
    private Component getEdgeBoundOptionsPanel() {
        if (edgeBoundOptionsPanel == null) {
            Label[] labels = new Label[DefaultLabel.getLabelCount()];
            for (char i = 0; i < labels.length; i++) {
                labels[i] = DefaultLabel.getLabel(i);
            }
            edgeBoundOptionsPanel = new EdgeBoundOptionsPanel(labels);
        }
        return edgeBoundOptionsPanel;
    }
    
    private Component getNodeBoundOptionsPanel () {
        if (nodeBoundOptionsPanel == null) {
            nodeBoundOptionsPanel = new NodeBoundOptionsPanel();
        }
        return nodeBoundOptionsPanel;
    }
    
    private Component getRuleApplOptionsPanel () {
        if (ruleApplOptionsPanel == null) {
            Rule[] rules = (Rule[]) getGrammar().getRules().toArray();
            ruleApplOptionsPanel = new RuleApplOptionsPanel(rules);
        }
        return ruleApplOptionsPanel;
    }
    
    private Component getOptionsPanel (ExploreConditionChoice c) {
        switch (c) {
            case EDGE_BOUND : return getEdgeBoundOptionsPanel();
            case NODE_BOUND : return getNodeBoundOptionsPanel();
            case RULE_APPL : return getRuleApplOptionsPanel();
            default : throw new UnsupportedOperationException("Missing implementation for " + c);
        }
    }
    
    private void initOptionsPanels() {
        for (int i = 0; i < exploreConditionComboBox.getModel().getSize(); i++) {
            ExploreConditionChoice c = (ExploreConditionChoice) exploreConditionComboBox.getModel().getElementAt(i);
            optionsPanel.add(getOptionsPanel(c), c.name());
        }
        updateExploreConditionChoice();
    }
    
    /** Updates made when an explore condition has been chosen. */
    private void updateExploreConditionChoice () {
        ExploreConditionChoice c = getSelectedExploreCondition();
        CardLayout l = (CardLayout) optionsPanel.getLayout();
        l.show(optionsPanel, c.name());
    }
    
}