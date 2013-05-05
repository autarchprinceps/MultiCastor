package multicastor.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.jdesktop.swingx.JXTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import multicastor.controller.ViewController;
import multicastor.data.MulticastData.Typ;
import multicastor.lang.LanguageManager;

/**
 * Ein Panel welches jeweils einen kompletten Programmteil beinhaltet. Durch
 * diese Panels kann man im Programm tabben.
 */
@SuppressWarnings("serial")
public class PanelTabbed extends JPanel {

	private ArrayList<TableColumn> columns;
	private JScrollPane console_scrollpane;
	private final LanguageManager lang;
	private MiscTableModel model;
	private PanelMulticastConfig pan_config;
	private PanelMulticastControl pan_control;
	private PanelGraph pan_graph;
	private final JPanel pan_left;
	private final JPanel pan_options;
	private final JPanel pan_space;
	private PanelStatusBar pan_status;
	private JPanel pan_table;
	private boolean popupsAllowed = true;
	private JTextArea ta_console;
	private JTabbedPane tab_console;
	private JXTable table;
	private JScrollPane table_scrollpane;
	private MiscBorder tablePanelBorder;

	/**
	 * Konstruktor fuer einen kompletten Programmteil in der GUI. Hierbei werden
	 * alle Komponenten fertig initialisiert.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param typ
	 *            Gibt an um welchen Programmteil es sich handelt.
	 */
	public PanelTabbed(final ViewController ctrl, final Typ typ) {
		lang = LanguageManager.getInstance();
		setLayout(new BorderLayout());
		initControlPanel(ctrl);
		initConfigPanel(ctrl, typ);
		initConsolePanel(ctrl, typ);
		initTablePanel(ctrl, typ);
		initStatusPanel(ctrl, typ);

		// Multicast Options Panel containing Control and Configuration
		pan_options = new JPanel(true);
		pan_options.setLayout(new BorderLayout());
		pan_options.add(pan_control, BorderLayout.NORTH);
		pan_options.add(pan_config, BorderLayout.EAST);

		// Space Panel, a Buffer for Resizing below the Left Panel
		pan_space = new JPanel(true);

		// The Left Panel containing the Options Panel and the Space Panel
		pan_left = new JPanel(true);
		pan_left.setLayout(new BorderLayout());
		pan_left.setPreferredSize(new Dimension(225, 500));
		pan_left.add(pan_options, BorderLayout.NORTH);
		pan_left.add(pan_space, BorderLayout.CENTER);
		// Adding all the Components to the Tab_Panel
		add(pan_left, BorderLayout.WEST);
		add(pan_table, BorderLayout.CENTER);
		add(pan_status, BorderLayout.SOUTH);
	}

	public ArrayList<TableColumn> getColumns() {
		return columns;
	}

	public JScrollPane getConsole_scrollpane() {
		return console_scrollpane;
	}

	public MiscTableModel getModel() {
		return model;
	}

	public PanelMulticastConfig getPan_config() {
		return pan_config;
	}

	public PanelMulticastControl getPan_control() {
		return pan_control;
	}

