   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.program.view;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.model.XmlParserWorkbench;

import net.miginfocom.swing.MigLayout;

/**
 * Hauptfenster des MultiCastor Tools
 * 
 * @author Daniel Becker
 * 
 */
@SuppressWarnings("serial")
public class FrameMain extends JFrame {
	private static final ResourceBundle messages = ResourceBundle
			.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$

	// GUI element variables
	/**
	 * Das Tabpanel mit welchem man durch die Programmteile schalten kann.
	 */
	// Console
	private JTextArea ta_console;
	// Graphic and Console in JTabbedPane
	// private JTabbedPane tab_console;
	// Graph
	private PanelGraph pan_graph;
	// Scrollpane for Console
	private JScrollPane console_scrollpane;

	protected JPanel pan_table = new JPanel();
	protected JTable table = new JTable();
	protected JScrollPane table_scrollpane = new JScrollPane();
	protected boolean autoSave;
	// protected PanelButtonBar panelButtonBar;

	/*
	 * Weitere Standard GUI Komponenten welche benï¿½tigt werden
	 */
	private JMenuBar mb_menubar;

	// nicht benï¿½tigte Elemente
	protected JMenuItem mi_profile1;
	protected JMenuItem mi_profile2;
	protected JMenuItem mi_profile3;

	protected JMenu m_file;
	protected JMenuItem mi_saveconfig;
	protected JMenuItem mi_loadconfig;
	protected JMenuItem mi_close;
	private ArrayList<JMenuItem> lastconfigs = new ArrayList<JMenuItem>();

	private JMenu m_mode;
	private JMenuItem mi_sender;
	private JMenuItem mi_receiver;

	public static final String MI_SENDER = "mi_sender"; //$NON-NLS-1$
	public static final String MI_RECEIVER = "mi_receiver"; //$NON-NLS-1$
	public static final String MI_ABOUT = "mi_about"; //$NON-NLS-1$
	public static final String MI_MANUAL = "mi_manual"; //$NON-NLS-1$
	public static final String MI_LAST_CONFIGS = "mi_last_configs";

	private JMenu m_help;
	private JMenuItem mi_manual;
	private JMenu m_language;
	private JMenuItem mi_about;

	private ButtonGroup bg_scale;
	private JRadioButton rb_custom;
	protected ImageIcon img_close;
	private FrameFileChooser fc_save;

	public Vector<String> getLastConfigs() {
		return lastConfigs;
	}

	private FrameFileChooser fc_load;
	private int aboutPanelState = 0; // 0 = invisible, 1 = visible, closeButton
										// unhovered, 2 = visible close button
										// hovered
	private Vector<String> lastConfigs = new Vector<String>();
	private PanelButtonBar panelButtonBar;
	private PanelStatusBar panelStatusBar;
	private PanelMulticastInfo pan_info;
	private PanelMulticastconfigNewContext panelMulticastconfigNewContext;
	private PanelTable panelTable;

	private ViewController viewCtrl = null;
	private JTabbedPane tab_console;
	private JRadioButtonMenuItem rdbtnEnglish;
	private JRadioButtonMenuItem rdbtnGerman;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Konstruktor welche das Hauptfenster des Multicastor tools erstellt,
	 * konfiguriert und anzeigt.
	 * 
	 * @param ctrl
	 *            Benï¿½tigte Referenz zum GUI Controller
	 */
	public FrameMain(ViewController ctrl) {
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		viewCtrl = ctrl;
		setMinimumSize(new Dimension(850, 694));
		initMenuBar(ctrl);
		initComponent();
		initFrameFileChooser(ctrl);
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/dhbw/multicastor/resources/images/icon.png")); //$NON-NLS-1$

		setIconImage(icon.getImage());

		if (viewCtrl.getCurrentModus() == Modus.SENDER) {
			this.setTitle(messages.getString("FrameMain.senderTitle")); //$NON-NLS-1$
		} else if (viewCtrl.getCurrentModus() == Modus.RECEIVER) {
			this.setTitle(messages.getString("FrameMain.receiverTitle")); //$NON-NLS-1$
		}

		this.setVisible(true);
		this.addComponentListener(ctrl);
		this.addWindowListener(ctrl);
		// this.addKeyListener(ctrl);
		// this.addWindowListener(ctrl);
		// System.out.println("p: "+tabpane.getIconAt(4).toString());
		// System.out.println("w: "+tabpane.getIconAt(4).getIconWidth());
		// System.out.println("h: "+tabpane.getIconAt(4).getIconHeight());

	}

