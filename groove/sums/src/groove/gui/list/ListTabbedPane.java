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
package groove.gui.list;

import groove.gui.Icons;
import groove.gui.display.TabLabel;
import groove.gui.list.ListPanel.SelectableListEntry;
import groove.io.HTMLConverter;

import java.util.Collections;

import javax.swing.JTabbedPane;

/**
 * Tabbed pane for lists.
 * @author Eduardo Zambon
 */
public final class ListTabbedPane extends JTabbedPane {

    private final ErrorListPanel errorList;
    private final SearchResultListPanel searchList;
    private int errorTabIndex;
    private TabLabel searchTitle;
    private int searchTabIndex;

    /** Constructs a fresh instance, for a given simulator. */
    public ListTabbedPane() {
        this.errorList = new ErrorListPanel(null);
        this.searchList = new SearchResultListPanel(null, this);
        this.errorTabIndex = -1;
        this.searchTabIndex = -1;
        this.adjustVisibility();
    }

    private void addErrorTab() {
        this.errorTabIndex = 0;
        if (this.isSearchTabVisible()) {
            this.searchTabIndex++;
        }
        this.insertTab(getErrorTitle(), Icons.ERROR_ICON, this.errorList,
            "Errors", this.errorTabIndex);
    }

    private void addSearchTab() {
        this.searchTabIndex = this.getTabCount();
        this.add(this.searchList);
        this.setTabComponentAt(this.searchTabIndex, getSearchTitle());
    }

    private void removeErrorTab() {
        this.removeTabAt(this.errorTabIndex);
        this.errorTabIndex = -1;
        if (this.isSearchTabVisible()) {
            this.searchTabIndex--;
        }
    }

    private void removeSearchTab() {
        this.removeTabAt(this.searchTabIndex);
        this.searchTabIndex = -1;
    }

    /** Closes the search tab. */
    public void closeSearchTab() {
        this.searchList.setEntries(Collections.<SelectableListEntry>emptySet());
        this.adjustVisibility();
    }

    private String getErrorTitle() {
        return HTMLConverter.HTML_TAG.on(HTMLConverter.STRONG_TAG.on("Errors in grammar"));
    }

    private TabLabel getSearchTitle() {
        if (this.searchTitle == null) {
            this.searchTitle =
                new TabLabel(this, Icons.SEARCH_ICON, "Search results");
        }
        return this.searchTitle;
    }

    private boolean isErrorTabVisible() {
        return this.errorTabIndex >= 0;
    }

    private boolean isSearchTabVisible() {
        return this.searchTabIndex >= 0;
    }

    /** Basic getter function. */
    public ErrorListPanel getErrorListPanel() {
        return this.errorList;
    }

    /** Basic getter function. */
    public SearchResultListPanel getSearchResultListPanel() {
        return this.searchList;
    }

    /** Toggles the visibility of this pane. */
    public void adjustVisibility() {
        if (this.errorList.hasContent() && !this.isErrorTabVisible()) {
            this.addErrorTab();
        }
        if (!this.errorList.hasContent() && this.isErrorTabVisible()) {
            this.removeErrorTab();
        }
        if (this.searchList.hasContent() && !this.isSearchTabVisible()) {
            this.addSearchTab();
        }
        if (!this.searchList.hasContent() && this.isSearchTabVisible()) {
            this.removeSearchTab();
        }
        if (this.isSearchTabVisible()) {
            this.setSelectedIndex(this.searchTabIndex);
        }
        setVisible(this.isErrorTabVisible() || this.isSearchTabVisible());
    }
}
