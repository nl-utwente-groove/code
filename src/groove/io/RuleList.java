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
 * $Id: RuleList.java,v 1.2 2008-01-30 09:33:41 iovka Exp $
 */
package groove.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import groove.trans.Rule;
import groove.trans.RuleSystem;

/**
 * List of rules, obtained from a file containing rule names.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleList extends ArrayList<String> {
    /**
     * Constructs a list by reading names from a given file
     * and looking up the rules in a given rule system.
     * @param file the file from which the names are to be read
     * @throws IOException if an error occurs during file reading, or the file 
     * contains a line that is not recognised as a rule name
     */
    public RuleList(File file) throws IOException {
        fill(new BufferedReader(new FileReader(file)));
    }
    
    /**
     * Constructs a list by reading names from a given file
     * and looking up the rules in a given rule system.
     * @param file the file from which the names are to be read
     * @throws IOException if an error occurs during file reading, or the file 
     * contains a line that is not recognised as a rule name
     */
    public RuleList(InputStream file) throws IOException {
        fill(new BufferedReader(new InputStreamReader(file)));
    }
    
    /**
     * Turns the list of rules into a list of rules from a given rule system.
     * @throws IllegalArgumentException any of the rule names does not occur in the rule system 
     */
    public List<Rule> getRules(RuleSystem ruleSystem) {
        List<Rule> result = new ArrayList<Rule>(size());
        for (String name: this) {
            Rule nextRule = ruleSystem.getRule(name);
            if (nextRule == null) {
                throw new IllegalArgumentException(String.format("Rule with name %s does not exist", name));
            }
            result.add(nextRule);
        }
        return result;
    }
    
    /**
     * Fills this list with rules whose names are read from a given file
     * and looked up in a given rule system
     * @param file the file from which the names are to be read
     * @throws IOException if an error occurs during file reading, or the file 
     * contains a line that is not recognised as a rule name
     */
    protected void fill(BufferedReader file) throws IOException {
        String nextName = file.readLine();
        while (nextName != null) {
            if (nextName.length() != 0) {
                add(nextName.trim());
            }
            nextName = file.readLine();
        }
    }
}
