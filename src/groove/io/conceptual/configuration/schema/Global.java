//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.07.20 at 10:40:55 AM CEST
//

package groove.io.conceptual.configuration.schema;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for global complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="global">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_overrides" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="strings" type="{ConfigSchema}stringsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nullable" type="{ConfigSchema}nullableType" default="required" />
 *       &lt;attribute name="id_mode" type="{ConfigSchema}idModeType" default="full" />
 *       &lt;attribute name="id_separator" type="{ConfigSchema}idSeparatorType" default="$" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "global", propOrder = {"idOverrides", "strings"})
@SuppressWarnings("javadoc")
public class Global {

    @XmlElement(name = "id_overrides") protected List<Global.IdOverrides> idOverrides;
    @XmlElement(required = true) protected StringsType strings;
    @XmlAttribute(name = "nullable") protected NullableType nullable;
    @XmlAttribute(name = "id_mode") protected IdModeType idMode;
    @XmlAttribute(name = "id_separator") protected String idSeparator;

    /**
     * Gets the value of the idOverrides property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idOverrides property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdOverrides().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Global.IdOverrides }
     *
     *
     */
    public List<Global.IdOverrides> getIdOverrides() {
        if (this.idOverrides == null) {
            this.idOverrides = new ArrayList<>();
        }
        return this.idOverrides;
    }

    /**
     * Gets the value of the strings property.
     *
     * @return
     *     possible object is
     *     {@link StringsType }
     *
     */
    public StringsType getStrings() {
        return this.strings;
    }

    /**
     * Sets the value of the strings property.
     *
     * @param value
     *     allowed object is
     *     {@link StringsType }
     *
     */
    public void setStrings(StringsType value) {
        this.strings = value;
    }

    /**
     * Gets the value of the nullable property.
     *
     * @return
     *     possible object is
     *     {@link NullableType }
     *
     */
    public NullableType getNullable() {
        if (this.nullable == null) {
            return NullableType.REQUIRED;
        } else {
            return this.nullable;
        }
    }

    /**
     * Sets the value of the nullable property.
     *
     * @param value
     *     allowed object is
     *     {@link NullableType }
     *
     */
    public void setNullable(NullableType value) {
        this.nullable = value;
    }

    /**
     * Gets the value of the idMode property.
     *
     * @return
     *     possible object is
     *     {@link IdModeType }
     *
     */
    public IdModeType getIdMode() {
        if (this.idMode == null) {
            return IdModeType.FULL;
        } else {
            return this.idMode;
        }
    }

    /**
     * Sets the value of the idMode property.
     *
     * @param value
     *     allowed object is
     *     {@link IdModeType }
     *
     */
    public void setIdMode(IdModeType value) {
        this.idMode = value;
    }

    /**
     * Gets the value of the idSeparator property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdSeparator() {
        if (this.idSeparator == null) {
            return "$";
        } else {
            return this.idSeparator;
        }
    }

    /**
     * Sets the value of the idSeparator property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdSeparator(String value) {
        this.idSeparator = value;
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
     *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"id", "name"})
    public static class IdOverrides {

        @XmlElement(required = true) protected String id;
        @XmlElement(required = true) protected String name;

        /**
         * Gets the value of the id property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getId() {
            return this.id;
        }

        /**
         * Sets the value of the id property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Gets the value of the name property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getName() {
            return this.name;
        }

        /**
         * Sets the value of the name property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setName(String value) {
            this.name = value;
        }

    }

}
