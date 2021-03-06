/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.measure.unit.NonSI;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPointSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPointSymbolizer>{

    public DefaultPointSymbolizerRenderer(final SymbolizerRendererService service,final CachedPointSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry();

        //could not find the border geometry
        if(projectedGeometry == null) return;

        portray(projectedGeometry, null);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedObject projectedFeature) throws PortrayalException{

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(!symbol.isVisible(candidate)) return;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

        portray(projectedGeometry, candidate);

    }

    private void portray(final ProjectedGeometry projectedGeometry, Object candidate) throws PortrayalException{

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return;

        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        //we switch to  more appropriate context CRS for rendering ---------
        // a point symbolis always paint in display unit -------------------
        renderingContext.switchToDisplayCRS();

        //we adjust coefficient for rendering ------------------------------
        float coeff;
        if(symbolUnit.equals(NonSI.PIXEL)){
            //symbol is in display unit
            coeff = 1;
        }else{
            //we have a special unit we must adjust the coefficient
            coeff = renderingContext.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff *= Math.abs(XAffineTransform.getScale(inverse));
        }

        //create the image--------------------------------------------------
        final BufferedImage img = symbol.getImage(candidate,coeff,hints);

        if(img == null){
            //may be correct, image can be too small for rendering
            return;
        }

        final float[] disps = new float[2];
        final float[] anchor = new float[2];
        symbol.getDisplacement(candidate,disps);
        symbol.getAnchor(candidate,anchor);
        disps[0] *= coeff ;
        disps[1] *= coeff ;

        final Geometry[] geoms;
        try {
            geoms = projectedGeometry.getDisplayGeometryJTS();
        } catch (TransformException ex) {
            throw new PortrayalException("Could not calculate display projected geometry",ex);
        }

        if(geoms == null){
            //no geometry
            return;
        }

        final double rot = AffineTransforms2D.getRotation(renderingContext.getObjectiveToDisplay());

        for(Geometry geom : geoms){
            if(rot==0){
                if(geom instanceof Point || geom instanceof MultiPoint){
                    //TODO use generalisation on multipoints

                    final Coordinate[] coords = geom.getCoordinates();
                    for(int i=0, n = coords.length; i<n ; i++){
                        final Coordinate coord = coords[i];
                        final int x = (int) (-img.getWidth()*anchor[0] + coord.x + disps[0]);
                        final int y = (int) (-img.getHeight()*anchor[1] + coord.y - disps[1]);
                        g2d.drawImage(img, x, y, null);
                    }

                }else{
                    //get most appropriate point
                    final Point pt2d = GO2Utilities.getBestPoint(geom);
                    if(pt2d == null || pt2d.isEmpty()){
                        //no geometry
                        return;
                    }

                    Coordinate pcoord = pt2d.getCoordinate();
                    if(Double.isNaN(pcoord.x)){
                        pcoord = geom.getCoordinate();
                    }

                    final int x = (int) (-img.getWidth()*anchor[0] + pcoord.x + disps[0]);
                    final int y = (int) (-img.getHeight()*anchor[1] + pcoord.y - disps[1]);
                    g2d.drawImage(img, x, y, null);
                }
            }else{
                if(geom instanceof Point || geom instanceof MultiPoint){
                    //TODO use generalisation on multipoints

                    final Coordinate[] coords = geom.getCoordinates();
                    for(int i=0, n = coords.length; i<n ; i++){
                        final Coordinate coord = coords[i];

                        final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
                        final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);
                        final AffineTransform ptrs = new AffineTransform();
                        ptrs.rotate(-rot);
                        ptrs.preConcatenate(new AffineTransform(1, 0, 0, 1, coord.x, coord.y));
                        ptrs.concatenate(new AffineTransform(1, 0, 0, 1, postx, posty));

                        g2d.drawImage(img, ptrs, null);
                    }
                }else{
                    //get most appropriate point
                    final Point pt2d = GO2Utilities.getBestPoint(geom);
                    if(pt2d == null || pt2d.isEmpty()){
                        //no geometry
                        return;
                    }

                    Coordinate pcoord = pt2d.getCoordinate();
                    if(Double.isNaN(pcoord.x)){
                        pcoord = geom.getCoordinate();
                    }

                    final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
                    final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);
                    final AffineTransform ptrs = new AffineTransform();
                    ptrs.rotate(-rot);
                    ptrs.preConcatenate(new AffineTransform(1, 0, 0, 1, pcoord.x, pcoord.y));
                    ptrs.concatenate(new AffineTransform(1, 0, 0, 1, postx, posty));

                    g2d.drawImage(img, ptrs, null);
                }
            }
        }

    }

    @Override
    public void portray(final Iterator<? extends ProjectedObject> graphics) throws PortrayalException {

        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        //we switch to  more appropriate context CRS for rendering ---------
        // a point symbolis always paint in display unit -------------------
        renderingContext.switchToDisplayCRS();
        //we adjust coefficient for rendering ------------------------------
        float coeff;
        if(symbolUnit.equals(NonSI.PIXEL)){
            //symbol is in display unit
            coeff = 1;
        }else{
            //we have a special unit we must adjust the coefficient
            coeff = renderingContext.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff *= Math.abs(XAffineTransform.getScale(inverse));
        }

        //caches
        ProjectedObject projectedobj;
        Object candidate;
        final float[] disps = new float[2];
        final float[] anchor = new float[2];
        final AffineTransform imgTrs = new AffineTransform();

        final double rot = AffineTransforms2D.getRotation(renderingContext.getObjectiveToDisplay());
        final AffineTransform mapRotationTrs = new AffineTransform();
        mapRotationTrs.rotate(-rot);

        while(graphics.hasNext()){
            if(monitor.stopRequested()) return;

            projectedobj = graphics.next();
            candidate = projectedobj.getCandidate();

            //test if the symbol is visible on this feature
            if(!symbol.isVisible(candidate)) continue;

            final ProjectedGeometry projectedGeometry = projectedobj.getGeometry(geomPropertyName);

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) continue;

            //create the image--------------------------------------------------
            final BufferedImage img = symbol.getImage(candidate,coeff,hints);

            if(img == null) throw new PortrayalException("A null image has been generated by a Mark symbol.");

            symbol.getDisplacement(candidate,disps);
            symbol.getAnchor(candidate,anchor);
            disps[0] *= coeff ;
            disps[1] *= coeff ;

            final Geometry[] geoms;
            try {
                geoms = projectedGeometry.getDisplayGeometryJTS();
            } catch (TransformException ex) {
                throw new PortrayalException("Could not calculate display projected geometry",ex);
            }

            for(Geometry geom : geoms){

                if(geom instanceof Point || geom instanceof MultiPoint){

                    //TODO use generalisation on multipoints

                    final Coordinate[] coords = geom.getCoordinates();
                    for(int i=0, n = coords.length; i<n ; i++){
                        final Coordinate coord = coords[i];
                        if(rot==0){
                            imgTrs.setToTranslation(
                                    -img.getWidth()*anchor[0] + coord.x + disps[0],
                                    -img.getHeight()*anchor[1] + coord.y - disps[1]);
                            g2d.drawRenderedImage(img, imgTrs);
                        }else{
                            final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
                            final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);
                            final AffineTransform ptrs = new AffineTransform(mapRotationTrs);
                            ptrs.preConcatenate(new AffineTransform(1, 0, 0, 1, coord.x, coord.y));
                            ptrs.concatenate(new AffineTransform(1, 0, 0, 1, postx, posty));
                            g2d.drawImage(img, ptrs, null);
                        }
                    }

                }else if(geom!=null){

                    //get most appropriate point
                    final Point pt2d = GO2Utilities.getBestPoint(geom);
                    if(pt2d == null || pt2d.isEmpty()){
                        //no geometry
                        return;
                    }
                    Coordinate pcoord = pt2d.getCoordinate();
                    if(Double.isNaN(pcoord.x)){
                        pcoord = geom.getCoordinate();
                    }
                    if(rot==0){
                        imgTrs.setToTranslation(
                                    -img.getWidth()*anchor[0] + pcoord.x + disps[0],
                                    -img.getHeight()*anchor[1] + pcoord.y - disps[1]);
                        g2d.drawRenderedImage(img, imgTrs);
                    }else{
                        final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
                        final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);
                        final AffineTransform ptrs = new AffineTransform(mapRotationTrs);
                        ptrs.preConcatenate(new AffineTransform(1, 0, 0, 1, pcoord.x, pcoord.y));
                        ptrs.concatenate(new AffineTransform(1, 0, 0, 1, postx, posty));
                        g2d.drawImage(img, ptrs, null);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedObject projectedFeature, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(candidate))) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        //we adjust coefficient for rendering ----------------------------------
        float coeff = 1;
        if(symbolUnit.equals(NonSI.PIXEL)){
            //symbol is in display unit
            coeff = 1;
        }else{
            //we have a special unit we must adjust the coefficient
            coeff = renderingContext.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff *= Math.abs(XAffineTransform.getScale(inverse));
        }

        //create the image------------------------------------------------------
        final BufferedImage img = symbol.getImage(candidate,coeff,null);
        final float[] disps = new float[2];
        symbol.getDisplacement(candidate,disps);
        disps[0] *= coeff ;
        disps[1] *= coeff ;

        final float[] anchor = new float[2];
        symbol.getAnchor(candidate,anchor);

        final Geometry[] geoms;
        try {
            geoms = projectedGeometry.getDisplayGeometryJTS();
        } catch (TransformException ex) {
            ex.printStackTrace();
            return false;
        }

        for(Geometry geom : geoms){
            if(geom instanceof Point || geom instanceof MultiPoint){

                //TODO use generalisation on multipoints

                final Coordinate[] coords = geom.getCoordinates();
                for(int i=0, n = coords.length; i<n ; i++){
                    final Coordinate coord = coords[i];
                    final int x = (int) (-img.getWidth()*anchor[0] + coord.x + disps[0]);
                    final int y = (int) (-img.getHeight()*anchor[1] + coord.y - disps[1]);

                    switch(filter){
                        case INTERSECTS :
                            if(mask.intersects(x,y,img.getWidth(),img.getHeight())){
                                //TODO should make a better test for the alpha pixel values in image
                                return true;
                            }
                            break;
                        case WITHIN :
                            if(mask.contains(x,y,img.getWidth(),img.getHeight())){
                                //TODO should make a better test for the alpha pixel values in image
                                return true;
                            }
                            break;
                    }

                }

            }else{
                //get most appropriate point
                final Point pt2d = GO2Utilities.getBestPoint(geom);
                Coordinate pcoord = pt2d.getCoordinate();
                if(Double.isNaN(pcoord.x)){
                    pcoord = geom.getCoordinate();
                }

                final int x = (int) (-img.getWidth()*anchor[0] + pcoord.x + disps[0]);
                final int y = (int) (-img.getHeight()*anchor[1] + pcoord.y - disps[1]);

                switch(filter){
                    case INTERSECTS :
                        if(mask.intersects(x,y,img.getWidth(),img.getHeight())){
                            //TODO should make a better test for the alpha pixel values in image
                            return true;
                        }
                        break;
                    case WITHIN :
                        if(mask.contains(x,y,img.getWidth(),img.getHeight())){
                            //TODO should make a better test for the alpha pixel values in image
                            return true;
                        }
                        break;
                }

            }
        }

        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedCoverage graphic, final SearchAreaJ2D mask, final VisitFilter filter) {
        return false;
    }

}
