/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.util.Locale;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.io.GridCoverageStorePool;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.Utilities;

import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.internal.sql.table.Entry;


/**
 * Information about an image format.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class FormatEntry extends Entry {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8790032968708208057L;

    /**
     * The image format name as declared in the database. This value shall be a name
     * useable in calls to {@link javax.imageio.ImageIO#getImageReadersByFormatName}.
     * <p>
     * For compatibility reason, the user should be prepared to handle MIME type
     * (as understood by {@link javax.imageio.ImageIO#getImageReadersByMIMEType}).
     * as well. As a heuristic rule, we can consider this value as a MIME type if
     * it contains the {@code '/'} character.
     */
    public final String imageFormat;

    /**
     * The sample dimensions for coverages encoded with this format, or {@code null} if undefined.
     * If non-null, then the list size is equals to the expected number of bands.
     * <p>
     * Each {@code SampleDimension} specifies how to convert pixel values to geophysics values,
     * or conversely. Their type (geophysics or not) is format dependent. For example coverages
     * read from PNG files will typically store their data as integer values (non-geophysics),
     * while coverages read from ASCII files will often store their pixel values as real numbers
     * (geophysics values).
     *
     * @see GridSampleDimension#geophysics(boolean)
     */
    public final List<GridSampleDimension> sampleDimensions;

    /**
     * The pool of coverage loaders, to be created when first needed. We use a different pool
     * instance for each format in order to reuse the same {@link javax.imageio.ImageReader}
     * instance when a {@code coverageLoaders.acquireReader().read(...)} method is invoked.
     */
    private transient GridCoverageStorePool coverageLoaders;

    /**
     * Creates a new entry for this format.
     *
     * @param name       An identifier for this entry.
     * @param formatName Format name (i.e. the plugin to use).
     * @param bands      Sample dimensions for coverages encoded with this format, or {@code null}.
     * @param geophysics {@code true} if coverage to be read are already geophysics values.
     */
    protected FormatEntry(final Comparable<?> name, final String formatName,
            final GridSampleDimension[] bands, final boolean geophysics)
    {
        super(name, null);
        this.imageFormat = formatName.trim();
        if (bands != null) {
            for (int i=0; i<bands.length; i++) {
                bands[i] = bands[i].geophysics(geophysics);
            }
            sampleDimensions = UnmodifiableArrayList.wrap(bands);
        } else {
            sampleDimensions = null;
        }
    }

    /**
     * Returns the ranges of valid sample values for each band in this format.
     * The range are always expressed in <cite>geophysics</cite> values.
     */
    final MeasurementRange<Double>[] getSampleValueRanges() {
        final List<GridSampleDimension> bands = sampleDimensions;
        if (bands == null) {
            return null;
        }
        @SuppressWarnings({"unchecked","rawtypes"})  // Generic array creation.
        final MeasurementRange<Double>[] ranges = new MeasurementRange[bands.size()];
        for (int i=0; i<ranges.length; i++) {
            final GridSampleDimension band = bands.get(i).geophysics(true);
            ranges[i] = MeasurementRange.create(band.getMinimumValue(), band.getMaximumValue(), band.getUnits());
        }
        return ranges;
    }

    /**
     * Returns the pool of coverage loaders associated with this format.
     *
     * @return The pool of coverage loaders.
     */
    public synchronized GridCoverageStorePool getCoverageLoaders() {
        if (coverageLoaders == null) {
            coverageLoaders = new GridCoverageLoader.Pool(this);
        }
        return coverageLoaders;
    }

    /**
     * Returns a tree representation of this format, including
     * {@linkplain SampleDimension sample dimensions} and {@linkplain Category categories}.
     *
     * @param  locale The locale to use for formatting labels in the tree.
     * @return The tree root.
     */
    public MutableTreeNode getTree(final Locale locale) {
        final DefaultMutableTreeNode root = new FormatTreeNode(this);
        if (sampleDimensions != null) {
            for (final GridSampleDimension band : sampleDimensions) {
                final List<Category> categories = band.getCategories();
                final int categoryCount = categories.size();
                final DefaultMutableTreeNode node = new FormatTreeNode(band, locale);
                for (int j=0; j<categoryCount; j++) {
                    node.add(new FormatTreeNode(categories.get(j), locale));
                }
                root.add(node);
            }
        }
        return root;
    }

    /**
     * Overriden as a safety, but should not be necessary since identifiers are supposed
     * to be unique in a given database. We don't compare the sample dimensions because
     * it may be costly.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final FormatEntry that = (FormatEntry) object;
            return Utilities.equals(imageFormat, that.imageFormat);
        }
        return false;
    }
}