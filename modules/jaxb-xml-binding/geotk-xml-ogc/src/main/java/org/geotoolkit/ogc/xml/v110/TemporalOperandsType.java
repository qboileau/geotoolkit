/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for TemporalOperandsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TemporalOperandsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TemporalOperand" type="{http://www.opengis.net/ogc}TemporalOperandType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalOperandsType", propOrder = {
    "temporalOperand"
})
public class TemporalOperandsType {

    @XmlElement(name = "TemporalOperand", required = true)
    private List<QName> temporalOperand;

    /**
     * Gets the value of the temporalOperand property.
     * 
     */
    public List<QName> getTemporalOperand() {
        if (temporalOperand == null) {
            temporalOperand = new ArrayList<QName>();
        }
        return this.temporalOperand;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[TemporalOperandsType]").append("\n");
        if (temporalOperand != null) {
            sb.append("temporalOperand:\n");
            for (QName q: temporalOperand) {
                sb.append(q).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

       if (object instanceof TemporalOperandsType) {
           final TemporalOperandsType that = (TemporalOperandsType) object;
       
            return Utilities.equals(this.temporalOperand, that.temporalOperand);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.temporalOperand != null ? this.temporalOperand.hashCode() : 0);
        return hash;
    }

}
