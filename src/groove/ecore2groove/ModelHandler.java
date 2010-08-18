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
package groove.ecore2groove;

import groove.util.Groove;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarFile;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Class that deals with loading Ecore models and instance models, and 
 * creating and storing instance models of the loaded Ecore meta model.
 * @author Stefan Teijgeler
 * @version
 */
public class ModelHandler {

    private Resource r = null;
    private Resource ir = null;
    private ResourceSet rs = null;
    private EPackage metaModelRoot = null;
    private boolean core = false;

    private Vector<EClass> classes = new Vector<EClass>();
    private Vector<EEnum> enums = new Vector<EEnum>();
    private Vector<EEnumLiteral> literals = new Vector<EEnumLiteral>();
    private Vector<EReference> references = new Vector<EReference>();
    private Vector<EDataType> datatypes = new Vector<EDataType>();
    private Vector<EAttribute> attributes = new Vector<EAttribute>();

    private boolean instanceLoaded = false;
    private Vector<EObject> iClasses = new Vector<EObject>();

    private String eClassType = "type:EClass";
    private String eReferenceType = "type:EReference";

    /**
     * Constructor for a new ModelHandler. It loads an ecore model and
     * iterates over all elements to add them to their respective vectors.
     * @param modelLoc A String that must contain the location of an 
     * ecore metamodel file.
     */
    public ModelHandler(String modelLoc) {

        // if modelLoc is -core, replace it with location of Ecore.ecore
        // and set core to true
        if (modelLoc.equals("-core")) {
            this.core = true;
            modelLoc = Groove.getResource("Ecore.ecore").getFile();
        }

        // Create new ResourceSet and register an XMI model loader
        this.rs = new ResourceSetImpl();
        this.rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            "*", new XMIResourceFactoryImpl());

