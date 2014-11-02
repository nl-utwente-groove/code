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
package groove.grammar.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction of a text-based resource.
 * This is essentially a list of strings, corresponding to the lines of the resource,
 * with a name and resource type.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Text implements Resource {
    /**
     * Creates an empty resource of a given name and type.
     */
    public Text(ResourceKind kind, String name, List<String> lines) {
        this.kind = kind;
        this.name = name;
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Constructs a text from a given single-string content.
     * The content will be split into lines at line breaks.
     */
    public Text(ResourceKind kind, String name, String content) {
        this.kind = kind;
        this.name = name;
        this.content = content;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public ResourceKind getKind() {
        return this.kind;
    }

    private final ResourceKind kind;

    /** Returns the content of this text, as a single string. */
    public String getContent() {
        if (this.content == null) {
            this.content = computeContent();
        }
        return this.content;
    }

    /** Content of this text, as a single string. */
    private String content;

    /** Computes the single-string content of this text, based on the stored lines. */
    private String computeContent() {
        assert this.lines != null;
        StringBuilder result = new StringBuilder();
        for (String line : getLines()) {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }

    /** Returns the lines of this text. */
    public List<String> getLines() {
        if (this.lines == null) {
            this.lines = computeLines();
        }
        return this.lines;
    }

    private List<String> lines;

    /** Computes the lines from the string content. */
    private List<String> computeLines() {
        assert this.content != null;
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(this.content))) {
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
        } catch (IOException exc) {
            assert false : "Shouldn't happen for a StringReader";
        }
        return result;
    }

    /**
     * Returns a renamed version of this text.
     * Reuses this object if the old and new names coincide.
     */
    @Override
    public Text rename(String newName) {
        Text result;
        if (newName.equals(getName())) {
            result = this;
        } else if (this.content == null) {
            result = new Text(getKind(), newName, this.lines);
        } else {
            result = new Text(getKind(), newName, this.content);
            // Copy over the lines,  for the sake of efficiency
            result.lines = this.lines;
        }
        return result;
    }
}
