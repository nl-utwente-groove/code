// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
 * $Id$
 */
package nl.utwente.groove.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * The GROOVE package name space, and access to the resources residing in it.
 * @version $Revision$
 * @author Arend Rensink
 */
public class Resources {
    /** Groove package name token. */
    public static final String GROOVE_PACKAGE_TOKEN = "groove";
    /** Resource package name token. */
    public static final String RESOURCE_PACKAGE_TOKEN = "resource";

    /** Institution domain name space as string. */
    public static final String UT_NAMESPACE = "nl.utwente";
    /** Base package name for the GROOVE tool set as string. */
    public static final String GROOVE_BASE = UT_NAMESPACE + "." + GROOVE_PACKAGE_TOKEN;
    /** Institution domain name space as qualified name. */
    public static final QualName UT_PACKAGE = QualName.parse(UT_NAMESPACE);
    /** Qualified base package name for the GROOVE tool set. */
    public static final QualName GROOVE_PACKAGE = QualName.parse(GROOVE_BASE);
    /** Qualified name of the resource package. */
    public static final QualName RESOURCE_PACKAGE = GROOVE_PACKAGE.extend(RESOURCE_PACKAGE_TOKEN);

    /** Returns the URL for a given resource, given as an absolute qualified name.
     */
    static public URL getResource(QualName name) {
        return ClassLoader.getSystemResource(name.toString('/'));
    }

    /** Returns an input stream reader to a given resource, given as an absolute qualified name.
     */
    static public InputStreamReader getResourceStream(QualName name) throws IOException {
        return new InputStreamReader(getResource(name).openStream());
    }

    private Resources() {
        // private constructor to prevent instantiation of this class
    }
}
