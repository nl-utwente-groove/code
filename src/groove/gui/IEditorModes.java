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
 * $Id: IEditorModes.java,v 1.3 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

/**
 * @author Carel van Leeuwen
 * @since june 2005
 */
public interface IEditorModes {
    /**
     * Callback method to determine whether an event concerns edge creation. To
     * be overridden by subclasses.
     * 
     * @return <tt>true</tt> if edge creation mode is available and enabled
     */
    public boolean isEdgeMode();

    /**
     * Callback method to determine whether an event concerns node creation. To
     * be overridden by subclasses.
     * 
     * @return <tt>true</tt> if node creation mode is available and enabled
     */
    public boolean isNodeMode();
}
