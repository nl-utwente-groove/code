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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Table containing information about the auxiliary libraries used in Groove.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LibrariesTable extends JTable {
    private LibrariesTable() {
        this.urlRenderer = new URLRenderer();
        addMouseListener(this.urlRenderer);
        addMouseMotionListener(this.urlRenderer);
        setModel(new TableModel(Util.readCSV("libraries", ';')));
        for (int i = 0; i < getColumnCount(); ++i) {
            getColumnModel().getColumn(i).setPreferredWidth(0);
        }
    }

    /* Overridden to set the preferred width of the columns to the
     * maximum preferred width of the content.
     * See http://binkley.blogspot.nl/2006/01/getting-jtable-columns-widths-to-fit.html
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIx, int colIx) {
        Component result = super.prepareRenderer(renderer, rowIx, colIx);
        // Set the preferred width to at least the width of the cell
        TableColumn col = getColumnModel().getColumn(colIx);
        col.setPreferredWidth(Math.max(result.getPreferredSize().width, col.getPreferredWidth()));

        return result;
    }

    @Override
    public int convertColumnIndexToModel(int viewColumnIndex) {
        int result = viewColumnIndex;
        if (result >= Column.DESCRIPTION.ordinal()) {
            result++;
        }
        return result;
    }

    @Override
    public int convertColumnIndexToView(int modelColumnIndex) {
        int result = modelColumnIndex;
        if (result == Column.DESCRIPTION.ordinal()) {
            result = -1;
        } else if (result > Column.DESCRIPTION.ordinal()) {
            result--;
        }
        return result;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (convertColumnIndexToModel(column) == Column.URL.ordinal()) {
            return this.urlRenderer;
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    @Override
    public String getToolTipText(MouseEvent evt) {
        int row = rowAtPoint(evt.getPoint());
        return "Library for " + getModel().getValueAt(row, Column.DESCRIPTION.ordinal());
    }

    /** Shows the content of this table as a dialog. */
    public void showDialog(Component parent) {
        JScrollPane scrollPane = new JScrollPane(this);
        Dimension size = scrollPane.getPreferredSize();
        size.height = getPreferredSize().height;
        scrollPane.getViewport().setPreferredSize(size);
        JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog(parent, "External libraries used in GROOVE");
        dialog.setVisible(true);
    }

    /** Shows the dialog. */
    public static void main(String[] args) {
        instance().showDialog(null);
    }

    /** Returns the single instance of this table. */
    public static LibrariesTable instance() {
        return instance;
    }

    private final URLRenderer urlRenderer;

    private static LibrariesTable instance = new LibrariesTable();

    /** Table columns. */
    private static enum Column {
        /** Name of the library. */
        NAME("Name"),
        /** One-line description of the library function. */
        DESCRIPTION("Description"),
        /** URL where info about the library can be found. */
        URL("URL") {
            @Override
            public java.net.URL format(String value) {
                try {
                    return new java.net.URL(value);
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        },
        /** Included version of the library. */
        VERSION("Version");

        private Column(String header) {
            this.header = header;
        }

        /** Returns an HTML-formatted version of the value. */
        Object format(String value) {
            return value;
        }

        /** Returns the column header. */
        public String getHeader() {
            return this.header;
        }

        private final String header;
    }

    /** Model for this table. */
    private class TableModel extends AbstractTableModel {
        TableModel(List<String[]> data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return this.data.size();
        }

        /* Note that this should really return the number of model columns,
         * but the JTable implementation is buggy in this respect.
         */
        @Override
        public int getColumnCount() {
            return Column.values().length - 1;
        }

        /* Note that the conversion to the model index should be superfluous,
         * but the JTable implementation is buggy in this respect.
         */
        @Override
        public String getColumnName(int column) {
            return Column.values()[convertColumnIndexToModel(column)].getHeader();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String result = this.data.get(rowIndex)[columnIndex];
            return Column.values()[columnIndex].format(result);
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
                Desktop desktop = null;
                try {
                    if (Desktop.isDesktopSupported()) {
                        desktop = Desktop.getDesktop();
                    }
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
                if (desktop != null) {
                    try {
                        desktop.browse(url.toURI());
                    } catch (Exception exc) {
                        // browsing failed; just don't do anything
                    }
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
