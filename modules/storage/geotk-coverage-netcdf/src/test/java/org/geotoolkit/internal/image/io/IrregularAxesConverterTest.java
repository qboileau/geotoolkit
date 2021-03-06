/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.image.io;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.Matrix;

import org.apache.sis.measure.Range;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.matrix.Matrix3;
import org.apache.sis.referencing.operation.matrix.Matrices;

import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests {@link IrregularAxesConverter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.15
 */
public final strictfp class IrregularAxesConverterTest {
    /**
     * A {@link DiscreteCoordinateSystemAxis} implementation used for this test.
     */
    private static final strictfp class Axis implements DiscreteCoordinateSystemAxis<Double> {
        /**
         * The ordinate values.
         */
        private final double[] ordinates;

        /**
         * Creates a new axis for the given ordinate values.
         */
        Axis(final double... ordinates) {
            this.ordinates = ordinates;
        }

        /**
         * Returns the number of ordinates.
         */
        @Override
        public int length() {
            return ordinates.length;
        }

        /**
        * Returns the type of ordinate values.
        */
        @Override
        public Class<Double> getElementType() {
            return Double.class;
        }

        /**
         * Returns the ordinates values at the given index.
         */
        @Override
        public Comparable<Double> getOrdinateAt(final int index) throws IndexOutOfBoundsException {
            return ordinates[index];
        }

        /**
         * Not needed for this test.
         */
        @Override
        public Range<Double> getOrdinateRangeAt(int index) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Tests {@link IrregularAxesConverter#canConvert}.
     * Latitude and longitude data are extracted from a dump of a Coriolis NetCDF file.
     */
    @Test
    @Ignore
    public void testCanConvert() {
        final Axis longitudes = new Axis(
                  -179.5, -179, -178.5, -178, -177.5, -177, -176.5, -176, -175.5,
            -175, -174.5, -174, -173.5, -173, -172.5, -172, -171.5, -171, -170.5,
            -170, -169.5, -169, -168.5, -168, -167.5, -167, -166.5, -166, -165.5,
            -165, -164.5, -164, -163.5, -163, -162.5, -162, -161.5, -161, -160.5,
            -160, -159.5, -159, -158.5, -158, -157.5, -157, -156.5, -156, -155.5,
            -155, -154.5, -154, -153.5, -153, -152.5, -152, -151.5, -151, -150.5,
            -150, -149.5, -149, -148.5, -148, -147.5, -147, -146.5, -146, -145.5,
            -145, -144.5, -144, -143.5, -143, -142.5, -142, -141.5, -141, -140.5,
            -140, -139.5, -139, -138.5, -138, -137.5, -137, -136.5, -136, -135.5,
            -135, -134.5, -134, -133.5, -133, -132.5, -132, -131.5, -131, -130.5,
            -130, -129.5, -129, -128.5, -128, -127.5, -127, -126.5, -126, -125.5,
            -125, -124.5, -124, -123.5, -123, -122.5, -122, -121.5, -121, -120.5,
            -120, -119.5, -119, -118.5, -118, -117.5, -117, -116.5, -116, -115.5,
            -115, -114.5, -114, -113.5, -113, -112.5, -112, -111.5, -111, -110.5,
            -110, -109.5, -109, -108.5, -108, -107.5, -107, -106.5, -106, -105.5,
            -105, -104.5, -104, -103.5, -103, -102.5, -102, -101.5, -101, -100.5,
            -100, -99.5, -99, -98.5, -98, -97.5, -97, -96.5, -96, -95.5, -95, -94.5,
            -94, -93.5, -93, -92.5, -92, -91.5, -91, -90.5, -90, -89.5, -89, -88.5,
            -88, -87.5, -87, -86.5, -86, -85.5, -85, -84.5, -84, -83.5, -83, -82.5,
            -82, -81.5, -81, -80.5, -80, -79.5, -79, -78.5, -78, -77.5, -77, -76.5,
            -76, -75.5, -75, -74.5, -74, -73.5, -73, -72.5, -72, -71.5, -71, -70.5,
            -70, -69.5, -69, -68.5, -68, -67.5, -67, -66.5, -66, -65.5, -65, -64.5,
            -64, -63.5, -63, -62.5, -62, -61.5, -61, -60.5, -60, -59.5, -59, -58.5,
            -58, -57.5, -57, -56.5, -56, -55.5, -55, -54.5, -54, -53.5, -53, -52.5,
            -52, -51.5, -51, -50.5, -50, -49.5, -49, -48.5, -48, -47.5, -47, -46.5,
            -46, -45.5, -45, -44.5, -44, -43.5, -43, -42.5, -42, -41.5, -41, -40.5,
            -40, -39.5, -39, -38.5, -38, -37.5, -37, -36.5, -36, -35.5, -35, -34.5,
            -34, -33.5, -33, -32.5, -32, -31.5, -31, -30.5, -30, -29.5, -29, -28.5,
            -28, -27.5, -27, -26.5, -26, -25.5, -25, -24.5, -24, -23.5, -23, -22.5,
            -22, -21.5, -21, -20.5, -20, -19.5, -19, -18.5, -18, -17.5, -17, -16.5,
            -16, -15.5, -15, -14.5, -14, -13.5, -13, -12.5, -12, -11.5, -11, -10.5,
            -10, -9.5, -9, -8.5, -8, -7.5, -7, -6.5, -6, -5.5, -5, -4.5, -4, -3.5,
            -3, -2.5, -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5,
            5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13,
            13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18, 18.5, 19, 19.5, 20,
            20.5, 21, 21.5, 22, 22.5, 23, 23.5, 24, 24.5, 25, 25.5, 26, 26.5, 27,
            27.5, 28, 28.5, 29, 29.5, 30, 30.5, 31, 31.5, 32, 32.5, 33, 33.5, 34,
            34.5, 35, 35.5, 36, 36.5, 37, 37.5, 38, 38.5, 39, 39.5, 40, 40.5, 41,
            41.5, 42, 42.5, 43, 43.5, 44, 44.5, 45, 45.5, 46, 46.5, 47, 47.5, 48,
            48.5, 49, 49.5, 50, 50.5, 51, 51.5, 52, 52.5, 53, 53.5, 54, 54.5, 55,
            55.5, 56, 56.5, 57, 57.5, 58, 58.5, 59, 59.5, 60, 60.5, 61, 61.5, 62,
            62.5, 63, 63.5, 64, 64.5, 65, 65.5, 66, 66.5, 67, 67.5, 68, 68.5, 69,
            69.5, 70, 70.5, 71, 71.5, 72, 72.5, 73, 73.5, 74, 74.5, 75, 75.5, 76,
            76.5, 77, 77.5, 78, 78.5, 79, 79.5, 80, 80.5, 81, 81.5, 82, 82.5, 83,
            83.5, 84, 84.5, 85, 85.5, 86, 86.5, 87, 87.5, 88, 88.5, 89, 89.5, 90,
            90.5, 91, 91.5, 92, 92.5, 93, 93.5, 94, 94.5, 95, 95.5, 96, 96.5, 97,
            97.5, 98, 98.5, 99, 99.5, 100, 100.5, 101, 101.5, 102, 102.5, 103, 103.5,
            104, 104.5, 105, 105.5, 106, 106.5, 107, 107.5, 108, 108.5, 109, 109.5,
            110, 110.5, 111, 111.5, 112, 112.5, 113, 113.5, 114, 114.5, 115, 115.5,
            116, 116.5, 117, 117.5, 118, 118.5, 119, 119.5, 120, 120.5, 121, 121.5,
            122, 122.5, 123, 123.5, 124, 124.5, 125, 125.5, 126, 126.5, 127, 127.5,
            128, 128.5, 129, 129.5, 130, 130.5, 131, 131.5, 132, 132.5, 133, 133.5,
            134, 134.5, 135, 135.5, 136, 136.5, 137, 137.5, 138, 138.5, 139, 139.5,
            140, 140.5, 141, 141.5, 142, 142.5, 143, 143.5, 144, 144.5, 145, 145.5,
            146, 146.5, 147, 147.5, 148, 148.5, 149, 149.5, 150, 150.5, 151, 151.5,
            152, 152.5, 153, 153.5, 154, 154.5, 155, 155.5, 156, 156.5, 157, 157.5,
            158, 158.5, 159, 159.5, 160, 160.5, 161, 161.5, 162, 162.5, 163, 163.5,
            164, 164.5, 165, 165.5, 166, 166.5, 167, 167.5, 168, 168.5, 169, 169.5,
            170, 170.5, 171, 171.5, 172, 172.5, 173, 173.5, 174, 174.5, 175, 175.5,
            176, 176.5, 177, 177.5, 178, 178.5, 179, 179.5, 180
        );
        final Axis latitudes = new Axis(
            -77.01048, -76.89761, -76.78378, -76.66898, -76.55320, -76.43644,
            -76.31867, -76.19991, -76.08014, -75.95934, -75.83752, -75.71467,
            -75.59077, -75.46582, -75.33981, -75.21273, -75.08458, -74.95534,
            -74.82500, -74.69357, -74.56102, -74.42735, -74.29256, -74.15662,
            -74.01955, -73.88131, -73.74191, -73.60134, -73.45959, -73.31665,
            -73.17251, -73.02715, -72.88058, -72.73279, -72.58376, -72.43347,
            -72.28194, -72.12914, -71.97506, -71.81970, -71.66305, -71.50510,
            -71.34583, -71.18523, -71.02332, -70.86005, -70.69543, -70.52946,
            -70.36211, -70.19337, -70.02325, -69.85174, -69.67880, -69.50445,
            -69.32867, -69.15144, -68.97276, -68.79263, -68.61102, -68.42793,
            -68.24334, -68.05725, -67.86966, -67.68053, -67.48988, -67.29768,
            -67.10394, -66.90862, -66.71173, -66.51326, -66.31319, -66.11152,
            -65.90823, -65.70332, -65.49677, -65.28857, -65.07871, -64.86720,
            -64.65399, -64.43910, -64.22252, -64.00423, -63.78421, -63.56247,
            -63.33899, -63.11375, -62.88676, -62.65800, -62.42746, -62.19513,
            -61.96099, -61.72505, -61.48729, -61.24769, -61.00626, -60.76297,
            -60.51783, -60.27082, -60.02193, -59.77115, -59.51847, -59.26389,
            -59.00738, -58.74895, -58.48859, -58.22628, -57.96202, -57.69580,
            -57.42760, -57.15743, -56.88527, -56.61111, -56.33495, -56.05677,
            -55.77657, -55.49435, -55.21008, -54.92377, -54.63541, -54.34499,
            -54.05251, -53.75795, -53.46131, -53.16258, -52.86176, -52.55884,
            -52.25381, -51.94667, -51.63742, -51.32603, -51.01253, -50.69688,
            -50.37910, -50.05918, -49.73710, -49.41288, -49.08650, -48.75796, -48.42726,
            -48.09439, -47.75935, -47.42214, -47.08276, -46.74120, -46.39746,
            -46.05155, -45.70345, -45.35318, -45.00072, -44.64608, -44.28926,
            -43.93025, -43.56907, -43.20571, -42.84017, -42.47246, -42.10257,
            -41.73051, -41.35629, -40.97990, -40.60135, -40.22064, -39.83779,
            -39.45279, -39.06564, -38.67636, -38.28495, -37.89143, -37.49578,
            -37.09803, -36.69818, -36.29624, -35.89222, -35.48612, -35.07796,
            -34.66775, -34.25550, -33.84122, -33.42492, -33.00661, -32.58632,
            -32.16404, -31.73980, -31.31360, -30.88546, -30.45541, -30.02345,
            -29.58959, -29.15387, -28.71628, -28.27686, -27.83562, -27.39258,
            -26.94775, -26.50117, -26.05284, -25.60278, -25.15103, -24.69760,
            -24.24251, -23.78579, -23.32746, -22.86754, -22.40606, -21.94304,
            -21.47852, -21.01250, -20.54502, -20.07611, -19.60579, -19.13410,
            -18.66105, -18.18668, -17.71101, -17.23409, -16.75592, -16.27655,
            -15.79601, -15.31433, -14.83153, -14.34766, -13.86273, -13.37679,
            -12.88987, -12.40200, -11.91322, -11.42355, -10.93304, -10.44172,
            -9.949614, -9.456768, -8.963216, -8.468991, -7.974132, -7.478673,
            -6.982651, -6.486102, -5.989064, -5.491573, -4.993666, -4.495381,
            -3.996755, -3.497825, -2.998630, -2.499207, -1.999594, -1.499829,
            -0.9999492, -0.4999937, 0, 0.4999937, 0.9999492, 1.499829, 1.999594,
            2.499207, 2.998630, 3.497825, 3.996755, 4.495381, 4.993666, 5.491573,
            5.989064, 6.486102, 6.982651, 7.478673, 7.974132, 8.468991, 8.963216,
            9.456768, 9.949614, 10.44172, 10.93304, 11.42355, 11.91322, 12.40200,
            12.88987, 13.37679, 13.86273, 14.34766, 14.83153, 15.31433, 15.79601,
            16.27655, 16.75592, 17.23409, 17.71101, 18.18668, 18.66105, 19.13410,
            19.60579, 20.07611, 20.54502, 21.01250, 21.47852, 21.94304, 22.40606,
            22.86754, 23.32746, 23.78579, 24.24251, 24.69760, 25.15103, 25.60278,
            26.05284, 26.50117, 26.94775, 27.39258, 27.83562, 28.27686, 28.71628,
            29.15387, 29.58959, 30.02345, 30.45541, 30.88546, 31.31360, 31.73980,
            32.16404, 32.58632, 33.00661, 33.42492, 33.84122, 34.25550, 34.66775,
            35.07796, 35.48612, 35.89222, 36.29624, 36.69818, 37.09803, 37.49578,
            37.89143, 38.28495, 38.67636, 39.06564, 39.45279, 39.83779, 40.22064,
            40.60135, 40.97990, 41.35629, 41.73051, 42.10257, 42.47246, 42.84017,
            43.20571, 43.56907, 43.93025, 44.28926, 44.64608, 45.00072, 45.35318,
            45.70345, 46.05155, 46.39746, 46.74120, 47.08276, 47.42214, 47.75935,
            48.09439, 48.42726, 48.75796, 49.08650, 49.41288, 49.73710, 50.05918,
            50.37910, 50.69688, 51.01253, 51.32603, 51.63742, 51.94667, 52.25381,
            52.55884, 52.86176, 53.16258, 53.46131, 53.75795, 54.05251, 54.34499,
            54.63541, 54.92377, 55.21008, 55.49435, 55.77657, 56.05677, 56.33495,
            56.61111, 56.88527, 57.15743, 57.42760, 57.69580, 57.96202, 58.22628,
            58.48859, 58.74895, 59.00738, 59.26389, 59.51847, 59.77115, 60.02193,
            60.27082, 60.51783, 60.76297, 61.00626, 61.24769, 61.48729, 61.72505,
            61.96099, 62.19513, 62.42746, 62.65800, 62.88676, 63.11375, 63.33899,
            63.56247, 63.78421, 64.00423, 64.22252, 64.43910, 64.65399, 64.8672,
            65.07871, 65.28857, 65.49677, 65.70332, 65.90823, 66.11152, 66.31319,
            66.51326, 66.71173, 66.90862, 67.10394, 67.29768, 67.48988, 67.68053,
            67.86966, 68.05725, 68.24334, 68.42793, 68.61102, 68.79263, 68.97276,
            69.15144, 69.32867, 69.50445, 69.67880, 69.85174, 70.02325, 70.19337,
            70.36211, 70.52946, 70.69543, 70.86005, 71.02332, 71.18523, 71.34583,
            71.50510, 71.66305, 71.81970, 71.97506, 72.12914, 72.28194, 72.43347,
            72.58376, 72.73279, 72.88058, 73.02715, 73.17251, 73.31665, 73.45959,
            73.60134, 73.74191, 73.88131, 74.01955, 74.15662, 74.29256, 74.42735,
            74.56102, 74.69357, 74.82500, 74.95534, 75.08458, 75.21273, 75.33981,
            75.46582, 75.59077, 75.71467, 75.83752, 75.95934, 76.08014, 76.19991,
            76.31867, 76.43644, 76.55320, 76.66898, 76.78378, 76.89761, 77.01048
        );
        /*
         * No match should be found for the WGS84 CRS, because the above latitudes were
         * computed using spherical formulas instead than spherical ones. We expect a
         * match for the sphere CRS instead.
         *
         * Note: we use 1E-4 as a threshold. This is slightly stricter than the NetCDF 4.1
         * library, where the thresholds for the CoordinateAxis1D.isRegular() method is 5E-3.
         */
        final IrregularAxesConverter converter = new IrregularAxesConverter(1E-4, null);
        assertNull(converter.canConvert(CommonCRS.WGS84.normalizedGeographic(), longitudes, latitudes));
        final ProjectedCRS result = converter.canConvert(CommonCRS.SPHERE.normalizedGeographic(), longitudes, latitudes);
        assertNotNull("When using the sphere CRS, a regular grid should have been found.", result);
        assertEquals("Mercator_1SP", result.getConversionFromBase().getParameterValues().getDescriptor().getName().getCode());
        /*
         * Try again while letting IrregularAxisConverter makes its own iteration over
         * candidate source CRS.
         */
        final ProjectedCRS again = converter.canConvert(longitudes, latitudes);
        assertNotSame(result, again);
        assertEquals(result, again);
        assertEquals(result.hashCode(), again.hashCode());
        /*
         * Inspect the grid geometry.
         */
        assertTrue(result.getClass().getName(), result instanceof GridGeometry);
        final GridGeometry geometry = (GridGeometry) result;
        final GridEnvelope range = geometry.getExtent();
        assertArrayEquals("GridEnvelope low",  new int[] {  0,   0}, range.getLow ().getCoordinateValues());
        assertArrayEquals("GridEnvelope high", new int[] {719, 498}, range.getHigh().getCoordinateValues());
        final Matrix gridToCRS = ((LinearTransform) geometry.getGridToCRS()).getMatrix();
        assertTrue(Matrices.equals(gridToCRS, new Matrix3(55597, 0, -19959489, 0, 55597, -13843771, 0, 0, 1), 1, false));
    }
}
