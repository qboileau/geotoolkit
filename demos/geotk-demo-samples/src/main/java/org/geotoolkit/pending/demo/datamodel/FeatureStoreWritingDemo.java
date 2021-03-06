

package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.HashSet;
import java.util.Set;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;

public class FeatureStoreWritingDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) throws  DataStoreException {
        Demos.init();
        
        final GeometryFactory gf = new GeometryFactory();


        //start by creating a memory featurestore for this test -----------------------------
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.add("name", String.class);
        ftb.add("length", Integer.class);
        ftb.add("position", Point.class, CommonCRS.WGS84.normalizedGeographic());
        ftb.setDefaultGeometry("position");
        final SimpleFeatureType type = ftb.buildSimpleFeatureType();


        //create the featurestore ---------------------------------------------------------
        final FeatureStore store = new MemoryFeatureStore();
        store.createFeatureType(type.getName(), type);


        ////////////////////////////////////////////////////////////////////////////////
        // ADDING RECORDS //////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////

        //working directly on the featurestore --------------------------------------------
        //best performance
        final FeatureWriter writer = store.getFeatureWriterAppend(type.getName());
        Feature feature = writer.next();
        feature.getProperty("name").setValue("sam");
        feature.getProperty("length").setValue(30);
        feature.getProperty("position").setValue(gf.createPoint(new Coordinate(20, 30)));
        writer.write();

        feature = writer.next();
        feature.getProperty("name").setValue("tomy");
        feature.getProperty("length").setValue(5);
        feature.getProperty("position").setValue(gf.createPoint(new Coordinate(41, 56)));
        writer.write();

        //and so on write features ...

        writer.close();


        //passing a collection -----------------------------------------------------------
        //used to copy values from one featurestore to another
        FeatureCollection toAdd = FeatureStoreUtilities.collection("collectionID", type);

        feature = FeatureUtilities.defaultFeature(type, "");
        feature.getProperty("name").setValue("speedy");
        feature.getProperty("length").setValue(78);
        feature.getProperty("position").setValue(gf.createPoint(new Coordinate(-12, -31)));
        toAdd.add(feature);
        //and so on add features in the collection ...

        //and finally store them
        store.addFeatures(type.getName(), toAdd);


        //From a the session -----------------------------------------------------------
        final Session session = store.createSession(true);
        toAdd = FeatureStoreUtilities.collection("collectionID", type);

        feature = FeatureUtilities.defaultFeature(type, "");
        feature.getProperty("name").setValue("ginette");
        feature.getProperty("length").setValue(74);
        feature.getProperty("position").setValue(gf.createPoint(new Coordinate(56, 101)));
        toAdd.add(feature);
        //and so on add features in the collection ...

        session.addFeatures(type.getName(), toAdd);
        //so far thoses features are only visible in the session, don't forget to commit
        session.commit();


        //On a FeatureCollection like normal java ----------------------------------------
        FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(type.getName()));

        feature = FeatureUtilities.defaultFeature(type, "");
        feature.getProperty("name").setValue("marcel");
        feature.getProperty("length").setValue(125);
        feature.getProperty("position").setValue(gf.createPoint(new Coordinate(-79, 2)));
        
        col.add(feature);

        session.commit();
        
        System.out.println(col);


        ////////////////////////////////////////////////////////////////////////////////
        // REMOVING RECORDS ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////

        //on the featurestore ------------------------------------------------------------
        Set<Identifier> ids = new HashSet<Identifier>();
        ids.add(new DefaultFeatureId("Fish.1"));
        store.removeFeatures(type.getName(), FF.id(ids));

        //same thing on the session and normal java way on the collection.
        //to remove everything use
        store.removeFeatures(type.getName(), Filter.INCLUDE);

        System.out.println("Number of features = " + col.size());

    }
    
}
