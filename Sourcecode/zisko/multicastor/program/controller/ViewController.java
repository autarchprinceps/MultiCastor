package zisko.multicastor.program.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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

import zisko.multicastor.program.view.FrameFileChooser;
import zisko.multicastor.program.view.FrameMain;
import zisko.multicastor.program.view.MiscBorder;
import zisko.multicastor.program.view.MiscTableModel;
import zisko.multicastor.program.view.PanelMulticastConfig;
import zisko.multicastor.program.view.PanelMulticastControl;
import zisko.multicastor.program.view.PanelStatusBar;
import zisko.multicastor.program.view.PanelTabbed;
import zisko.multicastor.program.view.PopUpMenu;
import zisko.multicastor.program.view.MiscBorder.BorderTitle;
import zisko.multicastor.program.view.MiscBorder.BorderType;
import zisko.multicastor.program.view.ReceiverGraph.valueType;
import zisko.multicastor.program.view.SnakeGimmick;
import zisko.multicastor.program.view.SnakeGimmick.SNAKE_DIRECTION;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.data.UserlevelData;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.data.UserlevelData.Userlevel;
import zisko.multicastor.program.model.InputValidator;
import zisko.multicastor.program.model.NetworkAdapter;
/**
 * Steuerungsklasse des GUI
 * @author	Daniel Becker
 *
 */
