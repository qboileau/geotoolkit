
package org.geotoolkit.pending.demo.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.DefaultTreeVisitor;
import org.geotoolkit.index.tree.io.TreeReader;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.geotoolkit.index.tree.io.TreeWriter;
import org.geotoolkit.index.tree.star.StarRTree;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * R-tree uses.
 * Exist : R-Tree (BasicRTree), R*Tree (StarRTree), Hilbert R-Tree (HilbertRTree).
 *
 * R-Tree         : fast indexing time, slow search query time.
 * R*Tree         : moderate indexing time, moderate search query time.
 * Hilbert R-Tree : slow indexing time, fast search query time.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeDemo {

    public static void main(String[] args) throws TransformException, IOException, ClassNotFoundException {
        Demos.init();
        
        /**
         * Tree creation.
         */

        //Create crs which in what coordinate space tree is define--------------
        final CoordinateReferenceSystem crs = DefaultEngineeringCRS.CARTESIAN_2D;

        //Create Calculator. Be careful to choice calculator adapted from crs---
        //final Calculator calculator = DefaultCalculator.CARTESIAN_2D;

        //In this demo basic r-tree requires split case. "LINEAR" or "QUADRATIC"
        final SplitCase splitcase = SplitCase.QUADRATIC;

        //creating tree (R-Tree)------------------------------------------------
        final Tree tree = new BasicRTree(4, crs, splitcase);

        //Create an entry to add in tree----------------------------------------
        //NOTE : entry are GeneralEnvelope type---------------------------------
        final GeneralEnvelope entry = new GeneralEnvelope(crs);
        entry.setEnvelope(10, 20, 50, 150);

        //Insert entry in tree--------------------------------------------------
        tree.insert(entry);

        //Create empty GeneralEnvelope list to put search query results---------
        final List<Envelope> resultList = new ArrayList<Envelope>();

        //Create Tree Visitor to know what we do search result------------------
        final TreeVisitor treevisitor = new DefaultTreeVisitor(resultList);

        //Create area search----------------------------------------------------
        final GeneralEnvelope areaSearch = new GeneralEnvelope(crs);
        areaSearch.setEnvelope(-10, -20, 100, 200);

        //search----------------------------------------------------------------
        tree.search(areaSearch, treevisitor);

        //Delete an entry in tree-----------------------------------------------
        tree.delete(entry);

        // re-insert
        tree.insert(entry);


                                /*-------------------
                                  -------------------*/

        /**
         * For example another tree creation.
         */

        final CoordinateReferenceSystem anotherCrs = DefaultEngineeringCRS.CARTESIAN_3D;
        //final Calculator anotherCalculator = DefaultCalculator.CARTESIAN_3D;

        //NOTE : no Splitcase required because split made is single in this tree case and contained in Tree body.
        final Tree anotherTree = new StarRTree(3, anotherCrs);

        //Same methods ---------------------------------------------------------
        final GeneralEnvelope anotherEntry = new GeneralEnvelope(anotherCrs);
        anotherEntry.setEnvelope(10, 20, 50, 150, 41, 78);

        final GeneralEnvelope anotherAreaSearch = new GeneralEnvelope(anotherCrs);
        anotherAreaSearch.setEnvelope(-10, -20, 100, 200, 100, 200);

        //we keep same visitor
        anotherTree.insert(anotherEntry);
        anotherTree.search(anotherAreaSearch, treevisitor);
        anotherTree.delete(anotherEntry);

                                /*-------------------
                                  -------------------*/

        /**
         * Tree write and read.
         */

        //Create File to stock tree---------------------------------------------
        final File fil = new File("tree.bin");

        //Create tree writer----------------------------------------------------
        final TreeWriter treeWriter = new TreeWriter();

        //supported format : java.io.File, java.io.OutputStream-----------------
        treeWriter.setOutput(fil);

        //write-----------------------------------------------------------------
        treeWriter.write(tree);

        //Release potential locks or opened stream------------------------------
        treeWriter.dispose();

        //Close potential previous stream and cache if there are some-----------
        treeWriter.reset();

        final TreeReader treeReader = new TreeReader();
        treeReader.setInput(fil);

        //Create an R-Tree to re-build read result------------------------------
        final Tree resultTree = new BasicRTree(4, crs, splitcase);

        //read (read result pushing in result tree)-----------------------------
        treeReader.read(resultTree);
        treeReader.dispose();
        treeReader.reset();

        //In another case, for a single write/read use exit static method-------
        //To write--------------------------------------------------------------
        TreeWriter.write(tree, fil);
        //to read---------------------------------------------------------------
        TreeReader.read(tree, fil);

                                /*-------------------
                                  -------------------*/
   }
}
