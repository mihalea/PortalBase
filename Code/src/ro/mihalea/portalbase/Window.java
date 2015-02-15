package ro.mihalea.portalbase;

import com.thoughtworks.xstream.XStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

/**
 * Created by Mircea on 19-Jun-14.
 */
public class Window {
    private JFrame frame;
    private CustomTableModel tableModel;
    private JTable jTable;
    private File saveFile;

    public Window() {
        frame = new JFrame();

        addMenuBar();
        configLayout();
        setupEvents();
        finalizeFrame();
    }

    private void setupEvents() {
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                checkLinks();
            }
        });
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu me_file = new JMenu("File");
        JMenuItem mi_open = new JMenuItem("Open");
        JMenuItem mi_save = new JMenuItem("Save");
        JMenuItem mi_saveas = new JMenuItem("Save as");

        mi_open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        mi_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile(false);
            }
        });
        mi_saveas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile(true);
            }
        });

        me_file.add(mi_open);
        me_file.add(mi_save);
        me_file.add(mi_saveas);

        menuBar.add(me_file);

        frame.setJMenuBar(menuBar);
    }

    private void configLayout() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 5, 10, 5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton("Add new pair");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.addRow();
            }
        });
        panel.add(button, BorderLayout.NORTH);

        tableModel = new CustomTableModel();
        tableModel.addRow();
        jTable = new JTable(tableModel);
        jTable.setTableHeader(null);
        jTable.setRowHeight(new ListView().getMinimumSize().height);
        jTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        CustomCellRenderer renderer = new CustomCellRenderer();
        jTable.setDefaultEditor(Object.class, renderer);
        jTable.setDefaultRenderer(Object.class, renderer);


        JScrollPane scrollPane = new JScrollPane(jTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(panel);
    }

    private void finalizeFrame() {
        frame.setTitle("PortalBase");
        frame.pack();
        frame.setSize(375, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            frame.setIconImage(ImageIO.read(getClass().getResource("/res/icon_128.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkLinks(){
        System.out.println("Checking...");
        boolean ok = true;

        int rowCount = tableModel.getRowCount();
        double overworldDistance, netherDistance;
        int netherID, overworldID;
        for (int i=0; i < rowCount ; i++){
            PortalPair startPair = (PortalPair) tableModel.getValueAt(i, 0);

            Portal overworld = startPair.getOverworld();
            Portal nether = startPair.getNether();

            PortalPair endPair = (PortalPair) tableModel.getValueAt(0, 0);
            overworldDistance = distance(netherToOverworld(nether), endPair.getOverworld());
            netherDistance = distance(overworldToNether(overworld), endPair.getNether());

            overworldID = i;
            netherID = i;

            for (int j = 1 ; j < rowCount ; j++){
                endPair = (PortalPair) tableModel.getValueAt(j, 0);

                double distance = distance(netherToOverworld(nether), endPair.getOverworld());
                if(distance < overworldDistance) {
                    overworldDistance = distance;
                    overworldID = j;
                }

                distance = distance(overworldToNether(overworld), endPair.getNether());
                if(distance < netherDistance) {
                    netherDistance = distance;
                    netherID = j;
                }
            }

            String errorMessage = "";

            if (overworldID != i) {
                String name = ((PortalPair)tableModel.getValueAt(overworldID, 0)).getName();
                errorMessage = "Nether portal links to " + name + "'s overworld portal. <br>";
            }

            if(netherID != i){
                String name = ((PortalPair)tableModel.getValueAt(netherID, 0)).getName();
                errorMessage += "Overworld portal links to " + name + "'s nether portal";
            }

            if(errorMessage.isEmpty() == false){
                startPair.setErrorMessage("<html>" + errorMessage + "</html>");
                ok = false;
            } else {
                startPair.setErrorMessage("");
            }
        }

        System.out.println("ok = " + ok);
    }

    private double distance(Portal a, Portal b){
        return Math.sqrt(1d * (b.getX() - a.getX()) * (b.getX() - a.getX()) +
                1d * (b.getY() - a.getY()) * (b.getY() - a.getY()) +
                1d * (b.getZ() - a.getZ()) * (b.getZ() - a.getZ()));
    }

    private Portal overworldToNether(Portal portal){
        return new Portal(portal.getX() / 8, portal.getY(), portal.getZ() / 8);
    }

    private Portal netherToOverworld(Portal portal){
        return new Portal(portal.getX() * 8, portal.getY(), portal.getZ() * 8);
    }

    private void endEdit(){
        if (jTable.isEditing())
            jTable.getCellEditor().stopCellEditing();
    }

    private void saveFile(boolean newPath){
        endEdit();

        int rows = tableModel.getRowCount();
        List<PortalPair> list = new ArrayList<PortalPair>();
        for (int i=0 ; i<rows ; i++){
            PortalPair portalPair = (PortalPair) tableModel.getValueAt(i, 0);
            list.add(portalPair);
        }

        XStream xStream = new XStream();
        String xml = xStream.toXML(list);

        if(saveFile == null || newPath) {
            JFileChooser fileChooser = new JFileChooser();

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
                saveFile = fileChooser.getSelectedFile();
            else
                return;
        }

        String path = saveFile.getPath();
        if(path.toLowerCase().endsWith(".xml") == false)
            saveFile = new File(path + ".xml");

        try {
            PrintWriter writer = new PrintWriter(saveFile);
            writer.print(xml);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void openFile(){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML files (*.xml)", "xml");
        fileChooser.setFileFilter(xmlFilter);

        if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
            saveFile = fileChooser.getSelectedFile();
        else
            return;

        try {
            String xml = new String(Files.readAllBytes(saveFile.toPath()));
            //System.out.println(xml);
            XStream xStream = new XStream();
            List<PortalPair> list = (List<PortalPair>) xStream.fromXML(xml);

            tableModel.clearAll();
            for(PortalPair portalPair : list)
                tableModel.addRow(portalPair);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class CustomTableModel extends DefaultTableModel{
        @Override
        public int getColumnCount() {
            return 1;
        }

        public void addRow(){
            this.addRow(new PortalPair("Name", new Portal(0, 0, 0), new Portal(0, 0, 0)));
        }

        public void addRow(PortalPair portalPair){
            super.addRow(new Object[]{portalPair});
        }

        public void clearAll(){
            int rowCount = tableModel.getRowCount();
            for (int i=0 ; i<rowCount; i++)
                tableModel.removeRow(0);
        }
    }

    private class CustomCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor{

        private ListView renderer = new ListView();
        private ListView editor   = new ListView();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editor.setPortalPair((PortalPair) value);
            return editor;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            renderer.setPortalPair((PortalPair) value);
            return renderer;
        }

        @Override
        public Object getCellEditorValue() {
            return editor.getPortalPair();
        }
    }

}
