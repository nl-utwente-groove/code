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
package groove.gui.tree;

import groove.grammar.Action.Role;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.RuleModel;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphProperties.Key;
import groove.gui.Icons;
import groove.gui.display.ResourceDisplay;
import groove.io.HTMLConverter;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Icon;

/**
 * Rule nodes (= level 1 nodes) of the directory
 */
class RuleTreeNode extends ResourceTreeNode implements ActionTreeNode {
    /**
     * Creates a new rule node based on a given rule name. The node can have
     * children.
     */
    public RuleTreeNode(ResourceDisplay display, String ruleName) {
        super(display, ruleName, true);
        this.tried = true;
    }

    @Override
    public Icon getIcon() {
        Icon result = super.getIcon();
        if (result != Icons.EDIT_WIDE_ICON) {
            boolean injective = getRule().isInjective();
            if (getRule().isProperty()) {
                result = getIconMap(injective).get(getRule().getRole());
            } else {
                result = injective ? Icons.RULE_I_TREE_ICON : Icons.RULE_TREE_ICON;
            }
        }
        return result;
    }

    /**
     * Convenience method to retrieve the user object as a rule name.
     */
    public RuleModel getRule() {
        return (RuleModel) getResource();
    }

    /** Indicates if this rule node is part of a recipe. */
    public boolean isPartial() {
        return getRule().hasRecipes();
    }

    @Override
    public boolean isProperty() {
        return getRule().isProperty();
    }

    /** Returns HTML-formatted tool tip text for this rule node. */
    @Override
    public String getTip() {
        StringBuilder result = new StringBuilder();
        result.append(getRule().isProperty() ? getRule().getRole().text(true) : "Rule ");
        result.append(" ");
        result.append(HTMLConverter.STRONG_TAG.on(getName()));
        AspectGraph source = getRule().getSource();
        String remark = GraphInfo.getRemark(source);
        if (!remark.isEmpty()) {
            result.append(": ");
            result.append(HTMLConverter.toHtml(remark));
        }
        GraphProperties properties = GraphInfo.getProperties(source);
        Map<String,String> filteredProps = new LinkedHashMap<String,String>();
        // collect the non-system, non-remark properties
        for (Key key : Key.values()) {
            String value = properties.getProperty(key);
            if (key != Key.REMARK && !key.isSystem() && !value.isEmpty()) {
                filteredProps.put(key.getKeyPhrase(), value);
            }
        }
        // collect the user properties
        for (Map.Entry<Object,Object> entry : properties.entrySet()) {
            String keyword = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (!GraphProperties.Key.isKey(keyword) && !value.isEmpty()) {
                filteredProps.put(keyword, value);
            }
        }
        // display everything
        for (Map.Entry<String,String> entry : filteredProps.entrySet()) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append(propertyToString(entry));
        }
        if (!isTried() && GraphInfo.isEnabled(source)) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Not scheduled in this state, due to rule priorities or control");
        }
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    /** Returns an HTML-formatted string for a given key/value-pair. */
    private String propertyToString(Map.Entry<String,String> entry) {
        return "<b>" + entry.getKey() + "</b> = " + entry.getValue();
    }

    @Override
    public boolean isEnabled() {
        return this.tried;
    }

    /** Returns the text to be displayed on the tree node. */
    @Override
    public String getText() {
        boolean showEnabled = getRule().isEnabled();
        if (showEnabled) {
            showEnabled =
                isProperty() || !isPartial() || (getParent() instanceof RecipeTreeNode)
                    || (getParent() instanceof StateTree.StateTreeNode);
        }
        String suffix =
            getRule().isProperty() ? roleSuffixMap.get(getRule().getRole()) : isPartial()
                ? SUBRULE_SUFFIX : RULE_SUFFIX;
        return getDisplay().getLabelText(getName(), suffix, showEnabled);
    }

    /** Indicates if the rule wrapped by this node has been tried on the current state. */
    public boolean isTried() {
        return this.tried;
    }

    /** Sets the tried state of the rule wrapped by this node. */
    public void setTried(boolean tried) {
        this.tried = tried;
    }

    /** Flag indicating whether the rule has been tried on the displayed state. */
    private boolean tried;

    private final static String SUBRULE_SUFFIX = " : " + HTMLConverter.STRONG_TAG.on("subrule");
    private final static String RULE_SUFFIX = " : " + HTMLConverter.STRONG_TAG.on("singular");
    private final static Map<Role,String> roleSuffixMap;

    /** Returns the icon map for normal or injective properties. */
    static private Map<Role,Icon> getIconMap(boolean injective) {
        return injective ? roleInjectiveIconMap : roleNormalIconMap;
    }

    private final static Map<Role,Icon> roleNormalIconMap;
    private final static Map<Role,Icon> roleInjectiveIconMap;
    static {
        Map<Role,String> suffixMap = roleSuffixMap = new EnumMap<Role,String>(Role.class);
        Map<Role,Icon> normalIconMap = roleNormalIconMap = new EnumMap<Role,Icon>(Role.class);
        Map<Role,Icon> injectiveIconMap = roleInjectiveIconMap = new EnumMap<Role,Icon>(Role.class);
        for (Role role : Role.values()) {
            suffixMap.put(role, " : " + HTMLConverter.STRONG_TAG.on(role.toString()));
            Icon normalIcon = null;
            Icon injectiveIcon = null;
            switch (role) {
            case CONDITION:
                normalIcon = Icons.CONDITION_TREE_ICON;
                injectiveIcon = Icons.CONDITION_I_TREE_ICON;
                break;
            case FORBIDDEN:
                normalIcon = Icons.FORBIDDEN_TREE_ICON;
                injectiveIcon = Icons.FORBIDDEN_I_TREE_ICON;
                break;
            case INVARIANT:
                normalIcon = Icons.INVARIANT_TREE_ICON;
                injectiveIcon = Icons.INVARIANT_I_TREE_ICON;
                break;
            }
            if (normalIcon != null) {
                normalIconMap.put(role, normalIcon);
                injectiveIconMap.put(role, injectiveIcon);
            }
        }
    }

}