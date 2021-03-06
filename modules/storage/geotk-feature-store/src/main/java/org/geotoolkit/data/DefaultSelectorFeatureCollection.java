/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.util.collection.CloseableIterator;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.Envelope;

/**
 * Feature collection that takes it's source from a single selector.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSelectorFeatureCollection extends AbstractFeatureCollection<Feature>{

    private final Query query;

    public DefaultSelectorFeatureCollection(final String id, final Query query){
        super(id,query.getSource());

        if(!(query.getSource() instanceof Selector)){
            throw new IllegalArgumentException("Query must have a selector source.");
        }

        if(!QueryUtilities.isAbsolute(query.getSource())){
            throw new IllegalArgumentException("Selector must be absolute.");
        }

        this.query = query;
    }

    @Override
    public Selector getSource() {
        return (Selector) super.getSource();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<Feature> subCollection(final Query query) throws DataStoreException {
        return getSession().getFeatureCollection(QueryUtilities.subQuery(this.query, query));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType() throws FeatureStoreRuntimeException{
        try {
            FeatureType ft = getSession().getFeatureStore().getFeatureType(query.getTypeName());
            ft = FeatureTypeUtilities.createSubType(ft, query.getPropertyNames(), query.getCoordinateSystemReproject());

            final Boolean hide = (Boolean) query.getHints().get(HintsPending.FEATURE_HIDE_ID_PROPERTY);
            if(hide != null && hide){
                ft = FeatureTypeUtilities.excludePrimaryKeyFields(ft);
            }

            return ft;
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (SchemaException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<Feature> iterator(final Hints hints) throws FeatureStoreRuntimeException{

        final Query iteQuery;
        if(hints != null){
            final QueryBuilder qb = new QueryBuilder(this.query);
            final Hints hts = new Hints(this.query.getHints());
            hts.add(hints);
            qb.setHints(hts);
            iteQuery = qb.buildQuery();
        }else{
            iteQuery = this.query;
        }

        try {
            return getSession().getFeatureIterator(iteQuery);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() throws FeatureStoreRuntimeException {
        try {
            return (int) getSession().getCount(query);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() throws FeatureStoreRuntimeException{
        try {
            return getSession().getEnvelope(query);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(final Feature e) {
        return addAll(Collections.singletonList(e));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAll(final Collection<? extends Feature> clctn) {
        try {
            getSession().addFeatures(query.getTypeName(), clctn);
            return true;
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public boolean isWritable(){
        try {
            return getSession().getFeatureStore().isWritable(query.getTypeName());
        } catch (DataStoreException ex) {
            Logger.getLogger(DefaultSelectorFeatureCollection.class.getName()).log(Level.WARNING, null, ex);
            return false;
        }
    }

    @Override
    public boolean remove(final Object o) throws FeatureStoreRuntimeException{

        if(isWritable()){
            if(o instanceof Feature){
                Id filter = FactoryFinder.getFilterFactory(null).id(Collections.singleton(((Feature)o).getIdentifier()));
                try {
                    getSession().removeFeatures(query.getTypeName(), filter);
                    return true;
                } catch (DataStoreException ex) {
                    throw new FeatureStoreRuntimeException(ex);
                }
            }else{
                //trying to remove an object which is not a feature
                //it has no effect
                //should we be strict and raise an error ? or log it ?
            }

        }else{
            throw new FeatureStoreRuntimeException("this collection is readable only");
        }
        return false;
    }

    @Override
    public boolean removeAll(final Collection<?> clctn) {

        if(isWritable()){
            final Set<Identifier> ids = new HashSet<Identifier>();

            final Iterator<?> ite = clctn.iterator();
            try{
                while(ite.hasNext()){
                    final Object o = ite.next();
                    if(o instanceof Feature){
                        ids.add(((Feature)o).getIdentifier());
                    }
                }
            }finally{
                if(ite instanceof CloseableIterator){
                    ((CloseableIterator)ite).close();
                }
            }

            if(!ids.isEmpty()){
                Id filter = FactoryFinder.getFilterFactory(null).id(ids);
                try {
                    getSession().removeFeatures(query.getTypeName(), filter);
                    return true;
                } catch (DataStoreException ex) {
                    throw new FeatureStoreRuntimeException(ex);
                }
            }

        }else{
            throw new FeatureStoreRuntimeException("this collection is readable only");
        }
        return false;
    }

    @Override
    public void clear() {

        if(isWritable()){
            try {
                getSession().removeFeatures(query.getTypeName(), query.getFilter());
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        }else{
            throw new FeatureStoreRuntimeException("this collection is readable only");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(final Filter filter, final Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        if(filter == Filter.INCLUDE){
            getSession().updateFeatures(query.getTypeName(),query.getFilter(),values);
        }else{
            getSession().updateFeatures(query.getTypeName(),FactoryFinder.getFilterFactory(null).and(query.getFilter(), filter),values);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove(final Filter filter) throws DataStoreException {
        if(filter == Filter.INCLUDE){
            getSession().removeFeatures(query.getTypeName(),query.getFilter());
        }else{
            getSession().removeFeatures(query.getTypeName(),FactoryFinder.getFilterFactory(null).and(query.getFilter(), filter));
        }
    }

}
