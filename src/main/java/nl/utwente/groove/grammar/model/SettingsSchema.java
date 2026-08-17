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
package nl.utwente.groove.grammar.model;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Vocabulary of a settings resource: the set of keys it may declare, and the
 * admissible values of those keys.
 * A schema is identified by its name, which is the top-level folder its
 * settings resources live in (and the value they may re-declare through the
 * reserved {@link SettingsModel#SCHEMA_KEY} key).
 * Schemas are made known to the settings mechanism by registering them with
 * {@link SettingsSchemas}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface SettingsSchema {
    /** Returns the name of this schema, under which it is registered. */
    public String getName();

    /**
     * Returns the resource name of the settings resource of this schema with a
     * given <i>local</i> name: the local name nested inside the schema's
     * top-level folder. Schema-specific contexts — references stored in the
     * grammar properties, selectors and name prompts of schema-specific
     * dialogs — use local names, since in such a context the folder is implied
     * by the schema; this method converts back to the resource name that
     * identifies the resource within the grammar.
     * @param localName the name of the resource within the schema folder
     */
    default public QualName getResourceName(QualName localName) {
        return localName.nest(getName());
    }

    /**
     * Returns the local name of a settings resource of this schema: the
     * resource name with its leading schema segment stripped off. Inverse of
     * {@link #getResourceName(QualName)}.
     * @param resourceName the name of a resource in the schema's folder; the
     * singleton form (the bare schema name) has no local name and is not
     * allowed here
     * @throws IllegalArgumentException if {@code resourceName} does not name a
     * resource inside this schema's folder
     */
    default public QualName getLocalName(QualName resourceName) {
        if (resourceName.size() < 2 || !resourceName.get(0).equals(getName())) {
            throw Exceptions
                .illegalArg("'%s' does not name a resource inside the '%s' folder", resourceName,
                            getName());
        }
        return resourceName.removeParent(QualName.name(getName()));
    }

    /**
     * Checks a set of settings entries against this schema.
     * The reserved {@link SettingsModel#SCHEMA_KEY} entry is included in the
     * properties, and should be disregarded by the implementation.
     * @param props the entries to be checked; non-{@code null}
     * @return a (non-{@code null}, possibly empty) set of errors in the entries
     */
    public FormatErrorSet check(Properties props);

    /**
     * Checks a set of settings entries against this schema, in the context of
     * the grammar holding the settings resource. The default implementation
     * ignores the grammar and delegates to {@link #check(Properties)}; a
     * schema whose entries refer to other grammar resources (rules, labels,
     * ...) should override this method, and declare the resource kinds it
     * inspects in {@link #getDependencies()} so that its errors are recomputed
     * when those resources change.
     * @param grammar the grammar holding the resource; may be {@code null} if
     * the resource is being considered outside the context of a grammar
     * @param props the entries to be checked; non-{@code null}
     * @return a (non-{@code null}, possibly empty) set of errors in the entries
     */
    default public FormatErrorSet check(@Nullable GrammarModel grammar, Properties props) {
        return check(props);
    }

    /**
     * Checks the parsed content of a settings resource against this schema.
     * The default implementation ignores the key positions recorded in the
     * content and delegates to {@link #check(GrammarModel, Properties)}; a
     * schema that wants its errors to carry the line and column of the key
     * they are about — so that selecting the error in the settings display
     * jumps to the offending line — should override this method, and pass
     * {@link SettingsContent#numbers(String)} as an additional argument to the
     * errors it creates.
     * @param grammar the grammar holding the resource; may be {@code null} if
     * the resource is being considered outside the context of a grammar
     * @param content the parsed content to be checked; non-{@code null}
     * @return a (non-{@code null}, possibly empty) set of errors in the entries
     */
    default public FormatErrorSet check(@Nullable GrammarModel grammar, SettingsContent content) {
        return check(grammar, content.properties());
    }

    /**
     * Returns the resource kinds (other than the settings themselves) that
     * {@link #check(GrammarModel, Properties)} inspects. A settings resource
     * of this schema is rechecked whenever one of these kinds changes.
     * The default implementation returns the empty set.
     */
    default public Set<ResourceKind> getDependencies() {
        return Collections.emptySet();
    }

    /**
     * Indicates if this schema is singular, meaning that it admits at most one
     * settings resource per grammar. All resources of an over-populated
     * singular schema are flagged with an error.
     * The default implementation returns {@code false}.
     */
    default public boolean isSingular() {
        return false;
    }

    /**
     * Indicates if resources of this schema can be <i>activated</i>: singled
     * out as the one resource of the schema that the grammar currently uses,
     * through an explicit reference in the grammar properties.
     * An activatable schema must also override
     * {@link #isActive(GrammarModel, QualName)} and
     * {@link #setActive(GrammarProperties, QualName, boolean)}.
     * The default implementation returns {@code false}.
     */
    default public boolean isActivatable() {
        return false;
    }

    /**
     * Tests if a given settings resource of this schema is currently active
     * in a given grammar. For a non-activatable schema (see
     * {@link #isActivatable()}) every resource counts as active, meaning it
     * is consulted by its client whenever applicable; this is what the
     * default implementation returns.
     * @param grammar the grammar holding the resource
     * @param name the name of the resource
     */
    default public boolean isActive(GrammarModel grammar, QualName name) {
        return true;
    }

    /**
     * Activates or deactivates a given settings resource of this schema, by
     * modifying the grammar properties accordingly. Only supported if this
     * schema is activatable (see {@link #isActivatable()}).
     * @param properties the (modifiable) grammar properties
     * @param name the name of the resource to be (de)activated
     * @param active if {@code true}, the resource becomes the schema's active
     * one; otherwise, the schema reverts to having no active resource
     */
    default public void setActive(GrammarProperties properties, QualName name, boolean active) {
        throw Exceptions.unsupportedOp("Schema '%s' does not support activation", getName());
    }

    /**
     * Returns a description of the (de)activation action for resources of
     * this schema, used (among others) as the tool tip of the enable button
     * in the settings display. Only meaningful if this schema is activatable
     * (see {@link #isActivatable()}).
     * @param activate if {@code true}, the description is for activation,
     * otherwise for deactivation
     */
    default public String getActivationText(boolean activate) {
        return String
            .format("%s this %s settings resource", activate
                ? "Activate"
                : "Deactivate", getName());
    }

    /**
     * Returns a syntax help map documenting the keys of this schema, from
     * (HTML-formatted) key syntax lines to tool tips; shown in the settings
     * display. A schema whose keys are not a fixed set may document the key
     * forms rather than individual keys.
     * @return a (non-{@code null}, possibly empty) help map
     */
    default public HelpMap getHelpMap() {
        return new HelpMap();
    }

    /**
     * Returns a plain-text explanation of the general purpose of settings
     * resources of this schema, for user guidance; the empty string if there
     * is none. Used (among others) as the generated comment header of a new
     * settings resource.
     */
    default public String getExplanation() {
        return "";
    }

    /**
     * Returns the initial text for a new settings resource of this schema.
     * The default implementation generates the explanation as comment lines,
     * followed by a {@link SettingsModel#SCHEMA_KEY} entry; schemas with a
     * documented vocabulary are advised to append their keys as commented-out
     * example lines.
     */
    default public String getNewText() {
        StringBuilder result = new StringBuilder();
        // wrap the explanation into comment lines
        StringBuilder line = new StringBuilder();
        for (String word : getExplanation().split(" +")) {
            if (word.isEmpty()) {
                continue;
            }
            if (line.length() > 0 && line.length() + word.length() > 74) {
                result.append("# ").append(line).append('\n');
                line.setLength(0);
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(word);
        }
        if (line.length() > 0) {
            result.append("# ").append(line).append('\n');
        }
        result.append(SettingsModel.SCHEMA_KEY).append(" = ").append(getName()).append('\n');
        return result.toString();
    }
}
