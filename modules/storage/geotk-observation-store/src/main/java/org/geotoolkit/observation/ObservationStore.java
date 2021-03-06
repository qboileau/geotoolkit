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

package org.geotoolkit.observation;

import java.util.List;
import java.util.Set;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.sos.netcdf.ExtractionResult;
import org.geotoolkit.sos.netcdf.ExtractionResult.ProcedureTree;
import org.geotoolkit.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class ObservationStore extends DataStore {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    public abstract ParameterValueGroup getConfiguration();

    public abstract Set<Name> getProcedureNames();

    public abstract List<ProcedureTree> getProcedures() throws DataStoreException;

    public abstract Set<String> getPhenomenonNames();

    public abstract TemporalGeometricPrimitive getTemporalBounds() throws DataStoreException;

    public abstract ExtractionResult getResults() throws DataStoreException;

    public abstract ExtractionResult getResults(final List<String> sensorIds) throws DataStoreException;

    public abstract ExtractionResult getResults(final String affectedSensorID, final List<String> sensorIds) throws DataStoreException;

    /**
     * Return an Observation Reader on the data.
     *
     * @return An Observation Reader.
     */
    public abstract ObservationReader getReader();
}
