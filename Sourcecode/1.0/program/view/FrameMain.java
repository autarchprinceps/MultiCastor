package zisko.multicastor.program.view;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.Locale;
import java.util.Vector;

import javax.swing.*;
import javax.swing.JPopupMenu.Separator;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.data.UserlevelData.Userlevel;
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
	private PanelTabbed panel_rec_ipv4;
	/**
	 *  Das IPv4Sender Panel
	 */
	private PanelTabbed panel_sen_ipv4;
	/**
	 *  Das IPv6Receiver Panel
	 */
	private PanelTabbed panel_rec_ipv6;
	/**
	 *  Das IPv6Sender Panel
	 */
	private PanelTabbed panel_sen_ipv6;
	/**
	 *  Das About Panel
	 */
	private PanelAbout panel_about;
	/*
	 * Weitere Standard GUI Komponenten welche benötigt werden 
	 */
	private JMenuBar mb_menubar;
	private JMenu m_menu;
	private JMenu m_options;
	private JMenu m_scale;
	private JMenu m_info;
	private ButtonGroup bg_scale;
	private JRadioButton rb_beginner;
	private JRadioButton rb_expert;
	private JRadioButton rb_custom;
	private JMenuItem mi_saveconfig;
	private JMenuItem mi_loadconfig;
	private JMenuItem mi_exit;
	private JMenuItem mi_about;
	private JMenuItem mi_snake;
	private JMenuItem mi_profile1;
	private JMenuItem mi_profile2;
	private JMenuItem mi_profile3;
	private JMenuItem mi_aSave;
	private JCheckBox mi_autoSave;
	private ImageIcon img_close;
	private FrameFileChooser fc_save;
	public Vector<String> getLastConfigs() {
		return lastConfigs;
	}
	private FrameFileChooser fc_load;
	private int aboutPanelState = 0; // 0 = invisible, 1 = visible, closeButton unhovered, 2 = visible close button hovered
	private Userlevel level = Userlevel.EXPERT;
	private Vector<String> lastConfigs=new Vector<String>();
	private Separator mi_separator;
	/**
	 * Konstruktor welche das Hauptfenster des Multicastor tools erstellt, konfiguriert und anzeigt.
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 */
	public FrameMain(ViewController ctrl) {
		initWindow(ctrl);
		initMenuBar(ctrl);
		initPanels(ctrl);
		setVisible(true);
		//System.out.println("<PROGRAM START>");
		this.addComponentListener(ctrl);
		this.addKeyListener(ctrl);
		this.addWindowListener(ctrl);
//		System.out.println("p: "+tabpane.getIconAt(4).toString());
//		System.out.println("w: "+tabpane.getIconAt(4).getIconWidth());
//		System.out.println("h: "+tabpane.getIconAt(4).getIconHeight());
	}
	/**
	 * Funktion welche die Menubar initialisiert.
	 * @param ctrl Benötigte Referenz zum GUI Controller.
	 */
	private void initMenuBar(ViewController ctrl) {
		mi_autoSave = new JCheckBox("AutoSave");
		mi_autoSave.setFont(MiscFont.getFont(0,14));
		mi_autoSave.addItemListener(ctrl);
		mi_autoSave.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/uncheck.png")));
		mi_saveconfig = new JMenuItem("Save Configuration",new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/save.png")));
		mi_saveconfig.setFont(MiscFont.getFont(0,14));
		mi_saveconfig.addActionListener(ctrl);
		mi_loadconfig = new JMenuItem("Load Configuration",new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png")));
		mi_loadconfig.setFont(MiscFont.getFont(0,14));
		mi_loadconfig.addActionListener(ctrl);
		mi_profile1 = new JMenuItem("error file not found",new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png")));
		mi_profile1.setFont(MiscFont.getFont(0,14));
		mi_profile2 = new JMenuItem("error file not found",new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png")));
		mi_profile2.setFont(MiscFont.getFont(0,14));
		mi_profile3 = new JMenuItem("error file not found",new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/load.png")));
		mi_profile3.setFont(MiscFont.getFont(0,14));
		mi_separator = new Separator();
		mi_snake = new JMenuItem("Snake",new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ksnake.png")));
		mi_snake.setFont(MiscFont.getFont(0,14));
		mi_snake.addActionListener(ctrl);
		mi_exit = new JMenuItem("Exit", new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/exit.png")));
		mi_exit.setFont(MiscFont.getFont(0,14));
		mi_exit.addActionListener(ctrl);
		bg_scale = new ButtonGroup();
		mi_about = new JMenuItem("About",new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/info.png")));
		mi_about.setFont(MiscFont.getFont(0,14));
		mi_about.addActionListener(ctrl);
		rb_beginner = new JRadioButton("Beginner");
		rb_beginner.setFont(MiscFont.getFont(0,14));
		rb_beginner.addItemListener(ctrl);
		rb_expert = new JRadioButton("Expert",true);
		rb_expert.setFont(MiscFont.getFont(0,14));
		rb_expert.addItemListener(ctrl);
		rb_custom = new JRadioButton("Custom");
		rb_custom.setFont(MiscFont.getFont(0,14));
		rb_custom.addItemListener(ctrl);
		bg_scale.add(rb_beginner);
		bg_scale.add(rb_expert);
		bg_scale.add(rb_custom);
		m_menu = new JMenu("Menu");
		m_menu.setFont(MiscFont.getFont(0,16));
		m_options = new JMenu("Options");
		m_options.setFont(MiscFont.getFont(0,16));
		m_scale = new JMenu("User Level");
		m_scale.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/users.png")));
		m_scale.setFont(MiscFont.getFont(0,16));
		m_info = new JMenu("Info");
		m_info.setFont(MiscFont.getFont(0,16));
		m_info.add(mi_about);
		m_info.add(mi_snake);
		m_scale.add(rb_beginner);
		m_scale.add(rb_expert);
		//m_scale.add(rb_custom);
		m_options.add(m_scale);
		m_options.add(mi_autoSave);
		m_menu.add(mi_saveconfig);
		m_menu.add(mi_loadconfig);

		mi_profile1.addActionListener(ctrl);
		mi_profile1.setActionCommand("lastConfig1");
		mi_profile2.addActionListener(ctrl);
		mi_profile2.setActionCommand("lastConfig2");
		mi_profile3.addActionListener(ctrl);
		mi_profile3.setActionCommand("lastConfig3");
		m_menu.add(new Separator());
		m_menu.add(mi_exit);
		mb_menubar = new JMenuBar();
		mb_menubar.add(m_menu);
		mb_menubar.add(m_options);
		mb_menubar.add(m_info);
		setJMenuBar(mb_menubar);
	}
	/**
	 * Hilfsfunktion zum Abfrage des Snake Menu Items
	 * @return Das Snake Menu Item
	 */
	public JMenuItem getMi_snake() {
		return mi_snake;
	}
	/**
	 * Funktion welche die Panels initialisiert.
	 * @param ctrl Benötigte Referenz zum GUI Controller.
	 */
	private void initPanels(ViewController ctrl) {
		panel_rec_ipv4 = new PanelTabbed(ctrl,Typ.RECEIVER_V4);
		panel_sen_ipv4 = new PanelTabbed(ctrl,Typ.SENDER_V4);
		panel_rec_ipv6 = new PanelTabbed(ctrl,Typ.RECEIVER_V6);
		panel_sen_ipv6 = new PanelTabbed(ctrl,Typ.SENDER_V6);
		panel_about = new PanelAbout();
		img_close = new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/close_icon.gif"));
		
		tabpane = new JTabbedPane();
		tabpane.addMouseListener(ctrl);
		tabpane.addMouseMotionListener(ctrl);
		tabpane.addTab(" Receiver IPv4 ", panel_rec_ipv4);
		tabpane.addTab(" Sender IPv4 ", panel_sen_ipv4);
		tabpane.addTab(" Receiver IPv6 ", panel_rec_ipv6);
		tabpane.addTab(" Sender IPv6 ", panel_sen_ipv6);
		tabpane.setIconAt(0, new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4receiver.png")));
		tabpane.setIconAt(1, new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4sender.png")));
		tabpane.setIconAt(2, new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv6receiver.png")));
		tabpane.setIconAt(3, new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv6sender.png")));
		//tabpane.addTab(" Configuration ",img_close, panel_config);
		tabpane.setSelectedIndex(1);
		tabpane.setFont(MiscFont.getFont(0,17));
		tabpane.setFocusable(false);
		tabpane.addChangeListener(ctrl);
		//tabpane.setForegroundAt(1, Color.red);
		add(tabpane);
	}
	/**
	 * Funktion welche das Frame initialisiert
	 * @param ctrl Benötigte Referenz zum GUI Controller.
	 */
	private void initWindow(ViewController ctrl) {
	    try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	       // UIManager.setLookAndFeel(
		   //"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

	    } catch (Exception e) { }
    
		setSize(1000,489);
		//setResizable(false);
		setMinimumSize(new Dimension(640,489));
		setMaximumSize(new Dimension(1920,1080));
		setTitle("MultiCastor");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//getClass().getResourceAsStream("/zisko/multicastor/resources/images/icon.png").
		ImageIcon icon = new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/icon.png"));
		
		setIconImage(icon.getImage());
		fc_save = new FrameFileChooser(ctrl, true);
		fc_load = new FrameFileChooser(ctrl, false);
	}
	public FrameFileChooser getFc_save() {
		return fc_save;
	}

	public FrameFileChooser getFc_load() {
		return fc_load;
	}

	public Dimension getGraphSize(){
		return panel_rec_ipv4.getGraphSize();
	}

	public JTabbedPane getTabpane() {
		return tabpane;
	}
	public PanelTabbed getPanelPart(Typ typ){
		PanelTabbed ret = null;
		switch(typ){
			case SENDER_V4: ret=panel_sen_ipv4; break;
			case SENDER_V6: ret=panel_sen_ipv6;break;
			case RECEIVER_V4: ret=panel_rec_ipv4;break;
			case RECEIVER_V6: ret=panel_rec_ipv6;break;
			default: System.out.println("Error in FrameMain - getPanelPart"); break;
		}
		return ret;
	}
	/**
	 * Funktion welche den Button des About Panel hovered.
	 * @param hover Boolean was angibt ob der Button gehovered ist.
	 */
	public void setAboutCloseHovered(boolean hover){
		ImageIcon hover_icon;
		if(hover){
			aboutPanelState=2;
			hover_icon = new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/close_icon_hover.gif"));
			tabpane.setIconAt(4, hover_icon);
		}
		else{
			aboutPanelState=1;
			hover_icon = new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/close_icon.gif"));
			tabpane.setIconAt(4, hover_icon);
		}
	}
	public PanelTabbed getPanel_rec_ipv4() {
		return panel_rec_ipv4;
	}

	public PanelTabbed getPanel_sen_ipv4() {
		return panel_sen_ipv4;
	}

	public PanelTabbed getPanel_rec_ipv6() {
		return panel_rec_ipv6;
	}

	public PanelTabbed getPanel_sen_ipv6() {
		return panel_sen_ipv6;
	}

	public PanelAbout getPanel_about() {
		return panel_about;
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

	public JMenu getM_scale() {
		return m_scale;
	}

	public JMenu getM_info() {
		return m_info;
	}

	public ButtonGroup getBg_scale() {
		return bg_scale;
	}

	public JRadioButton getRb_beginner() {
		return rb_beginner;
	}

	public JRadioButton getRb_expert() {
		return rb_expert;
	}

	public JMenuItem getMi_saveconfig() {
		return mi_saveconfig;
	}

	public JMenuItem getMi_loadconfig() {
		return mi_loadconfig;
	}

	public JMenuItem getMi_exit() {
		return mi_exit;
	}

	public JMenuItem getMi_about() {
		return mi_about;
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
	public int getAboutPanelState(){
		return aboutPanelState;
	}
	public void setAboutPanelState(int i){
		aboutPanelState=i;
	}
	public void setAboutPanelVisible(boolean visible){
		if(visible){
			aboutPanelState=1;
			tabpane.addTab(" About ",img_close, panel_about);
		}
		else{
			aboutPanelState=0;
			tabpane.remove(4);
		}
	}
	public boolean isAutoSaveEnabled(){
		return mi_autoSave.isSelected();
	}
	/**
	 * Hilfsfunktion welche das aktuell durch die Radiobuttons selektierte Userlevel ausliest
	 * @return das aktuell ausgewählte Userlevel
	 */
	public Userlevel getSelectedUserlevel(){
		Userlevel ret = Userlevel.UNDEFINED;
		if(rb_beginner.isSelected()){
			ret = Userlevel.BEGINNER;
		}
		else if(rb_expert.isSelected()){
			ret = Userlevel.EXPERT;
		}
		else if(rb_custom.isSelected()){
			ret = Userlevel.CUSTOM;
		}
		return ret;
	}

	public JRadioButton getRb_custom() {
		return rb_custom;
	}

	public JCheckBox getMi_autoSave() {
		return mi_autoSave;
	}
	public void setLevel(Userlevel level) {
		this.level = level;
	}
	public Userlevel getLevel() {
		return level;
	}
	public void setLastConfigs(Vector<String> l, boolean live) {
		lastConfigs = l;
		if(lastConfigs != null && lastConfigs.size()>0){
			if(lastConfigs.size() > 0){
				m_menu.remove(mi_exit);
				if(live){
					m_menu.remove(mi_separator);
				}
				mi_profile1.setText(lastConfigs.get(0));
				m_menu.add(mi_profile1);
				if(lastConfigs.size() > 1){
					mi_profile2.setText(lastConfigs.get(1));
					m_menu.add(mi_profile2);				
				}
				if(lastConfigs.size() > 2){
					mi_profile3.setText(lastConfigs.get(2));
					m_menu.add(mi_profile3);		
				}
				m_menu.add(mi_separator);
				m_menu.add(mi_exit);
			}
		}
	}
	public void updateLastConfigs(String s){
		if(lastConfigs.size() > 2){
			lastConfigs.remove(0);
		}
		lastConfigs.add(s);
		setLastConfigs(lastConfigs, true);
	}
	public void setAutoSave(boolean b){
		if(b){
			mi_autoSave.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/check.png")));
			mi_autoSave.setSelected(true);
		}
		else{
			mi_autoSave.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/uncheck.png")));
			mi_autoSave.setSelected(false);
		}
	}
}
