//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.07.20 at 10:40:55 AM CEST
//

package groove.io.conceptual.configuration.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for literalsType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="literalsType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="singleton"/>
 *     &lt;enumeration value="multiple"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "literalsType")
@XmlEnum
@SuppressWarnings("javadoc")
public enum LiteralsType {
    @XmlEnumValue("singleton") SINGLETON("singleton"),
    @XmlEnumValue("multiple") MULTIPLE("multiple");
    private final String value;

    LiteralsType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static LiteralsType fromValue(String v) {
        for (LiteralsType c : LiteralsType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
