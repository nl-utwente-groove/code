/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction;

import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class MatSearchTree {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    List<List<Edge>> tree;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public MatSearchTree() {
        this.tree = new ArrayList<List<Edge>>();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public void addToSearchTree(List<Edge> list) {
        this.tree.add(list);
    }

    /** EDUARDO */
    public Set<List<Edge>> getSearchPaths() {
        Set<List<Edge>> result = new HashSet<List<Edge>>();
        int treeHeight = this.tree.size() - 1;
        if (treeHeight >= 0) {
            // Start by the root elements.
            List<Edge> rootElems = this.tree.get(0);
            for (Edge rootElem : rootElems) {
                List<Edge> newPath = new ArrayList<Edge>();
                newPath.add(rootElem);
                result.add(newPath);
            }
            // Now descend in the tree and add more elements.
            for (int i = 1; i <= treeHeight; i++) {
                List<Edge> currElems = this.tree.get(i);
                Set<List<Edge>> oldResult = result;
                result = new HashSet<List<Edge>>();
                for (List<Edge> r : oldResult) {
                    result.remove(r);
                    for (Edge currElem : currElems) {
                        List<Edge> newR = new ArrayList<Edge>(r);
                        newR.add(currElem);
                        result.add(newR);
                    }
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static void testMatSearchTree0() {
        MatSearchTree tree = new MatSearchTree();

        Node n0 = DefaultNode.createNode();
        Node n1 = DefaultNode.createNode();
        Node n2 = DefaultNode.createNode();
        Node n3 = DefaultNode.createNode();
        Label label = DefaultLabel.createFreshLabel();

        List<Edge> level0 = new ArrayList<Edge>(2);
        level0.add(DefaultEdge.createEdge(n0, label, n3));
        level0.add(DefaultEdge.createEdge(n0, label, n2));
        tree.addToSearchTree(level0);

        List<Edge> level1 = new ArrayList<Edge>(3);
        level1.add(DefaultEdge.createEdge(n3, label, n1));
        level1.add(DefaultEdge.createEdge(n3, label, n3));
        level1.add(DefaultEdge.createEdge(n3, label, n2));
        tree.addToSearchTree(level1);

        for (List<Edge> searchPath : tree.getSearchPaths()) {
            System.out.println(searchPath);
        }
    }

    /** Unit test. */
    public static void main(String args[]) {
        testMatSearchTree0();
    }

}
