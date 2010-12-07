/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test.coverage;

import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.test.TestBase;

import static org.junit.Assert.*;


/**
 * Base class for tests on {@link AbstractCoverage} subclasses.
 * This base class provides static methods working on {@link Coverage}
 * and {@link AffineTransform} objects.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 2.1
 */
public abstract class CoverageTestBase extends TestBase {
    /**
     * {@code true} if the result of coverage operations should be displayed.
     * This is sometime useful for debugging purpose.
     */
    protected boolean viewEnabled = false;

    /**
     * Small value for comparison of sample values. Since most grid coverage implementations in
     * Geotk 2 store geophysics values as {@code float} numbers, this {@code EPS} value must
     * be of the order of {@code float} relative precision, not {@code double}.
     */
    protected static final float EPS = 1E-5f;

    /**
     * Creates a new test case.
     */
    protected CoverageTestBase() {
    }

    /**
     * Returns the "Sample to geophysics" transform as an affine transform, or {@code null}
     * if none. Note that the returned instance may be an immutable one, not necessarily the
     * default Java2D implementation.
     *
     * @param  coverage The coverage for which to get the "grid to CRS" affine transform.
     * @return The "grid to CRS" affine transform of the given coverage, or {@code null}
     *         if none or if the transform is not affine.
     */
    protected static AffineTransform getAffineTransform(final Coverage coverage) {
        if (coverage instanceof GridCoverage) {
            final GridGeometry geometry = ((GridCoverage) coverage).getGridGeometry();
            if (geometry != null) {
                final MathTransform gridToCRS = geometry.getGridToCRS();
                if (gridToCRS instanceof AffineTransform) {
                    return (AffineTransform) gridToCRS;
                }
            }
        }
        return null;
    }

    /**
     * Compares two affine transforms for equality.
     *
     * @param expected The expected affine transform.
     * @param actual   The actual affine transform.
     */
    protected static void assertTransformEquals(AffineTransform expected, AffineTransform actual) {
        assertEquals("scaleX",     expected.getScaleX(),     actual.getScaleX(),     EPS);
        assertEquals("scaleY",     expected.getScaleY(),     actual.getScaleY(),     EPS);
        assertEquals("shearX",     expected.getShearX(),     actual.getShearX(),     EPS);
        assertEquals("shearY",     expected.getShearY(),     actual.getShearY(),     EPS);
        assertEquals("translateX", expected.getTranslateX(), actual.getTranslateX(), EPS);
        assertEquals("translateY", expected.getTranslateY(), actual.getTranslateY(), EPS);
    }

    /**
     * Compares the rendered view of two coverages for equality.
     *
     * @param expected The coverage containing the expected pixel values.
     * @param actual   The coverage containing the actual pixel values.
     */
    protected static void assertRasterEquals(final Coverage expected, final Coverage actual) {
        assertRasterEquals(expected.getRenderableImage(0,1).createDefaultRendering(),
                             actual.getRenderableImage(0,1).createDefaultRendering());
    }

    /**
     * Compares two rasters for equality.
     *
     * @param expected The image containing the expected pixel values.
     * @param actual   The image containing the actual pixel values.
     */
    protected static void assertRasterEquals(final RenderedImage expected, final RenderedImage actual) {
        final RectIter e = RectIterFactory.create(expected, null);
        final RectIter a = RectIterFactory.create(actual,   null);
        if (!e.finishedLines()) do {
            assertFalse(a.finishedLines());
            if (!e.finishedPixels()) do {
                assertFalse(a.finishedPixels());
                if (!e.finishedBands()) do {
                    assertFalse(a.finishedBands());
                    final float pe = e.getSampleFloat();
                    final float pa = a.getSampleFloat();
                    assertEquals(pe, pa, EPS);
                    a.nextBand();
                } while (!e.nextBandDone());
                assertTrue(a.finishedBands());
                a.nextPixel();
                a.startBands();
                e.startBands();
            } while (!e.nextPixelDone());
            assertTrue(a.finishedPixels());
            a.nextLine();
            a.startPixels();
            e.startPixels();
        } while (!e.nextLineDone());
        assertTrue(a.finishedLines());
    }

    /**
     * Shows the default rendering of the specified coverage.
     * This is used for debugging only.
     *
     * @param coverage The coverage to display.
     */
    protected final void show(final Coverage coverage) {
        if (!viewEnabled) {
            return;
        }
        final RenderedImage image = coverage.getRenderableImage(0,1).createDefaultRendering();
        try {
            Class.forName("org.geotoolkit.gui.swing.OperationTreeBrowser")
                 .getMethod("show", new Class<?>[] {RenderedImage.class})
                 .invoke(null, new Object[]{image});
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            /*
             * The OperationTreeBrowser is not part of Geotk's core. It is optional and this
             * class should not fails if it is not presents. This is only a helper for debugging.
             */
            System.err.println(e);
        }
    }
}