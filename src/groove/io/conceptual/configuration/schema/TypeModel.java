//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.07.20 at 10:40:55 AM CEST
//

package groove.io.conceptual.configuration.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for typeModel complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="typeModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fields">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="intermediates">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="when" type="{ConfigSchema}intermediateWhenType" default="container" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="containers">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ordering">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="type" type="{ConfigSchema}orderType" default="index" />
 *                                     &lt;attribute name="mode" type="{ConfigSchema}modeType" default="useIntermediate" />
 *                                     &lt;attribute name="usePrevEdge" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="useTypeName" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="defaults">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="useRule" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                           &lt;attribute name="setValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="opposites" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="properties">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="useAbstract" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="useContainment" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="useIdentity" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="useKeyset" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="useDefaultValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="useOpposite" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="constraints">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="checkUniqueness" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="checkIdentifier" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="checkKeyset" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="checkOpposite" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="checkOrdering" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="checkEnum" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="metaSchema" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="enumMode" type="{ConfigSchema}enumModeType" default="node" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeModel", propOrder = {"fields", "properties", "constraints"})
@SuppressWarnings("javadoc")
public class TypeModel {
    @XmlElement(required = true) protected TypeModel.Fields fields;
    @XmlElement(required = true) protected TypeModel.Properties properties;
    @XmlElement(required = true) protected TypeModel.Constraints constraints;
    @XmlAttribute(name = "metaSchema") protected Boolean metaSchema;
    @XmlAttribute(name = "enumMode") protected EnumModeType enumMode;

    /**
     * Gets the value of the fields property.
     *
     * @return
     *     possible object is
     *     {@link TypeModel.Fields }
     *
     */
    public TypeModel.Fields getFields() {
        return this.fields;
    }

    /**
     * Sets the value of the fields property.
     *
     * @param value
     *     allowed object is
     *     {@link TypeModel.Fields }
     *
     */
    public void setFields(TypeModel.Fields value) {
        this.fields = value;
    }

    /**
     * Gets the value of the properties property.
     *
     * @return
     *     possible object is
     *     {@link TypeModel.Properties }
     *
     */
    public TypeModel.Properties getProperties() {
        return this.properties;
    }