        // Load Ecore model, location refers to inside of .jar, then extract 
        // file from it. Especially with Ecore.ecore inside GROOVE resources
        try {
            if (modelLoc.contains(".jar!")) {
                String substr =
                    modelLoc.substring(5, modelLoc.lastIndexOf(".jar!") + 4);
                JarFile jarFile = new JarFile(substr);
                InputStream in =
                    jarFile.getInputStream(jarFile.getEntry("Ecore.ecore"));
                this.r = this.rs.createResource(URI.createURI(substr));
                this.r.load(in, null);
            } else {
                this.r = this.rs.createResource(URI.createURI(modelLoc));
                this.r.load(new FileInputStream(modelLoc), null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Iterate over all elements to register packages and find root package
        for (Iterator<EObject> it = this.r.getAllContents(); it.hasNext();) {
            EObject obj = it.next();
            if (obj.eClass().getName().equals("EPackage")) {
                EPackage modelPackage = (EPackage) obj;
                modelPackage = (EPackage) obj;
                String nsURI = modelPackage.getNsURI();
                if (nsURI == null) {
                    nsURI = modelPackage.getName();
                    modelPackage.setNsURI(nsURI);
                }

                // register all packages we find in the ResourceSet
                this.rs.getPackageRegistry().put(modelPackage.getNsURI(),
                    modelPackage);
                //if (metaModelRoot.getName().equals(findEPackage)) {
                if (modelPackage.getESuperPackage() == null) {
                    this.metaModelRoot = modelPackage;
                }
            }
        }

        if (this.metaModelRoot == null) {
            System.out.println("No root EPackage found");
            System.exit(1);
        }

        // Add EObjects to Collections
        for (Iterator<EObject> it = this.metaModelRoot.eAllContents(); it.hasNext();) {
            EObject obj = it.next();
            if (obj.eClass().getName().equals("EClass")) {
                this.classes.add((EClass) obj);
            } else if (obj.eClass().getName().equals("EEnum")) {
                this.enums.add((EEnum) obj);
            } else if (obj.eClass().getName().equals("EEnumLiteral")) {
                this.literals.add((EEnumLiteral) obj);
            } else if (obj.eClass().getName().equals("EReference")) {
                this.references.add((EReference) obj);
            } else if (obj.eClass().getName().equals("EAttribute")) {
                this.attributes.add((EAttribute) obj);
                if (((EAttribute) obj).getEAttributeType().eClass().getName() == "EDataType") {
                    this.datatypes.add(((EAttribute) obj).getEAttributeType());
                }
            }
        }

        // Vector with classes and enums used to check safe names
        Vector<EClassifier> elements = new Vector<EClassifier>();
        elements.addAll(this.classes);
        elements.addAll(this.enums);

        // Check if EClass type is safe to use,
        // or keep suffixing underscores until it is
        boolean safeName = true;
        do {
            safeName = true;
            for (EClassifier element : elements) {
                if (GraphLabels.getLabel(element).equals(this.eClassType)) {
                    safeName = false;
                    this.eClassType += "_";
                    break;
                }
            }
        } while (!safeName);

        // Check if EReference type is safe to use,
        // or keep suffixing underscores until it is
        do {
            safeName = true;
            for (EClassifier element : elements) {
                if (GraphLabels.getLabel(element).equals(this.eReferenceType)) {
                    safeName = false;
                    this.eReferenceType += "_";
                    break;
                }
            }
        } while (!safeName);

    }

    /**
     * Get the String to safely represent the EClass type node in the ecore
     * type graph.
     * @return the string to represent the EClass type node
     */
    public String getEClassType() {
        return this.eClassType;
    }

    /**
     * Get the String to safely represent the EReference type node
     * in the ecore type graph
     * @return the string to represent the EReference type node
     */
    public String getEReferenceType() {
        return this.eReferenceType;
    }

    /**
     * @return the model name of the loaded Ecore metamodel
     */
    public String getModelName() {
        return this.metaModelRoot.getName();
    }

    /**
     * @return a Vector with all EClasses of the loaded Ecore metamodel
     */
    public Vector<EClass> getEClasses() {

        return this.classes;
    }

    /**
     * @return Vector with all EEnums of the loaded Ecore metamodel
     */
    public Vector<EEnum> getEEnums() {

        return this.enums;
    }

    /**
     * @return a Vector with all EEnumLiterals of the loaded Ecore metamodel
     */
    public Vector<EEnumLiteral> getEEnumLiterals() {

        return this.literals;
    }

    /**
     * @return a Vector with all EReferences of the loaded Ecore metamodel
     */
    public Vector<EReference> getEReferences() {

        return this.references;
    }

    /**
     * @return a Vector with all EAttributes of the loaded Ecore metamodel
     */
    public Vector<EAttribute> getEAttributes() {

        return this.attributes;
    }

    /**
     * @return a Vector with all EDataTypes of the loaded Ecore metamodel
     */
    public Vector<EDataType> getEDataTypes() {

        return this.datatypes;
    }

    /**
     * Returns whether or not the loaded Ecore model is actually the Ecore core model.
     * @return true if the Ecore core model is loaded as Ecore model
     */
    public boolean isCore() {
        return this.core;
    }

    /**
     * @return a Vector with all EClass instances of the loaded instance model
     */
    public Vector<EObject> getiClasses() {
        if (this.instanceLoaded) {
            return this.iClasses;
        }
        return null;
    }

    /**
     * Load a model that must be an instance of the loaded Ecore meta model
     * into the ResourceSet. Populates the set of EClass instances.
     * @param instanceLoc the location on disk of the instance model to load.
     */
    public void loadInstance(String instanceLoc) {
        // Create a location URI for the instanceLoc String
        URI modelURI = URI.createFileURI(instanceLoc);

        // if the Ecore model is the core model, we must load an instance into
        // a new ResourceSet, because it cannot be loaded as an instance of
        // Ecore.ecore
        if (this.core) {
            this.rs = new ResourceSetImpl();
            this.rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new XMIResourceFactoryImpl());

            this.ir = this.rs.getResource(modelURI, true);

            this.iClasses.clear();
            for (Iterator<EObject> it = this.ir.getAllContents(); it.hasNext();) {
                EObject obj = it.next();
                if (obj.eClass().eClass().getName().equals("EClass")) {
                    this.iClasses.add(obj);
                }
            }
        } else {

            this.ir = this.rs.getResource(modelURI, true);

            this.iClasses.clear();
            for (Iterator<EObject> it = this.ir.getAllContents(); it.hasNext();) {
                EObject obj = it.next();
                if (obj.eClass().eClass().getName().equals("EClass")) {
                    this.iClasses.add(obj);
                }
            }
        }

        this.instanceLoaded = true;

        /*} catch (Exception e) {
        	Resource new_r = null;
        	ResourceSet new_rs = null;
        	
        	new_rs = new ResourceSetImpl();		
        	new_rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
        			"ecore", new XMIResourceFactoryImpl()); 
        	
        	new_r = new_rs.getResource(modelURI, true);
        	
        	System.out.println(r);
        	
        	iClasses.clear();
        	for(Iterator<EObject> it = new_r.getAllContents() ; it.hasNext() ; ) {
            	EObject obj = it.next();	    	
            	if ( obj.eClass().eClass().getName().equals("EClass")){
        	    	iClasses.add(obj);
              	}
        	}
        	instanceLoaded = true;
        	
        	for(Iterator<EObject> it = new_r.getAllContents() ; it.hasNext() ; ) {
            	EObject obj = it.next();	
            	System.out.println(obj);
            }
        }*/
    }

    /**
     * Store an instance model to disk
     * @param r the instance model.
     * @param filePath the location where to store the model.
     */
    public void saveModel(Resource r, String filePath) {
        try {
            r.save(new FileOutputStream(filePath), Collections.EMPTY_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new model that is an instance of the loaded Ecore meta model 
     * and adds it to the ResourceSet.
     * @param name the name of the new model.
     * @return a blank model.
     */
    public Resource createModel(String name) {

        Resource resultModel = this.rs.createResource(URI.createURI(name));

        return resultModel;
    }
}
