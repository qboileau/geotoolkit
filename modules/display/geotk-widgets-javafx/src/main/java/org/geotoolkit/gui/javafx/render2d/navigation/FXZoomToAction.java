/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.render2d.navigation;

import java.util.Optional;
import java.util.logging.Level;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXZoomToAction extends FXMapAction {
    public static final Image ICON = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_THUMB_TACK, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    
    public FXZoomToAction(FXMap map) {
        super(map,GeotkFX.getString(FXZoomToAction.class,"zoom_at"),GeotkFX.getString(FXZoomToAction.class,"zoom_at"),ICON);
    }
    
    @Override
    public void accept(ActionEvent event) {
        if (map != null) {
            
            final Alert alert = new Alert(Alert.AlertType.NONE);
            
            final GridPane grid = new GridPane();
            grid.getColumnConstraints().add(new ColumnConstraints());
            grid.getColumnConstraints().add(new ColumnConstraints());
            grid.getRowConstraints().add(new RowConstraints());
            grid.getRowConstraints().add(new RowConstraints());
            grid.setHgap(10);
            grid.setVgap(10);
            
            final Label lblx = new Label(GeotkFX.getString(this, "lon"));
            final Label lbly = new Label(GeotkFX.getString(this, "lat"));
            
            final TextField fieldx = new TextField("0");
            final TextField fieldy = new TextField("0");
                        
            grid.add(lblx, 0, 0);
            grid.add(lbly, 0, 1);
            grid.add(fieldx, 1, 0);
            grid.add(fieldy, 1, 1);
            
            final DialogPane pane = new DialogPane();
            pane.setContent(grid);
            pane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
            alert.setDialogPane(pane);
            alert.setTitle(GeotkFX.getString(FXZoomToAction.class,"zoom_at"));
            final Optional<ButtonType> res = alert.showAndWait();
            if(ButtonType.OK.equals(res.get())){
                try {
                    final CoordinateReferenceSystem navCRS = CommonCRS.WGS84.normalizedGeographic();
                    final GeneralDirectPosition pos = new GeneralDirectPosition(navCRS);
                    pos.setOrdinate(0, Double.valueOf(fieldx.getText()));
                    pos.setOrdinate(1, Double.valueOf(fieldy.getText()));
                    map.getCanvas().setObjectiveCenter(pos);
                } catch (Exception ex) {
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            
        }
    }
    
}
