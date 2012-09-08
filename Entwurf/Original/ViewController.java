   
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
 
  

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.interfaces.MulticastThreadSuper;
import dhbw.multicastor.program.interfaces.UIController;
import dhbw.multicastor.program.model.FileNames;
import dhbw.multicastor.program.model.InputValidator;
import dhbw.multicastor.program.model.MulticastReceiverLayer3;
import dhbw.multicastor.program.model.MulticastSender;
import dhbw.multicastor.program.model.NetworkAdapter;
import dhbw.multicastor.program.model.XmlParserWorkbench;
import dhbw.multicastor.program.view.CustomTableColumnModel;
import dhbw.multicastor.program.view.FrameAbout;
import dhbw.multicastor.program.view.FrameFileChooser;
import dhbw.multicastor.program.view.FrameMain;
import dhbw.multicastor.program.view.GraphLines;
import dhbw.multicastor.program.view.MiscTableModel;
import dhbw.multicastor.program.view.PanelMulticastConfigNew;
import dhbw.multicastor.program.view.PanelMulticastConfigNewSender;
import dhbw.multicastor.program.view.PanelMulticastInfo;
import dhbw.multicastor.program.view.PanelMulticastconfigNewContext;
import dhbw.multicastor.program.view.PanelStatusBar;
import dhbw.multicastor.program.view.PopUpMenu;
import dhbw.multicastor.program.view.ReceiverGraph;
import dhbw.multicastor.program.view.ReceiverGraph.valueType;


/**
 * Steuerungsklasse des GUI
 * 
 * @author Daniel Becker
 * 
 */
