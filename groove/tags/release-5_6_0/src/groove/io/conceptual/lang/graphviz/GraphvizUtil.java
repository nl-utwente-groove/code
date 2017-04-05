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

import groove.grammar.QualName;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.StringType;

@SuppressWarnings("javadoc")
public class GraphvizUtil {
    private static final Id g_dotId = Id.getId(Id.ROOT, Name.getName("DOT"));

    private static TypeModel g_staticDOTModel = null;

    public static Class g_NodeClass = null;
    public static Field g_EdgeField = null;
    public static Field g_LabelField = null;
    public static Field g_AttrField = null;

    public static TypeModel getDOTModel() {
        if (g_staticDOTModel == null) {
            g_staticDOTModel = buildStaticModel();
        }
        return g_staticDOTModel;
    }

    private static TypeModel buildStaticModel() {
        TypeModel typeModel = new TypeModel(QualName.name("DOT"));
        g_NodeClass = typeModel.getClass(Id.getId(g_dotId, Name.getName("Node")), true);

        g_LabelField = new Field(Name.getName("label"), StringType.instance(), 0, 1);
        g_AttrField = new Field(Name.getName("attributes"),
            new Container(Kind.SET, StringType.instance()), 0, -1);
        g_NodeClass.addField(g_LabelField);
        g_NodeClass.addField(g_AttrField);

        //Edges may have attributes and labels, but not supported
        //Edges may be connected to ports, but not supported
        //Edges may be directed or undirected, but not supported
        g_EdgeField = new Field(Name.getName("edge"),
            new Container(Kind.SET, g_NodeClass.getProperClass()), 0, -1);
        g_NodeClass.addField(g_EdgeField);

        return typeModel;
    }
}
