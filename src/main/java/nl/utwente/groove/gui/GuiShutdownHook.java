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
package nl.utwente.groove.gui;

import java.awt.Window;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.cli.GrooveCmdLineTool;

/**
 * Shutdown hook for GUI-based command-line tools, delaying JVM exit
 * until all AWT windows have been closed.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class GuiShutdownHook implements Runnable {
    @Override
    public void run() {
        boolean exit;
        do {
            exit = true;
            for (Window win : Window.getWindows()) {
                if (win.isShowing()) {
                    exit = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                    break;
                }
            }
        } while (!exit);
    }

    /** Registers this hook with {@link GrooveCmdLineTool}; to be called
     * by GUI tools before starting their command-line execution. */
    public static void register() {
        GrooveCmdLineTool.setShutdownHook(new GuiShutdownHook());
    }
}