    /**
     * Sets the value of the properties property.
     *
     * @param value
     *     allowed object is
     *     {@link TypeModel.Properties }
     *
     */
    public void setProperties(TypeModel.Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the constraints property.
     *
     * @return
     *     possible object is
     *     {@link TypeModel.Constraints }
     *
     */
    public TypeModel.Constraints getConstraints() {
        return this.constraints;
    }

    /**
     * Sets the value of the constraints property.
     *
     * @param value
     *     allowed object is
     *     {@link TypeModel.Constraints }
     *
     */
    public void setConstraints(TypeModel.Constraints value) {
        this.constraints = value;
    }

    /**
     * Gets the value of the metaSchema property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isMetaSchema() {
        if (this.metaSchema == null) {
            return false;
        } else {
            return this.metaSchema;
        }
    }

    /**
     * Sets the value of the metaSchema property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setMetaSchema(Boolean value) {
        this.metaSchema = value;
    }

    /**
     * Gets the value of the enumMode property.
     *
     * @return
     *     possible object is
     *     {@link EnumModeType }
     *
     */
    public EnumModeType getEnumMode() {
        if (this.enumMode == null) {
            return EnumModeType.NODE;
        } else {
            return this.enumMode;
        }
    }

    /**
     * Sets the value of the enumMode property.
     *
     * @param value
     *     allowed object is
     *     {@link EnumModeType }
     *
     */
    public void setEnumMode(EnumModeType value) {
        this.enumMode = value;
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="checkUniqueness" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="checkIdentifier" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="checkKeyset" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="checkOpposite" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="checkOrdering" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="checkEnum" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Constraints {

        @XmlAttribute(name = "checkUniqueness") protected Boolean checkUniqueness;
        @XmlAttribute(name = "checkIdentifier") protected Boolean checkIdentifier;
        @XmlAttribute(name = "checkKeyset") protected Boolean checkKeyset;
        @XmlAttribute(name = "checkOpposite") protected Boolean checkOpposite;
        @XmlAttribute(name = "checkOrdering") protected Boolean checkOrdering;
        @XmlAttribute(name = "checkEnum") protected Boolean checkEnum;

        /**
         * Gets the value of the checkUniqueness property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isCheckUniqueness() {
            if (this.checkUniqueness == null) {
                return true;
            } else {
                return this.checkUniqueness;
            }
        }

        /**
         * Sets the value of the checkUniqueness property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setCheckUniqueness(Boolean value) {
            this.checkUniqueness = value;
        }

        /**
         * Gets the value of the checkIdentifier property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isCheckIdentifier() {
            if (this.checkIdentifier == null) {
                return true;
            } else {
                return this.checkIdentifier;
            }
        }

        /**
         * Sets the value of the checkIdentifier property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setCheckIdentifier(Boolean value) {
            this.checkIdentifier = value;
        }

        /**
         * Gets the value of the checkKeyset property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isCheckKeyset() {
            if (this.checkKeyset == null) {
                return true;
            } else {
                return this.checkKeyset;
            }
        }

        /**
         * Sets the value of the checkKeyset property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setCheckKeyset(Boolean value) {
            this.checkKeyset = value;
        }

        /**
         * Gets the value of the checkOpposite property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isCheckOpposite() {
            if (this.checkOpposite == null) {
                return true;
            } else {
                return this.checkOpposite;
            }
        }

        /**
         * Sets the value of the checkOpposite property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setCheckOpposite(Boolean value) {
            this.checkOpposite = value;
        }

        /**
         * Gets the value of the checkOrdering property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isCheckOrdering() {
            if (this.checkOrdering == null) {
                return true;
            } else {
                return this.checkOrdering;
            }
        }

        /**
         * Sets the value of the checkOrdering property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setCheckOrdering(Boolean value) {
            this.checkOrdering = value;
        }

        /**
         * Gets the value of the checkEnum property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isCheckEnum() {
            if (this.checkEnum == null) {
                return true;
            } else {
                return this.checkEnum;
            }
        }

        /**
         * Sets the value of the checkEnum property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setCheckEnum(Boolean value) {
            this.checkEnum = value;
        }

    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="intermediates">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="when" type="{ConfigSchema}intermediateWhenType" default="container" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="containers">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ordering">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="type" type="{ConfigSchema}orderType" default="index" />
     *                           &lt;attribute name="mode" type="{ConfigSchema}modeType" default="useIntermediate" />
     *                           &lt;attribute name="usePrevEdge" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="useTypeName" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="defaults">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="useRule" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *                 &lt;attribute name="setValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="opposites" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"intermediates", "containers", "defaults"})
    public static class Fields {

        @XmlElement(required = true) protected TypeModel.Fields.Intermediates intermediates;
        @XmlElement(required = true) protected TypeModel.Fields.Containers containers;
        @XmlElement(required = true) protected TypeModel.Fields.Defaults defaults;
        @XmlAttribute(name = "opposites") protected Boolean opposites;

        /**
         * Gets the value of the intermediates property.
         *
         * @return
         *     possible object is
         *     {@link TypeModel.Fields.Intermediates }
         *
         */
        public TypeModel.Fields.Intermediates getIntermediates() {
            return this.intermediates;
        }

        /**
         * Sets the value of the intermediates property.
         *
         * @param value
         *     allowed object is
         *     {@link TypeModel.Fields.Intermediates }
         *
         */
        public void setIntermediates(TypeModel.Fields.Intermediates value) {
            this.intermediates = value;
        }

        /**
         * Gets the value of the containers property.
         *
         * @return
         *     possible object is
         *     {@link TypeModel.Fields.Containers }
         *
         */
        public TypeModel.Fields.Containers getContainers() {
            return this.containers;
        }

        /**
         * Sets the value of the containers property.
         *
         * @param value
         *     allowed object is
         *     {@link TypeModel.Fields.Containers }
         *
         */
        public void setContainers(TypeModel.Fields.Containers value) {
            this.containers = value;
        }

        /**
         * Gets the value of the defaults property.
         *
         * @return
         *     possible object is
         *     {@link TypeModel.Fields.Defaults }
         *
         */
        public TypeModel.Fields.Defaults getDefaults() {
            return this.defaults;
        }

        /**
         * Sets the value of the defaults property.
         *
         * @param value
         *     allowed object is
         *     {@link TypeModel.Fields.Defaults }
         *
         */
        public void setDefaults(TypeModel.Fields.Defaults value) {
            this.defaults = value;
        }

        /**
         * Gets the value of the opposites property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isOpposites() {
            if (this.opposites == null) {
                return true;
            } else {
                return this.opposites;
            }
        }

        /**
         * Sets the value of the opposites property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setOpposites(Boolean value) {
            this.opposites = value;
        }

        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="ordering">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="type" type="{ConfigSchema}orderType" default="index" />
         *                 &lt;attribute name="mode" type="{ConfigSchema}modeType" default="useIntermediate" />
         *                 &lt;attribute name="usePrevEdge" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="useTypeName" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"ordering"})
        public static class Containers {

            @XmlElement(required = true) protected TypeModel.Fields.Containers.Ordering ordering;
            @XmlAttribute(name = "useTypeName") protected Boolean useTypeName;

            /**
             * Gets the value of the ordering property.
             *
             * @return
             *     possible object is
             *     {@link TypeModel.Fields.Containers.Ordering }
             *
             */
            public TypeModel.Fields.Containers.Ordering getOrdering() {
                return this.ordering;
            }

            /**
             * Sets the value of the ordering property.
             *
             * @param value
             *     allowed object is
             *     {@link TypeModel.Fields.Containers.Ordering }
             *
             */
            public void setOrdering(TypeModel.Fields.Containers.Ordering value) {
                this.ordering = value;
            }

            /**
             * Gets the value of the useTypeName property.
             *
             * @return
             *     possible object is
             *     {@link Boolean }
             *
             */
            public boolean isUseTypeName() {
                if (this.useTypeName == null) {
                    return false;
                } else {
                    return this.useTypeName;
                }
            }

            /**
             * Sets the value of the useTypeName property.
             *
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *
             */
            public void setUseTypeName(Boolean value) {
                this.useTypeName = value;
            }

            /**
             * <p>Java class for anonymous complex type.
             *
             * <p>The following schema fragment specifies the expected content contained within this class.
             *
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="type" type="{ConfigSchema}orderType" default="index" />
             *       &lt;attribute name="mode" type="{ConfigSchema}modeType" default="useIntermediate" />
             *       &lt;attribute name="usePrevEdge" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             *
             *
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Ordering {

                @XmlAttribute(name = "type") protected OrderType type;
                @XmlAttribute(name = "mode") protected ModeType mode;
                @XmlAttribute(name = "usePrevEdge") protected Boolean usePrevEdge;

                /**
                 * Gets the value of the type property.
                 *
                 * @return
                 *     possible object is
                 *     {@link OrderType }
                 *
                 */
                public OrderType getType() {
                    if (this.type == null) {
                        return OrderType.INDEX;
                    } else {
                        return this.type;
                    }
                }

                /**
                 * Sets the value of the type property.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link OrderType }
                 *
                 */
                public void setType(OrderType value) {
                    this.type = value;
                }

                /**
                 * Gets the value of the mode property.
                 *
                 * @return
                 *     possible object is
                 *     {@link ModeType }
                 *
                 */
                public ModeType getMode() {
                    if (this.mode == null) {
                        return ModeType.USE_INTERMEDIATE;
                    } else {
                        return this.mode;
                    }
                }

                /**
                 * Sets the value of the mode property.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link ModeType }
                 *
                 */
                public void setMode(ModeType value) {
                    this.mode = value;
                }

                /**
                 * Gets the value of the usePrevEdge property.
                 *
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *
                 */
                public boolean isUsePrevEdge() {
                    if (this.usePrevEdge == null) {
                        return false;
                    } else {
                        return this.usePrevEdge;
                    }
                }

                /**
                 * Sets the value of the usePrevEdge property.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *
                 */
                public void setUsePrevEdge(Boolean value) {
                    this.usePrevEdge = value;
                }

            }

        }

        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="useRule" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
         *       &lt;attribute name="setValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Defaults {

            @XmlAttribute(name = "useRule") protected Boolean useRule;
            @XmlAttribute(name = "setValue") protected Boolean setValue;

            /**
             * Gets the value of the useRule property.
             *
             * @return
             *     possible object is
             *     {@link Boolean }
             *
             */
            public boolean isUseRule() {
                if (this.useRule == null) {
                    return true;
                } else {
                    return this.useRule;
                }
            }

            /**
             * Sets the value of the useRule property.
             *
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *
             */
            public void setUseRule(Boolean value) {
                this.useRule = value;
            }

            /**
             * Gets the value of the setValue property.
             *
             * @return
             *     possible object is
             *     {@link Boolean }
             *
             */
            public boolean isSetValue() {
                if (this.setValue == null) {
                    return true;
                } else {
                    return this.setValue;
                }
            }

            /**
             * Sets the value of the setValue property.
             *
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *
             */
            public void setSetValue(Boolean value) {
                this.setValue = value;
            }

        }

        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="when" type="{ConfigSchema}intermediateWhenType" default="container" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Intermediates {

            @XmlAttribute(name = "when") protected IntermediateWhenType when;

            /**
             * Gets the value of the when property.
             *
             * @return
             *     possible object is
             *     {@link IntermediateWhenType }
             *
             */
            public IntermediateWhenType getWhen() {
                if (this.when == null) {
                    return IntermediateWhenType.CONTAINER;
                } else {
                    return this.when;
                }
            }

            /**
             * Sets the value of the when property.
             *
             * @param value
             *     allowed object is
             *     {@link IntermediateWhenType }
             *
             */
            public void setWhen(IntermediateWhenType value) {
                this.when = value;
            }

        }

    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="useAbstract" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="useContainment" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="useIdentity" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="useKeyset" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="useDefaultValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="useOpposite" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Properties {

