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

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import groove.grammar.Action.Role;
import groove.grammar.CheckPolicy;
import groove.grammar.QualName;
import groove.grammar.Signature;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.RuleModel;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphProperties.Key;
import groove.gui.Icons;
import groove.gui.display.ResourceDisplay;
import groove.io.HTMLConverter;
import groove.util.Exceptions;
import groove.util.Groove;
import groove.util.parse.FormatException;

/**
 * Rule nodes (= level 1 nodes) of the directory
 */
class RuleTreeNode extends ResourceTreeNode implements ActionTreeNode {
    /**
     * Creates a new rule node based on a given rule name. The node can have
     * children.
     */
    public RuleTreeNode(ResourceDisplay display, QualName ruleName) {
        super(display, ruleName, true);
        this.tried = true;
    }

    @Override
    public Icon getIcon() {
        Icon result;
        if (isRecipeChild()) {
            result = Icons.PUZZLE_ICON;
        } else if (super.getIcon() == Icons.EDIT_WIDE_ICON) {
            result = super.getIcon();
        } else {
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

    private boolean isRecipeChild() {
        return getParent() instanceof RecipeTreeNode;
    }

    @Override
    public boolean isProperty() {
        return getRule().isProperty();
    }

    /** Returns HTML-formatted tool tip text for this rule node. */
    @Override
    public String getTip() {
        StringBuilder result = new StringBuilder();
        result.append(getRule().getRole() == Role.TRANSFORMER ? "Rule" : getRule().getRole()
            .text(true));
        result.append(" ");
        result.append(HTMLConverter.ITALIC_TAG.on(getQualName()));
        AspectGraph source = getRule().getSource();
        String remark = GraphInfo.getRemark(source);
        if (!remark.isEmpty()) {
            result.append(": ");
            result.append(HTMLConverter.toHtml(remark));
        }
        if (isRecipeChild()) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Invoked as part of the recipe ");
            result
                .append(HTMLConverter.ITALIC_TAG.on(((RecipeTreeNode) getParent()).getQualName()));
        }
        if (getRule().getRole()
            .isConstraint()) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append(getRule().getPolicy()
                .getExplanation());
        }
        if (!GraphInfo.isEnabled(source)) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Explicitly disabled in the rule properties");
        } else if (getRule().getPolicy() == CheckPolicy.OFF) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Turned off by the rule policy in the grammar properties");
        } else if (!isRecipeChild() && getRule().hasRecipes()) {
            Set<QualName> recipes = getRule().getRecipes();
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Not enabled stand-alone because it is invoked from ");
            result.append(recipes.size() == 1 ? "recipe " : "recipes ");
            result.append(Groove.toString(getRule().getRecipes()
                .toArray(), "<i>", "</i>", "</i>, <i>", "</i> and <i>"));
        } else if (!isTried()) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Not scheduled in this state, due to rule priorities or control");
        } else if (getChildCount() == 0) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Scheduled in this state, but has no matches");
        }
        GraphProperties properties = GraphInfo.getProperties(source);
        Map<String,String> filteredProps = new LinkedHashMap<>();
        // collect the non-system, non-remark properties
        for (Key key : Key.values()) {
            if (key == Key.REMARK) {
                continue;
            }
            if (key.isSystem()) {
                continue;
            }
            if (key == Key.PRIORITY && getRule().getRole()
                .isConstraint()) {
                continue;
            }
            String value = properties.getProperty(key);
            if (!value.isEmpty()) {
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
        // display all properties
        for (Map.Entry<String,String> entry : filteredProps.entrySet()) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append(propertyToString(entry));
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
            showEnabled = isProperty() || !isPartial() || (getParent() instanceof RecipeTreeNode)
                || (getParent() instanceof StateTree.StateTreeNode);
        }
        String suffix = "";
        try {
            Signature<?> sig = getRule().getSignature();
            if (!sig.isEmpty()) {
                suffix = sig.toString();
            }
        } catch (FormatException exc) {
            // don't add a suffix string
        }
        suffix += getRule().isProperty() ? roleSuffixMap.get(getRule().getRole())
            : isRecipeChild() ? INGREDIENT_SUFFIX : RULE_SUFFIX;
        return getDisplay().getLabelText(getQualName(), suffix, showEnabled);
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

    private final static String INGREDIENT_SUFFIX =
        " : " + HTMLConverter.STRONG_TAG.on("ingredient");
    private final static String RULE_SUFFIX = " : " + HTMLConverter.STRONG_TAG.on("rule");
    private final static Map<Role,String> roleSuffixMap;

    /** Returns the icon map for normal or injective properties. */
    static private Map<Role,Icon> getIconMap(boolean injective) {
        return injective ? roleInjectiveIconMap : roleNormalIconMap;
    }

    private final static Map<Role,Icon> roleNormalIconMap;
    private final static Map<Role,Icon> roleInjectiveIconMap;

    static {
        Map<Role,String> suffixMap = roleSuffixMap = new EnumMap<>(Role.class);
        Map<Role,Icon> normalIconMap = roleNormalIconMap = new EnumMap<>(Role.class);
        Map<Role,Icon> injectiveIconMap = roleInjectiveIconMap = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            suffixMap.put(role, " : " + HTMLConverter.STRONG_TAG.on(role.toString()));
            Icon normalIcon;
            Icon injectiveIcon;
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
            case TRANSFORMER:
                normalIcon = null;
                injectiveIcon = null;
                break;
            default:
                throw Exceptions.UNREACHABLE;
            }
            if (normalIcon != null) {
                normalIconMap.put(role, normalIcon);
                injectiveIconMap.put(role, injectiveIcon);
            }
        }
    }

}