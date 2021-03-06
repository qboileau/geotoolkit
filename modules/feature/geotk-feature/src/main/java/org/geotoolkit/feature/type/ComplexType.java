/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2006-2014 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.geotoolkit.feature.type;

import java.util.Collection;

import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Property;

/**
 * The type of a complex attribute.
 * <br/>
 * <p>
 * Similar to how a complex attribute is composed of other properties, a complex
 * type is composed of property descriptors. A complex type is very much like a
 * complex type from xml schema. Consider the following xml schema complex type:
 * <pre>
 * &lt;element name="myComplexElement" type="myComplexType"/>
 * &lt;complexType name="myComplexType">
 *   &lt;sequence>
 *     &lt;element name="foo" type="xs:string" minOccurs="2" maxOccurs="4">
 *     &lt;element name="bar" type="xs:int" nillable=false/>
 *   &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * The corresponding complex type that would emerge would be composed as follows:
 * <pre>
 *   ComplexType complexType = ...;
 *   complexType.getProperties().size() == 2;
 *
 *   //the foo property descriptor
 *   PropertyDescriptor foo = complexType.getProperty( "foo" );
 *   foo.getName().getLocalPart() == "foo";
 *   foo.getMinOccurs() == 2;
 *   foo.getMaxOccurs() == 4;
 *   foo.isNillable() == true;
 *   foo.getType().getName().getLocalPart() == "string";
 *
 *   //the bar property descriptor
 *   PropertyDescriptor bar = complexType.getProperty( "bar" );
 *   foo.getName().getLocalPart() == "bar";
 *   foo.getMinOccurs() == 1;
 *   foo.getMaxOccurs() == 1;
 *   foo.isNillable() == false;
 *   foo.getType().getName().getLocalPart() == "int";
 * </pre>
 * </p>
 * Now consider the following xml instance document:
 * <pre>
 * &lt;myComplexElement>
 *   &lt;foo>one&lt;/foo>
 *   &lt;foo>two&lt;/foo>
 *   &lt;foo>three&lt;/foo>
 *   &lt;bar>1&lt;/bar>
 * &lt;/myComplexElement>
 * </pre>
 * <br>
 * The resulting complex attribute would be composed as follows:
 * <pre>
 *   ComplexAttribute attribute = ...;
 *   attribute.getName().getLocalPart() == "myComplexElement";
 *   attribute.getType().getName().getLocalPart() == "myComplexType";
 *
 *   Collection foo = attribute.getProperties( "foo" );
 *   foo.size() == 3;
 *   foo.get(0).getValue() == "one";
 *   foo.get(1).getValue() == "two";
 *   foo.get(2).getValue() == "three";
 *
 *   Property bar = attribute.getProperty( "bar" );
 *   bar.getValue() == 1;
 * </pre>
 * </p>
 * @see ComplexAttribute
 *
 * @author Jody Garnett (Refractions Research)
 * @author Justin Deoliveira (The Open Planning Project)
 *
 * @deprecated Redesigned as {@link org.opengis.feature.FeatureType} in the {@code org.opengis.feature} package.
 */
@Deprecated
public interface ComplexType extends AttributeType {
    /**
     * Override and type narrow to Collection<Property>.class.
     */
    Class<Collection<Property>> getBinding();

    /**
     * The property descriptor which compose the complex type.
     * <p>
     * A complex type can be composed of attributes and associations which means
     * this collection returns instances of {@link AttributeDescriptor} and
     * {@link AssociationDescriptor}.
     * </p>
     *
     * @return Collection of descriptors representing the composition of the
     * complex type.
     *
     * @deprecated Replaced by {@link org.opengis.feature.FeatureType#getProperties(boolean)}.
     */
    Collection<PropertyDescriptor> getDescriptors();

    /**
     * Describe a single property by name.
     * <p>
     * This method returns <code>null</code> if no such property is found.
     * </p>
     * @param name The name of the property to get.
     *
     * @return The property matching the specified name, or <code>null</code>.
     */
    PropertyDescriptor getDescriptor( Name name );

    /**
     * Describe a single property by unqualified name.
     * <p>
     * Note: Special care should be taken when using this method in the case
     * that two properties with the same local name but different namespace uri
     * exist. For this reason using {@link #getDescriptor(Name)} is safer.
     * </p>
     * <p>
     * This method returns <code>null</code> if no such property is found.
     * </p>
     * @param name The name of the property to get.
     *
     * @return The property matching the specified name, or <code>null</code>.
     *
     * @deprecated Replaced by {@link org.opengis.feature.FeatureType#getProperty(String)}.
     */
    PropertyDescriptor getDescriptor( String name );

    /**
     * Indicates ability of XPath to notice this attribute.
     * <p>
     * This facility is used to "hide" an attribute from XPath searches, while the compelx contents will still
     * be navigated no additional nesting will be considered. It will be as if the content were "folded" inline
     * resulting in a flatter nesting structure.
     * </p>
     * <p>
     * Construct described using Java Interfaces:<pre><code>
     * interface TestSample {
     *     String name;
     *     List<Measurement> measurement;
     * }
     * interface Measurement {
     *     long timestamp;
     *     Point point;
     *     long reading;
     * }
     * </code></pre>
     * The above is can hold the following information:<pre><code>
     * [ name="survey1",
     *   measurements=(
     *       [timestamp=3,point=(2,3), reading=4200],
     *       [timestamp=9,point=(2,4), reading=445600],
     *   )
     * ]
     * </code></pre>
     * Out of the box this is represented to XPath as the following tree:<pre><code>
     * root/name: survey1
     * root/measurement[0]/timestamp:3
     * root/measurement[0]/point: (2,3)
     * root/measurement[0]/reading: 4200
     * root/measurement[1]/timestamp:9
     * root/measurement[2]/point: (2,4)
     * root/measurement[3]/reading: 445600
     * </code></pre>
     *
     * By inlining Measurement we can achive the following:<pre><code>
     * root/name: survey1
     * root/timestamp[0]:3
     * root/point[0]: (2,3)
     * root/reading[0]: 4200
     * root/timestamp[1]:9
     * root/point[1]: (2,4)
     * root/reading[1] 445600
     * </code></pre>
     *
     * @return true if  attribute is to be considered transparent by XPath queries
     */
    boolean isInline();

    /**
     * Describes allowable content, indicating containment.
     * <p>
     * A collection of AttributeDescriptors (name and AttributeType) is used.
     * We make no restrictions as to attribute order. All attributes are considered
     * accessable by name (and order is thus insignificant).
     * </p>
     * <p>
     * If you are modling a typing system where attribute order is relevant
     * you may make use of a List. Similarly if duplicate attributes are
     * disallowed you may make use of a Set.
     * </p>
     * <p>
     * This method follows JavaBeans naming convention indicating this is part of
     * our data model.
     * </p>
     */
    //Collection<AttributeDescriptor> attributes();

    /**
     * Allowable associations, indicating non containment relationships.
     */
    //Collection<AssociationDescriptor> associations();

}
