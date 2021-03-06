/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.coverage.processing;

import java.awt.Color;
import java.io.Writer;
import java.util.Locale;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.Interpolation;
import javax.media.jai.KernelJAI;

import org.opengis.util.InternationalString;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.parameter.ParameterWriter;
import org.geotoolkit.internal.image.ImageUtilities;
import org.geotoolkit.resources.Vocabulary;


/**
 * Format grid coverage operation parameters in a tabular format.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
final class CoverageParameterWriter extends ParameterWriter {
    /**
     * Creates a new formatter writing parameters to the specified output stream.
     */
    public CoverageParameterWriter(final Writer out) {
        super(out);
    }

    /**
     * Formats the specified value as a string.
     */
    @Override
    protected String formatValue(final Object value) {
        if (KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL.equals(value)) {
            return "GRADIENT_MASK_SOBEL_HORIZONTAL";
        }
        if (KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL.equals(value)) {
            return "GRADIENT_MASK_SOBEL_VERTICAL";
        }
        if (value instanceof AbstractCoverage) {
            final InternationalString name = ((AbstractCoverage) value).getName();
            final Locale locale = getLocale();
            return (name != null) ? name.toString(locale) :
                Vocabulary.getResources(locale).getString(Vocabulary.Keys.UNTITLED);
        }
        if (value instanceof Interpolation) {
            return ImageUtilities.getInterpolationName((Interpolation) value);
        }
        if (value instanceof EnumeratedParameter) {
            return ((EnumeratedParameter) value).getName();
        }
        if (value instanceof Color) {
            final Color c = (Color) value;
            return "RGB[" + c.getRed() + ',' + c.getGreen() + ',' + c.getBlue() + ']';
        }
        return super.formatValue(value);
    }
}
