/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.binding;

import java.util.Collection;
import java.util.regex.Pattern;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultAssociation;
import org.geotoolkit.feature.type.DefaultName;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.filter.binding.AbstractBinding;
import org.geotoolkit.feature.Attribute;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Feature;

import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;

/**
 * Creates a property accessor for simple features.
 * <p>
 * The created accessor handles a small subset of xpath expressions, a
 * non-nested "name" which corresponds to a feature attribute, and "@id",
 * corresponding to the feature id.
 * </p>
 * <p>
 * THe property accessor may be run against {@link SimpleFeature}, or 
 * against {@link SimpleFeature}. In the former case the feature property 
 * value is returned, in the latter the feature property type is returned. 
 * </p>
 * 
 * @author Justin Deoliveira, The Open Planning Project
 * @module pending
 */
public final class FeatureBinding_old  {
//
//    /** Single instnace is fine - classes are thread safe */
//    private static final PropertyAccessor ATTRIBUTE_ACCESS = new SimpleFeaturePropertyAccessor();
//    private static final PropertyAccessor DEFAULT_GEOMETRY_ACCESS = new DefaultGeometrySimpleFeaturePropertyAccessor();
//    private static final PropertyAccessor FID_ACCESS = new FidSimpleFeaturePropertyAccessor();
//    private static final PropertyAccessor XNUM_ACCESS = new XNumPropertyAccessor();
//    private static final PropertyAccessor ATT_ACCESS = new IdentityAccessor();
//    private static final Pattern ID_PATTERN       = Pattern.compile("@(\\w+:)?id");
//    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\w+:)?(.+)");
//    private static final Cache<String,PropertyAccessor> CACHE = new Cache<String, PropertyAccessor>();
//
//    /**
//     * {@inheritDoc }
//     */
//    @Override
//    public PropertyAccessor createPropertyAccessor(final Class type, final String xpath, final Class target, final Hints hints) {
//
//        if (xpath == null) {
//            return null;
//        }
//
//        if (!ComplexAttribute.class.isAssignableFrom(type) && !ComplexType.class.isAssignableFrom(type)) {
//            
//            if(ATT_ACCESS.canHandle(type, xpath, target)){
//                return ATT_ACCESS;
//            }
//            
//            return null; // we only work with feature
//        }
//
//
//        //try to find the accessor in the cache---------------------------------
//        PropertyAccessor accessor = CACHE.peek(xpath);
//        if(accessor != null){
//            return accessor;
//        }
//
//        //if ("".equals(xpath) && target == Geometry.class)---------------------
//        if (xpath.isEmpty()) {
//            final Cache.Handler<PropertyAccessor> handler = CACHE.lock(xpath);
//            accessor = handler.peek();
//            if (accessor == null) {
//                accessor = DEFAULT_GEOMETRY_ACCESS;
//            }
//            handler.putAndUnlock(accessor);
//            return DEFAULT_GEOMETRY_ACCESS;
//        }
//
//        if(xpath.startsWith("/")){
//            return null;
//        }
//
//        //check for fid access--------------------------------------------------
//        if (ID_PATTERN.matcher(xpath).matches()) {
//            final Cache.Handler<PropertyAccessor> handler = CACHE.lock(xpath);
//            accessor = handler.peek();
//            if (accessor == null) {
//                accessor = FID_ACCESS;
//            }
//            handler.putAndUnlock(accessor);
//            return FID_ACCESS;
//        }
//
//        //check xpath form *[number]--------------------------------------------
//        if(xpath.startsWith("*[") && xpath.endsWith("]")){
//            String num = xpath.substring(2, xpath.length()-1);
//
//            if(num.startsWith("position()=")){
//                num = num.substring(11);
//            }
//
//            try{
//                Integer.valueOf(num);
//                final Cache.Handler<PropertyAccessor> handler = CACHE.lock(xpath);
//                accessor = handler.peek();
//                if (accessor == null) {
//                    accessor = XNUM_ACCESS;
//                }
//                handler.putAndUnlock(accessor);
//                return XNUM_ACCESS;
//
//            }catch(NumberFormatException ex){
//            }
//        }
//
//        //check for simple property acess---------------------------------------
//        if (PROPERTY_PATTERN.matcher(xpath).matches()) {
//            final Cache.Handler<PropertyAccessor> handler = CACHE.lock(xpath);
//            accessor = handler.peek();
//            if (accessor == null) {
//                accessor = ATTRIBUTE_ACCESS;
//            }
//            handler.putAndUnlock(accessor);
//            return ATTRIBUTE_ACCESS;
//        }
//
//        
//
//        return null;
//    }
//
//    /**
//     * We strip off namespace prefix, we need new feature model to do this
//     * property
//     * <ul>
//     * <li>BEFORE: foo:bar
//     * <li>AFTER: bar
//     * </ul>
//     * 
//     * @param xpath
//     * @return xpath with any XML prefixes removed
//     */
//    private static String stripPrefix(String xpath) {
//        while(xpath.charAt(0) == '/'){
//            xpath = xpath.substring(1);
//        }
//        return xpath;
//    }
//
//    @Override
//    public int getPriority() {
//        return 10;
//    }
//
//    /**
//     * Access to SimpleFeature Identifier.
//     * 
//     * @author Jody Garnett, Refractions Research Inc.
//     */
//    private static class FidSimpleFeaturePropertyAccessor implements PropertyAccessor {
//
//        @Override
//        public boolean canHandle(final Class clazz, final String xpath, final Class target) {
//            //we only work against feature, not feature type
//            return ComplexAttribute.class.isAssignableFrom(clazz) && xpath.matches("@(\\w+:)?id");
//        }
//
//        @Override
//        public Object get(final Object object, final String xpath, final Class target) {
//            if(object instanceof ComplexAttribute){
//                final ComplexAttribute feature = (ComplexAttribute) object;
//                return feature.getIdentifier().getID();
//            }else if(object instanceof FeatureType){
//                final FeatureType ft = (FeatureType) object;
//                if(ft.isIdentified()){
//                    return true;
//                }
//            }
//            return null;
//        }
//
//        @Override
//        public void set(final Object object, final String xpath, final Object value, final Class target)
//                throws IllegalArgumentException {
//            throw new IllegalArgumentException("feature id is immutable");
//        }
//    }
//
//    static class DefaultGeometrySimpleFeaturePropertyAccessor implements PropertyAccessor {
//
//        @Override
//        public boolean canHandle(final Class clazz, final String xpath, final Class target) {
//            if (!"".equals(xpath)) {
//                return false;
//            }
//
//            return Feature.class.isAssignableFrom(clazz)
//                || FeatureType.class.isAssignableFrom(clazz);
//        }
//
//        @Override
//        public Object get(final Object object, final String xpath, final Class target) {
//            if(object instanceof SimpleFeature){
//                return ((SimpleFeature) object).getDefaultGeometry();
//            }else if (object instanceof Feature) {
//                return ((Feature) object).getDefaultGeometryProperty().getValue();
//            }
//            if (object instanceof FeatureType) {
//                return ((FeatureType) object).getGeometryDescriptor();
//            }
//
//            return null;
//        }
//
//        @Override
//        public void set(final Object object, final String xpath, final Object value, final Class target)
//                throws IllegalArgumentException{
//
//            if (object instanceof Feature) {
//                ((Feature) object).getDefaultGeometryProperty().setValue(value);
//            }
//            if (object instanceof FeatureType) {
//                throw new IllegalArgumentException("feature type is immutable");
//            }
//
//        }
//    }
//
//    static class SimpleFeaturePropertyAccessor implements PropertyAccessor {
//
//        @Override
//        public boolean canHandle(final Class clazz, String xpath, final Class target) {
//            xpath = stripPrefix(xpath);
//
//            return ComplexAttribute.class.isAssignableFrom(clazz)
//                || ComplexType.class.isAssignableFrom(clazz);
//        }
//
//        @Override
//        public Object get(final Object object, String xpath, final Class target) {
//            xpath = stripPrefix(xpath);
//
//            if (object instanceof ComplexAttribute) {
//                if(target != null){
//                    if(Property.class.isAssignableFrom(target)){
//                        final Property prop = ((ComplexAttribute) object).getProperty(xpath);
//                        return prop;
//                    }else if(Collection.class.isAssignableFrom(target)){
//                        final Collection<Property> props = ((ComplexAttribute) object).getProperties(xpath);
//                        return props;
//                    }
//                }
//
//                final Property prop = ((ComplexAttribute) object).getProperty(xpath);
//                if(prop == null){
//                    return null;
//                }else if(prop instanceof DefaultAssociation){
//                    return ((DefaultAssociation)prop).getLink();
//                }else{
//                    return prop.getValue();
//                }
//            }else if(object instanceof ComplexType) {
//                return ((ComplexType) object).getDescriptor(xpath);
//            }
//
//            return null;
//        }
//
//        @Override
//        public void set(final Object object, String xpath, final Object value, final Class target)
//                throws IllegalArgumentException {
//            xpath = stripPrefix(xpath);
//            final Name name = DefaultName.valueOf(xpath);
//
//            if(object instanceof SimpleFeature) {
//                ((SimpleFeature) object).setAttribute(name, value);
//            }
//
//            if (object instanceof ComplexAttribute) {
//                ((ComplexAttribute) object).getProperty(name).setValue(value);
//            }
//
//            if (object instanceof FeatureType) {
//                throw new IllegalArgumentException("Feature type is immutable");
//            }
//
//        }
//    }
//
//    static class XNumPropertyAccessor implements PropertyAccessor {
//
//        private int toIndex(final String xpath){
//            String num = xpath.substring(2, xpath.length()-1);
//
//            if(num.startsWith("position()=")){
//                num = num.substring(11);
//            }
//
//            return Integer.valueOf(num);
//        }
//
//        @Override
//        public boolean canHandle(final Class clazz, final String xpath, final Class target) {
//
//            return ComplexAttribute.class.isAssignableFrom(clazz)
//                || ComplexType.class.isAssignableFrom(clazz);
//        }
//
//        @Override
//        public Object get(final Object object, final String xpath, final Class target) {
//            final int index = toIndex(xpath);
//
//            if(object instanceof SimpleFeature){
//                ((SimpleFeature) object).getAttribute(index);
//            }
//
//            if (object instanceof ComplexAttribute) {
//                final ComplexAttribute feature = (ComplexAttribute)object;
//                int i = 1;
//                for(Property prop : feature.getProperties()){
//                    if(i == index){
//                        return feature.getProperty(prop.getName()).getValue();
//                    }
//                    i++;
//                }
//            }
//
//            if (object instanceof ComplexType) {
//                final ComplexType ft = (ComplexType)object;
//                int i = 1;
//                for(PropertyDescriptor prop : ft.getDescriptors()){
//                    if(i == index){
//                        return prop;
//                    }
//                    i++;
//                }
//            }
//
//            return null;
//        }
//
//        @Override
//        public void set(final Object object, final String xpath, final Object value, final Class target)
//                throws IllegalArgumentException {
//            final int index = toIndex(xpath);
//
//            if(object instanceof SimpleFeature){
//                ((SimpleFeature) object).setAttribute(index, value);
//            }
//
//            if (object instanceof ComplexAttribute) {
//                final ComplexAttribute feature = (ComplexAttribute)object;
//                int i = 0;
//                for(Property prop : feature.getProperties()){
//                    if(i == index){
//                        feature.getProperty(prop.getName()).setValue(value);
//                        return;
//                    }
//                    i++;
//                }
//            }
//
//            if (object instanceof ComplexType) {
//                throw new IllegalArgumentException("Complex types are immutable");
//            }
//
//        }
//    }
//
//    static class IdentityAccessor extends AbstractBinding<Attribute>{
//
//        public IdentityAccessor() {
//            super(Attribute.class, 10);
//        }
//
//        @Override
//        public boolean support(String xpath) {
//            xpath = stripPrefix(xpath);
//            return ".".equalsIgnoreCase(xpath);
//        }
//
//        @Override
//        public <T> T get(Attribute candidate, String xpath, Class<T> target) throws IllegalArgumentException {
//            if(candidate instanceof Attribute){
//                if(Property.class.isAssignableFrom(target)){
//                    return (T) candidate;
//                }else{
//                    return (T) ((Attribute)candidate).getValue();
//                }
//            }
//            return ObjectConverters.convert(candidate, target);
//        }
//
//        @Override
//        public void set(Attribute candidate, String xpath, Object value) throws IllegalArgumentException {
//            if(object instanceof Attribute){
//                ((Attribute)object).setValue(value);
//            }
//        }
//        
//    }
//    
}
