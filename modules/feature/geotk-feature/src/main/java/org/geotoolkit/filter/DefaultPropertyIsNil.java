/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.filter;

import java.io.Serializable;
import static org.apache.sis.util.ArgumentChecks.*;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.expression.Expression;

/**
 * Immutable "is nill" filter.
 * This implementation is similar to isNull.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPropertyIsNil implements PropertyIsNil,Serializable {

    private final Expression exp;

    public DefaultPropertyIsNil(final Expression exp) {
        ensureNonNull("expression", exp);
        this.exp = exp;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getExpression() {
        return exp;
    }

    @Override
    public String getNilReason() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        Object obj = exp.evaluate(object);
        return obj == null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return "IsNull:"+exp.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultPropertyIsNil other = (DefaultPropertyIsNil) obj;
        if (this.exp != other.exp && (this.exp == null || !this.exp.equals(other.exp))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.exp != null ? this.exp.hashCode() : 0);
        return hash;
    }

}
