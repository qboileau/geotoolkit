/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.geotoolkit.feature.type.AssociationDescriptor;
import org.geotoolkit.feature.type.AssociationType;
import org.geotoolkit.feature.type.AttributeType;

/**
 * Default implementation of an association.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultAssociation extends DefaultProperty<Attribute,AssociationDescriptor> implements Association {

    /**
     * Additional information when relation is 0:N.
     * This value can be a reference id, an URI ...
     */
    private final Object link;
    
    public DefaultAssociation(final Attribute value, final AssociationDescriptor descriptor) {
        this(value, descriptor, null);
    }
    
    public DefaultAssociation(final Attribute value, final AssociationDescriptor descriptor, final Object link) {
        super(value, descriptor);
        this.link = link;
    }

    /**
     * Additional information when relation is 0:N.
     * This value can be a reference id, an URI ...
     * @return Object : can be null
     */
    public Object getLink() {
        return link;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType getRelatedType() {
        return descriptor.getType().getRelatedType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AssociationType getType() {
        return descriptor.getType();
    }
    
}
