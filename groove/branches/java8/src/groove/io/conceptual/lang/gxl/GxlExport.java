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
package groove.io.conceptual.lang.gxl;

import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.Export;
import groove.io.external.PortException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.eclipse.jdt.annotation.Nullable;

import de.gupro.gxl.gxl_1_0.EdgemodeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;

/** Export for GXL format. */
public class GxlExport extends Export {
    // For exported resource
    private final GxlType m_gxlTypeType;
    private final GxlType m_gxlTypeInstance;
    // For temp resource (type for instance that does not export type as well)
    private final GxlType m_gxlTypeTemp;
    private final Map<String,GraphType> m_graphs = new HashMap<String,GraphType>();

    private final @Nullable Path m_typeFile;
    private final @Nullable Path m_instanceFile;
    /** Relative path of instance with respect to type file. */
    private final String relPath;

    /**
     * Constructs an export object for a given type and instance file.
     * Both files may new {@code null}.
     */
    public GxlExport(@Nullable Path typeFile, @Nullable Path instanceFile) {
        this.m_typeFile = typeFile;
        this.m_instanceFile = instanceFile;
        boolean typeInstanceEqual =
            typeFile == null ? instanceFile == null : typeFile.equals(instanceFile);
        this.relPath = typeInstanceEqual ? "" : instanceFile.relativize(typeFile).toString();
        this.m_gxlTypeType = new GxlType();
        this.m_gxlTypeTemp = new GxlType();
        this.m_gxlTypeInstance = typeInstanceEqual ? this.m_gxlTypeType : new GxlType();
    }

    /** Returns an (initially empty) type graph for a given name. */
    public GraphType getTypeGraph(String graphId) {
        GraphType result = this.m_graphs.get(graphId);
        if (result == null) {
            result = new GraphType();
            result.setId(graphId);
            result.setEdgeids(true);
            GxlUtil.setElemType(result, GxlUtil.g_gxlTypeGraphURI + "#gxl-1.0");

            if (this.m_typeFile != null) {
                this.m_gxlTypeType.getGraph().add(result);
            } else {
                this.m_gxlTypeTemp.getGraph().add(result);
            }

            this.m_graphs.put(graphId, result);
        }
        return result;
    }

    /** Returns a GXL instance graph with a given name and type name. */
    public GraphType getInstanceGraph(String typeId, String name) {
        GraphType result = this.m_graphs.get(name);
        if (result == null) {
            // a graph with this name does not yet exit; create and add it
            result = new GraphType();
            result.setId(name);
            GxlUtil.setElemType(result, getTypePath() + "#" + typeId);
            // No edge IDs in instance graphs
            result.setEdgeids(false);
            result.setEdgemode(EdgemodeType.DEFAULTDIRECTED);
            this.m_gxlTypeInstance.getGraph().add(result);
            this.m_graphs.put(name, result);
        }
        return result;
    }

    /** Returns the relative path from instance to type file, if both are set. */
    public String getTypePath() {
        return this.relPath;
    }

    @Override
    public boolean export() throws PortException {
        return export(false);
    }

    /** Helper method containing some leftover code to use old-style export. */
    private boolean export(boolean oldStyle) throws PortException {
        // m_gxlType contains all graphs
        int timer = Timer.start("Save GXL");
        JAXBElement<GxlType> mainElement = GxlUtil.g_objectFactory.createGxl(this.m_gxlTypeType);
        JAXBElement<GxlType> instanceElement = null;
        if (this.m_instanceFile != null && this.m_gxlTypeInstance != this.m_gxlTypeType) {
            instanceElement = GxlUtil.g_objectFactory.createGxl(this.m_gxlTypeInstance);
        }

        try {
            if (!oldStyle) {
                // Regular export
                if (this.m_typeFile != null) {
                    try (OutputStream os = Files.newOutputStream(this.m_typeFile)) {
                        GxlUtil.g_marshaller.marshal(mainElement, os);
                    }
                }
                if (this.m_instanceFile != null && instanceElement != null) {
                    try (OutputStream os = Files.newOutputStream(this.m_instanceFile)) {
                        GxlUtil.g_marshaller.marshal(instanceElement, os);
                    }
                }
            } else {
                if (this.m_typeFile != null) {
                    String xmlString = marshalAsString(mainElement);
                    xmlString = convertXmlString(xmlString);
                    Files.write(this.m_typeFile, Collections.singletonList(xmlString));
                }
                if (this.m_instanceFile != null && instanceElement != null) {
                    String xmlString = marshalAsString(instanceElement);
                    xmlString = convertXmlString(xmlString);
                    Files.write(this.m_typeFile, Collections.singletonList(xmlString));
                }
            }
        } catch (JAXBException | IOException e) {
            throw new PortException(e);
        }

        Timer.stop(timer);

        return true;
    }

    /**
     * Marshals a certain XML subtree into a string.
     */
    private String marshalAsString(JAXBElement<GxlType> mainElement) throws JAXBException,
        UnsupportedEncodingException, IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            GxlUtil.g_marshaller.marshal(mainElement, os);
            return os.toString("UTF-8");
        }
    }

    /**
     * Insert doctype, move xmlns:xlink around and remove standalone
     * Really hacky, but the old gxlvalidator wont accept the document otherwise
     * I'm no XML expert ;)
     */
    private String convertXmlString(String xmlString) {
        xmlString =
            xmlString.replaceAll("standalone=\"yes\"", "")
                .replaceAll("xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "")
                .replaceAll("<gxl[^>]*>",
                    "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n"
                        + "<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
        return xmlString;
    }

}
