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
package groove.io.conceptual.lang.graphviz;

import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Id;

public class GraphvizResource extends ExportableResource {
    private Map<String,Graph> m_typeGraphs = new HashMap<String,Graph>();
    private Map<String,Graph> m_instanceGraphs = new HashMap<String,Graph>();

    private Path m_typeFile;
    private Path m_instanceFile;

    public GraphvizResource(Path typeTarget, Path instanceTarget) {
        this.m_typeFile = typeTarget;
        this.m_instanceFile = instanceTarget;
    }

    public Graph getTypeGraph(String name) {
        if (this.m_typeGraphs.containsKey(name)) {
            return this.m_typeGraphs.get(name);
        }

        Graph g = new Graph();
        g.setId(new Id());
        g.getId().setId("\"" + name + "\"");
        g.setType(com.alexmerz.graphviz.objects.Graph.DIRECTED);
        g.addGenericNodeAttribute("shape", "box");

        this.m_typeGraphs.put(name, g);

        return g;
    }

    public Graph getInstanceGraph(String name) {
        if (this.m_instanceGraphs.containsKey(name)) {
            return this.m_instanceGraphs.get(name);
        }

        Graph g = new Graph();
        g.setId(new Id());
        g.getId().setId("\"" + name + "\"");
        g.setType(com.alexmerz.graphviz.objects.Graph.DIRECTED);
        g.addGenericNodeAttribute("shape", "box");

        this.m_instanceGraphs.put(name, g);

        return g;
    }

    @Override
    public boolean export() throws ExportException {
        List<String> typeGraphs =
            this.m_typeGraphs.values().stream().map(g -> g.toString()).collect(Collectors.toList());
        try {
            int timer = Timer.start("Save DOT");
            Files.write(this.m_typeFile, typeGraphs);
            Timer.stop(timer);
        } catch (IOException e) {
            throw new ExportException(e);
        }

        if (this.m_instanceFile != null) {
            List<String> instanceGraphs =
                this.m_instanceGraphs.values()
                    .stream()
                    .map(g -> g.toString())
                    .collect(Collectors.toList());
            try {
                int timer = Timer.start("Save DOT");
                Files.write(this.m_instanceFile, instanceGraphs);
                Timer.stop(timer);
            } catch (IOException e) {
                throw new ExportException(e);
            }
        }
        return true;
    }

}
