package multicastor.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;

import multicastor.data.GUIData;
import multicastor.data.MulticastData;
import multicastor.data.UserInputData;
import multicastor.data.MulticastData.Typ;
import multicastor.lang.LanguageManager;
import multicastor.model.InputValidator;
import multicastor.model.NetworkAdapter;
import multicastor.model.NetworkAdapter.IPType;
import multicastor.view.FrameMain;
import multicastor.view.MiscBorder;
import multicastor.view.MiscFont;
import multicastor.view.MiscTableModel;
import multicastor.view.PanelMulticastConfig;
import multicastor.view.PanelMulticastControl;
import multicastor.view.PanelStatusBar;
import multicastor.view.PanelTabbed;
import multicastor.view.PopUpMenu;
import multicastor.view.ReceiverGraph;
import multicastor.view.SnakeGimmick;
import multicastor.view.WideComboBox;
import multicastor.view.MiscBorder.BorderTitle;
import multicastor.view.MiscBorder.BorderType;
import multicastor.view.ReceiverGraph.valueType;
import multicastor.view.SnakeGimmick.SNAKE_DIRECTION;

/**
 * Steuerungsklasse des GUI
 */
public class ViewController implements ActionListener, MouseListener,
		ChangeListener, ComponentListener, ListSelectionListener, KeyListener,
		DocumentListener, ItemListener, ContainerListener,
		TableColumnModelListener, WindowListener {
	/**
	 * Enum welches angibt um was fuer eine Art von GUI Benachrichtigung es sich
	 * handelt.
	 */
	public enum MessageTyp {
		ERROR, INFO, WARNING
	}

	/**
	 * Enum welches angibt um was fuer eine Art von GUI Update es sich handelt.
	 */
	public enum UpdateTyp {
		DELETE, INSERT, UPDATE
	}

	/**
	 * stores the setting of the GUI like tab position and selection
	 * 
	 * @author Daniel Becker
	 */
	public GUIData guidata;

	/**
	 * Referenz zum Frame in welchem das MultiCastor Tool angezeigt wird.
	 */
	private FrameMain f;

	/**
	 * Hilfsvariable welche benoetigt wird um die gemeinsamen Revceiver Graph
	 * Daten (JITTER, LOST PACKETS, MEASURED PACKET RATE) von mehreren
	 * Mutlicasts zu berechnen.
	 */
	private MulticastData[] graphData;

	/**
	 * Hilfsvariable welche angibt wann die Initialisierung der GUI
	 * abgeschlossen ist.
	 */
	private boolean initFinished = false;

	/**
	 * 2-Dimensionales Boolean Feld welches angibt in welchem Feld des
	 * jeweiligen Konfiguration Panels eine richtige oder falsche eingabe
	 * getuetigt wurde.
	 */
	private final boolean[][] input = new boolean[4][6];

	/**
	 * Datenobjekt welches den Input des Layer2 Receivers enthaelt.
	 */
	private UserInputData inputData_R2;

	/**
	 * Datenobjekt welches den Input des Layer3 Receivers enthaelt.
	 */
	private UserInputData inputData_R3;

	/**
	 * Datenobjekt welches den Input des Layer2 Senders enthaelt.
	 */
	private UserInputData inputData_S2;

	/**
	 * Datenobjekt welches den Input des Layer3 Senders enthaelt.
	 */
	private UserInputData inputData_S3;

	/**
	 * LanguageManager provides the access to language files
	 */
	private final LanguageManager lang;

	/**
	 * Referenz zum MulticastController, wichtigste Schnittstelle der Klasse.
	 */
	private MulticastController mc;

	/**
	 * Snake is very funny stuff in this program.
	 */
	private SnakeGimmick.SNAKE_DIRECTION snakeDir = SNAKE_DIRECTION.E;

	/**
	 * Standardkonstruktor der GUI, hierbei wird die GUI noch nicht
	 * initialisiert!
	 */
	public ViewController() {
		lang = LanguageManager.getInstance();
	}

	/**
	 * Implementierung des ActionListeners, betrifft die meisten GUI
	 * Komponenten. Diese Funktion wird aufgerufen wenn eine Interaktion mit
	 * einer GUI Komponente stattfindet, welche den ActionListener dieser
	 * ViewController Klasse huelt. Die IF-THEN-ELSEIF Abragen dienen dazu die
	 * Komponente zu identifizieren bei welcher die Interaktion stattgefunden
	 * hat.
	 * 
	 * @throws NeedRestartException
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {

		// Wenn Draggable Tabbed Pane entfernt wurde
		if(f.isPaneDel()) {
			f.openPane();
		}
		// Wenn "Views" ausgewaehlt werden
		if(e.getActionCommand().startsWith("open_layer")
				|| e.getActionCommand().equals("open_about")) {
			f.getTabpane().openTab(e.getActionCommand());
		}
		if(e.getActionCommand().startsWith("m_open_layer")
				|| e.getActionCommand().equals("m_open_about")) {
			f.getTabpane().openOrCloseTab(e.getActionCommand());
		}

		else if(e.getSource() == f.getMi_help()) {
			// Create File from help file path of current language file
			File helpfile = new File("Language/help."
					+ LanguageManager.getCurrentLanguage() + ".pdf");
			if(!helpfile.exists()) {
				// If there is no help file for current language use english
				// file
				helpfile = new File("Language/help.english.pdf");
			}
			// Check if Desktop is supportet in current environment
			if(Desktop.isDesktopSupported()) {
				// Get desktop instance
				final Desktop desktop = Desktop.getDesktop();

				// Check if Open with standard Program is supported
				if(desktop.isSupported(Desktop.Action.OPEN)) {
					// Show Help File with standart PDF-Reader
					try {
						desktop.open(helpfile);
					} catch(final IOException e1) {
						JOptionPane
								.showMessageDialog(
										f,
										lang.getProperty("message.canNotOpenHelpPart1")
												+ "\n"
												+ helpfile.getAbsolutePath()
												+ "\n"
												+ lang.getProperty("message.canNotOpenHelpPart2"));
					}
				} else {
					JOptionPane
							.showMessageDialog(
									f,
									lang.getProperty("message.canNotOpenHelpPart1")
											+ "\n"
											+ helpfile.getAbsolutePath()
											+ "\n"
											+ lang.getProperty("message.canNotOpenHelpPart2"));
				}
			} else {
				JOptionPane
						.showMessageDialog(
								f,
								lang.getProperty("message.canNotOpenHelpPart1")
										+ "\n"
										+ helpfile.getAbsolutePath()
										+ "\n"
										+ lang.getProperty("message.canNotOpenHelpPart2"));
			}
		}

		else if(e.getActionCommand().startsWith("change_lang_to")) {
			LanguageManager.setCurrentLanguage(e.getActionCommand()
					.replaceFirst("change_lang_to_", ""));
			f.reloadLanguage();
			f.repaint();
		}

		else if(e.getSource() == f.getMi_saveconfig()) {
			saveGUIFileEvent();
		}

		else if(e.getSource() == f.getMi_saveAllMc()) {
			// check if there are any Multicasts to save
			if((mc.getMCs(Typ.L2_RECEIVER).size()
					+ mc.getMCs(Typ.L3_RECEIVER).size()
					+ mc.getMCs(Typ.L2_SENDER).size() + mc
					.getMCs(Typ.L3_SENDER).size()) < 1) {
				JOptionPane.showMessageDialog(f,
						lang.getProperty("message.noMcCreated"));
			} else {
				saveFileEvent(false);
			}
		}

		else if(e.getSource() == f.getMi_saveSelectedMc()) {
			// check if there are any Multicasts to save
			if((f.getPanel(0, 0).getTable().getSelectedRowCount()
					+ f.getPanel(0, 1).getTable().getSelectedRowCount()
					+ f.getPanel(1, 0).getTable().getSelectedRowCount() + f
					.getPanel(1, 1).getTable().getSelectedRowCount()) < 1) {
				JOptionPane.showMessageDialog(f,
						lang.getProperty("message.noMcSelected"));
			} else {
				saveFileEvent(true);
			}
		}

		else if(e.getSource() == f.getMi_loadconfig()) {
			loadGUIFileEvent();
		}

		else if(e.getSource() == f.getMi_loadMc()) {
			loadFileEvent(false);
		}

		else if(e.getSource() == f.getMi_loadAdditionalMc()) {
			loadFileEvent(true);
		}

		else if(e.getSource() == f.getMi_snake()) {
			if((getSelectedTab() != Typ.UNDEFINED)
					&& (getSelectedTab() != Typ.CONFIG)) {
				if(getFrame().getPanelPart(getSelectedTab()).getPan_graph().runSnake) {
					getFrame().getPanelPart(getSelectedTab()).getPan_graph()
							.snake(false);
				} else {
					getFrame().getPanelPart(getSelectedTab()).getPan_graph()
							.snake(true);
				}
			}
		} else if(e.getSource() == f.getMi_exit()) {
			closeProgram();
		}

		else if(e.getSource() == f.getMi_setTitle()) {
			final String temp = JOptionPane.showInputDialog(
					lang.getProperty("message.setNewTitle"), f.getBaseTitle());
			if(temp != null) {
				f.setBaseTitle(temp);
				f.updateTitle();
			}
		}

		// Add Button im Config Panel
		else if(e.getSource() == getPanConfig(Typ.L3_SENDER).getBt_enter()) {
			pressBTenter(Typ.L3_SENDER);
		} else if(e.getSource() == getPanConfig(Typ.L3_RECEIVER).getBt_enter()) {
			pressBTenter(Typ.L3_RECEIVER);
		} else if(e.getSource() == getPanConfig(Typ.L2_SENDER).getBt_enter()) {
			pressBTenter(Typ.L2_SENDER);
		} else if(e.getSource() == getPanConfig(Typ.L2_RECEIVER).getBt_enter()) {
			pressBTenter(Typ.L2_RECEIVER);
		}

		// Active/Inactive Button im Config Panel
		else if(e.getSource() == getPanConfig(Typ.L3_SENDER).getTb_active()) {
			toggleBTactive(Typ.L3_SENDER);
		} else if(e.getSource() == getPanConfig(Typ.L3_RECEIVER).getTb_active()) {
			toggleBTactive(Typ.L3_RECEIVER);
		} else if(e.getSource() == getPanConfig(Typ.L2_SENDER).getTb_active()) {
			toggleBTactive(Typ.L2_SENDER);
		} else if(e.getSource() == getPanConfig(Typ.L2_RECEIVER).getTb_active()) {
			toggleBTactive(Typ.L2_RECEIVER);
		}

		// Delete Button im Control Panel
		else if(e.getSource() == getPanControl(Typ.L3_SENDER).getDelete()) {
			pressBTDelete(Typ.L3_SENDER);
		} else if(e.getSource() == getPanControl(Typ.L3_RECEIVER).getDelete()) {
			pressBTDelete(Typ.L3_RECEIVER);
		} else if(e.getSource() == getPanControl(Typ.L2_RECEIVER).getDelete()) {
			pressBTDelete(Typ.L2_RECEIVER);
		} else if(e.getSource() == getPanControl(Typ.L2_SENDER).getDelete()) {
			pressBTDelete(Typ.L2_SENDER);
		}

		// New Button im Control Panel
		else if(e.getSource() == getPanControl(Typ.L3_SENDER).getNewmulticast()) {
			pressBTNewMC(Typ.L3_SENDER);
		} else if(e.getSource() == getPanControl(Typ.L3_RECEIVER)
				.getNewmulticast()) {
			pressBTNewMC(Typ.L3_RECEIVER);
		} else if(e.getSource() == getPanControl(Typ.L2_SENDER)
				.getNewmulticast()) {
			pressBTNewMC(Typ.L2_SENDER);
		} else if(e.getSource() == getPanControl(Typ.L2_RECEIVER)
				.getNewmulticast()) {
			pressBTNewMC(Typ.L2_RECEIVER);
		}

		// (De)SelectAll Button im Control Panel
		else if(e.getSource() == getPanControl(Typ.L3_SENDER)
				.getSelectDeselect_all()) {
			pressBTSelectAll(Typ.L3_SENDER, true);
		} else if(e.getSource() == getPanControl(Typ.L3_RECEIVER)
				.getSelectDeselect_all()) {
			pressBTSelectAll(Typ.L3_RECEIVER, true);
		} else if(e.getSource() == getPanControl(Typ.L2_SENDER)
				.getSelectDeselect_all()) {
			pressBTSelectAll(Typ.L2_SENDER, true);
		} else if(e.getSource() == getPanControl(Typ.L2_RECEIVER)
				.getSelectDeselect_all()) {
			pressBTSelectAll(Typ.L2_RECEIVER, true);
		}

		// Start/Stop Button im Control Panel
		else if(e.getSource() == getPanControl(Typ.L3_SENDER).getStartStop()) {
			pressBTStartStop(Typ.L3_SENDER);
		} else if(e.getSource() == getPanControl(Typ.L3_RECEIVER)
				.getStartStop()) {
			pressBTStartStop(Typ.L3_RECEIVER);
		} else if(e.getSource() == getPanControl(Typ.L2_SENDER).getStartStop()) {
			pressBTStartStop(Typ.L2_SENDER);
		}
		// Start/Stop Button im Control Panel
		else if(e.getSource() == getPanControl(Typ.L3_SENDER).getStartStop()) {
			pressBTStartStop(Typ.L3_SENDER);
		} else if(e.getSource() == getPanControl(Typ.L3_RECEIVER)
				.getStartStop()) {
			pressBTStartStop(Typ.L3_RECEIVER);
		} else if(e.getSource() == getPanControl(Typ.L2_SENDER).getStartStop()) {
			pressBTStartStop(Typ.L2_SENDER);
		} else if(e.getSource() == getPanControl(Typ.L2_RECEIVER)
				.getStartStop()) {
			pressBTStartStop(Typ.L2_RECEIVER);
		}

		else if(e.getActionCommand().equals("hide")) {
			hideColumnClicked();
			getTable(getSelectedTab()).getColumnModel().removeColumn(
					getTable(getSelectedTab()).getColumnModel().getColumn(
							PopUpMenu.getSelectedColumn()));
		} else if(e.getActionCommand().equals("showall")) {
			popUpResetColumnsPressed();
		} else if(e.getActionCommand().equals("PopupCheckBox")) {
			popUpCheckBoxPressed();
		}

		autoSave();

	}

	/**
	 * Funktion welche ein neues Multicast Datenobjekt an den MultiCast
	 * Controller weitergibt zur Verarbeitung.
	 * 
	 * @param mcd
	 *            neu erstelltes Multicast DatenObjekt welches verarbeitet
	 *            werden soll.
	 */
	public void addMC(final MulticastData mcd) {
		mc.addMC(mcd);
		updateTable(mcd.getTyp(), UpdateTyp.INSERT);
	}

	/**
	 * Funktion welche die aktuellen Nutzereingaben im Programm speichert.
	 */
	public void autoSave() {

		if(initFinished && f.isAutoSaveEnabled()) {
			submitInputData();
		}

	}

	/**
	 * Funktion welche aufgerufen wird wenn sich die Parameter eines Textfelds
	 * geuendert haben.
	 */
	@Override
	public void changedUpdate(final DocumentEvent arg0) {
	}

	/**
	 * Funktion welche ein geuendertes Multicast Datenobjekt an den MultiCast
	 * Controller weitergibt zur Verarbeitung.
	 * 
	 * @param mcd
	 *            Das geuenderter MulticastData Object.
	 */
	public void changeMC(final MulticastData mcd) {
		// System.out.println(mcd.toString());
		mc.changeMC(mcd);
		updateTable(mcd.getTyp(), UpdateTyp.UPDATE);
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte zur Tabelle hinzugefuegt
	 * wird.
	 */
	@Override
	public void columnAdded(final TableColumnModelEvent arg0) {

	}

	/**
	 * Funktion welche aufgerufen wird wenn sich der Aussenabstand der
	 * Tabellenspalte uendert.
	 */
	@Override
	public void columnMarginChanged(final ChangeEvent arg0) {

	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte in der Tabelle
	 * verschoben wird.
	 */
	@Override
	public void columnMoved(final TableColumnModelEvent arg0) {
		if(arg0.getFromIndex() != arg0.getToIndex()) {
			// System.out.println("column moved from "+arg0.getFromIndex()+" "+arg0.getToIndex());
			getUserInputData(getSelectedTab()).changeColumns(
					arg0.getFromIndex(), arg0.getToIndex());
			autoSave();
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte aus der Tabelle entfernt
	 * wird.
	 */
	@Override
	public void columnRemoved(final TableColumnModelEvent arg0) {

	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Andere Spalte in der Tabelle
	 * selektiert wird.
	 */
	@Override
	public void columnSelectionChanged(final ListSelectionEvent arg0) {

	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	 * ComponentListener zum ViewPort hinzugefuegt wird.
	 */
	@Override
	public void componentAdded(final ContainerEvent e) {
		addKeyAndContainerListenerToAll(e.getChild());
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	 * ComponentListener unsichtbar gemacht wird.
	 */
	@Override
	public void componentHidden(final ComponentEvent e) {

	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	 * ComponentListener verschoben wird.
	 */
	@Override
	public void componentMoved(final ComponentEvent e) {

	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	 * ComponentListener von dem ViewPane entfernt wird.
	 */
	@Override
	public void componentRemoved(final ContainerEvent e) {
		removeKeyAndContainerListenerToAll(e.getChild());
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	 * ComponentListener in der Grueuee veruendert wird.
	 */
	@Override
	public void componentResized(final ComponentEvent e) {
		if(e.getSource() == getFrame()) {
			frameResizeEvent();
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem
	 * ComponentListener sichtbar gemacht wird.
	 */
	@Override
	public void componentShown(final ComponentEvent e) {

	}

	/**
	 * Hilfsfunktion welche alle Multicasts aus dem jeweiligen Programmteil
	 * luescht
	 * 
	 * @param typ
	 */
	public void deleteAllMulticasts(final Typ typ) {
		pressBTSelectAll(typ, false);
		pressBTDelete(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn ein bestimmter Multicast geluescht
	 * werden soll.
	 * 
	 * @param mcd
	 *            MulticastData Objekt des Multicasts welcher geluescht werden
	 *            soll.
	 */
	public void deleteMC(final MulticastData mcd) {
		mc.deleteMC(mcd);
		updateTable(mcd.getTyp(), UpdateTyp.DELETE);
	}

	/**
	 * Hilfsfunktion welche das Frame zurueckgibt in welchem das MultiCastor
	 * Tool gezeichnet wird.
	 * 
	 * @return FrameMain Objekt welches angefordert wurde.
	 */
	public FrameMain getFrame() {
		return f;
	}

	/**
	 * Hilfsfunktion welche die aktuelle Anzahl an Multicasts vom
	 * MulticastController anfordert.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Multicasts gezuehlt werden sollen.
	 * @return Zuehler der angibt wievielel Multicasts sich in einem
	 *         Programmteil befinden.
	 */
	public int getMCCount(final Typ typ) {
		return mc.getMCs(typ).size();
	}

	/**
	 * Hilfsfunktion welche Multicast Daten vom MulticastController anfordert.
	 * 
	 * @param i
	 *            Index des angeforderten Multicasts (Index in der Tabelle).
	 * @param typ
	 *            Programmteil in zu welchem der Multicast gehoert.
	 * @return MulticastData Objekt welches vom MulticastController zuruegegeben
	 *         wird.
	 */
	public MulticastData getMCData(final int i, final MulticastData.Typ typ) {
		return mc.getMC(i, typ);
	}

	/**
	 * Hilfsfunktion welche einen bestimten Programmteil zurueck gibt.
	 * 
	 * @param typ
	 *            Programmteil welcher angeforder wird.
	 * @return JPanel mit dem angeforderten Programmteil.
	 */
	public PanelTabbed getPanTabbed(final Typ typ) {
		switch(typ) {
			case L2_SENDER:
				return f.getPanel(1, 0);
			case L2_RECEIVER:
				return f.getPanel(0, 0);
			case L3_SENDER:
				return f.getPanel(1, 1);
			case L3_RECEIVER:
				return f.getPanel(0, 1);
			default:
				return null;
		}
	}

	/**
	 * Hilfsfunktion welche ein Integer Array mit den Selektierten Zeilen einer
	 * Tabele zurueckgibt.
	 * 
	 * @param typ
	 *            Programmteil in welchem sich die Tabelle befindet.
	 * @return Integer Array mit selektierten Zeilen. (leer wenn keine Zeile
	 *         selektiert ist).
	 */
	public int[] getSelectedRows(final Typ typ) {
		JXTable t = getTable(typ);
		int[] result = t.getSelectedRows();
		for(int i = 0; i < result.length; i++) {
			result[i] = t.convertRowIndexToModel(result[i]);
		}
		return result;
	}

	/**
	 * Hilfsfunktion welche den Programmteil zurueckgibt welcher im Moment per
	 * Tab selektiert ist.
	 * 
	 * @return Programmteil welcher im Vordergrund ist.
	 */
	public Typ getSelectedTab() {
		final String title = f.getTabpane().getTitleAt(
				f.getTabpane().getSelectedIndex());
		Typ typ;
		if(title.equals(" " + lang.getProperty("tab.l3s") + " ")) {
			typ = Typ.L3_SENDER;
		} else if(title.equals(" " + lang.getProperty("tab.l3r") + " ")) {
			typ = Typ.L3_RECEIVER;
		} else if(title.equals(" " + lang.getProperty("tab.l2s") + " ")) {
			typ = Typ.L2_SENDER;
		} else if(title.equals(" " + lang.getProperty("tab.l2r") + " ")) {
			typ = Typ.L2_RECEIVER;
		} else {
			typ = Typ.UNDEFINED;
		}

		return typ;
	}

	/**
	 * Hilfsfunktion welche die Richtung im SnakeProgramm zurueckgibt.
	 * 
	 * @return Richtung in welche die "Snake" laufen soll
	 */
	public SNAKE_DIRECTION getSnakeDir() {
		return snakeDir;
	}

	/**
	 * Hilfsfunktion welche die Tabelle des jeweiligen Programmteil zurueckgibt.
	 * 
	 * @param typ
	 *            Programmteil aus welchem die Tabelle angeforder wird.
	 * @return Die JTable welche angefordert wurde.
	 */
	public JXTable getTable(final Typ typ) {
		switch(typ) {
			case L2_RECEIVER:
				return f.getPanel(0, 0).getTable();
			case L2_SENDER:
				return f.getPanel(1, 0).getTable();
			case L3_RECEIVER:
				return f.getPanel(0, 1).getTable();
			case L3_SENDER:
				return f.getPanel(1, 1).getTable();
			default:
				return null;
		}
	}

	/**
	 * Hilfsfunktion welches das Model der jeweiligen Tabelle zurueckgibt.
	 * 
	 * @param typ
	 *            Programmteil von welchem das Tabellenmodel angeforder wird.
	 * @return das Tabellenmodel des spezifizierten Programmteils.
	 */
	public MiscTableModel getTableModel(final Typ typ) {
		return ((MiscTableModel)getTable(typ).getModel());
	}

	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom
	 * Multicast Tool empfangen wird (IPv4 & IPv6).
	 * 
	 * @return Gibt den Insgesamten Traffic des IPv4SENDER und IPv6SENDER als
	 *         String zurueck (Mbit/s) im Format "##0.000"
	 */
	public String getTotalTrafficDown() {
		double sum = 0.0;

		for(int i = 0; i < getTable(Typ.L3_RECEIVER).getModel().getRowCount(); i++) {
			sum = sum
					+ Double.parseDouble(((String)getTable(Typ.L3_RECEIVER)
							.getModel().getValueAt(i, 6)).replace(",", "."));
		}
		return (new DecimalFormat("##0.000")).format(sum);
	}

	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom
	 * Multicast Tool verschickt wird (IPv4 & IPv6).
	 */
	public String getTotalTrafficUP() {
		double sum = 0.0;

		for(int i = 0; i < getTable(Typ.L3_SENDER).getModel().getRowCount(); i++) {
			sum = sum
					+ Double.parseDouble(((String)getTable(Typ.L3_SENDER)
							.getModel().getValueAt(i, 6)).replace(",", "."));
		}
		return (new DecimalFormat("##0.000")).format(sum);
	}

	/**
	 * Hilfsfunktion zur Bestimmung des UserInputData Objekts anhand des Typs.
	 * 
	 * @param typ
	 *            Programmteil fuer welchen das UserInputData Objekt angefordert
	 *            wird
	 * @return Gibt das UserInputData Objekt des entsprechenden Typs zurueck
	 */
	public UserInputData getUserInputData(final Typ typ) {
		switch(typ) {
			case L3_SENDER:
				return inputData_S3;
			case L3_RECEIVER:
				return inputData_R3;
			case L2_SENDER:
				return inputData_S2;
			case L2_RECEIVER:
				return inputData_R2;
			default:
				return null;
		}
	}

	/**
	 * Funktion welche die GUI startet und initialisiert
	 * 
	 * @param p_mc
	 *            Referenz zum MultiCast Controller, die wichtigste
	 *            Schnittstelle der GUI.
	 */
	public void initialize(final MulticastController p_mc) {
		mc = p_mc;

		inputData_S3 = new UserInputData();
		inputData_R3 = new UserInputData();
		inputData_S2 = new UserInputData();
		inputData_R2 = new UserInputData();

		f = new FrameMain(this);
		addKeyAndContainerListenerToAll(f);
		try {
			Thread.sleep(500);
		} catch(final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Funktion, welche die ComboBox fuer Layer2(MMRP/MAC) mit den richtigen
	 * Netzwerkadaptern fuellt.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Box geupdated werden soll.
	 */
	public void insertNetworkAdapters(final Typ typ) {
		final WideComboBox cbSrc = getPanConfig(typ).getCb_sourceIPaddress();

		/* Als erstes die CB leeren */
		cbSrc.removeAllItems();
		cbSrc.addItem("");

		final Vector<byte[]> temp = NetworkAdapter.getMacAdapters();

		if(temp != null) {
			for(int i = 0; i < temp.size(); i++) {
				cbSrc.addItem(NetworkAdapter.getNameToMacAdress(temp.get(i)));
			}
		}

	}

	/**
	 * Funktion welche aufgerufen wird wenn Inhalt in ein Feld des Configuration
	 * Panel eingetrgen wird.
	 */
	@Override
	public void insertUpdate(final DocumentEvent source) {
		if(source.getDocument() == getPanConfig(Typ.L3_SENDER)
				.getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_RECEIVER)
				.getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L3_RECEIVER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER)
				.getTf_udp_port().getDocument()) {
			docEventTFport(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_RECEIVER)
				.getTf_udp_port().getDocument()) {
			docEventTFport(Typ.L3_RECEIVER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER)
				.getTf_ttl().getDocument()) {
			docEventTFttl(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER)
				.getTf_packetrate().getDocument()) {
			docEventTFrate(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER)
				.getTf_udp_packetlength().getDocument()) {
			docEventTFlength(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L2_RECEIVER)
				.getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L2_RECEIVER);
		} else if(source.getDocument() == getPanConfig(Typ.L2_SENDER)
				.getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L2_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L2_SENDER)
				.getTf_udp_packetlength().getDocument()) {
			docEventTFlength(Typ.L2_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L2_SENDER)
				.getTf_packetrate().getDocument()) {
			docEventTFrate(Typ.L2_SENDER);
		}
		autoSave();
	}

	/**
	 * Provides information about the initialization status.
	 * 
	 * @return status of initialization. true if the initialization is fished.
	 */
	public boolean isInitFinished() {
		return initFinished;
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine GUI Komponente mit dem
	 * ItemListener selektiert oder deselektiert wird. Dieser Listener wird fuer
	 * RadioButtons und Checkboxen verwendet.
	 */
	@SuppressWarnings({ "static-access" })
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		if(arg0.getStateChange() == arg0.SELECTED) {
			/* Auswahl eines Netzwerkinterfaces */
			if(arg0.getSource() == getPanConfig(Typ.L3_SENDER)
					.getTf_sourceIPaddress()) {
				changeNetworkInterface(Typ.L3_SENDER);
			} else if(arg0.getSource() == getPanConfig(Typ.L3_RECEIVER)
					.getTf_sourceIPaddress()) {
				changeNetworkInterface(Typ.L3_RECEIVER);
			} else if(arg0.getSource() == getPanConfig(Typ.L2_SENDER)
					.getTf_sourceIPaddress()) {
				changeNetworkInterface(Typ.L2_SENDER);
			} else if(arg0.getSource() == getPanConfig(Typ.L2_RECEIVER)
					.getTf_sourceIPaddress()) {
				changeNetworkInterface(Typ.L2_RECEIVER);
			}

			/* Auswahl des Lost Packages Graphen im Receiver L3 */
			else if(arg0.getSource() == ((ReceiverGraph)getPanTabbed(
					Typ.L3_RECEIVER).getPan_graph()).getLostPktsRB()) {
				((ReceiverGraph)getPanTabbed(Typ.L3_RECEIVER).getPan_graph())
						.selectionChanged(valueType.LOSTPKT);
			}
			/* Auswahl des Jitter Graphen im Receiver L3 */
			else if(arg0.getSource() == ((ReceiverGraph)getPanTabbed(
					Typ.L3_RECEIVER).getPan_graph()).getJitterRB()) {
				((ReceiverGraph)getPanTabbed(Typ.L3_RECEIVER).getPan_graph())
						.selectionChanged(valueType.JITTER);
			}
			/* Auswahl des Measured Packages Graphen im Receiver L3 */
			else if(arg0.getSource() == ((ReceiverGraph)getPanTabbed(
					Typ.L3_RECEIVER).getPan_graph()).getMeasPktRtRB()) {
				((ReceiverGraph)getPanTabbed(Typ.L3_RECEIVER).getPan_graph())
						.selectionChanged(valueType.MEASPKT);
			}

			/* Auswahl des Lost Packages Graphen im Receiver L2 */
			else if(arg0.getSource() == ((ReceiverGraph)getPanTabbed(
					Typ.L2_RECEIVER).getPan_graph()).getLostPktsRB()) {
				((ReceiverGraph)getPanTabbed(Typ.L2_RECEIVER).getPan_graph())
						.selectionChanged(valueType.LOSTPKT);
			}
			/* Auswahl des Jitter Graphen im Receiver L2 */
			else if(arg0.getSource() == ((ReceiverGraph)getPanTabbed(
					Typ.L2_RECEIVER).getPan_graph()).getJitterRB()) {
				((ReceiverGraph)getPanTabbed(Typ.L2_RECEIVER).getPan_graph())
						.selectionChanged(valueType.JITTER);
			}
			/* Auswahl des Measured Packages Graphen im Receiver L2 */
			else if(arg0.getSource() == ((ReceiverGraph)getPanTabbed(
					Typ.L2_RECEIVER).getPan_graph()).getMeasPktRtRB()) {
				((ReceiverGraph)getPanTabbed(Typ.L2_RECEIVER).getPan_graph())
						.selectionChanged(valueType.MEASPKT);
			}
		} else {
			if(arg0.getSource() == f.getMi_autoSave()) {
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {
					submitInputData();
				}
			}
		}
		if(arg0.getSource() == f.getMi_autoSave()) {
			f.setAutoSave(f.isAutoSaveEnabled());
		}
		autoSave();
	}

	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur gedrueckt
	 * wird.
	 */
	@Override
	public void keyPressed(final KeyEvent arg0) {
		switch(arg0.getKeyCode()) {
			case 38:
				snakeDir = SnakeGimmick.SNAKE_DIRECTION.N;
				break;
			case 40:
				snakeDir = SnakeGimmick.SNAKE_DIRECTION.S;
				break;
			case 37:
				snakeDir = SnakeGimmick.SNAKE_DIRECTION.W;
				break;
			case 39:
				snakeDir = SnakeGimmick.SNAKE_DIRECTION.E;
			default:
		}
	}

	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur losgelassen
	 * wird.
	 */
	@Override
	public void keyReleased(final KeyEvent arg0) {
	}

	/**
	 * Funktion welche Aufgerufen wird sobald die Tastatur einen Input bei
	 * gedrueckter Taste an das System weitergibt.
	 */
	@Override
	public void keyTyped(final KeyEvent arg0) {
	}

	/**
	 * Funktion welche bei Programmstart die Automatische
	 */
	public void loadAutoSave() {
		final Vector<UserInputData> loaded = mc.loadAutoSave();
		if((loaded != null) && (loaded.size() == 4)) {
			inputData_S3 = loaded.get(0);
			inputData_R3 = loaded.get(1);
			inputData_S2 = loaded.get(2);
			inputData_R2 = loaded.get(3);
			loadAutoSavePart(inputData_S3, Typ.L3_SENDER);
			loadAutoSavePart(inputData_R3, Typ.L3_RECEIVER);
			loadAutoSavePart(inputData_S2, Typ.L2_SENDER);
			loadAutoSavePart(inputData_R2, Typ.L2_RECEIVER);
			f.setLastConfigs(mc.getLastConfigs(), false);
			// System.out.println("size: "+mc.getLastConfigs().size());
		}
		initFinished = true;
	}

	/**
	 * Hilfsfunktion zum teilweise laden der Autosave Date, unterschieden nach
	 * Programmteil welche sie betreffen
	 * 
	 * @param data
	 *            die zu ladenden UserInputData
	 * @param typ
	 *            der zu den UserInputData zugehuerige Programmtetil
	 */
	public void loadAutoSavePart(final UserInputData data, final Typ typ) {
		switch(data.getTyp()) {
			case L3_SENDER:
				f.getTabpane().setSelectedIndex(0);
				break;
			case L3_RECEIVER:
				f.getTabpane().setSelectedIndex(1);
				break;
			case L2_SENDER:
				f.getTabpane().setSelectedIndex(2);
				break;
			case L2_RECEIVER:
				f.getTabpane().setSelectedIndex(3);
				break;
			default:
				f.getTabpane().setSelectedIndex(0);
		}
		f.setAutoSave((data.isAutoSaveEnabled()));
		getPanConfig(typ).getTf_groupIPaddress().setText(data.getGroupadress());
		getPanConfig(typ).getTf_udp_port().setText(data.getPort());
		if(data.isActive()) {
			setTBactive(true, typ);
		}
		if(typ == Typ.L3_SENDER) {
			getPanConfig(typ).getCb_sourceIPaddress().setSelectedIndex(
					data.getSourceAdressIndex());
			getPanConfig(typ).getTf_packetrate().setText(data.getPacketrate());
			getPanConfig(typ).getTf_ttl().setText(data.getTtl());
			getPanConfig(typ).getTf_udp_packetlength().setText(
					data.getPacketlength());
		}
	}

	/**
	 * MouseEvent welches ausgeluest wird wenn eine Maustaste gedrueckt und
	 * wieder losgelassen wird.
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {
		// is rightclick?
		if(e.getButton() == MouseEvent.BUTTON3) {
			// Source of click is table header of selected tab?
			if((getSelectedTab() != Typ.CONFIG)
					&& (getSelectedTab() != Typ.UNDEFINED)) {
				if(e.getSource() == getTable(getSelectedTab()).getTableHeader()) {
					if(getPanTabbed(getSelectedTab()).isPopupsAllowed()) {
						PopUpMenu.createTableHeaderPopup(
								getTable(getSelectedTab()), this, e);
					}
				}
			}
		}
		autoSave();
	}

	/**
	 * MouseEvent welches auf das Betreten einer Komponente der Maus reagiert.
	 */
	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	/**
	 * MouseEvent welches auf das Verlassen einer Komponente der Maus reagiert.
	 */
	@Override
	public void mouseExited(final MouseEvent e) {

	}

	/**
	 * MouseEvent welches auf Druecken einer Maustaste reagiert.
	 */
	@Override
	public void mousePressed(final MouseEvent e) {

	}

	/**
	 * MouseEvent welches auf Loslassen einer Maustaste reagiert.
	 */
	@Override
	public void mouseReleased(final MouseEvent e) {

	}

	/**
	 * Funktion welche es dem Multicast Controller und somit den restlichen
	 * Programmteilen ermueglicht Ausgaben in der Konsole des GUI zu tuetigen.
	 * 
	 * @param s
	 *            Nachricht welche in der Konsole der GUI ausgegeben werden soll
	 */
	public void printConsole(final String s) {
		getFrame().getPanelPart(Typ.L3_SENDER).getTa_console().append(s + "\n");
		getFrame().getPanelPart(Typ.L3_RECEIVER).getTa_console()
				.append(s + "\n");
		getFrame().getPanelPart(Typ.L2_SENDER).getTa_console().append(s + "\n");
		getFrame().getPanelPart(Typ.L2_RECEIVER).getTa_console()
				.append(s + "\n");
		getFrame()
				.getPanelPart(Typ.L3_SENDER)
				.getTa_console()
				.setCaretPosition(
						getFrame().getPanelPart(Typ.L3_SENDER).getTa_console()
								.getText().length());
		getFrame()
				.getPanelPart(Typ.L3_RECEIVER)
				.getTa_console()
				.setCaretPosition(
						getFrame().getPanelPart(Typ.L3_RECEIVER)
								.getTa_console().getText().length());
		getFrame()
				.getPanelPart(Typ.L2_SENDER)
				.getTa_console()
				.setCaretPosition(
						getFrame().getPanelPart(Typ.L2_SENDER).getTa_console()
								.getText().length());
		getFrame()
				.getPanelPart(Typ.L2_RECEIVER)
				.getTa_console()
				.setCaretPosition(
						getFrame().getPanelPart(Typ.L2_RECEIVER)
								.getTa_console().getText().length());
	}

	/**
	 * Funktion welche aufgerufen wird wenn ein Zeichen aus einem Textfeld
	 * geluescht wird.
	 */
	@Override
	public void removeUpdate(final DocumentEvent e) {
		insertUpdate(e);
	}

	/**
	 * liest die UserInputData fuer einen bestimmten Programmteil, ordnet die
	 * Tabellenspalten entsprechend an und setzt die Sichtbarkeit der
	 * Tabellenspalten.
	 * 
	 * @param input
	 *            UserInputData Objekt welches aus der permanenten
	 *            Konfigurationsdatei gelesen wird
	 * @param typ
	 *            Bestimmt den Programmteil fuer welchen die Tabelle angepasst
	 *            werden soll
	 */
	public void setColumnSettings(final UserInputData input, final Typ typ) {
		System.out.println("input: " + input);
		System.out.println("typ: " + typ);
		final ArrayList<TableColumn> columns = getPanTabbed(typ).getColumns();
		final ArrayList<Integer> saved_visibility = input.getColumnVisbility();
		final ArrayList<Integer> saved_order = input.getColumnOrder();
		final int columnCount = getTable(typ).getColumnCount();
		for(int i = 0; i < columnCount; i++) {
			getTable(getSelectedTab()).getColumnModel().removeColumn(
					getTable(getSelectedTab()).getColumnModel().getColumn(0));
		}
		// System.out.println("saved visibility size"+saved_visibility.size());
		for(int i = 0; i < saved_visibility.size(); i++) {
			getTable(getSelectedTab()).getColumnModel().addColumn(
					columns.get(saved_visibility.get(i).intValue()));
		}
		getUserInputData(typ).setColumnOrder(saved_order);
		getUserInputData(typ).setColumnVisibility(saved_visibility);
	}

	/**
	 * Method is used to aply GUI setting from an GUI Config file
	 * 
	 * @param data
	 *            File content als GUIData object
	 */
	public void setGUIConfig(final GUIData data) {

		guidata = data;
		LanguageManager.setCurrentLanguage(data.getLanguage());
		f.reloadLanguage();
		f.repaint();

		for(int i = 0; i < LanguageManager.languages.length; i++) {
			if(f.getMi_languages()[i].getText().equals(data.getLanguage())) {
				f.getMi_languages()[i].setSelected(true);
			} else {
				f.getMi_languages()[i].setSelected(false);
			}
		}

		f.setBaseTitle(data.getWindowName());
		f.updateTitle();

		String title = "";

		f.getTabpane().closeTab(title);

		if(data.getL2_RECEIVER() == GUIData.TabState.invisible) {
			title = " " + lang.getProperty("tab.l2r") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().closeTab(f.getTabpane().getTitleAt(i));
					f.getTabpane().remove(i);
				}
			}
		}
		if(data.getL3_RECEIVER() == GUIData.TabState.invisible) {
			title = " " + lang.getProperty("tab.l3r") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().closeTab(f.getTabpane().getTitleAt(i));
					f.getTabpane().remove(i);
				}
			}
		}
		if(data.getL2_SENDER() == GUIData.TabState.invisible) {
			title = " " + lang.getProperty("tab.l2s") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().closeTab(f.getTabpane().getTitleAt(i));
					f.getTabpane().remove(i);
				}
			}
		}
		if(data.getL3_SENDER() == GUIData.TabState.invisible) {
			title = " " + lang.getProperty("tab.l3s") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().closeTab(f.getTabpane().getTitleAt(i));
					f.getTabpane().remove(i);
				}
			}
		}
		if(data.getABOUT() == GUIData.TabState.invisible) {
			title = " " + lang.getProperty("mi.about") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().closeTab(f.getTabpane().getTitleAt(i));
					f.getTabpane().remove(i);
				}
			}
		}

		if((data.getL2_RECEIVER() == GUIData.TabState.visible)
				|| (data.getL2_RECEIVER() == GUIData.TabState.selected)) {
			f.getTabpane().openTab("open_layer2_r");
		}

		if((data.getL3_RECEIVER() == GUIData.TabState.visible)
				|| (data.getL3_RECEIVER() == GUIData.TabState.selected)) {
			f.getTabpane().openTab("open_layer3_r");
		}

		if((data.getL2_SENDER() == GUIData.TabState.visible)
				|| (data.getL2_SENDER() == GUIData.TabState.selected)) {
			f.getTabpane().openTab("open_layer2_s");
		}

		if((data.getL3_SENDER() == GUIData.TabState.visible)
				|| (data.getL3_RECEIVER() == GUIData.TabState.selected)) {
			f.getTabpane().openTab("open_layer3_s");
		}

		if((data.getABOUT() == GUIData.TabState.visible)
				|| (data.getABOUT() == GUIData.TabState.selected)) {
			f.getTabpane().openTab("open_about");
		}

		// select the selected tab

		if(data.getL2_RECEIVER() == GUIData.TabState.selected) {
			title = " " + lang.getProperty("tab.l2r") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().setSelectedIndex(i);
				}
			}
		}
		if(data.getL3_RECEIVER() == GUIData.TabState.selected) {
			title = " " + lang.getProperty("tab.l3r") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().setSelectedIndex(i);
				}
			}
		}
		if(data.getL2_SENDER() == GUIData.TabState.selected) {
			title = " " + lang.getProperty("tab.l2s") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().setSelectedIndex(i);
				}
			}
		}
		if(data.getL3_SENDER() == GUIData.TabState.selected) {
			title = " " + lang.getProperty("tab.l3s") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().setSelectedIndex(i);
				}
			}
		}
		if(data.getABOUT() == GUIData.TabState.selected) {
			title = " " + lang.getProperty("mi.about") + " ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().setSelectedIndex(i);
				}
			}
		}
		if(data.getPLUS() == GUIData.TabState.selected) {
			title = " + ";
			for(int i = 0; i < f.getTabpane().getTabCount(); ++i) {
				if(f.getTabpane().getTitleAt(i).equals(title)) {
					f.getTabpane().setSelectedIndex(i);
				}
			}
		}

	}

	public void setInitFinished(final boolean initFinished) {
		this.initFinished = initFinished;
	}

	/**
	 * Funktion welche das aussehen des ActiveButtons anpasst je nach dem
	 * welcher Multicast selektiert ist in der Tabelle
	 * 
	 * @param selectedLine
	 *            Array welches die Selektierten Reihen in einem Programmteil
	 *            angibt
	 * @param typ
	 *            Programmteil in welchem sich der Active Button befindet
	 */
	public void setTBactive(final int[] selectedLine, final Typ typ) {
		getPanConfig(typ).getTb_active().setEnabled(true);
		if(selectedLine.length == 1) {
			if(mc.getMC(selectedLine[0], typ).isActive()) {
				getPanConfig(typ).getTb_active().setSelected(true);
				getPanConfig(typ).getTb_active().setText(
						lang.getProperty("button.active"));
				getPanConfig(typ).getTb_active().setForeground(
						new Color(0, 175, 0));
			} else {
				getPanConfig(typ).getTb_active().setSelected(false);
				getPanConfig(typ).getTb_active().setText(
						lang.getProperty("button.inactive"));
				getPanConfig(typ).getTb_active().setForeground(
						new Color(200, 0, 0));
			}
		}
	}

	/**
	 * Funktion welche ermueglich Nachrichten in der GUI anzuzeigen. Gibt
	 * anderen Programmteilen ueber den MulticastController die Mueglichkeit
	 * Informations, Warnungs und Errormeldungen auf dem GUI auszugeben.
	 * 
	 * @param typ
	 *            Art der Nachricht (INFO / WARNING / ERROR)
	 * @param message
	 *            Die eigentliche Nachricht welche angezeigt werden soll
	 */
	public void showMessage(final MessageTyp typ, final String message) { // type
																			// 0
																			// ==
																			// info,
		// type 1 ==
		// warning, type
		// 2 == error
		switch(typ) {
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
	 * Bildet die Schnittstelle zum Multicast Controller zum starten von einem
	 * Bestimmten Multicast. Sorgt fuer die ensprechenden Updates in der GUI
	 * nach dem Versuch den Multicast zu stoppen.
	 * 
	 * @param row
	 *            Zeilenindex des Multicast welcher gestartet werden soll
	 * @param typ
	 *            Programmteil in welchem sich der Multicast befindet welcher
	 *            gestartet werden soll
	 */
	public void startMC(final int row, final Typ typ) {
		mc.startMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn das Frame in der Grueuee geuendert
	 * oder verschoben wird.
	 */
	@Override
	public void stateChanged(final ChangeEvent arg0) {
		if(arg0.getSource() == getFrame().getTabpane()) {
			frameResizeEvent();
		}
	}

	/**
	 * Bildet die Schnittstelle zum Multicast Controller zum stoppen von einem
	 * Bestimmten Multicast. Sorgt fuer die ensprechenden Updates in der GUI
	 * nach dem Versuch den Multicast zu stoppen.
	 * 
	 * @param row
	 *            Zeilenindex des Multicast welcher gestoppt werden soll
	 * @param typ
	 *            Programmteil in welchem sich der Multicast befindet welcher
	 *            gestoppt werden soll
	 */
	public void stopMC(final int row, final Typ typ) {
		mc.stopMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}

	/**
	 * Diese Funktion liest die akutellen Benutzereingaben in der GUI aus und
	 * speichert sie in den 4 UserInputData Objekten und gibt sie weiter zum
	 * speichern in der permanenten Konfigurationsdatei.
	 */
	public void submitInputData() {
		inputData_S3.setSelectedTab(getSelectedTab());
		inputData_S2.setSelectedTab(getSelectedTab());
		inputData_R3.setSelectedTab(getSelectedTab());
		inputData_R2.setSelectedTab(getSelectedTab());
		inputData_S3.setSelectedRowsArray(getSelectedRows(Typ.L3_SENDER));
		inputData_S2.setSelectedRowsArray(getSelectedRows(Typ.L2_SENDER));
		inputData_R3.setSelectedRowsArray(getSelectedRows(Typ.L3_RECEIVER));
		inputData_R2.setSelectedRowsArray(getSelectedRows(Typ.L2_RECEIVER));
		inputData_S3.setNetworkInterface(getPanConfig(Typ.L3_SENDER)
				.getSelectedSourceIndex());
		inputData_S2.setNetworkInterface(getPanConfig(Typ.L2_SENDER)
				.getSelectedSourceIndex());
		inputData_S3.setGroupadress(getPanConfig(Typ.L3_SENDER)
				.getTf_groupIPaddress().getText());
		inputData_S2.setGroupadress(getPanConfig(Typ.L2_SENDER)
				.getTf_groupIPaddress().getText());
		inputData_R3.setGroupadress(getPanConfig(Typ.L3_RECEIVER)
				.getTf_groupIPaddress().getText());
		inputData_R2.setGroupadress(getPanConfig(Typ.L2_RECEIVER)
				.getTf_groupIPaddress().getText());
		inputData_S3.setPort(getPanConfig(Typ.L3_SENDER).getTf_udp_port()
				.getText());
		inputData_S2.setPort(getPanConfig(Typ.L2_SENDER).getTf_udp_port()
				.getText());
		inputData_R3.setPort(getPanConfig(Typ.L3_RECEIVER).getTf_udp_port()
				.getText());
		inputData_R2.setPort(getPanConfig(Typ.L2_RECEIVER).getTf_udp_port()
				.getText());
		inputData_S3.setTtl(getPanConfig(Typ.L3_SENDER).getTf_ttl().getText());
		inputData_S2.setTtl(getPanConfig(Typ.L2_SENDER).getTf_ttl().getText());
		inputData_S3.setPacketrate(getPanConfig(Typ.L3_SENDER)
				.getTf_packetrate().getText());
		inputData_S2.setPacketrate(getPanConfig(Typ.L2_SENDER)
				.getTf_packetrate().getText());
		inputData_S3.setPacketlength(getPanConfig(Typ.L3_SENDER)
				.getTf_udp_packetlength().getText());
		inputData_S2.setPacketlength(getPanConfig(Typ.L2_SENDER)
				.getTf_udp_packetlength().getText());
		inputData_S3.setActiveButton(getPanConfig(Typ.L3_SENDER).getTb_active()
				.isSelected());
		inputData_S2.setActiveButton(getPanConfig(Typ.L2_SENDER).getTb_active()
				.isSelected());
		inputData_R3.setActiveButton(getPanConfig(Typ.L3_RECEIVER)
				.getTb_active().isSelected());
		inputData_R2.setActiveButton(getPanConfig(Typ.L2_RECEIVER)
				.getTb_active().isSelected());
		inputData_S3.setSelectedRows(getSelectedRows(Typ.L3_SENDER));
		inputData_S2.setSelectedRows(getSelectedRows(Typ.L2_SENDER));
		inputData_R3.setSelectedRows(getSelectedRows(Typ.L3_RECEIVER));
		inputData_R2.setSelectedRows(getSelectedRows(Typ.L2_RECEIVER));
		inputData_S3.setIsAutoSaveEnabled("" + f.getMi_autoSave().isSelected());
		inputData_S2.setIsAutoSaveEnabled("" + f.getMi_autoSave().isSelected());
		inputData_R3.setIsAutoSaveEnabled("" + f.getMi_autoSave().isSelected());
		inputData_R2.setIsAutoSaveEnabled("" + f.getMi_autoSave().isSelected());

		final Vector<UserInputData> packet = new Vector<UserInputData>();
		packet.add(inputData_S3);
		packet.add(inputData_R3);
		packet.add(inputData_S2);
		packet.add(inputData_R2);
		mc.autoSave(packet);

	}

	/**
	 * Funktion welche unterscheidet welche Art von Update in der Multicast
	 * Tabelle erfolgt ist. Hierbei kann zwischen Einfuegen, Lueschen und
	 * Updaten einer Zeile unterschieden werden.
	 * 
	 * @param typ
	 *            Bestimmt den Programmteil welcher geupdated wird
	 * @param utyp
	 *            Bestimmt die Art des Updates welches Erfolgt ist
	 */
	public void updateTable(final Typ typ, final UpdateTyp utyp) { // i=0 ->
																	// insert,
																	// i=1 ->
		// delete, i=2 -> change
		final PanelTabbed tabpart = getPanTabbed(typ);
		switch(utyp) {
			case INSERT:
				tabpart.getTableModel().insertUpdate();
				tabpart.getPan_status()
						.getLb_multicast_count()
						.setText(
								getMCCount(typ) + " "
										+ lang.getProperty("status.mcTotal"));
				if(initFinished) {
					clearInput(typ);
				}
				break;
			case DELETE:
				tabpart.getTableModel().deleteUpdate();
				tabpart.getPan_status()
						.getLb_multicast_count()
						.setText(
								getMCCount(typ) + " "
										+ lang.getProperty("status.mcTotal"));
				if(initFinished) {
					clearInput(typ);
				}
				break;

			case UPDATE:
				tabpart.getTableModel().changeUpdate();
				break;
		}
	}

	/**
	 * Funktion die einen bestimmten Programmteil updatet.
	 * 
	 * @param typ
	 *            Programmteil der geupdatet werden soll
	 */
	public void updateTablePart(final Typ typ) {
		if(typ != Typ.UNDEFINED) {
			updateTable(typ, UpdateTyp.UPDATE);
		}
	}

	/**
	 * Implementierung des ListSelectionListeners, sorgt fuer korrektes
	 * Verhalten der GUI beim Selektieren und Deselektieren von einer oder
	 * mehreren Zeilen in der Tabelle.
	 */
	@Override
	public void valueChanged(final ListSelectionEvent e) {
		if(e.getSource() == getTable(Typ.L3_SENDER).getSelectionModel()) {
			listSelectionEventFired(Typ.L3_SENDER);
		}
		if(e.getSource() == getTable(Typ.L3_RECEIVER).getSelectionModel()) {
			listSelectionEventFired(Typ.L3_RECEIVER);
		}
		if(e.getSource() == getTable(Typ.L2_SENDER).getSelectionModel()) {
			listSelectionEventFired(Typ.L2_SENDER);
		}
		if(e.getSource() == getTable(Typ.L2_RECEIVER).getSelectionModel()) {
			listSelectionEventFired(Typ.L2_RECEIVER);
		}
		autoSave();
	}

	/**
	 * Diese Funktion bildet die eigentliche Schnittstelle zum
	 * MulticastController und ermoeglicht die GUI zu einem bestimmen Zeitpunkt
	 * zu updaten.
	 */
	public void viewUpdate() {
		final Typ typ = getSelectedTab();
		if(typ != Typ.UNDEFINED) {
			updateTablePart(typ);
			updateGraph(typ);
			getPanStatus(typ).updateTraffic(this);
			getPanStatus(typ).getLb_multicast_count().setText(
					getMCCount(typ) + " " + lang.getProperty("status.mcTotal"));
			PanelTabbed tabpart = null;
			switch(typ) {
				case L2_SENDER:
					tabpart = f.getPanel(1, 0);
					break;
				case L3_SENDER:
					tabpart = f.getPanel(1, 1);
					break;
				case L2_RECEIVER:
					tabpart = f.getPanel(0, 0);
					break;
				case L3_RECEIVER:
					tabpart = f.getPanel(0, 1);
					break;
			}
			final int[] selectedRows = tabpart.getTable().getSelectedRows();
			for(int i = 0; i < selectedRows.length; i++) {
				selectedRows[i] = tabpart.getTable().convertRowIndexToModel(selectedRows[i]);
			}
			getPanStatus(typ).getLb_multicasts_selected().setText(
					selectedRows.length + " "
							+ lang.getProperty("status.mcSelected") + " ");
		}
	}

	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object aktiviert wird
	 */
	@Override
	public void windowActivated(final WindowEvent e) {
	}

	/**
	 * Listener welcher darauf reagiert wenn das Fenster geschlossen wird
	 */
	@Override
	public void windowClosed(final WindowEvent e) {
	}

	/**
	 * Listener welcher darauf reagiert wenn das Fenster geueffnet wird
	 */
	@Override
	public void windowClosing(final WindowEvent e) {
		closeProgram();
	}

	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object deaktiviert wird
	 */
	@Override
	public void windowDeactivated(final WindowEvent e) {
	}

	/**
	 * Listener welcher darauf reagiert wenn das Fenster de-minimiert wurde
	 */
	@Override
	public void windowDeiconified(final WindowEvent e) {
	}

	/**
	 * Listener welcher darauf reagiert wenn das Fenster minimiert wurde
	 */
	@Override
	public void windowIconified(final WindowEvent e) {
	}

	/**
	 * Listener welcher darauf reagiert wenn das Fenster geueffnet wird
	 */
	@Override
	public void windowOpened(final WindowEvent e) {
	}

	/**
	 * Added einen Container und einen Keylistener zu dem Component c. Hat die
	 * Komponente Kinder, mache das gleiche mit jedem Child
	 * 
	 * @param c
	 *            Container, der die Listener erhalten soll
	 */
	private void addKeyAndContainerListenerToAll(final Component c) {
		c.addKeyListener(this);
		// Wenn Container, noch containerListener benuetigt
		// Auueerdem benuetigen die Childs den gleichen listener
		if(c instanceof Container) {
			final Container cont = (Container)c;
			cont.addContainerListener(this);
			final Component[] children = cont.getComponents();
			for(final Component element : children) {
				addKeyAndContainerListenerToAll(element);
			}
		}
	}

	/**
	 * Hilfsfunktion welche den momentanen Input des KonfigurationsPanels in ein
	 * Multicast Datenobjekt schreibt. Wird benuetigt zum anlegen neuer
	 * Multicast sowie zum uendern vorhandener Multicasts.
	 * 
	 * @param mcd
	 *            MulticastData Objet welches geuendert werden soll.
	 * @param typ
	 *            Programmteil aus welchem die Input Daten ausgelesen werden
	 *            sollen.
	 * @return Geuenderters Multicast Datenobjekt.
	 */
	private MulticastData changeMCData(final MulticastData mcd,
			final MulticastData.Typ typ, final IPType iptype) {
		switch(typ) {
			case L3_SENDER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText()
						.equals("...")) {
					if(iptype == IPType.IPv4) {
						mcd.setGroupIp(InputValidator
								.checkMC_IPv4(getPanConfig(typ)
										.getTf_groupIPaddress().getText()));
					} else if(iptype == IPType.IPv6) {
						mcd.setGroupIp(InputValidator
								.checkMC_IPv6(getPanConfig(typ)
										.getTf_groupIPaddress().getText()));
					}
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress()
						.getSelectedIndex() == 0)) {
					mcd.setSourceIp(getPanConfig(typ).getSelectedAddress(typ,
							iptype));
				}
				if(!getPanConfig(typ).getTf_udp_packetlength().getText()
						.equals("...")) {
					if(iptype == IPType.IPv4) {
						mcd.setPacketLength(InputValidator
								.checkIPv4PacketLength(getPanConfig(typ)
										.getTf_udp_packetlength().getText()));
					} else if(iptype == IPType.IPv6) {
						mcd.setPacketLength(InputValidator
								.checkIPv6PacketLength(getPanConfig(typ)
										.getTf_udp_packetlength().getText()));
					}
				}
				if(!getPanConfig(typ).getTf_ttl().getText().equals("...")) {
					mcd.setTtl(InputValidator.checkTimeToLive(getPanConfig(typ)
							.getTf_ttl().getText()));
				}
				if(!getPanConfig(typ).getTf_packetrate().getText()
						.equals("...")) {
					mcd.setPacketRateDesired(InputValidator
							.checkPacketRate(getPanConfig(typ)
									.getTf_packetrate().getText()));
				}
				break;
			case L3_RECEIVER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText()
						.equals("...")) {
					if(iptype == IPType.IPv4) {
						mcd.setGroupIp(InputValidator
								.checkMC_IPv4(getPanConfig(typ)
										.getTf_groupIPaddress().getText()));
					} else if(iptype == IPType.IPv6) {
						mcd.setGroupIp(InputValidator
								.checkMC_IPv6(getPanConfig(typ)
										.getTf_groupIPaddress().getText()));
					}
				}
				// V1.5 [FH] Added source IP for receiver (network interface)
				if(!(getPanConfig(typ).getCb_sourceIPaddress()
						.getSelectedIndex() == 0)) {
					mcd.setSourceIp(getPanConfig(typ).getSelectedAddress(typ,
							iptype));
				}
				break;
			case L2_SENDER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText()
						.equals("...")) {
					if(InputValidator.checkMulticastGroup(getPanConfig(typ)
							.getTf_groupIPaddress().getText())) {
						final String inputGroupText = getPanConfig(typ)
								.getTf_groupIPaddress().getText();
						final byte[] b = new byte[6];
						for(int fromIndex = -1, arrayIndex = 0, begin = 0; arrayIndex < 6; arrayIndex++) {
							begin = fromIndex;

							if(arrayIndex < 5) {
								fromIndex = inputGroupText.indexOf(':',
										fromIndex + 1);
							} else {
								fromIndex = inputGroupText.length();
							}

							final String tmp = (String)inputGroupText
									.subSequence(begin + 1, fromIndex);
							if(!tmp.equals("")) {
								b[arrayIndex] = new Byte(
										(byte)Integer.parseInt(tmp, 16));
							} else {
								b[arrayIndex] = new Byte("0");
							}
						}
						mcd.setMmrpGroupMac(b);
					}
					;
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress()
						.getSelectedIndex() == 0)) {
					mcd.setMmrpSourceMac(getPanConfig(typ).getSelectedAddress(
							typ));
				}
				if(!getPanConfig(typ).getTf_udp_packetlength().getText()
						.equals("...")) {
					mcd.setPacketLength(InputValidator
							.checkMMRPPacketLength(getPanConfig(typ)
									.getTf_udp_packetlength().getText()));
				}
				if(!getPanConfig(typ).getTf_packetrate().getText()
						.equals("...")) {
					mcd.setPacketRateDesired(InputValidator
							.checkPacketRate(getPanConfig(typ)
									.getTf_packetrate().getText()));
				}
				break;
			case L2_RECEIVER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText()
						.equals("...")) {
					if(InputValidator.checkMulticastGroup(getPanConfig(typ)
							.getTf_groupIPaddress().getText())) {
						final String inputGroupText = getPanConfig(typ)
								.getTf_groupIPaddress().getText();
						final byte[] b = new byte[6];
						for(int fromIndex = -1, arrayIndex = 0, begin = 0; arrayIndex < 6; arrayIndex++) {
							begin = fromIndex;

							if(arrayIndex < 5) {
								fromIndex = inputGroupText.indexOf(':',
										fromIndex + 1);
							} else {
								fromIndex = inputGroupText.length();
							}

							final String tmp = (String)inputGroupText
									.subSequence(begin + 1, fromIndex);
							if(!tmp.equals("")) {
								b[arrayIndex] = new Byte(
										(byte)Integer.parseInt(tmp, 16));
							} else {
								b[arrayIndex] = new Byte("0");
							}
						}
						mcd.setMmrpGroupMac(b);
					}
					;
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress()
						.getSelectedIndex() == 0)) {
					mcd.setMmrpSourceMac(getPanConfig(typ).getSelectedAddress(
							typ));
				}
				break;
			default:
				System.out.println("changeMCData(Multicastdata mcd) - ERROR");
		}

		if(!getPanConfig(typ).getTf_udp_port().getText().equals("...")) {
			mcd.setUdpPort(InputValidator.checkPort(getPanConfig(typ)
					.getTf_udp_port().getText()));
		}
		if(!getPanConfig(typ).getTb_active().getText().equals("multiple")) {
			mcd.setActive(getPanConfig(typ).getTb_active().isSelected());
		}

		mcd.setTyp(typ);
		return mcd;
	}

	/**
	 * Funktion welche aufgerufen wird wenn der User das Netzwerk Interface in
	 * einem Sender uendert.
	 * 
	 * @param typ
	 *            Programmteil in welchem das Netzwerkinterface geuendert wurde.
	 */
	private void changeNetworkInterface(final Typ typ) {
		final PanelMulticastConfig configpart = getPanConfig(typ);
		final int selectedIndex = configpart.getTf_sourceIPaddress()
				.getSelectedIndex();

		if(selectedIndex != 0) {
			if((typ == Typ.L3_SENDER) || (typ == Typ.L3_RECEIVER)) {
				configpart.getPan_sourceIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L3SOURCE,
								BorderType.TRUE));
				if(typ == Typ.L3_SENDER) {
					input[0][1] = true;
				} else {
					input[1][1] = true;
				}
			} else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L2_SENDER)) {
				configpart.getPan_sourceIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L2SOURCE,
								BorderType.TRUE));
				if(typ == Typ.L2_SENDER) {
					input[2][1] = true;
				} else {
					input[3][1] = true;
				}
			}
		} else if((getSelectedRows(typ).length > 1)
				&& configpart.getCb_sourceIPaddress().getItemAt(0)
						.equals("...")) {
			if((typ == Typ.L3_SENDER) || (typ == Typ.L3_RECEIVER)) {
				configpart.getPan_sourceIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L3SOURCE,
								BorderType.TRUE));
				if(typ == Typ.L3_SENDER) {
					input[0][1] = true;
				} else {
					input[1][1] = true;
				}
			} else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L2_SENDER)) {
				configpart.getPan_sourceIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L2SOURCE,
								BorderType.TRUE));
				if(typ == Typ.L2_SENDER) {
					input[2][1] = true;
				} else {
					input[3][1] = true;
				}
			}
		} else {
			switch(typ) {
				case L3_SENDER:
					input[0][1] = false;
					break;
				case L3_RECEIVER:
					input[1][1] = false;
					break;
				case L2_SENDER:
					input[2][1] = false;
					break;
				case L2_RECEIVER:
					input[3][1] = false;
					break;
			}
			if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
				configpart.getPan_sourceIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L3SOURCE,
								BorderType.NEUTRAL));
			} else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L2_SENDER)) {
				configpart.getPan_sourceIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L2SOURCE,
								BorderType.NEUTRAL));
			}
		}
		checkInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn sich der Inhalt eines Textfelds im
	 * Konfigurations Panel aendert. Prueft ob alle eingaben korrekt sind.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Eingaben geprueft werden sollen.
	 */
	private void checkInput(final Typ typ) {

		switch(typ) {
			case L3_SENDER:
				if(input[0][0] && input[0][1] && input[0][2] && input[0][3]
						&& input[0][4] && input[0][5]) {
					getPanConfig(typ).getBt_enter().setEnabled(true);
				} else if(getSelectedRows(getSelectedTab()).length <= 1) {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				} else {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
			case L3_RECEIVER:
				if(input[1][0] && input[1][1] && input[1][2]) {
					getPanConfig(typ).getBt_enter().setEnabled(true);
				} else if(getSelectedRows(getSelectedTab()).length <= 1) {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				} else {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
			case L2_SENDER:
				if(input[2][0] && input[2][1] && input[2][4] && input[2][5]) {
					getPanConfig(typ).getBt_enter().setEnabled(true);
				} else if(getSelectedRows(getSelectedTab()).length <= 1) {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				} else {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
			case L2_RECEIVER:
				if(input[3][0] && input[3][1]) {
					getPanConfig(typ).getBt_enter().setEnabled(true);
				} else if(getSelectedRows(getSelectedTab()).length <= 1) {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				} else {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn die Eingaben der Textfelder des
	 * Konfigurations Panels zurueckgesetzt werden sollen.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Textfelder zurueckgesetzt werden
	 *            sollen.
	 */
	private void clearInput(final Typ typ) {
		if(initFinished) {
			// Only for L3
			if((typ == Typ.L3_SENDER) || (typ == Typ.L3_RECEIVER)) {
				getPanConfig(typ).getTf_groupIPaddress().setText(
						guidata.Default_L3_GroupIp);
				getPanConfig(typ).getTf_udp_port().setText(
						guidata.Default_L3_UdpPort);

				// Only L3 Sender
				if(typ == Typ.L3_SENDER) {
					getPanConfig(typ).getTf_ttl().setText(
							guidata.Default_L3_Ttl);
					getPanConfig(typ).getTf_packetrate().setText(
							guidata.Default_L3_PacketRateDesired);
					getPanConfig(typ).getTf_udp_packetlength().setText(
							guidata.Default_L3_PacketLength);
				}
			} else {
				getPanConfig(typ).getTf_groupIPaddress().setText(
						guidata.Default_L2_GroupMac);

				// Only L2 Sender
				if(typ == Typ.L2_SENDER) {
					getPanConfig(typ).getTf_packetrate().setText(
							guidata.Default_L2_PacketRateDesired);
					getPanConfig(typ).getTf_udp_packetlength().setText(
							guidata.Default_L2_PacketLength);
				}
			}
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0);
			getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);

			getPanConfig(typ).getTb_active().setSelected(false);
			getPanConfig(typ).getTb_active().setText(
					lang.getProperty("button.inactive"));
			getPanConfig(typ).getTb_active().setForeground(Color.red);
			getPanConfig(typ).getTf_groupIPaddress().requestFocusInWindow();

			/*
			 * easiest method to have the NetworkAdapter and PacketLength field
			 * set their states correcly
			 */
			docEventTFgrp(typ);
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn das Programm beendet wird. Sorgt
	 * fuer ein sauberes Beenden des Programms. (nicht immer mueglich)
	 */
	private void closeProgram() {
		// System.out.println("Shutting down GUI...");
		f.setVisible(false);
		// System.out.println("Cleanup...");
		mc.destroy();
		// System.out.println("Closing program...");
		System.exit(0);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Input des Group IP Adress Felds
	 * geuendert wurde.
	 * 
	 * @param typ
	 *            Programmteil in welchem das Group IP Adress Feld geuendert
	 *            wurde.
	 */
	private void docEventTFgrp(final Typ typ) {
		final boolean isIPv4 = InputValidator.checkMC_IPv4(getPanConfig(typ)
				.getTf_groupIPaddress().getText()) != null;
		final boolean isIPv6 = InputValidator.checkMC_IPv6(getPanConfig(typ)
				.getTf_groupIPaddress().getText()) != null;
		final boolean mac = InputValidator
				.checkMulticastGroup(getPanConfig(typ).getTf_groupIPaddress()
						.getText());

		if((typ == Typ.L2_SENDER) || (typ == Typ.L2_RECEIVER)) {
			if(getPanConfig(typ).getCb_sourceIPaddress().getItemCount() <= 1) {
				insertNetworkAdapters(typ);
			}

			if(mac) {
				getPanConfig(typ).getPan_groupIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L2GROUP,
								BorderType.TRUE));
				if(typ == Typ.L2_SENDER) {
					input[2][0] = true;
				} else {
					input[3][0] = true;
				}
			} else {
				getPanConfig(typ).getPan_groupIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L2GROUP,
								BorderType.FALSE));
				if(typ == Typ.L2_SENDER) {
					input[2][0] = false;
				} else {
					input[3][0] = false;
				}
			}
		} else if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
			if(isIPv4
					|| isIPv6
					|| ((getSelectedRows(typ).length > 1) && getPanConfig(typ)
							.getTf_groupIPaddress().getText().equals("..."))) {
				getPanConfig(typ).getPan_groupIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L3GROUP,
								BorderType.TRUE));
				/* Lade die richtigen Netzwerkinterfaces */
				if(isIPv4) {
					insertNetworkAdapters(typ, true);
				} else if(isIPv6) {
					insertNetworkAdapters(typ, false);
				}

				if(typ == Typ.L3_SENDER) {
					input[0][0] = true;
					getPanConfig(typ).getTf_udp_packetlength().setText("");
					getPanConfig(typ).getTf_udp_packetlength().setEnabled(true);
				} else {
					input[1][0] = true;
				}
			} else {
				getPanConfig(typ).getPan_groupIPaddress().setBorder(
						MiscBorder.getBorder(BorderTitle.L3GROUP,
								BorderType.FALSE));
				// insertNetworkAdapters(typ);
				if(typ == Typ.L3_SENDER) {
					input[0][0] = false;
					getPanConfig(typ).getTf_udp_packetlength()
							.setEnabled(false);
					getPanConfig(typ).getTf_udp_packetlength().setText(
							lang.getProperty("config.message.ipFirstShort"));
				} else {
					input[1][0] = false;
				}
				/*
				 * Netzwerkadapterliste leeren, da jetzt wieder v4 oder v6 sein
				 * kann
				 */
				getPanConfig(typ).getCb_sourceIPaddress().removeAllItems();
				getPanConfig(typ).getCb_sourceIPaddress().addItem(
						lang.getProperty("config.message.ipFirst"));

			}
		}
		if(getPanConfig(typ).getTf_groupIPaddress().getText()
				.equalsIgnoreCase("")
				&& (typ != Typ.L2_SENDER) && (typ != Typ.L2_RECEIVER)) {
			getPanConfig(typ).getPan_groupIPaddress().setBorder(
					MiscBorder.getBorder(BorderTitle.L3GROUP,
							BorderType.NEUTRAL));
			/* Netzwerkadapterliste leeren, da jetzt wieder v4 oder v6 sein kann */
			getPanConfig(typ).getCb_sourceIPaddress().removeAllItems();
			getPanConfig(typ).getCb_sourceIPaddress().addItem(
					lang.getProperty("config.message.ipFirst"));

			if(typ == Typ.L3_SENDER) {
				getPanConfig(typ).getTf_udp_packetlength().setEnabled(false);
				getPanConfig(typ).getTf_udp_packetlength().setText("IP first");
			}
		}
		checkInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Length Felds
	 * geuendert wurde.
	 * 
	 * @param typ
	 *            Programmteil in welchem das Packet Length Feld geuendert
	 *            wurde.
	 */
	private void docEventTFlength(final Typ typ) {
		final IPType ipTyp = NetworkAdapter.getAddressType(getPanConfig(typ)
				.getTf_groupIPaddress().getText());
		if((typ == Typ.L3_SENDER) && (ipTyp != null)) {
			if(((ipTyp == IPType.IPv4) && (InputValidator
					.checkIPv4PacketLength(getPanConfig(typ)
							.getTf_udp_packetlength().getText()) > 0))
					|| ((ipTyp == IPType.IPv6) && (InputValidator
							.checkIPv6PacketLength(getPanConfig(typ)
									.getTf_udp_packetlength().getText()) > 0))) {
				getPanConfig(typ).getPan_packetlength().setBorder(
						MiscBorder.getBorder(BorderTitle.LENGTH,
								BorderType.TRUE));
				input[0][5] = true;
			} else {
				getPanConfig(typ).getPan_packetlength().setBorder(
						MiscBorder.getBorder(BorderTitle.LENGTH,
								BorderType.FALSE));
				input[0][5] = false;
			}
		} else if(typ == Typ.L2_SENDER) {
			if(InputValidator.checkMMRPPacketLength(getPanConfig(typ)
					.getTf_udp_packetlength().getText()) > 0) {
				getPanConfig(typ).getPan_packetlength().setBorder(
						MiscBorder.getBorder(BorderTitle.LENGTH,
								BorderType.TRUE));
				input[2][5] = true;
			} else {
				getPanConfig(typ).getPan_packetlength().setBorder(
						MiscBorder.getBorder(BorderTitle.LENGTH,
								BorderType.FALSE));
				input[2][5] = false;
			}

		}

		if(getPanConfig(typ).getTf_udp_packetlength().getText()
				.equalsIgnoreCase("")) {
			getPanConfig(typ).getPan_packetlength().setBorder(
					MiscBorder
							.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Input des Port Felds geuendert
	 * wurde.
	 * 
	 * @param typ
	 *            Programmteil in welchem das Port Feld geuendert wurde.
	 */
	private void docEventTFport(final Typ typ) {
		if((InputValidator.checkPort(getPanConfig(typ).getTf_udp_port()
				.getText()) > 0)
				|| ((getSelectedRows(typ).length > 1) && getPanConfig(typ)
						.getTf_udp_port().getText().equals("..."))) {
			getPanConfig(typ).getPan_udp_port().setBorder(
					MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
			if(typ == Typ.L3_SENDER) {
				input[0][2] = true;
			} else if(typ == Typ.L3_RECEIVER) {
				input[1][2] = true;
			}
		} else {
			getPanConfig(typ).getPan_udp_port().setBorder(
					MiscBorder.getBorder(BorderTitle.PORT, BorderType.FALSE));
			if(typ == Typ.L3_SENDER) {
				input[0][2] = false;
			} else if(typ == Typ.L3_RECEIVER) {
				input[1][2] = false;
			}
		}

		if(getPanConfig(typ).getTf_udp_port().getText().equalsIgnoreCase("")) {
			getPanConfig(typ).getPan_udp_port().setBorder(
					MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Rate Felds
	 * geuendert wurde.
	 * 
	 * @param typ
	 *            Programmteil in welchem das Packet Rate Feld geuendert wurde.
	 */
	private void docEventTFrate(final Typ typ) {
		if((InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate()
				.getText()) > 0)
				|| ((getSelectedRows(typ).length > 1) && getPanConfig(typ)
						.getTf_packetrate().getText().equals("..."))) {
			getPanConfig(typ).getPan_packetrate().setBorder(
					MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
			if(typ == Typ.L3_SENDER) {
				input[0][4] = true;
			} else if(typ == Typ.L2_SENDER) {
				if(InputValidator.checkPacketRate(getPanConfig(typ)
						.getTf_packetrate().getText()) > 0) {
					input[2][4] = true;
				}
			}
		} else {
			getPanConfig(typ).getPan_packetrate().setBorder(
					MiscBorder.getBorder(BorderTitle.RATE, BorderType.FALSE));
			if(typ == Typ.L3_SENDER) {
				input[0][4] = false;
			} else if(typ == Typ.L2_SENDER) {
				input[2][4] = false;
			}
		}
		if(getPanConfig(typ).getTf_packetrate().getText().equalsIgnoreCase("")) {
			getPanConfig(typ).getPan_packetrate().setBorder(
					MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Input des TTL Felds geuendert
	 * wurde.
	 * 
	 * @param typ
	 *            Programmteil in welchem das TTL Feld geuendert wurde.
	 */
	private void docEventTFttl(final Typ typ) {
		if((InputValidator.checkTimeToLive(getPanConfig(typ).getTf_ttl()
				.getText()) > 0)
				|| ((getSelectedRows(typ).length > 1) && getPanConfig(typ)
						.getTf_ttl().getText().equals("..."))) {
			getPanConfig(typ).getPan_ttl().setBorder(
					MiscBorder.getBorder(BorderTitle.TTL, BorderType.TRUE));
			if(typ == Typ.L3_SENDER) {
				input[0][3] = true;
			}
		} else {
			getPanConfig(typ).getPan_ttl().setBorder(
					MiscBorder.getBorder(BorderTitle.TTL, BorderType.FALSE));
			if(typ == Typ.L3_SENDER) {
				input[0][3] = false;
			}
		}
		if(getPanConfig(typ).getTf_ttl().getText().equalsIgnoreCase("")) {
			getPanConfig(typ).getPan_ttl().setBorder(
					MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn die Fenstergroesse geaendert wurde.
	 * Passt die Komponenten der GUI auf die neue Groesse an.
	 */
	private void frameResizeEvent() {
		if(getSelectedTab() != Typ.UNDEFINED) {
			if(getFrame().getSize().width > 1000) {
				getTable(getSelectedTab()).setAutoResizeMode(
						JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			} else {
				getTable(getSelectedTab()).setAutoResizeMode(
						JXTable.AUTO_RESIZE_OFF);
			}
		}
		if((getSelectedTab() == Typ.L2_RECEIVER)
				|| (getSelectedTab() == Typ.L3_RECEIVER)) {
			if(((ReceiverGraph)getPanTabbed(getSelectedTab()).getPan_graph())
					.isVisible()
					&& getPanTabbed(getSelectedTab()).getTab_console()
							.isVisible()) {
				((ReceiverGraph)getPanTabbed(getSelectedTab()).getPan_graph())
						.resize(new Dimension(getFrame().getSize().width - 262,
								100));
			}
		} else if((getSelectedTab() == Typ.L2_SENDER)
				|| (getSelectedTab() == Typ.L3_SENDER)) {
			if(getPanTabbed(getSelectedTab()).getPan_graph().isVisible()
					&& getPanTabbed(getSelectedTab()).getTab_console()
							.isVisible()) {
				getPanTabbed(getSelectedTab()).getPan_graph().resize(
						new Dimension(getFrame().getSize().width - 262, 100));
			}
		}
	}

	/**
	 * Hilfsfunktion welche das Configuration Panel eines bestimmten
	 * Programmteils zurueckgibt.
	 * 
	 * @param typ
	 *            Programmteil in welchem sich das Configuration Panel befindet.
	 * @return PnaleMulticastConfig, welches angefordert wurde.
	 */
	private PanelMulticastConfig getPanConfig(final Typ typ) {
		PanelMulticastConfig configpart = null;
		switch(typ) {
			case L2_SENDER:
				configpart = f.getPanel(1, 0).getPan_config();
				break;
			case L2_RECEIVER:
				configpart = f.getPanel(0, 0).getPan_config();
				break;
			case L3_SENDER:
				configpart = f.getPanel(1, 1).getPan_config();
				break;
			case L3_RECEIVER:
				configpart = f.getPanel(0, 1).getPan_config();
				break;
		}
		return configpart;
	}

	/**
	 * Hilfsfunktion welche das Control Panel eines bestimmten Programmteils
	 * zurueckgibt.
	 * 
	 * @param typ
	 *            Programmteil in welchem sich das Control Panel befindet.
	 * @return PanelMulticastControl, welches angefordert wurde.
	 */
	private PanelMulticastControl getPanControl(final Typ typ) {
		PanelMulticastControl controlpart = null;
		switch(typ) {
			case L2_SENDER:
				controlpart = f.getPanel(1, 0).getPan_control();
				break;
			case L2_RECEIVER:
				controlpart = f.getPanel(0, 0).getPan_control();
				break;
			case L3_SENDER:
				controlpart = f.getPanel(1, 1).getPan_control();
				break;
			case L3_RECEIVER:
				controlpart = f.getPanel(0, 1).getPan_control();
				break;
		}
		return controlpart;
	}

	/**
	 * Hilfsfunktion welche die Statusbar des jeweiligen Programmteils zurueck
	 * gibt.
	 * 
	 * @param typ
	 *            Programmteil in welchem sich die Statusbar befindet.
	 * @return PanelStatusbar welche angefordert wurde.
	 */
	private PanelStatusBar getPanStatus(final Typ typ) {
		PanelStatusBar statusbarpart = null;
		switch(typ) {
			case L2_SENDER:
				statusbarpart = f.getPanel(1, 0).getPan_status();
				break;
			case L2_RECEIVER:
				statusbarpart = f.getPanel(0, 0).getPan_status();
				break;
			case L3_SENDER:
				statusbarpart = f.getPanel(1, 1).getPan_status();
				break;
			case L3_RECEIVER:
				statusbarpart = f.getPanel(0, 1).getPan_status();
				break;
		}
		return statusbarpart;
	}

	/**
	 * Funktion welche aufgerufen wird wenn Hide im PopupMenu des Tabellenkopfs
	 * gedrueckt wurde
	 */
	private void hideColumnClicked() {
		getUserInputData(getSelectedTab()).hideColumn(
				PopUpMenu.getSelectedColumn());
	}

	/**
	 * Funktion, welche die ComboBox mit den richtigen Netzwerkadaptern fuellt.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Box geupdated werden soll.
	 * @param isIPv4
	 *            true bei IPv4 und false bei IPv6
	 */
	private void insertNetworkAdapters(final Typ typ, final boolean isIPv4) {
		final WideComboBox cbSrc = getPanConfig(typ).getCb_sourceIPaddress();

		/* Als erstes die CB leeren */
		cbSrc.removeAllItems();
		cbSrc.addItem("");

		Vector<InetAddress> temp = null;
		if(isIPv4) {
			temp = NetworkAdapter.getipv4Adapters();

		} else {
			temp = NetworkAdapter.getipv6Adapters();
		}
		if(temp != null) {
			for(int i = 0; i < temp.size(); i++) {
				try {
					cbSrc.addItem(NetworkInterface
							.getByInetAddress(temp.get(i)).getDisplayName());
				} catch(final SocketException e) {
					// In this case the interface disappeared while running
					// MCastor,
					// So we commented this out and do nothing
					// Feel free to do anything here :D
					// e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Funktion welche ausgeluest wird wenn der User eine oder mehrere Zeilen in
	 * der Tabelle selektiert.
	 * 
	 * @param typ
	 *            Programmteil in welchem der User die Zeilen selektiert hat.
	 */
	private void listSelectionEventFired(final Typ typ) {
		PanelTabbed tabpart = null;
		switch(typ) {
			case L3_SENDER:
				tabpart = f.getPanel(1, 1);
				break;
			case L3_RECEIVER:
				tabpart = f.getPanel(0, 1);
				break;
			case L2_SENDER:
				tabpart = f.getPanel(1, 0);
				break;
			case L2_RECEIVER:
				tabpart = f.getPanel(0, 0);
				break;
		}

		final int[] selectedRows = tabpart.getTable().getSelectedRows();
		for(int i = 0; i < selectedRows.length; i++) {
			selectedRows[i] = tabpart.getTable().convertRowIndexToModel(selectedRows[i]);
		}
		tabpart.getPan_status()
				.getLb_multicasts_selected()
				.setText(
						selectedRows.length + " "
								+ lang.getProperty("status.mcSelected") + " ");
		if(selectedRows.length > 1) {
			multipleSelect(typ);
		}
		if(selectedRows.length == 1) {
			tabpart.getPan_config().getBt_enter()
					.setText(lang.getProperty("button.change"));
			tabpart.getPan_config().getTf_groupIPaddress().setEnabled(true);
			if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
				tabpart.getPan_config()
						.getTf_groupIPaddress()
						.setText(
								getMCData(selectedRows[0], typ).getGroupIp()
										.toString().substring(1));
				tabpart.getPan_config()
						.getTf_udp_port()
						.setText(
								""
										+ getMCData(selectedRows[0], typ)
												.getUdpPort());
				tabpart.getPan_config().getTf_udp_port().setEnabled(true);

				final IPType iptype = NetworkAdapter.getAddressType(tabpart
						.getPan_config().getTf_groupIPaddress().getText());
				if(iptype == IPType.IPv4) {
					insertNetworkAdapters(typ, true);
				} else if(iptype == IPType.IPv6) {
					insertNetworkAdapters(typ, false);
				}
				tabpart.getPan_config()
						.getTf_sourceIPaddress()
						.setSelectedIndex(
								NetworkAdapter.findAddressIndex(getMCData(
										selectedRows[0], typ).getSourceIp()
										.toString().substring(1)) + 1);
			} else {
				tabpart.getPan_config()
						.getTf_groupIPaddress()
						.setText(
								getMCData(selectedRows[0], typ)
										.getMmrpGroupMacAsString());
				insertNetworkAdapters(typ);
				tabpart.getPan_config()
						.getTf_sourceIPaddress()
						.setSelectedIndex(
								NetworkAdapter.findAddressIndexMMRP(Arrays
										.toString(getMCData(selectedRows[0],
												typ).getMmrpSourceMac())));
			}

			if(typ == Typ.L2_SENDER) {
				tabpart.getPan_config()
						.getTf_packetrate()
						.setText(
								""
										+ getMCData(selectedRows[0], typ)
												.getPacketRateDesired());
				tabpart.getPan_config()
						.getTf_udp_packetlength()
						.setText(
								""
										+ getMCData(selectedRows[0], typ)
												.getPacketLength());
			}
			if(typ == Typ.L3_SENDER) {
				tabpart.getPan_config()
						.getTf_packetrate()
						.setText(
								""
										+ getMCData(selectedRows[0], typ)
												.getPacketRateDesired());
				tabpart.getPan_config().getTf_ttl()
						.setText("" + getMCData(selectedRows[0], typ).getTtl());
				;
				tabpart.getPan_config()
						.getTf_udp_packetlength()
						.setText(
								""
										+ getMCData(selectedRows[0], typ)
												.getPacketLength());
				;
			}
			setTBactive(selectedRows, typ);
			setBTStartStopDelete(typ);
		} else if(selectedRows.length == 0) {
			clearInput(typ);
			tabpart.getPan_config().getBt_enter()
					.setText(lang.getProperty("button.add"));
			tabpart.getPan_config().getTf_groupIPaddress().setEnabled(true);
			if(typ == Typ.L3_SENDER) {
				tabpart.getPan_config().getTf_sourceIPaddress()
						.setEnabled(true);
			}
			tabpart.getPan_config().getTf_udp_port().setEnabled(true);
			setTBactive(selectedRows, typ);
		}
	}

	/**
	 * Funktion welche ausgeloest wird, wenn der User versucht eine
	 * Konfigurationsdatei mit dem "Datei Laden" Dialog zu laden.
	 */
	private void loadFileEvent(final boolean incremental) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files",
				"xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());

		final int ret = chooser.showOpenDialog(f);

		if(ret == JFileChooser.APPROVE_OPTION) {
			loadMulticastConfig(chooser.getSelectedFile().toString(),
					incremental);
		}
	}

	/**
	 * Funktion welche ausgeloest wird, wenn der User versucht ein GUI
	 * Konfigurationsdatei mit dem "Konfiguration Laden" Dialog zu laden.
	 */
	private void loadGUIFileEvent() {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files",
				"xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		final int ret = chooser.showOpenDialog(f);
		if(ret == JFileChooser.APPROVE_OPTION) {
			mc.loadGUIConfig(chooser.getSelectedFile().toString(), false);
		}
	}

	private void loadMulticastConfig(final String path,
			final boolean incremental) {
		if(!incremental) {
			deleteAllMulticasts(Typ.L3_SENDER);
			deleteAllMulticasts(Typ.L2_SENDER);
			deleteAllMulticasts(Typ.L3_RECEIVER);
			deleteAllMulticasts(Typ.L2_RECEIVER);
		}
		mc.loadMulticastConfig(path, false);
	}

	/**
	 * Funktion welche aufgerufen wird wenn mehr als ein Multicast in der
	 * Tabelle selektiert wurde. Passt die GUI entsprechend an.
	 * 
	 * @param typ
	 *            Programmteil in welchem mehrere Multicasts Selektiert wurden.
	 */
	private void multipleSelect(final Typ typ) {
		/*
		 * pruefe, ob v4 und v6 gleichzeitig vorkommen, dann darf kein Network
		 * Adapter ausgewaehlt werden
		 */
		boolean ipv4 = false;
		boolean ipv6 = false;

		if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
			final int[] rows = getSelectedRows(typ);
			for(final int row : rows) {
				if(InputValidator.checkIPv4((String)getTable(typ).getModel()
						.getValueAt(row, 3)) != null) {
					ipv4 = true;
				} else if(InputValidator.checkIPv6((String)getTable(typ)
						.getModel().getValueAt(row, 3)) != null) {
					ipv6 = true;
				}
			}
		}

		getPanConfig(typ).getBt_enter().setText(
				lang.getProperty("button.changeAll"));
		if(typ == Typ.L3_SENDER) {
			getPanConfig(typ).getTf_sourceIPaddress().removeItemListener(this);
			getPanConfig(typ).getTf_packetrate().getDocument()
					.removeDocumentListener(this);
			getPanConfig(typ).getTf_udp_packetlength().getDocument()
					.removeDocumentListener(this);
			getPanConfig(typ).getTf_ttl().getDocument()
					.removeDocumentListener(this);
			getPanConfig(typ).getPan_sourceIPaddress()
					.setBorder(
							MiscBorder.getBorder(BorderTitle.L3SOURCE,
									BorderType.TRUE));
			getPanConfig(typ).getPan_packetrate().setBorder(
					MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
			getPanConfig(typ).getPan_packetlength().setBorder(
					MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
			getPanConfig(typ).getPan_ttl().setBorder(
					MiscBorder.getBorder(BorderTitle.TTL, BorderType.TRUE));
			getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
			getPanConfig(typ).getTf_packetrate().setText("...");
			getPanConfig(typ).getTf_udp_packetlength().setText("...");
			getPanConfig(typ).getTf_ttl().setText("...");
			getPanConfig(typ).getTf_sourceIPaddress().addItemListener(this);
			getPanConfig(typ).getTf_packetrate().getDocument()
					.addDocumentListener(this);
			getPanConfig(typ).getTf_udp_packetlength().getDocument()
					.addDocumentListener(this);
			getPanConfig(typ).getTf_ttl().getDocument()
					.addDocumentListener(this);

			if(ipv4 && ipv6) {
				getPanConfig(typ).getCb_sourceIPaddress().removeAllItems();
			} else if(ipv4 && !ipv6) {
				insertNetworkAdapters(typ, true);
				getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			} else if(!ipv4 && ipv6) {
				insertNetworkAdapters(typ, false);
				getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			}
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("...", 0);
			getPanConfig(typ).getCb_sourceIPaddress().setSelectedIndex(0);
		}
		getPanConfig(typ).getTf_groupIPaddress().getDocument()
				.removeDocumentListener(this);
		getPanConfig(typ).getTf_udp_port().getDocument()
				.removeDocumentListener(this);
		getPanConfig(typ).getPan_groupIPaddress().setBorder(
				MiscBorder.getBorder(BorderTitle.L3GROUP, BorderType.TRUE));
		getPanConfig(typ).getPan_udp_port().setBorder(
				MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
		getPanConfig(typ).getTf_groupIPaddress().setText("...");
		getPanConfig(typ).getTf_udp_port().setText("...");
		getPanConfig(typ).getTb_active().setText("multiple");
		getPanConfig(typ).getTb_active().setForeground(Color.green);
		getPanConfig(typ).getTb_active().setSelected(false);
		getPanConfig(typ).getTf_groupIPaddress().getDocument()
				.addDocumentListener(this);
		getPanConfig(typ).getTf_udp_port().getDocument()
				.addDocumentListener(this);
		getPanConfig(typ).getBt_enter().setEnabled(true);
		setBTStartStopDelete(typ);
	}

	/**
	 * Funktion die aufgerufen wird wenn eine Checkbox im Popup Menu des
	 * Tabellenkopfs gedrueckt wird. Wird verwendet zum Einblenden und
	 * Ausblenden von Tabellenspalten.
	 */
	private void popUpCheckBoxPressed() {
		final ArrayList<Integer> visibility = new ArrayList<Integer>();
		final UserInputData store = new UserInputData();
		store.setColumnOrder(getUserInputData(getSelectedTab())
				.getColumnOrder());
		final JCheckBox[] columns = PopUpMenu.getColumns();
		for(final JCheckBox column : columns) {
			if(column.isSelected()) {
				final String s = column.getText();
				if(s.equals("STATE")) {
					visibility.add(new Integer(0));
				} else if(s.equals("ID")) {
					visibility.add(new Integer(1));
				} else if(s.equals("GRP IP")) {
					visibility.add(new Integer(2));
				} else if(s.equals("D RATE")) {
					visibility.add(new Integer(3));
				} else if(s.equals("M RATE")) {
					visibility.add(new Integer(4));
				} else if(s.equals("Mbit/s")) {
					visibility.add(new Integer(5));
				} else if(s.equals("PORT")) {
					visibility.add(new Integer(6));
				} else if(s.equals("SRC IP") || s.equals("LOSS/S")) {
					visibility.add(new Integer(7));
				} else if(s.equals("#SENT") || s.equals("AVG INT")) {
					visibility.add(new Integer(8));
				} else if(s.equals("#INT") || s.equals("TTL")) {
					visibility.add(new Integer(9));
				} else if(s.equals("SRC") || s.equals("LENGTH")) {
					visibility.add(new Integer(10));
				}
			}
		}
		store.setColumnVisibility(visibility);
		setColumnSettings(store, getSelectedTab());
	}

	/**
	 * Funktion welche aufgerufen wird wenn der User Reset View im Popup Menu
	 * des Tabellenkopf drueckt. Stellt das urspruengliche Aussehen der Tabelle
	 * wieder her.
	 */
	private void popUpResetColumnsPressed() {
		getUserInputData(getSelectedTab()).resetColumns();
		getPanTabbed(getSelectedTab()).setTableModel(this, getSelectedTab());
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedrueckt wird.
	 * 
	 * @param typ
	 *            Programmteil in welchem der Add Button gedrueckt wurde
	 */
	private void pressBTAdd(final Typ typ) {
		final IPType iptype = NetworkAdapter.getAddressType(getPanConfig(typ)
				.getTf_groupIPaddress().getText());
		addMC(changeMCData(new MulticastData(), typ, iptype));
		clearInput(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Change Button gedrueckt wird.
	 * Bei selektierten Multicast(s).
	 * 
	 * @param typ
	 *            Programmteil in welchem der Change Button gedrueckt wurde
	 */
	private void pressBTChange(final Typ typ) {
		IPType iptype = null;
		final int[] selectedList = getSelectedRows(typ);
		if(selectedList.length == 1) {
			iptype = NetworkAdapter.getAddressType(getPanConfig(typ)
					.getTf_groupIPaddress().getText());
			final MulticastData mcd = getMCData(selectedList[0], typ);
			changeMC(changeMCData(mcd, typ, iptype));
			getTable(typ).getSelectionModel().setSelectionInterval(0, 0);
		} else {
			final PanelMulticastConfig config = getPanConfig(typ);
			if((typ == Typ.L3_SENDER)
					&& (config.getTf_groupIPaddress().getText().equals("..."))
					&& (config.getCb_sourceIPaddress().getSelectedIndex() == 0)
					&& (config.getTf_udp_port().getText().equals("..."))
					&& (config.getTf_ttl().getText().equals("..."))
					&& (config.getTf_packetrate().getText().equals("..."))
					&& (config.getTf_udp_packetlength().getText().equals("..."))
					&& (config.getTf_udp_port().getText().equals("..."))
					&& (config.getTb_active().getText().equals("multiple"))) {
				showMessage(MessageTyp.INFO,
						"No changes were made.\n\"...\" keeps old values!");
			} else if((typ == Typ.L3_RECEIVER)
					&& (config.getTf_groupIPaddress().getText().equals("..."))
					&& (config.getTf_udp_port().getText().equals("..."))
					&& (config.getTb_active().getText().equals("multiple"))) {
				showMessage(MessageTyp.INFO,
						"No changes were made.\n\"...\" keeps old values!");
			} else {
				for(int i = selectedList.length - 1; i >= 0; i--) {
					final MulticastData mcd = getMCData(selectedList[i]
							+ ((selectedList.length - 1) - i), typ);
					if(config.getTf_groupIPaddress().getText().equals("...")) {
						if(InputValidator.checkIPv6((String)getTable(typ)
								.getModel().getValueAt(selectedList[i], 3)) != null) {
							changeMC(changeMCData(mcd, typ, IPType.IPv6));
						} else if(InputValidator
								.checkIPv4((String)getTable(typ).getModel()
										.getValueAt(selectedList[i], 3)) != null) {
							changeMC(changeMCData(mcd, typ, IPType.IPv4));
						} else {
							/* MMRP */
							changeMC(changeMCData(mcd, typ, null));
						}
					} else {
						if(InputValidator.checkMC_IPv6(config
								.getTf_groupIPaddress().getText()) != null) {
							changeMC(changeMCData(mcd, typ, IPType.IPv6));
						} else if(InputValidator.checkMC_IPv4(config
								.getTf_groupIPaddress().getText()) != null) {
							changeMC(changeMCData(mcd, typ, IPType.IPv4));
						} else {
							/* MMRP */
							changeMC(changeMCData(mcd, typ, null));
						}
					}

				}
				getTable(typ).getSelectionModel().setSelectionInterval(0,
						selectedList.length - 1);
			}
			setBTStartStopDelete(getSelectedTab());
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Delete Button gedrueckt wird.
	 * 
	 * @param typ
	 *            Programmteil in welchem der Delete Button gedrueckt wurde
	 */
	private void pressBTDelete(final Typ typ) {
		final int[] selectedRows = getTable(typ).getSelectedRows();
		for(int i = 0; i < selectedRows.length; i++) {
			selectedRows[i] = getTable(typ).convertRowIndexToModel(selectedRows[i]);
		}
		for(int i = 0; i < selectedRows.length; i++) {
			// System.out.println("luesche zeile: "+selectedRows[i]);
			deleteMC(getMCData(selectedRows[i] - i, typ));
		}
		setBTStartStopDelete(typ);
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedrueckt wird. Diese
	 * Funktion unterscheided ob eine uenderung an einem Multicast stattfinden
	 * soll, oder ein Neuer angelegt werden soll.
	 * 
	 * @param typ
	 *            Programmteil in welchem der Add Button gedrueckt wurde
	 */
	private void pressBTenter(final Typ typ) {
		if(getPanConfig(typ).getBt_enter().getText()
				.equals(lang.getProperty("button.add"))) {
			pressBTAdd(typ);
		} else {
			pressBTChange(typ);
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn der New Button gedrueckt wird.
	 * 
	 * @param typ
	 *            Programmteil in welchem der New Button gedrueckt wurde
	 */
	private void pressBTNewMC(final Typ typ) {
		clearInput(typ);
		getTable(typ).clearSelection();
		setBTStartStopDelete(typ);
		getPanStatus(typ).getLb_multicasts_selected().setText(
				"0 " + lang.getProperty("status.mcSelected") + "");
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Select All Button gedrueckt
	 * wird.
	 * 
	 * @param typ
	 *            Programmteil in welchem der Select All Button gedrueckt wurde
	 * @param deselectPossible
	 *            Bestimmt, ob, wenn alle Zeilen selektiert sind, alle
	 *            deselektiert werden sollen
	 */
	private void pressBTSelectAll(final Typ typ, final boolean deselectPossible) {
		/*
		 * If all rows are already selected, deselect all which is the same
		 * functionality like pressing new
		 */
		if((getSelectedRows(typ).length == getTable(typ).getRowCount())
				&& deselectPossible) {
			pressBTNewMC(typ);
		} else {
			getTable(typ).selectAll();
			if(getSelectedRows(typ).length > 1) {
				multipleSelect(typ);
			}
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Start Button gedrueckt wird.
	 * 
	 * @param typ
	 *            Programmteil in welchem der Start Button gedrueckt wurde
	 */
	// TODO FIXME Sortierung
	private void pressBTStartStop(final Typ typ) {
		final int[] selectedLine = getSelectedRows(typ);	
		boolean oneActive = false;

		if(selectedLine.length == 1) {
			if(!getMCData(selectedLine[0], typ).isActive()) {
				startMC(selectedLine[0], typ);
				updateTable(typ, UpdateTyp.UPDATE);
			} else {
				if(getMCData(selectedLine[0], typ).isActive()) {
					stopMC(selectedLine[0], typ);
					updateTable(typ, UpdateTyp.UPDATE);
				}
			}
		} else if(selectedLine.length > 1) {
			for(final int element : selectedLine) {
				if(getMCData(element, typ).isActive()) {
					oneActive = true;
				}
			}
			/* if at least one is active, deactivate all otherwise activate all */
			if(oneActive) {
				for(final int element : selectedLine) {
					if(getMCData(element, typ).isActive()) {
						stopMC(element, typ);
						updateTable(typ, UpdateTyp.UPDATE);
					}
				}
			} else {
				for(int i = 0; i < selectedLine.length; i++) {
					if(!getMCData(selectedLine[i], typ).isActive()) {
						startMC(selectedLine[i], typ);
						updateTable(typ, UpdateTyp.UPDATE);
					}
				}
			}
		}
		setBTStartStopDelete(typ);

	}

	/**
	 * Removed einen Container und einen Keylistener vom Component c. Hat die
	 * Komponente Kinder, mache das gleiche mit jedem Child
	 * 
	 * @param c
	 *            Container, von dem die Listener entfernt werden sollen
	 */
	private void removeKeyAndContainerListenerToAll(final Component c) {
		c.removeKeyListener(this);
		if(c instanceof Container) {
			final Container cont = (Container)c;
			cont.removeContainerListener(this);
			final Component[] children = cont.getComponents();
			for(final Component element : children) {
				removeKeyAndContainerListenerToAll(element);
			}
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn versucht wird eine Datei zu
	 * speichern im Datei speichern Dialog.
	 * 
	 * @param e
	 *            ActionEvent welches vom Datei speichern Dialog erzeugt wird
	 */
	private void saveFileEvent(final boolean partiell) {
		// Create the Save Dialog
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files",
				"xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		// show the save dialog
		final int ret = chooser.showSaveDialog(f);
		// save the file
		if(ret == JFileChooser.APPROVE_OPTION) {
			if(!partiell) {
				final Vector<MulticastData> v = new Vector<MulticastData>();
				v.addAll(mc.getMCs(Typ.L2_RECEIVER));
				v.addAll(mc.getMCs(Typ.L3_RECEIVER));
				v.addAll(mc.getMCs(Typ.L2_SENDER));
				v.addAll(mc.getMCs(Typ.L3_SENDER));
				mc.saveMulticastConfig(chooser.getSelectedFile().getPath(), v);
			} else {
				final Vector<MulticastData> v = new Vector<MulticastData>();
				for(final int row : f.getPanel(0, 0).getTable()
						.getSelectedRows()) {
					v.add(mc.getMC(f.getPanel(0, 0).getTable().convertRowIndexToModel(row), Typ.L2_RECEIVER));
				}
				for(final int row : f.getPanel(0, 1).getTable()
						.getSelectedRows()) {
					v.add(mc.getMC(f.getPanel(0, 1).getTable().convertRowIndexToModel(row), Typ.L3_RECEIVER));
				}
				for(final int row : f.getPanel(1, 0).getTable()
						.getSelectedRows()) {
					v.add(mc.getMC(f.getPanel(1, 0).getTable().convertRowIndexToModel(row), Typ.L2_SENDER));
				}
				for(final int row : f.getPanel(1, 1).getTable()
						.getSelectedRows()) {
					v.add(mc.getMC(f.getPanel(1, 1).getTable().convertRowIndexToModel(row), Typ.L3_SENDER));
				}
				mc.saveMulticastConfig(chooser.getSelectedFile().getPath(), v);
			}
		}
	}

	/**
	 * Method is called for saving a GUI Config file
	 */
	private void saveGUIFileEvent() {
		// Create the Save Dialog
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files",
				"xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		// show the save dialog
		final int ret = chooser.showSaveDialog(f);
		// save the file
		if(ret == JFileChooser.APPROVE_OPTION) {
			mc.updateGUIData(guidata);
			mc.saveGUIConfig(chooser.getSelectedFile().getPath(), guidata);
		}
	}

	/**
	 * Funktion welche das Aussehen des Start Stop und Delete Buttons anpasst je
	 * nach dem welche Multicasts ausgewuehlt wurden.
	 * 
	 * @param typ
	 *            Programmteil in welchem die Buttons angepasst werden sollen.
	 */
	private void setBTStartStopDelete(final Typ typ) {
		final int[] selectedLine = getSelectedRows(typ);
		if(selectedLine.length == 1) {
			getPanControl(typ).getDelete().setEnabled(true);
			getPanControl(typ).getStartStop().setEnabled(true);
		} else if(selectedLine.length == 0) {
			getPanControl(typ).getStartStop().setEnabled(false);
			getPanControl(typ).getDelete().setEnabled(false);
		} else {
			getPanControl(typ).getDelete().setEnabled(true);
			getPanControl(typ).getStartStop().setEnabled(true);
		}
	}

	/**
	 * Funktion welche das aussehen des ActiveButtons anpasst (Togglefunktion)
	 * 
	 * @param b
	 *            Array welches die Selektierten Reihen in einem Programmteil
	 *            angibt
	 * @param typ
	 *            Programmteil in welchem sich der Active Button befindet
	 */
	private void setTBactive(final boolean b, final Typ typ) {
		if(b) {
			getPanConfig(typ).getTb_active().setSelected(true);
			getPanConfig(typ).getTb_active().setText(
					lang.getProperty("button.active"));
			getPanConfig(typ).getTb_active()
					.setForeground(new Color(0, 175, 0));
		} else {
			getPanConfig(typ).getTb_active().setSelected(false);
			getPanConfig(typ).getTb_active().setText(
					lang.getProperty("button.inactive"));
			getPanConfig(typ).getTb_active()
					.setForeground(new Color(200, 0, 0));
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn der Active Button im ControlPanel
	 * gedrueckt wird.
	 * 
	 * @param typ
	 *            Bestimmt den Programmteil in welchem der Active Button
	 *            gedrueckt wurde
	 */
	private void toggleBTactive(final Typ typ) {
		if(getPanConfig(typ).getTb_active().isSelected()) {
			setTBactive(true, typ);
		} else {
			setTBactive(false, typ);
		}
	}

	/**
	 * Funktion welche sich um das Update des Graphen kuemmert.
	 * 
	 * @param typ
	 *            bestimmt welcher Graph in welchem Programmteil geupdatet
	 *            werden soll
	 */
	private void updateGraph(final Typ typ) {
		// boolean variable which determines if graph can be painted or not, is
		// graph in front of SRC address dropdown
		boolean showupdate = false;
		// check if graph is selected
		if(getPanTabbed(typ).getTab_console().getSelectedIndex() == 0) {
			if(getPanTabbed(typ).getPan_graph().isVisible()
					&& getPanTabbed(typ).getTab_console().isVisible()) {
				if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
					showupdate = !getPanConfig(typ).getCb_sourceIPaddress()
							.isPopupVisible() && !PopUpMenu.isPopUpVisible();
				} else {
					showupdate = !PopUpMenu.isPopUpVisible();
				}
			} else {
				showupdate = false;
			}
		}
		// check which tab is selected and update graph for specific program
		// part
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			getPanTabbed(typ).getPan_graph().updateGraph(mc.getPPSSender(typ),
					showupdate);
		} else {
			graphData = new MulticastData[getSelectedRows(typ).length];
			for(int i = 0; i < getSelectedRows(typ).length; i++) {
				graphData[i] = mc.getMC(getSelectedRows(typ)[i], typ);
			}
			((ReceiverGraph)getPanTabbed(typ).getPan_graph()).updateGraph(
					graphData, showupdate);

		}
	}
}
