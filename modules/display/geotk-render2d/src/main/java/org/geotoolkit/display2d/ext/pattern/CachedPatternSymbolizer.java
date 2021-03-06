/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.display2d.ext.pattern;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.coverage.coveragetovector.CoverageToVectorDescriptor;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedPatternSymbolizer extends CachedSymbolizer<PatternSymbolizer>{

    public CachedPatternSymbolizer(final PatternSymbolizer symbol,
            final SymbolizerRendererService<PatternSymbolizer,? extends CachedSymbolizer<PatternSymbolizer>> renderer){
        super(symbol,renderer);
    }

    public Map<SimpleFeature,List<CachedSymbolizer>> getMasks(final GridCoverage2D coverage) throws IOException, TransformException{
        final Map<SimpleFeature,List<CachedSymbolizer>> features = new LinkedHashMap<SimpleFeature, List<CachedSymbolizer>>();
        final Map<NumberRange,List<CachedSymbolizer>> styles = new LinkedHashMap<NumberRange, List<CachedSymbolizer>>();
        final Map<Expression, List<Symbolizer>> categorizes = styleElement.getRanges();
        final Expression[] steps = categorizes.keySet().toArray(new Expression[categorizes.size()]);
        Arrays.sort(steps, new Comparator<Expression>() {

            @Override
            public int compare(Expression t1, Expression t2) {
                if(t1 == null){
                    return -1;
                }else if(t2 == null){
                    return +1;
                }

                double d1 = t1.evaluate(null, Double.class);
                double d2 = t2.evaluate(null, Double.class);
                double res = d1-d2;
                if(res < 0){
                    return -1;
                }else if(res > 0){
                    return +1;
                }else{
                    return 0;
                }
            }
        });

        //fill the numberranges ------------------------------------------------
        double last = Double.NEGATIVE_INFINITY;
        double end = Double.POSITIVE_INFINITY;
        NumberRange interval;
        int i=0;
        for(;i<steps.length-1;i++){
            end = steps[i+1].evaluate(null,Double.class);
            interval = NumberRange.create(last, true, end, false);
            styles.put(interval, getCached(categorizes.get(steps[i])));
            last = end;
        }

        //last element
        end = Double.POSITIVE_INFINITY;
        styles.put(NumberRange.create(last, true, end, true),
                getCached(categorizes.get(steps[i])) );

        //calculate the polygons -----------------------------------------------
        final ProcessDescriptor descriptor = CoverageToVectorDescriptor.INSTANCE;

        final Integer band = styleElement.getChannel().evaluate(null,Integer.class);

        final ParameterValueGroup input = descriptor.getInputDescriptor().createValue();
        input.parameter(CoverageToVectorDescriptor.COVERAGE.getName().getCode()).setValue(coverage);
        final Set<NumberRange> nrs = styles.keySet();
        input.parameter(CoverageToVectorDescriptor.RANGES.getName().getCode()).setValue(nrs.toArray(new NumberRange[nrs.size()]));
        input.parameter(CoverageToVectorDescriptor.BAND.getName().getCode()).setValue(band);
        final Process process = descriptor.createProcess(input);

        final Geometry[] polygons;
        try {
            polygons = (Geometry[]) process.call().parameter(
                CoverageToVectorDescriptor.GEOMETRIES.getName().getCode()).getValue();
        } catch (ProcessException ex) {
            Logger.getLogger(CachedPatternSymbolizer.class.getName()).log(Level.WARNING, null, ex);
            return features;
        }


        //build the features ---------------------------------------------------
        final FeatureTypeBuilder sftBuilder = new FeatureTypeBuilder();
        final String geometryField = "geometry";
        sftBuilder.setName("DynamicFeature");
        sftBuilder.add(geometryField, Geometry.class, coverage.getCoordinateReferenceSystem());
        sftBuilder.setDefaultGeometry(geometryField);
        final SimpleFeatureType sft = sftBuilder.buildSimpleFeatureType();

        final SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(sft);
        int id = 0;
        for(Geometry entry : polygons){
            sfBuilder.reset();
            sfBuilder.set(geometryField, entry);
            final SimpleFeature sf = sfBuilder.buildFeature(String.valueOf(id++));

            features.put(sf, styles.get(entry.getUserData()));
        }

        return features;
    }

    private static List<CachedSymbolizer> getCached(final List<Symbolizer> symbols){
        final List<CachedSymbolizer> cached = new ArrayList<CachedSymbolizer>();

        if(symbols != null){
            for(final Symbolizer sy : symbols){
                cached.add(GO2Utilities.getCached(sy,null));
            }
        }

        return cached;
    }

    @Override
    public float getMargin(final Object candidate, final float coeff) {
        return 0;
    }

    @Override
    protected void evaluate() {
    }

    @Override
    public boolean isVisible(final Object candidate) {
        return false;
    }

}
