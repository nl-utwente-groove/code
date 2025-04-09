/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.tree;

import static nl.utwente.groove.io.HTMLConverter.HTML_LINEBREAK;
import static nl.utwente.groove.io.HTMLConverter.HTML_PAR_5PT;
import static nl.utwente.groove.io.HTMLConverter.HTML_TAG;
import static nl.utwente.groove.io.HTMLConverter.STRONG_TAG;
import static nl.utwente.groove.util.Properties.INFO_COLOR_TAG;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Icon;

import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphProperties;
import nl.utwente.groove.graph.GraphProperties.Key;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.io.HTMLConverter.HTMLTag;
import nl.utwente.groove.io.Util;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Rule nodes (= level 1 nodes) of the directory
 */
class RuleTreeNode extends ActionTreeNode {
    /**
     * Creates a new action node node based on a given rule.
     */
    public RuleTreeNode(ResourceDisplay display, RuleModel ruleModel) {
        super(display, ruleModel.getQualName());
        this.ruleModel = ruleModel;
    }

    /**
     * Creates a new action node node based on a given rule name.
     */
    public RuleTreeNode(ResourceDisplay display, Rule rule) {
        super(display, rule.getQualName());
        this.rule = rule;
    }

    @Override
    Icon getIcon() {
        Icon result;
        if (isFragment()) {
            result = Icons.PUZZLE_ICON;
        } else if (super.getIcon() == Icons.EDIT_WIDE_ICON) {
            result = super.getIcon();
        } else {
            boolean injective = getRuleModel().isInjective();
            if (getRuleModel().isProperty()) {
                result = getIconMap(injective).get(getRuleModel().getRole());
            } else {
                result = injective
                    ? Icons.RULE_I_TREE_ICON
                    : Icons.RULE_TREE_ICON;
            }
        }
        return result;
    }

    /** Returns HTML-formatted tool tip text for this rule node. */
    @Override
    String getTip() {
        StringBuilder result = new StringBuilder();
        var ruleRole = getRuleModel().getRole();
        result
            .append(ruleRole == Role.TRANSFORMER
                ? "Rule"
                : ruleRole.text(true));
        result.append(" ");
        result.append(HTMLConverter.ITALIC_TAG.on(getQualName()));
        AspectGraph source = getRuleGraph();
        String remark = GraphInfo.getRemark(source);
        if (!remark.isEmpty()) {
            result.append(": ");
            result.append(HTMLConverter.toHtml(remark));
        }
        if (isFragment()) {
            var recipes = getRuleModel().getRecipes();
            result.append(HTML_LINEBREAK);
            result.append("Invoked from ");
            result
                .append(recipes.size() == 1
                    ? "recipe "
                    : "recipes ");
            result
                .append(Groove
                    .toString(recipes.toArray(), "<i>", "</i>", "</i>, <i>", "</i> and <i>"));
        }
        var rulePolicy = getRuleModel().getPolicy();
        if (ruleRole.isConstraint()) {
            result.append(HTML_LINEBREAK);
            result.append(rulePolicy.getExplanation());
        }
        if (!getRuleModel().isActive()) {
            result.append(HTML_LINEBREAK);
            result.append("Explicitly disabled in the rule or system properties");
        } else if (rulePolicy == CheckPolicy.OFF) {
            result.append(HTML_LINEBREAK);
            result.append("Turned off by the rule policy in the system properties");
        } else if (getStatus() == Status.STANDBY) {
            result.append(HTML_LINEBREAK);
            result.append("Not scheduled in this state, due to rule priorities or control");
        } else if (getChildCount() == 0) {
            result.append(HTML_LINEBREAK);
            result.append("Scheduled in this state, but has no matches");
        }
        GraphProperties properties = source.getProperties();
        if (properties.isNotable()) {
            result.append(HTML_PAR_5PT);
            result
                .append(INFO_COLOR_TAG
                    .on("Notable non-detault rule properties (consult Properties tab for more info):"));
            result.append(properties.getNotableProperties());
        }
        Map<String,String> filteredProps = new LinkedHashMap<>();
        // collect the non-system, non-remark properties
        for (Key key : Key.values()) {
            if (key == Key.REMARK) {
                continue;
            }
            if (key.isSystem()) {
                continue;
            }
            if (key == Key.PRIORITY && ruleRole.isConstraint()) {
                continue;
            }
            String value = properties.getProperty(key);
            if (value != null && !value.isEmpty()) {
                filteredProps.put(key.getKeyPhrase(), value);
            }
        }
        // collect the user properties
        properties
            .entryStream()
            .filter(e -> GraphProperties.Key.isKey(e.getKey()))
            .filter(e -> !e.getValue().isEmpty())
            .forEach(e -> {
                result.append(HTML_LINEBREAK);
                result.append(propertyToString(e));
            });
        HTML_TAG.on(result);
        return result.toString();
    }

