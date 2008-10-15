/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: GrooveModules.java,v 1.1 2008-03-04 12:42:33 kastenberg Exp $
 */
package groove.util;

/**
 * This class provides constants that can be used when loading modules. The
 * constants can be used as identifiers for system properties. Those modules can
 * be enabled or disables by setting the value of the system property properly.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class GrooveModules {

    /**
     * Value for system properties of enabled modules.
     */
    public static String GROOVE_MODULE_ENABLED = "enabled";
    /**
     * Value for system properties of disabled modules.
     */
    public static String GROOVE_MODULE_DISABLED = "disabled";

    /**
     * Id of the system property for ltl verification
     */
    public static String GROOVE_MODULE_LTL_VERIFICATION =
        "groove.modules.verification.ltl";
}
