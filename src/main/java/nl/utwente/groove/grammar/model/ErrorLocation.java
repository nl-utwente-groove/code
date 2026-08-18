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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Resource location information derived from the context of a
 * {@link FormatError}: the kind and names of the resources in which the error
 * occurs, the graph elements the error is anchored at, and the property key it
 * refers to. This reconstitutes the domain interpretation of the error context
 * that {@link FormatError} itself, being a general-purpose class, does not
 * perform.
 * @param kind the kind of the resources in which the error occurs;
 * {@code null} if the error carries no resource context
 * @param names the names of the resources in which the error occurs; may be empty
 * @param elements the graph elements at which the error is anchored; may be empty
 * @param key the property key to which the error refers, if any
 * @author Arend Rensink
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public record ErrorLocation(@Nullable ResourceKind kind, SortedSet<QualName> names,
    List<Element> elements, ResourceProperties.@Nullable Key key) {

    /** Derives the location information from the context of a given error. */
    public static ErrorLocation of(FormatError error) {
        ResourceKind kind = null;
        SortedSet<QualName> names = new TreeSet<>();
        List<Element> elements = new ArrayList<>();
        ResourceProperties.Key key = null;
        for (var par : error.getContext()) {
            if (par instanceof ResourceProperties.Key k) {
                key = k;
            } else if (par instanceof AspectGraph g) {
                kind = ResourceKind.toResource(g.getRole());
                names.add(g.getQualName());
            } else if (par instanceof ControlModel c) {
                kind = ResourceKind.CONTROL;
                names.add(c.getQualName());
            } else if (par instanceof PrologModel p) {
                kind = ResourceKind.PROLOG;
                names.add(p.getQualName());
            } else if (par instanceof Element e) {
                elements.add(e);
            } else if (par instanceof TypeGraph tg) {
                kind = ResourceKind.TYPE;
                names.add(tg.getQualName());
            } else if (par instanceof Rule r) {
                kind = ResourceKind.RULE;
                names.add(r.getQualName());
            } else if (par instanceof Recipe r) {
                kind = ResourceKind.CONTROL;
                names.add(r.getControlName());
            } else if (par instanceof GrammarKey k) {
                kind = ResourceKind.PROPERTIES;
                names.add(QualName.name(k.getName()));
            } else if (par instanceof ResourceId r) {
                kind = r.kind();
                names.add(r.name());
            }
        }
        return new ErrorLocation(kind, names, elements, key);
    }
}
