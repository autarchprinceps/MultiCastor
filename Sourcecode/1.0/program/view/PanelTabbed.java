package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.Typ;
/**
 * Ein Panel welches jeweils einen kompletten Programmteil beinhaltet. 
 * Durch diese Panels kann man im Programm tabben.
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelTabbed extends JPanel {

	private JPanel pan_table;
	private JPanel pan_left;
	private JPanel pan_options;
	private JPanel pan_space;
	private JTextArea ta_console;
	private PanelGraph pan_graph;
	private JScrollPane table_scrollpane;
	private JScrollPane console_scrollpane;
	private JTable table;
	private PanelMulticastConfig pan_config;
	private PanelMulticastControl pan_control;
	private PanelStatusBar pan_status;
	private JTabbedPane tab_console;
	private MiscTableModel model;
	private boolean popupsAllowed = true;
	private ArrayList<TableColumn> columns;

	/**
	 * Konstruktor für einen kompletten Programmteil in der GUI.
	 * Hierbei werden alle Komponenten fertig initialisiert.
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 * @param typ Gibt an um welchen Programmteil es sich handelt
	 */
	public PanelTabbed(ViewController ctrl, Typ typ) {
		setLayout(new BorderLayout());
		initControlPanel(ctrl);
		initConfigPanel(ctrl, typ);
		initConsolePanel(ctrl, typ);
		initTablePanel(ctrl, typ);
		initStatusPanel(ctrl, typ);
		

		// Multicast Options Panel containing Control and Configuration
		pan_options = new JPanel();
		pan_options.setLayout(new BorderLayout());
		pan_options.add(pan_control,BorderLayout.NORTH);
		pan_options.add(pan_config, BorderLayout.EAST);
		
		
		// Space Panel, a Buffer for Resizing below the Left Panel
		pan_space = new JPanel();
		
		// The Left Panel containing the Options Panel and the Space Panel
		pan_left = new JPanel();
		pan_left.setLayout(new BorderLayout());
		pan_left.setPreferredSize(new Dimension(225,500));
		pan_left.add(pan_options, BorderLayout.NORTH);
		pan_left.add(pan_space, BorderLayout.CENTER);
		// Adding all the Components to the Tab_Panel
		add(pan_left, BorderLayout.WEST);
		add(pan_table,BorderLayout.CENTER);
		add(pan_status, BorderLayout.SOUTH);
	}
	/**
	 * Initialisiert die Statusbar
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Statusbar gehört
	 */
	private void initStatusPanel(ViewController ctrl, Typ typ) {
		pan_status = new PanelStatusBar();
	}
	/**
	 * Initialisiert den Graph und die Console der GUI
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Komponenten gehören
	 */
	private void initConsolePanel(ViewController ctrl, Typ typ) {
		tab_console = new JTabbedPane();
		tab_console.setFont(MiscFont.getFont(0, 14));
		ta_console = new JTextArea("");
		ta_console.setFont(MiscFont.getFont(0, 11));
		ta_console.setEditable(false);
		console_scrollpane = new JScrollPane(ta_console);
		console_scrollpane.setPreferredSize(new Dimension(300, 100));
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			pan_graph = new PanelGraph(500, "sec", "Packets per Second (total)", false);
		}
		else{
			pan_graph = new ReceiverGraph(ctrl);
		}
		tab_console.addTab("Graph", pan_graph);;
		tab_console.addTab("Console", console_scrollpane);