public class ViewController implements ActionListener, MouseListener,
		ComponentListener, ListSelectionListener, KeyListener, ItemListener,
		WindowListener, UIController, FocusListener {
	/**
	 * Enum welches angibt um was fï¿½r eine Art von GUI Benachrichtigung es sich
	 * handelt.
	 * 
	 * @author Daniel Becker
	 * 
	 */
	public enum MessageTyp {
		INFO, WARNING, ERROR
	}

	/**
	 * wird benï¿½tigt um die Position der Fenster zu speichern und dem neuen
	 * Fenster zu ï¿½bergeben
	 */
	private Preferences prefs;

	/**
	 * Enum welches angibt um was fï¿½r eine Art von GUI Update es sich handelt.
	 * 
	 * @author Daniel Becker
	 * 
	 */
	public enum UpdateTyp {
		UPDATE, INSERT, DELETE
	}

	/**
	 * Referenz zum MulticastController, wichtigste Schnittstelle der Klasse.
	 */
	private MulticastController mcController = null;
	/**
	 * Referenz zum Frame in welchem das MultiCastor Tool angezeigt wird.
	 */
	private FrameMain f;

	/**
	 * Hilfsvariable welche benï¿½tigt wird um die gemeinsamen Revceiver Graph
	 * Daten (JITTER, LOST PACKETS, MEASURED PACKET RATE) von mehreren
	 * Mutlicasts zu berechnen.
	 */
	private MulticastData[] graphData;
	/**
	 * Hilfsvariable welche angibt wann die Initialisierung der GUI
	 * abgeschlossen ist.
	 */
	private boolean initFinished = false;

	private static ResourceBundle messages = ResourceBundle
			.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$

	private Logger logger = Logger
			.getLogger("dhbw.multicastor.program.controller.main");

	/**
	 * Standardkonstruktor der GUI, hierbei wird die GUI noch nicht
	 * initialisiert!
	 */
	public ViewController() {
	}

	/**
	 * Implementierung des ActionListeners, betrifft die meisten GUI
	 * Komponenten. Diese Funktion wird aufgerufen wenn eine Interaktion mit
	 * einer GUI Komponente stattfindet, welche den ActionListener dieser
	 * ViewController Klasse hï¿½lt. Die IF-THEN-ELSEIF Abragen dienen dazu die
	 * Komponente zu identifizieren bei welcher die Interaktion stattgefunden
	 * hat.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == PanelMulticastConfigNew.RBTN_IGMP) {
			f.getPanelMulticastconfigNewContext().setIgmp();
			PanelMulticastConfigNew panel = f
					.getPanelMulticastconfigNewContext()
					.getPanelMulticastConfigNew();
			this.setDefaultValues();
			panel.getIgmpDocumentListener().insertUpdate();
			f.repaint();
			checkSelectedRow();
		} else if (e.getActionCommand() == PanelMulticastConfigNew.RBTN_MLD) {
			f.getPanelMulticastconfigNewContext().setMld();
			PanelMulticastConfigNew panel = f
					.getPanelMulticastconfigNewContext()
					.getPanelMulticastConfigNew();
			this.setDefaultValues();
			panel.getMldDocumentListener().insertUpdate();
			f.repaint();
			checkSelectedRow();
		} else if (e.getActionCommand() == PanelMulticastConfigNew.RBTN_MMRP) {
			f.getPanelMulticastconfigNewContext().setMmrp();
			PanelMulticastConfigNew panel = f
					.getPanelMulticastconfigNewContext()
					.getPanelMulticastConfigNew();
			this.setDefaultValues();
			f.repaint();
			panel.getMmrpDocumentListener().insertUpdate();
			checkSelectedRow();

		} else if (e.getActionCommand() == FrameMain.MI_RECEIVER) {
			initializeReceiver();
			f.getPanelMulticastconfigNewContext().getButton().getRbtn_IGMP()
					.doClick();
			f.setTitle("Receiver");
		} else if (e.getActionCommand() == FrameMain.MI_SENDER) {
			initializeSender();
			f.getPanelMulticastconfigNewContext().getButton().getRbtn_IGMP()
					.doClick();
			f.setTitle("Sender");
		} else if (e.getSource() == f.getMi_saveconfig()) {
			// System.out.println("Saving!");
			f.getFc_save().toggle();
		} else if (e.getSource() == f.getMi_exit()) {
			closeProgram();
		} else if (e.getSource() == f.getMi_loadconfig()) {
			// System.out.println("Loading!");
			f.getFc_load().toggle();
		} else if (e.getSource() == f.getFc_save().getChooser()) {
			saveFileEvent(e);
		} else if (e.getSource() == f.getFc_load().getChooser()) {
			loadFileEvent(e);
		} else if (e.getSource() == f.getPanelMulticastconfigNewContext()
				.getButton().getBtn_add()) {
			try {
				this.setEditable(false);
				pressBTAdd();
				setDefaultValues();
			} catch (Exception ex) {
				System.out.println("Es ist ein Fehler aufgetreten: ");
				ex.printStackTrace();
			}
		} else if (e.getSource() == f.getPanelMulticastconfigNewContext()
				.getButton().getBtn_change()) {
			MulticastData mc = getMCData(getSelectedRow());
			pressBTChange(mc);
			selectPreviousRow(mc);
			setDefaultValues();
		} else if (e.getActionCommand() == FrameMain.MI_ABOUT) {
			FrameAbout about = new FrameAbout();
			about.setVisible(true);
		} else if (e.getActionCommand() == FrameMain.MI_MANUAL) {
			try {
				if(Locale.getDefault().getLanguage().equals("de")){
					Desktop.getDesktop().open(new File(FileNames.MANUAL_DE));
				}else{
					Desktop.getDesktop().open(new File(FileNames.MANUAL_ENG));
				}
			} catch (IOException e1) {
				logger.info("Manual couldn't be opened");
			} catch (IllegalArgumentException e2) {
				logger.info("Manual doesn't exist");
			}
		} else if (e.getActionCommand().equals("deselect_all")) {
			deselectAll();
		} else if (e.getActionCommand().equals("select_all")) {
			selectAll();
		} else if (e.getActionCommand().equals("hide")) {
			f.getPanelTable().removeColumnFromView(e);
		} else if (e.getActionCommand().equals("restore_columns")) {
			f.getPanelTable().restoreColumns();
		} else if (e.getActionCommand().equals("Popup_toggleColumn")) {
			toggleColumnPopup(e);
		} else if (e.getSource() == f.getPanelButtonBar().getB_activate()) {
			_performActivateButton();
		} else if (e.getSource() == f.getPanelButtonBar().getB_delete()) {
			_performDeleteButton();
			this.setDefaultValues();
		} else if (e.getSource() == f.getPanelButtonBar().getB_reset()) {
			_performResetButton();
		} else if (e.getSource() == f.getPanelButtonBar().getB_display()) {
			_performDisplayButton();
		} else if (e.getActionCommand().equals("delete_popup")) {
			_performDeleteButton();
		} else if (e.getSource().equals(f.getRdbtnEnglish())) {
			Locale.setDefault(new Locale("en", "US"));
			if (f.getRdbtnEnglish().isSelected()) {
				if (showRestartDialog() == 0) {
					mcController.restart();
				}
			}
		} else if (e.getSource().equals(f.getRdbtnGerman())) {
			Locale.setDefault(new Locale("de", "DE"));
			if (f.getRdbtnGerman().isSelected()) {
				if (showRestartDialog() == 0) {
					mcController.restart();
				}
			}
		} else if (e.getActionCommand().equals("groupItems")) {
			displayGraph(true);
		} else if (e.getActionCommand().equals(FrameMain.MI_LAST_CONFIGS)) {
			ArrayList<JMenuItem> lastconfigs = f.getLastconfigs();
			for (JMenuItem jMenuItem : lastconfigs) {
				if (e.getSource() == jMenuItem) {
					mcController.getConfig().loadConfig(jMenuItem.getText(),
							false, mcController);
				}
			}

		} else if (e.getSource() == f.getPanelButtonBar().getB_leave()) {
			_performLeaveButton();
		} else if (e.getSource() == f.getPanelButtonBar().getB_leaveAll()) {
			_performLeaveAllButton();
		}

	}

	private void selectPreviousRow(MulticastData mc) {
		int count = f.getTable().getRowCount();
		for (int i = 0; i < count; i++) {
			if (mc.equals(getMCData(i))) {
				f.getTable().getSelectionModel().setSelectionInterval(i, i);
				return;
			}
		}
	}

	private void checkSelectedRow() {
		int[] sel = getSelectedRows();
		ProtocolType r = f.getPanelMulticastconfigNewContext().getButton()
				.getSelectedProtocolType();
		for (int i = 0; i < sel.length; i++) {
			if (!getMCData(sel[i]).getProtocolType().equals(r)) {
				deselectAll();
				return;
			}
		}
	}

	public void displayGraph(boolean graph) {
		// Graphen aktivieren ueber Display Button
		int[] sel = getSelectedRows();
		if (sel.length > 0) {
			ArrayList<MulticastData> mcs = new ArrayList<MulticastData>();
			for (int i = 0; i < sel.length; i++) {
				mcs.add(getMCData(sel[i]));
			}
			if (graph) {
				if (!GraphLines.addMC(mcs)) {
					logger.log(Level.INFO, "Only 5 graphlines are possible");
				} else {
					multicastSetGraph(mcs, true);
				}
			} else {
				multicastSetGraph(mcs, false);
				GraphLines.deleteMC(mcs);
			}
			_checkDisplayButton(sel);
		}
	}

	private void multicastSetGraph(ArrayList<MulticastData> mcs, boolean b) {
		for (MulticastData mc : mcs) {
			mc.setGraph(b);
		}
	}

	private void multicastSetGraph(MulticastData mc, boolean b) {
		mc.setGraph(b);
	}

	private void toggleColumnPopup(ActionEvent e) {
		JCheckBox checkbox = (JCheckBox) e.getSource();
		for (int i = 0; i < getTable().getModel().getColumnCount(); i++) {
			String columnName = getTable().getModel().getColumnName(i);
			if (columnName.equals(checkbox.getText())) {
				CustomTableColumnModel columnModel = (CustomTableColumnModel) getTable()
						.getColumnModel();
				columnModel.toggleColumnVisable(checkbox.isSelected(), i);
				break;
			}
		}
	}

	public void initializeReceiver() {
//		saveWindowPreferences();
		GraphLines.deleteMCs();
		// setzt die Position auf die des vorherigen Fensters
//		setWindowPreferences();
		this.setInitFinished(false);
		this.deleteAllMulticasts();
		changeModus(Modus.RECEIVER);
		// versetzt PanelInfo in Receiver Modus
		f.getPan_info().setReceiver();
		f.switchModus();
		this.setInitFinished(true);
	}

	public void initializeSender() {
		// speichert die Position des aktuellen Fensters
//		saveWindowPreferences();
		GraphLines.deleteMCs();
		this.setInitFinished(false);
		this.deleteAllMulticasts();
		changeModus(Modus.SENDER);
		// setzt die Position auf die des vorherigen Fensters
//		setWindowPreferences();

		// versetzt PanelInfo in Sender Modus
		f.getPan_info().setSender();
		f.switchModus();
		this.setInitFinished(true);
	}

	private void selectAll() {
		f.getTable().selectAll();
	}

	protected void setDefaultValues() {
		this.setEditable(true);
		this.clearInput();
		f.getPanelMulticastconfigNewContext().setDefaultValues();
	}

	protected void setEditable(boolean bool) {
		f.getPanelMulticastconfigNewContext().setEditableFields(bool);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
			_performDeleteButton();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		if (f.getPan_graph().isVisible()) {
			f.getPan_graph()
					.resize(new Dimension(f.getSize().width - 225, 100));
		}
		// Set the TableColumns width to Auto if Window is bigger then 1040px
		if (f.getContentPane().getWidth() > 1040) {
			f.getTable().setAutoResizeMode(
					JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		} else {
			f.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			// Table Header Popup
			if (e.getSource() == f.getTable().getTableHeader()) {
				PopUpMenu headerPopup = new PopUpMenu();
				headerPopup.createTableHeaderPopup(this, e);
				CustomTableColumnModel columnModel = (CustomTableColumnModel) getTable()
						.getColumnModel();
				int columnIndex = getTable().getColumnModel()
						.getColumnIndexAtX(e.getX());
				if (columnIndex > 0) {
					columnModel.setSelectedColumn(columnIndex);
				}
			}
			// Table ContextMenu
			if (e.getSource() == f.getTable()) {
				PopUpMenu popup = new PopUpMenu();
				popup.createTablePopup(f.getTable(), this, e);
			}
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getSource() != f.getTable()) {
				deselectAll();
				f.getPan_info().reset();
			}
		}
	}

	/**
	 * Funktion welche eni neues Multicast Datenobjekt an den MultiCast
	 * Controller weitergibt zur Verarbeitung.
	 * 
	 * @param mcd
	 *            neu erstelltes Multicast DatenObjekt welches verarbeitet
	 *            werden soll.
	 */
	public void addMC(MulticastData mcd) {
		mcController.addMC(mcd);
		updateTable(UpdateTyp.INSERT);
	}

	public Modus getCurrentModus() {
		if (mcController != null) {
			return mcController.getCurrentModus();
		}
		return Modus.UNDEFINED;
	}

	/**
	 * Funktion welche ein geï¿½ndertes Multicast Datenobjekt an den MultiCast
	 * Controller weitergibt zur Verarbeitung.
	 * 
	 * @param mcd
	 *            Das geï¿½nderter MulticastData Object.
	 */
	public void changeMC(MulticastData mcd) {
		mcController.changeMC(mcd);
		updateTable(UpdateTyp.UPDATE);
	}

	/**
	 * Hilfsfunktion welche den momentanen Input des KonfigurationsPanels in ein
	 * Multicast Datenobjekt schreibt. Wird benï¿½tigt zum anlegen neuer Multicast
	 * sowie zum ï¿½ndern vorhandener Multicasts.
	 * 
	 * @param mcd
	 *            MulticastData Objet welches geï¿½ndert werden soll.
	 * @param typ
	 *            Programmteil aus welchem die Input Daten ausgelesen werden
	 *            sollen.
	 * @return Geï¿½nderters Multicast Datenobjekt.
	 */
	private MulticastData changeMCData(MulticastData mcd,
			MulticastData.ProtocolType typ) {
		PanelMulticastConfigNew panel = f.getPanelMulticastconfigNewContext()
				.getPanelMulticastConfigNew();
		if (mcd.getProtocolType().equals(ProtocolType.IGMP)
				|| mcd.getProtocolType().equals(ProtocolType.MLD)) {
			IgmpMldData data = (IgmpMldData) mcd;
			if (panel instanceof PanelMulticastConfigNewSender) {
				if (typ.equals(ProtocolType.IGMP)) {
					data.setSourceIp(InputValidator.checkIPv4(panel
							.getCb_interface().toString()));
				} else {
					data.setSourceIp(InputValidator.checkIPv6(panel
							.getCb_interface().toString()));
				}
				data.setPacketLength(InputValidator.checkIPv4PacketLength(panel
						.getTf_PLength().getText()));
				data.setTtl(InputValidator.checkTimeToLive(panel.getTf_TTL()
						.getText()));
				data.setPacketRateDesired(InputValidator.checkPacketRate(panel
						.getTf_PRate().getText()));
			}
			if (typ.equals(ProtocolType.IGMP)) {
				data.setGroupIp(InputValidator.checkMC_IPv4(panel
						.getTf_address().getText()));
			} else {
				data.setGroupIp(InputValidator.checkMC_IPv6(panel
						.getTf_address().getText()));
			}
			data.setUdpPort(InputValidator.checkPort(panel.getTf_port()
					.getText()));
		} else if (mcd.getProtocolType().equals(ProtocolType.MMRP)) {

			MMRPData data = (MMRPData) mcd;
			if (panel instanceof PanelMulticastConfigNewSender) {
				mcd.setPacketLength(InputValidator.checkIPv6PacketLength(panel
						.getTf_PLength().getText()));
				data.setPacketRateDesired(InputValidator.checkPacketRate(panel
						.getTf_PRate().getText()));
			} else {
				data.setTimerJoinMt(InputValidator.checkJoinMtTimer(panel
						.getTf_JoinMtTimer().getText()));
			}
			data.setMacGroupId(InputValidator.checkMACGroupAddress(panel
					.getTf_address().getText()));
			data.setMacSourceId(InputValidator.checkMAC(panel.getCb_interface()
					.getSelectedItem().toString()));
		} else {
			System.out.println("changeMCData(Multicastdata mcd) - ERROR");
		}
		mcd.setProtocolType(typ);
		return mcd;
	}

	private void saveWindowPreferences() {
		prefs.putInt("window.position.x", f.getX());
		prefs.putInt("window.position.y", f.getY());
	}

	private void setWindowPreferences() {
		f.setLocation(prefs.getInt("window.position.x", 0),
				prefs.getInt("window.position.y", 0));
	}

	// /**
	// * Funktion welche aufgerufen wird wenn der User das Netzwerk Interface in
	// einem Sender ï¿½ndert.
	// * @param typ Programmteil in welchem das Netzwerkinterface geï¿½ndert
	// wurde.
	// */
	// private void changeNetworkInterface(ProtocolType typ) {
	// PanelMulticastConfig configpart = getPanConfig(typ);
	// int selectedIndex =
	// configpart.getTf_sourceIPaddress().getSelectedIndex();
	// if(selectedIndex != 0){
	// if(typ==ProtocolType.SENDER_V4 || typ == ProtocolType.RECEIVER_V4){
	// configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE,
	// BorderType.TRUE));
	// if(typ==ProtocolType.SENDER_V4){
	// input[0][1]=true;
	// }
	// else{
	// input[1][1]=true;
	// }
	// }
	// else{
	// configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv6SOURCE,
	// BorderType.TRUE));
	// if(typ==ProtocolType.SENDER_V6){
	// input[2][1]=true;
	// }
	// else{
	// input[3][1]=true;
	// }
	// }
	// }
	// else if(getSelectedRows(typ).length > 1 &&
	// configpart.getCb_sourceIPaddress().getItemAt(0).equals("...")){
	// if(typ==ProtocolType.SENDER_V4 || typ == ProtocolType.RECEIVER_V4){
	// configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE,
	// BorderType.TRUE));
	// if(typ==ProtocolType.SENDER_V4){
	// input[0][1]=true;
	// }
	// else{
	// input[1][1]=true;
	// }
	// }
	// else{
	// configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv6SOURCE,
	// BorderType.TRUE));
	// if(typ==ProtocolType.SENDER_V6){
	// input[2][1]=true;
	// }
	// else{
	// input[3][1]=true;
	// }
	// }
	// }
	// else{
	// switch(typ){
	// case SENDER_V4: input[0][1]=false; break;
	// case RECEIVER_V4: input[1][1]=false; break;
	// case SENDER_V6: input[2][1]=false; break;
	// case RECEIVER_V6: input[3][1]=false; break;
	// }
	// if(typ == ProtocolType.SENDER_V6){
	// configpart.getPan_sourceIPaddress()
	// .setBorder(MiscBorder.getBorder(BorderTitle.IPv6SOURCE,
	// BorderType.NEUTRAL));
	// }
	// else{
	// configpart.getPan_sourceIPaddress()
	// .setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE,
	// BorderType.NEUTRAL));
	// }
	//
	// }
	// checkInput(typ);
	// }
	// /**

	/**
	 * Funktion welche aufgerufen wird wenn die Eingaben der Textfelder des
	 * Konfigurations Panels zurï¿½ckgesetzt werden sollen.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Textfelder zurï¿½ckgesetzt werden
	 *            sollen.
	 */
	private void clearInput() {

		PanelMulticastconfigNewContext p = f
				.getPanelMulticastconfigNewContext();
		if (p.getButton().getSelectedRadioButton()
				.equals(PanelMulticastConfigNew.RBTN_IGMP)) {
			p.getIgmp().clearIgmp();
		} else if (p.getButton().getSelectedRadioButton()
				.equals(PanelMulticastConfigNew.RBTN_MLD)) {
			p.getMld().clearMld();
		} else {
			p.getMmrp().clearMmrp();
		}
		f.validate();
	}

	/**
	 * Funktion welche aufgerufen wird wenn das Programm beendet wird. Sorgt fï¿½r
	 * ein sauberes Beenden des Programms. (nicht immer mï¿½glich)
	 */
	private void closeProgram() {
		// System.out.println("Shutting down GUI...");
		f.setVisible(false);
		mcController.getUpdateTask().cancel();
		System.out.println("Cleanup...");

		XmlParserWorkbench workbench = new XmlParserWorkbench(logger, this);
		workbench.saveWorkbench(FileNames.WORKBENCH);

		mcController.destroy();

		System.exit(0);
	}

	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	ComponentListener unsichtbar gemacht wird.
	 */
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	ComponentListener verschoben wird.
	 */
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	ComponentListener sichtbar gemacht wird.
	 */
	public void componentShown(ComponentEvent e) {

	}

	/**
	 * Hilfsfunktion welche alle Multicasts aus dem jeweiligen Programmteil
	 * lï¿½scht
	 */
	public void deleteAllMulticasts() {
		selectAll();
		_performDeleteButton();
	}

	/**
	 * Funktion welche aufgerufen wird wenn ein bestimmter Multicast gelï¿½scht
	 * werden soll.
	 * 
	 * @param mcd
	 *            MulticastData Objekt des Multicasts welcher gelï¿½scht werden
	 *            soll.
	 */
	public void deleteMC(MulticastData mcd) {
		mcController.deleteMC(mcd);
		updateTable(UpdateTyp.DELETE);
	}

	/**
	 * Hilfsfunktion welche die aktuelle Anzahl an Multicasts vom
	 * MulticastController anfordert.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Multicasts gezï¿½hlt werden sollen.
	 * @return Zï¿½hler der angibt wievielel Multicasts sich in einem Programmteil
	 *         befinden.
	 */
	public int getMCCount() {
		int count = 0;
		if (mcController != null) {
			if (mcController.getCurrentModus().equals(Modus.SENDER)) {
				count = mcController.getMcDataVectorSender().size();
			} else {
				count = mcController.getMcDataVectorReceiver().size();
			}
		}
		return count;
	}

	/**
	 * Hilfsfunktion welche Multicast Daten vom MulticastController anfordert.
	 * 
	 * @param i
	 *            Index des angeforderten Multicasts (Index in der Tabelle).
	 * @return MulticastData Objekt welches vom MulticastController
	 *         zurï¿½ckgegeben wird.
	 */
	public MulticastData getMCData(int i) {
		return mcController.getMCByIndex(i);
	}

	/**
	 * Hilfsfunktion welche Multicast Daten vom MulticastController anfordert.
	 * 
	 * @param i
	 *            Index des angeforderten Multicasts (Index in der Tabelle).
	 * @return MulticastData Objekt welches vom MulticastController
	 *         zurï¿½ckgegeben wird.
	 */
	public ArrayList<MulticastData> getMCData(int i[]) {
		ArrayList<MulticastData> mc = new ArrayList<MulticastData>();
		for (int x = 0; x < i.length; x++) {
			mc.add(mcController.getMCByIndex(i[x]));
		}
		return mc;
	}

	/**
	 * Hilfsfunktion welche die Statusbar des jeweiligen Programmteils zurï¿½ck
	 * gibt.
	 * 
	 * @return PanelStatusbar welche angefordert wurde.
	 */
	private PanelStatusBar getPanStatus() {
		return f.getPanelStatusBar();
	}

	/**
	 * Hilfsfunktion welche ein Integer Array mit den Selektierten Zeilen einer
	 * Tabele zurï¿½ckgibt.
	 * 
	 * @param typ
	 *            Programmteil in welchem sich die Tabelle befindet.
	 * @return Integer Array mit selektierten Zeilen. (leer wenn keine Zeile
	 *         selektiert ist).
	 * @throws InterruptedException
	 */
	// public int[] getSelectedRows() {
	// int[] ret = null;
	// ret = f.getTable().getSelectedRows();
	// return ret;
	// }

	public void initialize(MulticastController p_mc) {
		mcController = p_mc;
		XmlParserWorkbench workbench = new XmlParserWorkbench(logger, this);
		workbench.loadWorkbench(FileNames.WORKBENCH);
		workbench.setWorkbenchBefore();
		messages = ResourceBundle
				.getBundle("dhbw.multicastor.resources.i18n.messages");
		

		prefs = Preferences.userRoot().node("multicastor/viewController");
		if (f == null) {
			f = new FrameMain(this);
			workbench.setWorkbenchAfter();
			this.setInitFinished(true);
		}
	}

	// /**
	// * Hilfsfunktion zum Testen des Programm mits realen daten, durch diese
	// Funktion kï¿½nnen extrem groï¿½e
	// * Datenmengen simuliert werden.
	// */
	// private void insertTestData(){
	// for(int i = 1 ; i < 6 ; i++){
	// getPanConfig(ProtocolType.SENDER_V4).getTf_groupIPaddress().setText("224.0.0."+i);
	// getPanConfig(ProtocolType.SENDER_V4).getCb_sourceIPaddress().setSelectedIndex(1);
	// getPanConfig(ProtocolType.SENDER_V4).getTf_udp_port().setText("4000"+i);
	// getPanConfig(ProtocolType.SENDER_V4).getTf_ttl().setText("32");
	// getPanConfig(ProtocolType.SENDER_V4).getTf_packetrate().setText(""+10*i);
	// getPanConfig(ProtocolType.SENDER_V4).getTf_udp_packetlength().setText("2048");
	// getPanConfig(ProtocolType.SENDER_V4).getTb_active().setSelected(true);
	// pressBTAdd(ProtocolType.SENDER_V4);
	// //Thread.sleep(100);
	//
	// getPanConfig(ProtocolType.RECEIVER_V4).getTf_groupIPaddress().setText("224.0.0."+i);
	// getPanConfig(ProtocolType.RECEIVER_V4).getTf_udp_port().setText("4000"+i);
	// getPanConfig(ProtocolType.RECEIVER_V4).getTb_active().setSelected(true);
	// pressBTAdd(ProtocolType.RECEIVER_V4);
	// //Thread.sleep(100);
	// getPanConfig(ProtocolType.SENDER_V6).getTf_groupIPaddress().setText("ff00::"+i);
	// getPanConfig(ProtocolType.SENDER_V6).getCb_sourceIPaddress().setSelectedIndex(1);
	// getPanConfig(ProtocolType.SENDER_V6).getTf_udp_port().setText("4000"+i);
	// getPanConfig(ProtocolType.SENDER_V6).getTf_ttl().setText("32");
	// getPanConfig(ProtocolType.SENDER_V6).getTf_packetrate().setText(""+10*i);
	// getPanConfig(ProtocolType.SENDER_V6).getTf_udp_packetlength().setText("1024");
	// getPanConfig(ProtocolType.SENDER_V6).getTb_active().setSelected(true);
	// pressBTAdd(ProtocolType.SENDER_V6);
	// //Thread.sleep(100);
	// getPanConfig(ProtocolType.RECEIVER_V6).getTf_groupIPaddress().setText("ff00::"+i);
	// getPanConfig(ProtocolType.RECEIVER_V6).getTf_udp_port().setText("4000"+i);
	// getPanConfig(ProtocolType.RECEIVER_V6).getTb_active().setSelected(true);
	// pressBTAdd(ProtocolType.RECEIVER_V6);
	// //Thread.sleep(100);
	// }
	//
	// }

	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine GUI Komponente mit dem
	ItemListener selektiert oder deselektiert wird.
	 * Dieser Listener wird fï¿½r RadioButtons und Checkboxen verwendet.
	 */
	public void itemStateChanged(ItemEvent arg0) {
		// if (arg0.getStateChange() == arg0.SELECTED) {
		// if (arg0.getSource() == getPanConfig(ProtocolType.SENDER_V4)
		// .getTf_sourceIPaddress()) {
		// changeNetworkInterface(ProtocolType.SENDER_V4);
		// } else if (arg0.getSource() == getPanConfig(
		// ProtocolType.RECEIVER_V4).getTf_sourceIPaddress()) {
		// changeNetworkInterface(ProtocolType.RECEIVER_V4);
		// } else if (arg0.getSource() == getPanConfig(ProtocolType.SENDER_V6)
		// .getTf_sourceIPaddress()) {
		// changeNetworkInterface(ProtocolType.SENDER_V6);
		// } else if (arg0.getSource() == getPanConfig(
		// ProtocolType.RECEIVER_V6).getTf_sourceIPaddress()) {
		// changeNetworkInterface(ProtocolType.RECEIVER_V6);
		// } else if (arg0.getSource() == getPanTabbed(
		// ProtocolType.RECEIVER_V4).getPan_recGraph().getLostPktsRB()) {
		// // System.out.println("RECEIVER_V4 - LostPacketsRB");
		// getPanTabbed(ProtocolType.RECEIVER_V4).getPan_recGraph()
		// .selectionChanged(valueType.LOSTPKT);
		// } else if (arg0.getSource() == getPanTabbed(
		// ProtocolType.RECEIVER_V4).getPan_recGraph().getJitterRB()) {
		// // System.out.println("RECEIVER_V4 - JitterRB");
		// getPanTabbed(ProtocolType.RECEIVER_V4).getPan_recGraph()
		// .selectionChanged(valueType.JITTER);
		// } else if (arg0.getSource() == getPanTabbed(
		// ProtocolType.RECEIVER_V4).getPan_recGraph()
		// .getMeasPktRtRB()) {
		// // System.out.println("RECEIVER_V4 - MeasPktRtRB");
		// getPanTabbed(ProtocolType.RECEIVER_V4).getPan_recGraph()
		// .selectionChanged(valueType.MEASPKT);
		// } else if (arg0.getSource() == getPanTabbed(
		// ProtocolType.RECEIVER_V6).getPan_recGraph().getLostPktsRB()) {
		// // System.out.println("RECEIVER_V6 - LostPacketsRB");
		// getPanTabbed(ProtocolType.RECEIVER_V6).getPan_recGraph()
		// .selectionChanged(valueType.LOSTPKT);
		// } else
		if (arg0.getSource() == ((ReceiverGraph) f.getPan_graph())
				.getJitterRB()) {
			// System.out.println("RECEIVER_V6 - JitterRB");
			((ReceiverGraph) f.getPan_graph())
					.selectionChanged(valueType.JITTER);
		} else if (arg0.getSource() == ((ReceiverGraph) f.getPan_graph())
				.getMeasPktRtRB()) {
			// System.out.println("RECEIVER_V6 - MeasPktRtRB");
			((ReceiverGraph) f.getPan_graph())
					.selectionChanged(valueType.MEASPKT);
		} else if (arg0.getSource() == ((ReceiverGraph) f.getPan_graph())
				.getLostPktsRB()) {
			// System.out.println("RECEIVER_V6 - LostPktRtRB");
			((ReceiverGraph) f.getPan_graph())
					.selectionChanged(valueType.LOSTPKT);
		}

	}

	/**
	 * Funktion welche ausgelï¿½st wird wenn der User eine oder mehrere Zeilen in
	 * der Tabelle selektiert.
	 * 
	 * @param typ
	 *            Programmteil in welchem der User die Zeilen selektiert hat.
	 */
	private void listSelectionEventFired() {
		int[] selectedRows = getSelectedRows();
		f.getPanelStatusBar().setLabelSelectedText(selectedRows.length);

		if (selectedRows.length > 1) {
			// multipleSelect(typ);
			this.setEditable(false);
			this.clearInput();
		}
		int row = 0;
		if (selectedRows.length == 1) {
			GraphLines.setHighlightMC(getMCData(selectedRows[0]));
			try {
				this.setEditable(true);
				if (getMCData(selectedRows[0]).getProtocolType().equals(
						ProtocolType.MMRP)) {
					f.getPanelMulticastconfigNewContext().getButton()
							.getRbtn_MMRP().doClick();
					PanelMulticastConfigNew panel = f
							.getPanelMulticastconfigNewContext()
							.getPanelMulticastConfigNew();
					panel.getTf_address().setEnabled(true);

					MMRPData mmrp = (MMRPData) getMCData(selectedRows[0]);
					row = NetworkAdapter.findAddressIndex(
							MulticastData.ProtocolType.MMRP, mmrp
									.getMacSourceId().toString());
					panel.getTf_address().setText(
							mmrp.getMacGroupId().toString());
					panel.getCb_interface().setEnabled(true);
					panel.getCb_interface().setSelectedIndex(row);
					if (this.mcController.getCurrentModus() == MulticastController.Modus.SENDER) {
						panel.getTf_PRate().setText(
								String.valueOf(mmrp.getPacketRateDesired()));
						panel.getTf_PLength().setText(
								String.valueOf(mmrp.getPacketLength()));
					} else {
						System.out
								.println(String.valueOf(mmrp.getTimerJoinMt()));
						panel.getTf_JoinMtTimer().setText(
								String.valueOf(mmrp.getTimerJoinMt()));
					}

				} else {
					IgmpMldData igmpMld = (IgmpMldData) getMCData(selectedRows[0]);
					if (igmpMld.getProtocolType().equals(
							MulticastData.ProtocolType.IGMP)) {
						f.getPanelMulticastconfigNewContext().getButton()
								.getRbtn_IGMP().doClick();
					} else {
						f.getPanelMulticastconfigNewContext().getButton()
								.getRbtn_MLD().doClick();
					}
					PanelMulticastConfigNew panel = f
							.getPanelMulticastconfigNewContext()
							.getPanelMulticastConfigNew();
					panel.getTf_address().setEnabled(true);
					panel.getTf_port().setEnabled(true);
					panel.getTf_address().setText(igmpMld.getGroupIpToString());
					panel.getTf_port().setText(
							String.valueOf(igmpMld.getUdpPort()));
					if (this.mcController.getCurrentModus() == MulticastController.Modus.SENDER) {
						if (igmpMld.getProtocolType().equals(
								MulticastData.ProtocolType.IGMP)) {
							row = NetworkAdapter.findAddressIndex(
									MulticastData.ProtocolType.IGMP, igmpMld
											.getSourceIp().toString());
						} else {
							row = NetworkAdapter.findAddressIndex(
									MulticastData.ProtocolType.MLD, igmpMld
											.getSourceIp().toString());
						}
						panel.getCb_interface().setSelectedIndex(row);
						panel.getTf_TTL().setText("" + igmpMld.getTtl());
						panel.getTf_PRate().setText(
								"" + igmpMld.getPacketRateDesired());
						panel.getTf_port().setText("" + igmpMld.getUdpPort());
						panel.getTf_PLength().setText(
								"" + igmpMld.getPacketLength());
					}

				}
			} catch (Exception e) {
				logger.info("Table couldn't be filled because of missing arguments");
			}
			f.getPanelMulticastconfigNewContext().getButton()
					.btn_ChangeSetEnabled(true);
		} else if (selectedRows.length == 0) {
			clearInput();
			GraphLines.clearHighlight();
			f.getPanelMulticastconfigNewContext().getButton()
					.btn_ChangeSetEnabled(false);
			setDefaultValues();
			this.setEditable(true);
		}
	}

	/**
	 * Funktion welche ausgelï¿½st wird, wenn der User versucht eine
	 * Konfigurationsdatei mit dem "Datei Laden" Dialog zu laden.
	 */
	private void loadFileEvent(ActionEvent e) {
		if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
			FrameFileChooser fc_load = f.getFc_load();
			loadConfig(fc_load.getSelectedFile(),
					fc_load.isCbIncrementalSelected());

			fc_load.getChooser().rescanCurrentDirectory();
			fc_load.toggle();
		} else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
			f.getFc_load().toggle();
		}
	}

	private void loadConfig(String s, boolean incremental) {
		if (!incremental) {
			deleteAllMulticasts();
		}
		mcController.loadConfigFile(s);
		XmlParserWorkbench.addFilePath(s);
		f.resetMenubar();
	}

	@Override
	/**
	 * MouseEvent welches auf das Betreten einer Komponente der Maus reagiert.
	 */
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	/**
	 * MouseEvent welches auf das Verlassen einer Komponente der Maus
	reagiert.
	 */
	public void mouseExited(MouseEvent e) {

	}

	@Override
	/**
	 * MouseEvent welches auf Drï¿½cken einer Maustaste reagiert.
	 */
	public void mousePressed(MouseEvent e) {

	}

	@Override
	/**
	 * MouseEvent welches auf Loslassen einer Maustaste reagiert.
	 */
	public void mouseReleased(MouseEvent e) {

	}

	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedrï¿½ckt wird.
	 * 
	 * @param typ
	 *            Programmteil in welchem der Add Button gedrï¿½ckt wurde
	 */
	private void pressBTAdd() {
		PanelMulticastConfigNew panel = f.getPanelMulticastconfigNewContext()
				.getPanelMulticastConfigNew();
		ProtocolType typ = panel.getType();
		if (typ.equals(ProtocolType.IGMP)) {
			this.addMC(changeMCData(new IgmpMldData(ProtocolType.IGMP), typ));
		} else if (typ.equals(ProtocolType.MLD)) {
			this.addMC(changeMCData(new IgmpMldData(ProtocolType.MLD), typ));
		} else {
			MulticastData mmrp = changeMCData(new MMRPData(ProtocolType.MMRP), typ);
			if(checkIfAlreadyInserted(mmrp)){
				this.addMC(mmrp);
			}
		}
		clearInput();
	}

	private boolean checkIfAlreadyInserted(MulticastData mmrp) {
		return mcController.checkIfInterfaceAlreadyInserted((MMRPData)mmrp);
	}

	/**
	 * Method that clears the current selection
	 */
	private void deselectAll() {
		f.getTable().clearSelection();
	}

	/**
	 * Funktion welche es dem Multicast Controller und somit den restlichen
	 * Programmteilen ermï¿½glicht Ausgaben in der Konsole des GUI zu tï¿½tigen.
	 * 
	 * @param s
	 *            Nachricht welche in der Konsole der GUI ausgegeben werden soll
	 */
	public void printConsole(String s) {
		if(f.isVisible()){
			f.getTa_console().append(s + "\n");
		}
	}

	//
	// /**
	// * Removed einen Container und einen Keylistener vom
	// * Component c. Hat die Komponente Kinder, mache das
	// * gleiche mit jedem Child
	// * @param c Container, von dem die Listener entfernt werden sollen
	// */
	// private void removeKeyAndContainerListenerToAll(Component c)
	// {
	// c.removeKeyListener(this);
	// if(c instanceof Container) {
	// Container cont = (Container)c;
	// cont.removeContainerListener(this);
	// Component[] children = cont.getComponents();
	// for(int i = 0; i < children.length; i++){
	// removeKeyAndContainerListenerToAll(children[i]);
	// }
	// }
	// }

	/**
	 * Funktion welche aufgerufen wird wenn versucht wird eine Datei zu
	 * speichern im Datei speichern Dialog.
	 * 
	 * @param e
	 *            ActionEvent welches vom Datei speichern Dialog erzeugt wird
	 */
	private void saveFileEvent(ActionEvent e) {
		if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
			FrameFileChooser fc_save = f.getFc_save();
			// System.out.println("selected File: "+fc_save.getSelectedFile());
			mcController
					.saveConfig(fc_save.getSelectedFile(), true, true, true);
			f.updateLastConfigs(fc_save.getSelectedFile());
			fc_save.getChooser().rescanCurrentDirectory();
			fc_save.toggle();
		} else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
			f.getFc_save().toggle();
		}
	}

	/**
	 * Funktion welche ermï¿½glich Nachrichten in der GUI anzuzeigen. Gibt anderen
	 * Programmteilen ï¿½ber den MulticastController die Mï¿½glichkeit Informations,
	 * Warnungs und Errormeldungen auf dem GUI auszugeben.
	 * 
	 * @param typ
	 *            Art der Nachricht (INFO / WARNING / ERROR)
	 * @param message
	 *            Die eigentliche Nachricht welche angezeigt werden soll
	 */
	public void showMessage(MessageTyp typ, String message) { // type 0 ==
		// info, type 1 == warning, type 2 == error
		switch (typ) {
		case INFO:
			JOptionPane.showMessageDialog(null, message, "Information",
					JOptionPane.INFORMATION_MESSAGE);
			break;
		case WARNING:
			JOptionPane.showMessageDialog(null, message, "Warning",
					JOptionPane.WARNING_MESSAGE);
			break;
		case ERROR:
			JOptionPane.showMessageDialog(null, message, "Error",
					JOptionPane.ERROR_MESSAGE);
			break;
		}
	}

	/**
	 * Funktion welche sich um das Update des Graphen kï¿½mmert.
	 * 
	 * @param typ
	 *            bestimmt welcher Graph in welchem Programmteil geupdatet
	 *            werden soll
	 */
	private void updateGraph() {
		// boolean variable which determines if graph can be painted or not, is
		// graph in front of SRC address dropdown
		if (f.getTab_console().getSelectedIndex() == 0) {
			if (mcController.getCurrentModus().equals(
					MulticastController.Modus.SENDER)) {
				// System.out.println("showupdate "+showupdate);

				// int[] selectedRows = f.getTable().getSelectedRows();
				// if (selectedRows.length == 0) {
				// if (getMCData(selectedRows[0]) instanceof IgmpMldData) {
				// IgmpMldData igmpMld = (IgmpMldData)
				// getMCData(selectedRows[0]);
				// f.getPan_graph().updateGraph(mc.getPPSSender(igmpMld),
				// showupdate);
				// }
				// }
				for (ArrayList<MulticastData> d : GraphLines.getGroupDatas()) {
					GraphLines.setValue(d, mcController.getPPSSender(d));
				}
				f.getPan_graph().updateGraph();
			} else if (mcController.getCurrentModus().equals(
					MulticastController.Modus.RECEIVER)) {
				for (ArrayList<MulticastData> d : GraphLines.getGroupDatas()) {
					((ReceiverGraph) f.getPan_graph()).updateGraph(d);
				}
			}
		}
		f.getPan_graph().repaint();
		f.getPan_graph().resize(new Dimension(f.getSize().width - 225, 100));
	}

	/**
	 * Funktion welche unterscheidet welche Art von Update in der Multicast
	 * Tabelle erfolgt ist. Hierbei kann zwischen Einfï¿½gen, Lï¿½schen und Updaten
	 * einer Zeile unterschieden werden.
	 * 
	 * @param typ
	 *            Bestimmt den Programmteil welcher geupdated wird
	 * @param utyp
	 *            Bestimmt die Art des Updates welches Erfolgt ist
	 */
	public void updateTable(UpdateTyp utyp) { // i=0 ->
												// insert, i=1
												// -> delete,
												// i=2 -> change
		MiscTableModel table = (MiscTableModel) f.getTable().getModel();

		switch (utyp) {
		case INSERT:
			table.insertUpdate();
			f.getPanelStatusBar().setLabelMCCountText(getMCCount());
			if (initFinished) {
				clearInput();
			}
			break;
		case DELETE:
			table.deleteUpdate();
			f.getPanelStatusBar().setLabelMCCountText(getMCCount());
			if (initFinished) {
				clearInput();
			}
			break;

		case UPDATE:
			table.changeUpdate();
			break;
		}
	}

	/**
	 * Funktion die einen bestimmten Programmteil updatet.
	 */
	public void updateTablePart() {
		updateTable(UpdateTyp.UPDATE);
	}

	@Override
	/**
	 * Implementierung des ListSelectionListeners, sorgt fï¿½r korrektes
	Verhalten der GUI
	 * beim Selektieren und Deselektieren von einer oder mehreren Zeilen in
	der Tabelle.
	 */
	public void valueChanged(ListSelectionEvent e) {

		// If cell selection is enabled, both row and column change events are
		// fired
		if (e.getSource() == f.getTable().getSelectionModel()
				&& f.getTable().getRowSelectionAllowed()
				&& !e.getValueIsAdjusting()) {
			// Row selection changed
			listSelectionEventFired();

			int index = getSelectedRow();
			String frameTitle = "";
			if (mcController.getCurrentModus().equals(Modus.SENDER)) {
				frameTitle = messages.getString("FrameMain.senderTitle");
			} else {
				frameTitle = messages.getString("FrameMain.receiverTitle");
			}
			if (index != -1) {
				MulticastData mc = getMCData(index);
				PanelMulticastInfo panInfo = f.getPan_info();
				panInfo.update(mc);
				if (mc instanceof IgmpMldData) {
					// set frame-title
					if (getCurrentModus().equals(Modus.SENDER)) {
						f.setTitle(frameTitle + " "
								+ ((IgmpMldData) mc).getSourceIp().toString());
					}
				} else if (mc instanceof MMRPData) {
					// set frame-title
					f.setTitle(frameTitle + " "
							+ ((MMRPData) mc).getMacSourceId().toString());
				}
			} else {
				f.setTitle(frameTitle);
			}

		} else if (e.getSource() == f.getTable().getColumnModel()
				.getSelectionModel()
				&& f.getTable().getColumnSelectionAllowed()) {
			// Column selection changed
		}

		_updateButtons(getSelectedRows());

		f.repaint();
		autoSave();
	}

	/**
	 * Diese Funktion bildet die eigentliche Schnittstelle zum
	 * MulticastController und ermï¿½glicht die GUI zu einem bestimmen Zeitpunkt
	 * zu updaten.
	 */
	public void viewUpdate() {
		ProtocolType typ = f.getPanelMulticastconfigNewContext()
				.getPanelMulticastConfigNew().getType();
		if (typ != ProtocolType.UNDEFINED) {
			int index = getSelectedRow();
			if (index > -1) {
				f.getPan_info().update(getMCData(index));
			}
			updateTablePart();
			if (f.getPan_graph().isVisible()) {
				updateGraph();
			}
			getPanStatus().updateTraffic(getTotalTrafficUP(),
					getTotalTrafficDown());
		}
	}

	public int getSelectedRow() {
		int index = f.getTable().getSelectedRow();
		if (index > -1) {
			return f.getTable().convertRowIndexToModel(index);
		} else {
			return -1;
		}
	}

	public int[] getSelectedRows() {
		int[] rows = f.getTable().getSelectedRows();
		int[] ret = new int[rows.length];
		for (int i = 0; i < rows.length; i++) {
			ret[i] = f.getTable().convertRowIndexToModel(rows[i]);
		}
		return ret;
	}

	/**
	 * Funktion welche die aktuellen Nutzereingaben im Programm speichert.
	 */
	public void autoSave() {
		if (initFinished /* && f.isAutoSaveEnabled() */) {
			// submitInputData();
		}
	}

	/**
	 * Funktion welche bei Programmstart die Automatische
	 */
	public void loadAutoSave() {
		// Vector<UserInputData> loaded = mcController.loadAutoSave();
		// System.out.println("loaded.size "+loaded.size());
		// if ((loaded != null) && (loaded.size() == 4)) {
		// inputData_R4 = loaded.get(0);
		// inputData_S4 = loaded.get(1);
		// inputData_R6 = loaded.get(2);
		// inputData_S6 = loaded.get(3);

		// TODO muss noch kontrolliert werden
		// hab ich nur mal geï¿½ndert damit es keinen Fehler mehr gibt
		// loadAutoSavePart(inputData_R4, ProtocolType.IGMP);
		// loadAutoSavePart(inputData_S4, ProtocolType.MLD);
		// loadAutoSavePart(inputData_R6, ProtocolType.MMRP);

		// f.setLastConfigs(mcController.getConfig().getLastConfigs(), false);
		// System.out.println("size: "+mc.getLastConfigs().size());
		// }

		initFinished = true;
	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object aktiviert wird
	 */
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster geschlossen wird
	 */
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster geï¿½ffnet wird
	 */
	public void windowClosing(WindowEvent e) {
		closeProgram();
	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object deaktiviert
	wird
	 */
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster de-minimiert wurde
	 */
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster minimiert wurde
	 */
	public void windowIconified(WindowEvent e) {

	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster geï¿½ffnet wird
	 */
	public void windowOpened(WindowEvent e) {

	}

	public double getTotalTrafficUP() {
		double traffic = 0.0;
		for (MulticastData data : mcController.getMcDataVectorSender()) {
			traffic += (data.getTraffic() / 1024.0 / 1024.0 * 8.0);
		}
		return traffic;
	}

	public double getTotalTrafficDown() {
		double traffic = 0.0;
		for (MulticastData data : mcController.getMcDataVectorReceiver()) {
			traffic += (data.getTraffic() / 1024.0 / 1024.0 * 8.0);
		}
		return traffic;
	}

	public void startMC(MulticastData data) {
		f.getPanelButtonBar().toggleButtonText(data.getActive());
		mcController.startMC(data);
	}

	public void stopMC(MulticastData data) {
		f.getPanelButtonBar().toggleButtonText(data.getActive());
		mcController.stopMC(data);
	}

	public void changeModus(Modus modus) {
		mcController.setCurrentModus(modus);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	public Vector<MulticastData> getMCs() {
		if (mcController.getCurrentModus().equals(Modus.SENDER)) {
			return mcController.getMcDataVectorSender();
		} else {
			return mcController.getMcDataVectorReceiver();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			JTextField text = (JTextField) e.getSource();
			text.selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {

	}

	private void _performActivateButton() {
		int[] rows = getSelectedRows();
		boolean activate = _checkActivateButton(rows);
		MulticastData[] datas = new MulticastData[rows.length];
		if (activate) {
			for (int i = 0; i < rows.length; i++) {
				datas[i] = getMCData(rows[i]);
				if (!datas[i].getActive()) {
//					System.out.println(datas[i].hashCode());
					startMC(datas[i]);
					f.getPanelButtonBar().toggleButtonText(false);
				}
			}
		} else {
			for (int i = 0; i < rows.length; i++) {
				datas[i] = getMCData(rows[i]);
				if (datas[i].getActive()) {
					stopMC(datas[i]);
					f.getPanelButtonBar().toggleButtonText(true);
					GraphLines.deleteMC(datas[i]);
				}
			}
		}
		displayGraph(activate);
	}

	private void _updateButtons(int[] rows) {
		if (rows.length != 0) {
			f.getPanelButtonBar().enableAllButtons(true);
			f.getPanelButtonBar().getB_activate().setEnabled(true);
			boolean active = _checkActivateButton(rows);
			f.getPanelButtonBar().toggleButtonText(active);
			_checkDisplayButton(rows);
			f.getPanelButtonBar().getB_leaveAll()
					.setEnabled(_leaveAllActive(rows));
			f.getPanelButtonBar().getB_leave().setEnabled(_leaveActive(rows));
		} else {
			f.getPanelButtonBar().enableAllButtons(false);
			f.getPanelButtonBar().toggleButtonText(true);
		}

	}

	/**
	 * Bedingung, dass LeaveAll-Button aktive ist.
	 * 
	 * @return
	 */
	private boolean _leaveAllActive(int[] rows) {
		boolean allMMRP = true;
		for (int i = 0; i < rows.length; i++) {
			if (!(getMCData(rows[i]).getProtocolType() == ProtocolType.MMRP)) {
				allMMRP = false;
			}
		}
		return allMMRP;
	}

	/**
	 * Bedingung, dass Leave-Button aktive ist.
	 * 
	 * @return
	 */
	private boolean _leaveActive(int[] rows) {
		if (getCurrentModus() == Modus.SENDER) {
			boolean allMMRP = true;
			for (int i = 0; i < rows.length; i++) {
				if (!(getMCData(rows[i]).getProtocolType() == ProtocolType.MMRP)) {
					allMMRP = false;
				}
			}
			return allMMRP;
		}
		return false;
	}

	/**
	 * Prueft den ersten makierten MC. Setzt den Text des Buttons.<br>
	 * Display bei inactiven Graphen.<br>
	 * Hide bei activen Graphen.
	 * 
	 * @param sel
	 *            , selektierte Reihen in der Tabelle
	 */
	private void _checkDisplayButton(int[] sel) {
		if (sel.length > 0 && getMCData(sel[0]).isGraph()) {
			f.getPanelButtonBar()
					.getB_display()
					.setText(
							messages.getString("PanelButtonBar.b_display.hide.text"));
		} else {
			f.getPanelButtonBar()
					.getB_display()
					.setText(
							messages.getString("PanelButtonBar.b_display.text"));
		}
	}

	private boolean _checkActivateButton(int[] rows) {
		boolean active = false;
		int i = 0;
		MulticastData mc = null;
		while (!active && i < rows.length) {
			mc = getMCData(rows[i]);
			if (!mc.getActive())
				active = true;
			i++;
		}
		return active;
	}

	private void _performResetButton() {
		// TODO Convert view to model
		int[] rows = getSelectedRows();
		MulticastData mc = null;
		for (int i = 0; i < rows.length; i++) {
			mc = getMCData(rows[i]);
			mc.setPacketCount(0);
			MulticastThreadSuper t = mcController.getMcMap().get(mc);
			if (t instanceof MulticastSender) {
				((MulticastSender) t).reset();
			} else if (t instanceof MulticastReceiverLayer3) {
				((MulticastReceiverLayer3) t).reset();
			}
		}
	}

	/**
	 * Aktiviert bzw. Deaktiviert in Abhaengigkeit vom ersten selektierten MC
	 * den Graphen.
	 */
	private void _performDisplayButton() {
		int[] sel = getSelectedRows();
		if (getMCData(sel[0]).isGraph()) {
			displayGraph(false);
		} else {
			displayGraph(true);
		}
		_checkDisplayButton(sel);
	}

	private void _performDeleteButton() {
		int rows[] = getSelectedRows();
		MulticastData[] datas = new MulticastData[rows.length];
		for (int i = 0; i < rows.length; i++) {
			datas[i] = getMCData(rows[i]);
		}
		for (int i = 0; i < datas.length; i++) {
			mcController.deleteMC(datas[i]);
			if (datas[i].isGraph()) {
				GraphLines.deleteMC(datas[i]);
			}
			updateTable(UpdateTyp.DELETE);
		}
		f.getPan_info().reset();
	}

	/**
	 * Wird ausgefuehrt wenn Leave-Button betaetigt wird.
	 */
	private void _performLeaveButton() {
		int rows[] = getSelectedRows();
		MulticastData mc = null;
		for (int i = 0; i < rows.length; i++) {
			mc = getMCData(rows[i]);
			mcController.sendLeave(mc);
		}
	}

	/**
	 * Wird ausgefuehrt wenn LeaveAll-Button betaetigt wird.
	 */
	private void _performLeaveAllButton() {
		int rows[] = getSelectedRows();
		MulticastData mc = null;
		for (int i = 0; i < rows.length; i++) {
			mc = getMCData(rows[i]);
			mcController.sendLeaveAll(mc);
		}
	}

	private int showRestartDialog() {
		int n = JOptionPane.showOptionDialog(getFrameMain(),
				messages.getString("MessageBox.text"),
				messages.getString("MessageBox.title"),
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				null, null);
		return n;
	}

	private void pressBTChange(MulticastData mc) {
		mc.resetValues();
		mc = changeMCData(mc, mc.getProtocolType());
		changeMC(mc);
	}

	public void enableAddButton(boolean enabled) {
		f.getPanelMulticastconfigNewContext().getButton().getBtn_add()
				.setEnabled(enabled);
	}

	public FrameMain getFrameMain() {
		return f;
	}

	public JTable getTable() {
		return f.getTable();
	}

	public MulticastController getMcController() {
		return mcController;
	}

	public void setInitFinished(boolean initFinished) {
		this.initFinished = initFinished;
	}

	public boolean isInitFinished() {
		return initFinished;
	}

	public void restoreSettings() {
		XmlParserWorkbench workbench = new XmlParserWorkbench(logger, this);
		workbench.loadWorkbench(FileNames.WORKBENCH);
		workbench.setRB();
	}
}