	public PanelGraph getPan_graph() {
		return pan_graph;
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

	public PanelStatusBar getPan_status() {
		return pan_status;
	}

	public JPanel getPan_table() {
		return pan_table;
	}

	public JTextArea getTa_console() {
		return ta_console;
	}

	public JTabbedPane getTab_console() {
		return tab_console;
	}

	public JXTable getTable() {
		return table;
	}

	public JScrollPane getTable_scrollpane() {
		return table_scrollpane;
	}

	public MiscTableModel getTableModel() {
		return model;
	}

	public boolean isPopupsAllowed() {
		return popupsAllowed;
	}

	public void reloadLanguage() {
		pan_control.reloadLanguage();
		pan_config.reloadLanguage();
		tablePanelBorder.setTitle(lang.getProperty("miscBorder.mcOverwiew"));
		tab_console.setTitleAt(0, lang.getProperty("tab.graph"));
		tab_console.setTitleAt(1, lang.getProperty("tab.console"));
		pan_graph.reloadLanguage();
	}

	/**
	 * Setzt die einzelnen Panels.
	 * 
	 * @param config
	 *            Config anzeigen ja / nein.
	 * @param control
	 *            Control anzeigen ja / nein.
	 * @param status
	 *            Status anzeigen ja / nein.
	 * @param console
	 *            Konsole anzeigen ja / nein.
	 * @param graph
	 *            Graph anzeigen ja / nein.
	 */
	public void setPanels(final boolean config, final boolean control,
			final boolean status, final boolean console, final boolean graph) {
		if (!config && !control) {
			pan_left.setVisible(false);
		} else {
			pan_config.setVisible(config);
			pan_control.setVisible(control);
		}
		if (!console && !graph) {
			tab_console.setVisible(false);
		} else if (!console) {
			ta_console.setVisible(false);
			tab_console.remove(1);
		} else if (!graph) {
			pan_graph.setVisible(false);
			tab_console.remove(0);
		}
		pan_status.setVisible(status);
	}

	public void setPopupsAllowed(final boolean popupsAllowed) {
		this.popupsAllowed = popupsAllowed;
	}

	/**
	 * Resettet das Aussehen der Tabelle auf das Standard Aussehen.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param typ
	 *            Gibt den Programmteil an zu welchem die Tabelle gehoert.
	 */
	public void setTableModel(final ViewController ctrl, final Typ typ) {
		model = new MiscTableModel(ctrl, typ);
		table.setModel(model);
		final TableColumnModel colmodel = table.getColumnModel();
		final Enumeration<TableColumn> e = colmodel.getColumns();
		columns = new ArrayList<TableColumn>();
		while (e.hasMoreElements()) {
			columns.add(e.nextElement());
		}
		colmodel.addColumnModelListener(ctrl);
		colmodel.getColumn(0).setMinWidth(45);
		colmodel.getColumn(1).setMinWidth(100);
		colmodel.getColumn(2).setMinWidth(100);
		colmodel.getColumn(3).setMinWidth(100);
		colmodel.getColumn(4).setMinWidth(60);
		colmodel.getColumn(5).setMinWidth(60);
		colmodel.getColumn(0).setPreferredWidth(45);
		colmodel.getColumn(1).setPreferredWidth(100);
		colmodel.getColumn(2).setPreferredWidth(100);
		colmodel.getColumn(3).setPreferredWidth(100);
		colmodel.getColumn(4).setPreferredWidth(60);
		colmodel.getColumn(5).setPreferredWidth(60);

		// V1.5: L2 und L3 Sender hinzugefuegt
		if (typ == Typ.L3_SENDER) {
			colmodel.getColumn(6).setMinWidth(50);
			colmodel.getColumn(7).setMinWidth(60);
			colmodel.getColumn(8).setMinWidth(60);
			colmodel.getColumn(9).setMinWidth(30);
			colmodel.getColumn(10).setMinWidth(60);
			colmodel.getColumn(11).setMinWidth(100);
			colmodel.getColumn(6).setPreferredWidth(50);
			colmodel.getColumn(7).setPreferredWidth(60);
			colmodel.getColumn(8).setPreferredWidth(60);
			colmodel.getColumn(9).setPreferredWidth(30);
			colmodel.getColumn(10).setPreferredWidth(60);
			colmodel.getColumn(11).setPreferredWidth(100);
		} else if (typ == Typ.L2_SENDER) {
			colmodel.getColumn(4).setMinWidth(70);
			colmodel.getColumn(5).setMinWidth(70);
			colmodel.getColumn(6).setMinWidth(80);
			colmodel.getColumn(7).setMinWidth(80);
			colmodel.getColumn(8).setMinWidth(80);
			colmodel.getColumn(9).setMinWidth(100);
			colmodel.getColumn(4).setPreferredWidth(70);
			colmodel.getColumn(5).setPreferredWidth(80);
			colmodel.getColumn(6).setPreferredWidth(80);
			colmodel.getColumn(7).setPreferredWidth(80);
			colmodel.getColumn(8).setPreferredWidth(80);
			colmodel.getColumn(9).setPreferredWidth(100);
		} else if (typ == Typ.L3_RECEIVER) {
			colmodel.getColumn(6).setMinWidth(50);
			colmodel.getColumn(7).setMinWidth(60);
			colmodel.getColumn(8).setMinWidth(60);
			colmodel.getColumn(9).setMinWidth(45);
			colmodel.getColumn(10).setMinWidth(85);
			colmodel.getColumn(11).setMinWidth(100);
			colmodel.getColumn(6).setPreferredWidth(50);
			colmodel.getColumn(7).setPreferredWidth(60);
			colmodel.getColumn(8).setPreferredWidth(60);
			colmodel.getColumn(9).setPreferredWidth(45);
			colmodel.getColumn(10).setPreferredWidth(85);
			colmodel.getColumn(11).setPreferredWidth(100);
		} else if (typ == Typ.L2_RECEIVER) {
			colmodel.getColumn(6).setMinWidth(65);
			colmodel.getColumn(7).setMinWidth(65);
			colmodel.getColumn(8).setMinWidth(65);
			colmodel.getColumn(9).setMinWidth(65);
			colmodel.getColumn(10).setMinWidth(100);
			colmodel.getColumn(6).setPreferredWidth(65);
			colmodel.getColumn(7).setPreferredWidth(65);
			colmodel.getColumn(8).setPreferredWidth(65);
			colmodel.getColumn(9).setPreferredWidth(65);
			colmodel.getColumn(10).setPreferredWidth(100);
		}
                table.getColumnExt(0).setSortable(false);
	}

	private void initConfigPanel(final ViewController ctrl, final Typ typ) {
		pan_config = new PanelMulticastConfig(ctrl, typ);
	}

	/**
	 * Initialisiert den Graph und die Console der GUI.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param typ
	 *            Gibt den Programmteil an zu welchem die Komponenten gehoeren.
	 */
	private void initConsolePanel(final ViewController ctrl, final Typ typ) {
		tab_console = new JTabbedPane();
		tab_console.setFont(MiscFont.getFont(0, 14));
		ta_console = new JTextArea("");
		ta_console.setFont(MiscFont.getFont(0, 11));
		ta_console.setEditable(false);
		console_scrollpane = new JScrollPane(ta_console);
		console_scrollpane.setPreferredSize(new Dimension(300, 100));

		// V1.5: L2_SENDER und L3_SENDER hinzugefuegt
		if ((typ == Typ.L3_SENDER) || (typ == Typ.L2_SENDER)) {
			pan_graph = new PanelGraph(500, lang.getProperty("graph.sec"),
					lang.getProperty("graph.packetsPerSec"), false);
		} else {
			pan_graph = new ReceiverGraph(ctrl);
		}
		tab_console.addTab(lang.getProperty("tab.graph"), pan_graph);
		tab_console.addTab(lang.getProperty("tab.console"), console_scrollpane);
	}

	private void initControlPanel(final ViewController ctrl) {
		pan_control = new PanelMulticastControl(ctrl);
	}

	/**
	 * Initialisiert die Statusbar.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param typ
	 *            Gibt den Programmteil an zu welchem die Statusbar gehoert.
	 */
	private void initStatusPanel(final ViewController ctrl, final Typ typ) {
		pan_status = new PanelStatusBar();
	}

	/**
	 * Initialisiert die Tabelle.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param typ
	 *            Gibt den Programmteil an zu welchem die Tabelle gehoert.
	 */
	private void initTablePanel(final ViewController ctrl, final Typ typ) {
		pan_table = new JPanel(true);
		table = new JXTable();
		setTableModel(ctrl, typ);

		table.setFont(MiscFont.getFont(0, 10));
		table.getTableHeader().setFont(MiscFont.getFont(0, 10));
		table.getSelectionModel().addListSelectionListener(ctrl);
		table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
		table.getTableHeader().addMouseListener(ctrl);

		table_scrollpane = new JScrollPane(table);
		table_scrollpane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		table_scrollpane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pan_table.setLayout(new BorderLayout());
		pan_table.setBorder(tablePanelBorder = new MiscBorder(lang
				.getProperty("miscBorder.mcOverwiew")));
		pan_table.add(tab_console, BorderLayout.SOUTH);
		pan_table.add(table_scrollpane, BorderLayout.CENTER);
	}

}
