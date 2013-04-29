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
package groove.io.external.format;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;
import groove.gui.Simulator;
import groove.io.FileType;
import groove.io.external.Format;
import groove.io.external.FormatImporter;
import groove.io.external.PortException;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** 
 * Class that implements loading of graphs in the DIMACS .col graph format.
 * Saving in this format is unsupported.
 * 
 * The format is described in
 * <a href="http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps">
 * http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps</a>.
 * See <a href="http://mat.gsia.cmu.edu/COLOR/instances.html">
 * http://mat.gsia.cmu.edu/COLOR/instances.html</a>
 * for example graphs in this format.
 * 
 * @author Arend Rensink 
 */
public class ColImporter implements FormatImporter {
    private ColImporter() {
        this.formats = Arrays.asList(new Format(this, FileType.COL));
    }

    @Override
    public Kind getFormatKind() {
        return Kind.RESOURCE;
    }

    @Override
    public Collection<? extends Format> getSupportedFormats() {
        return this.formats;
    }

    // Methods from FileFormat.

    @Override
    public Set<Resource> doImport(File file, Format format, GrammarModel grammar)
        throws PortException {
        Set<Resource> resources;
        try {
            FileInputStream stream = new FileInputStream(file);
            resources =
                this.doImport(format.stripExtension(file.getName()), stream,
                    format, grammar);
            stream.close();
        } catch (IOException e) {
            throw new PortException(e);
        }
        return resources;
    }

    @Override
    public Set<Resource> doImport(String name, InputStream stream,
            Format format, GrammarModel grammar) throws PortException {
        try {
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(stream));

            DefaultHostGraph graph = new DefaultHostGraph(name);
            Algebra<?> intAlgebra =
                AlgebraFamily.getInstance().getAlgebraFor("0");
            TypeLabel valueLabel = TypeLabel.createBinaryLabel("value");
            for (String nextLine = reader.readLine(); nextLine != null; nextLine =
                reader.readLine()) {
                String[] fragments = nextLine.split(" ");
                if (fragments[0].equals("n")) {
                    HostNode node = this.addNode(graph, fragments[1]);
                    ValueNode valueNode =
                        graph.addNode(intAlgebra,
                            intAlgebra.getValueFromSymbol(fragments[2]));
                    graph.addEdge(node, valueLabel, valueNode);
                } else if (fragments[0].equals("e")) {
                    HostNode source = this.addNode(graph, fragments[1]);
                    HostNode target = this.addNode(graph, fragments[2]);
                    graph.addEdge(source, LABEL, target);
                }
            }
            AspectGraph aGraph = GraphConverter.toAspect(graph);

            Resource res = new Resource(ResourceKind.HOST, name, aGraph);

            reader.close();

            return Collections.singleton(res);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }

    private HostNode addNode(HostGraph result, String id) {
        HostNode node = result.getFactory().createNode(Integer.parseInt(id));
        result.addEdge(node, TypeLabel.createLabel(EdgeRole.FLAG, "i" + id),
            node);
        return node;
    }

    /** Returns the parent component for a dialog. */
    protected Frame getParent() {
        return this.parent;
    }

    @Override
    public void setSimulator(Simulator simulator) {
        this.parent = simulator.getFrame();
    }

    private final List<Format> formats;
    private Frame parent;

    /** Returns the singleton instance of this class. */
    public static final ColImporter getInstance() {
        return instance;
    }

    private static final ColImporter instance = new ColImporter();

    // Methods from FileFormat.

    private static final TypeLabel LABEL = TypeLabel.createBinaryLabel("n");

}
