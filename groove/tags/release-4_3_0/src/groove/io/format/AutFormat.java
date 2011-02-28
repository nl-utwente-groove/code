/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.io.format;

import groove.graph.Graph;

import java.io.IOException;

/**
 * @author Eduardo Zambon
 */
public final class AutFormat extends AbsFileFormat {

    private static final String DESCRIPTION = "CADP .aut files";
    private static final String EXTENSION = ".aut";
    private static final boolean ACCEPT_DIR = false;

    private static final AutFormat INSTANCE = new AutFormat();

    public static final AutFormat getInstance() {
        return INSTANCE;
    }

    private AutFormat() {
        super(DESCRIPTION, EXTENSION, ACCEPT_DIR);
    }

    @Override
    public Graph<?,?> load(String fileName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save(Graph<?,?> graph, String fileName) throws IOException {
        // TODO Auto-generated method stub

    }

}
