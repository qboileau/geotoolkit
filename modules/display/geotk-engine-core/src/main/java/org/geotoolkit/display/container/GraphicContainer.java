/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display.container;

import org.geotoolkit.display.primitive.SceneNode;

/**
 * A Graphic Container holds a scene definition.
 * The scene tree might not be modifiable or dynamic.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface GraphicContainer<G extends SceneNode> {
    
    /**
     * Get the root scene node.
     * @return SceneNode, can be null.
     */
    SceneNode getRoot();
    
    /**
     * Set the root scene node.
     * @param node SceneNode, can be null.
     */
    void setRoot(SceneNode node);
    
}
