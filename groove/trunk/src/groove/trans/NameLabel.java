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
 * $Id: NameLabel.java,v 1.1.1.2 2007-03-20 10:42:56 kastenberg Exp $
 */
package groove.trans;

import groove.graph.GraphFormatException;
import groove.graph.Label;
import groove.graph.WrapperLabel;

/**
 * The name of a production rule.
 * The displayed version of the rule is between <tt>BEGIN_CHAR</tt> and
 * <tt>END_CHAR</tt>-characters.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:56 $
 */
public class NameLabel extends WrapperLabel<String> {
    /** The obligatory first character of a rule name. */
    public static final char BEGIN_CHAR = '<';
    /** The obligatory last character of a rule name. */
    public static final char END_CHAR = '>';

    /**
     * Constructs a new RuleName on the basis of a given String.
     * @param name the name of the production rule, as a String
     * @require text != null
     * @ensure name().equals(name)
     */
    public NameLabel(String name) {
        super(name);
    }

    @Deprecated
    public Label parse(String text) throws GraphFormatException {
        if ( text.charAt(0) == BEGIN_CHAR
             && text.charAt(text.length()-1) == END_CHAR)
            return new NameLabel(text.substring(1,text.length()-1));
        else
            throw new GraphFormatException
                ("Rule names must be between '"+BEGIN_CHAR+"' and '"+END_CHAR+"'");
    }

    /**
     * Returns the text of the rule name without the enclosing characters.
     * @return the text of the rule name without the enclosing characters
     * @ensure <tt>text().equals(""+BEGIN_CHAR+return+END_CHAR)</tt>
     */
    public String name() {
        return getContent();
    }
    
    protected String convertToText(String name) {
        return ""+BEGIN_CHAR+name+END_CHAR;
    }
}
