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
 * $Id: RuleNameLabel.java,v 1.1 2007-04-30 19:53:27 rensink Exp $
 *
 * Angela Lozano's thesis. EMOOSE student 2002 - 2003
 * EMOOSE (European Master in Object-Oriented & Software Engineering technologies)
 * Vrije Universiteit Brussel - Ecole des Mines de Nantes - Universiteit Twente 
 */
package groove.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Representation of a structured rule name.
 * A structured rule name is a rule name consisting of a nonempty sequence of
 * tokens, seperated by <tt>SEPARATOR</tt> characters. Each individual token
 * may be empty. The prefix without the last token is called the parent (which
 * is <tt>null</tt> if there is only a single token); the last token is called
 * the child.
 *
 * @author Angela Lozano and Arend Rensink
 * @version $Revision: 1.1 $ $Date: 2007-04-30 19:53:27 $
 */
public class RuleNameLabel extends NameLabel {
    /**
     * Character to separate constituent tokens.
     */
    static public final char SEPARATOR = '.';
    
    /**
     * Creates a new structured rule name, on the basis of a given <tt>String</tt>.
     * <tt>SEPARATOR</tt> characters appearing in the proposed name will be
     * interpreted as token separators.
     * @param name the text of the new structured rule name (without enclosing
     *             characters)
     * @require <tt>name != null</tt>
     */
    public RuleNameLabel(String name) {
        super(name);
    }

    /**
     * Creates a new structured rule name, on the basis of a given parent name
     * and child <tt>String</tt>.
     * If the parent name is <tt>null</tt>, the resulting structured rule name
     * consists of a single token (just the child).
     * @param parent the parent structured rule name
     * @param child the child rule name (without enclosing characters)
     * @require <tt>child != null</tt>
     */
    public RuleNameLabel(RuleNameLabel parent, String child) {
        this(parent == null ? child : parent.name()+SEPARATOR+child);
    }

    /**
     * Returns the token in this rule name at a specific instance
     * @param i the index at which the token is requested
     * @return the token at index <tt>i</tt>
     * @require <tt>0 <= i && i < size()</tt>
     * @ensure </tt>return == tokens[i]</tt>
     */
    public String get(int i) {
        return tokens()[i];
    }

    /**
     * Indicates whether this structured rule name has a (nun-<tt>null</tt>) parent.
     * @return <tt>true</tt> if this rule name has a (non-<tt>null</tt>) parent
     * @ensure <tt>return == size()>1</tt>
     */
    public boolean hasParent() {
        return name().indexOf(SEPARATOR) >= 0;
    }

    /**
     * Returns the number of tokens in the rule name.
     * @return number of tokens in this rule name
     * @ensure <tt>return > 0</tt>
     */
    public int size(){
        return tokens().length;
    }

    /**
     * Returns the last token of this rule name. 
     * @return the last token of the rule name
     * @ensure <tt>return == get(size()-1)</tt>
     */
    public String child() {
        String name = name();
        return name.substring(name.lastIndexOf(SEPARATOR)+1);
    }
    
    /**
     * Returns the parent rule name (all tokens except the last), or <tt>null</tt>
     * if there is no parent rule name. There is no parent rule name iff the
     * rule name consists of a single token only.
     * @return the parent rule name
     * @ensure <tt>return == null</tt> iff <tt>size() == 1</tt>
     */
    public RuleNameLabel parent() {
        String name = name();
        int dot = name.lastIndexOf(SEPARATOR);
        if (dot < 0)
            return null;
        else
            return new RuleNameLabel(name.substring(0,dot));
    }
 
    /**
     * Returns the tokens in this structured rule name as an array of strings.
     * @return the tokens in this structured rule name as an array of strings
     * @ensure return.length > 0
     */
    public String[] tokens() {
        List<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(name(),""+SEPARATOR);
        while (tokenizer.hasMoreTokens())
            result.add(tokenizer.nextToken());
        return result.toArray(new String[0]);
    }
}