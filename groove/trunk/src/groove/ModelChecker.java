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

/**
 * Wrapper class for the model checker.
 * @see groove.verify.CTLModelChecker
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-03-20 10:42:39 $
 */
public class ModelChecker {
    /**
     * Main method.
     * @param args list of command-line arguments
     */
    public static void main(String[] args) {
        groove.verify.CTLModelChecker.main(args);
    }
}
