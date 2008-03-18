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
/**
 * $Id: Tokenizer.java,v 1.3 2008-01-30 09:32:02 iovka Exp $
 */
package groove.util;

import java.util.StringTokenizer;

/**
 * Tokenizer that only returns tokens with an even number of quotes.
 * The method <tt>countTokens()</tt> is invalidated.
 * Can handle dots inside quotes, even delimiter is a dot.
 * @author Arend Rensink
 * @version $Revision: 1.3 $ $Date: 2008-01-30 09:32:02 $
 */
public class Tokenizer extends StringTokenizer {

    String delims;
    String str;

    public Tokenizer(String str, String delims) {
        super(str, delims);
	this.delims = delims;
	this.str = str;
    }

    public Tokenizer(String str, String delims, boolean returnDelims) {
        super(str, delims, returnDelims);
	this.delims = delims;
	this.str = str;
    }

    @Override
    public int countTokens() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String nextToken() {
        StringBuffer res = new StringBuffer();
	boolean quoted=false;

        // add tokens to nextLabel until the number of quotes is even
        int toIndex = 0;
        // the number of quotes in res[0..toIndex-1]
        int nrOfQuotes = 0;
        do {
	    // if label is quoted and delimeter is a dot, do special trick
	    if (this.delims.compareTo(".")==0 && this.str.charAt(toIndex) == '"'){
		quoted = true;
	    }
	    
            res.append(super.nextToken());
	    	    
            // count number of quotes in nextLabel
            while (toIndex < res.length()) {
                if (res.charAt(toIndex) == '"')
                        nrOfQuotes++;
                toIndex++;
            }
	    // add dot to token if not all quotes are found
	    if (quoted && nrOfQuotes % 2 != 0)
	    	res.append(".");
        } while (nrOfQuotes % 2 != 0);

        return res.toString();
    }
}