//		pan_graph.setVisible(false);
//		tab_console.remove(0);
	}
	/**
	 * Initialisiert die Tabelle
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Tabelle gehört
	 */
	private void initTablePanel(ViewController ctrl, Typ typ) {
		pan_table = new JPanel();
		table = new JTable();
		setTableModel(ctrl, typ);
		
		table.setDefaultRenderer( Object.class, new WrappingCellRenderer(table.getDefaultRenderer(Object.class),ctrl));
		table.setDefaultRenderer( Boolean.class, new WrappingCellRenderer(table.getDefaultRenderer(Boolean.class),ctrl));
		table.setDefaultRenderer( Integer.class, new WrappingCellRenderer(table.getDefaultRenderer(Integer.class),ctrl));
		table.setDefaultRenderer( Double.class, new WrappingCellRenderer(table.getDefaultRenderer(Double.class),ctrl));
		table.setDefaultRenderer( Long.class, new WrappingCellRenderer(table.getDefaultRenderer(Long.class),ctrl));
		table.setFont(MiscFont.getFont(0,10));
		table.getTableHeader().setFont(MiscFont.getFont(0,10));
		table.getSelectionModel().addListSelectionListener(ctrl);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().addMouseListener(ctrl);


		table_scrollpane = new JScrollPane(table);
		table_scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		table_scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pan_table.setLayout(new BorderLayout());
		pan_table.setBorder(new MiscBorder("MultiCast Overview"));
		pan_table.add(tab_console,BorderLayout.SOUTH);
		pan_table.add(table_scrollpane,BorderLayout.CENTER);
	}
	/**
	 * Resettet das Aussehen der Tabelle auf das Standard Aussehen
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Tabelle gehört
	 */
	public void setTableModel(ViewController ctrl, Typ typ) {
		model = new MiscTableModel(ctrl,typ);
		table.setModel(model);
		TableColumnModel colmodel = table.getColumnModel();
		Enumeration<TableColumn> e = colmodel.getColumns();
		columns = new ArrayList<TableColumn>();
		while(e.hasMoreElements()){
			columns.add((TableColumn) e.nextElement());
		}
		colmodel.addColumnModelListener(ctrl);
		colmodel.getColumn(0).setMinWidth(45);
		colmodel.getColumn(1).setMinWidth(100);
		colmodel.getColumn(2).setMinWidth(100);
		colmodel.getColumn(3).setMinWidth(60);
		colmodel.getColumn(4).setMinWidth(60);
		colmodel.getColumn(5).setMinWidth(60);
		colmodel.getColumn(0).setPreferredWidth(45);
		colmodel.getColumn(1).setPreferredWidth(100);
		colmodel.getColumn(2).setPreferredWidth(100);
		colmodel.getColumn(3).setPreferredWidth(60);
		colmodel.getColumn(4).setPreferredWidth(60);
		colmodel.getColumn(5).setPreferredWidth(60);
		if(typ == Typ.SENDER_V4 || typ ==Typ.SENDER_V6){
			colmodel.getColumn(6).setMinWidth(50);
			colmodel.getColumn(7).setMinWidth(100);
			colmodel.getColumn(8).setMinWidth(60);
			colmodel.getColumn(9).setMinWidth(30);
			colmodel.getColumn(10).setMinWidth(60);
			colmodel.getColumn(6).setPreferredWidth(50);
			colmodel.getColumn(7).setPreferredWidth(100);
			colmodel.getColumn(8).setPreferredWidth(60);
			colmodel.getColumn(9).setPreferredWidth(30);
			colmodel.getColumn(10).setPreferredWidth(60);
		}
		else{
			colmodel.getColumn(6).setMinWidth(50);
			colmodel.getColumn(7).setMinWidth(60);
			colmodel.getColumn(8).setMinWidth(60);
			colmodel.getColumn(9).setMinWidth(45);
			colmodel.getColumn(10).setMinWidth(85);
			colmodel.getColumn(6).setPreferredWidth(50);
			colmodel.getColumn(7).setPreferredWidth(60);
			colmodel.getColumn(8).setPreferredWidth(60);
			colmodel.getColumn(9).setPreferredWidth(45);
			colmodel.getColumn(10).setPreferredWidth(85);
		}
	}
	public MiscTableModel getModel() {
		return model;
	}
	private void initConfigPanel(ViewController ctrl, Typ typ) {
		pan_config = new PanelMulticastConfig(ctrl, typ);
	}

	private void initControlPanel(ViewController ctrl) {
		pan_control = new PanelMulticastControl(ctrl);
	}

	public Dimension getGraphSize(){
		return pan_graph.getSize();
	}

	public JPanel getPan_table() {
		return pan_table;
	}

	public JPanel getPan_left() {
		return pan_left;
	}

	public JPanel getPan_options() {
		return pan_options;
	}

	public JPanel getPan_space() {
		return pan_space;
	}

	public JTextArea getTa_console() {
		return ta_console;
	}

	public PanelGraph getPan_graph() {
		return pan_graph;
	}
	public ReceiverGraph getPan_recGraph(){
		return ((ReceiverGraph) pan_graph);
	}

	public JScrollPane getTable_scrollpane() {
		return table_scrollpane;
	}

	public JScrollPane getConsole_scrollpane() {
		return console_scrollpane;
	}

	public JTable getTable() {
		return table;
	}

	public PanelMulticastConfig getPan_config() {
		return pan_config;
	}

	public PanelMulticastControl getPan_control() {
		return pan_control;
	}

	public PanelStatusBar getPan_status() {
		return pan_status;
	}

	public JTabbedPane getTab_console() {
		return tab_console;
	}
	public MiscTableModel getTableModel() {
		return model;
	}
	public void setPanels(boolean config, boolean control, boolean status, boolean console, boolean graph){
		if(!config && !control){
			pan_left.setVisible(false);
		}
		else{
			pan_config.setVisible(config);
			pan_control.setVisible(control);
		}
		if(!console && !graph){
			tab_console.setVisible(false);
		}
		else if(!console){
			ta_console.setVisible(false);
			tab_console.remove(1);
		}
		else if(!graph){
			pan_graph.setVisible(false);
			tab_console.remove(0);
		}
		pan_status.setVisible(status);
	}
	public boolean isPopupsAllowed() {
		return popupsAllowed;
	}
	public void setPopupsAllowed(boolean popupsAllowed) {
		this.popupsAllowed = popupsAllowed;
	}
	public ArrayList<TableColumn> getColumns() {
		return columns;
	}
}
