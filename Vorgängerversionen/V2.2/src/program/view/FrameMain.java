package program.view;

import java.awt.Dimension;
import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import program.controller.Messages;
import program.controller.ViewController;
import program.data.MulticastData.Typ;
/**
 * Hauptfenster des MultiCastor Tools
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class FrameMain extends JFrame {

	//GUI element variables
	/**
	 * Das Tabpanel mit welchem man durch die Programmteile schalten kann.
	 */
	private JTabbedPane tabpane;

	/**
	 *  Das IPv4Receiver Panel
	 */
	private PanelTabbed panel_receiver;
	/**
	 *  Das IPv4Sender Panel
	 */
	private PanelTabbed panel_sender;
	/*
	 * Weitere Standard GUI Komponenten welche ben�tigt werden
	 */
	private JMenuBar mb_menubar;
	private JMenu m_menu;
	private JMenu m_options;
	private JMenu m_info;
	private JMenuItem mi_saveconfig;
	private JMenuItem mi_loadconfig;
	private JMenuItem mi_saveprofile;
	private JMenuItem mi_loadprofile;
	private JMenuItem mi_exit;
	private JMenuItem mi_help;
	private JMenuItem mi_about;
	private JMenuItem mi_profile1;
	private JMenuItem mi_profile2;
	private JMenuItem mi_profile3;
	private JMenuItem mi_options;
	private JMenuItem mi_settings;
	private JMenuItem mi_autoSave;
	private ImageIcon img_close;
	private FrameFileChooser fc_save;
	private FrameFileChooser fc_load;
	private FrameFileChooser fc_save_profile;
	private FrameFileChooser fc_load_profile;
	public Vector<String> getLastConfigs() {
		return lastConfigs;
	}

	private Vector<String> lastConfigs=new Vector<String>();
	private Separator mi_separator;

	/**
	 * Konstruktor welche das Hauptfenster des Multicastor tools erstellt, konfiguriert und anzeigt.
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 */
	public FrameMain(ViewController ctrl) {
		initWindow(ctrl);
		initMenuBar(ctrl);
		initPanels(ctrl);
		this.addComponentListener(ctrl);
		this.addKeyListener(ctrl);
		this.addWindowListener(ctrl);
	}
	/**
	 * Funktion welche die Menubar initialisiert.
	 * @param ctrl Ben�tigte Referenz zum GUI Controller.
	 */
	private void initMenuBar(ViewController ctrl) {
		mi_autoSave = new JMenuItem("Settings");
		mi_autoSave.setFont(MiscFont.getFont(0,12));
		mi_autoSave.addItemListener(ctrl);
		mi_autoSave.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/settings.png")).getImage().getScaledInstance(25, -1, Image.SCALE_SMOOTH)));
		mi_saveconfig = new JMenuItem(Messages.getString("FrameMain.2"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/save.png")));
		mi_saveconfig.setFont(MiscFont.getFont(0,12));
		mi_saveconfig.addActionListener(ctrl);
		mi_loadconfig = new JMenuItem(Messages.getString("FrameMain.4"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_loadconfig.setFont(MiscFont.getFont(0,12));
		mi_loadconfig.addActionListener(ctrl);
		mi_saveprofile = new JMenuItem(Messages.getString("FrameMain.6"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/save.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_saveprofile.setFont(MiscFont.getFont(0,12));
		mi_saveprofile.addActionListener(ctrl);
		mi_loadprofile = new JMenuItem(Messages.getString("FrameMain.8"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_loadprofile.setFont(MiscFont.getFont(0,12));
		mi_loadprofile.addActionListener(ctrl);
		mi_profile1 = new JMenuItem(Messages.getString("FrameMain.10"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_profile1.setFont(MiscFont.getFont(0,12));
		mi_profile2 = new JMenuItem(Messages.getString("FrameMain.10"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_profile2.setFont(MiscFont.getFont(0,12));
		mi_profile3 = new JMenuItem(Messages.getString("FrameMain.10"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_profile3.setFont(MiscFont.getFont(0,12));
		mi_separator = new Separator();
		mi_exit = new JMenuItem(Messages.getString("FrameMain.18"), new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/exit.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_exit.setFont(MiscFont.getFont(0,12));
		mi_exit.addActionListener(ctrl);
		mi_help = new JMenuItem(Messages.getString("FrameMain.20"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/info.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_help.setFont(MiscFont.getFont(0,12));
		mi_help.addActionListener(ctrl);
		mi_about = new JMenuItem(Messages.getString("FrameMain.about"),new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/info.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		mi_about.setFont(MiscFont.getFont(0,12));
		mi_about.addActionListener(ctrl);
		mi_settings = new JMenuItem(Messages.getString("FrameMain.settings"), new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/settings.png")));
		mi_settings.setFont(MiscFont.getFont(0,12));
		mi_settings.addActionListener(ctrl);
		m_menu = new JMenu(Messages.getString("FrameMain.25")); //$NON-NLS-1$
		m_menu.setFont(MiscFont.getFont(0,14));
		m_options = new JMenu(Messages.getString("FrameMain.26")); //$NON-NLS-1$
		m_options.setFont(MiscFont.getFont(0,14));
		m_options.addActionListener(ctrl);
		m_info = new JMenu(Messages.getString("FrameMain.29")); //$NON-NLS-1$
		m_info.setFont(MiscFont.getFont(0,14));
		m_info.add(mi_help);
		m_info.add(mi_about);
		//m_scale.add(rb_custom);
		m_options.add(mi_settings);
		m_menu.add(mi_saveconfig);
		m_menu.add(mi_loadconfig);
		m_menu.add(mi_saveprofile);
		m_menu.add(mi_loadprofile);


		mi_profile1.addActionListener(ctrl);
		mi_profile1.setActionCommand("lastConfig1"); //$NON-NLS-1$
		mi_profile2.addActionListener(ctrl);
		mi_profile2.setActionCommand("lastConfig2"); //$NON-NLS-1$
		mi_profile3.addActionListener(ctrl);
		mi_profile3.setActionCommand("lastConfig3"); //$NON-NLS-1$
		m_menu.add(new Separator());
		m_menu.add(mi_exit);
		mb_menubar = new JMenuBar();
		mb_menubar.add(m_menu);
		mb_menubar.add(m_options);
		mb_menubar.add(m_info);
		setJMenuBar(mb_menubar);
	}
	/**
	 * Funktion welche die Panels initialisiert.
	 * @param ctrl Ben�tigte Referenz zum GUI Controller.
	 */
	private void initPanels(ViewController ctrl) {
		panel_receiver = new PanelTabbed(ctrl,Typ.RECEIVER_V4);
		panel_sender = new PanelTabbed(ctrl,Typ.SENDER_V4);
		img_close = new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/close_icon.gif")); //$NON-NLS-1$

		tabpane = new JTabbedPane();
		/*
		 * Diese Methode versteckt die Tableiste
		 */
		tabpane.setUI(new BasicTabbedPaneUI() {
			@Override
			protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
				//Lässt die tableiste verschwinden
				return -2;
			}
		});
		tabpane.addMouseListener(ctrl);
		tabpane.addMouseMotionListener(ctrl);
		tabpane.addTab(Messages.getString("FrameMain.34"), panel_sender); //$NON-NLS-1$
		tabpane.addTab(Messages.getString("FrameMain.35"), panel_receiver); //$NON-NLS-1$
		tabpane.setIconAt(0, new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4receiver.png"))); //$NON-NLS-1$
		tabpane.setIconAt(1, new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4sender.png"))); //$NON-NLS-1$
		tabpane.setSelectedIndex(0);
		tabpane.setFont(MiscFont.getFont(0,17));
		tabpane.setFocusable(false);
		tabpane.addChangeListener(ctrl);
		add(tabpane);
	}
	/**
	 * Funktion welche das Frame initialisiert
	 * @param ctrl Ben�tigte Referenz zum GUI Controller.
	 */
	private void initWindow(ViewController ctrl) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) { }

		setSize(1000,516);
		//setResizable(false);
		setMinimumSize(new Dimension(700,516));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/icon.png")); //$NON-NLS-1$

		setIconImage(icon.getImage());
		fc_save = new FrameFileChooser(ctrl, true, true);
		fc_load = new FrameFileChooser(ctrl, false, true);
		fc_save_profile = new FrameFileChooser(ctrl, true, false);
		fc_load_profile = new FrameFileChooser(ctrl, false, false);
	}
	public FrameFileChooser getFc_save_profile() {
		return fc_save_profile;
	}
	public FrameFileChooser getFc_load_profile() {
		return fc_load_profile;
	}
	public FrameFileChooser getFc_save() {
		return fc_save;
	}

	public FrameFileChooser getFc_load() {
		return fc_load;
	}

	public Dimension getGraphSize(){
		return panel_receiver.getGraphSize();
	}

	public JTabbedPane getTabpane() {
		return tabpane;
	}
	public PanelTabbed getPanelPart(Typ typ){
		PanelTabbed ret = null;
		switch(typ){
		case SENDER_V4: ret=panel_sender; break;
		case RECEIVER_V4: ret=panel_receiver;break;
		}
		return ret;
	}

	public PanelTabbed getPanel_receiver() {
		return panel_receiver;
	}

	public PanelTabbed getPanel_sender() {
		return panel_sender;
	}


	public PanelMulticastConfig getPan_config(Typ typ) {
		if(typ==Typ.RECEIVER_MMRP || typ==Typ.RECEIVER_V4 ||typ==Typ.RECEIVER_V6){
			return panel_receiver.getPan_config(typ);
		}else {
			return panel_sender.getPan_config(typ);
		}

	}

	public PanelTabbed getPanel(Typ typ) {
		if(typ==Typ.RECEIVER_MMRP || typ==Typ.RECEIVER_V4 ||typ==Typ.RECEIVER_V6){
			return panel_receiver;
		}else{
			return panel_sender;
		}
	}

	public PanelTabpaneConfiguration getConfigTabPane(Typ typ) {
		if(Typ.isReceiver(typ)){
			return panel_receiver.getPan_TabsConfig();
		}else{
			return panel_sender.getPan_TabsConfig();
		}
	}

	public JMenuBar getMb_menubar() {
		return mb_menubar;
	}

	public JMenu getM_menu() {
		return m_menu;
	}

	public JMenu getM_options() {
		return m_options;
	}

	public JMenu getM_info() {
		return m_info;
	}

	public JMenuItem getMi_saveconfig() {
		return mi_saveconfig;
	}

	public JMenuItem getMi_loadconfig() {
		return mi_loadconfig;
	}

	public JMenuItem getMi_saveprofile() {
		return mi_saveprofile;
	}
	public JMenuItem getMi_loadprofile() {
		return mi_loadprofile;
	}
	public JMenuItem getMi_exit() {
		return mi_exit;
	}

	public JMenuItem getMi_help() {
		return mi_help;
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

	public JMenuItem getMi_options() {
		return mi_options;
	}
	public void setMi_options(JMenuItem mi_options) {
		this.mi_options = mi_options;
	}
	public ImageIcon getImg_close() {
		return img_close;
	}

	public boolean isAutoSaveEnabled(){
		return mi_autoSave.isSelected();
	}

	public JMenuItem getMi_autoSave() {
		return mi_autoSave;
	}

	public JMenuItem getMi_settings() {
		return mi_settings;
	}
	public JMenuItem getMi_about() {
		return mi_about;
	}
	public void setMi_settings(JMenuItem mi_settings) {
		this.mi_settings = mi_settings;
	}
	public void setLastConfigs(Vector<String> l, boolean live) {
	
	}
	public void updateLastConfigs(String s){
		
	}
	public void setAutoSave(boolean b){
		if(b){
			mi_autoSave.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/check.png"))); //$NON-NLS-1$
			mi_autoSave.setSelected(true);
		}
		else{
			mi_autoSave.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/uncheck.png"))); //$NON-NLS-1$
			mi_autoSave.setSelected(false);
		}
	}
}
