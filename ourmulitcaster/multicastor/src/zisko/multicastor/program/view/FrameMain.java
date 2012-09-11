package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.lang.LanguageManager;

/**
 * Hauptfenster des MultiCastor Tools In Version 2 wurde CheckBox und
 * RadioButton Farbgebungsfehler gefixt Ausserdem Drag&Drop Tabs durch
 * DraggableTabbedPane eingeuehrt
 * 
 * @version 1.5
 */
@SuppressWarnings("serial")
public class FrameMain extends JFrame {

	/**
	 * Wird durch den Konstruktur spaeter mit der Instanz des ViewControllers
	 * beschrieben.
	 */
	ViewController ctrl;

	private int aboutPanelState = 0; // 0 = invisible, 1 = visible, closeButton

	/**
	 * V1.5: Variable zur Speicherung des Basistitels
	 */
	private String baseTitle;
	private ButtonGroup bg_userLevel;
	private ImageIcon img_close;
	private final LanguageManager lang = LanguageManager.getInstance();
	// unhovered, 2 = visible close button
	// hovered
	private Vector<String> lastConfigs = new Vector<String>();
	private JMenu m_info;
	private JMenu m_language;
	private JMenu m_menu;
	private JMenu m_options;
	private JMenu m_view;
	/*
	 * Weitere Standard GUI Komponenten welche benuetigt werden
	 */
	private JMenuBar mb_menubar;
	private JMenuItem mi_about;
	private JCheckBoxMenuItem mi_autoSave;
	private JMenuItem mi_exit;
	private JMenuItem mi_help;
	private JRadioButtonMenuItem[] mi_languages;
	private JMenuItem mi_loadAdditionalMc;
	private JMenuItem mi_loadconfig;
	private JMenuItem mi_loadMc;
	private JCheckBoxMenuItem mi_open_about;
	private JCheckBoxMenuItem mi_open_l2r;
	private JCheckBoxMenuItem mi_open_l2s;
	private JCheckBoxMenuItem mi_open_l3r;
	private JCheckBoxMenuItem mi_open_l3s;
	private JMenuItem mi_profile1;
	private JMenuItem mi_profile2;
	private JMenuItem mi_profile3;
	private JMenuItem mi_saveAllMc;
	private JMenuItem mi_saveconfig;
	private JMenuItem mi_saveSelectedMc;
	private Separator mi_separator;
	// V1.5: Set individual title
	private JMenuItem mi_setTitle;
	private JMenuItem mi_snake;
	/**
	 * V1.5: Zum entfernen der TabPane
	 */
	private boolean paneDel = false;
	/**
	 * Das About Panel
	 */
	private PanelAbout panel_about;

	/**
	 * V1.5: Panel zum oeffnen neuer Tabs
	 */
	private PanelPlus panel_plus;

	/**
	 * Das Layer 2 Receiver Panel neu in V1.5
	 */
	private PanelTabbed panel_rec_lay2;

	/**
	 * Das Layer 3 Receiver Panel neu in V1.5
	 */
	private PanelTabbed panel_rec_lay3;

	/**
	 * Das Layer 2 Sender Panel neu in V1.5
	 */
	private PanelTabbed panel_sen_lay2;
	/**
	 * Das Layer 3 Sender Panel neu in V1.5
	 */
	private PanelTabbed panel_sen_lay3;
	private String subTitle;

	// GUI element variables
	/**
	 * Das Tabpanel mit welchem man durch die Programmteile schalten kann.
	 */
	private DraggableTabbedPane tabpane;

