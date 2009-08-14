/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.metadata;


/**
 * Whatever {@link java.util.Map} of types should contain entries for the property type, the
 * element type or the declaring class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @see MetadataStandard#asTypeMap(Class, TypeValuePolicy, KeyNamePolicy)
 *
 * @since 3.03
 * @module
 */
public enum TypeValuePolicy {
    /**
     * The type of a property, as infered from the
     * {@linkplain java.lang.reflect.Method#getReturnType() return type} of the property method.
     * Collections are not handled in any special way; if the return type is a collection, then
     * the value is {@code Collection.class} (or a subclass).
     */
    PROPERTY_TYPE,

    /**
     * The type of a property, or type of elements if the property is a collections. This is the
     * same than {@link #PROPERTY_TYPE} except that collections are handled in a special way: if
     * the property is a collection, then the value is the type of <em>elements</em> in that
     * collection.
     *
     * {@note Current implementation has an additional slight difference, in that if the metadata
     *        implementation has setter methods, then <code>ELEMENT_TYPE</code> favor a type which
     *        is consistent with both the getter and setter methods, if such type is found.}
     */
    ELEMENT_TYPE,

    /**
     * The type of the class that declares the method. A metadata implementation may have
     * different declaring classes for its properties if some of them are declared in parent
     * classes.
     */
    DECLARING_CLASS,

    /**
     * The type of the interface that declares the method. This is the same than
     * {@link #DECLARING_CLASS}, except that the interface from the metadata standard
     * is returned instead than the implementation class.
     */
    DECLARING_INTERFACE
}
