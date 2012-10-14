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
package groove.gui;

import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.io.HTMLConverter;
import groove.view.RuleModel;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Rule nodes (= level 1 nodes) of the directory
 */
class RuleTreeNode extends DefaultMutableTreeNode {
    /**
     * Creates a new rule node based on a given rule name. The node can have
     * children.
     */
    public RuleTreeNode(RuleModel rule) {
        super(rule, true);
    }

    /**
     * Convenience method to retrieve the user object as a rule name.
     */
    public RuleModel getRule() {
        return (RuleModel) getUserObject();
    }

    /**
     * To display, show child name only.
     */
    @Override
    public String toString() {
        return getRule().getLastName();
    }

    /** Returns HTML-formatted tool tip text for this rule node. */
    public String getToolTipText() {
        StringBuilder result = new StringBuilder();
        result.append("Rule ");
        result.append(HTMLConverter.STRONG_TAG.on(getRule().getFullName()));
        GraphProperties properties =
            GraphInfo.getProperties(getRule().getSource(), false);
        if (properties != null && !properties.isEmpty()) {
            boolean hasProperties;
            String remark = properties.getRemark();
            if (remark != null) {
                result.append(": ");
                result.append(HTMLConverter.toHtml(remark));
                hasProperties = properties.size() > 1;
            } else {
                hasProperties = true;
            }
            if (hasProperties) {
                for (String key : properties.getPropertyKeys()) {
                    if (!GraphProperties.isSystemKey(key)
                        && !key.equals(GraphProperties.REMARK_KEY)) {
                        result.append(HTMLConverter.HTML_LINEBREAK);
                        result.append(propertyToString(key,
                            properties.getProperty(key)));
                    }
                }
            }
        }
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    /** Returns an HTML-formatted string for a given key/value-pair. */
    private String propertyToString(String key, String value) {
        return "<b>" + key + "</b> = " + value;
    }
}