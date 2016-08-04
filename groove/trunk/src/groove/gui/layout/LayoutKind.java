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
package groove.gui.layout;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import com.jgraph.layout.organic.JGraphSelfOrganizingOrganicLayout;
import com.jgraph.layout.simple.SimpleGridLayout;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;
import com.jgraph.layout.tree.JGraphRadialTreeLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;

/** Enumeration of possible JGraph layout algorithms. */
public enum LayoutKind {

    /** Puts the nodes in a circle. */
    SIMPLE_CIRCLE("Simple Circle", new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE)),
    /** Tilts the nodes of the graph by a few points. */
    SIMPLE_TILT("Simple Tilt", new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_TILT)),
    /** Random placement of nodes.  */
    SIMPLE_RANDOM("Simple Randomized",
        new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM, 500, 500)),
    /** Grid alignment. */
    SIMPLE_GRID("Simple Grid", new SimpleGridLayout()),
    /** Compact tree representation. */
    COMPACT_TREE("Compact Tree", new JGraphCompactTreeLayout()),
    /** Radial tree layout. */
    RADIAL_TREE("Radial Tree", new JGraphRadialTreeLayout()),
    /** Simple tree layout. */
    BASIC_TREE("Basic Tree", new JGraphTreeLayout()),
    /** Organic Layout (Slow) */
    ORGANIC("Organic", new JGraphOrganicLayout()),
    /** Organic Layout (Fast) */
    FAST_ORGANIC("Fast Organic", new JGraphFastOrganicLayout()),
    /** Self-organizing map. */
    SELF_ORGANIZ("Self-Organizing", new JGraphSelfOrganizingOrganicLayout()),
    /** Hierarchical Layout. */
    HIERARCHICAL("Hierarchical", new JGraphHierarchicalLayout());

    private String displayString;
    private JGraphLayout layout;

    private LayoutKind(String displayString, JGraphLayout layout) {
        this.displayString = displayString;
        this.layout = layout;
    }

    /** Returns the string to be shown in the GUI. */
    public String getDisplayString() {
        return this.displayString + " Layout";
    }

    /** Returns the layout algorithm. */
    public JGraphLayout getLayout() {
        return this.layout;
    }

    /** Returns the prototype instance of the menu item. */
    public static LayouterItem getLayouterItemProto(LayoutKind kind) {
        LayouterItem result = map.get(kind);
        if (result == null) {
            result = new LayouterItem(kind);
            map.put(kind, result);
        }
        return result;
    }

    private static Map<LayoutKind,LayouterItem> map = new HashMap<>();

    /** Creates the panel with the options of the given layouter. */
    public static JPanel createLayoutPanel(LayouterItem item) {
        JGraphLayout layout = item.getLayout();
        if (layout instanceof JGraphSimpleLayout) {
            return createLayoutPanel(item, (JGraphSimpleLayout) layout);
        } else if (layout instanceof SimpleGridLayout) {
            return createLayoutPanel(item, (SimpleGridLayout) layout);
        } else if (layout instanceof JGraphCompactTreeLayout) {
            return createLayoutPanel(item, (JGraphCompactTreeLayout) layout);
        } else if (layout instanceof JGraphRadialTreeLayout) {
            return createLayoutPanel(item, (JGraphRadialTreeLayout) layout);
        } else if (layout instanceof JGraphTreeLayout) {
            return createLayoutPanel(item, (JGraphTreeLayout) layout);
        } else if (layout instanceof JGraphOrganicLayout) {
            return createLayoutPanel(item, (JGraphOrganicLayout) layout);
        } else if (layout instanceof JGraphFastOrganicLayout) {
            return createLayoutPanel(item, (JGraphFastOrganicLayout) layout);
        } else if (layout instanceof JGraphSelfOrganizingOrganicLayout) {
            return createLayoutPanel(item, (JGraphSelfOrganizingOrganicLayout) layout);
        } else if (layout instanceof JGraphHierarchicalLayout) {
            return createLayoutPanel(item, (JGraphHierarchicalLayout) layout);
        } else {
            return null;
        }

    }

    private static JPanel createLayoutPanel(LayouterItem item, JGraphSimpleLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        panel.createSlider(getMethod(layout, "setMaxx", Integer.TYPE), 0, 500, 20, "Maximum X");
        panel.createSlider(getMethod(layout, "setMaxy", Integer.TYPE), 0, 500, 20, "Maximum Y");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item, SimpleGridLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        panel.createSlider(getMethod(layout, "setNumCellsPerRow", Integer.TYPE),
            0,
            20,
            0,
            "Nodes per row");
        panel.createSlider(getMethod(layout, "setHeightSpacing", Integer.TYPE),
            0,
            100,
            20,
            "Height space between nodes");
        panel.createSlider(getMethod(layout, "setWidthSpacing", Integer.TYPE),
            0,
            100,
            20,
            "Width space between nodes");
        panel.createCheckBox(getMethod(layout, "setActOnUnconnectedVerticesOnly", Boolean.TYPE),
            "Act on unconnected vertices only",
            true);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item, JGraphCompactTreeLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        String labels[] = {"North", "West"};
        int values[] = {SwingConstants.NORTH, SwingConstants.WEST};
        panel.createRadioButtonGroup(getMethod(layout, "setOrientation", Integer.TYPE),
            labels,
            values,
            1,
            "Orientation");
        panel.createSpinner(getMethod(layout, "setLevelDistance", Double.TYPE),
            30.0,
            1.0,
            100.0,
            1.0,
            "Level distance");
        panel.createSpinner(getMethod(layout, "setNodeBorder", Double.TYPE),
            5.0,
            1.0,
            100.0,
            1.0,
            "Node distance");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item, JGraphRadialTreeLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        panel.createCheckBox(getMethod(layout, "setAutoRadius", Boolean.TYPE),
            "Auto radius",
            false);
        panel.createSpinner(getMethod(layout, "setMinradiusx", Double.TYPE),
            80.0,
            1.0,
            2000.0,
            1.0,
            "Minimum radius X");
        panel.createSpinner(getMethod(layout, "setMinradiusy", Double.TYPE),
            80.0,
            1.0,
            2000.0,
            1.0,
            "Minimum radius Y");
        panel.createSpinner(getMethod(layout, "setMaxradiusx", Double.TYPE),
            1000.0,
            1.0,
            2000.0,
            1.0,
            "Maximum radius X");
        panel.createSpinner(getMethod(layout, "setMaxradiusy", Double.TYPE),
            1000.0,
            1.0,
            2000.0,
            1.0,
            "Maximum radius Y");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item, JGraphTreeLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        String labels[] = {"Top", "Center", "Bottom"};
        int values[] = {SwingConstants.TOP, SwingConstants.CENTER, SwingConstants.BOTTOM};
        panel.createRadioButtonGroup(getMethod(layout, "setAlignment", Integer.TYPE),
            labels,
            values,
            0,
            "Alignment");
        panel.createCheckBox(getMethod(layout, "setCombineLevelNodes", Boolean.TYPE),
            "Combine level nodes",
            true);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item, JGraphOrganicLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        panel.createCheckBox(getMethod(layout, "setDeterministic", Boolean.TYPE),
            "Deterministic",
            true);
        panel.createSlider(getMethod(layout, "setMaxIterations", Integer.TYPE),
            1,
            1000,
            100,
            "Width space between nodes");
        panel.createCheckBox(getMethod(layout, "setOptimizeNodeDistribution", Boolean.TYPE),
            "Optimize node distribution",
            true);
        panel.createSpinner(getMethod(layout, "setNodeDistributionCostFactor", Double.TYPE),
            300000.0,
            1.0,
            10000000.0,
            10.0,
            "Node distribution cost factor");
        panel.createCheckBox(getMethod(layout, "setOptimizeEdgeCrossing", Boolean.TYPE),
            "Optimize edge crossing",
            true);
        panel.createSpinner(getMethod(layout, "setEdgeCrossingCostFactor", Double.TYPE),
            2000.0,
            1.0,
            20000.0,
            1.0,
            "Edge crossing cost factor");
        panel.createCheckBox(getMethod(layout, "setOptimizeEdgeDistance", Boolean.TYPE),
            "Optimize edge distance",
            true);
        panel.createSpinner(getMethod(layout, "setEdgeDistanceCostFactor", Double.TYPE),
            4000.0,
            1.0,
            20000.0,
            1.0,
            "Edge distance cost factor");
        panel.createCheckBox(getMethod(layout, "setOptimizeEdgeLength", Boolean.TYPE),
            "Optimize edge length",
            true);
        panel.createSpinner(getMethod(layout, "setEdgeLengthCostFactor", Double.TYPE),
            0.02,
            0.01,
            1.0,
            0.01,
            "Edge length cost factor");
        panel.createCheckBox(getMethod(layout, "setOptimizeBorderLine", Boolean.TYPE),
            "Optimize border line",
            true);
        panel.createSpinner(getMethod(layout, "setBorderLineCostFactor", Double.TYPE),
            5.0,
            0.1,
            100.0,
            1.0,
            "Border line cost factor");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item, JGraphFastOrganicLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        panel.createSpinner(getMethod(layout, "setForceConstant", Double.TYPE),
            50.0,
            0.001,
            500.0,
            1.0,
            "Force constant");
        panel.createSpinner(getMethod(layout, "setInitialTemp", Double.TYPE),
            200.0,
            1,
            1000.0,
            1.0,
            "Initial temperature");
        panel.createSlider(getMethod(layout, "setMaxIterations", Integer.TYPE),
            0,
            500,
            100,
            "Maximum number of interations");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item,
        JGraphSelfOrganizingOrganicLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        panel.createSpinner(getMethod(layout, "setCoolingFactor", Double.TYPE),
            1.0,
            0.01,
            10.0,
            0.01,
            "Cooling factor");
        panel.createSlider(getMethod(layout, "setStartRadius", Integer.TYPE),
            0,
            100,
            0,
            "Start radius");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static JPanel createLayoutPanel(LayouterItem item, JGraphHierarchicalLayout layout) {
        LayoutPanel panel = new LayoutPanel(layout, item);
        panel.createCheckBox(getMethod(layout, "setDeterministic", Boolean.TYPE),
            "Deterministic",
            false);
        panel.createCheckBox(getMethod(layout, "setLayoutFromSinks", Boolean.TYPE),
            "Layout from sinks",
            true);
        panel.createCheckBox(getMethod(layout, "setCompactLayout", Boolean.TYPE),
            "Compact layout",
            false);
        panel.createSpinner(getMethod(layout, "setInterHierarchySpacing", Double.TYPE),
            60.0,
            1.0,
            200.0,
            1.0,
            "Space between unconnected hierarchies");
        panel.createSpinner(getMethod(layout, "setInterRankCellSpacing", Double.TYPE),
            50.0,
            1.0,
            200.0,
            1.0,
            "Space between cell on adjacent layers");
        panel.createSpinner(getMethod(layout, "setIntraCellSpacing", Double.TYPE),
            30.0,
            1.0,
            200.0,
            1.0,
            "Space between cell on same layer");
        panel.createSpinner(getMethod(layout, "setParallelEdgeSpacing", Double.TYPE),
            10.0,
            1.0,
            200.0,
            1.0,
            "Parallel edge spacing");
        String labels[] = {"North", "South", "East", "West"};
        int values[] =
            {SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST};
        panel.createRadioButtonGroup(getMethod(layout, "setOrientation", Integer.TYPE),
            labels,
            values,
            0,
            "Orientation");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return panel;
    }

    private static Method getMethod(JGraphLayout layout, String name, Class<?>... parameterTypes) {
        Method method = null;
        try {
            method = layout.getClass()
                .getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        return method;
    }

    private static class LayoutPanel extends JPanel
        implements ChangeListener, ItemListener, ActionListener {

        final JGraphLayout layout;
        final LayouterItem item;

        LayoutPanel(JGraphLayout layout, LayouterItem item) {
            this.layout = layout;
            this.item = item;
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            ((ReflectiveComponent) e.getSource()).setLayoutParameter();
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            ((ReflectiveComponent) e.getSource()).setLayoutParameter();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((ReflectiveComponent) e.getSource()).setLayoutParameter();
        }

        void createSlider(Method methodToCall, int min, int max, int init, String label) {
            JSlider slider = new MyJSlider(methodToCall, min, max, init, this.layout, this.item);
            slider.addChangeListener(this);
            JLabel sliderLabel = new JLabel(label, SwingConstants.LEFT);
            this.add(sliderLabel);
            this.add(slider);
        }

        void createCheckBox(Method methodToCall, String label, boolean selected) {
            JCheckBox checkBox =
                new MyCheckBox(methodToCall, this.layout, this.item, label, selected);
            checkBox.addItemListener(this);
            this.add(checkBox);
        }

        void createRadioButtonGroup(Method methodToCall, String[] label, int[] value,
            int selectedValue, String title) {
            ButtonGroup group = new ButtonGroup();
            JPanel radioPanel = new JPanel(new GridLayout(0, 4));
            assert label.length == value.length;
            for (int i = 0; i < label.length; i++) {
                JRadioButton button =
                    new MyRadioButton(methodToCall, this.layout, this.item, label[i], value[i]);
                button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 4));
                button.addActionListener(this);
                group.add(button);
                radioPanel.add(button);
                if (i == selectedValue) {
                    group.setSelected(button.getModel(), true);
                }
            }
            radioPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
            add(radioPanel, BorderLayout.LINE_START);
        }

        void createSpinner(Method methodToCall, double value, double min, double max, double step,
            String label) {
            JSpinner spinner =
                new MySpinner(methodToCall, this.layout, this.item, value, min, max, step);
            spinner.addChangeListener(this);
            JLabel l = new JLabel(label);
            this.add(l);
            l.setLabelFor(spinner);
            this.add(spinner);
        }
    }

    private interface ReflectiveComponent {
        void setLayoutParameter();
    }

    private static class MyJSlider extends JSlider implements ReflectiveComponent {

        final Method methodToCall;
        final JGraphLayout layout;
        final LayouterItem item;

        MyJSlider(Method methodToCall, int min, int max, int init, JGraphLayout layout,
            LayouterItem item) {
            super(SwingConstants.HORIZONTAL, min, max, init);
            this.methodToCall = methodToCall;
            this.layout = layout;
            this.item = item;
            // Turn on labels at major tick marks.
            this.setMajorTickSpacing((max - min) / 5);
            this.setMinorTickSpacing((max - min) / 50);
            this.setPaintTicks(true);
            this.setPaintLabels(true);
            this.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            Font font = new Font("Serif", Font.ITALIC, 10);
            this.setFont(font);
        }

        @Override
        public void setLayoutParameter() {
            if (!this.getValueIsAdjusting()) {
                this.invoke();
                this.item.start();
            }
        }

        void invoke() {
            try {
                this.methodToCall.invoke(this.layout, this.getValue());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }
    }

    private static class MyCheckBox extends JCheckBox implements ReflectiveComponent {

        final Method methodToCall;
        final JGraphLayout layout;
        final LayouterItem item;

        MyCheckBox(Method methodToCall, JGraphLayout layout, LayouterItem item, String label,
            boolean selected) {
            super(label);
            this.methodToCall = methodToCall;
            this.layout = layout;
            this.item = item;
            this.setSelected(selected);
        }

        @Override
        public void setLayoutParameter() {
            this.invoke();
            this.item.start();
        }

        void invoke() {
            try {
                this.methodToCall.invoke(this.layout, this.isSelected());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }
    }

    private static class MyRadioButton extends JRadioButton implements ReflectiveComponent {

        final Method methodToCall;
        final JGraphLayout layout;
        final LayouterItem item;
        final int value;

        MyRadioButton(Method methodToCall, JGraphLayout layout, LayouterItem item, String label,
            int value) {
            super(label);
            this.methodToCall = methodToCall;
            this.layout = layout;
            this.item = item;
            this.value = value;
        }

        @Override
        public void setLayoutParameter() {
            this.invoke();
            this.item.start();
        }

        void invoke() {
            try {
                this.methodToCall.invoke(this.layout, this.value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }
    }

    private static class MySpinner extends JSpinner implements ReflectiveComponent {

        final Method methodToCall;
        final JGraphLayout layout;
        final LayouterItem item;

        MySpinner(Method methodToCall, JGraphLayout layout, LayouterItem item, double value,
            double min, double max, double step) {
            super(new SpinnerNumberModel(value, min, max, step));
            this.methodToCall = methodToCall;
            this.layout = layout;
            this.item = item;
        }

        @Override
        public void setLayoutParameter() {
            this.invoke();
            this.item.start();
        }

        @Override
        public SpinnerNumberModel getModel() {
            return (SpinnerNumberModel) super.getModel();
        }

        void invoke() {
            try {
                this.methodToCall.invoke(this.layout, this.getModel()
                    .getNumber()
                    .doubleValue());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }
    }
}
