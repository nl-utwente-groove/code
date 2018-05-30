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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Id;

import groove.grammar.QualName;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;

@SuppressWarnings("javadoc")
public class GraphvizResource extends ExportableResource {
    private Map<QualName,Graph> m_typeGraphs = new HashMap<>();
    private Map<QualName,Graph> m_instanceGraphs = new HashMap<>();

    private File m_typeFile;
    private File m_instanceFile;

    public GraphvizResource(File typeTarget, File instanceTarget) {
        this.m_typeFile = typeTarget;
        this.m_instanceFile = instanceTarget;
    }

    public Graph getTypeGraph(QualName name) {
        if (this.m_typeGraphs.containsKey(name)) {
            return this.m_typeGraphs.get(name);
        }

        Graph g = new Graph();
        g.setId(new Id());
        g.getId()
            .setId("\"" + name + "\"");
        g.setType(com.alexmerz.graphviz.objects.Graph.DIRECTED);
        g.addGenericNodeAttribute("shape", "box");

        this.m_typeGraphs.put(name, g);

        return g;
    }

    public Graph getInstanceGraph(QualName name) {
        if (this.m_instanceGraphs.containsKey(name)) {
            return this.m_instanceGraphs.get(name);
        }

        Graph g = new Graph();
        g.setId(new Id());
        g.getId()
            .setId("\"" + name + "\"");
        g.setType(com.alexmerz.graphviz.objects.Graph.DIRECTED);
        g.addGenericNodeAttribute("shape", "box");

        this.m_instanceGraphs.put(name, g);

        return g;
    }

    @Override
    public boolean export() throws ExportException {
        for (Graph typeGraph : this.m_typeGraphs.values()) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(this.m_typeFile))) {
                int timer = Timer.start("Save DOT");
                out.write(typeGraph.toString());
                Timer.stop(timer);
            } catch (IOException e) {
                throw new ExportException(e);
            }
        }

        if (this.m_instanceFile != null) {
            for (Graph instanceGraph : this.m_instanceGraphs.values()) {
                try (BufferedWriter out = new BufferedWriter(new FileWriter(this.m_instanceFile))) {
                    int timer = Timer.start("Save DOT");
                    out.write(instanceGraph.toString());
                    Timer.stop(timer);
                } catch (IOException e) {
                    throw new ExportException(e);
                }
            }
        }
        return true;
    }

}