    /** Returns an HTML-formatted string for a given key/value-pair. */
    private String propertyToString(Map.Entry<String,String> entry) {
        return "<b>" + entry.getKey() + "</b> = " + entry.getValue();
    }

    RuleModel getRuleModel() {
        RuleModel result = this.ruleModel;
        if (result == null) {
            this.ruleModel = result = ((RuleModel) getDisplay().getResource(getQualName()));
        }
        return result;
    }

    private RuleModel ruleModel;

    private AspectGraph getRuleGraph() {
        return getRuleModel().getSource();
    }

    /**
     * Returns the rule associated with this node.
     */
    private Rule getRule() throws FormatException {
        Rule result = this.rule;
        if (result == null) {
            this.rule = result = getRuleModel().toResource();
        }
        return result;
    }

    private Rule rule;

    /** Indicates if this rule node is part of a recipe. */
    private boolean isFragment() {
        return getRuleModel().hasRecipes();
    }

    @Override
    String getText() {
        String suffix = "";
        try {
            var sig = getRule().getSignature();
            if (!sig.isEmpty()) {
                suffix = sig.toString();
            }
        } catch (FormatException exc) {
            // do nothing
        }
        suffix += getRuleModel().isProperty()
            ? roleSuffixMap.get(getRuleModel().getRole())
            : isFragment()
                ? INGREDIENT_SUFFIX
                : RULE_SUFFIX;
        var result
            = getDisplay().getLabelText(getQualName(), suffix, getStatus().isEnabled(), false);
        if (getRuleGraph().getProperties().isNotable()) {
            result += "  " + INFO_SYMBOL;
        }
        return result;
    }

    @Override
    Status getStatus() {
        return isActivated()
            ? Status.ACTIVE
            : getRuleModel().isActive()
                ? Status.STANDBY
                : Status.DISABLED;
    }

    private final static String INGREDIENT_SUFFIX
        = " : " + HTMLConverter.STRONG_TAG.on("ingredient");
    private final static String RULE_SUFFIX = " : " + HTMLConverter.STRONG_TAG.on("rule");
    private final static Map<Role,String> roleSuffixMap;

    /** Returns the icon map for normal or injective properties. */
    static private Map<Role,Icon> getIconMap(boolean injective) {
        return injective
            ? roleInjectiveIconMap
            : roleNormalIconMap;
    }

    private final static Map<Role,Icon> roleNormalIconMap;
    private final static Map<Role,Icon> roleInjectiveIconMap;

    static {
        Map<Role,String> suffixMap = roleSuffixMap = new EnumMap<>(Role.class);
        Map<Role,Icon> normalIconMap = roleNormalIconMap = new EnumMap<>(Role.class);
        Map<Role,Icon> injectiveIconMap = roleInjectiveIconMap = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            suffixMap.put(role, " : " + STRONG_TAG.on(role.toString()));
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

    static private final HTMLTag INFO_FONT_TAG
        = new HTMLTag("span", "style", "font-family: 'Display'; font-size:15");
    static private final String INFO_SYMBOL
        = INFO_COLOR_TAG.on(STRONG_TAG.on(INFO_FONT_TAG.on(Util.INFO_SYMBOL)));
}