        @XmlAttribute(name = "useAbstract") protected Boolean useAbstract;
        @XmlAttribute(name = "useContainment") protected Boolean useContainment;
        @XmlAttribute(name = "useIdentity") protected Boolean useIdentity;
        @XmlAttribute(name = "useKeyset") protected Boolean useKeyset;
        @XmlAttribute(name = "useDefaultValue") protected Boolean useDefaultValue;
        @XmlAttribute(name = "useOpposite") protected Boolean useOpposite;

        /**
         * Gets the value of the useAbstract property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isUseAbstract() {
            if (this.useAbstract == null) {
                return true;
            } else {
                return this.useAbstract;
            }
        }

        /**
         * Sets the value of the useAbstract property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setUseAbstract(Boolean value) {
            this.useAbstract = value;
        }

        /**
         * Gets the value of the useContainment property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isUseContainment() {
            if (this.useContainment == null) {
                return true;
            } else {
                return this.useContainment;
            }
        }

        /**
         * Sets the value of the useContainment property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setUseContainment(Boolean value) {
            this.useContainment = value;
        }

        /**
         * Gets the value of the useIdentity property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isUseIdentity() {
            if (this.useIdentity == null) {
                return true;
            } else {
                return this.useIdentity;
            }
        }

        /**
         * Sets the value of the useIdentity property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setUseIdentity(Boolean value) {
            this.useIdentity = value;
        }

        /**
         * Gets the value of the useKeyset property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isUseKeyset() {
            if (this.useKeyset == null) {
                return true;
            } else {
                return this.useKeyset;
            }
        }

        /**
         * Sets the value of the useKeyset property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setUseKeyset(Boolean value) {
            this.useKeyset = value;
        }

        /**
         * Gets the value of the useDefaultValue property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isUseDefaultValue() {
            if (this.useDefaultValue == null) {
                return true;
            } else {
                return this.useDefaultValue;
            }
        }

        /**
         * Sets the value of the useDefaultValue property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setUseDefaultValue(Boolean value) {
            this.useDefaultValue = value;
        }

        /**
         * Gets the value of the useOpposite property.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isUseOpposite() {
            if (this.useOpposite == null) {
                return true;
            } else {
                return this.useOpposite;
            }
        }

        /**
         * Sets the value of the useOpposite property.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setUseOpposite(Boolean value) {
            this.useOpposite = value;
        }

    }

}