	private void initFrameFileChooser(ViewController ctrl) {
		fc_load = new FrameFileChooser(ctrl, false);
		fc_save = new FrameFileChooser(ctrl, true);

	}

	/**
	 * Funktion welche die Menubar initialisiert.
	 * 
	 * @param ctrl
	 *            Benï¿½tigte Referenz zum GUI Controller.
	 */
	protected void initMenuBar(ViewController ctrl) {

		// FileMenu erstellen
		mi_saveconfig = new JMenuItem(
				messages.getString("FrameMain.mi_saveconfig.text"), new ImageIcon(getClass().getResource("/dhbw/multicastor/resources/images/save.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_saveconfig.setFont(MiscFont.getFont(0, 14));
		mi_saveconfig.addActionListener(ctrl);
		mi_loadconfig = new JMenuItem(
				messages.getString("FrameMain.mi_loadconfig.text"), new ImageIcon(getClass().getResource("/dhbw/multicastor/resources/images/load.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_loadconfig.setFont(MiscFont.getFont(0, 14));
		mi_loadconfig.addActionListener(ctrl);

		mi_close = new JMenuItem(
				messages.getString("FrameMain.mi_close.text"), new ImageIcon(getClass().getResource("/dhbw/multicastor/resources/images/exit.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_close.setFont(MiscFont.getFont(0, 14));
		mi_close.addActionListener(ctrl);

		m_file = new JMenu(messages.getString("FrameMain.m_file.text")); //$NON-NLS-1$
		m_file.setFont(MiscFont.getFont(0, 16));
		m_file.add(mi_saveconfig);
		m_file.add(mi_loadconfig);
		m_file.add(new Separator());

		_initLastConfigFiles();

		m_file.add(mi_close);

		// ModeMenu erstellen
		mi_sender = new JMenuItem(
				messages.getString("FrameMain.mi_sender.text"), new ImageIcon(getClass().getResource("/dhbw/multicastor/resources/images/sender.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_sender.setFont(MiscFont.getFont(0, 14));
		mi_sender.setActionCommand(MI_SENDER);
		mi_sender.addActionListener(ctrl);
		mi_receiver = new JMenuItem(
				messages.getString("FrameMain.mi_receiver.text"), new ImageIcon(getClass().getResource("/dhbw/multicastor/resources/images/receiver.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_receiver.setFont(MiscFont.getFont(0, 14));
		mi_receiver.setActionCommand(MI_RECEIVER);
		mi_receiver.addActionListener(ctrl);
		m_mode = new JMenu(messages.getString("FrameMain.m_mode.text")); //$NON-NLS-1$
		m_mode.add(mi_sender);
		m_mode.add(mi_receiver);
		m_mode.setFont(MiscFont.getFont(0, 16));

		// HelpMenu erstellen
		mi_manual = new JMenuItem(
				messages.getString("FrameMain.mi_manual.text")); //$NON-NLS-1$
		mi_manual.setFont(MiscFont.getFont(0, 14));
		mi_manual.setActionCommand(MI_MANUAL);
		mi_manual.addActionListener(ctrl);
		m_language = new JMenu(messages.getString("FrameMain.m_language.text")); //$NON-NLS-1$
		m_language.setFont(MiscFont.getFont(0, 14));

		rdbtnEnglish = new JRadioButtonMenuItem(
				messages.getString("FrameMain.rdbtnmntmNewRadioItem.text")); //$NON-NLS-1$
		rdbtnEnglish.addActionListener(viewCtrl);
		rdbtnEnglish
				.setIcon(new ImageIcon(
						FrameMain.class
								.getResource("/dhbw/multicastor/resources/images/flagGB.png"))); //$NON-NLS-1$
		rdbtnEnglish.setFont(UIManager.getFont("ProgressBar.font")); //$NON-NLS-1$

		buttonGroup.add(rdbtnEnglish);
		if (Locale.getDefault().toString().equals("en_US")) { //$NON-NLS-1$
			rdbtnEnglish.setSelected(true);
		}
		m_language.add(rdbtnEnglish);

		rdbtnGerman = new JRadioButtonMenuItem(
				messages.getString("FrameMain.rdbtnmntmNewRadioItem_1.text")); //$NON-NLS-1$
		rdbtnGerman.addActionListener(viewCtrl);
		rdbtnGerman.setIcon(new ImageIcon(FrameMain.class
				.getResource("/dhbw/multicastor/resources/images/flagD.png"))); //$NON-NLS-1$
		buttonGroup.add(rdbtnGerman);
		if (Locale.getDefault().toString().equals("de_DE")) { //$NON-NLS-1$
			rdbtnGerman.setSelected(true);
		}
		m_language.add(rdbtnGerman);
		mi_about = new JMenuItem(messages.getString("FrameMain.mi_about.text")); //$NON-NLS-1$
		mi_about.setFont(MiscFont.getFont(0, 14));
		mi_about.setActionCommand(MI_ABOUT);
		mi_about.addActionListener(ctrl);
		m_help = new JMenu(messages.getString("FrameMain.m_help.text")); //$NON-NLS-1$
		m_help.setFont(MiscFont.getFont(0, 16));
		m_help.add(mi_manual);
		m_help.add(m_language);
		m_help.add(mi_about);

		// Menubar erstellen
		mb_menubar = new JMenuBar();
		mb_menubar.add(m_file);
		mb_menubar.add(m_mode);
		mb_menubar.add(m_help);
		setJMenuBar(mb_menubar);
	}

	public void initComponent() {
		getContentPane()
				.setLayout(
						new MigLayout(
								"", "[270px:n:270px,grow][grow]", "[135.00px:n:140px,grow][50px:n:50px,grow][350px:n:350px,grow][grow][20px:n:20px,grow,bottom]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		pan_info = new PanelMulticastInfo();
		pan_info.setMinimumSize(new Dimension(270, 175));
		pan_info.setPreferredSize(new Dimension(270, 175));
		getContentPane().add(pan_info, "cell 0 0 1 2,grow"); //$NON-NLS-1$

		tab_console = new JTabbedPane(JTabbedPane.TOP);
		tab_console.setFont(MiscFont.getFont(0, 14));
		ta_console = new JTextArea();
		ta_console.setFont(MiscFont.getFont(0, 11));
		ta_console.setEditable(false);
		console_scrollpane = new JScrollPane(ta_console);
		console_scrollpane.setPreferredSize(new Dimension(300, 100));
		if (viewCtrl.getCurrentModus() == Modus.SENDER) {
			pan_graph = new PanelGraph(500,
					"sec", "Packets per Second (total)", false); //$NON-NLS-1$ //$NON-NLS-2$
		} else if (viewCtrl.getCurrentModus() == Modus.RECEIVER) {
			pan_graph = new ReceiverGraph(viewCtrl);
		}
		tab_console.addTab(
				messages.getString("FrameMain.tabGraphTitle"), pan_graph); //$NON-NLS-1$
		tab_console
				.addTab(messages.getString("FrameMain.tabConsoleTitle"), console_scrollpane); //$NON-NLS-1$
		getContentPane().add(tab_console, "cell 1 0,grow"); //$NON-NLS-1$

		panelButtonBar = new PanelButtonBar(viewCtrl);
		getContentPane().add(panelButtonBar, "cell 1 1,growx,aligny center"); //$NON-NLS-1$

		panelMulticastconfigNewContext = new PanelMulticastconfigNewContext(
				viewCtrl);
		getContentPane().add(panelMulticastconfigNewContext, "cell 0 2,grow"); //$NON-NLS-1$

		panelTable = new PanelTable(viewCtrl);
		getContentPane().add(panelTable, "cell 1 2 1 2,grow"); //$NON-NLS-1$

		panelStatusBar = new PanelStatusBar();
		getContentPane().add(panelStatusBar, "cell 0 4 2 1,grow"); //$NON-NLS-1$

	}

	public PanelStatusBar getPanelStatusBar() {
		return this.panelStatusBar;
	}

	public void switchModus() {
		final int width = this.getWidth();

		getContentPane().remove(panelMulticastconfigNewContext);
		panelMulticastconfigNewContext = new PanelMulticastconfigNewContext(
				viewCtrl);
		getContentPane().add(panelMulticastconfigNewContext, "cell 0 2,grow"); //$NON-NLS-1$
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				getContentPane().remove(panelTable);
				panelTable = new PanelTable(viewCtrl);
				if (width > 1040) {
					getTable().setAutoResizeMode(
							JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
				}
				getContentPane().add(panelTable, "cell 1 2 1 2,grow"); //$NON-NLS-1$
				tab_console.remove(pan_graph);
				getContentPane().remove(pan_graph);
				if (viewCtrl.getCurrentModus() == Modus.SENDER) {
					pan_graph = new PanelGraph(500,
							"sec", "Packets per Second (total)", false); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (viewCtrl.getCurrentModus() == Modus.RECEIVER) {
					pan_graph = new ReceiverGraph(viewCtrl);
				}
				tab_console.insertTab("Graph", null, pan_graph, null, 0); //$NON-NLS-1$
				tab_console.setSelectedIndex(0);
				getContentPane().validate();
				getContentPane().repaint();

			}
		});
		// getContentPane().remove(panelMulticastconfigNewContext);
		// panelMulticastconfigNewContext = new
		// PanelMulticastconfigNewContext(viewCtrl);
		//		getContentPane().add(panelMulticastconfigNewContext, "cell 0 2,grow"); //$NON-NLS-1$
		// getContentPane().remove(panelTable);
		// panelTable = new PanelTable(viewCtrl);
		// if(this.getWidth() > 1040){
		// getTable().setAutoResizeMode(
		// JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// }
		//		getContentPane().add(panelTable, "cell 1 2 1 2,grow"); //$NON-NLS-1$
		// tab_console.remove(pan_graph);
		// getContentPane().remove(pan_graph);
		// if(viewCtrl.getCurrentModus() == Modus.SENDER){
		//			pan_graph = new PanelGraph(500, "sec", "Packets per Second (total)", false); //$NON-NLS-1$ //$NON-NLS-2$
		// }else if (viewCtrl.getCurrentModus() == Modus.RECEIVER){
		// pan_graph = new ReceiverGraph(viewCtrl);
		// }
		//		tab_console.insertTab("Graph", null, pan_graph, null, 0); //$NON-NLS-1$
		// tab_console.setSelectedIndex(0);
		// getContentPane().validate();
		// getContentPane().repaint();
	}

	public FrameFileChooser getFc_save() {
		return fc_save;
	}

	public FrameFileChooser getFc_load() {
		return fc_load;
	}

	public PanelMulticastInfo getPan_info() {
		return pan_info;
	}

	public PanelButtonBar getPanelButtonBar() {
		return panelButtonBar;
	}

	public JMenuBar getMb_menubar() {
		return mb_menubar;
	}

	public JMenu getM_menu() {
		return m_file;
	}

	public JMenu getM_options() {
		return m_mode;
	}

	public JMenu getM_info() {
		return m_help;
	}

	public ButtonGroup getBg_scale() {
		return bg_scale;
	}

	public PanelMulticastconfigNewContext getPanelMulticastconfigNewContext() {
		return panelMulticastconfigNewContext;
	}

	public JMenuItem getMi_saveconfig() {
		return mi_saveconfig;
	}

	public JMenuItem getMi_loadconfig() {
		return mi_loadconfig;
	}

	public JMenuItem getMi_exit() {
		return mi_close;
	}

	public JMenuItem getMi_about() {
		return mi_about;
	}

	public JMenuItem getMi_manual() {
		return mi_manual;
	}

	public JMenuItem getMi_profile1() {
		return mi_profile1;
	}

	public JMenuItem getMi_profile2() {
		return mi_profile2;
	}

	public JMenuItem getMi_profile3() {
		return mi_profile3;
	}

	public ImageIcon getImg_close() {
		return img_close;
	}

	public int getAboutPanelState() {
		return aboutPanelState;
	}

	public void setAboutPanelState(int i) {
		aboutPanelState = i;
	}

	public JRadioButton getRb_custom() {
		return rb_custom;
	}

	public void setLastConfigs(Vector<String> l, boolean live) {
		lastConfigs = l;
		if (lastConfigs != null && lastConfigs.size() > 0) {
			if (lastConfigs.size() > 0) {
				m_file.remove(mi_close);
				if (live) {
					// m_file.remove(mi_separator);
				}
				// mi_profile1.setText(lastConfigs.get(0));
				// m_file.add(mi_profile1);
				// if(lastConfigs.size() > 1){
				// mi_profile2.setText(lastConfigs.get(1));
				// m_file.add(mi_profile2);
				// }
				// if(lastConfigs.size() > 2){
				// mi_profile3.setText(lastConfigs.get(2));
				// m_file.add(mi_profile3);
				// }
				// m_file.add(mi_separator);
				m_file.add(mi_close);
			}
		}
	}

	public void updateLastConfigs(String s) {
		if (lastConfigs.size() > 2) {
			lastConfigs.remove(0);
		}
		lastConfigs.add(s);
		setLastConfigs(lastConfigs, true);
	}

	public void setAutoSave(boolean b) {
		autoSave = b;
	}

	public JTable getTable() {
		return this.panelTable.getTable();
	}

	public ArrayList<Integer> getSelectedRowsId() {
		int array[] = table.getSelectedRows();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int i : array) {
			ids.add((Integer) table.getValueAt(i, 0));
		}
		return ids;
	}

	public JTextArea getTa_console() {
		return ta_console;
	}

	public PanelGraph getPan_graph() {
		return pan_graph;
	}

	public JTabbedPane getTab_console() {
		return tab_console;
	}

	public PanelTable getPanelTable() {
		return panelTable;
	}

	public JScrollPane getTable_scrollpane() {
		return table_scrollpane;
	}

	public JRadioButtonMenuItem getRdbtnEnglish() {
		return rdbtnEnglish;
	}

	public JRadioButtonMenuItem getRdbtnGerman() {
		return rdbtnGerman;
	}

	private void _initLastConfigFiles() {
		ArrayList<String> paths = XmlParserWorkbench.getLastConfigFiles();
		int count = 0;
		for (String curpath : paths) {
			if (count < 4) {
				JMenuItem mi_path = new JMenuItem(
						curpath,
						new ImageIcon(getClass().getResource(
								"/dhbw/multicastor/resources/images/load.png")));
				mi_path.setFont(MiscFont.getFont(0, 14));
				mi_path.addActionListener(viewCtrl);
				mi_path.setActionCommand(MI_LAST_CONFIGS);
				lastconfigs.add(mi_path);
				m_file.add(mi_path);
				count++;
			}
		}
		if (paths.size() > 0) {
			m_file.add(new Separator());
		}
	}

	public void resetMenubar() {
		this.remove(mb_menubar);
		initMenuBar(viewCtrl);
	}

	public ArrayList<JMenuItem> getLastconfigs() {
		return lastconfigs;
	}
}
