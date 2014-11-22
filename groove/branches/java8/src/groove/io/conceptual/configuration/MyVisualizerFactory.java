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
package groove.io.conceptual.configuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.jaxfront.core.type.SimpleType;
import com.jaxfront.core.type.Type;
import com.jaxfront.core.ui.Visualizer;
import com.jaxfront.swing.ui.visualizers.JavaSwingFactory;

/**
 * A visualiser factory that creates a file dialog for the format location.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MyVisualizerFactory extends JavaSwingFactory {
    /**
     * Constructs the singleton instance.
     */
    private MyVisualizerFactory() {
        // empty
    }

    @Override
    public Visualizer createView(SimpleType arg0, Type arg1, Visualizer arg2) {
        if (arg0.getName().equals("sourceLocation")) {
            return createMyFileDialog(arg0);
        } else {
            return super.createView(arg0, arg1, arg2);
        }
    }

    private Visualizer createMyFileDialog(SimpleType arg0) {
        return new MyFileDialog(arg0);
    }

    /** Returns the singleton instance of this class. */
    public static MyVisualizerFactory instance() {
        if (INSTANCE == null) {
            try (PrintStream tmpOut = new PrintStream(File.createTempFile("tmp", null))) {
                PrintStream out = System.out;
                System.setOut(tmpOut);
                INSTANCE = new MyVisualizerFactory();
                System.setOut(out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return INSTANCE;
    }

    private static MyVisualizerFactory INSTANCE;
}
