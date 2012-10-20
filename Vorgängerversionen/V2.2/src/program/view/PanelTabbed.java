package program.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import program.controller.Messages;
import program.controller.ViewController;
import program.data.MulticastData.Typ;
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
	private JPanel pan_switcher;
	private JTextArea ta_console;
	private PanelGraph pan_graph;
	private JScrollPane table_scrollpane;
	private JScrollPane console_scrollpane;
	private JTable table;

	private JButton senderBt;
	private JButton receiverBt;
//	private PanelMulticastConfig pan_config;
	private PanelTabpaneConfiguration pan_config;
	private PanelMulticastControl pan_control;
	private PanelStatusBar pan_status;
	private JTabbedPane tab_console;
	private MiscTableModel model;
	private boolean popupsAllowed = true;
	private ArrayList<TableColumn> columns;

	/**
	 * Konstruktor f�r einen kompletten Programmteil in der GUI.
	 * Hierbei werden alle Komponenten fertig initialisiert.
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 * @param typ Gibt an um welchen Programmteil es sich handelt
	 */
	public PanelTabbed(ViewController ctrl, Typ typ) {
		setLayout(new BorderLayout());
		initSwitcherPanel(ctrl,typ);
		initControlPanel(ctrl, typ);
		initConfigPanel(ctrl, typ);
		initConsolePanel(ctrl, typ);
		initTablePanel(ctrl, typ);
		initStatusPanel(ctrl, typ);


		// Multicast Options Panel containing Control and Configuration
		pan_options = new JPanel();
		pan_options.setPreferredSize(new Dimension(240, 600));
		pan_options.add(pan_switcher);
		pan_options.add(pan_control);
		pan_options.add(pan_config);


		// Space Panel, a Buffer for Resizing below the Left Panel
		pan_space = new JPanel();

		// The Left Panel containing the Options Panel and the Space Panel
		pan_left = new JPanel();
		pan_left.setLayout(new BorderLayout());
		pan_left.setPreferredSize(new Dimension(240,600));
		pan_left.add(pan_options, BorderLayout.NORTH);
		// Adding all the Components to the Tab_Panel
		add(pan_left, BorderLayout.WEST);
		add(pan_table,BorderLayout.CENTER);
		add(pan_status, BorderLayout.SOUTH);
	}
	private void initSwitcherPanel(ViewController ctrl, Typ typ) {
		senderBt = new JButton(Messages.getString("PanelTabbed.0")); //$NON-NLS-1$
		receiverBt =new JButton(Messages.getString("PanelTabbed.1")); //$NON-NLS-1$

		senderBt.setMnemonic('y');
		receiverBt.setMnemonic('x');

		senderBt.setFont(MiscFont.getFont());
		receiverBt.setFont(MiscFont.getFont());

		senderBt.setMargin(new Insets(0, 0, 0, 0));
		receiverBt.setMargin(new Insets(0, 0, 0, 0));

		senderBt.setFocusable(false);
		receiverBt.setFocusable(false);
		senderBt.setBounds(10,20,100,40);
		receiverBt.setBounds(115,20,100,40);
		if(typ==Typ.RECEIVER_V4){
			senderBt.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/sender_d.png"))); //$NON-NLS-1$
			senderBt.setForeground(new Color(193, 193, 193));
			receiverBt.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/receiver.png"))); //$NON-NLS-1$
			receiverBt.setForeground(new Color(0, 180, 0));}
		else{
			receiverBt.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/receiver_d.png"))); //$NON-NLS-1$
			receiverBt.setForeground(new Color(193, 193, 193));
			senderBt.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/sender.png"))); //$NON-NLS-1$
			senderBt.setForeground(Color.BLUE);
		}

		senderBt.addActionListener(ctrl);
		receiverBt.addActionListener(ctrl);


		pan_switcher = new JPanel();
		pan_switcher.setLayout(null);
		pan_switcher.setPreferredSize(new Dimension(225, 70));
		pan_switcher.setBorder(new MiscBorder(Messages.getString("PanelTabbed.6"))); //$NON-NLS-1$

		pan_switcher.add(senderBt);
		pan_switcher.add(receiverBt);
	}
	/**
	 * Initialisiert die Statusbar
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Statusbar geh�rt
	 */
	private void initStatusPanel(ViewController ctrl, Typ typ) {
		pan_status = new PanelStatusBar();
	}
	/**
	 * Initialisiert den Graph und die Console der GUI
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Komponenten geh�ren
	 */
	private void initConsolePanel(ViewController ctrl, Typ typ) {
		tab_console = new JTabbedPane();
		tab_console.setFont(MiscFont.getFont(0, 14));
		ta_console = new JTextArea(""); //$NON-NLS-1$
		ta_console.setFont(MiscFont.getFont(0, 11));
		ta_console.setEditable(false);
		console_scrollpane = new JScrollPane(ta_console);
		console_scrollpane.setPreferredSize(new Dimension(300, 100));
		if(Typ.isSender(typ)){
			pan_graph = new PanelGraph(500, "sec", Messages.getString("PanelTabbed.9"), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else{
			pan_graph = new ReceiverGraph(ctrl);
		}
		tab_console.addTab(Messages.getString("PanelTabbed.2"), pan_graph);; //$NON-NLS-1$
		tab_console.addTab(Messages.getString("PanelTabbed.11"), console_scrollpane); //$NON-NLS-1$
//		pan_graph.setVisible(false);
//		tab_console.remove(0);
	}
	/**
	 * Initialisiert die Tabelle
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Tabelle geh�rt
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
		pan_table.setBorder(new MiscBorder(Messages.getString("PanelTabbed.12"))); //$NON-NLS-1$
		pan_table.add(tab_console,BorderLayout.SOUTH);
		pan_table.add(table_scrollpane,BorderLayout.CENTER);

	}

	/**
	 * Resettet das Aussehen der Tabelle auf das Standard Aussehen
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 * @param typ Gibt den Programmteil an zu welchem die Tabelle geh�rt
	 */

	public void setTableModel(ViewController ctrl, Typ typ) {
		model = new MiscTableModel(ctrl,typ);
		table.setModel(model);
		table.getTableHeader().setDefaultRenderer(new HeaderCellRender(typ));
		TableColumnModel colmodel = table.getColumnModel();
		Enumeration<TableColumn> e = colmodel.getColumns();
		columns = new ArrayList<TableColumn>();
		while(e.hasMoreElements()){
			columns.add(e.nextElement());
		}

		colmodel.addColumnModelListener(ctrl);
		colmodel.getColumn(0).setMinWidth(35);
		colmodel.getColumn(1).setMinWidth(35);
		colmodel.getColumn(2).setMinWidth(35);
		colmodel.getColumn(3).setMinWidth(35);
		colmodel.getColumn(4).setMinWidth(35);
		colmodel.getColumn(5).setMinWidth(35);
		colmodel.getColumn(6).setMinWidth(35);
		colmodel.getColumn(0).setPreferredWidth(35);
		colmodel.getColumn(1).setPreferredWidth(35);
		colmodel.getColumn(2).setPreferredWidth(100);
		colmodel.getColumn(3).setPreferredWidth(130);
		colmodel.getColumn(4).setPreferredWidth(45);
		colmodel.getColumn(5).setPreferredWidth(45);
		colmodel.getColumn(6).setPreferredWidth(35);
		if(typ == Typ.SENDER_V4){
			colmodel.getColumn(7).setMinWidth(35);
			colmodel.getColumn(8).setMinWidth(35);
			colmodel.getColumn(9).setMinWidth(35);
			colmodel.getColumn(10).setMinWidth(30);
			colmodel.getColumn(11).setMinWidth(35);
			colmodel.getColumn(7).setPreferredWidth(50);
			colmodel.getColumn(8).setPreferredWidth(100);
			colmodel.getColumn(9).setPreferredWidth(60);
			colmodel.getColumn(10).setPreferredWidth(30);
			colmodel.getColumn(11).setPreferredWidth(45);
		}
		else{
			colmodel.getColumn(7).setMinWidth(35);
			colmodel.getColumn(8).setMinWidth(35);
			colmodel.getColumn(9).setMinWidth(35);
			colmodel.getColumn(10).setMinWidth(35);
			colmodel.getColumn(11).setMinWidth(35);
			colmodel.getColumn(7).setPreferredWidth(50);
			colmodel.getColumn(8).setPreferredWidth(60);
			colmodel.getColumn(9).setPreferredWidth(60);
			colmodel.getColumn(10).setPreferredWidth(45);
			colmodel.getColumn(11).setPreferredWidth(85);
		}
	}
	public MiscTableModel getModel() {
		return model;
	}
	
	private void initConfigPanel(ViewController ctrl, Typ typ) {
//		pan_config = new PanelMulticastConfig(ctrl, typ);
		pan_config = new PanelTabpaneConfiguration(ctrl, typ);
	}

	private void initControlPanel(ViewController ctrl, Typ typ) {
		pan_control = new PanelMulticastControl(ctrl, typ);
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


	public PanelMulticastConfig getPan_config(Typ typ){
		return pan_config.getPan(typ);
	}
	public PanelTabpaneConfiguration getPan_TabsConfig() {
		return pan_config;
	}

	public PanelMulticastControl getPan_control() {
		return pan_control;
	}

	public PanelStatusBar getPan_status() {
		return pan_status;
	}

	public JButton getBtSender(){
		return senderBt;
	}

	public JButton getBtReceiver(){
		return receiverBt;
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
	class HeaderCellRender implements TableCellRenderer {
		Typ typ;
		public HeaderCellRender(Typ typ){
			this.typ=typ;
		}
	    
	    public Component getTableCellRendererComponent(JTable table, Object value, 
	            boolean isSelected, boolean hasFocus, int row, int column) {
	        JComponent c = null;

	        if(value instanceof String ) {
	            c = new JLabel((String)value);
	            ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);

		        if(typ==Typ.SENDER_V4){
		        	c.setBackground(new Color(168,215,237));
		        } else {
		        	c.setBackground(new Color(215,237,168));	        	
		        }
	        	if(((String) value).charAt(0)=='+'||((String)value).charAt(0)=='-') 
	        		c.setBackground(new Color(250,250,250));
	            }
	        c.setEnabled(true);
	        c.setFont(table.getFont());
	        c.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        c.setOpaque(true);

	        return c;
	    }
	}
}
