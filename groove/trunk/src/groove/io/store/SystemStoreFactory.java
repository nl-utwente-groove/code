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
package groove.io.store;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import groove.io.FileType;
import groove.util.Exceptions;
import groove.util.Unzipper;

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
    static public SystemStore newStore(File file, boolean create) throws IOException {
        SystemStore result;
        if (FileType.ZIP.hasExtension(file) || FileType.JAR.hasExtension(file)) {
            // the file is zipped
            if (create) {
                throw new IOException("Can't create zipped grammar " + file.toString());
            }
            result = newStoreFromTmp(file.getPath(), Unzipper.instance()
                .unzip(file));
        } else {
            result = new DefaultFileSystemStore(file, create);
        }
        return result;
    }

    /**
     * Creates an appropriate system store from a given URL. The resulting store
     * has not yet been loaded.
     * @param url the URL to create the store from; non-null
     * @return a store created from <code>url</code>; non-null
     * @throws IOException if a store cannot be created from <code>url</code>
     */
    static public SystemStore newStore(URL url) throws IOException {
        SystemStore result;
        try {
            result = newStore(new File(url.toURI()), false);
        } catch (IllegalArgumentException exc) {
            result = newStoreFromTmp(url.toString(), Unzipper.instance()
                .unzip(url));
        } catch (URISyntaxException exc) {
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    static private SystemStore newStoreFromTmp(String orig, Path path) throws IOException {
        File[] files = path.toFile()
            .listFiles();
        if (files.length != 1) {
            throw new IOException(
                String.format("Zip file %s should only contain production system", orig));
        }
        return new DefaultFileSystemStore(files[0], false);
    }
}
