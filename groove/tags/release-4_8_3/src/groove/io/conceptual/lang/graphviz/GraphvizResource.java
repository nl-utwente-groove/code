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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Id;

public class GraphvizResource extends ExportableResource {
    private Map<String,Graph> m_typeGraphs = new HashMap<String,Graph>();
    private Map<String,Graph> m_instanceGraphs = new HashMap<String,Graph>();

    private File m_typeFile;
    private File m_instanceFile;

    public GraphvizResource(File typeTarget, File instanceTarget) {
        m_typeFile = typeTarget;
        m_instanceFile = instanceTarget;
    }

    public Graph getTypeGraph(String name) {
        if (m_typeGraphs.containsKey(name)) {
            return m_typeGraphs.get(name);
        }

        Graph g = new Graph();
        g.setId(new Id());
        g.getId().setId("\"" + name + "\"");
        g.setType(com.alexmerz.graphviz.objects.Graph.DIRECTED);
        g.addGenericNodeAttribute("shape", "box");

        m_typeGraphs.put(name, g);

        return g;
    }

    public Graph getInstanceGraph(String name) {
        if (m_instanceGraphs.containsKey(name)) {
            return m_instanceGraphs.get(name);
        }

        Graph g = new Graph();
        g.setId(new Id());
        g.getId().setId("\"" + name + "\"");
        g.setType(com.alexmerz.graphviz.objects.Graph.DIRECTED);
        g.addGenericNodeAttribute("shape", "box");

        m_instanceGraphs.put(name, g);

        return g;
    }

    @Override
    public boolean export() throws ExportException {
        for (Entry<String,Graph> entry : m_typeGraphs.entrySet()) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(m_typeFile));
                int timer = Timer.start("Save DOT");
                out.write(entry.getValue().toString());
                Timer.stop(timer);
                out.close();
            } catch (IOException e) {
                throw new ExportException(e);
            }
        }

        if (m_instanceFile != null) {
            for (Entry<String,Graph> entry : m_instanceGraphs.entrySet()) {
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(m_instanceFile));
                    int timer = Timer.start("Save DOT");
                    out.write(entry.getValue().toString());
                    Timer.stop(timer);
                    out.close();
                } catch (IOException e) {
                    throw new ExportException(e);
                }
            }
        }
        return true;
    }

}
