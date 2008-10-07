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
 * $Id: NameLabel.java,v 1.8 2008-02-29 11:17:59 fladder Exp $
 */
package groove.trans;

import groove.graph.WrapperLabel;

/**
 * The name of a production rule.
 * The displayed version of the rule is between <tt>BEGIN_CHAR</tt> and
 * <tt>END_CHAR</tt>-characters.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-29 11:17:59 $
 */
public class NameLabel extends WrapperLabel<String> {
    /** The obligatory first character of a rule name. */
    public static final char BEGIN_CHAR = '<';
    /** The obligatory last character of a rule name. */
    public static final char END_CHAR = '>';    private final String name;        /**     * Constructs a new RuleName on the basis of a given String.     * @param name the name of the production rule, as a String     * @require text != null     * @ensure name().equals(name)     */    public NameLabel(String name, boolean useBrackets) {        super( useBrackets? ""+BEGIN_CHAR+name+END_CHAR : name  );        this.name = name;    }
    /**     * Constructs a new RuleName on the basis of a given String.     * @param name the name of the production rule, as a String     */    public NameLabel(String name) {        this(name, false);    }
    /**
     * Returns the text of the rule name without the enclosing characters.
     */
    public String name() {
        return name;
    }
    
    @Override
    protected String convertToText(String name) {        return name;
    }
}
