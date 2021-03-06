/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.function.other.OtherFunctionFactory;

import org.geotoolkit.feature.type.AssociationType;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.type.FeatureTypeFactory;
import org.geotoolkit.feature.type.GeometryType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyType;
import org.geotoolkit.filter.function.string.StringFunctionFactory;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;


/**
 * Builder for attribute types and descriptors.
 * <p>
 * Building an attribute type:
 * <pre>
 * <code>
 *  //create the builder
 * 	AttributeTypeBuilder builder = new AttributeTypeBuilder();
 *
 *  //set type information
 *  builder.setName( "intType" ):
 *  builder.setBinding( Integer.class );
 *  builder.setNillable( false );
 *
 *  //build the type
 *  AttributeType type = builder.buildType();
 * </code>
 * </pre>
 * </p>
 * <p>
 * Building an attribute descriptor:
 * <pre>
 * <code>
 *  //create the builder
 * 	AttributeTypeBuilder builder = new AttributeTypeBuilder();
 *
 *  //set type information
 *  builder.setName( "intType" ):
 *  builder.setBinding( Integer.class );
 *  builder.setNillable( false );
 *
 *  //set descriptor information
 *  builder.setMinOccurs(0);
 *  builder.setMaxOccurs(1);
 *  builder.setNillable(true);
 *
 *  //build the descriptor
 *  AttributeDescriptor descriptor = builder.buildDescriptor("intProperty");
 * </code>
 * </pre>
 * <p>
 * This class maintains state and is not thread safe.
 * </p>
 *
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 */
public class AttributeTypeBuilder {

    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private final FeatureTypeFactory factory;

    /**
     * Local name used to name a descriptor; or combined with namespaceURI to name a type.
     */
    private Name name;
    /**
     * abstract flag
     */
    private boolean isAbstract = false;
    /**
     * restrictions
     */
    private List<Filter> restrictions;
    /**
     * string description
     */
    private CharSequence description;
    /**
     * identifiable flag
     */
    private boolean isIdentifiable = false;
    /**
     * bound java class
     */
    private Class binding;
    /**
     * super type
     */
    private PropertyType superType;

    /**
     * GeometryType CRS
     */
    private CoordinateReferenceSystem crs;

    /**
     * If this value is set an additional restriction
     * will be added based on the length function.
     */
    private Integer length = null;

    private final Map<Object,Object> userData = new HashMap<Object, Object>();

    /**
     * Constructs the builder.
     */
    public AttributeTypeBuilder() {
        this(null);
    }

    /**
     * Constructs the builder specifying the factory used to build attribute
     * types.
     *
     */
    public AttributeTypeBuilder(final FeatureTypeFactory factory) {
        if(factory == null){
            this.factory = FeatureTypeFactory.INSTANCE;
        }else{
            this.factory = factory;
        }
    }

    /**
     * Resets all internal state.
     */
    public void reset(){
        name = null;
        isAbstract = false;
        restrictions = null;
        description = null;
        isIdentifiable = false;
        binding = null;
        superType = null;
        crs = null;
        length = null;
        userData.clear();
    }

    /**
     * Initializes builder state from another attribute type.
     */
    public AttributeTypeBuilder copy(final AttributeType type) {
        name = type.getName();
        isAbstract = type.isAbstract();

        if (type.getRestrictions() != null) {
            restrictions().addAll(type.getRestrictions());
        }

        description = type.getDescription() != null ? type.getDescription().toString() : null;
        isIdentifiable = type.isIdentified();
        binding = type.getBinding();
        superType = type.getSuper();

        if (type instanceof GeometryType) {
            crs = ((GeometryType) type).getCoordinateReferenceSystem();
        }

        userData.putAll(type.getUserData());

        return this;
    }

    public void setBinding(final Class binding) {
        this.binding = binding;
    }

    public Class getBinding() {
        return binding;
    }

    public void setName(final Name name) {
        this.name = name;
    }

    public void setName(final String localPart){
        this.name = new DefaultName(localPart);
    }

    public void setName(final String namespace, final String localPart){
        this.name = new DefaultName(namespace,localPart);
    }

    public void setName(final String namespace, final String separator, final String localPart){
        this.name = new DefaultName(namespace,separator,localPart);
    }

    public Name getName() {
        return name;
    }

    public void setCRS(final CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    public void setDescription(final CharSequence description) {
        this.description = description;
    }

    public CharSequence getDescription() {
        return description;
    }

    public void setAbstract(final boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isIsAbstract() {
        return isAbstract;
    }

    public void setIdentifiable(final boolean isIdentifiable) {
        this.isIdentifiable = isIdentifiable;
    }

    public boolean isIsIdentifiable() {
        return isIdentifiable;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }

    public void setRestrictions(List<Filter> restrictions){
        this.restrictions = restrictions;
    }

    public List<Filter> getRestrictions() {
        return restrictions;
    }

    public void addRestriction(final Filter restriction) {
        restrictions().add(restriction);
    }

    public void addUserData(final Object key, final Object value) {
        userData.put(key, value);
    }

    public PropertyType getParentType() {
        return superType;
    }

    public void setParentType(final PropertyType superType) {
        this.superType = superType;
    }

    /**
     * Builds the attribute type.
     * <p>
     * This method resets all state after the attribute is built.
     * </p>
     */
    public AttributeType buildType() {
        final Name name;
        if(this.name == null){
            name = new DefaultName(binding.getSimpleName());
        }else{
            name = this.name;
        }


        if (length != null) {
            final Filter lengthRestriction = lengthRestriction(length);
            restrictions().add(lengthRestriction);
        }

        final AttributeType type = factory.createAttributeType(
                name, binding, isIdentifiable, isAbstract,
                restrictions(), (AttributeType)superType, description());
        type.getUserData().putAll(userData);
        return type;
    }

    /**
     * Builds the geometry attribute type.
     * <p>
     * This method resets all state after the attribute is built.
     * </p>
     */
    public GeometryType buildGeometryType() {
        final Name name;
        if(this.name == null){
            name = new DefaultName(binding.getSimpleName());
        }else{
            name = this.name;
        }

        final GeometryType type = factory.createGeometryType(
                name, binding, crs, isIdentifiable, isAbstract,
                restrictions(), (AttributeType)superType, description());
        type.getUserData().putAll(userData);
        return type;
    }

    public AssociationType buildAssociationType(final AttributeType distType){
        final Name name;
        if(this.name == null){
            name = new DefaultName(binding.getSimpleName());
        }else{
            name = this.name;
        }

        final AssociationType type = factory.createAssociationType(name, distType,
                isAbstract, restrictions(),(AssociationType)superType,description());
        type.getUserData().putAll(userData);
        return type;
    }

    private InternationalString description() {
        if(description instanceof InternationalString){
            return (InternationalString) description;
        }

        return description != null ? new SimpleInternationalString(description.toString()) : null;
    }

    private List<Filter> restrictions() {
        if (restrictions == null) {
            restrictions = new ArrayList();
        }

        return restrictions;
    }

    /**
     * Helper method to create a "length" filter.
     */
    private Filter lengthRestriction(final int length) {
        if (length < 0) {
            return null;
        }
        final Expression lengthFunction = FF.function(StringFunctionFactory.LENGTH,FF.property(name));
        if (lengthFunction == null) {
            return null;
        }
        Filter cf = FF.lessOrEqual(lengthFunction, FF.literal(length));
        return (cf == null) ? Filter.EXCLUDE : cf;
    }

}
