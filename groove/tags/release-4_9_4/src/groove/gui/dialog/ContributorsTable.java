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
package groove.gui.dialog;

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.UNDERLINE_TAG;
import groove.io.HTMLConverter;
import groove.io.Util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table containing information about the auxiliary libraries used in Groove. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class ContributorsTable extends JTable {
    private ContributorsTable() {
        this.urlRenderer = new URLRenderer();
        addMouseListener(this.urlRenderer);
        addMouseMotionListener(this.urlRenderer);
        setModel(new TableModel(Util.readCSV("contributors", ';')));
        for (int i = 0; i < getColumnCount(); ++i) {
            int width = 0;
            for (int j = 0; j < getRowCount(); j++) {
                Component renderer = prepareRenderer(getCellRenderer(j, i), j, i);
                // Set the preferred width to at least the width of the cell
                width = Math.max(renderer.getPreferredSize().width, width);
            }
            getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }

    /** Shows the content of this table as a dialog. */
    public void showDialog(Component parent, String title) {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.getViewport().setPreferredSize(getPreferredSize());
        JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setVisible(true);
    }

    /** Shows the dialog. */
    public static void main(String[] args) {
        instance().showDialog(null, "Contributors");
    }

    /** Returns the single instance of this table. */
    public static ContributorsTable instance() {
        return instance;
    }

    private final URLRenderer urlRenderer;

    private static ContributorsTable instance = new ContributorsTable();

    /** Model for this table. */
    private class TableModel extends AbstractTableModel {
        TableModel(List<String[]> data) {
            assert !data.isEmpty();
            this.data = data;
        }

        @Override
        public String getColumnName(int column) {
            return "Name";
        }

        @Override
        public int getRowCount() {
            return this.data.size();
        }

        @Override
        public int getColumnCount() {
            return this.data.get(0).length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return this.data.get(rowIndex)[columnIndex];
        }

        private final List<String[]> data;
    }

    /**
     * Taken from {@link "http://java-swing-tips.blogspot.nl/2009/02/hyperlink-in-jtable-cell.html"}
     * @author TERAI Atsuhiro
     */
    private static class URLRenderer extends DefaultTableCellRenderer implements MouseListener,
            MouseMotionListener {
        private int row = -1;
        private int col = -1;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            StringBuilder text = new StringBuilder(value.toString());
            BLUE_TAG.on(text);
            if (this.row == row && this.col == column) {
                UNDERLINE_TAG.on(text);
            }
            setText(HTML_TAG.on(text).toString());
            return this;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            JTable table = (JTable) e.getSource();
            Point pt = e.getPoint();
            int prev_row = this.row;
            int prev_col = this.col;
            this.row = table.rowAtPoint(pt);
            this.col = table.columnAtPoint(pt);
            if (this.row != prev_row || this.col != prev_col) {
                Rectangle r = table.getCellRect(this.row, this.col, false);
                r = r.union(table.getCellRect(prev_row, prev_col, false));
                table.repaint(r);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JTable table = (JTable) e.getSource();
            table.repaint(table.getCellRect(this.row, this.col, false));
            this.row = -1;
            this.col = -1;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JTable table = (JTable) e.getSource();
            Point pt = e.getPoint();
            int ccol = table.columnAtPoint(pt);
            int crow = table.rowAtPoint(pt);
            Object value = table.getValueAt(crow, ccol);
            if (value instanceof URL) {
                URL url = (URL) value;
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(url.toURI());
                    }
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // do nothing
        }

        private static final HTMLConverter.HTMLTag BLUE_TAG =
            HTMLConverter.createColorTag(Color.BLUE);
    }
}
