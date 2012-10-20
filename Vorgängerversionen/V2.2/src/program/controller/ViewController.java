package program.controller;


import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;

import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.data.UserInputData;
import program.model.InputValidator;
import program.model.NetworkAdapter;
import program.view.ConfigMenu;
import program.view.FrameFileChooser;
import program.view.FrameMain;
import program.view.MiscBorder;
import program.view.MiscBorder.BorderTitle;
import program.view.MiscBorder.BorderType;
import program.view.MiscTableModel;
import program.view.PanelMulticastConfig;
import program.view.PanelMulticastControl;
import program.view.PanelStatusBar;
import program.view.PanelTabbed;
import program.view.PanelTabpaneConfiguration;
import program.view.PopUpMenu;
import program.view.ReceiverGraph.valueType;
/**
 * Steuerungsklasse des GUI
 * @author	Daniel Becker
 *
 */

 
public class ViewController implements 	ActionListener, MouseListener, ChangeListener, ComponentListener,
ListSelectionListener, KeyListener, DocumentListener, ItemListener,
ContainerListener, TableColumnModelListener, MouseMotionListener, WindowListener{
	/**
	 * Enum welches angibt um was f�r eine Art von GUI Benachrichtigung es sich handelt.
	 * @author Daniel Becker
	 *
	 */
	public enum MessageTyp {
		INFO, WARNING, ERROR
	}
	/**
	 * Enum welches angibt um was f�r eine Art von GUI Update es sich handelt.
	 * @author Daniel Becker
	 *
	 */
	public enum UpdateTyp{
		UPDATE, INSERT, DELETE
	}
	/**
	 * Referenz zum MulticastController, wichtigste Schnittstelle der Klasse.
	 */
	private MulticastController mc;
	/**
	 * Referenz zum Frame in welchem das MultiCastor Tool angezeigt wird.
	 */
	private FrameMain f;
	/**
	 * 2-Dimensionales Boolean Feld welches angibt in welchem Feld des jeweiligen Konfiguration Panels
	 * eine richtige oder falsche eingabe get�tigt wurde.
	 * [0]:IP Sender
	 * [1]:IP Receier
	 * [2]:MMRP Sender
	 * [3]:MMRP Receier
	 */
	private boolean graphUpdate = false;
	private boolean[][] input = new boolean[4][6];
	/**
	 * Hilfsvariable welche ben�tigt wird um die gemeinsamen Revceiver Graph Daten
	 * (JITTER, LOST PACKETS, MEASURED PACKET RATE) von mehreren Mutlicasts zu berechnen.
	 */
	private MulticastData[] graphData;
	/**
	 * Hilfsvariable welche angibt wann die Initialisierung der GUI abgeschlossen ist.
	 */
	private boolean initFinished=false;
	public void setInitFinished(boolean initFinished) {
		this.initFinished = initFinished;
	}
	/**
	 * Datenobjekt welches den Input des IPv4 Senders enth�lt.
	 */
	private UserInputData userInputData;
	public boolean isInitFinished() {
		return initFinished;
	}

	/**
	 * Standardkonstruktor der GUI, hierbei wird die GUI noch nicht initialisiert!
	 */
	public ViewController(){
		//initialize(null);
	}
	/**
	 * Implementierung des ActionListeners, betrifft die meisten GUI Komponenten.
	 * Diese Funktion wird aufgerufen wenn eine Interaktion mit einer GUI Komponente stattfindet, welche
	 * den ActionListener dieser ViewController Klasse h�lt. Die IF-THEN-ELSEIF Abragen dienen dazu
	 * die Komponente zu identifizieren bei welcher die Interaktion stattgefunden hat.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==f.getMi_saveconfig()){
			f.getFc_save().getChooser().rescanCurrentDirectory();
			f.getFc_save().toggle();
		}
		else if(e.getSource()==f.getMi_loadconfig()){
			f.getFc_load().getChooser().rescanCurrentDirectory();
			f.getFc_load().toggle();
			setColumnSettings(userInputData, getSelectedTab());
		}
		else if(e.getSource()==f.getMi_loadprofile()){
			f.getFc_load_profile().getChooser().rescanCurrentDirectory();
			f.getFc_load_profile().toggle();
		}
		else if(e.getSource()==f.getMi_saveprofile()){
			f.getFc_save_profile().getChooser().rescanCurrentDirectory();
			f.getFc_save_profile().toggle();
		}
		else if(e.getSource()==f.getMi_settings()) {
			ConfigMenu.showDialog(f, this);
		}
		else if(e.getSource()==f.getMi_exit()){
			closeProgram();
		}
		else if(e.getSource() == f.getMi_help()){
			//failed in manchen distributionen
			if (Desktop.isDesktopSupported()) {
				try {
					File myFile;
					
					if(Messages.getString("ConfigMenu.0").compareToIgnoreCase("Allgemeine Einstellungen") == 0)
					{
						myFile = new File("MultiCastor_manual_de.pdf");
					}
					else
					{
						myFile = new File("MultiCastor_manual.pdf");
					}

					
					Desktop.getDesktop().open(myFile);

				} catch (Exception ex) {
					System.out.println("ERROR: Could not open manual file.");
					System.out.println(ex.getMessage());
					// no application registered for PDFs
				}
			}
		}
		else if(e.getSource()==f.getMi_about()){
			JOptionPane.showMessageDialog(f, "Version: "+Main.version, "Version", 1);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_V4).getBt_change()){
			pressBTChange(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_V4).getBt_change()){
			pressBTChange(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_MMRP).getBt_change()){
			pressBTChange(Typ.SENDER_MMRP);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_MMRP).getBt_change()){
			pressBTChange(Typ.RECEIVER_MMRP);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_V4).getBt_add()){
			pressBTAdd(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_V4).getBt_add()){
			pressBTAdd(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_MMRP).getBt_add()){
			pressBTAdd(Typ.SENDER_MMRP);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_MMRP).getBt_add()){
			pressBTAdd(Typ.RECEIVER_MMRP);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getDelete()){
			pressBTDelete(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getDelete()){
			pressBTDelete(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==f.getPanel_sender().getBtReceiver()){
			pressBTReceiver();
		}
		else if(e.getSource()==f.getPanel_receiver().getBtReceiver()){
			pressBTReceiver();
		}
		else if(e.getSource()==f.getPanel_sender().getBtReceiver()){
			pressBTSender();
		}
		else if(e.getSource()==f.getPanel_receiver().getBtSender()){
			pressBTSender();
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getSelectDeselect_all()){
			pressBTSelectDeselectAll(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getSelectDeselect_all()){
			pressBTSelectDeselectAll(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getNewmulticast()){
			pressBTNewMC(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getNewmulticast()){
			pressBTNewMC(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V6).getNewmulticast()){
			pressBTNewMC(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V6).getNewmulticast()){
			pressBTNewMC(Typ.RECEIVER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getStop()){
			pressBTStop(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getStop()){
			pressBTStop(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getStart()){
			pressBTStart(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getStart()){
			pressBTStart(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getFrame().getFc_save().getChooser()){
			saveFileEvent(e);
		}
		else if(e.getSource()==getFrame().getFc_load().getChooser()){
			loadFileEvent(e);
		}
		else if(e.getSource()==getFrame().getFc_save_profile().getChooser()){
			saveProfileEvent(e);
		}
		else if(e.getSource()==getFrame().getFc_load_profile().getChooser()){
			loadProfileEvent(e);
		}
		else if(e.getActionCommand().equals("hide")){ //$NON-NLS-1$
			hideColumnClicked();
			getTable(getSelectedTab()).getColumnModel().removeColumn(getTable(getSelectedTab()).getColumnModel().getColumn(PopUpMenu.getSelectedColumn()));
		}
		else if(e.getActionCommand().equals("showall")){ //$NON-NLS-1$
			popUpResetColumnsPressed();
		}
		else if(e.getActionCommand().equals("PopupCheckBox")){ //$NON-NLS-1$
			popUpCheckBoxPressed();
		}
		else if(e.getActionCommand().equals("lastConfig1")){ //$NON-NLS-1$
			loadConfig(f.getLastConfigs().get(0), f.getFc_load());
		}
		else if(e.getActionCommand().equals("lastConfig2")){ //$NON-NLS-1$
			loadConfig(f.getLastConfigs().get(1), f.getFc_load());
		}
		else if(e.getActionCommand().equals("lastConfig3")){ //$NON-NLS-1$
			loadConfig(f.getLastConfigs().get(2), f.getFc_load());
			} else {
			//System.out.println("DEBUG: UNDETECTED EVENT"); //$NON-NLS-1$
			autoSave();
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn der User Reset View im Popup Menu des Tabellenkopf dr�ckt.
	 * Stellt das urspr�ngliche Aussehen der Tabelle wieder her.
	 */
	private void popUpResetColumnsPressed() {
		userInputData.resetColumns(getSelectedTab());
		getPanTabbed(getSelectedTab()).setTableModel(this, getSelectedTab());
	}
	/**
	 * Funktion die aufgerufen wird wenn eine Checkbox im Popup Menu des Tabellenkopfs gedr�ckt wird.
	 * Wird verwendet zum Einblenden und Ausblenden von Tabellenspalten.
	 */
	private void popUpCheckBoxPressed() {
		ArrayList<Integer> visibility = new ArrayList<Integer>();
		UserInputData store = new UserInputData();
		store.setColumnOrder(userInputData.getColumnOrder(getSelectedTab()),getSelectedTab());
		JCheckBox[] columns = PopUpMenu.getColumns();
		for(int i = 0 ; i < columns.length ; i++){
			if(columns[i].isSelected()){
				String s = columns[i].getText();
				if(s.equals(Messages.getString("TableS0"))){ //$NON-NLS-1$
					visibility.add(new Integer(0));
				}
				else if(s.equals(Messages.getString("TableS1"))){ //$NON-NLS-1$
					visibility.add(new Integer(1));
				}
				else if(s.equals(Messages.getString("TableS2"))){ //$NON-NLS-1$
					visibility.add(new Integer(2));
				}
				else if(s.equals(Messages.getString("TableS3"))){ //$NON-NLS-1$
					visibility.add(new Integer(3));
				}
				else if(s.equals(Messages.getString("TableS4"))){ //$NON-NLS-1$
					visibility.add(new Integer(4));
				}
				else if(s.equals(Messages.getString("TableS5"))){ //$NON-NLS-1$
					visibility.add(new Integer(5));
				}
				else if(s.equals(Messages.getString("TableS6"))){ //$NON-NLS-1$
					visibility.add(new Integer(6));
				}
				else if(s.equals(Messages.getString("TableS7"))){ //$NON-NLS-1$
					visibility.add(new Integer(7));
				}
				else if(s.equals(Messages.getString("TableS8")) || s.equals(Messages.getString("TableR0"))){ //$NON-NLS-1$ //$NON-NLS-2$
					visibility.add(new Integer(8));
				}
				else if(s.equals(Messages.getString("TableS9")) || s.equals(Messages.getString("TableR1"))){ //$NON-NLS-1$ //$NON-NLS-2$
					visibility.add(new Integer(9));
				}
				else if(s.equals(Messages.getString("TableS10")) || s.equals(Messages.getString("TableR2"))){ //$NON-NLS-1$ //$NON-NLS-2$
					visibility.add(new Integer(10));
				}
				else if(s.equals(Messages.getString("TableS11")) || s.equals(Messages.getString("TableR3"))){ //$NON-NLS-1$ //$NON-NLS-2$
					visibility.add(new Integer(11));
				}
			}
		}
		store.setColumnVisibility(visibility,getSelectedTab());
		setColumnSettings(store, getSelectedTab());
	}
	/**
	 * Added einen Container und einen Keylistener zu dem
	 * Component c. Hat die Komponente Kinder, mache das
	 * gleiche mit jedem Child
	 * @param c Container, der die Listener erhalten soll
	 */
	private void addKeyAndContainerListenerToAll(Component c)
	{
		c.addKeyListener(this);
		if(c instanceof Container) {
			Container cont = (Container)c;
			cont.addContainerListener(this);
			Component[] children = cont.getComponents();
			for(int i = 0; i < children.length; i++){
				addKeyAndContainerListenerToAll(children[i]);
			}
		}
	}
	/**
	 * Funktion welche eni neues Multicast Datenobjekt an den MultiCast Controller weitergibt zur Verarbeitung.
	 * @param mcd neu erstelltes Multicast DatenObjekt welches verarbeitet werden soll.
	 */
	public void addMC(MulticastData mcd){
		mc.addMC(mcd);
		updateTable(mcd.getTyp(),UpdateTyp.INSERT);
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn sich die Parameter eines Textfelds ge�ndert haben.
	 */
	public void changedUpdate(DocumentEvent arg0) {


	}
	/**
	 * Funktion welche ein ge�ndertes Multicast Datenobjekt an den MultiCast Controller weitergibt zur Verarbeitung.
	 * @param mcd Das ge�nderter MulticastData Object.
	 */
	public void changeMC(MulticastData mcd){
		mc.changeMC(mcd);
		updateTable(mcd.getTyp(),UpdateTyp.UPDATE);
	}
	/**
	 * Hilfsfunktion welche den momentanen Input des KonfigurationsPanels in ein Multicast Datenobjekt schreibt.
	 * Wird ben�tigt zum anlegen neuer Multicast sowie zum �ndern vorhandener Multicasts.
	 * @param mcd MulticastData Objet welches ge�ndert werden soll.
	 * @param typ Programmteil aus welchem die Input Daten ausgelesen werden sollen.
	 * @return Ge�nderters Multicast Datenobjekt.
	 */
	private MulticastData changeMCData(MulticastData mcd, MulticastData.Typ typ){
		switch(typ){
		case SENDER_V4:{
			PanelMulticastConfig panConfig=getPanConfig(Typ.SENDER_V4);
				mcd.setGroupIp(InputValidator.checkMC_IPv4(panConfig.getTf_groupIPaddress().getText()));
			if(!(panConfig.getCb_sourceIPaddress().getSelectedIndex()==0)){
				mcd.setSourceIp(panConfig.getSelectedAddress(typ));
			}
				mcd.setPacketLength(InputValidator.checkIPv4PacketLength(panConfig.getTf_udp_packetlength().getText()));
				mcd.setTtl(InputValidator.checkTimeToLive(panConfig.getTf_ttl().getText()));
				mcd.setPacketRateDesired(InputValidator.checkPacketRate(panConfig.getTf_packetrate().getText()));
				mcd.setUdpPort(InputValidator.checkPort(getPanConfig(typ).getTf_udp_port().getText()));
		}
		break;
		case SENDER_V6:{
			PanelMulticastConfig panConfig=getPanConfig(Typ.SENDER_V4);
			mcd.setGroupIp(InputValidator.checkMC_IPv6(panConfig.getTf_groupIPaddress().getText()));
			if(!(panConfig.getCb_sourceIPaddress().getSelectedIndex()==0)){
				mcd.setSourceIp(panConfig.getSelectedAddress(typ));
			}
			mcd.setPacketLength(InputValidator.checkIPv6PacketLength(panConfig.getTf_udp_packetlength().getText()));
			mcd.setTtl(InputValidator.checkTimeToLive(panConfig.getTf_ttl().getText()));
			mcd.setPacketRateDesired(InputValidator.checkPacketRate(panConfig.getTf_packetrate().getText()));
			mcd.setUdpPort(InputValidator.checkPort(panConfig.getTf_udp_port().getText()));
			break;
		}
		case SENDER_MMRP:{
			PanelMulticastConfig panConfig=getPanConfig(Typ.SENDER_MMRP);
			mcd.setGroupMac(NetworkAdapter.macToByteArray(panConfig.getTf_groupIPaddress().getText()));
			
			if(!(panConfig.getCb_sourceIPaddress().getSelectedIndex()==0)){
				mcd.setDevice(panConfig.getSelectedAddress());
			}
			
			mcd.setPacketLength(InputValidator.checkMmrpPacketLength(panConfig.getTf_udp_packetlength().getText()));

			mcd.setTtl(-1); //bei MMRP nicht vorhanden

				mcd.setPacketRateDesired(InputValidator.checkPacketRate(panConfig.getTf_packetrate().getText()));
			mcd.setUdpPort(-1); //bei MMRP nicht vorhanden


			break;
		}
		case RECEIVER_V4:{
			PanelMulticastConfig panConfig=getPanConfig(Typ.RECEIVER_V4);
			mcd.setGroupIp(InputValidator.checkMC_IPv4(panConfig.getTf_groupIPaddress().getText()));
				mcd.setUdpPort(InputValidator.checkPort(panConfig.getTf_udp_port().getText()));
			if(!(panConfig.getCb_sourceIPaddress().getSelectedIndex()==0)){
				mcd.setSourceIp(panConfig.getSelectedAddress(typ));
			}
			break;}
		case RECEIVER_V6:{
			PanelMulticastConfig panConfig=getPanConfig(Typ.RECEIVER_V4);
			mcd.setGroupIp(InputValidator.checkMC_IPv6(panConfig.getTf_groupIPaddress().getText()));
			if(!(panConfig.getCb_sourceIPaddress().getSelectedIndex()==0)){
				mcd.setSourceIp(panConfig.getSelectedAddress(typ));
			}
				mcd.setUdpPort(InputValidator.checkPort(panConfig.getTf_udp_port().getText()));
			break;
		}
		case RECEIVER_MMRP:{
			PanelMulticastConfig panConfig=getPanConfig(Typ.RECEIVER_MMRP);
			mcd.setGroupMac(NetworkAdapter.macToByteArray(panConfig.getTf_groupIPaddress().getText()));
			if(!(panConfig.getCb_sourceIPaddress().getSelectedIndex()==0)){
				mcd.setDevice(panConfig.getSelectedAddress());
			}
			mcd.setUdpPort(-1);

			break;}
		default: //$NON-NLS-1$
		}
		mcd.setTyp(typ);
		return mcd;
	}
	/**
	 * Funktion welche aufgerufen wird wenn der User das Netzwerk Interface in einem Sender �ndert.
	 * @param typ Programmteil in welchem das Netzwerkinterface ge�ndert wurde.
	 */
	private void changeNetworkInterface(Typ typ) {
		PanelMulticastConfig configpart = getPanConfig(typ);
		int selectedIndex = configpart.getTf_sourceAddress().getSelectedIndex();
		if(selectedIndex != 0){
			if(typ==Typ.SENDER_V4 || typ == Typ.RECEIVER_V4){
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.TRUE));
				if(typ==Typ.SENDER_V4){
					input[0][1]=true;
				}
				else{
					input[1][1]=true;
				}
			}
			else{
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPSOURCE, BorderType.TRUE));
				if(typ==Typ.SENDER_MMRP){
					InputValidator.setMmrpMax(getPanConfig(Typ.SENDER_MMRP).getSelectedAddress().getName());
					input[2][1]=true;
					docEventTFlength(typ);
				}
				else{
					InputValidator.setMmrpMax(getPanConfig(Typ.RECEIVER_MMRP).getSelectedAddress().getName());
					input[3][1]=true;
				}
			}
		}
		else if(getSelectedRows(typ).length > 1 && configpart.getCb_sourceIPaddress().getItemAt(0).equals("...")){ //$NON-NLS-1$
			if(typ==Typ.SENDER_V4 || typ == Typ.RECEIVER_V4){
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.TRUE));
				if(typ==Typ.SENDER_V4){
					input[0][1]=true;
				}
				else{
					input[1][1]=true;
				}
			}
			else{
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPSOURCE, BorderType.TRUE));
				if(typ==Typ.SENDER_MMRP){
					input[2][1]=true;
				}
				else{
					input[3][1]=true;
				}
			}
		}
		else{
			switch(typ){
			case SENDER_V4: input[0][1]=false;  break;
			case RECEIVER_V4: input[1][1]=false;  break;
			case SENDER_MMRP: input[2][1]=false;  break;
			case RECEIVER_MMRP: input[3][1]=false;  break;
			}
			if(typ == Typ.SENDER_V4||typ == Typ.RECEIVER_V4){
				configpart.getPan_sourceIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.NEUTRAL));
			}
			else{
				configpart.getPan_sourceIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.MMRPSOURCE, BorderType.NEUTRAL));
			}

		}
		checkInput(typ);
	}
	/**
	 * Setzt Button aktiv/deaktiv abhänig von der Anzahl der selektierten Tabellenspalten
	 * @param typ
	 * @param value
	 */

	private void changeConfigButtonsState(Typ typ, boolean value){
		if(!value){
			getPanConfig(typ).getBt_change().setEnabled(false);
			getPanConfig(typ).getBt_add().setEnabled(false);
		}else {
			int temp = getSelectedRows(typ).length;
			switch(temp){
			case 0:{
				getPanConfig(typ).getBt_change().setText(Messages.getString("ViewController.1")); //$NON-NLS-1$
				getPanConfig(typ).getBt_change().setEnabled(false);
				getPanConfig(typ).getBt_add().setEnabled(true);
				break;
			}
			case 1:{
				getPanConfig(typ).getBt_change().setText(Messages.getString("ViewController.1")); //$NON-NLS-1$
				getPanConfig(typ).getBt_change().setEnabled(true);
				getPanConfig(typ).getBt_add().setEnabled(true);
				break;
			}
			default:{
				if(Typ.isSender(typ)){
					getPanConfig(Typ.SENDER_MMRP).getBt_add().setEnabled(true);
					getPanConfig(Typ.SENDER_V4).getBt_add().setEnabled(true);
					getPanConfig(Typ.SENDER_MMRP).getBt_change().setEnabled(false);
					getPanConfig(Typ.SENDER_V4).getBt_change().setEnabled(false);
				}	else {
					getPanConfig(Typ.RECEIVER_MMRP).getBt_add().setEnabled(true);
					getPanConfig(Typ.RECEIVER_V4).getBt_add().setEnabled(true);
					getPanConfig(Typ.RECEIVER_MMRP).getBt_change().setEnabled(false);
					getPanConfig(Typ.RECEIVER_V4).getBt_change().setEnabled(false);
				}
				break;
			}

			}
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn sich der Inhalt eines Textfelds im Konfigurations Panel �ndert.
	 * Pr�ft ob alle eingaben korrekt sind.
	 * @param typ Programmteil in welchem die Eingaben gepr�ft werden sollen.
	 */
	private void checkInput(Typ typ){
		switch(typ){
		case SENDER_V4:{
			changeConfigButtonsState(typ,
					(	input[0][0] &&
							input[0][1] &&
							input[0][2] &&
							input[0][3] &&
							input[0][4] &&
							input[0][5]));

			break;}
		case RECEIVER_V4:{
			changeConfigButtonsState(typ,
					(	input[1][0] &&
							input[1][1] &&
							input[1][2]));
			break;}
		case SENDER_MMRP:{
			changeConfigButtonsState(typ,
					(	input[2][0] &&
							input[2][1] && //Port und TTL muss nicht geprüft werden
							input[2][4] &&
							input[2][5]));
			break;}
		case RECEIVER_MMRP:	{
			changeConfigButtonsState(typ,
					(	input[3][0] &&
							input[3][1]  ));
			break;}
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn die Eingaben der Textfelder des Konfigurations Panels zur�ckgesetzt werden sollen.
	 * @param typ Programmteil in welchem die Textfelder zur�ckgesetzt werden sollen.
	 */
	private void clearInput(Typ typ){
		if(initFinished){
			getPanConfig(typ).getTf_groupIPaddress().setText(""); //$NON-NLS-1$
			if(Typ.isIP(typ)){
				//	getPanConfig(typ).getTf_udp_port().setText("-");
				getPanConfig(typ).getTf_udp_port().setText("");} //$NON-NLS-1$
			if(Typ.isSender(typ) ){
				getPanConfig(typ).getTf_sourceAddress().setSelectedIndex(0);
				if(typ != Typ.SENDER_MMRP){
					getPanConfig(typ).getTf_ttl().setText("");} //$NON-NLS-1$
				getPanConfig(typ).getTf_packetrate().setText(""); //$NON-NLS-1$
				getPanConfig(typ).getTf_udp_packetlength().setText(""); //$NON-NLS-1$
			}
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0); //$NON-NLS-1$
			getPanConfig(typ).getTf_sourceAddress().setSelectedIndex(0);
			getPanConfig(typ).getTf_groupIPaddress().requestFocusInWindow();
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn das Programm beendet wird.
	 * Sorgt f�r ein sauberes Beenden des Programms. (nicht immer m�glich)
	 */
	private void closeProgram() {
		f.setVisible(false);
		mc.destroy();
		System.exit(0);
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte zur Tabelle hinzugef�gt wird.
	 */
	public void columnAdded(TableColumnModelEvent arg0) {
		// Not used

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn sich der Aussenabstand der Tabellenspalte �ndert.
	 */
	public void columnMarginChanged(ChangeEvent arg0) {
		// Not used

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte in der Tabelle verschoben wird.
	 */
	public void columnMoved(TableColumnModelEvent arg0) {
		if(arg0.getFromIndex() != arg0.getToIndex()){
			userInputData.changeColumns(arg0.getFromIndex(), arg0.getToIndex(),getSelectedTab());
			autoSave();
		}
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte aus der Tabelle entfernt wird.
	 */
	public void columnRemoved(TableColumnModelEvent arg0) {
		// not used

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Andere Spalte in der Tabelle selektiert wird.
	 */
	public void columnSelectionChanged(ListSelectionEvent arg0) {
		// not used

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener zum ViewPort hinzugef�gt wird.
	 */
	public void componentAdded(ContainerEvent e) {
		addKeyAndContainerListenerToAll(e.getChild());

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener unsichtbar gemacht wird.
	 */
	public void componentHidden(ComponentEvent e) {
		// not used

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener verschoben wird.
	 */
	public void componentMoved(ComponentEvent e) {
		// not used

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener von dem ViewPane entfernt wird.
	 */
	public void componentRemoved(ContainerEvent e) {
		removeKeyAndContainerListenerToAll(e.getChild());

	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener in der Gr��e ver�ndert wird.
	 */
	public void componentResized(ComponentEvent e) {
		if(e.getSource()==getFrame()){
			frameResizeEvent();
		}
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener sichtbar gemacht wird.
	 */
	public void componentShown(ComponentEvent e) {
		// not used

	}
	/**
	 * Hilfsfunktion welche alle Multicasts aus dem jeweiligen Programmteil l�scht
	 * @param typ
	 */
	public void deleteAllMulticasts(Typ typ){
		pressBTSelectAll(typ);
		pressBTDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn ein bestimmter Multicast gel�scht werden soll.
	 * @param mcd MulticastData Objekt des Multicasts welcher gel�scht werden soll.
	 */
	public void deleteMC(MulticastData mcd){
		mc.deleteMC(mcd);
		updateTable(mcd.getTyp(),UpdateTyp.DELETE);
	}


	/**
	 * Funktion welche aufgerufen wird wenn der Input des Group IP Adress Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Group IP Adress Feld ge�ndert wurde.
	 */
	private void docEventTFgrp(final Typ typ){
		String groupAddress=getPanConfig(typ).getTf_groupIPaddress().getText();
		if(Typ.isV4(typ)){ //Wenn aus dem IGMP Panel
			//Wenn gültige IPv6 Adresse eingegeben wird
			if((InputValidator.checkMC_IPv6(groupAddress)!= null)	|| (getSelectedRows(typ).length > 1 && groupAddress.equals("..."))){ //$NON-NLS-1$
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.TRUE));
				if(typ==Typ.SENDER_V4){
					getPanConfig(Typ.SENDER_V4).switchTo(Typ.SENDER_V6);
					input[0][0]=true;
				}
				else{
					getPanConfig(Typ.RECEIVER_V4).switchTo(Typ.RECEIVER_V6);
					input[1][0]=true;
				}
			}
			//Wenn gültige IPv4 Adresse eingegeben wird
			else if((InputValidator.checkMC_IPv4(groupAddress)!= null)	|| (getSelectedRows(typ).length > 1 && groupAddress.equals("..."))){ //$NON-NLS-1$
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.TRUE));
				if(typ==Typ.SENDER_V4){
					getPanConfig(Typ.SENDER_V4).switchTo(Typ.SENDER_V4);
					input[0][0]=true;
				}
				else{
					getPanConfig(Typ.RECEIVER_V4).switchTo(Typ.RECEIVER_V4);
					input[1][0]=true;
				}
			}
			else if(groupAddress.equalsIgnoreCase("")){ //$NON-NLS-1$
				getPanConfig(typ).getPan_groupIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.NEUTRAL));
			}
			else{
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.FALSE));
				if(typ==Typ.SENDER_V4){
					input[0][0]=false;
				}
				else{
					input[1][0]=false;
				}
			}
		}
		else if(Typ.isMMRP(typ)){
			if(InputValidator.checkMC_MMRP(groupAddress)){
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPGROUP, BorderType.TRUE));
				if(Typ.isSender(typ)){
					input[2][0]=true;
				} else {
					input[3][0]=true;
				}
			}else if(groupAddress.equalsIgnoreCase("")){ //$NON-NLS-1$
				getPanConfig(typ).getPan_groupIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.MMRPGROUP, BorderType.NEUTRAL));
			}else {
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPGROUP, BorderType.FALSE));
				if(Typ.isSender(typ)){
					input[2][0]=false;
				} else {
					input[3][0]=false;
				}
			}
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Length Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Packet Length Feld ge�ndert wurde.
	 */
	private void docEventTFlength(Typ typ){
		switch(typ){
		case SENDER_V4:
			if((InputValidator.checkIPv4PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText())> 0)
					|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_udp_packetlength().getText().equals("..."))){ //$NON-NLS-1$
				getPanConfig(typ).getPan_packetlength()
				.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
				input[0][5]=true;
			}
			else{
				getPanConfig(typ).getPan_packetlength()
				.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
				input[0][5]=false;
			}
			break;
		case SENDER_MMRP:
			if((InputValidator.checkMmrpPacketLength(getPanConfig(typ).getTf_udp_packetlength().getText())> 0)
					|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_udp_packetlength().getText().equals("..."))){ //$NON-NLS-1$
				getPanConfig(typ).getPan_packetlength()
				.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
				input[2][5]=true;
			}
			else{
				getPanConfig(typ).getPan_packetlength()
				.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
				input[2][5]=false;
			}
		}
		if(getPanConfig(typ).getTf_udp_packetlength().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
			getPanConfig(typ).getPan_packetlength()
			.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Port Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Port Feld ge�ndert wurde.
	 */
	private void docEventTFport(Typ typ){
		if((InputValidator.checkPort(getPanConfig(typ).getTf_udp_port().getText()) > 0)
				|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_udp_port().getText().equals("..."))){ //$NON-NLS-1$
			getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
			if(typ==Typ.SENDER_V4){
				input[0][2]=true;
			}
			else if(typ == Typ.RECEIVER_V4){
				input[1][2]=true;
			}
		}
		else{
			getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.FALSE));
			if(typ==Typ.SENDER_V4){
				input[0][2]=false;
			}
			else if(typ == Typ.RECEIVER_V4){
				input[1][2]=false;
			}
		}
		if(getPanConfig(typ).getTf_udp_port().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
			getPanConfig(typ).getPan_udp_port()
			.setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Rate Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Packet Rate Feld ge�ndert wurde.
	 */
	private void docEventTFrate(Typ typ){
		if((InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText())> 0)
				|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_packetrate().getText().equals("..."))){ //$NON-NLS-1$
			getPanConfig(typ).getPan_packetrate()
			.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
			if(typ == Typ.SENDER_V4){
				input[0][4]=true;
			}
			else if(typ == Typ.SENDER_MMRP){
				input[2][4]=true;
			}
		}
		else{
			getPanConfig(typ).getPan_packetrate()
			.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.FALSE));
			if(typ == Typ.SENDER_V4){
				input[0][4]=false;
			}
			else if(typ == Typ.SENDER_MMRP){
				input[2][4]=false;
			}
		}
		if(getPanConfig(typ).getTf_packetrate().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
			getPanConfig(typ).getPan_packetrate()
			.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des TTL Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das TTL Feld ge�ndert wurde.
	 */
	private void docEventTFttl(Typ typ){
		if((InputValidator.checkTimeToLive(getPanConfig(typ).getTf_ttl().getText())> 0)
				|| 	(getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_ttl().getText().equals("..."))){ //$NON-NLS-1$
			getPanConfig(typ).getPan_ttl()
			.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.TRUE));
			if(typ == Typ.SENDER_V4){
				input[0][3]=true;
			}
			else if(typ == Typ.SENDER_V6){
				input[2][3]=true;
			}
		}
		else{
			getPanConfig(typ).getPan_ttl()
			.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.FALSE));
			if(typ == Typ.SENDER_V4){
				input[0][3]=false;
			}
			else if(typ == Typ.SENDER_V6){
				input[2][3]=false;
			}
		}
		if(getPanConfig(typ).getTf_ttl().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
			getPanConfig(typ).getPan_ttl()
			.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn die Fenstergr��e ge�ndert wurde.
	 * Passt die Komponenten der GUI auf die neue Gr��e an.
	 */
	private void frameResizeEvent() {
		if(getSelectedTab() != Typ.UNDEFINED){
			if(getFrame().getSize().width > 1000){
				getTable(getSelectedTab()).setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			}
			else{
				getTable(getSelectedTab()).setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
		}
		if(getSelectedTab() == Typ.SENDER_V4 || getSelectedTab() == Typ.SENDER_V6){
			if(getPanTabbed(getSelectedTab()).getPan_graph().isVisible() && getPanTabbed(getSelectedTab()).getTab_console().isVisible()){
				getPanTabbed(getSelectedTab()).getPan_graph().resize(new Dimension(getFrame().getSize().width-262,100));
			}
		}
		else if(getSelectedTab() == Typ.RECEIVER_V4 || getSelectedTab() == Typ.RECEIVER_V6){
			if(getPanTabbed(getSelectedTab()).getPan_graph().isVisible() && getPanTabbed(getSelectedTab()).getTab_console().isVisible()){
				getPanTabbed(getSelectedTab()).getPan_recGraph().resize(new Dimension(getFrame().getSize().width-262,100));
			}
		}
	}
	/**
	 * Hilfsfunktion welche das Frame zur�ckgibt in welchem das MultiCastor Tool gezeichnet wird.
	 * @return FrameMain Objekt welches angefordert wurde.
	 */
	public FrameMain getFrame() {
		return f;
	}
	/**
	 * Hilfsfunktion welche die aktuelle Anzahl an Multicasts vom MulticastController anfordert.
	 * @param typ Programmteil in welchem die Multicasts gez�hlt werden sollen.
	 * @return Z�hler der angibt wievielel Multicasts sich in einem Programmteil befinden.
	 */
	public int getMCCount(Typ typ){
		return mc.getMCs(typ).size();
	}
	/**
	 * Hilfsfunktion welche Multicast Daten vom MulticastController anfordert.
	 * @param i Index des angeforderten Multicasts (Index in der Tabelle).
	 * @param typ Programmteil in zu welchem der Multicast geh�rt.
	 * @return MulticastData Objekt welches vom MulticastController zur�ckgegeben wird.
	 */
	public MulticastData getMCData(int i, MulticastData.Typ typ){
		return mc.getMC(i, typ);
	}


	/**
	 * Hilfsfunktion welche das Configuration Panel eines bestimmten Programmteils zur�ckgibt.
	 * Editiert für Version 2
	 * @param typ Programmteil in welchem sich das Configuration Panel befindet.
	 * @return PnaleMulticastConfig, welches angefordert wurde.
	 */
	private PanelMulticastConfig getPanConfig(Typ typ){
		return f.getPan_config(typ);
	}

	private PanelTabpaneConfiguration getConfigTabPane(Typ typ){
		return f.getConfigTabPane(typ);
	}
	/**
	 * Hilfsfunktion welche das Control Panel eines bestimmten Programmteils zur�ckgibt.
	 * @param typ Programmteil in welchem sich das Control Panel befindet.
	 * @return PanelMulticastControl, welches angefordert wurde.
	 */
	private PanelMulticastControl getPanControl(Typ typ){
		PanelMulticastControl controlpart = null;
		switch(typ){
		case SENDER_V6: ;
		case SENDER_MMRP:;
		case SENDER_V4: controlpart=f.getPanel_sender().getPan_control(); break;
		case RECEIVER_V6: ;
		case RECEIVER_MMRP: ;
		case RECEIVER_V4: controlpart=f.getPanel_receiver().getPan_control(); break;
		}
		return controlpart;
	}
	/**
	 * Hilfsfunktion welche die Statusbar des jeweiligen Programmteils zur�ck gibt.
	 * @param typ Programmteil in welchem sich die Statusbar befindet.
	 * @return PanelStatusbar welche angefordert wurde.
	 */
	private PanelStatusBar getPanStatus(Typ typ){
		PanelStatusBar statusbarpart = null;
		switch(typ){
		case SENDER_V6: ;
		case SENDER_MMRP:;
		case SENDER_V4: statusbarpart=f.getPanel_sender().getPan_status(); break;
		case RECEIVER_V6: ;
		case RECEIVER_MMRP: ;
		case RECEIVER_V4: statusbarpart=f.getPanel_receiver().getPan_status(); break;
		}
		return statusbarpart;
	}
	/**
	 * Todo
	 * Hilfsfunktion welche einen bestimten Programmteil zur�ck gibt.
	 * @param typ Programmteil welcher angeforder wird.
	 * @return JPanel mit dem angeforderten Programmteil.
	 */
	public PanelTabbed getPanTabbed(Typ typ){
		PanelTabbed ret = null;
		switch(typ){
		case SENDER_V4: ;
		case SENDER_V6: ;
		case SENDER_MMRP: ret=f.getPanel_sender(); break;
		case RECEIVER_V4: ;
		case RECEIVER_V6: ;
		case RECEIVER_MMRP: ret=f.getPanel_receiver(); break;
		}
		return ret;
	}
	/**
	 * Hilfsfunktion welche ein Integer Array mit den Selektierten Zeilen einer Tabele zur�ckgibt.
	 * @param typ Programmteil in welchem sich die Tabelle befindet.
	 * @return Integer Array mit selektierten Zeilen. (leer wenn keine Zeile selektiert ist).
	 */
	public int[] getSelectedRows(Typ typ){
		int[] ret=null;
		ret = getTable(typ).getSelectedRows();
		return ret;
	}
	/**
	 * Hilfsfunktion welche den Programmteil zur�ckgibt welcher im Moment per Tab selektiert ist.
	 * @return Programmteil welcher im Vordergrund ist.
	 */
	public Typ getSelectedTab() {
		Typ typ;
		switch(f.getTabpane().getSelectedIndex()){
		case 1: typ = Typ.RECEIVER_V4; break;
		case 0: typ = Typ.SENDER_V4; break;
		default: typ = Typ.UNDEFINED; break;
		}
		return typ;
	}
	/**
	 * Hilfsfunktion welche die Tabelle des jeweiligen Programmteil zur�ckgibt.
	 * @param typ Programmteil aus welchem die Tabelle angeforder wird.
	 * @return Die JTable welche angefordert wurde.
	 */
	public JTable getTable(Typ typ){
		JTable tablepart = null;
		switch(typ){
		case SENDER_V6: ;
		case SENDER_MMRP:;
		case SENDER_V4: tablepart=f.getPanel_sender().getTable(); break;
		case RECEIVER_V6: ;
		case RECEIVER_MMRP: ;
		case RECEIVER_V4: tablepart=f.getPanel_receiver().getTable(); break;
		}
		return tablepart;
	}
	/**
	 * Hilfsfunktion welches das Model der jeweiligen Tabelle zur�ckgibt.
	 * @param typ Programmteil von welchem das Tabellenmodel angeforder wird.
	 * @return das Tabellenmodel des spezifizierten Programmteils.
	 */
	public MiscTableModel getTableModel(Typ typ){
		return ((MiscTableModel) getTable(typ).getModel());
	}
	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom Multicast Tool empfangen wird (IPv4 & IPv6).
	 * @return Gibt den Insgesamten Traffic des IPv4SENDER und IPv6SENDER als String zur�ck (Mbit/s) im Format "##0.000"
	 */
	public String getTotalTrafficDown(){
		DecimalFormat ret = new DecimalFormat("##0.000"); //$NON-NLS-1$
		double sum = 0.0;
		for(int i = 0; i < getTable(Typ.RECEIVER_V4).getModel().getRowCount(); i++){
			sum = sum + Double.parseDouble(((String) getTable(Typ.RECEIVER_V4).getModel().getValueAt(i, 6)).replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ret.format(sum);
	}
	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom Multicast Tool verschickt wird (IPv4 & IPv6).
	 */
	public String getTotalTrafficUP(){
		DecimalFormat ret = new DecimalFormat("##0.000"); //$NON-NLS-1$
		double sum = 0.0;
		for(int i = 0; i < getTable(Typ.SENDER_V4).getModel().getRowCount(); i++){
			sum = sum + Double.parseDouble(((String) getTable(Typ.SENDER_V4).getModel().getValueAt(i, 6)).replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ret.format(sum);
	}
	/**
	 * Funktion welche aufgerufen wird wenn Hide im PopupMenu des Tabellenkopfs gedr�ckt wurde
	 */
	private void hideColumnClicked() {
		userInputData.hideColumn(PopUpMenu.getSelectedColumn(),getSelectedTab());
	}
	/**
	 * Funktion welche die GUI startet und initialisiert
	 * @param p_mc Referenz zum MultiCast Controller, die wichtigste Schnittstelle der GUI.
	 */
	public void initialize(MulticastController p_mc , UserInputData UID){
		mc = p_mc;
		userInputData = UID;
		f = new FrameMain(this);
		setDefaultValues(Typ.RECEIVER_V4);
		setDefaultValues(Typ.RECEIVER_MMRP);
		setDefaultValues(Typ.SENDER_V4);
		setDefaultValues(Typ.SENDER_MMRP);
		f.setVisible(true);
	}

	private void setDefaultValues(Typ typ){
		switch (typ) {
		case SENDER_MMRP:
			getPanConfig(Typ.SENDER_MMRP).getTf_groupIPaddress().setText(userInputData.getDefaultL2GroupAddress());
			getPanConfig(Typ.SENDER_MMRP).getCb_sourceIPaddress().setSelectedIndex(NetworkAdapter.findAddressIndex(typ, userInputData.getDefaultL2InterfaceAddress())+1);
			getPanConfig(Typ.SENDER_MMRP).getTf_packetrate().setText(""+userInputData.getDefaultL2PacketRate()); //$NON-NLS-1$
			getPanConfig(Typ.SENDER_MMRP).getTf_udp_packetlength().setText(""+userInputData.getDefaultL2PacketLength()); //$NON-NLS-1$
			break;
		case SENDER_V4:
		case SENDER_V6:
			getPanConfig(Typ.SENDER_V4).getTf_groupIPaddress().setText(userInputData.getDefaultL3GroupAddress());
			getPanConfig(Typ.SENDER_V4).getCb_sourceIPaddress().setSelectedIndex(NetworkAdapter.findAddressIndex(typ, userInputData.getDefaultL3InterfaceAddress())+1);
			getPanConfig(Typ.SENDER_V4).getTf_udp_port().setText(userInputData.getDefaultL3UdpPort()+""); //$NON-NLS-1$
			getPanConfig(Typ.SENDER_V4).getTf_ttl().setText(userInputData.getDefaultL3TTL()+""); //$NON-NLS-1$
			getPanConfig(Typ.SENDER_V4).getTf_packetrate().setText(""+userInputData.getDefaultL3PacketRate()); //$NON-NLS-1$
			getPanConfig(Typ.SENDER_V4).getTf_udp_packetlength().setText(""+userInputData.getDefaultL3PacketLength()); //$NON-NLS-1$
			break;
		case RECEIVER_MMRP:
			getPanConfig(Typ.RECEIVER_MMRP).getTf_groupIPaddress().setText(userInputData.getDefaultL2GroupAddress());
			getPanConfig(Typ.RECEIVER_MMRP).getCb_sourceIPaddress().setSelectedIndex(NetworkAdapter.findAddressIndex(typ, userInputData.getDefaultL2InterfaceAddress())+1);
			break;
		case RECEIVER_V4:
		case RECEIVER_V6:
			getPanConfig(Typ.RECEIVER_V4).getTf_groupIPaddress().setText(userInputData.getDefaultL3GroupAddress());
			getPanConfig(Typ.RECEIVER_V4).getCb_sourceIPaddress().setSelectedIndex(NetworkAdapter.findAddressIndex(typ, userInputData.getDefaultL3InterfaceAddress())+1);
			getPanConfig(Typ.RECEIVER_V4).getTf_udp_port().setText(userInputData.getDefaultL3UdpPort()+""); //$NON-NLS-1$

			break;
		default:
			break;
		}
	}


	@Override
	/**
	 * Funktion welche aufgerufen wird wenn Inhalt in ein Feld des Configuration Panel eingetrgen wird.
	 */
	public void insertUpdate(DocumentEvent source) {
		//KEY Event in IPv4 Sender - GroupAddress
		if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_groupIPaddress().getDocument()){
			docEventTFgrp(Typ.SENDER_V4);
		}
		//KEY Event in IPv4 Receiver - GroupAddress
		else if(source.getDocument() == getPanConfig(Typ.RECEIVER_V4).getTf_groupIPaddress().getDocument()){
			docEventTFgrp(Typ.RECEIVER_V4);
		}
		//KEY Event in IPv4 Sender - GroupAddress
		if(source.getDocument() == getPanConfig(Typ.SENDER_MMRP).getTf_groupIPaddress().getDocument()){
			docEventTFgrp(Typ.SENDER_MMRP);
		}
		//KEY Event in IPv4 Receiver - GroupAddress
		else if(source.getDocument() == getPanConfig(Typ.RECEIVER_MMRP).getTf_groupIPaddress().getDocument()){
			docEventTFgrp(Typ.RECEIVER_MMRP);
		}
		//KEY Event in IPv4 Sender - UDP Port
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_udp_port().getDocument()){
			docEventTFport(Typ.SENDER_V4);
		}
		//KEY Event in IPv4 Receiver - UDP Port
		else if(source.getDocument() == getPanConfig(Typ.RECEIVER_V4).getTf_udp_port().getDocument()){
			docEventTFport(Typ.RECEIVER_V4);
		}
		//KEY Event in IPv4 Sender - TTL
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_ttl().getDocument()){
			docEventTFttl(Typ.SENDER_V4);
		}
		//KEY Event in IPv4 Sender - PacketRate
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_packetrate().getDocument()){
			docEventTFrate(Typ.SENDER_V4);
		}
		//KEY Event in IPv4 Sender - PacketLength
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_udp_packetlength().getDocument()){
			docEventTFlength(Typ.SENDER_V4);
		}
		//KEY Event in IPv4 Sender - PacketRate
		else if(source.getDocument() == getPanConfig(Typ.SENDER_MMRP).getTf_packetrate().getDocument()){
			docEventTFrate(Typ.SENDER_MMRP);
		}
		//KEY Event in IPv4 Sender - PacketLength
		else if(source.getDocument() == getPanConfig(Typ.SENDER_MMRP).getTf_udp_packetlength().getDocument()){
			docEventTFlength(Typ.SENDER_MMRP);
		}
		autoSave();
	}
	@SuppressWarnings("static-access")
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine GUI Komponente mit dem ItemListener selektiert oder deselektiert wird.
	 * Dieser Listener wird f�r RadioButtons und Checkboxen verwendet.
	 */
	public void itemStateChanged(ItemEvent arg0) {
		if(arg0.getStateChange() == arg0.SELECTED){
			if(arg0.getSource() == getPanConfig(Typ.SENDER_V4).getTf_sourceAddress()){
				changeNetworkInterface(Typ.SENDER_V4);
			}
			else if(arg0.getSource() == getPanConfig(Typ.RECEIVER_V4).getTf_sourceAddress()){
				changeNetworkInterface(Typ.RECEIVER_V4);
			}
			else if(arg0.getSource() == getPanConfig(Typ.SENDER_MMRP).getTf_sourceAddress()){
				changeNetworkInterface(Typ.SENDER_MMRP);
			}
			else if(arg0.getSource() == getPanConfig(Typ.RECEIVER_MMRP).getTf_sourceAddress()){
				changeNetworkInterface(Typ.RECEIVER_MMRP);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().getLostPktsRB()){
				getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().selectionChanged(valueType.LOSTPKT);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().getJitterRB()){
				getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().selectionChanged(valueType.JITTER);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().getMeasPktRtRB()){
				getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().selectionChanged(valueType.MEASPKT);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().getLostPktsRB()){
				getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().selectionChanged(valueType.LOSTPKT);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().getJitterRB()){
				getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().selectionChanged(valueType.JITTER);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().getMeasPktRtRB()){
				getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().selectionChanged(valueType.MEASPKT);
			}
		}
		else{
			if(arg0.getSource() == f.getMi_autoSave()){
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {
					submitInputData();
				}
			}
		}
		if(arg0.getSource() == f.getMi_autoSave()){
			f.setAutoSave(f.isAutoSaveEnabled());
		}
		autoSave();
	}

	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur gedr�ckt wird.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		//not used
	}
	@Override
	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur losgelassen wird.
	 */
	public void keyReleased(KeyEvent arg0) {
		// not used

	}
	@Override
	/**
	 * Funktion welche Aufgerufen wird sobald die Tastatur einen Input bei gedr�ckter Taste an das System weitergibt.
	 */
	public void keyTyped(KeyEvent arg0) {

	}
	/**
	 * Funktion welche ausgel�st wird wenn der User eine oder mehrere Zeilen in der Tabelle selektiert.
	 * @param typ Programmteil in welchem der User die Zeilen selektiert hat.
	 */
	private void listSelectionEventFired(Typ typ) {

		PanelTabbed tabpart = null;
		switch(typ){
		case SENDER_V4: tabpart=f.getPanel_sender(); break;
		case RECEIVER_V4: tabpart=f.getPanel_receiver(); break;
		}
		int[] selectedRows = tabpart.getTable().getSelectedRows();
		tabpart.getPan_status().getLb_multicasts_selected().setText(selectedRows.length+Messages.getString("ViewController.3")); //$NON-NLS-1$
		if(selectedRows.length > 1){
			multipleSelect(typ);
		}
		if(selectedRows.length == 1){
			MulticastData mcData=getMCData(selectedRows[0],typ);
			typ = mcData.getTyp();
			tabpart.getPan_config(typ).getBt_change().setText(Messages.getString("ViewController.1")); //$NON-NLS-1$

			tabpart.getPan_config(typ).getTf_groupIPaddress().setText(mcData.getGroupAddress());
			tabpart.getPan_config(typ).getTf_sourceAddress().setSelectedIndex(NetworkAdapter.findAddressIndex(typ, mcData.getSourceAddress())+1);
			if(Typ.isIP(typ)){
				tabpart.getPan_TabsConfig().setSelectedIndex(1);
				tabpart.getPan_config(typ).getTf_udp_port().setEnabled(true);
				tabpart.getPan_config(typ).getTf_udp_port().setText(""+mcData.getUdpPort()); //$NON-NLS-1$
			} else{
				tabpart.getPan_TabsConfig().setSelectedIndex(0);
			}
			if(Typ.isSender(typ)){
				getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
				getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0); //$NON-NLS-1$
				tabpart.getPan_config(typ).getTf_sourceAddress().setEnabled(true);
				tabpart.getPan_config(typ).getTf_packetrate().setText(""+mcData.getPacketRateDesired());; //$NON-NLS-1$
				tabpart.getPan_config(typ).getTf_udp_packetlength().setText(""+mcData.getPacketLength());; //$NON-NLS-1$
				if(typ!=Typ.SENDER_MMRP)
				{
					tabpart.getPan_config(typ).getTf_ttl().setText(""+mcData.getTtl()); //$NON-NLS-1$
				}
			}
			setBTStartStopDelete(typ);
		}
		else if(selectedRows.length == 0){
			tabpart.getPan_config(typ).getBt_change().setText(Messages.getString("ViewController.1")); //$NON-NLS-1$
			tabpart.getPan_config(typ).getTf_groupIPaddress().setEnabled(true);
			if(Typ.isSender(typ)){
				tabpart.getPan_config(typ).getTf_sourceAddress().setEnabled(true);
			}
			if(typ!=Typ.SENDER_MMRP){
				tabpart.getPan_config(typ).getTf_udp_port().setEnabled(true);
			}
		}
	}
	/**
	 * Funktion welche ausgel�st wird, wenn der User versucht eine Konfigurationsdatei mit dem "Datei Laden"
	 * Dialog zu laden.
	 */
	private void loadFileEvent(ActionEvent e) {
		if(e.getActionCommand().equals("ApproveSelection")){ //$NON-NLS-1$
			FrameFileChooser fc_load = getFrame().getFc_load();
			loadConfig(	fc_load.getSelectedFile(), fc_load);

			fc_load.getChooser().rescanCurrentDirectory();
			//fc_load.toggle();
		}
		else if(e.getActionCommand().equals("CancelSelection")){ //$NON-NLS-1$
			getFrame().getFc_load().toggle();
		}
	}
	/**
	 * Funktion welche ausgel�st wird, wenn der User versucht eine Profildatei mit dem "Datei Laden"
	 * Dialog zu laden.
	 */
	private void loadProfileEvent(ActionEvent e) {
		if(e.getActionCommand().equals("ApproveSelection")){ //$NON-NLS-1$
			FrameFileChooser fc_load = getFrame().getFc_load_profile();
			loadProfile(fc_load.getSelectedFile(), fc_load);

			fc_load.getChooser().rescanCurrentDirectory();
			//fc_load.toggle();
		}
		else if(e.getActionCommand().equals("CancelSelection")){ //$NON-NLS-1$
			getFrame().getFc_load_profile().toggle();
		}

	}

	private void loadConfig(String s, FrameFileChooser FFC) {
		mc.loadConfigFile(s, FFC);
		if(f.getTabpane().getSelectedIndex()==0)
			f.setTitle("Sender - "+userInputData.getFilename()+" - Multicastor"); //$NON-NLS-1$
		else
			f.setTitle("Receiver - "+userInputData.getFilename()+" - Multicastor"); //$NON-NLS-1$
		
		
	}

	/**
	 * Funktion, die Profildatei liest und Multicaststream in die Oberfl�che einf�gt
	 * @param s Der Pfad zur Profildatei
	 */
	private void loadProfile(String s, FrameFileChooser FFC) {
		mc.loadProfile(s, FFC);
	}


	@Override
	/**
	 * MouseEvent welches ausgel�st wird wenn eine Maustaste gedr�ckt und wieder losgelassen wird.
	 */
	public void mouseClicked(MouseEvent e) {
		if(e.getSource()==getTable(getSelectedTab()).getTableHeader()){
			if(e.getButton() == MouseEvent.BUTTON3){
				if(getPanTabbed(getSelectedTab()).isPopupsAllowed()){
				PopUpMenu.createTableHeaderPopup(getTable(getSelectedTab()), this, e);}
			}else{
				Point p = e.getPoint();
				int col = getTable(getSelectedTab()).columnAtPoint(p);
				String name = getTable(getSelectedTab()).getTableHeader().getColumnModel().getColumn(col).getHeaderValue().toString();
				getTableModel(getSelectedTab()).sort(mc.getMCs(getSelectedTab()),col, name);
				//System.out.print("DEBUG: col:" + col + " NAME: "+ name+ " Header: ");
				//System.out.println(getTable(getSelectedTab()).getColumnModel().getColumn(col).getHeaderValue());
			}

			autoSave();			
		}

	}
	@Override
	/**
	 * MouseEvent welches ausgel�st wird wenn die Maus bei gedr�ckter Taste bewegt wird.
	 */
	public void mouseDragged(MouseEvent arg0) {
		// not used

	}
	@Override
	/**
	 * MouseEvent welches auf das Betreten einer Komponente der Maus reagiert.
	 */
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	/**
	 * MouseEvent welches auf das Verlassen einer Komponente der Maus reagiert.
	 */
	public void mouseExited(MouseEvent e) {
		// not used

	}
	@Override
	/**
	 * MouseEvent welches auf Dr�cken einer Maustaste reagiert.
	 */
	public void mousePressed(MouseEvent e) {


	}
	@Override
	/**
	 * MouseEvent welches auf Loslassen einer Maustaste reagiert.
	 */
	public void mouseReleased(MouseEvent e) {
		// not used

	}

	public UserInputData getUserInputData() {
		return this.userInputData;
	}
	/**
	 * Funktion welche aufgerufen wird wenn mehr als ein Multicast in der Tabelle selektiert wurde.
	 * Passt die GUI entsprechend an.
	 * @param typ Programmteil in welchem mehrere Multicasts Selektiert wurden.
	 */

	private void multipleSelect(Typ typ) {
		checkInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Add Button gedr�ckt wurde
	 */
	private void pressBTAdd(Typ typ) {
		getTable(typ).clearSelection();
		if(typ==Typ.SENDER_V4){
			this.addMC(changeMCData(new MulticastData(), getPanConfig(Typ.SENDER_V4).getTyp()));
		}
		if(typ==Typ.RECEIVER_V4){
			this.addMC(changeMCData(new MulticastData(), getPanConfig(Typ.RECEIVER_V4).getTyp()));
		}
		if(typ==Typ.SENDER_MMRP){
			this.addMC(changeMCData(new MulticastData(), getPanConfig(Typ.SENDER_MMRP).getTyp()));
		}
		if(typ==Typ.RECEIVER_MMRP){
			this.addMC(changeMCData(new MulticastData(), getPanConfig(Typ.RECEIVER_MMRP).getTyp()));
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Change Button gedr�ckt wird. Bei selektierten Multicast(s).
	 * @param typ Programmteil in welchem der Change Button gedr�ckt wurde
	 */
	private void pressBTChange(Typ typ) {
		int[] selectedList = getSelectedRows(typ);
		if(selectedList.length == 1){
			MulticastData mcd = getMCData(selectedList[0],typ);
			if(Typ.isMMRP(typ)){
				this.changeMC(changeMCData(mcd, typ));
			}else if(typ==Typ.SENDER_V4){
				this.changeMC(changeMCData(mcd, getPanConfig(Typ.SENDER_V4).getTyp()));
			}else{
				this.changeMC(changeMCData(mcd, getPanConfig(Typ.RECEIVER_V4).getTyp()));
			}

			getTable(typ).getSelectionModel().setSelectionInterval(0, 0);
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Delete Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Delete Button gedr�ckt wurde
	 */
	private void pressBTDelete(Typ typ) {
		int[] selectedRows =getTable(typ).getSelectedRows();
		for(int i=0 ; i<selectedRows.length ; i++){
			deleteMC(getMCData(selectedRows[i]-i, typ));
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der (De)select All Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der (De)select All Button gedr�ckt wurde
	 */
	private void pressBTSelectDeselectAll(Typ typ) {
		if(getTable(typ).getSelectedRows().length == getTable(typ).getRowCount()){
			clearSelection(typ);
		}else {
			selectAll(typ);
		}
	}
	/**
	 * Deselektiert alle Einträge in der Tabelle
	 * @param typ
	 */
	private void clearSelection(Typ typ){
		getTable(typ).clearSelection();
		setBTStartStopDelete(typ);
		if(Typ.isSender(typ)){
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0); //$NON-NLS-1$
			getPanConfig(typ).getTf_sourceAddress().setSelectedIndex(0);
		}
		getPanStatus(typ).getLb_multicasts_selected().setText(Messages.getString("ViewController.5")); //$NON-NLS-1$
	}
	/**
	 * Deselektiert alle Einträge in der Tabelle
	 * @param typ
	 */
	private void selectAll(Typ typ){
		getTable(typ).selectAll();
		if(getSelectedRows(typ).length > 1){
			multipleSelect(typ);
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der New Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der New Button gedr�ckt wurde
	 */
	private void pressBTNewMC(Typ typ) {
		if(getPanTabbed(typ).getPan_TabsConfig().getSelectedIndex()==0){
			if(typ==Typ.SENDER_V4){
				setDefaultValues(Typ.SENDER_MMRP);
			}else{
				setDefaultValues(Typ.RECEIVER_MMRP);
			}
		} else {
			setDefaultValues(typ);
		}
		getTable(typ).clearSelection();
		setBTStartStopDelete(typ);
		getPanStatus(typ).getLb_multicasts_selected().setText(Messages.getString("ViewController.5")); //$NON-NLS-1$
	}

	public void pressBTReceiver() {
		f.getTabpane().setSelectedIndex(1);
		f.setTitle(Messages.getString("ViewController.13") + " - " +userInputData.getFilename()+" - Multicastor");
		frameResizeEvent();
		
	}
	private void pressBTSender() {
		f.getTabpane().setSelectedIndex(0);
		f.setTitle(Messages.getString("ViewController.12") + " - "+userInputData.getFilename()+" - Multicastor");
		frameResizeEvent();
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Select All Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Select All Button gedr�ckt wurde
	 */
	private void pressBTSelectAll(Typ typ) {
		selectAll(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Start Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Start Button gedr�ckt wurde
	 */
	private void pressBTStart(Typ typ){
		int[] selectedLine = getSelectedRows(typ);
		for(int i = 0 ; i < selectedLine.length ; i++){
			if(!getMCData(selectedLine[i], typ).isActive()){
				startMC(selectedLine[i], typ);
				updateTable(typ, UpdateTyp.UPDATE);
			}
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Stop Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Stop Button gedr�ckt wurde
	 */
	private void pressBTStop(Typ typ){
		int[] selectedLine = getSelectedRows(typ);
		for(int i = 0 ; i < selectedLine.length ; i++){
			if(getMCData(selectedLine[i], typ).isActive()){
				stopMC(selectedLine[i], typ);
				updateTable(typ, UpdateTyp.UPDATE);
			}
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche es dem Multicast Controller und somit den restlichen Programmteilen erm�glicht
	 * Ausgaben in der Konsole des GUI zu t�tigen.
	 * @param s Nachricht welche in der Konsole der GUI ausgegeben werden soll
	 */
	public void printConsole(String s){
		getFrame().getPanelPart(Typ.SENDER_V4).getTa_console().append(s+"\n"); //$NON-NLS-1$
		getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().append(s+"\n"); //$NON-NLS-1$
		getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().getText().length());
	}

	/**
	 * Removed einen Container und einen Keylistener vom
	 * Component c. Hat die Komponente Kinder, mache das
	 * gleiche mit jedem Child
	 * @param c Container, von dem die Listener entfernt werden sollen
	 */
	private void removeKeyAndContainerListenerToAll(Component c)
	{
		c.removeKeyListener(this);
		if(c instanceof Container) {
			Container cont = (Container)c;
			cont.removeContainerListener(this);
			Component[] children = cont.getComponents();
			for(int i = 0; i < children.length; i++){
				removeKeyAndContainerListenerToAll(children[i]);
			}
		}
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn ein Zeichen aus einem Textfeld gel�scht wird.
	 */
	public void removeUpdate(DocumentEvent e) {
		insertUpdate(e);
	}
	/**
	 * Funktion welche aufgerufen wird wenn versucht wird eine Datei zu speichern im Datei speichern Dialog.
	 * @param e ActionEvent welches vom Datei speichern Dialog erzeugt wird
	 */
	private void saveFileEvent(ActionEvent e) {
		if(e.getActionCommand().equals("ApproveSelection")){ //$NON-NLS-1$
			FrameFileChooser fc_save = getFrame().getFc_save();
			mc.saveConfig(	fc_save.getSelectedFile());
			fc_save.getChooser().rescanCurrentDirectory();
			fc_save.toggle();
		}
		else if(e.getActionCommand().equals("CancelSelection")){ //$NON-NLS-1$
			getFrame().getFc_save().toggle();
		}
	}

	/**
	 * Funtion, die aufgerufen wird, wenn versucht wird, ein Profil zu speichern
	 * @param e ActionEvent welches vom FileChooser Dialog erzeugt wird
	 */
	private void saveProfileEvent(ActionEvent e) {
		if(e.getActionCommand().equals("ApproveSelection")){ //$NON-NLS-1$
			FrameFileChooser fc_save = getFrame().getFc_save_profile();
			mc.saveProfile(fc_save.getSelectedFile(), mc.getMCs());
			fc_save.getChooser().rescanCurrentDirectory();
			fc_save.toggle();
		}
		else if(e.getActionCommand().equals("CancelSelection")){ //$NON-NLS-1$
			getFrame().getFc_save_profile().toggle();
		}

	}
	/**
	 * Funktion welche das Aussehen des Start Stop und Delete Buttons anpasst je nach dem welche Multicasts ausgew�hlt wurden.
	 * @param typ Programmteil in welchem die Buttons angepasst werden sollen.
	 */
	private void setBTStartStopDelete(Typ typ) {
		int[] selectedLine=getSelectedRows(typ);
		if(selectedLine.length == 1){
			getPanControl(typ).getDelete().setEnabled(true);
			if(getMCData(selectedLine[0],typ).isActive()){
				getPanControl(typ).getStart().setEnabled(false);
				getPanControl(typ).getStop().setEnabled(true);
			}
			else{
				getPanControl(typ).getStart().setEnabled(true);
				getPanControl(typ).getStop().setEnabled(false);
			}
		}
		else if(selectedLine.length == 0){
			getPanControl(typ).getStart().setEnabled(false);
			getPanControl(typ).getStop().setEnabled(false);
			getPanControl(typ).getDelete().setEnabled(false);
		}
		else{
			getPanControl(typ).getDelete().setEnabled(true);
			for(int i = 1 ; i < selectedLine.length ; i++){
				if(getMCData(selectedLine[i-1], typ).isActive() && getMCData(selectedLine[i], typ).isActive()){
					getPanControl(typ).getStart().setEnabled(false);
				}
				else{
					getPanControl(typ).getStart().setEnabled(true);
					break;
				}
			}
			for(int i = 1 ; i < selectedLine.length ; i++){
				if((!getMCData(selectedLine[i-1], typ).isActive()) && (!getMCData(selectedLine[i], typ).isActive())){
					getPanControl(typ).getStop().setEnabled(false);
				}
				else{
					getPanControl(typ).getStop().setEnabled(true);
					break;
				}
			}
		}
	}

	/**
	 * Funktion welche erm�glich Nachrichten in der GUI anzuzeigen. Gibt anderen Programmteilen �ber den
	 * MulticastController die M�glichkeit Informations, Warnungs und Errormeldungen auf dem GUI auszugeben.
	 * @param typ Art der Nachricht (INFO / WARNING / ERROR)
	 * @param message Die eigentliche Nachricht welche angezeigt werden soll
	 */
	public void showMessage(MessageTyp typ, String message){ //type 0 == info, type 1 == warning, type 2 == error
		switch(typ){
		case INFO: JOptionPane.showMessageDialog(null, message, Messages.getString("ViewController.8"), JOptionPane.INFORMATION_MESSAGE); break; //$NON-NLS-1$
		case WARNING: JOptionPane.showMessageDialog(null, message, Messages.getString("ViewController.9"), JOptionPane.WARNING_MESSAGE); break; //$NON-NLS-1$
		case ERROR: JOptionPane.showMessageDialog(null, message, Messages.getString("ViewController.10"), JOptionPane.ERROR_MESSAGE); break; //$NON-NLS-1$
		}
	}
	/**
	 * Bildet die Schnittstelle zum Multicast Controller zum starten von einem Bestimmten Multicast.
	 * Sorgt f�r die ensprechenden Updates in der GUI nach dem Versuch den Multicast zu stoppen.
	 * @param row Zeilenindex des Multicast welcher gestartet werden soll
	 * @param typ Programmteil in welchem sich der Multicast befindet welcher gestartet werden soll
	 */
	public void startMC(int row, Typ typ){
		graphUpdate=true;
		mc.startMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn das Frame in der Gr��e ge�ndert oder verschoben wird. Oder die Config Tabs gewechselt werden
	 */
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==getConfigTabPane(Typ.SENDER_V4)&&
				getSelectedRows(Typ.SENDER_V4).length==1 &&
				(Typ.isIP(mc.getMC(getSelectedRows(Typ.SENDER_V4)[0], Typ.SENDER_V4).getTyp())&&
						((PanelTabpaneConfiguration)e.getSource()).getSelectedIndex()==0 ||
						Typ.isMMRP(mc.getMC(getSelectedRows(Typ.SENDER_V4)[0], Typ.SENDER_V4).getTyp())&&
						((PanelTabpaneConfiguration)e.getSource()).getSelectedIndex()==1))
		{
			getTable(Typ.SENDER_V4).clearSelection();
			// Wenn eine Zeile ausgewählt(nicht MMRP) ist und das Configpanel zu MMRP gewechselt wird
			// oder wenn eine Zeile ausgewählt(MMRP) ist und das Configpanel zu IP gewechselt wird
		} else if(e.getSource()==getConfigTabPane(Typ.RECEIVER_V4)&&
				getSelectedRows(Typ.RECEIVER_V4).length==1 &&
				(Typ.isIP(mc.getMC(getSelectedRows(Typ.RECEIVER_V4)[0], Typ.RECEIVER_V4).getTyp())&&
						((PanelTabpaneConfiguration)e.getSource()).getSelectedIndex()==0 ||
						Typ.isMMRP(mc.getMC(getSelectedRows(Typ.RECEIVER_V4)[0], Typ.RECEIVER_V4).getTyp())&&
						((PanelTabpaneConfiguration)e.getSource()).getSelectedIndex()==1))
		{
			getTable(Typ.RECEIVER_V4).clearSelection();
			// Wenn eine Zeile ausgewählt(nicht MMRP) ist und das Configpanel zu MMRP gewechselt wird
			// oder wenn eine Zeile ausgewählt(MMRP) ist und das Configpanel zu IP gewechselt wird
		}
	}
	/**
	 * Bildet die Schnittstelle zum Multicast Controller zum stoppen von einem Bestimmten Multicast.
	 * Sorgt f�r die ensprechenden Updates in der GUI nach dem Versuch den Multicast zu stoppen.
	 * @param row Zeilenindex des Multicast welcher gestoppt werden soll
	 * @param typ Programmteil in welchem sich der Multicast befindet welcher gestoppt werden soll
	 */
	public void stopMC(int row, Typ typ){
		mc.stopMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}

	/**
	 * Funktion welche sich um das Update des Graphen k�mmert.
	 * @param typ bestimmt welcher Graph in welchem Programmteil geupdatet werden soll
	 */
	private void updateGraph(Typ typ) {
		//boolean variable which determines if graph can be painted or not, is graph in front of SRC address dropdown
		boolean showupdate=false;
		//check if graph is selected
		if(getPanTabbed(typ).getTab_console().getSelectedIndex()==0){
			if(getPanTabbed(typ).getPan_graph().isVisible() && getPanTabbed(typ).getTab_console().isVisible()){
					showupdate = 	!getPanConfig(typ).getCb_sourceIPaddress().isPopupVisible() &&
							!PopUpMenu.isPopUpVisible();
			}
			else{
				showupdate=false;
			}
		}
		//check which tab is selected and update graph for specific program part
		if(Typ.isSender(typ)){
			getPanTabbed(typ).getPan_graph().updateGraph(mc.getPPSSender(typ), showupdate&&graphUpdate);
		}
		else{
			graphData = new MulticastData[mc.getMCs(typ).size()];
			mc.getMCs(typ).toArray(graphData);
			getPanTabbed(typ).getPan_recGraph().updateGraph(graphData, showupdate&&graphUpdate);

		}
	}
	/**
	 * Funktion welche unterscheidet welche Art von Update in der Multicast Tabelle erfolgt ist.
	 * Hierbei kann zwischen Einf�gen, L�schen und Updaten einer Zeile unterschieden werden.
	 * @param typ Bestimmt den Programmteil welcher geupdated wird
	 * @param utyp Bestimmt die Art des Updates welches Erfolgt ist
	 */
	public void updateTable(Typ typ, UpdateTyp utyp){ //i=0 -> insert, i=1 -> delete, i=2 -> change
		PanelTabbed tabpart = getPanTabbed(typ);
		switch(utyp){
		case INSERT:	tabpart.getTableModel().insertUpdate();
		tabpart.getPan_status().getLb_multicast_count().setText(getMCCount(typ)+Messages.getString("ViewController.11")); //$NON-NLS-1$
		if(initFinished){
			clearInput(typ);
		}
		break;
		case DELETE:	tabpart.getTableModel().deleteUpdate();
		tabpart.getPan_status().getLb_multicast_count().setText(getMCCount(typ)+Messages.getString("ViewController.11")); //$NON-NLS-1$
		if(initFinished){
			clearInput(typ);
		}
		break;

		case UPDATE:	tabpart.getTableModel().changeUpdate();
		break;
		}
	}
	/**
	 * Funktion die einen bestimmten Programmteil updatet.
	 * @param typ Programmteil der geupdatet werden soll
	 */
	public void updateTablePart(Typ typ){
		if(typ != Typ.UNDEFINED){
			updateTable(typ, UpdateTyp.UPDATE);
		}
	}
	@Override
	/**
	 * Implementierung des ListSelectionListeners, sorgt f�r korrektes Verhalten der GUI
	 * beim Selektieren und Deselektieren von einer oder mehreren Zeilen in der Tabelle.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource()==getTable(Typ.SENDER_V4).getSelectionModel()){
			listSelectionEventFired(Typ.SENDER_V4);
		}
		if(e.getSource()==getTable(Typ.RECEIVER_V4).getSelectionModel()){
			listSelectionEventFired(Typ.RECEIVER_V4);
		}
		if(e.getSource()==getTable(Typ.SENDER_V6).getSelectionModel()){
			listSelectionEventFired(Typ.SENDER_V4);
		}
		if(e.getSource()==getTable(Typ.RECEIVER_V6).getSelectionModel()){
			listSelectionEventFired(Typ.RECEIVER_V4);
		}
		if(e.getSource()==getTable(Typ.SENDER_MMRP).getSelectionModel()){
			listSelectionEventFired(Typ.SENDER_V4);
		}
		if(e.getSource()==getTable(Typ.RECEIVER_MMRP).getSelectionModel()){
			listSelectionEventFired(Typ.RECEIVER_V4);
		}
		autoSave();
	}
	/**
	 * Diese Funktion bildet die eigentliche Schnittstelle zum MulticastController und erm�glicht
	 * die GUI zu einem bestimmen Zeitpunkt zu updaten.
	 */
	public void viewUpdate(){
		Typ typ = getSelectedTab();
		if(typ!=Typ.UNDEFINED){
			updateTablePart(typ);
			updateGraph(typ);
			getPanStatus(typ).updateTraffic(this);
		}
	}
	/**
	 * Diese Funktion liest die akutellen Benutzereingaben in der GUI aus und speichert sie
	 * in den 4 UserInputData Objekten und gibt sie weiter zum speichern in der permanenten
	 * Konfigurationsdatei.
	 */
	public void submitInputData(){
		userInputData.setSelectedTab(getSelectedTab());
		userInputData.setSelectedRowsArray(getSelectedRows(Typ.SENDER_V4));
		userInputData.setNetworkInterface(getPanConfig(Typ.SENDER_V4).getSelectedSourceIndex());
		userInputData.setGroupadress(getPanConfig(Typ.SENDER_V4).getTf_groupIPaddress().getText());
		userInputData.setPort(getPanConfig(Typ.SENDER_V4).getTf_udp_port().getText());
		userInputData.setTtl(getPanConfig(Typ.SENDER_V4).getTf_ttl().getText());
		userInputData.setPacketrate(getPanConfig(Typ.SENDER_V4).getTf_packetrate().getText());
		userInputData.setPacketlength(getPanConfig(Typ.SENDER_V4).getTf_udp_packetlength().getText());
		userInputData.setSelectedRows(getSelectedRows(Typ.SENDER_V4));
		userInputData.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected()); //$NON-NLS-1$
		mc.autoSave(userInputData);
	}

	/**
	 *	liest die UserInputData f�r einen bestimmten Programmteil,
	 *	ordnet die Tabellenspalten entsprechend an und setzt die Sichtbarkeit der Tabellenspalten.
	 * @param input UserInputData Objekt welches aus der permanenten Konfigurationsdatei gelesen wird
	 * @param typ Bestimmt den Programmteil f�r welchen die Tabelle angepasst werden soll
	 */
	public void setColumnSettings(UserInputData input, Typ typ){
		ArrayList<TableColumn> columns = getPanTabbed(typ).getColumns();
		ArrayList<Integer> saved_visibility = input.getColumnVisbility(typ);
		ArrayList<Integer> saved_order = input.getColumnOrder(typ);
		int columnCount = getTable(typ).getColumnCount();
		for(int i = 0 ; i < columnCount ; i++){
			getTable(getSelectedTab()).getColumnModel().removeColumn(getTable(getSelectedTab()).getColumnModel().getColumn(0));
		}
		for(int i = 0; i < saved_visibility.size() ; i++){
			getTable(getSelectedTab()).getColumnModel().addColumn(columns.get(saved_visibility.get(i).intValue()));
		}
		userInputData.setColumnOrder(saved_order,typ);  //Typ
		userInputData.setColumnVisibility(saved_visibility,typ); //Typ
	}
	/**
	 * Funktion welche die aktuellen Nutzereingaben im Programm speichert.
	 */
	public void autoSave(){
		if(initFinished && f.isAutoSaveEnabled()){
			submitInputData();
		}
	}
	/**
	 * Funktion welche bei Programmstart die Automatische
	 */
	public void loadAutoSave() {
		UserInputData loaded = mc.loadAutoSave();
		if(loaded != null){
			userInputData = loaded;
			f.setTitle("Sender - " +userInputData.getFilename()+" - Multicastor");
			f.setAutoSave((userInputData.isAutoSaveEnabled()));
		}
		initFinished=true;
	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object aktiviert wird
	 */
	public void windowActivated(WindowEvent e) {
		// not used

	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster geschlossen wird
	 */
	public void windowClosed(WindowEvent e) {
		// not used

	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster ge�ffnet wird
	 */
	public void windowClosing(WindowEvent e) {
		closeProgram();
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object deaktiviert wird
	 */
	public void windowDeactivated(WindowEvent e) {
		// not used

	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster de-minimiert wurde
	 */
	public void windowDeiconified(WindowEvent e) {
		// not used

	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster minimiert wurde
	 */
	public void windowIconified(WindowEvent e) {
		// not used

	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster ge�ffnet wird
	 */
	public void windowOpened(WindowEvent e) {
		// not used

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// not used
		
	}

}