public class ViewController implements 	ActionListener, MouseListener, ChangeListener, ComponentListener, 
										ListSelectionListener, KeyListener, DocumentListener, ItemListener, 
										ContainerListener, TableColumnModelListener, MouseMotionListener, WindowListener{
	/**
	 * Enum welches angibt um was für eine Art von GUI Benachrichtigung es sich handelt.
	 * @author Daniel Becker
	 *
	 */
	public enum MessageTyp {
		INFO, WARNING, ERROR
	}
	/**
	 * Enum welches angibt um was für eine Art von GUI Update es sich handelt.
	 * @author Daniel Becker
	 *
	 */
	public enum UpdateTyp{
		UPDATE, INSERT, DELETE
	}
	private SnakeGimmick.SNAKE_DIRECTION snakeDir = SNAKE_DIRECTION.E;
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
	 * eine richtige oder falsche eingabe getätigt wurde.
	 */
	private boolean[][] input = new boolean[4][6];
	/**
	 * Hilfsvariable welche benötigt wird um die gemeinsamen Revceiver Graph Daten 
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
	 * Datenobjekt welches den Input des IPv4 Senders enthält.
	 */
	private UserInputData inputData_S4;
	public boolean isInitFinished() {
		return initFinished;
	}
	/**
	 * Datenobjekt welches den Input des IPv6 Senders enthält.
	 */
	private UserInputData inputData_S6;
	/**
	 * Datenobjekt welches den Input des IPv4 Receivers enthält.
	 */
	private UserInputData inputData_R4;
	/**
	 * Datenobjekt welches den Input des IPv6 Receivers enthält.
	 */
	private UserInputData inputData_R6;
	private Point aboutTabLocation;
	
	/**
	 * Standardkonstruktor der GUI, hierbei wird die GUI noch nicht initialisiert!
	 */
	public ViewController(){
		//initialize(null);
	}
	/**
	 * Implementierung des ActionListeners, betrifft die meisten GUI Komponenten.
	 * Diese Funktion wird aufgerufen wenn eine Interaktion mit einer GUI Komponente stattfindet, welche
	 * den ActionListener dieser ViewController Klasse hält. Die IF-THEN-ELSEIF Abragen dienen dazu 
	 * die Komponente zu identifizieren bei welcher die Interaktion stattgefunden hat.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==f.getMi_saveconfig()){
			//System.out.println("Saving!");
			f.getFc_save().toggle();
		}
		else if(e.getSource()==f.getMi_loadconfig()){
			//System.out.println("Loading!");
			f.getFc_load().toggle();
			setColumnSettings(getUserInputData(getSelectedTab()), getSelectedTab());
		}
		else if(e.getSource()==f.getMi_snake()){
			if(getSelectedTab()!=Typ.UNDEFINED && getSelectedTab()!=Typ.CONFIG){
				if(getFrame().getPanelPart(getSelectedTab()).getPan_graph().runSnake)
					getFrame().getPanelPart(getSelectedTab()).getPan_graph().snake(false);
				else
					getFrame().getPanelPart(getSelectedTab()).getPan_graph().snake(true);
			}
		}
		else if(e.getSource()==f.getMi_exit()){
			closeProgram();
		}
		else if(e.getSource() == f.getMi_about()){
			f.setAboutPanelVisible(true);
			f.getTabpane().setSelectedIndex(4);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_V4).getTb_active()){
			toggleBTactive(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_V4).getTb_active()){
			toggleBTactive(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_V6).getTb_active()){
			toggleBTactive(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_V6).getTb_active()){
			toggleBTactive(Typ.RECEIVER_V6);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_V4).getBt_enter()){
			pressBTenter(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_V4).getBt_enter()){
			pressBTenter(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanConfig(Typ.SENDER_V6).getBt_enter()){
			pressBTenter(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanConfig(Typ.RECEIVER_V6).getBt_enter()){
			pressBTenter(Typ.RECEIVER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getDelete()){
			pressBTDelete(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getDelete()){
			pressBTDelete(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V6).getDelete()){
			pressBTDelete(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V6).getDelete()){
			pressBTDelete(Typ.RECEIVER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getDeselect_all()){
			pressBTDeselectAll(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getDeselect_all()){
			pressBTDeselectAll(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V6).getDeselect_all()){
			pressBTDeselectAll(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V6).getDeselect_all()){
			pressBTDeselectAll(Typ.RECEIVER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getSelect_all()){
			pressBTSelectAll(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getSelect_all()){
			pressBTSelectAll(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V6).getSelect_all()){
			pressBTSelectAll(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V6).getSelect_all()){
			pressBTSelectAll(Typ.RECEIVER_V6);
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
		else if(e.getSource()==getPanControl(Typ.SENDER_V6).getStop()){
			pressBTStop(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V6).getStop()){
			pressBTStop(Typ.RECEIVER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V4).getStart()){
			pressBTStart(Typ.SENDER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V4).getStart()){
			pressBTStart(Typ.RECEIVER_V4);
		}
		else if(e.getSource()==getPanControl(Typ.SENDER_V6).getStart()){
			pressBTStart(Typ.SENDER_V6);
		}
		else if(e.getSource()==getPanControl(Typ.RECEIVER_V6).getStart()){
			pressBTStart(Typ.RECEIVER_V6);
		}
		else if(e.getSource()==getFrame().getFc_save().getChooser()){
			saveFileEvent(e);
		}
		else if(e.getSource()==getFrame().getFc_load().getChooser()){
			loadFileEvent(e);
		}
		else if(e.getActionCommand().equals("hide")){
			hideColumnClicked();
			getTable(getSelectedTab()).getColumnModel().removeColumn(getTable(getSelectedTab()).getColumnModel().getColumn(PopUpMenu.getSelectedColumn()));
		}
		else if(e.getActionCommand().equals("showall")){
			popUpResetColumnsPressed();
		}
		else if(e.getActionCommand().equals("PopupCheckBox")){
			popUpCheckBoxPressed();
		}
		else if(e.getActionCommand().equals("lastConfig1")){
			loadConfig(f.getLastConfigs().get(0), true, true, true, true, true);
		}
		else if(e.getActionCommand().equals("lastConfig2")){
			loadConfig(f.getLastConfigs().get(1), true, true, true, true, true);
		}
		else if(e.getActionCommand().equals("lastConfig3")){
			loadConfig(f.getLastConfigs().get(2), true, true, true, true, true);
		}
		autoSave();
	}
	/**
	 * Funktion welche aufgerufen wird wenn der User Reset View im Popup Menu des Tabellenkopf drückt.
	 * Stellt das ursprüngliche Aussehen der Tabelle wieder her.
	 */
	private void popUpResetColumnsPressed() {
		getUserInputData(getSelectedTab()).resetColumns();
		getPanTabbed(getSelectedTab()).setTableModel(this, getSelectedTab());
	}
	/**
	 * Funktion die aufgerufen wird wenn eine Checkbox im Popup Menu des Tabellenkopfs gedrückt wird.
	 * Wird verwendet zum Einblenden und Ausblenden von Tabellenspalten. 
	 */
	private void popUpCheckBoxPressed() {
		ArrayList<Integer> visibility = new ArrayList<Integer>();
		UserInputData store = new UserInputData();
		store.setColumnOrder(getUserInputData(getSelectedTab()).getColumnOrder());
		JCheckBox[] columns = PopUpMenu.getColumns();
		for(int i = 0 ; i < columns.length ; i++){
			if(columns[i].isSelected()){
				String s = columns[i].getText();
				if(s.equals("STATE")){
					visibility.add(new Integer(0));
				}
				else if(s.equals("ID")){
					visibility.add(new Integer(1));
				}
				else if(s.equals("GRP IP")){
					visibility.add(new Integer(2));
				}
				else if(s.equals("D RATE")){
					visibility.add(new Integer(3));
				}
				else if(s.equals("M RATE")){
					visibility.add(new Integer(4));
				}
				else if(s.equals("Mbit/s")){
					visibility.add(new Integer(5));
				}
				else if(s.equals("PORT")){
					visibility.add(new Integer(6));
				}
				else if(s.equals("SRC IP") || s.equals("LOSS/S")){
					visibility.add(new Integer(7));
				}
				else if(s.equals("#SENT") || s.equals("AVG INT")){
					visibility.add(new Integer(8));
				}
				else if(s.equals("#INT") || s.equals("TTL")){
					visibility.add(new Integer(9));
				}
				else if(s.equals("SRC") || s.equals("LENGTH")){
					visibility.add(new Integer(10));
				}
			}
		}
		store.setColumnVisibility(visibility);
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
         //Wenn Container, noch containerListener benï¿½tigt
         //Auï¿½erdem benï¿½tigen die Childs den gleichen listener
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
	 * Funktion welche aufgerufen wird wenn sich die Parameter eines Textfelds geändert haben.
	 */
	public void changedUpdate(DocumentEvent arg0) {

		
	}
	/**
	 * Funktion welche ein geändertes Multicast Datenobjekt an den MultiCast Controller weitergibt zur Verarbeitung.
	 * @param mcd Das geänderter MulticastData Object.
	 */
	public void changeMC(MulticastData mcd){
		//System.out.println(mcd.toString());
		mc.changeMC(mcd);
		updateTable(mcd.getTyp(),UpdateTyp.UPDATE);
	}
	/**
	 * Hilfsfunktion welche den momentanen Input des KonfigurationsPanels in ein Multicast Datenobjekt schreibt.
	 * Wird benötigt zum anlegen neuer Multicast sowie zum ändern vorhandener Multicasts.
	 * @param mcd MulticastData Objet welches geändert werden soll.
	 * @param typ Programmteil aus welchem die Input Daten ausgelesen werden sollen.
	 * @return Geänderters Multicast Datenobjekt.
	 */
	private MulticastData changeMCData(MulticastData mcd, MulticastData.Typ typ){
		switch(typ){
			case SENDER_V4:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")){
					mcd.setGroupIp(InputValidator.checkMC_IPv4(getPanConfig(typ).getTf_groupIPaddress().getText()));
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress().getSelectedIndex()==0)){
					mcd.setSourceIp(getPanConfig(typ).getSelectedAddress(typ));	
				}
				if(!getPanConfig(typ).getTf_udp_packetlength().getText().equals("...")){
					mcd.setPacketLength(InputValidator.checkIPv4PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()));
					if(getSelectedUserLevel() == Userlevel.BEGINNER){
						mcd.setPacketLength(InputValidator.checkIPv4PacketLength("2048"));
					}
				}
				if(!getPanConfig(typ).getTf_ttl().getText().equals("...")){
					mcd.setTtl(InputValidator.checkTimeToLive(getPanConfig(typ).getTf_ttl().getText()));
					if(getSelectedUserLevel() == Userlevel.BEGINNER){
						mcd.setTtl(InputValidator.checkTimeToLive("32"));
					}
				}
				if(!getPanConfig(typ).getTf_packetrate().getText().equals("...")){
					mcd.setPacketRateDesired(InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText()));
					if(getSelectedUserLevel() == Userlevel.BEGINNER){
						mcd.setPacketRateDesired(InputValidator.checkPacketRate("50"));
					}
				}
				
			break;
			case SENDER_V6:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")){
					mcd.setGroupIp(InputValidator.checkMC_IPv6(getPanConfig(typ).getTf_groupIPaddress().getText()));
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress().getSelectedIndex()==0)){
					mcd.setSourceIp(getPanConfig(typ).getSelectedAddress(typ));	
				}
				if(!getPanConfig(typ).getTf_udp_packetlength().getText().equals("...")){
					mcd.setPacketLength(InputValidator.checkIPv6PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()));
					if(getSelectedUserLevel() == Userlevel.BEGINNER){
						mcd.setPacketLength(InputValidator.checkIPv6PacketLength("2048"));
					}
				}
				if(!getPanConfig(typ).getTf_ttl().getText().equals("...")){
					mcd.setTtl(InputValidator.checkTimeToLive(getPanConfig(typ).getTf_ttl().getText()));
					if(getSelectedUserLevel() == Userlevel.BEGINNER){
						mcd.setTtl(InputValidator.checkTimeToLive("32"));
					}
				}
				if(!getPanConfig(typ).getTf_packetrate().getText().equals("...")){
					mcd.setPacketRateDesired(InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText()));
					if(getSelectedUserLevel() == Userlevel.BEGINNER){
						mcd.setPacketRateDesired(InputValidator.checkPacketRate("50"));
					}
				}
			break;
			case RECEIVER_V4:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")){
					mcd.setGroupIp(InputValidator.checkMC_IPv4(getPanConfig(typ).getTf_groupIPaddress().getText()));
				}
			break;
			case RECEIVER_V6:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")){
					mcd.setGroupIp(InputValidator.checkMC_IPv6(getPanConfig(typ).getTf_groupIPaddress().getText()));
				}
			break;
			default: System.out.println("changeMCData(Multicastdata mcd) - ERROR");
		}
		if(!getPanConfig(typ).getTf_udp_port().getText().equals("...")){
			mcd.setUdpPort(InputValidator.checkPort(getPanConfig(typ).getTf_udp_port().getText()));
		}
		if(!getPanConfig(typ).getTb_active().getText().equals("multiple")){
			mcd.setActive(getPanConfig(typ).getTb_active().isSelected());
		}
		mcd.setTyp(typ);
		//System.out.println(mcd.toString());
		return mcd;
	}
	/**
	 * Funktion welche aufgerufen wird wenn der User das Netzwerk Interface in einem Sender ändert.
	 * @param typ Programmteil in welchem das Netzwerkinterface geändert wurde.
	 */
	private void changeNetworkInterface(Typ typ) {
		PanelMulticastConfig configpart = getPanConfig(typ);
		int selectedIndex = configpart.getTf_sourceIPaddress().getSelectedIndex();
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
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv6SOURCE, BorderType.TRUE));
				if(typ==Typ.SENDER_V6){
					input[2][1]=true;
				}
				else{
					input[3][1]=true;
				}
			}
		}
		else if(getSelectedRows(typ).length > 1 && configpart.getCb_sourceIPaddress().getItemAt(0).equals("...")){
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
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv6SOURCE, BorderType.TRUE));
				if(typ==Typ.SENDER_V6){
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
				case SENDER_V6: input[2][1]=false;  break;
				case RECEIVER_V6: input[3][1]=false;  break;
			}
			if(typ == Typ.SENDER_V6){
				configpart.getPan_sourceIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.IPv6SOURCE, BorderType.NEUTRAL));
			}
			else{
				configpart.getPan_sourceIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.NEUTRAL));
			}
			
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn sich der Inhalt eines Textfelds im Konfigurations Panel ändert.
	 * Prüft ob alle eingaben korrekt sind.
	 * @param typ Programmteil in welchem die Eingaben geprüft werden sollen.
	 */
	private void checkInput(Typ typ){
		if(getSelectedUserLevel()==Userlevel.BEGINNER){
			input[0][3] = true;
			input[0][4] = true;
			input[0][5] = true;
			input[2][3] = true;
			input[2][4] = true;
			input[2][5] = true;
		}
		switch(typ){
			case SENDER_V4:
				if(	input[0][0] &&
					input[0][1] &&
					input[0][2] &&
					input[0][3] &&
					input[0][4] &&
					input[0][5]){
							getPanConfig(typ).getBt_enter().setEnabled(true);
				}
				else if(getSelectedRows(getSelectedTab()).length <=1){
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
			case RECEIVER_V4:	
				if(	input[1][0] &&
					input[1][2]){
					f.getPanel_rec_ipv4().getPan_config().getBt_enter().setEnabled(true);
				}
				else if(getSelectedRows(typ).length <=1){
					f.getPanel_rec_ipv4().getPan_config().getBt_enter().setEnabled(false);
				}
				break;
			case SENDER_V6:		
				if(	input[2][0] &&
					input[2][1] &&
					input[2][2] &&
					input[2][3] &&
					input[2][4] &&
					input[2][5]){
					f.getPanel_sen_ipv6().getPan_config().getBt_enter().setEnabled(true);
				}
				else if(getSelectedRows(getSelectedTab()).length <=1){
					f.getPanel_sen_ipv6().getPan_config().getBt_enter().setEnabled(false);
				}
				break;
			case RECEIVER_V6:	
				if(	input[3][0] &&
					input[3][2]){
					f.getPanel_rec_ipv6().getPan_config().getBt_enter().setEnabled(true);
				}
				else if(getSelectedRows(getSelectedTab()).length <=1){
					f.getPanel_rec_ipv6().getPan_config().getBt_enter().setEnabled(false);
				}
				break;
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn die Eingaben der Textfelder des Konfigurations Panels zurückgesetzt werden sollen.
	 * @param typ Programmteil in welchem die Textfelder zurückgesetzt werden sollen.
	 */
	private void clearInput(Typ typ){
		if(initFinished){
			//System.out.println("clearinput");
			getPanConfig(typ).getTf_groupIPaddress().setText("-");
			getPanConfig(typ).getTf_udp_port().setText("-");
			getPanConfig(typ).getTf_groupIPaddress().setText("");;
			getPanConfig(typ).getTf_udp_port().setText("");
			if(typ==Typ.SENDER_V4 || typ==Typ.SENDER_V6){
				getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
				getPanConfig(typ).getTf_ttl().setText("");
				getPanConfig(typ).getTf_packetrate().setText("");;
				getPanConfig(typ).getTf_udp_packetlength().setText("");;
				getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
				getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0);
				getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
			}
			getPanConfig(typ).getTb_active().setSelected(false);
			getPanConfig(typ).getTb_active().setText("inactive");
			getPanConfig(typ).getTb_active().setForeground(Color.red);
			getPanConfig(typ).getTf_groupIPaddress().requestFocusInWindow();
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn das About Panel geschlossen werden soll.
	 */
	public void closeAboutPanel() {
		getFrame().setAboutPanelVisible(false);
	}
	/**
	 * Funktion welche aufgerufen wird wenn das Programm beendet wird. 
	 * Sorgt für ein sauberes Beenden des Programms. (nicht immer möglich)
	 */
	private void closeProgram() {
		//System.out.println("Shutting down GUI...");
		f.setVisible(false);
		//System.out.println("Cleanup...");
		mc.destroy();
		//System.out.println("Closing program...");
		System.exit(0);
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte zur Tabelle hinzugefügt wird.
	 */
	public void columnAdded(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override	
	/**
	 * Funktion welche aufgerufen wird wenn sich der Aussenabstand der Tabellenspalte ändert.
	 */
	public void columnMarginChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte in der Tabelle verschoben wird.
	 */
	public void columnMoved(TableColumnModelEvent arg0) {
		if(arg0.getFromIndex() != arg0.getToIndex()){
			//System.out.println("column moved from "+arg0.getFromIndex()+" "+arg0.getToIndex());
			getUserInputData(getSelectedTab()).changeColumns(arg0.getFromIndex(), arg0.getToIndex());
			autoSave();
		}
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte aus der Tabelle entfernt wird.
	 */
	public void columnRemoved(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Andere Spalte in der Tabelle selektiert wird.
	 */
	public void columnSelectionChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener zum ViewPort hinzugefügt wird.
	 */
	public void componentAdded(ContainerEvent e) {
		addKeyAndContainerListenerToAll(e.getChild());
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener unsichtbar gemacht wird.
	 */
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener verschoben wird.
	 */
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
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
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener in der Größe verändert wird.
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
		// TODO Auto-generated method stub
		
	}
	/**
	 * Hilfsfunktion welche alle Multicasts aus dem jeweiligen Programmteil löscht
	 * @param typ
	 */
	public void deleteAllMulticasts(Typ typ){
		pressBTSelectAll(typ);
		pressBTDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn ein bestimmter Multicast gelöscht werden soll.
	 * @param mcd MulticastData Objekt des Multicasts welcher gelöscht werden soll.
	 */
	public void deleteMC(MulticastData mcd){
		mc.deleteMC(mcd);
		updateTable(mcd.getTyp(),UpdateTyp.DELETE);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Group IP Adress Felds geändert wurde.
	 * @param typ Programmteil in welchem das Group IP Adress Feld geändert wurde.
	 */
	private void docEventTFgrp(Typ typ){
		if(typ==Typ.SENDER_V4 || typ == Typ.RECEIVER_V4){
			if((InputValidator.checkMC_IPv4(getPanConfig(typ).getTf_groupIPaddress().getText())!= null) 
			|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_groupIPaddress().getText().equals("..."))){
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.TRUE));
				if(typ==Typ.SENDER_V4){
					input[0][0]=true;		
				}
				else{
					input[1][0]=true;
				}
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
			if(getPanConfig(typ).getTf_groupIPaddress().getText().equalsIgnoreCase("")){		
				getPanConfig(typ).getPan_groupIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.NEUTRAL));
			}
		}
		else if(typ==Typ.SENDER_V6 || typ == Typ.RECEIVER_V6){
			if((InputValidator.checkMC_IPv6(getPanConfig(typ).getTf_groupIPaddress().getText())!= null)
			|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_groupIPaddress().getText().equals("..."))){
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv6GROUP, BorderType.TRUE));
				if(typ==Typ.SENDER_V6){
					input[2][0]=true;		
				}
				else{
					input[3][0]=true;
				}
			}
			else{
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv6GROUP, BorderType.FALSE));
				if(typ==Typ.SENDER_V6){
					input[2][0]=false;		
				}
				else{
					input[3][0]=false;
				}
			}
			if(getPanConfig(typ).getTf_groupIPaddress().getText().equalsIgnoreCase("")){		
				getPanConfig(typ).getPan_groupIPaddress()
				.setBorder(MiscBorder.getBorder(BorderTitle.IPv6GROUP, BorderType.NEUTRAL));
			}			
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Length Felds geändert wurde.
	 * @param typ Programmteil in welchem das Packet Length Feld geändert wurde.
	 */
	private void docEventTFlength(Typ typ){
		switch(typ){
			case SENDER_V4:
				if((InputValidator.checkIPv4PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText())> 0)
				|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_udp_packetlength().getText().equals("..."))){
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
			case SENDER_V6:
				if((InputValidator.checkIPv6PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText())> 0)
				|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_udp_packetlength().getText().equals("..."))){
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
		if(getPanConfig(typ).getTf_udp_packetlength().getText().equalsIgnoreCase("")){					
			getPanConfig(typ).getPan_packetlength()
			.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));					
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Port Felds geändert wurde.
	 * @param typ Programmteil in welchem das Port Feld geändert wurde.
	 */
	private void docEventTFport(Typ typ){
		if((InputValidator.checkPort(getPanConfig(typ).getTf_udp_port().getText()) > 0)
		|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_udp_port().getText().equals("..."))){
			getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
			if(typ==Typ.SENDER_V4){
				input[0][2]=true;		
			}
			else if(typ == Typ.RECEIVER_V4){
				input[1][2]=true;
			}
			else if(typ == Typ.SENDER_V6){
				input[2][2]=true;
			}
			else if(typ == Typ.RECEIVER_V6){
				input[3][2]=true;
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
			else if(typ == Typ.SENDER_V6){
				input[2][2]=false;
			}
			else if(typ == Typ.RECEIVER_V6){
				input[3][2]=false;
			}
		}
		if(getPanConfig(typ).getTf_udp_port().getText().equalsIgnoreCase("")){		
			getPanConfig(typ).getPan_udp_port()
			.setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));
		}		
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Rate Felds geändert wurde.
	 * @param typ Programmteil in welchem das Packet Rate Feld geändert wurde.
	 */
	private void docEventTFrate(Typ typ){
		if((InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText())> 0)
		|| (getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_packetrate().getText().equals("..."))){
			getPanConfig(typ).getPan_packetrate()
			.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
			if(typ == Typ.SENDER_V4){
				input[0][4]=true;
			}
			else if(typ == Typ.SENDER_V6){
				input[2][4]=true;
			}
		}
		else{
			getPanConfig(typ).getPan_packetrate()
			.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.FALSE));
			if(typ == Typ.SENDER_V4){
				input[0][4]=false;
			}
			else if(typ == Typ.SENDER_V6){
				input[2][4]=false;
			}
		}
		if(getPanConfig(typ).getTf_packetrate().getText().equalsIgnoreCase("")){					
			getPanConfig(typ).getPan_packetrate()
			.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));					
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des TTL Felds geändert wurde.
	 * @param typ Programmteil in welchem das TTL Feld geändert wurde.
	 */
	private void docEventTFttl(Typ typ){
		if((InputValidator.checkTimeToLive(getPanConfig(typ).getTf_ttl().getText())> 0)
		|| 	(getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_ttl().getText().equals("..."))){
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
		if(getPanConfig(typ).getTf_ttl().getText().equalsIgnoreCase("")){					
			getPanConfig(typ).getPan_ttl()
			.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));					
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn die Fenstergröße geändert wurde. 
	 * Passt die Komponenten der GUI auf die neue Größe an.
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
	 * Hilfsfunktion welche das Frame zurückgibt in welchem das MultiCastor Tool gezeichnet wird.
	 * @return FrameMain Objekt welches angefordert wurde.
	 */
	public FrameMain getFrame() {
		return f;
	}
	/**
	 * Hilfsfunktion welche die aktuelle Anzahl an Multicasts vom MulticastController anfordert.
	 * @param typ Programmteil in welchem die Multicasts gezählt werden sollen.
	 * @return Zähler der angibt wievielel Multicasts sich in einem Programmteil befinden.
	 */
	public int getMCCount(Typ typ){
		return mc.getMCs(typ).size();
	}
	/**
	 * Hilfsfunktion welche Multicast Daten vom MulticastController anfordert.
	 * @param i Index des angeforderten Multicasts (Index in der Tabelle).
	 * @param typ Programmteil in zu welchem der Multicast gehört.
	 * @return MulticastData Objekt welches vom MulticastController zurückgegeben wird.
	 */
	public MulticastData getMCData(int i, MulticastData.Typ typ){
		return mc.getMC(i, typ);
	}
	/**
	 * Hilfsfunktion welche das Configuration Panel eines bestimmten Programmteils zurückgibt.
	 * @param typ Programmteil in welchem sich das Configuration Panel befindet.
	 * @return PnaleMulticastConfig, welches angefordert wurde.
	 */
	private PanelMulticastConfig getPanConfig(Typ typ){
		PanelMulticastConfig configpart = null;
		switch(typ){
			case SENDER_V4: configpart=f.getPanel_sen_ipv4().getPan_config(); break;
			case RECEIVER_V4: configpart=f.getPanel_rec_ipv4().getPan_config(); break;
			case SENDER_V6: configpart=f.getPanel_sen_ipv6().getPan_config(); break;
			case RECEIVER_V6: configpart=f.getPanel_rec_ipv6().getPan_config(); break;
		}
		return configpart;
	}
	/**
	 * Hilfsfunktion welche das Control Panel eines bestimmten Programmteils zurückgibt.
	 * @param typ Programmteil in welchem sich das Control Panel befindet.
	 * @return PanelMulticastControl, welches angefordert wurde.
	 */
	private PanelMulticastControl getPanControl(Typ typ){
		PanelMulticastControl controlpart = null;
		switch(typ){
			case SENDER_V4: controlpart=f.getPanel_sen_ipv4().getPan_control(); break;
			case RECEIVER_V4: controlpart=f.getPanel_rec_ipv4().getPan_control(); break;
			case SENDER_V6: controlpart=f.getPanel_sen_ipv6().getPan_control(); break;
			case RECEIVER_V6: controlpart=f.getPanel_rec_ipv6().getPan_control(); break;
		}
		return controlpart;
	}
	/**
	 * Hilfsfunktion welche die Statusbar des jeweiligen Programmteils zurück gibt.
	 * @param typ Programmteil in welchem sich die Statusbar befindet.
	 * @return PanelStatusbar welche angefordert wurde.
	 */
	private PanelStatusBar getPanStatus(Typ typ){
		PanelStatusBar statusbarpart = null;
		switch(typ){
			case SENDER_V4: statusbarpart=f.getPanel_sen_ipv4().getPan_status(); break;
			case RECEIVER_V4: statusbarpart=f.getPanel_rec_ipv4().getPan_status(); break;
			case SENDER_V6: statusbarpart=f.getPanel_sen_ipv6().getPan_status(); break;
			case RECEIVER_V6: statusbarpart=f.getPanel_rec_ipv6().getPan_status(); break;
		}
		return statusbarpart;
	}
	/**
	 * Hilfsfunktion welche einen bestimten Programmteil zurück gibt.
	 * @param typ Programmteil welcher angeforder wird.
	 * @return JPanel mit dem angeforderten Programmteil.
	 */
	public PanelTabbed getPanTabbed(Typ typ){
		PanelTabbed ret = null;
		switch(typ){
			case SENDER_V4: ret=f.getPanel_sen_ipv4(); break;
			case RECEIVER_V4: ret=f.getPanel_rec_ipv4(); break;
			case SENDER_V6: ret=f.getPanel_sen_ipv6(); break;
			case RECEIVER_V6: ret=f.getPanel_rec_ipv6(); break;
		}
		return ret;
	}
	/**
	 * Hilfsfunktion welche ein Integer Array mit den Selektierten Zeilen einer Tabele zurückgibt.
	 * @param typ Programmteil in welchem sich die Tabelle befindet.
	 * @return Integer Array mit selektierten Zeilen. (leer wenn keine Zeile selektiert ist).
	 */
	public int[] getSelectedRows(Typ typ){
		int[] ret=null;
		ret = getTable(typ).getSelectedRows();
		return ret;
	}
	/**
	 * Hilfsfunktion welche den Programmteil zurückgibt welcher im Moment per Tab selektiert ist.
	 * @return Programmteil welcher im Vordergrund ist.
	 */
	public Typ getSelectedTab() {
		Typ typ;
		switch(f.getTabpane().getSelectedIndex()){
			case 0: typ = Typ.RECEIVER_V4; break;
			case 1: typ = Typ.SENDER_V4; break;
			case 2: typ = Typ.RECEIVER_V6; break;
			case 3: typ = Typ.SENDER_V6; break;
			default: typ = Typ.UNDEFINED; break;
		}
		return typ;
	}
	/**
	 * Hilfsfunktion welche die Richtung im SnakeProgramm zurückgibt.
	 * @return Richtung in welche die "Snake" laufen soll
	 */
	public SNAKE_DIRECTION getSnakeDir(){
		return snakeDir;
	}
	/**
	 * Hilfsfunktion welche die Tabelle des jeweiligen Programmteil zurückgibt.
	 * @param typ Programmteil aus welchem die Tabelle angeforder wird.
	 * @return Die JTable welche angefordert wurde.
	 */
	public JTable getTable(Typ typ){
		JTable tablepart = null;
		switch(typ){
			case SENDER_V4: tablepart=f.getPanel_sen_ipv4().getTable(); break;
			case RECEIVER_V4: tablepart=f.getPanel_rec_ipv4().getTable(); break;
			case SENDER_V6: tablepart=f.getPanel_sen_ipv6().getTable(); break;
			case RECEIVER_V6: tablepart=f.getPanel_rec_ipv6().getTable(); break;
		}
		return tablepart;
	}
	/**
	 * Hilfsfunktion welches das Model der jeweiligen Tabelle zurückgibt.
	 * @param typ Programmteil von welchem das Tabellenmodel angeforder wird.
	 * @return das Tabellenmodel des spezifizierten Programmteils.
	 */
	public MiscTableModel getTableModel(Typ typ){
		return ((MiscTableModel) getTable(typ).getModel());
	}
	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom Multicast Tool empfangen wird (IPv4 & IPv6).
	 * @return Gibt den Insgesamten Traffic des IPv4SENDER und IPv6SENDER als String zurück (Mbit/s) im Format "##0.000"
	 */
	public String getTotalTrafficDown(){
		 DecimalFormat ret = new DecimalFormat("##0.000");
		 double sum = 0.0;
		 for(int i = 0; i < getTable(Typ.RECEIVER_V4).getModel().getRowCount(); i++){
			 sum = sum + Double.parseDouble(((String) getTable(Typ.RECEIVER_V4).getModel().getValueAt(i, 5)).replace(",", "."));
		 }
		 for(int i = 0; i < getTable(Typ.RECEIVER_V6).getModel().getRowCount(); i++){
			 sum = sum + Double.parseDouble(((String) getTable(Typ.RECEIVER_V6).getModel().getValueAt(i, 5)).replace(",", "."));
		 }
		 return ret.format(sum);
	 }
	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom Multicast Tool verschickt wird (IPv4 & IPv6).
	 */
	public String getTotalTrafficUP(){
		 DecimalFormat ret = new DecimalFormat("##0.000");
		 double sum = 0.0;
		 for(int i = 0; i < getTable(Typ.SENDER_V4).getModel().getRowCount(); i++){
			 sum = sum + Double.parseDouble(((String) getTable(Typ.SENDER_V4).getModel().getValueAt(i, 5)).replace(",", "."));
		 }
		 for(int i = 0; i < getTable(Typ.SENDER_V6).getModel().getRowCount(); i++){
			 sum = sum + Double.parseDouble(((String) getTable(Typ.SENDER_V6).getModel().getValueAt(i, 5)).replace(",", "."));
		 }
		 return ret.format(sum);
	 }
	/**
	 * Funktion welche aufgerufen wird wenn Hide im PopupMenu des Tabellenkopfs gedrückt wurde
	 */
	private void hideColumnClicked() {
		getUserInputData(getSelectedTab()).hideColumn(PopUpMenu.getSelectedColumn());
//		PopUpMenu.updateCheckBoxes(this);
//		removeColumnsFromTable(PopUpMenu.getSelectedColumn());
	}
	/**
	 * Funktion welche die GUI startet und initialisiert
	 * @param p_mc Referenz zum MultiCast Controller, die wichtigste Schnittstelle der GUI.
	 */
	public void initialize(MulticastController p_mc){
		mc = p_mc;
		inputData_S4 = new UserInputData();
		inputData_S6 = new UserInputData();
		inputData_R4 = new UserInputData();
		inputData_R6 = new UserInputData();
		//levelData = new UserlevelData();
		f = new FrameMain(this);
		addKeyAndContainerListenerToAll(f);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Hilfsfunktion zum Testen des Programm mits realen daten, durch diese Funktion können extrem große
	 * Datenmengen simuliert werden.
	 */
	private void insertTestData(){
		for(int i = 1 ; i < 6 ; i++){
			getPanConfig(Typ.SENDER_V4).getTf_groupIPaddress().setText("224.0.0."+i);
			getPanConfig(Typ.SENDER_V4).getCb_sourceIPaddress().setSelectedIndex(1);
			getPanConfig(Typ.SENDER_V4).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.SENDER_V4).getTf_ttl().setText("32");
			getPanConfig(Typ.SENDER_V4).getTf_packetrate().setText(""+10*i);
			getPanConfig(Typ.SENDER_V4).getTf_udp_packetlength().setText("2048");
			getPanConfig(Typ.SENDER_V4).getTb_active().setSelected(true);
			pressBTAdd(Typ.SENDER_V4);
			//Thread.sleep(100);
			
			getPanConfig(Typ.RECEIVER_V4).getTf_groupIPaddress().setText("224.0.0."+i);
			getPanConfig(Typ.RECEIVER_V4).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.RECEIVER_V4).getTb_active().setSelected(true);
			pressBTAdd(Typ.RECEIVER_V4);
			//Thread.sleep(100);
			getPanConfig(Typ.SENDER_V6).getTf_groupIPaddress().setText("ff00::"+i);
			getPanConfig(Typ.SENDER_V6).getCb_sourceIPaddress().setSelectedIndex(1);
			getPanConfig(Typ.SENDER_V6).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.SENDER_V6).getTf_ttl().setText("32");
			getPanConfig(Typ.SENDER_V6).getTf_packetrate().setText(""+10*i);
			getPanConfig(Typ.SENDER_V6).getTf_udp_packetlength().setText("1024");
			getPanConfig(Typ.SENDER_V6).getTb_active().setSelected(true);
			pressBTAdd(Typ.SENDER_V6);
			//Thread.sleep(100);
			getPanConfig(Typ.RECEIVER_V6).getTf_groupIPaddress().setText("ff00::"+i);
			getPanConfig(Typ.RECEIVER_V6).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.RECEIVER_V6).getTb_active().setSelected(true);
			pressBTAdd(Typ.RECEIVER_V6);
			//Thread.sleep(100);
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
		//KEY Event in IPv6 Sender - GroupAddress
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V6).getTf_groupIPaddress().getDocument()){
			docEventTFgrp(Typ.SENDER_V6);				
		}
		//KEY Event in IPv6 Receiver - GroupAddress
		else if(source.getDocument() == getPanConfig(Typ.RECEIVER_V6).getTf_groupIPaddress().getDocument()){
			docEventTFgrp(Typ.RECEIVER_V6);			
		}
		//KEY Event in IPv4 Sender - UDP Port
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_udp_port().getDocument()){
			docEventTFport(Typ.SENDER_V4);
		}
		//KEY Event in IPv4 Receiver - UDP Port
		else if(source.getDocument() == getPanConfig(Typ.RECEIVER_V4).getTf_udp_port().getDocument()){
			docEventTFport(Typ.RECEIVER_V4);
		}
		//KEY Event in IPv6 Sender - UDP Port
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V6).getTf_udp_port().getDocument()){
			docEventTFport(Typ.SENDER_V6);
		}
		//KEY Event in IPv6 Receiver - UDP Port
		else if(source.getDocument() == getPanConfig(Typ.RECEIVER_V6).getTf_udp_port().getDocument()){
			docEventTFport(Typ.RECEIVER_V6);
		}
		//KEY Event in IPv4 Sender - TTL
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_ttl().getDocument()){
			docEventTFttl(Typ.SENDER_V4);
		}
		//KEY Event in IPv6 Sender - TTL
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V6).getTf_ttl().getDocument()){
			docEventTFttl(Typ.SENDER_V6);
		}
		//KEY Event in IPv4 Sender - PacketRate
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_packetrate().getDocument()){
			docEventTFrate(Typ.SENDER_V4);
		}
		//KEY Event in IPv6 Sender - PacketRate
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V6).getTf_packetrate().getDocument()){
			docEventTFrate(Typ.SENDER_V6);
		}
		//KEY Event in IPv4 Sender - PacketLength
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V4).getTf_udp_packetlength().getDocument()){
			docEventTFlength(Typ.SENDER_V4);
		}
		//KEY Event in IPv6 Sender - PacketLength
		else if(source.getDocument() == getPanConfig(Typ.SENDER_V6).getTf_udp_packetlength().getDocument()){
			docEventTFlength(Typ.SENDER_V6);
		}
		autoSave();
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine GUI Komponente mit dem ItemListener selektiert oder deselektiert wird.
	 * Dieser Listener wird für RadioButtons und Checkboxen verwendet.
	 */
	public void itemStateChanged(ItemEvent arg0) {
		if(arg0.getStateChange() == arg0.SELECTED){
			if(arg0.getSource() == getPanConfig(Typ.SENDER_V4).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.SENDER_V4);
			}
			else if(arg0.getSource() == getPanConfig(Typ.RECEIVER_V4).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.RECEIVER_V4);
			}
			else if(arg0.getSource() == getPanConfig(Typ.SENDER_V6).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.SENDER_V6);
			}
			else if(arg0.getSource() == getPanConfig(Typ.RECEIVER_V6).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.RECEIVER_V6);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().getLostPktsRB()){
				//System.out.println("RECEIVER_V4 - LostPacketsRB");
				getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().selectionChanged(valueType.LOSTPKT);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().getJitterRB()){
				//System.out.println("RECEIVER_V4 - JitterRB");
				getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().selectionChanged(valueType.JITTER);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().getMeasPktRtRB()){
				//System.out.println("RECEIVER_V4 - MeasPktRtRB");
				getPanTabbed(Typ.RECEIVER_V4).getPan_recGraph().selectionChanged(valueType.MEASPKT);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().getLostPktsRB()){
				//System.out.println("RECEIVER_V6 - LostPacketsRB");
				getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().selectionChanged(valueType.LOSTPKT);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().getJitterRB()){
				//System.out.println("RECEIVER_V6 - JitterRB");
				getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().selectionChanged(valueType.JITTER);
			}
			else if(arg0.getSource() == getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().getMeasPktRtRB()){
				//System.out.println("RECEIVER_V6 - MeasPktRtRB");
				getPanTabbed(Typ.RECEIVER_V6).getPan_recGraph().selectionChanged(valueType.MEASPKT);
			}
			else if(arg0.getSource() == f.getRb_beginner()){
				changeUserLevel(Userlevel.BEGINNER);
				//System.out.println("userlevel beginner");
			}
			else if(arg0.getSource() == f.getRb_expert()){
				changeUserLevel(Userlevel.EXPERT);
				//System.out.println("userlevel expert");
			}
			else if(arg0.getSource() == f.getRb_custom()){
				changeUserLevel(Userlevel.CUSTOM);
				//System.out.println("userlevel custom");
			}

		}
		else{
			if(arg0.getSource() == f.getMi_autoSave()){
				if(arg0.getStateChange() == ItemEvent.DESELECTED)
				submitInputData();
			}
		}
		if(arg0.getSource() == f.getMi_autoSave()){
			f.setAutoSave(f.isAutoSaveEnabled());
		}
		autoSave();
	}
	/**
	 * Funktion welche aufgerufen wird wenn der User über das Menu UserLevel das Benutzerlevel einstellt.
	 * @param level Nutzerlevel welches eingestellt wurde in der Menubar.
	 */
	private void selectUserLevel(Userlevel level){
		switch(level){
		case BEGINNER: f.getRb_beginner().setSelected(true); break;
		case EXPERT: f.getRb_expert().setSelected(true);; break;
		case CUSTOM: f.getRb_custom().setSelected(true);; break;
		default: f.getRb_expert().setSelected(true);; break;
		}
	}
	private void changeUserLevel(Userlevel level) {
		//load userlevel data
		UserlevelData levelDataSv4 = mc.getUserLevel(Typ.SENDER_V4, level);
		UserlevelData levelDataSv6 = mc.getUserLevel(Typ.SENDER_V6, level);
		UserlevelData levelDataRv4 = mc.getUserLevel(Typ.RECEIVER_V4, level);
		UserlevelData levelDataRv6 = mc.getUserLevel(Typ.RECEIVER_V6, level);
		if(levelDataSv4 != null && levelDataSv6 !=null && levelDataRv4 != null && levelDataRv6!=null){
			//configure visibility settings for panels
			getPanTabbed(Typ.SENDER_V4).setPanels(	levelDataSv4.isConfigPanel(), 
													levelDataSv4.isControlPanel(), 
													levelDataSv4.isStatusBar(), 
													levelDataSv4.isConsole(),
													levelDataSv4.isGraph());
			getPanTabbed(Typ.SENDER_V6).setPanels(	levelDataSv6.isConfigPanel(), 
													levelDataSv6.isControlPanel(), 
													levelDataSv6.isStatusBar(), 
													levelDataSv6.isConsole(),
													levelDataSv6.isGraph());
			getPanTabbed(Typ.RECEIVER_V4).setPanels(levelDataRv4.isConfigPanel(), 
													levelDataRv4.isControlPanel(), 
													levelDataRv4.isStatusBar(), 
													levelDataRv4.isConsole(),
													levelDataRv4.isGraph());
			getPanTabbed(Typ.RECEIVER_V4).setPanels(levelDataRv6.isConfigPanel(), 
													levelDataRv6.isControlPanel(), 
													levelDataRv6.isStatusBar(), 
													levelDataRv6.isConsole(),
													levelDataRv6.isGraph());
			//configure visibility settings for control panel
			//configure visibility settings for start button
			getPanControl(Typ.SENDER_V4).getStart().setVisible(levelDataSv4.isStartButton());
			getPanControl(Typ.SENDER_V6).getStart().setVisible(levelDataSv6.isStartButton());
			getPanControl(Typ.RECEIVER_V4).getStart().setVisible(levelDataRv4.isStartButton());
			getPanControl(Typ.RECEIVER_V6).getStart().setVisible(levelDataRv6.isStartButton());
			//configure visibility settings for stop button
			getPanControl(Typ.SENDER_V4).getStop().setVisible(levelDataSv4.isStopButton());
			getPanControl(Typ.SENDER_V6).getStop().setVisible(levelDataSv6.isStopButton());
			getPanControl(Typ.RECEIVER_V4).getStop().setVisible(levelDataRv4.isStopButton());
			getPanControl(Typ.RECEIVER_V6).getStop().setVisible(levelDataRv6.isStopButton());
			//configure visibility settings for select all button
			getPanControl(Typ.SENDER_V4).getSelect_all().setVisible(levelDataSv4.isSelectAllButton());
			getPanControl(Typ.SENDER_V6).getSelect_all().setVisible(levelDataSv6.isSelectAllButton());
			getPanControl(Typ.RECEIVER_V4).getSelect_all().setVisible(levelDataRv4.isSelectAllButton());
			getPanControl(Typ.RECEIVER_V6).getSelect_all().setVisible(levelDataRv6.isSelectAllButton());
			//configure visibility settings for deselect all button
			getPanControl(Typ.SENDER_V4).getDeselect_all().setVisible(levelDataSv4.isDeselectAllButton());
			getPanControl(Typ.SENDER_V6).getDeselect_all().setVisible(levelDataSv6.isDeselectAllButton());
			getPanControl(Typ.RECEIVER_V4).getDeselect_all().setVisible(levelDataRv4.isDeselectAllButton());
			getPanControl(Typ.RECEIVER_V6).getDeselect_all().setVisible(levelDataRv6.isDeselectAllButton());
			//configure visibility settings for new multicast button
			getPanControl(Typ.SENDER_V4).getNewmulticast().setVisible(levelDataSv4.isNewButton());
			getPanControl(Typ.SENDER_V6).getNewmulticast().setVisible(levelDataSv6.isNewButton());
			getPanControl(Typ.RECEIVER_V4).getNewmulticast().setVisible(levelDataRv4.isNewButton());
			getPanControl(Typ.RECEIVER_V6).getNewmulticast().setVisible(levelDataRv6.isNewButton());
			//configure visibility settings for delete button
			getPanControl(Typ.SENDER_V4).getDelete().setVisible(levelDataSv4.isDeleteButton());
			getPanControl(Typ.SENDER_V6).getDelete().setVisible(levelDataSv6.isDeleteButton());
			getPanControl(Typ.RECEIVER_V4).getDelete().setVisible(levelDataRv4.isDeleteButton());
			getPanControl(Typ.RECEIVER_V6).getDelete().setVisible(levelDataRv6.isDeleteButton());
			//configure visibility settings for config panel
			//configure visibility settings for group ip address field
			getPanConfig(Typ.SENDER_V4).getPan_groupIPaddress().setVisible(levelDataSv4.isGroupIpField());
			getPanConfig(Typ.SENDER_V6).getPan_groupIPaddress().setVisible(levelDataSv6.isGroupIpField());
			getPanConfig(Typ.RECEIVER_V4).getPan_groupIPaddress().setVisible(levelDataRv4.isGroupIpField());
			getPanConfig(Typ.RECEIVER_V6).getPan_groupIPaddress().setVisible(levelDataRv6.isGroupIpField());
			//configure visibility settings for source ip address field
			getPanConfig(Typ.SENDER_V4).getPan_sourceIPaddress().setVisible(levelDataSv4.isSourceIpField());
			getPanConfig(Typ.SENDER_V6).getPan_sourceIPaddress().setVisible(levelDataSv6.isSourceIpField());
			//configure visibility settings for port field
			getPanConfig(Typ.SENDER_V4).getPan_udp_port().setVisible(levelDataSv4.isPortField());
			getPanConfig(Typ.SENDER_V6).getPan_udp_port().setVisible(levelDataSv6.isPortField());
			getPanConfig(Typ.RECEIVER_V4).getPan_udp_port().setVisible(levelDataRv4.isPortField());
			getPanConfig(Typ.RECEIVER_V6).getPan_udp_port().setVisible(levelDataRv6.isPortField());
			//configure visibility settings for TTL field
			getPanConfig(Typ.SENDER_V4).getPan_ttl().setVisible(levelDataSv4.isTtlField());
			getPanConfig(Typ.SENDER_V6).getPan_ttl().setVisible(levelDataSv6.isTtlField());
			//configure visibility settings for packetlength field
			getPanConfig(Typ.SENDER_V4).getPan_packetlength().setVisible(levelDataSv4.isPacketLengthField());
			getPanConfig(Typ.SENDER_V6).getPan_packetlength().setVisible(levelDataSv6.isPacketLengthField());
			//configure visibility settings for packetrate field
			getPanConfig(Typ.SENDER_V4).getPan_packetrate().setVisible(levelDataSv4.isPacketRateField());
			getPanConfig(Typ.SENDER_V6).getPan_packetrate().setVisible(levelDataSv6.isPacketRateField());
			//configure visibility settings for active button
			getPanConfig(Typ.SENDER_V4).getTb_active().setVisible(levelDataSv4.isActiveField());
			getPanConfig(Typ.SENDER_V6).getTb_active().setVisible(levelDataSv6.isActiveField());
			getPanConfig(Typ.RECEIVER_V4).getTb_active().setVisible(levelDataRv4.isActiveField());
			getPanConfig(Typ.RECEIVER_V6).getTb_active().setVisible(levelDataRv6.isActiveField());
			//configure visibility settings for enter button
			getPanConfig(Typ.SENDER_V4).getBt_enter().setVisible(levelDataSv4.isEnterField());
			getPanConfig(Typ.SENDER_V6).getBt_enter().setVisible(levelDataSv4.isEnterField());
			getPanConfig(Typ.RECEIVER_V4).getBt_enter().setVisible(levelDataSv4.isEnterField());
			getPanConfig(Typ.RECEIVER_V6).getBt_enter().setVisible(levelDataSv4.isEnterField());
			//configure visibility settings for menu items
			//configure visibility settings for load dialog
			f.getMi_loadconfig().setVisible(levelDataSv4.isLoadConfigDialog());
			f.getMi_loadconfig().setVisible(levelDataSv6.isLoadConfigDialog());
			f.getMi_loadconfig().setVisible(levelDataRv4.isLoadConfigDialog());
			f.getMi_loadconfig().setVisible(levelDataRv6.isLoadConfigDialog());
			//configure visibility settings for save dialog
			f.getMi_saveconfig().setVisible(levelDataSv4.isSaveConfigDialog());
			f.getMi_saveconfig().setVisible(levelDataSv6.isSaveConfigDialog());
			f.getMi_saveconfig().setVisible(levelDataRv4.isSaveConfigDialog());
			f.getMi_saveconfig().setVisible(levelDataRv6.isSaveConfigDialog());
			//configure visibility settings for userlevel dialog
			f.getM_scale().setVisible(levelDataSv4.isUserLevelRadioGrp());
			f.getM_scale().setVisible(levelDataSv6.isUserLevelRadioGrp());
			f.getM_scale().setVisible(levelDataRv4.isUserLevelRadioGrp());
			f.getM_scale().setVisible(levelDataRv6.isUserLevelRadioGrp());
			//configure visibility settings for autosave dialog
			f.getMi_autoSave().setVisible(levelDataSv4.isAutoSaveCheckbox());
			f.getMi_autoSave().setVisible(levelDataSv6.isAutoSaveCheckbox());
			f.getMi_autoSave().setVisible(levelDataRv4.isAutoSaveCheckbox());
			f.getMi_autoSave().setVisible(levelDataRv6.isAutoSaveCheckbox());
			//configure visibility settings for snake dialog
			f.getMi_snake().setVisible(levelDataSv4.isSnakeGame());
			f.getMi_snake().setVisible(levelDataSv6.isSnakeGame());
			f.getMi_snake().setVisible(levelDataRv4.isSnakeGame());
			f.getMi_snake().setVisible(levelDataRv6.isSnakeGame());
			//configure access rights in multicast table
			//configure state checkbox access in column 0 in jtable
			getTableModel(Typ.SENDER_V4).setStateCheckboxEnabled(levelDataSv4.isStartStopCheckBox());
			getTableModel(Typ.SENDER_V6).setStateCheckboxEnabled(levelDataSv6.isStartStopCheckBox());
			getTableModel(Typ.RECEIVER_V4).setStateCheckboxEnabled(levelDataRv4.isStartStopCheckBox());
			getTableModel(Typ.RECEIVER_V6).setStateCheckboxEnabled(levelDataRv6.isStartStopCheckBox());
			//configure popup menu for jtable columns
			getPanTabbed(Typ.SENDER_V4).setPopupsAllowed(levelDataSv4.isPopupsEnabled());
			getPanTabbed(Typ.SENDER_V6).setPopupsAllowed(levelDataSv6.isPopupsEnabled());
			getPanTabbed(Typ.RECEIVER_V4).setPopupsAllowed(levelDataRv4.isPopupsEnabled());
			getPanTabbed(Typ.RECEIVER_V6).setPopupsAllowed(levelDataRv6.isPopupsEnabled());
			f.setLevel(level);
		}
		else{
			resetRBGroupTo(f.getLevel());
		}
	}
	private void resetRBGroupTo(Userlevel level) {
		//System.out.println("userlevel "+level);
		switch(level){
			case BEGINNER: f.getRb_beginner().setSelected(true); break;
			case EXPERT: f.getRb_expert().setSelected(true); break;
			case CUSTOM: f.getRb_custom().setSelected(true); break;
		}
		
	}
	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur gedrückt wird.
	 */
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		  	case 38: snakeDir = SnakeGimmick.SNAKE_DIRECTION.N;
		  			 break;
		 	case 40: snakeDir = SnakeGimmick.SNAKE_DIRECTION.S;
		  			 break;
		  	case 37: snakeDir = SnakeGimmick.SNAKE_DIRECTION.W;
		  			 break;
		  	case 39: snakeDir = SnakeGimmick.SNAKE_DIRECTION.E;
		  	default:
		  	}
	}
	@Override
	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur losgelassen wird.
	 */
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * Funktion welche Aufgerufen wird sobald die Tastatur einen Input bei gedrückter Taste an das System weitergibt.
	 */
	public void keyTyped(KeyEvent arg0) {

	}
	/**
	 * Funktion welche ausgelöst wird wenn der User eine oder mehrere Zeilen in der Tabelle selektiert.
	 * @param typ Programmteil in welchem der User die Zeilen selektiert hat.
	 */
	private void listSelectionEventFired(Typ typ) {
		PanelTabbed tabpart = null;
		switch(typ){
			case SENDER_V4: tabpart=f.getPanel_sen_ipv4(); break;
			case RECEIVER_V4: tabpart=f.getPanel_rec_ipv4(); break;
			case SENDER_V6: tabpart=f.getPanel_sen_ipv6(); break;
			case RECEIVER_V6: tabpart=f.getPanel_rec_ipv6(); break;
		}
		int[] selectedRows = tabpart.getTable().getSelectedRows();
		tabpart.getPan_status().getLb_multicasts_selected()
		.setText(selectedRows.length+" Multicasts Selected ");
		if(selectedRows.length > 1){
			multipleSelect(typ);
		}
		if(selectedRows.length == 1){
			tabpart.getPan_config().getBt_enter().setText("Change");
			tabpart.getPan_config().getTf_groupIPaddress().setEnabled(true);
			tabpart.getPan_config().getTf_udp_port().setEnabled(true);
			tabpart.getPan_config().getTf_groupIPaddress().setText(getMCData(selectedRows[0],typ).getGroupIp().toString().substring(1));
			//System.out.println("Index: "+NetworkAdapter.findAddressIndex(typ, getMCData(selectedRows[0],typ).getSourceIp().toString()));
			//tabpart.getPan_config().getTf_sourceIPaddress().setText(getMCData(selectedRows[0],typ).getSourceIp().toString().substring(1));;
			tabpart.getPan_config().getTf_udp_port().setText(""+getMCData(selectedRows[0],typ).getUdpPort());;;
			if(typ==Typ.SENDER_V4 || typ == Typ.SENDER_V6){
				getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
				getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0);
				tabpart.getPan_config().getTf_sourceIPaddress().setEnabled(true);
				tabpart.getPan_config().getTf_sourceIPaddress().setSelectedIndex(NetworkAdapter.findAddressIndex(typ, getMCData(selectedRows[0],typ).getSourceIp().toString())+1);
				tabpart.getPan_config().getTf_ttl().setText(""+getMCData(selectedRows[0],typ).getTtl());;
				tabpart.getPan_config().getTf_packetrate().setText(""+getMCData(selectedRows[0],typ).getPacketRateDesired());;
				tabpart.getPan_config().getTf_udp_packetlength().setText(""+getMCData(selectedRows[0],typ).getPacketLength());;
			}
			setTBactive(selectedRows, typ);
			setBTStartStopDelete(typ);
		}
		else if(selectedRows.length == 0){
			clearInput(typ);
			tabpart.getPan_config().getBt_enter().setText("Add");
			tabpart.getPan_config().getTf_groupIPaddress().setEnabled(true);
			if(typ==Typ.SENDER_V4 || typ==Typ.SENDER_V6){
				tabpart.getPan_config().getTf_sourceIPaddress().setEnabled(true);				
			}
			tabpart.getPan_config().getTf_udp_port().setEnabled(true);
			setTBactive(selectedRows, typ);
		}
	}
	/**
	 * Funktion welche ausgelöst wird, wenn der User versucht eine Konfigurationsdatei mit dem "Datei Laden"
	 * Dialog zu laden.
	 */
	private void loadFileEvent(ActionEvent e) {
		if(e.getActionCommand().equals("ApproveSelection")){
			FrameFileChooser fc_load = getFrame().getFc_load();
			loadConfig(	fc_load.getSelectedFile(),
						fc_load.isCbSenderV4Selected(),
						fc_load.isCbSenderV6Selected(),
						fc_load.isCbReceiverV4Selected(),
						fc_load.isCbReceiverV4Selected(),
						fc_load.isCbIncrementalSelected());

			fc_load.getChooser().rescanCurrentDirectory();
			fc_load.toggle();
		}
		else if(e.getActionCommand().equals("CancelSelection")){
			getFrame().getFc_load().toggle();
		}
	}
	private void loadConfig(String s, boolean sv4, boolean sv6, boolean rv4, boolean rv6, boolean incremental) {
		if(incremental){
			//System.out.println("incremental selected");
			if(sv4){
				deleteAllMulticasts(Typ.SENDER_V4);
			}
			if(sv6){
				deleteAllMulticasts(Typ.SENDER_V6);
			}
			if(rv4){
				deleteAllMulticasts(Typ.RECEIVER_V4);
			}
			if(rv6){
				deleteAllMulticasts(Typ.RECEIVER_V6);
			}	
		}
		mc.loadConfigFile(s,sv4,sv6,rv4,rv6);
	}
	@Override
	/**
	 * MouseEvent welches ausgelöst wird wenn eine Maustaste gedrückt und wieder losgelassen wird.
	 */
	public void mouseClicked(MouseEvent e) {
		//is rightclick?
		if(e.getButton() == MouseEvent.BUTTON3){
			//Source of click is table header of selected tab?
			if(getSelectedTab() != Typ.CONFIG && getSelectedTab() != Typ.UNDEFINED){
				if(e.getSource()==getTable(getSelectedTab()).getTableHeader()){
					if(getPanTabbed(getSelectedTab()).isPopupsAllowed()){
						PopUpMenu.createTableHeaderPopup(getTable(getSelectedTab()), this, e);
					}
				}
			}
		}
		else if(e.getButton() == MouseEvent.BUTTON1){
			if(e.getSource() == getFrame().getTabpane()){
				if(getFrame().getAboutPanelState()==2){
					closeAboutPanel();
				}
			}
		}
		autoSave();
	}
	@Override
	/**
	 * MouseEvent welches ausgelöst wird wenn die Maus bei gedrückter Taste bewegt wird.
	 */
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * MouseEvent welches auf Bewegung der Maus reagiert.
	 */
	public void mouseMoved(MouseEvent arg0) {
		if(		arg0.getX()>=431 
				&& arg0.getX()<=445 
				&& arg0.getY() >= 10 
				&& arg0.getY() <= 24 
				&& getFrame().getAboutPanelState()!=2 
				&& getFrame().getAboutPanelState()!=0){
			getFrame().setAboutCloseHovered(true);
		}
		else if(	(arg0.getX()<=431 
					|| arg0.getX()>=445 
					|| arg0.getY() <= 10 
					|| arg0.getY() >= 24) 	
					&& getFrame().getAboutPanelState()!=1
					&& getFrame().getAboutPanelState()!=0){
			getFrame().setAboutCloseHovered(false);
		}
	}
	@Override
	/**
	 * MouseEvent welches auf Drücken einer Maustaste reagiert.
	 */
	public void mousePressed(MouseEvent e) {

		
	}
	@Override
	/**
	 * MouseEvent welches auf Loslassen einer Maustaste reagiert.
	 */
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Funktion welche aufgerufen wird wenn mehr als ein Multicast in der Tabelle selektiert wurde.
	 * Passt die GUI entsprechend an.
	 * @param typ Programmteil in welchem mehrere Multicasts Selektiert wurden.
	 */
	private void multipleSelect(Typ typ) {
		getPanConfig(typ).getBt_enter().setText("Change All");
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			getPanConfig(typ).getTf_sourceIPaddress().removeItemListener(this);
			getPanConfig(typ).getTf_packetrate().getDocument().removeDocumentListener(this);
			getPanConfig(typ).getTf_udp_packetlength().getDocument().removeDocumentListener(this);
			getPanConfig(typ).getTf_ttl().getDocument().removeDocumentListener(this);
			getPanConfig(typ).getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.TRUE));
			getPanConfig(typ).getPan_packetrate().setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
			getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
			getPanConfig(typ).getPan_ttl().setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.TRUE));
			getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
			getPanConfig(typ).getTf_packetrate().setText("...");
			getPanConfig(typ).getTf_udp_packetlength().setText("...");
			getPanConfig(typ).getTf_ttl().setText("...");
			getPanConfig(typ).getTf_sourceIPaddress().addItemListener(this);
			getPanConfig(typ).getTf_packetrate().getDocument().addDocumentListener(this);
			getPanConfig(typ).getTf_udp_packetlength().getDocument().addDocumentListener(this);
			getPanConfig(typ).getTf_ttl().getDocument().addDocumentListener(this);
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("...", 0);
			getPanConfig(typ).getCb_sourceIPaddress().setSelectedIndex(0);
		}
		getPanConfig(typ).getTf_groupIPaddress().getDocument().removeDocumentListener(this);
		getPanConfig(typ).getTf_udp_port().getDocument().removeDocumentListener(this);
		getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.TRUE));
		getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
		getPanConfig(typ).getTf_groupIPaddress().setText("...");
		getPanConfig(typ).getTf_udp_port().setText("...");
		getPanConfig(typ).getTb_active().setText("multiple");
		getPanConfig(typ).getTb_active().setForeground(Color.green);
		getPanConfig(typ).getTb_active().setSelected(false);
		getPanConfig(typ).getTf_groupIPaddress().getDocument().addDocumentListener(this);
		getPanConfig(typ).getTf_udp_port().getDocument().addDocumentListener(this);
		getPanConfig(typ).getBt_enter().setEnabled(true);
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche das About Panel als Tab zum Programm hinzufügt.
	 */
	private void openAboutPanel() {
		if(getFrame().getAboutPanelState()==0){
			getFrame().setAboutPanelVisible(true);
			getFrame().getTabpane().setSelectedIndex(4);
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedrückt wird.
	 * @param typ Programmteil in welchem der Add Button gedrückt wurde
	 */
	private void pressBTAdd(Typ typ) {
		this.addMC(changeMCData(new MulticastData(), typ));
		clearInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Change Button gedrückt wird. Bei selektierten Multicast(s).
	 * @param typ Programmteil in welchem der Change Button gedrückt wurde
	 */
	private void pressBTChange(Typ typ) {
		int[] selectedList = getSelectedRows(typ);
		if(selectedList.length == 1){
			MulticastData mcd = getMCData(selectedList[0],typ);
			changeMC(changeMCData(mcd, typ));
			getTable(typ).getSelectionModel().setSelectionInterval(0, 0);
		}
		else{
			PanelMulticastConfig config = getPanConfig(typ);
			if((typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6)
			&& (config.getTf_groupIPaddress().getText().equals("..."))
			&& (config.getCb_sourceIPaddress().getSelectedIndex()==0)
			&& (config.getTf_udp_port().getText().equals("..."))
			&& (config.getTf_ttl().getText().equals("..."))
			&& (config.getTf_packetrate().getText().equals("..."))
			&& (config.getTf_udp_packetlength().getText().equals("..."))
			&& (config.getTf_udp_port().getText().equals("..."))
			&& (config.getTb_active().getText().equals("multiple"))){
				showMessage(MessageTyp.INFO, "No changes were made.\n\"...\" keeps old values!");
			}
			else if((typ == Typ.RECEIVER_V4 || typ == Typ.RECEIVER_V6)
					&& (config.getTf_groupIPaddress().getText().equals("..."))
					&& (config.getTf_udp_port().getText().equals("..."))
					&& (config.getTb_active().getText().equals("multiple"))){
				showMessage(MessageTyp.INFO, "No changes were made.\n\"...\" keeps old values!");
			}
			else{
				for(int i=selectedList.length-1; i >= 0  ; i--){
					//System.out.println("selected: "+i+": "+selectedList[i]);
					//System.out.println("getting: "+((selectedList.length-1)-i));
					MulticastData mcd = getMCData(selectedList[i]+((selectedList.length-1)-i),typ);
					changeMC(changeMCData(mcd, typ));				
				}
				getTable(typ).getSelectionModel().setSelectionInterval(0, selectedList.length-1);
			}
			setBTStartStopDelete(getSelectedTab());
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Delete Button gedrückt wird.
	 * @param typ Programmteil in welchem der Delete Button gedrückt wurde
	 */
	private void pressBTDelete(Typ typ) {
		int[] selectedRows =getTable(typ).getSelectedRows();
		for(int i=0 ; i<selectedRows.length ; i++){
			//System.out.println("lï¿½sche zeile: "+selectedRows[i]);
			deleteMC(getMCData(selectedRows[i]-i, typ));
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Deselect All Button gedrückt wird.
	 * @param typ Programmteil in welchem der Deselect All Button gedrückt wurde
	 */
	private void pressBTDeselectAll(Typ typ) {
		getTable(typ).clearSelection();
		setBTStartStopDelete(typ);
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0);
			getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
		}
		getPanStatus(typ).getLb_multicasts_selected().setText("0 Multicasts Selected ");
		
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedrückt wird.
	 * Diese Funktion unterscheided ob eine Änderung an einem Multicast stattfinden soll,
	 * oder ein Neuer angelegt werden soll.
	 * @param typ Programmteil in welchem der Add Button gedrückt wurde
	 */
	private void pressBTenter(Typ typ) {
		if(getPanConfig(typ).getBt_enter().getText().equals("Add")){
			pressBTAdd(typ);
		}
		else{
			pressBTChange(typ);
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der New Button gedrückt wird.
	 * @param typ Programmteil in welchem der New Button gedrückt wurde
	 */
	private void pressBTNewMC(Typ typ) {
		clearInput(typ);
		getTable(typ).clearSelection();
		setBTStartStopDelete(typ);
		getPanStatus(typ).getLb_multicasts_selected().setText("0 Multicasts Selected ");
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Select All Button gedrückt wird.
	 * @param typ Programmteil in welchem der Select All Button gedrückt wurde
	 */
	private void pressBTSelectAll(Typ typ) {
		getTable(typ).selectAll();
		if(getSelectedRows(typ).length > 1){
			multipleSelect(typ);
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Start Button gedrückt wird.
	 * @param typ Programmteil in welchem der Start Button gedrückt wurde
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
	 * Funktion welche aufgerufen wird wenn der Stop Button gedrückt wird.
	 * @param typ Programmteil in welchem der Stop Button gedrückt wurde
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
	 * Funktion welche es dem Multicast Controller und somit den restlichen Programmteilen ermöglicht
	 * Ausgaben in der Konsole des GUI zu tätigen. 
	 * @param s Nachricht welche in der Konsole der GUI ausgegeben werden soll
	 */
	public void printConsole(String s){
		getFrame().getPanelPart(Typ.SENDER_V4).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.SENDER_V6).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.RECEIVER_V6).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.SENDER_V6).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.SENDER_V6).getTa_console().getText().length());
		getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().getText().length());
		getFrame().getPanelPart(Typ.RECEIVER_V6).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.RECEIVER_V6).getTa_console().getText().length());

		
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
	 * Funktion welche aufgerufen wird wenn ein Zeichen aus einem Textfeld gelöscht wird.
	 */
	public void removeUpdate(DocumentEvent e) {
		insertUpdate(e);
	}
	/**
	 * Funktion welche aufgerufen wird wenn versucht wird eine Datei zu speichern im Datei speichern Dialog.
	 * @param e ActionEvent welches vom Datei speichern Dialog erzeugt wird
	 */
	private void saveFileEvent(ActionEvent e) {
		if(e.getActionCommand().equals("ApproveSelection")){
			FrameFileChooser fc_save = getFrame().getFc_save();
			//System.out.println("selected File: "+fc_save.getSelectedFile());
			mc.saveConfig(	fc_save.getSelectedFile(), 
							fc_save.isCbSenderV4Selected(), 
							fc_save.isCbSenderV6Selected(), 
							fc_save.isCbReceiverV4Selected(), 
							fc_save.isCbReceiverV6Selected());
			f.updateLastConfigs(fc_save.getSelectedFile());
			fc_save.getChooser().rescanCurrentDirectory();
			fc_save.toggle();
		}
		else if(e.getActionCommand().equals("CancelSelection")){
			getFrame().getFc_save().toggle();
		}
	}
	 /**
	  * Funktion welche das Aussehen des Start Stop und Delete Buttons anpasst je nach dem welche Multicasts ausgewählt wurden.
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
	  * Funktion welche das aussehen des ActiveButtons anpasst (Togglefunktion)
	  * @param b Array welches die Selektierten Reihen in einem Programmteil angibt
	  * @param typ Programmteil in welchem sich der Active Button befindet
	  */
	 private void setTBactive(boolean b, Typ typ) {
		if(b){
			getPanConfig(typ).getTb_active().setSelected(true);
			getPanConfig(typ).getTb_active().setText("active");
			getPanConfig(typ).getTb_active().setForeground(new Color(0,175,0));
		}
		else{
			getPanConfig(typ).getTb_active().setSelected(false);
			getPanConfig(typ).getTb_active().setText("inactive");
			getPanConfig(typ).getTb_active().setForeground(new Color(200,0,0));
		}
	}
	 /**
	  * Funktion welche das aussehen des ActiveButtons anpasst je nach dem welcher Multicast selektiert ist in der Tabelle
	  * @param selectedLine Array welches die Selektierten Reihen in einem Programmteil angibt
	  * @param typ Programmteil in welchem sich der Active Button befindet
	  */
	 public void setTBactive(int[] selectedLine, Typ typ){
		getPanConfig(typ).getTb_active().setEnabled(true);
		if(selectedLine.length==1){	
			if(mc.getMC(selectedLine[0], typ).isActive()){
				getPanConfig(typ).getTb_active().setSelected(true);
				getPanConfig(typ).getTb_active().setText("active");
				getPanConfig(typ).getTb_active().setForeground(new Color(0,175,0));
			}
			else{
				getPanConfig(typ).getTb_active().setSelected(false);
				getPanConfig(typ).getTb_active().setText("inactive");
				getPanConfig(typ).getTb_active().setForeground(new Color(200,0,0));
			}
		}
	}
	/**
	 * Funktion welche ermöglich Nachrichten in der GUI anzuzeigen. Gibt anderen Programmteilen über den
	 * MulticastController die Möglichkeit Informations, Warnungs und Errormeldungen auf dem GUI auszugeben.
	 * @param typ Art der Nachricht (INFO / WARNING / ERROR)
	 * @param message Die eigentliche Nachricht welche angezeigt werden soll
	 */
	public void showMessage(MessageTyp typ, String message){ //type 0 == info, type 1 == warning, type 2 == error
		switch(typ){
			case INFO: JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE); break;
			case WARNING: JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE); break;
			case ERROR: JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE); break;
		}
	}
	/**
	 * Bildet die Schnittstelle zum Multicast Controller zum starten von einem Bestimmten Multicast.
	 * Sorgt für die ensprechenden Updates in der GUI nach dem Versuch den Multicast zu stoppen.
	 * @param row Zeilenindex des Multicast welcher gestartet werden soll
	 * @param typ Programmteil in welchem sich der Multicast befindet welcher gestartet werden soll
	 */
	public void startMC(int row, Typ typ){
		mc.startMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn das Frame in der Größe geändert oder verschoben wird.
	 */
	public void stateChanged(ChangeEvent arg0) {
		if(arg0.getSource() == getFrame().getTabpane()){
			frameResizeEvent();
		}	
	}
	/**
	 * Bildet die Schnittstelle zum Multicast Controller zum stoppen von einem Bestimmten Multicast.
	 * Sorgt für die ensprechenden Updates in der GUI nach dem Versuch den Multicast zu stoppen.
	 * @param row Zeilenindex des Multicast welcher gestoppt werden soll
	 * @param typ Programmteil in welchem sich der Multicast befindet welcher gestoppt werden soll
	 */
	public void stopMC(int row, Typ typ){
		mc.stopMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Active Button im ControlPanel gedrückt wird.
	 * @param typ Bestimmt den Programmteil in welchem der Active Button gedrückt wurde
	 */
	private void toggleBTactive(Typ typ) {
		if(getPanConfig(typ).getTb_active().isSelected()){
			setTBactive(true, typ);
		}
		else{
			setTBactive(false, typ);
		}
	}
	/**
	 * Funktion welche sich um das Update des Graphen kümmert.
	 * @param typ bestimmt welcher Graph in welchem Programmteil geupdatet werden soll
	 */
	private void updateGraph(Typ typ) {
		//boolean variable which determines if graph can be painted or not, is graph in front of SRC address dropdown
		boolean showupdate=false;
		//check if graph is selected
		if(getPanTabbed(typ).getTab_console().getSelectedIndex()==0){
			if(getPanTabbed(typ).getPan_graph().isVisible() && getPanTabbed(typ).getTab_console().isVisible()){
				//System.out.println("panel graph visible");
				if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
					showupdate = 	!getPanConfig(typ).getCb_sourceIPaddress().isPopupVisible() &&
									!PopUpMenu.isPopUpVisible();
				}
				else{
					showupdate = !PopUpMenu.isPopUpVisible();
				}
			}
			else{
				showupdate=false;
			}
		}
		//check which tab is selected and update graph for specific program part
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			//System.out.println("showupdate "+showupdate);
			getPanTabbed(typ).getPan_graph().updateGraph(mc.getPPSSender(typ), showupdate);
		}
		else{
			graphData = new MulticastData[getSelectedRows(typ).length];
			for(int i = 0; i < getSelectedRows(typ).length ; i++){
				graphData[i]=mc.getMC(getSelectedRows(typ)[i], typ);
			}
			getPanTabbed(typ).getPan_recGraph().updateGraph(graphData, showupdate);
			
		}
	}
	/**
	 * Funktion welche unterscheidet welche Art von Update in der Multicast Tabelle erfolgt ist.
	 * Hierbei kann zwischen Einfügen, Löschen und Updaten einer Zeile unterschieden werden.
	 * @param typ Bestimmt den Programmteil welcher geupdated wird
	 * @param utyp Bestimmt die Art des Updates welches Erfolgt ist
	 */
	public void updateTable(Typ typ, UpdateTyp utyp){ //i=0 -> insert, i=1 -> delete, i=2 -> change
		PanelTabbed tabpart = getPanTabbed(typ);
		switch(utyp){
			case INSERT:	tabpart.getTableModel().insertUpdate();
							tabpart.getPan_status().getLb_multicast_count().setText(getMCCount(typ)+" Multicasts Total");
							if(initFinished){
								clearInput(typ);
							}
							break;
			case DELETE:	tabpart.getTableModel().deleteUpdate();
							tabpart.getPan_status().getLb_multicast_count().setText(getMCCount(typ)+" Multicasts Total");
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
	 * Implementierung des ListSelectionListeners, sorgt für korrektes Verhalten der GUI
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
			listSelectionEventFired(Typ.SENDER_V6);
		}
		if(e.getSource()==getTable(Typ.RECEIVER_V6).getSelectionModel()){
			listSelectionEventFired(Typ.RECEIVER_V6);
		}
		autoSave();
	}
	/**
	 * Diese Funktion bildet die eigentliche Schnittstelle zum MulticastController und ermöglicht
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
			inputData_S4.setSelectedTab(getSelectedTab());
			inputData_S6.setSelectedTab(getSelectedTab());
			inputData_R4.setSelectedTab(getSelectedTab());
			inputData_R6.setSelectedTab(getSelectedTab());
			inputData_S4.setSelectedRowsArray(getSelectedRows(Typ.SENDER_V4));
			inputData_S6.setSelectedRowsArray(getSelectedRows(Typ.SENDER_V6));
			inputData_R4.setSelectedRowsArray(getSelectedRows(Typ.RECEIVER_V4));
			inputData_R6.setSelectedRowsArray(getSelectedRows(Typ.RECEIVER_V6));
			inputData_S4.setNetworkInterface(getPanConfig(Typ.SENDER_V4).getSelectedSourceIndex());
			inputData_S6.setNetworkInterface(getPanConfig(Typ.SENDER_V6).getSelectedSourceIndex());
			inputData_S4.setSelectedUserlevel(f.getSelectedUserlevel());
			inputData_S6.setSelectedUserlevel(f.getSelectedUserlevel());
			inputData_R4.setSelectedUserlevel(f.getSelectedUserlevel());
			inputData_R6.setSelectedUserlevel(f.getSelectedUserlevel());			
			inputData_S4.setGroupadress(getPanConfig(Typ.SENDER_V4).getTf_groupIPaddress().getText());
			inputData_S6.setGroupadress(getPanConfig(Typ.SENDER_V6).getTf_groupIPaddress().getText());	
			inputData_R4.setGroupadress(getPanConfig(Typ.RECEIVER_V4).getTf_groupIPaddress().getText());
			inputData_R6.setGroupadress(getPanConfig(Typ.RECEIVER_V6).getTf_groupIPaddress().getText());
			inputData_S4.setPort(getPanConfig(Typ.SENDER_V4).getTf_udp_port().getText());
			inputData_S6.setPort(getPanConfig(Typ.SENDER_V6).getTf_udp_port().getText());
			inputData_R4.setPort(getPanConfig(Typ.RECEIVER_V4).getTf_udp_port().getText());
			inputData_R6.setPort(getPanConfig(Typ.RECEIVER_V6).getTf_udp_port().getText());		
			inputData_S4.setTtl(getPanConfig(Typ.SENDER_V4).getTf_ttl().getText());
			inputData_S6.setTtl(getPanConfig(Typ.SENDER_V6).getTf_ttl().getText());			
			inputData_S4.setPacketrate(getPanConfig(Typ.SENDER_V4).getTf_packetrate().getText());
			inputData_S6.setPacketrate(getPanConfig(Typ.SENDER_V6).getTf_packetrate().getText());		
			inputData_S4.setPacketlength(getPanConfig(Typ.SENDER_V4).getTf_udp_packetlength().getText());
			inputData_S6.setPacketlength(getPanConfig(Typ.SENDER_V6).getTf_udp_packetlength().getText());		
			inputData_S4.setActiveButton(getPanConfig(Typ.SENDER_V4).getTb_active().isSelected());
			inputData_S6.setActiveButton(getPanConfig(Typ.SENDER_V6).getTb_active().isSelected());
			inputData_R4.setActiveButton(getPanConfig(Typ.RECEIVER_V4).getTb_active().isSelected());
			inputData_R6.setActiveButton(getPanConfig(Typ.RECEIVER_V6).getTb_active().isSelected());	
			inputData_S4.setSelectedRows(getSelectedRows(Typ.SENDER_V4));
			inputData_S6.setSelectedRows(getSelectedRows(Typ.SENDER_V6));
			inputData_R4.setSelectedRows(getSelectedRows(Typ.RECEIVER_V4));
			inputData_R6.setSelectedRows(getSelectedRows(Typ.RECEIVER_V6));
			inputData_S4.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			inputData_S6.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			inputData_R4.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			inputData_R6.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			
			Vector<UserInputData> packet = new Vector<UserInputData>();
			packet.add(inputData_R4);
			packet.add(inputData_S4);
			packet.add(inputData_R6);
			packet.add(inputData_S6);
			mc.autoSave(packet);
	}
	/**
	 * Hilfsfunktion zur Bestimmung des UserInputData Objekts anhand des Typs.
	 * @param typ Programmteil für welchen das UserInputData Objekt angefordert wird
	 * @return Gibt das UserInputData Objekt des entsprechenden Typs zurück
	 */
	public UserInputData getUserInputData(Typ typ){
		UserInputData ret = null;
		switch(typ){
			case SENDER_V4: ret = inputData_S4; break;
			case SENDER_V6: ret = inputData_S6; break;
			case RECEIVER_V4: ret = inputData_R4; break;
			case RECEIVER_V6: ret = inputData_R6; break;
		}
		return ret;
	}
	/**
	 *	liest die UserInputData für einen bestimmten Programmteil, 
	 *	ordnet die Tabellenspalten entsprechend an und setzt die Sichtbarkeit der Tabellenspalten.
	 * @param input UserInputData Objekt welches aus der permanenten Konfigurationsdatei gelesen wird
	 * @param typ Bestimmt den Programmteil für welchen die Tabelle angepasst werden soll
	 */
	public void setColumnSettings(UserInputData input, Typ typ){
		ArrayList<TableColumn> columns = getPanTabbed(typ).getColumns();
		ArrayList<Integer> saved_visibility = input.getColumnVisbility();
		ArrayList<Integer> saved_order = input.getColumnOrder();
		int columnCount = getTable(typ).getColumnCount();
		for(int i = 0 ; i < columnCount ; i++){
			getTable(getSelectedTab()).getColumnModel().removeColumn(getTable(getSelectedTab()).getColumnModel().getColumn(0));
		}
		//System.out.println("saved visibility size"+saved_visibility.size());
		for(int i = 0; i < saved_visibility.size() ; i++){
			getTable(getSelectedTab()).getColumnModel().addColumn(columns.get(saved_visibility.get(i).intValue()));
		}
		getUserInputData(typ).setColumnOrder(saved_order);
		getUserInputData(typ).setColumnVisibility(saved_visibility);
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
		Vector<UserInputData> loaded = mc.loadAutoSave();
		//System.out.println("loaded.size "+loaded.size());
		if((loaded != null) && (loaded.size()==4)){
			inputData_R4 = loaded.get(0);
			inputData_S4 = loaded.get(1);
			inputData_R6 = loaded.get(2);
			inputData_S6 = loaded.get(3);		
			loadAutoSavePart(inputData_R4, Typ.RECEIVER_V4);
			loadAutoSavePart(inputData_S4, Typ.SENDER_V4);
			loadAutoSavePart(inputData_R6, Typ.RECEIVER_V6);
			loadAutoSavePart(inputData_S6, Typ.SENDER_V6);
			f.setLastConfigs(mc.getLastConfigs(), false);
			//System.out.println("size: "+mc.getLastConfigs().size());
		}
		initFinished=true;
	}
	/**
	 * Hilfsfunktion zum teilweise laden der Autosave Date, unterschieden nach Programmteil
	 * welche sie betreffen
	 * @param die zu ladenden UserInputData
	 * @param typ der zu den UserInputData zugehörige Programmtetil
	 */
	public void loadAutoSavePart(UserInputData data, Typ typ){
		//System.out.println("typ: "+typ);
		switch(data.getTyp()){
			case RECEIVER_V4: f.getTabpane().setSelectedIndex(0); break;
			case SENDER_V4: f.getTabpane().setSelectedIndex(1); break;
			case RECEIVER_V6: f.getTabpane().setSelectedIndex(2); break;
			case SENDER_V6: f.getTabpane().setSelectedIndex(3); break;
			case UNDEFINED: openAboutPanel();  break;
			default:  f.getTabpane().setSelectedIndex(0);
		}
		f.setAutoSave((data.isAutoSaveEnabled()));
		selectUserLevel(data.getUserLevel());
		//System.out.println("setgroupIP: "+data.getGroupadress());
		getPanConfig(typ).getTf_groupIPaddress().setText(data.getGroupadress());
		//System.out.println("setport: "+data.getPort());
		getPanConfig(typ).getTf_udp_port().setText(data.getPort());
		//System.out.println("setactive: "+data.isActive());
		if(data.isActive()){
			//System.out.println("toggle bt");
			setTBactive(true, typ);
		}
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			//System.out.println("adapter index: "+data.getSourceAdressIndex());
			getPanConfig(typ).getCb_sourceIPaddress().setSelectedIndex(data.getSourceAdressIndex());
			//System.out.println("packetrate: "+data.getPacketrate());
			getPanConfig(typ).getTf_packetrate().setText(data.getPacketrate());
			//System.out.println("ttl: "+data.getTtl());
			getPanConfig(typ).getTf_ttl().setText(data.getTtl());
			//System.out.println("length: "+data.getPacketlength());
			getPanConfig(typ).getTf_udp_packetlength().setText(data.getPacketlength());
		}
	}
	public Userlevel getSelectedUserLevel(){
		Userlevel ret = Userlevel.UNDEFINED;
		if(f.getRb_beginner().isSelected()){
			ret = Userlevel.BEGINNER;
		}
		else if(f.getRb_expert().isSelected()){
			ret = Userlevel.EXPERT;
		}
		else if(f.getRb_custom().isSelected()){
			ret = Userlevel.CUSTOM;
		}
		return ret;
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
	 * Listener welcher darauf reagiert wenn das Fenster geöffnet wird
	 */
	public void windowClosing(WindowEvent e) {
		closeProgram();
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object deaktiviert wird
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
		// TODO Auto-generated method stub
		
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster geöffnet wird
	 */
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
