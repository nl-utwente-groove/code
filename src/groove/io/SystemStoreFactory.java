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
 * $Id$
 */
package groove.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Factory to create a rule system store from URL or file.
 * @author Arend
 * @version $Revision $
 */
public final class SystemStoreFactory {
    /**
     * Creates an appropriate system store from a given file. The resulting
     * store has not yet been loaded. A flag indicates if the file should be
     * created if it does not yet exist.
     * @param file the file to create the store from; non-null
     * @param create if <code>true</code> and <code>file</code> does not yet
     *        exist, attempt to create it.
     * @return a store created from <code>file</code>; non-null
     * @throws IOException if a store cannot be created from <code>file</code>
     */
    static public SystemStore newStore(File file, boolean create)
        throws IOException {
        SystemStore store = null;
        try {
            store = new DefaultFileSystemStore(file, create);
        } catch (IllegalArgumentException exc) {
            try {
                store = new DefaultArchiveSystemStore(file);
            } catch (IllegalArgumentException exc1) {
                throw new IOException(exc1.getMessage());
            }
        }
        store.reload();
        return store;
    }

    /**
     * Creates an appropriate system store from a given URL. The resulting store
     * has not yet been loaded.
     * @param url the URL to create the store from; non-null
     * @return a store created from <code>url</code>; non-null
     * @throws IOException if a store cannot be created from <code>url</code>
     */
    static public SystemStore newStore(URL url) throws IOException {
        SystemStore store = null;
        try {
            store = new DefaultArchiveSystemStore(url);
        } catch (IllegalArgumentException exc) {
            try {
                store = new DefaultFileSystemStore(url);
            } catch (IllegalArgumentException exc1) {
                throw new IOException(exc1.getMessage());
            }
        }
        return store;
    }

    /**
     * Creates an appropriate system store from a location, given as a string.
     * The string is tried to be parsed as a URL; if that fails, it is parsed as
     * a file. The resulting store has not yet been loaded.
     * @param location the location (URL or file) to create the store from;
     *        non-null
     * @return a store created from <code>location</code>; non-null
     * @throws IOException if a store cannot be created from
     *         <code>location</code>
     */
    static public SystemStore newStore(String location) throws IOException {
        try {
            return newStore(new URL(location));
        } catch (IOException exc) {
            return newStore(new File(location), false);
        }
    }
}
