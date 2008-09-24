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
 * $Id: CommandLineOption.java,v 1.2 2008-01-30 09:32:02 iovka Exp $
 */
 package groove.util;

/**
 * Interface for command-line options.
 * Classes implementing this interface must provide a name and a multi-line
 * description (used in help functionality) and they may specify that they need 
 * a parameter. If a command-line option with the given name occurs, 
 * the corresponding option instance will be given the opportunity to exert
 * its effect, through a call of the <tt>{@link #parse}</tt> method.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface CommandLineOption {
    /** Returns the (one-word, often one-letter) name of this option. */
    public String getName();
    /** Returns a multi-line description of this option. */
    public String[] getDescription();
    /** Returns a symbolic name for the option parameter, if the option has any. */
    public String getParameterName();
    /** Specifies whether this option requires an additional parameter. */
    public boolean hasParameter();
    /** 
     * Effects this option, with a given parameter (null if the option does not require one).
     * @throws IllegalArgumentException if <tt>parameter</tt> is not valid for
     * the option.
     */
    public void parse(String parameter) throws IllegalArgumentException;
}

