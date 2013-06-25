/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.Arrays;
import java.util.List;
import static org.geotoolkit.index.tree.DefaultTreeUtils.add;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getCoords;
import static org.junit.Assert.assertTrue;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author rmarechal
 */
 abstract class HilbertRtreeTest extends SpatialTreeTest {

    public HilbertRtreeTest(Tree tree) throws TransformException {
        super(tree);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkBoundaryNode(final Node node) {
        double[] globalEnv = null;
        final int size = node.getChildCount();
        if (node.isLeaf()) {
            for (int i = 0; i < size; i++) {
                final Node cuCell = node.getChild(i);
                if (!cuCell.isEmpty()) {
                    for (int j = 0, s = cuCell.getCoordsCount(); j < s; j++) {
                        if (globalEnv == null) {
                            globalEnv = cuCell.getCoordinate(j).clone();
                        } else {
                            add(globalEnv, cuCell.getCoordinate(j));
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                final Node child = node.getChild(i);
                if (globalEnv == null) {
                    globalEnv = child.getBoundary().clone();
                } else {
                    add(globalEnv, child.getBoundary());
                }
            }
        }
        return Arrays.equals(globalEnv, node.getBoundary());
    }

    @Override
    protected boolean checkTreeElts(Tree tree) {
        return (tree.getElementsNumber() == countEltsInHilbertNode(tree.getRoot(), 0));
    }
    
    private static int countEltsInHilbertNode(final Node candidate, int count) {
        final int size = candidate.getChildCount();
        if (candidate.isLeaf()) {
            for (int i = 0; i < size; i++) {
                final Node cuCell = candidate.getChild(i);
                final int ccount = cuCell.getCoordsCount();
                assert  ccount == cuCell.getObjectCount() : "countEltsInHilbertNode : coord and object length must concord.";
                count += ccount;
            }
        } else {
            for (int i = 0; i < size; i++) {
                count = countEltsInHilbertNode(candidate.getChild(i), count);
            }
        }
        return count;
    }
    
    @Override
    protected boolean checkElementInsertion(final Node candidate, List<Envelope> listRef) {
        final int siz = candidate.getChildCount();
        if (candidate.isLeaf()) {
            for (int i = 0; i < siz; i++) {
                final Node cuCell = candidate.getChild(i);
                assert (cuCell.getCoordsCount() == cuCell.getObjectCount()) : "coord and object should be same length.";
                for (int j = 0, sc = cuCell.getCoordsCount(); j < sc; j++) {
                    Object cuObj = cuCell.getObject(j);
                    double[] coords = cuCell.getCoordinate(j);
                    assertTrue(Arrays.equals(coords, getCoords((Envelope)cuObj)));
                    boolean found = false;
                    for (int il = 0, s = listRef.size(); il < s; il++) {
                        if (cuObj == listRef.get(il)) {
                            if (Arrays.equals(coords, getCoords(listRef.get(il)))) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) return false;
                }
            }
            return true;// we found all.
        } else {
            for (int i = 0; i < siz; i++) {
                boolean check = checkElementInsertion(candidate.getChild(i), listRef);
                if (!check) return false;
            }
            return true;
        }
    }
}