	/**
	 * Konstruktor, der das Hauptfenster des Multicastor-Tools erstellt,
	 * konfiguriert und anzeigt.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller
	 */
	public FrameMain(final ViewController ctrl) {
		this.ctrl = ctrl;
		initWindow(ctrl);
		initMenuBar(ctrl, true);
		initPanels(ctrl, true);
		setVisible(true);
		addComponentListener(ctrl);
		addKeyListener(ctrl);
		addWindowListener(ctrl);

		baseTitle = "MultiCastor";
		updateTitle();
		tabpane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				updateTitle();
			}
		});

	}

	public int getAboutPanelState() {
		return aboutPanelState;
	}

	/**
	 * Holt den ersten Teil des Fenstertitels.
	 * 
	 * @return Fenstertitel (Teil 1)
	 */
	public String getBaseTitle() {
		return baseTitle;
	}

	public Dimension getGraphSize() {
		return panel_rec_lay3.getGraphSize();
	}

	public ImageIcon getImg_close() {
		return img_close;
	}

	public Vector<String> getLastConfigs() {
		return lastConfigs;
	}

	public JMenu getM_info() {
		return m_info;
	}

	public JMenuItem getM_language() {
		return m_language;
	}

	public JMenu getM_menu() {
		return m_menu;
	}

	public JMenu getM_options() {
		return m_options;
	}

	public JMenuBar getMb_menubar() {
		return mb_menubar;
	}

	public JMenuItem getMi_about() {
		return mi_about;
	}

	public JCheckBoxMenuItem getMi_autoSave() {
		return mi_autoSave;
	}

	public JMenuItem getMi_exit() {
		return mi_exit;
	}

	public JMenuItem getMi_help() {
		return mi_help;
	}

	public JRadioButtonMenuItem[] getMi_languages() {
		return mi_languages;
	}

	public JMenuItem getMi_loadAdditionalMc() {
		return mi_loadAdditionalMc;
	}

	public JMenuItem getMi_loadconfig() {
		return mi_loadconfig;
	}

	public JMenuItem getMi_loadMc() {
		return mi_loadMc;
	}

	public JCheckBoxMenuItem getMi_open_about() {
		return mi_open_about;
	}

	public JCheckBoxMenuItem getMi_open_l2r() {
		return mi_open_l2r;
	}

	public JCheckBoxMenuItem getMi_open_l2s() {
		return mi_open_l2s;
	}

	public JCheckBoxMenuItem getMi_open_l3r() {
		return mi_open_l3r;
	}

	public JCheckBoxMenuItem getMi_open_l3s() {
		return mi_open_l3s;
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

	public JMenuItem getMi_saveAllMc() {
		return mi_saveAllMc;
	}

	public JMenuItem getMi_saveconfig() {
		return mi_saveconfig;
	}

	public JMenuItem getMi_saveSelectedMc() {
		return mi_saveSelectedMc;
	}

	public JMenuItem getMi_setTitle() {
		return mi_setTitle;
	}

	/**
	 * Hilfsfunktion zum Abfrage des Snake Menu Items
	 * 
	 * @return Das Snake Menu Item
	 */
	public JMenuItem getMi_snake() {
		return mi_snake;
	}

	public PanelAbout getPanel_about() {
		return panel_about;
	}

	public PanelTabbed getPanel_rec_lay2() {
		return panel_rec_lay2;
	}

	public PanelTabbed getPanel_rec_lay3() {
		return panel_rec_lay3;
	}

	public PanelTabbed getPanel_sen_lay2() {
		return panel_sen_lay2;
	}

	public PanelTabbed getPanel_sen_lay3() {
		return panel_sen_lay3;
	}

	/**
	 * Gibt das Panel zum zugehoerigen internen Namen des Tab-Panels zurueck.
	 * 
	 * @param typ
	 *            Interner Name des Tab-Panels.
	 * @return Ein Panel, welches jeweils den kompletten Programmteil
	 *         beinhaltet.
	 */
	public PanelTabbed getPanelPart(final Typ typ) {
		PanelTabbed ret = null;
		switch(typ) {
		// V1.5 Panels fuer L2/L3 hinzugefuegt
			case L2_SENDER:
				ret = panel_sen_lay2;
				break;
			case L3_SENDER:
				ret = panel_sen_lay3;
				break;
			case L2_RECEIVER:
				ret = panel_rec_lay2;
				break;
			case L3_RECEIVER:
				ret = panel_rec_lay3;
				break;
			default:
				System.out.println("Error in FrameMain - getPanelPart");
				break;
		}
		return ret;
	}

	public DraggableTabbedPane getTabpane() {
		return tabpane;
	}

	public boolean isAutoSaveEnabled() {
		return mi_autoSave.isSelected();
	}

	/**
	 * Prueft, ob Panel geloescht wurde oder nicht.
	 * 
	 * @return Status des Panels.
	 */
	public boolean isPaneDel() {
		return paneDel;
	}

	/**
	 * Aeffnet einen gewuenschten Tab.
	 */
	public void openPane() {

		if(subTitle.equals(" " + lang.getProperty("tab.l2s") + " ")) {
			remove(panel_sen_lay2);
			tabpane.insertTab(" L2 Sender ", null, panel_sen_lay2, null, 0);
			tabpane.setTabComponentAt(0, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv4sender.png", ctrl));

		} else if(subTitle.equals(" " + lang.getProperty("tab.l3s") + " ")) {
			remove(panel_sen_lay3);
			tabpane.insertTab(" " + lang.getProperty("tab.l3s") + " ", null,
					panel_sen_lay3, null, 0);
			tabpane.setTabComponentAt(0, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv6sender.png", ctrl));

		} else if(subTitle.equals(" " + lang.getProperty("tab.l2r") + " ")) {
			remove(panel_rec_lay2);
			tabpane.insertTab(" " + lang.getProperty("tab.l2r") + " ", null,
					panel_rec_lay2, null, 0);
			tabpane.setTabComponentAt(0, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv4receiver.png",
					ctrl));

		} else if(subTitle.equals(" " + lang.getProperty("tab.l3r") + " ")) {
			remove(panel_rec_lay3);
			tabpane.insertTab(" " + lang.getProperty("tab.l3r") + " ", null,
					panel_rec_lay3, null, 0);
			tabpane.setTabComponentAt(0, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv6receiver.png",
					ctrl));
		} else if(subTitle.equals(" " + lang.getProperty("mi.about") + " ")) {
			remove(panel_about);
			tabpane.insertTab(" " + lang.getProperty("mi.about") + " ", null,
					panel_about, null, 0);
			tabpane.setTabComponentAt(0, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/about.png", ctrl));
		}

		add(tabpane);
		paneDel = false;

	}

	/**
	 * Methode, die aufgerufen wird, wenn eine andere Sprache ausgewaehlt wird.
	 */
	public void reloadLanguage() {
		initMenuBar(ctrl, false);
		initPanels(ctrl, false);
		MiscBorder.reloadLanguage();
		repaint();
	}

	public void removePane() {

		remove(tabpane);

		final String title = tabpane.getTitleAt(0);
		subTitle = title;
		paneDel = true;

		if(title.equals(" " + lang.getProperty("tab.l2s") + " ")) {
			add(panel_sen_lay2);
		} else if(title.equals(" " + lang.getProperty("tab.l3s") + " ")) {
			add(panel_sen_lay3);
		} else if(title.equals(" " + lang.getProperty("tab.l2r") + " ")) {
			add(panel_rec_lay2);
		} else if(title.equals(" " + lang.getProperty("tab.l3r") + " ")) {
			add(panel_rec_lay3);
		} else if(title.equals(" " + lang.getProperty("mi.about") + " ")) {
			add(panel_about);
		}

	}

	public void setAboutPanelState(final int i) {
		aboutPanelState = i;
	}

	/**
	 * Setzt das About-Panel auf sichtbar / unsichtbar.
	 * 
	 * @param visible
	 *            Angabe true / false fuer sichtbar / unsichtbar.
	 */
	public void setAboutPanelVisible(final boolean visible) {
		if(visible) {
			aboutPanelState = 1;
			tabpane.addTab(" About ", img_close, panel_about);
		} else {
			aboutPanelState = 0;
			tabpane.remove(4);
		}
	}

	/**
	 * Setzt den Menuepunkt "Automatisches Speichern" auf an / aus.
	 * 
	 * @param b
	 *            True / False fuer an / aus.
	 */
	public void setAutoSave(final boolean b) {
		if(b) {
			// mi_autoSave.setIcon(new
			// ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/check.png")));
			mi_autoSave.setSelected(true);
		} else {
			// mi_autoSave.setIcon(new
			// ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/uncheck.png")));
			mi_autoSave.setSelected(false);
		}
	}

	/**
	 * Setzt den Fenstertitel (erster Teil des Titels).
	 * 
	 * @param baseTitle
	 *            Gewuenschter Fenstertitel
	 */
	public void setBaseTitle(final String baseTitle) {
		this.baseTitle = baseTitle;
	}

	/**
	 * Setzt die zuletzt gespeicherten Configs.
	 * 
	 * @param l
	 *            Config-Daten als String-Vektor.
	 * @param live
	 *            Angabe, ob die Config-Daten in Echtzeit aktualisiert werden
	 *            sollen.
	 */
	public void setLastConfigs(final Vector<String> l, final boolean live) {

		lastConfigs = l;
		if((lastConfigs != null) && (lastConfigs.size() > 0)) {
			if(lastConfigs.size() > 0) {
				m_menu.remove(mi_exit);
				if(live) {
					m_menu.remove(mi_separator);
				}
				mi_profile1.setText(lastConfigs.get(0));
				m_menu.add(mi_profile1);
				if(lastConfigs.size() > 1) {
					mi_profile2.setText(lastConfigs.get(1));
					m_menu.add(mi_profile2);
				}
				if(lastConfigs.size() > 2) {
					mi_profile3.setText(lastConfigs.get(2));
					m_menu.add(mi_profile3);
				}
				m_menu.add(mi_separator);
				m_menu.add(mi_exit);
			}
		}
	}

	public void setMi_help(final JMenuItem miHelp) {
		mi_help = miHelp;
	}

	public void setMi_language(final JMenu miLanguage) {
		m_language = miLanguage;
	}

	public void setMi_setTitle(final JMenuItem miSetTitle) {
		mi_setTitle = miSetTitle;
	}

	public void setPanel_rec_lay2(final PanelTabbed panelRecLay2) {
		panel_rec_lay2 = panelRecLay2;
	}

	public void setPanel_rec_lay3(final PanelTabbed panelRecLay3) {
		panel_rec_lay3 = panelRecLay3;
	}

	public void setPanel_sen_lay2(final PanelTabbed panelSenLay2) {
		panel_sen_lay2 = panelSenLay2;
	}

	public void setPanel_sen_lay3(final PanelTabbed panelSenLay3) {
		panel_sen_lay3 = panelSenLay3;
	}

	/**
	 * Aktualisiert die letzten Config-Daten.
	 * 
	 * @param s
	 *            Config-Daten.
	 */
	public void updateLastConfigs(final String s) {
		if(lastConfigs.size() > 2) {
			lastConfigs.remove(0);
		}
		lastConfigs.add(s);
		setLastConfigs(lastConfigs, true);
	}

	/**
	 * V1.5: Methode zum updaten des Fenster-Titels
	 */
	public void updateTitle() {

		if(!paneDel) {
			setTitle(baseTitle + (baseTitle.isEmpty() ? "" : ": ")
					+ tabpane.getTitleAt(tabpane.getSelectedIndex()).trim());
		} else {
			setTitle(baseTitle + (baseTitle.isEmpty() ? "" : ": ")
					+ subTitle.trim());
		}

	}

	/**
	 * Funktion welche die Menubar initialisiert.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param Gibt
	 *            an, ob die Menue-Bar zum ersten mal initialisiert wird oder
	 *            nicht.
	 */
	private void initMenuBar(final ViewController ctrl, final boolean firstInit) {

		if(firstInit) {
			mi_autoSave = new JCheckBoxMenuItem();
			mi_autoSave.addItemListener(ctrl);
			mi_autoSave.setFont(MiscFont.getFont(0, 14));

			mi_setTitle = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/title.png")));
			mi_setTitle.setFont(MiscFont.getFont(0, 14));
			mi_setTitle.addActionListener(ctrl);

			m_language = new JMenu();
			m_language.setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/language.png")));
			m_language.setFont(MiscFont.getFont(0, 14));

			bg_userLevel = new ButtonGroup();

			mi_saveconfig = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/save.png")));
			mi_saveconfig.setFont(MiscFont.getFont(0, 14));
			mi_saveconfig.addActionListener(ctrl);

			mi_saveSelectedMc = new JMenuItem(new ImageIcon(
					getClass().getResource(
							"/zisko/multicastor/resources/images/save.png")));
			mi_saveSelectedMc.setFont(MiscFont.getFont(0, 14));
			mi_saveSelectedMc.addActionListener(ctrl);

			mi_saveAllMc = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/save.png")));
			mi_saveAllMc.setFont(MiscFont.getFont(0, 14));
			mi_saveAllMc.addActionListener(ctrl);

			mi_loadconfig = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/load.png")));
			mi_loadconfig.setFont(MiscFont.getFont(0, 14));
			mi_loadconfig.addActionListener(ctrl);

			mi_loadAdditionalMc = new JMenuItem(new ImageIcon(
					getClass().getResource(
							"/zisko/multicastor/resources/images/load.png")));
			mi_loadAdditionalMc.setFont(MiscFont.getFont(0, 14));
			mi_loadAdditionalMc.addActionListener(ctrl);

			mi_loadMc = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/load.png")));
			mi_loadMc.setFont(MiscFont.getFont(0, 14));
			mi_loadMc.addActionListener(ctrl);

			mi_profile1 = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/load.png")));
			mi_profile1.setFont(MiscFont.getFont(0, 14));
			mi_profile1.addActionListener(ctrl);
			mi_profile1.setActionCommand("lastConfig1");

			mi_profile2 = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/load.png")));
			mi_profile2.setFont(MiscFont.getFont(0, 14));
			mi_profile2.addActionListener(ctrl);
			mi_profile2.setActionCommand("lastConfig2");

			mi_profile3 = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/load.png")));
			mi_profile3.setFont(MiscFont.getFont(0, 14));
			mi_profile3.addActionListener(ctrl);
			mi_profile3.setActionCommand("lastConfig3");

			mi_separator = new Separator();

			mi_snake = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ksnake.png")));
			mi_snake.setFont(MiscFont.getFont(0, 14));
			mi_snake.addActionListener(ctrl);

			mi_help = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/help.png")));
			mi_help.setFont(MiscFont.getFont(0, 14));
			mi_help.addActionListener(ctrl);

			mi_exit = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/exit.png")));
			mi_exit.setFont(MiscFont.getFont(0, 14));
			mi_exit.addActionListener(ctrl);

			mi_about = new JMenuItem(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/info.png")));
			mi_about.setFont(MiscFont.getFont(0, 14));
			mi_about.setActionCommand("open_about");
			mi_about.addActionListener(ctrl);

			m_menu = new JMenu();
			m_menu.setFont(MiscFont.getFont(0, 16));

			m_options = new JMenu();
			m_options.setFont(MiscFont.getFont(0, 16));

			m_view = new JMenu();
			m_view.setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/view.png")));
			m_view.setFont(MiscFont.getFont(0, 14));

			mi_open_l2r = new JCheckBoxMenuItem();
			mi_open_l2r.setFont(MiscFont.getFont(0, 14));
			mi_open_l2r.setActionCommand("m_open_layer2_r");
			mi_open_l2r.addActionListener(ctrl);

			mi_open_l2s = new JCheckBoxMenuItem();
			mi_open_l2s.setFont(MiscFont.getFont(0, 14));
			mi_open_l2s.setActionCommand("m_open_layer2_s");
			mi_open_l2s.addActionListener(ctrl);

			mi_open_l3r = new JCheckBoxMenuItem();
			mi_open_l3r.setFont(MiscFont.getFont(0, 14));
			mi_open_l3r.setActionCommand("m_open_layer3_r");
			mi_open_l3r.addActionListener(ctrl);

			mi_open_l3s = new JCheckBoxMenuItem();
			mi_open_l3s.setFont(MiscFont.getFont(0, 14));
			mi_open_l3s.setActionCommand("m_open_layer3_s");
			mi_open_l3s.addActionListener(ctrl);

			mi_open_about = new JCheckBoxMenuItem(lang.getProperty("mi.about"));
			mi_open_about.setFont(MiscFont.getFont(0, 14));
			mi_open_about.setActionCommand("m_open_about");
			mi_open_about.addActionListener(ctrl);

			m_info = new JMenu();
			m_info.setFont(MiscFont.getFont(0, 16));

			mb_menubar = new JMenuBar();

			// Create Language Buttons
			mi_languages = new JRadioButtonMenuItem[LanguageManager.languages.length];
			for(int i = 0; i < LanguageManager.languages.length; i++) {
				mi_languages[i] = new JRadioButtonMenuItem(
						LanguageManager.languages[i].replaceAll(".lang", ""));
				if(mi_languages[i].getText().equals(
						LanguageManager.getCurrentLanguage())) {
					mi_languages[i].setSelected(true);
				}
				m_language.add(mi_languages[i]);
				mi_languages[i].setFont(MiscFont.getFont(0, 14));
				mi_languages[i].setActionCommand("change_lang_to_"
						+ mi_languages[i].getText());
				mi_languages[i].addActionListener(ctrl);
				bg_userLevel.add(mi_languages[i]);
			}

			m_view.add(mi_open_l2r);
			m_view.add(mi_open_l2s);
			m_view.add(mi_open_l3s);
			m_view.add(mi_open_l3r);
			m_view.add(mi_open_about);
			m_info.add(mi_snake);
			m_info.add(mi_about);
			m_info.add(mi_help);
			m_options.add(m_view);
			m_options.add(m_language);
			m_options.add(mi_setTitle);
			m_options.add(mi_autoSave);
			m_menu.add(mi_saveconfig);
			m_menu.add(mi_saveAllMc);
			m_menu.add(mi_saveSelectedMc);
			m_menu.add(mi_loadconfig);
			m_menu.add(mi_loadMc);
			m_menu.add(mi_loadAdditionalMc);
			m_menu.add(mi_exit); // [FF] WHHHHO... hab ein vergessenes Menu Ding
									// gefunden
			mb_menubar.add(m_menu);
			mb_menubar.add(m_options);
			mb_menubar.add(m_info);
			setJMenuBar(mb_menubar);

		}

		mi_autoSave.setText(lang.getProperty("mi.autoSave"));
		mi_setTitle.setText(lang.getProperty("mi.changeWindowTitle"));
		m_language.setText(lang.getProperty("mi.language"));
		mi_saveconfig.setText(lang.getProperty("mi.saveConfiguration"));
		mi_saveAllMc.setText(lang.getProperty("mi.saveAllMc"));
		mi_saveSelectedMc.setText(lang.getProperty("mi.saveSelectedMc"));
		mi_loadconfig.setText(lang.getProperty("mi.loadConfiguration"));
		mi_loadMc.setText(lang.getProperty("mi.loadMc"));
		mi_loadAdditionalMc.setText(lang.getProperty("mi.loadAdditionalMc"));
		mi_profile1.setText("mi.errorFileNotFound");
		mi_profile2.setText("mi.errorFileNotFound");
		mi_profile3.setText("mi.errorFileNotFound");
		mi_snake.setText(lang.getProperty("mi.snake"));
		mi_help.setText(lang.getProperty("mi.help"));
		mi_exit.setText(lang.getProperty("mi.exit"));
		mi_about.setText(lang.getProperty("mi.about"));
		m_menu.setText(lang.getProperty("mi.menu"));
		m_options.setText(lang.getProperty("mi.options"));
		m_view.setText(lang.getProperty("mi.views"));
		mi_open_l2r.setText(lang.getProperty("mi.layer2Receiver"));
		mi_open_l2s.setText(lang.getProperty("mi.layer2Sender"));
		mi_open_l3r.setText(lang.getProperty("mi.layer3Receiver"));
		mi_open_l3s.setText(lang.getProperty("mi.layer3Sender"));
		m_info.setText(lang.getProperty("mi.info"));

	}

	/**
	 * Funktion welche die Panels initialisiert.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param Gibt
	 *            an, ob die Menue-Bar zum ersten mal initialisiert wird oder
	 *            nicht.
	 */
	private void initPanels(final ViewController ctrl, final boolean firstInit) {

		if(firstInit) {
			// v1.5: Added new Tabs: L2 Receiver, L2 Sender, L3 Receiver, L3
			// Sender
			panel_rec_lay2 = new PanelTabbed(ctrl, Typ.L2_RECEIVER);
			panel_sen_lay2 = new PanelTabbed(ctrl, Typ.L2_SENDER);
			panel_rec_lay3 = new PanelTabbed(ctrl, Typ.L3_RECEIVER);
			panel_sen_lay3 = new PanelTabbed(ctrl, Typ.L3_SENDER);
			panel_plus = new PanelPlus(this, ctrl);
			panel_about = new PanelAbout();

			// V1.5: Variable int i um automatisch die Indexnummer korrekt zu
			// setzen
			int i = 0;
			// V1.5: Referenz auf sich selbst, wird uebergeben, um Titel zu
			// refreshen
			tabpane = new DraggableTabbedPane(this, ctrl);
			tabpane.addMouseListener(ctrl);

			tabpane.addTab(" " + lang.getProperty("tab.l2r") + " ",
					panel_rec_lay2);
			tabpane.setTabComponentAt(i++, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv4receiver.png",
					ctrl));

			tabpane.addTab(" " + lang.getProperty("tab.l2s") + " ",
					panel_sen_lay2);
			tabpane.setTabComponentAt(i++, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv4sender.png", ctrl));

			tabpane.addTab(" " + lang.getProperty("tab.l3r") + " ",
					panel_rec_lay3);
			tabpane.setTabComponentAt(i++, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv6receiver.png",
					ctrl));

			tabpane.addTab(" " + lang.getProperty("tab.l3s") + " ",
					panel_sen_lay3);
			tabpane.setTabComponentAt(i++, new ButtonTabComponent(tabpane,
					"/zisko/multicastor/resources/images/ipv6sender.png", ctrl));

			// V1.5: + Panel zum Aeffnen neuer Tabs
			tabpane.addTab(" + ", panel_plus);

		} else {
			for(int i = 0; i < tabpane.getTabCount(); i++) {
				if(tabpane.getComponentAt(i) == panel_rec_lay2) {
					tabpane.setTitleAt(i, " " + lang.getProperty("tab.l2r")
							+ " ");
				} else if(tabpane.getComponentAt(i) == panel_sen_lay2) {
					tabpane.setTitleAt(i, " " + lang.getProperty("tab.l2s")
							+ " ");
				} else if(tabpane.getComponentAt(i) == panel_rec_lay3) {
					tabpane.setTitleAt(i, " " + lang.getProperty("tab.l3r")
							+ " ");
				} else if(tabpane.getComponentAt(i) == panel_sen_lay3) {
					tabpane.setTitleAt(i, " " + lang.getProperty("tab.l3s")
							+ " ");
				}
				// New Layout is required for auto resize of the tabs
				if(tabpane.getTabComponentAt(i) != null) {
					tabpane.getTabComponentAt(i).doLayout();
				}
			}
		}

		if(firstInit) {
			mi_open_l2r.setSelected(true);
			mi_open_l2s.setSelected(true);
			mi_open_l3r.setSelected(true);
			mi_open_l3s.setSelected(true);
			// tabpane.addTab(" Configuration ",img_close, panel_config);
			tabpane.setSelectedIndex(0);
			tabpane.setFont(MiscFont.getFont(0, 17));
			tabpane.setFocusable(false);
			tabpane.addChangeListener(ctrl);
			// tabpane.setForegroundAt(1, Color.red);
			add(tabpane);
		} else {
			panel_plus.reloadLanguage();
			panel_rec_lay2.reloadLanguage();
			panel_sen_lay2.reloadLanguage();
			panel_rec_lay3.reloadLanguage();
			panel_sen_lay3.reloadLanguage();
			panel_about.reloadLanguage();
		}
	}

	/**
	 * Funktion welche das Frame initialisiert.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 */
	private void initWindow(final ViewController ctrl) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(
			// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		} catch(final Exception e) {
		}

		setSize(1000, 489);
		// setResizable(false);
		setMinimumSize(new Dimension(640, 489));
		setMaximumSize(new Dimension(1920, 1080));

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// getClass().getResourceAsStream("/zisko/multicastor/resources/images/icon.png").
		final ImageIcon icon = new ImageIcon(getClass().getResource(
				"/zisko/multicastor/resources/images/icon.png"));

		setIconImage(icon.getImage());
	}

}
