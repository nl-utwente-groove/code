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
 * $Id$
 */
package groove.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Returns the correct grammar loader class based on the given URL.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class URLLoaderFactory {

    
    
    public static AspectualViewGps getLoader(URL url, boolean layouted) throws MalformedURLException {
        if( url.getProtocol().equals("file")) {
            return new FileGps(layouted);
        } else if( url.getProtocol().equals("jar")) {
            return new JarGps(layouted);
        } else {
            throw new MalformedURLException("Unrecognized protocol: " + url.getProtocol());
        }
    }
    
    /** returns the loader for the url, that - by default - does not load layouts */
    public static AspectualViewGps getLoader(URL url) throws MalformedURLException {
        return getLoader(url, false);
    }
    
    
    /** returns a loader given a string that should hold a wellformed url */
    public static AspectualViewGps getLoader(String url) throws MalformedURLException {
        return getLoader(new URL(url));
    }
    
}
