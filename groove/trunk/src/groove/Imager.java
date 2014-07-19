// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove;

import groove.gui.dialog.BugReportDialog;

/**
 * Wrapper class for the imager utility.
 * @see groove.io.Imager
 * @author Arend Rensink
 * @version $Revision$
 */
public class Imager {
    /**
     * Main method.
     * @param args list of command-line arguments
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                new BugReportDialog(e);
                System.exit(1);
            }
        });

        groove.io.Imager.main(args);
    }
}
