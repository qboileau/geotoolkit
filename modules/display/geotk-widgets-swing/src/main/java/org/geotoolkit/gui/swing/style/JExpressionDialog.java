/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.style;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

/**
 * Expression dialog
 * 
 * @author Johann Sorel
 */
public class JExpressionDialog extends javax.swing.JDialog {

    /** 
     * Creates new form JExpressionDialog 
     */
    public JExpressionDialog() {
        this(null, null);
    }

    /**
     * 
     * @param layer the layer to edit
     */
    public JExpressionDialog(MapLayer layer) {
        this(layer, null);
    }

    /**
     * 
     * @param exp the default expression
     */
    public JExpressionDialog(Expression exp) {
        this(null, exp);
    }

    /**
     * 
     * @param layer the layer to edit
     * @param exp the default expression
     */
    public JExpressionDialog(MapLayer layer, Expression exp) {
        initComponents();

        setLayer(layer);
        setExpression(exp);

        lst_field.addListSelectionListener(new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {

                        if (lst_field.getSelectedValue() != null) {
                            append(lst_field.getSelectedValue().toString());
                        }
                    }
                });
    }

    private void append(String val) {
        if (!jta.getText().endsWith(val)) {

            if (!jta.getText().endsWith(" ") && jta.getText().length() > 0) {
                jta.append(" ");
            }
            jta.append(val);
        }
    }

    /**
     * 
     * @param layer the layer to edit
     */
    public void setLayer(MapLayer layer) {
        lst_field.removeAll();

        if (layer != null && layer instanceof FeatureMapLayer) {

            lst_field.removeAll();

            Collection<PropertyDescriptor> col = ((FeatureMapLayer)layer).getFeatureSource().getSchema().getDescriptors();
            Iterator<PropertyDescriptor> it = col.iterator();

            PropertyDescriptor desc;
            Vector<String> vec = new Vector<String>();
            while (it.hasNext()) {
                desc = it.next();
                vec.add(desc.getName().toString());
            }

            lst_field.setListData(vec);
        }

    }

    /**
     * 
     * @param exp the default expression
     */
    public void setExpression(Expression exp) {

        if (exp != null) {
            if (exp != Expression.NIL) {
                jta.setText(exp.toString());
            }
        }

    }

    /**
     * 
     * @return Expression : New Expression
     */
    public Expression getExpression() {

        FilterFactory ff = FactoryFinder.getFilterFactory(null);
        Expression expr = ff.property(jta.getText());

        return expr;
    //        try {
//            Expression expr = CQL.toExpression(jta.getText());           
//            return expr;
//        } catch (CQLException ex) {
//            ex.printStackTrace();
//        }
//        
//        StyleBuilder sb = new StyleBuilder();
//        return sb.literalExpression(jta.getText());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new JScrollPane();
        lst_field = new JList();
        jScrollPane1 = new JScrollPane();
        jta = new JTextArea();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
        jButton5 = new JButton();
        jButton6 = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane2.setViewportView(lst_field);

        jta.setColumns(20);
        jta.setRows(5);
        jScrollPane1.setViewportView(jta);

        jButton2.setText("+");
        jButton2.setMargin(new Insets(2, 4, 2, 4));
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2actionPlus(evt);
            }
        });

        jButton3.setText("-");
        jButton3.setMargin(new Insets(2, 4, 2, 4));
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton3actionMinus(evt);
            }
        });

        jButton4.setText("/");
        jButton4.setMargin(new Insets(2, 4, 2, 4));
        jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton4actionDivide(evt);
            }
        });

        jButton5.setText("*");
        jButton5.setMargin(new Insets(2, 4, 2, 4));
        jButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton5actionMultiply(evt);
            }
        });

        jButton6.setText(MessageBundle.getString("apply")); // NOI18N
        jButton6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionClose(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(jScrollPane1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addComponent(jButton6))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton4)
                            .addComponent(jButton5))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addGap(5, 5, 5)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButton2actionPlus(ActionEvent evt) {//GEN-FIRST:event_jButton2actionPlus
        append("+");
    }//GEN-LAST:event_jButton2actionPlus

    private void jButton3actionMinus(ActionEvent evt) {//GEN-FIRST:event_jButton3actionMinus
        append("-");
    }//GEN-LAST:event_jButton3actionMinus

    private void jButton4actionDivide(ActionEvent evt) {//GEN-FIRST:event_jButton4actionDivide
        append("/");
    }//GEN-LAST:event_jButton4actionDivide

    private void jButton5actionMultiply(ActionEvent evt) {//GEN-FIRST:event_jButton5actionMultiply
        append("*");
    }//GEN-LAST:event_jButton5actionMultiply

    private void actionClose(ActionEvent evt) {//GEN-FIRST:event_actionClose
        dispose();
    }//GEN-LAST:event_actionClose
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JButton jButton6;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextArea jta;
    private JList lst_field;
    // End of variables declaration//GEN-END:variables
}
