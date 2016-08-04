/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.io.conceptual.lang.ecore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import groove.grammar.QualName;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.ExportException;

@SuppressWarnings("javadoc")
public class EcoreResource extends groove.io.conceptual.lang.ExportableResource {
    private ResourceSet m_resourceSet;
    private Map<QualName,Resource> m_typeResources = new HashMap<>();
    private Map<QualName,Resource> m_instanceResources = new HashMap<>();

    private File m_typeFile;
    private File m_instanceFile;

    private String relPath;

    //files allowed null if instance or type not required
    public EcoreResource(File typeTarget, File instanceTarget) {
        this.m_resourceSet = new ResourceSetImpl();
        this.m_resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put("*", new XMIResourceFactoryImpl());

        this.m_typeFile = typeTarget;
        this.m_instanceFile = instanceTarget;

        if (this.m_typeFile == this.m_instanceFile || this.m_typeFile == null
            || this.m_instanceFile == null) {
            this.relPath = "";
        } else {
            this.relPath =
                groove.io.Util.getRelativePath(new File(this.m_instanceFile.getAbsoluteFile()
                    .getParent()), this.m_typeFile.getAbsoluteFile())
                    .toString();
        }
    }

    public ResourceSet getResourceSet() {
        return this.m_resourceSet;
    }

    public String getTypePath() {
        return this.relPath;
    }

    public Resource getTypeResource(QualName resourceName) {
        String flatName = this.m_typeFile == null ? resourceName.toFile()
            .toString() : this.m_typeFile.toString();
        Resource result = this.m_typeResources.get(resourceName);
        if (result == null) {
            result = this.m_resourceSet.createResource(URI.createURI(flatName));
            this.m_typeResources.put(resourceName, result);
        }
        return result;
    }

    public Resource getInstanceResource(QualName resourceName) {
        Resource result = this.m_instanceResources.get(resourceName);
        if (result == null) {
            result = this.m_resourceSet.createResource(URI.createURI(resourceName.toFile()
                .toString()));
            this.m_instanceResources.put(resourceName, result);
        }
        return result;
    }

    @Override
    public boolean export() throws ExportException {
        try {
            if (this.m_typeFile != null) {
                for (Entry<QualName,Resource> resourceEntry : this.m_typeResources.entrySet()) {
                    try (FileOutputStream out = new FileOutputStream(this.m_typeFile)) {
                        int timer = Timer.cont("Ecore save");
                        resourceEntry.getValue()
                            .save(out, null);
                        Timer.stop(timer);
                    }
                }
            }

            if (this.m_instanceFile != null) {
                for (Entry<QualName,Resource> resourceEntry : this.m_instanceResources.entrySet()) {
                    try (FileOutputStream out = new FileOutputStream(this.m_instanceFile)) {
                        Map<Object,Object> opts = new HashMap<>();
                        if (this.m_typeFile != null) {
                            // If a target type resource has been defined, use schemaLocation. Otherwise don't, because it will point to nothing
                            opts.put(XMLResource.OPTION_SCHEMA_LOCATION, true);
                        }

                        int timer = Timer.cont("Ecore save");
                        resourceEntry.getValue()
                            .save(out, opts);
                        Timer.stop(timer);
                    }
                }
            }
        } catch (IOException e) {
            // abort
            throw new ExportException(e);
        }
        return true;
    }
}
