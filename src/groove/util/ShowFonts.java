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
package groove.util;

import groove.io.Util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to check which fonts support which characters 
 * @author rensink
 * @version $Revision $
 */
public class ShowFonts {
    /** Call with hex codes of interesting characters. */
    public static void main(String[] args) {
        List<Integer> chars;
        if (args.length == 0) {
            chars = Collections.singletonList((int) Util.DT);
        } else {
            chars = new ArrayList<>(args.length);
            for (String arg : args) {
                chars.add(Integer.parseInt(arg, 16));
            }
        }
        Font[] fonts =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Integer c : chars) {
            System.out.println("Fonts that can display 0x"
                + Integer.toHexString(c));
            for (Font f : fonts) {
                if (f.canDisplay(c)) {
                    System.out.println(f.getFontName());
                }
            }
        }
    } // end of main
}