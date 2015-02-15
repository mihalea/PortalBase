package ro.mihalea.portalbase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Mircea on 19-Jun-14.
 */
public class ListView extends JPanel {
    private JLabel lb_overworld = new JLabel("OVERWORLD");
    private JLabel lb_xOver = new JLabel("X:");
    private JLabel lb_yOver = new JLabel("Y:");
    private JLabel lb_zOver = new JLabel("Z:");

    private JLabel lb_nether = new JLabel("NETHER");
    private JLabel lb_xNether = new JLabel("X:");
    private JLabel lb_yNether = new JLabel("Y:");
    private JLabel lb_zNether = new JLabel("Z:");

    private JLabel lb_errorMessage = new JLabel("No error");


    private IntegerField tf_xOver;
    private IntegerField tf_yOver;
    private IntegerField tf_zOver;

    private IntegerField tf_xNether;
    private IntegerField tf_yNether;
    private IntegerField tf_zNether;

    private JTextField tf_name = new JTextField("Name");


    private JButton bt_remove = new JButton("Remove");

    public ListView() {
        configureEvents();
        configureLayout();
    }

    private void configureEvents() {
        bt_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, (Component) e.getSource());
                int row = table.getEditingRow();
                table.getCellEditor().stopCellEditing();
                ((DefaultTableModel) table.getModel()).removeRow(row);
            }
        });
    }

    private void configureLayout() {
        lb_overworld.setFont(lb_overworld.getFont().deriveFont(Font.BOLD));
        lb_nether.setFont(lb_nether.getFont().deriveFont(Font.BOLD));
        lb_errorMessage.setVisible(false);
        lb_errorMessage.setForeground(Color.RED);


        tf_xOver = new IntegerField();
        tf_yOver = new IntegerField();
        tf_zOver = new IntegerField();
        tf_xNether = new IntegerField();
        tf_yNether = new IntegerField();
        tf_zNether = new IntegerField();

        tf_name.setColumns(28);
        tf_xOver.setColumns(5);
        tf_yOver.setColumns(5);
        tf_zOver.setColumns(5);
        tf_xNether.setColumns(5);
        tf_yNether.setColumns(5);
        tf_zNether.setColumns(5);

        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(3, 5, 2, 5));
        this.setMinimumSize(new Dimension(0, 115));

        this.addComponent(tf_name,      0, 0, 7, new Insets(0, 0, 5, 0));
        //this.addComponent(bt_remove,    1, 0, 1, new Insets(0, 0, 5, 0));
        this.addComponent(lb_overworld, 0, 1, 1, new Insets(0, 0, 5, 0));
        this.addComponent(lb_xOver,     1, 1, 1, new Insets(0, 5, 5, 0));
        this.addComponent(tf_xOver,     2, 1, 1, new Insets(0, 5, 5, 0));
        this.addComponent(lb_yOver,     3, 1, 1, new Insets(0, 5, 5, 0));
        this.addComponent(tf_yOver,     4, 1, 1, new Insets(0, 5, 5, 0));
        this.addComponent(lb_zOver,     5, 1, 1, new Insets(0, 5, 5, 0));
        this.addComponent(tf_zOver,     6, 1, 1, new Insets(0, 5, 5, 0));
        this.addComponent(new JPanel(), 7, 1, 1, null, 1d, 0d);
        this.addComponent(lb_nether,    0, 2, 1, new Insets(0, 0, 5, 0));
        this.addComponent(lb_xNether  , 1, 2, 1, new Insets(0, 5, 5, 0));
        this.addComponent(tf_xNether  , 2, 2, 1, new Insets(0, 5, 5, 0));
        this.addComponent(lb_yNether  , 3, 2, 1, new Insets(0, 5, 5, 0));
        this.addComponent(tf_yNether  , 4, 2, 1, new Insets(0, 5, 5, 0));
        this.addComponent(lb_zNether  , 5, 2, 1, new Insets(0, 5, 5, 0));
        this.addComponent(tf_zNether  , 6, 2, 1, new Insets(0, 5, 5, 0));
        this.addComponent(new JPanel(), 7, 2, 1, null, 1d, 0d);
        this.addComponent(lb_errorMessage, 0, 3, 7, null);
    }

    private void addComponent(Component component, int gridx, int gridy, int width, Insets insets){
        this.addComponent(component, gridx, gridy, width, insets, 0d, 0d);
    }

    private void addComponent(Component component, int gridx, int gridy, int width, Insets insets, double weightx, double weighty) {
        GridBagConstraints c = new GridBagConstraints();
        if(insets!=null)
            c.insets = insets;
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = width;
        c.weightx = weightx;
        c.weighty = weighty;
        c.anchor = GridBagConstraints.NORTHWEST;

        this.add(component, c);
    }

    public void setPortalPair(PortalPair portalPair){
        tf_xOver.setText(Integer.toString(portalPair.getOverworld().getX()));
        tf_yOver.setText(Integer.toString(portalPair.getOverworld().getY()));
        tf_zOver.setText(Integer.toString(portalPair.getOverworld().getZ()));

        tf_xNether.setText(Integer.toString(portalPair.getNether().getX()));
        tf_yNether.setText(Integer.toString(portalPair.getNether().getY()));
        tf_zNether.setText(Integer.toString(portalPair.getNether().getZ()));

        tf_name.setText(portalPair.getName());

        if(portalPair.getErrorMessage().trim().isEmpty() == false){
            lb_errorMessage.setText(portalPair.getErrorMessage());
            lb_errorMessage.getParent().revalidate();
            lb_errorMessage.setVisible(true);
        } else {
            lb_errorMessage.setVisible(false);
        }


    }

    public PortalPair getPortalPair() {
        try {
            return new PortalPair(tf_name.getText(),
                                  new Portal(new Integer(tf_xOver.getText()), new Integer(tf_yOver.getText()), new Integer(tf_zOver.getText())),
                                  new Portal(new Integer(tf_xNether.getText()), new Integer(tf_yNether.getText()), new Integer(tf_zNether.getText())));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private class IntegerField extends JTextField {

        public IntegerField() {
            super();
        }

        public IntegerField( int cols ) {
            super( cols );
        }

        @Override
        protected Document createDefaultModel() {
            return new UpperCaseDocument();
        }

        class UpperCaseDocument extends PlainDocument {

            @Override
            public void insertString( int offs, String str, AttributeSet a )
                    throws BadLocationException {

                if ( str == null ) {
                    return;
                }

                char[] chars = str.toCharArray();
                boolean ok = true;

                for ( int i = 0; i < chars.length; i++ ) {
                    try {
                        if(i==0 && chars[i] == '-')
                            continue;

                        Integer.parseInt(String.valueOf(chars[i]));
                    } catch ( NumberFormatException exc ) {
                        ok = false;
                        break;
                    }
                }

                if ( ok )
                    super.insertString( offs, new String( chars ), a );

            }
        }

    }
}
