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
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import de.gupro.gxl.gxl_1_0.EdgemodeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;

public class GxlResource extends ExportableResource {
    // For exported resource
    private GxlType m_gxlTypeType;
    private GxlType m_gxlTypeInstance;
    // For temp resource (type for instance that does not export type as well)
    private GxlType m_gxlTypeTemp;
    private Map<String,GraphType> m_graphs = new HashMap<String,GraphType>();

    private File m_typeFile;
    private File m_instanceFile;

    private String relPath;

    public GxlResource(File typeTarget, File instanceTarget) {
        if (typeTarget != null) {
            m_typeFile = typeTarget;
        }
        if (instanceTarget != null) {
            if (instanceTarget.equals(typeTarget)) {
                m_instanceFile = m_typeFile;
            } else {
                m_instanceFile = instanceTarget;
            }
        }

        if (m_typeFile == m_instanceFile || m_typeFile == null) {
            relPath = "";
        } else {
            relPath = groove.io.Util.getRelativePath(new File(m_instanceFile.getAbsoluteFile().getParent()), m_typeFile.getAbsoluteFile()).toString();
        }

        m_gxlTypeType = new GxlType();
        m_gxlTypeTemp = new GxlType();
        if (m_typeFile == m_instanceFile) {
            m_gxlTypeInstance = m_gxlTypeType;
        } else {
            m_gxlTypeInstance = new GxlType();
        }
    }

    public GraphType getTypeGraph(String graphId) {
        if (m_graphs.containsKey(graphId)) {
            return m_graphs.get(graphId);
        }
        GraphType graph = new GraphType();
        graph.setId(graphId);
        graph.setEdgeids(true);
        GxlUtil.setElemType(graph, GxlUtil.g_gxlTypeGraphURI + "#gxl-1.0");

        if (m_typeFile != null) {
            m_gxlTypeType.getGraph().add(graph);
        } else {
            m_gxlTypeTemp.getGraph().add(graph);
        }

        m_graphs.put(graphId, graph);

        return graph;
    }

    public GraphType getInstanceGraph(String typeId, String name) {
        if (m_graphs.containsKey(name)) {
            return m_graphs.get(name);
        }
        GraphType graph = new GraphType();
        graph.setId(name);
        GxlUtil.setElemType(graph, getTypePath() + "#" + typeId);
        // No edge IDs in instance graphs
        graph.setEdgeids(false);
        graph.setEdgemode(EdgemodeType.DEFAULTDIRECTED);

        m_gxlTypeInstance.getGraph().add(graph);

        m_graphs.put(name, graph);

        return graph;
    }

    public String getTypePath() {
        return relPath;
    }

    @Override
    public boolean export() throws ExportException {
        return export(false);
    }

    public boolean export(boolean oldStyle) throws ExportException {
        // m_gxlType contains all graphs
        int timer = Timer.start("Save GXL");
        JAXBElement<GxlType> mainElement = GxlUtil.g_objectFactory.createGxl(m_gxlTypeType);
        JAXBElement<GxlType> instanceElement = null;
        if (m_instanceFile != null && m_gxlTypeInstance != m_gxlTypeType) {
            instanceElement = GxlUtil.g_objectFactory.createGxl(m_gxlTypeInstance);
        }

        OutputStream os;
        try {
            if (!oldStyle) {
                // Regular export
                if (m_typeFile != null) {
                    os = new FileOutputStream(m_typeFile);
                    GxlUtil.g_marshaller.marshal(mainElement, os);
                    os.close();
                }

                if (m_instanceFile != null && instanceElement != null) {
                    os = new FileOutputStream(m_instanceFile);
                    GxlUtil.g_marshaller.marshal(instanceElement, os);
                    os.close();
                }
            } else {
                // Insert doctype, move xmlns:xlink around and remove standalone
                // Really hacky, but the old gxlvalidator wont accept the document otherwise
                // I'm no XML expert ;)
                if (m_typeFile != null) {
                    os = new ByteArrayOutputStream();
                    GxlUtil.g_marshaller.marshal(mainElement, os);

                    String xmlString = ((ByteArrayOutputStream) os).toString("UTF-8");
                    xmlString = xmlString.replaceAll("standalone=\"yes\"", "").replaceAll("xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "").replaceAll("<gxl[^>]*>", "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n" +
                        "<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
                    BufferedWriter out = new BufferedWriter(new FileWriter(m_typeFile));
                    out.write(xmlString);
                    out.close();
                }

                if (m_instanceFile != null && instanceElement != null) {
                    os = new ByteArrayOutputStream();
                    GxlUtil.g_marshaller.marshal(instanceElement, os);

                    String xmlString = ((ByteArrayOutputStream) os).toString("UTF-8");
                    xmlString = xmlString.replaceAll("standalone=\"yes\"", "").replaceAll("xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "").replaceAll("<gxl[^>]*>", "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n" +
                        "<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
                    BufferedWriter out = new BufferedWriter(new FileWriter(m_instanceFile));
                    out.write(xmlString);
                    out.close();
                }
            }

        } catch (FileNotFoundException e) {
            throw new ExportException(e);
        } catch (JAXBException e) {
            throw new ExportException(e);
        } catch (UnsupportedEncodingException e) {
            throw new ExportException(e);
        } catch (IOException e) {
            throw new ExportException(e);
        }

        Timer.stop(timer);

        return true;
    }

}
