   
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
 
 package dhbw.multicastor.program.controller;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.interfaces.MulticastThreadSuper;
import dhbw.multicastor.program.interfaces.UIController;
import dhbw.multicastor.program.model.ConfigHandler;
import dhbw.multicastor.program.model.FileNames;
import dhbw.multicastor.program.model.LibraryHandler;
import dhbw.multicastor.program.model.MulticastLogHandler;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;
import dhbw.multicastor.program.model.MulticastReceiver;
import dhbw.multicastor.program.model.MulticastReceiverLayer3;
import dhbw.multicastor.program.model.MulticastSenderIgmpMld;
import dhbw.multicastor.program.model.RegularLoggingTask;
import dhbw.multicastor.program.model.UpdateTask;
import dhbw.multicastor.program.model.WorkbenchHandler;
import dhbw.multicastor.program.model.XmlParserWorkbench;
import dhbw.multicastor.program.model.mmrp.IMmrpSenderAndReceiver;


/**
 * Der MulticastController verwaltet Multicasts und abstrahiert das Speichern
 * und Laden von Konfigurationsdateien von der View.
 * 
 * @author Bastian Wagener
 * 
 */
public class MulticastController {

	public enum Modus {
		SENDER, RECEIVER, UNDEFINED, CONFIG
	}

	// ****************************************************
	// Felder fuer MultiCast Controller
	// ****************************************************

	/**
	 * In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte
	 * abgebildet um die Verbindung von MulticastData-Objektu zu
	 * MulticastThreadSuper herstellen zu koennen.
	 */
	/**
	 * Diese Map bildet MulticastData-Objekte auf Threads ab, um von einem
	 * Multicast direkt mit dem entsprechenden Thread kommunizieren zu koennen.
	 * Dies wird vor allem beim Beenden der Multicasts genutzt.
	 */
	private Map<MulticastData, Thread> threads;
	/**
	 * Der ViewController stellt eine Funktion zur Ausgabe von MessageBoxen
	 * bereit.
	 */
	private ViewController view_controller;

	/** Der Logger speichert Daten in einer Logdatei */
	private Logger logger;
	/**
	 * Der UpdateTask sorgt fuer Aktualisierungen in der Datenhaltung und der
	 * View.
	 */
	private UpdateTask updateTask;
	/** Referenz auf einen Timer fuer updateTask */
	private Timer timer1;
	/** Referenz auf einen Timer fuer messageCheck */
	private Timer timer2;

	/**
	 * Loesst eine Aktualisierung der Durchschnittswerte in der Datenhaltung aus
	 * und speichert diese mittels Logger.
	 */
	private RegularLoggingTask regularLoggingTask;

	/**
	 * Der Modus wird jetzt in dieser Klasse gehalten
	 */
	private Modus currentModus;

	private ConfigHandler config = null;
	private Vector<MulticastData> mcDataVectorSender;
	private Vector<MulticastData> mcDataVectorReceiver;

	private HashMap<MulticastData, MulticastThreadSuper> mcMap;

	private CLIController cli_controller;

	static StringBuffer cmd;

	/**
	 * Kontrolliert die gesamte MMRP-Kommunikation
	 */
	private MMRPController mmrp_controller;

	public MulticastController() {
		Logger logger = Logger
				.getLogger("dhbw.multicastor.program.controller.main");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);
		this.logger = logger;
		LibraryHandler libHandler = new LibraryHandler();
		libHandler.checkLibrary();

		this.setMmrp_controller(new MMRPController());
		// Thread t = new Thread(new ShutdownHook(this.logger, this));
		// // Registrierung
		// Runtime.getRuntime().addShutdownHook(t);
		currentModus = Modus.SENDER;

		mcDataVectorSender = new Vector<MulticastData>();
		mcDataVectorReceiver = new Vector<MulticastData>();

		mcMap = new HashMap<MulticastData, MulticastThreadSuper>();

		threads = new HashMap<MulticastData, Thread>();

	}

	public void registerUIController(UIController uiController) {
		config = new ConfigHandler(logger);
		if (uiController instanceof ViewController) {
			restoreModus();
			config.loadDefaultMulticastData();
			config.loadConfig(this);
			this.view_controller = (ViewController) uiController;
			MulticastLogHandler consoleHandlerWithGUI;
			consoleHandlerWithGUI = new MulticastLogHandler(
					this.view_controller);
			consoleHandlerWithGUI.setLevel(Level.FINEST);
			logger.addHandler(consoleHandlerWithGUI);
			logger.log(Level.INFO, "Starting MultiCastor with a gui");
			view_controller.initialize(this);
			
		}
		if (uiController instanceof CLIController) {
			this.view_controller = null;
			this.cli_controller = (CLIController) uiController;
			MulticastLogHandler consoleHandler;
			consoleHandler = new MulticastLogHandler();
			consoleHandler.setLevel(Level.FINEST);
			logger.addHandler(consoleHandler);
			cli_controller.initialize(this);
		}
		
		updateTask = new UpdateTask(logger, mcMap, view_controller,
				cli_controller);
		timer1 = new Timer();
		timer1.schedule(updateTask, 3000, 1000);

		if (cli_controller != null) {
			if (!cli_controller.isOneMulticast()) {
				regularLoggingTask = new RegularLoggingTask(logger, mcMap);
				timer2 = new Timer();
				timer2.schedule(regularLoggingTask, 30000, 30000);
			}
		}else {
			regularLoggingTask = new RegularLoggingTask(logger, mcMap);
			timer2 = new Timer();
			timer2.schedule(regularLoggingTask, 30000, 30000);
		}
		if(view_controller != null){
			view_controller.restoreSettings();
		}
	}

	private void restoreModus() {
		XmlParserWorkbench workbench = new XmlParserWorkbench(logger, null);
		workbench.loadWorkbench(FileNames.WORKBENCH);
		WorkbenchHandler handler = new WorkbenchHandler(null);
		setCurrentModus(handler.getSavedModus(workbench));
	}

	public void restartGui() {
		this.view_controller = new ViewController();
		view_controller.initialize(this);
	}

	/**
	 * Fuegt das uebergebene MulticastData-Objekt hinzu, erzeugt entsprechenden
	 * Thread und startet diesen falls notwendig.
	 * 
	 * @param m
	 *            MulticastData-Objekt das hinzugefï¿½gt werden soll.
	 */
	public void addMC(MulticastData m) {
		boolean check = true;
		if (m.getProtocolType().equals(ProtocolType.MMRP)) {
			check = checkIfInterfaceAlreadyInserted((MMRPData) m);
		}else{
			check = true;
		}
		if (check) {
			if (m.getMulticastID() == null) {
				m.setMulticastID(UUID.randomUUID());
			}
			// Erzeugt den passenden MulticastThreadSuper
			MulticastThreadSuper t = getMulticastThreadSuper(m);
			// Fuegt Multicasts zu der entsprechenden Map hinzu
			if (getCurrentModus().equals(Modus.SENDER)) {
				getMcDataVectorSender().add(0, m);
			} else {
				getMcDataVectorReceiver().add(0, m);
			}
			getMcMap().put(m, t);
			logger.log(Level.INFO, m.identify(), Event.ADDED);
			// Startet den Multicast, falls notwendig
			if (m.getActive()) {
				startMC(m);
			}
		}
	}

	public MulticastThreadSuper getMulticastThreadSuper(MulticastData m) {
		MulticastThreadSuper t = null;
		if (currentModus == Modus.SENDER) {
			if (m.getProtocolType().equals(ProtocolType.MMRP)) {
				t = mmrp_controller.createNewMulticastSenderMmrp((MMRPData) m,
						logger);
			} else {
				t = new MulticastSenderIgmpMld(m, logger);
			}

		} else if (currentModus == Modus.RECEIVER) {
			if (m.getProtocolType().equals(ProtocolType.MMRP)) {
				t = mmrp_controller.createNewMulticastReceiverMmrp(
						(MMRPData) m, logger);
			} else {
				t = new MulticastReceiverLayer3(m, logger);
			}
		}
		return t;
	}

	// ****************************************************
	// Multicast-Steuerung
	// ****************************************************

	/**
	 * Diese Methode muss aufgerufen werden, wenn sich Einstellungen des
	 * Multicasts aendern. Zum Beispeil die GroupIP. Hier wird der vorhandene
	 * Multicast geloescht und anschlieï¿½end der Multicast neu erzeugt.
	 * 
	 * @param m
	 *            Refernez auf das Datenobjekt, welches veraendert wurde.
	 */
	public void changeMC(MulticastData m) {
		// Loescht den Multicast und fuegt ihn neu hinzu. Dadurch werden
		// Sender/Receiver mit den neuen Einstellungen neu erzeugt.
		// Zwischenspeichern von active ist notwendig, da Multicasts beim
		// Loeschen gestoppt wird.
		boolean active = m.getActive();
		deleteMC(m);
		m.setActive(active);
		addMC(m);
	}

	/**
	 * Entfernt alle Multicasts im Vektor.
	 * 
	 * @param m
	 *            Vector mit zu entfernenden Multicasts.
	 */
	public void deleteMC(Vector<MulticastData> m) {
		for (MulticastData mc : m) {
			deleteMC(mc);
		}
	}

	/**
	 * Stoppt den zugehoerigen Thread und loescht alle Referenzen auf diesen,
	 * sowie das MulticastData-Objekt.
	 * 
	 * @param m
	 *            Data object to delete.
	 */
	public void deleteMC(MulticastData m) {
		if (m.getActive()) {
			stopMC(m);
		}

		if (getCurrentModus().equals(Modus.SENDER)) {
			getMcDataVectorSender().remove(m);
			if (m.getProtocolType().equals(ProtocolType.MMRP)) {
				this.mmrp_controller.deleteMulticastSenderMmrp((MMRPData) m);
			}

		} else {
			getMcDataVectorReceiver().remove(m);

			if (m.getProtocolType().equals(ProtocolType.MMRP)) {
				this.mmrp_controller.deleteMulticastReceiverMmrp((MMRPData) m);
			}
		}
		getMcMap().remove(m);
		logger.log(Level.INFO, m.toStringConsole(), Event.DELETED);
	}

	/**
	 * Stops all Multicasts Threads and removes them from corresponding vectors.
	 */
	public void destroy() {
		if (cli_controller != null) {
			if (!cli_controller.isOneMulticast()) {
				saveCompleteConfig();
			}
		} else {
			saveCompleteConfig();
		}
		// Disable all Multicasts
		for (Entry<MulticastData, MulticastThreadSuper> m : getMcMap()
				.entrySet()) {
			m.getValue().setActive(false);
			if(m.getKey() instanceof MMRPData){
				if(currentModus.equals(Modus.SENDER)) {
					mmrp_controller.deleteMulticastSenderMmrp((MMRPData)m.getKey());
				} else {
					mmrp_controller.deleteMulticastReceiverMmrp((MMRPData)m.getKey());
				}
			}
		}

		getMcDataVectorSender().removeAllElements();
		getMcDataVectorReceiver().removeAllElements();

		getMcMap().clear();
	}

	/**
	 * Gibt das MulticastData-Objekt an der Stelle im Vektor zurueck.
	 * 
	 * @param index
	 *            Index des MulticastData-Objekts
	 * 
	 * @return Das MulticastData-Objekt an der entsprechenden Stelle im Vektor.
	 *         Gibt <code>null</code> zurueck, wenn das Objekt nicht existiert.
	 */
	public MulticastData getMCByIndex(int index) {
		if (index < 0) {
			return null;
		}
		try {
			if (getCurrentModus().equals(Modus.SENDER)) {
				return this.mcDataVectorSender.get(index);
			} else {
				return this.mcDataVectorReceiver.get(index);
			}
		} catch (IndexOutOfBoundsException e) {
			logger.log(Level.SEVERE,
					"IndexOutOfBoundsException in MulticastController - getMC",
					Event.INFO);
			return null;
		}
	}

	/**
	 * 
	 * Adds up the measuredPacketRate from MulticastSenders
	 * 
	 * @param typ
	 *            Specifies wehter Sender_v4 or Sender_v6 is returned.
	 * @return Sum of all sent packets. Returns 0 if typ is invalid.
	 */
	public int getPPSSender(MulticastData mc) {
		if (mcDataVectorSender.contains(mc)) {
			return mc.getPacketRateMeasured();
		}
		return 0;
	}

	/**
	 * 
	 * Adds up the measuredPacketRate from MulticastSenders
	 * 
	 * @param typ
	 *            Specifies wehter Sender_v4 or Sender_v6 is returned.
	 * @return Sum of all sent packets. Returns 0 if typ is invalid.
	 */
	public int getPPSSender(ArrayList<MulticastData> mcs) {
		int counter = 0;
		for (MulticastData mc : mcs) {
			if (mcDataVectorSender.contains(mc)) {
				counter += mc.getPacketRateMeasured();
			}
		}
		return counter;
	}

	/**
	 * Laedt die Konfigurationsdatei am angegebenen Pfad. Fehlermeldungen werden
	 * ausgegeben. Diese Funktion sollte
	 * 
	 * @param s
	 *            String to Configuration file
	 */
	public void loadConfigFile(String s) {
		config.loadConfig(s, false, this);
		// loadConfig(s, false);
	}

	// ****************************************************
	// Other-Stuff
	// ****************************************************

	/**
	 * Speichert die Standardkonfigurationsdatei.
	 */
	private void saveCompleteConfig() {
		if(getCurrentModus()== Modus.SENDER){
			config.saveConfig("", true, getMcDataVectorSender());	
		}else if(getCurrentModus() == Modus.RECEIVER){
			config.saveConfig("", true, getMcDataVectorReceiver());
		}
	}

	public void saveCompleteConfig(String s) {
		if(getCurrentModus()== Modus.SENDER){
			config.saveConfig(s, false, getMcDataVectorSender());	
		}else if(getCurrentModus() == Modus.RECEIVER){
			config.saveConfig(s, false, getMcDataVectorReceiver());
		}
		
	}

	/**
	 * Speichert eine Konfigurationsdatei an den angegebenen Pfad. Hierbei
	 * werden nur die Multicasts von Typen mit uebergebenem True gespeichert.
	 * 
	 * @param s
	 *            Pfad zur Konfigurationsdatei
	 */
	public void saveConfig(String s, boolean igmp, boolean mld, boolean mmrp) {
		// Sammelt alle zu speichernden Multicasts in einem Vektor
		// Vector<MulticastData> v = new Vector<MulticastData>();
		// if(igmp){
		// v.addAll(mc_igmp);
		// }
		// if(mld){
		// v.addAll(mc_mld);
		// }
		// if(mmrp){
		// v.addAll(mc_mmrp);
		// }
		Vector<MulticastData> mc = new Vector<MulticastData>(
				getMcDataVectorReceiver());
		// (Vector<MulticastData>) getMcDataVectorReceiver()
		// .clone();
		mc.addAll(getMcDataVectorSender());
		config.saveConfig(s, false, mc);
	}

	public void sendLeaveAll(MulticastData m) {
		mmrp_controller.leaveAll(m);
	}

	public void sendLeave(MulticastData m) {
		mmrp_controller.leave(m);
	}

	/**
	 * Startet den Multicast und gibt eine Fehlermeldung aus, wenn dies nicht
	 * moeglich ist.
	 * 
	 * @param m
	 *            MulticastData-Objekt des zu startenden Multicasts.
	 */
	public void startMC(MulticastData m) {
		// writeConfig();
		// System.out.println("writeConfig");
		synchronized (m) { 
			if (!threads.containsKey(m)) { // prueft ob der Multicast schon
				// laeuft.
				long time = System.currentTimeMillis() + 2000; // versucht, 2
				// Sekunden lang
				// auf den noch
				// laufenden
				// Thread zu
				// warten.
				while ((getMcMap().get(m)).isStillRunning()) {
					if (time < System.currentTimeMillis()) { // verhindert
						logger.log(Level.SEVERE, "Could not start Multicast: "
								+ m + " Advised a Change on that Multicast.",
								Event.WARNING);
						return;
					}
				}

				// Beim Receiver wird der Wert aus dem Datenpaket ausgelesen.
				if (currentModus != Modus.SENDER) { // Receiver haben den
													// GroupJoin ausgelagert.
				((MulticastReceiver) getMcMap().get(m)).joinGroup();
				}
				// Multicast auf aktiv setzen, einen neuen Thread erzeugen und
				// starten.
				getMcMap().get(m).setActive(true);
				Thread t = null;
				if (m.getProtocolType().equals(ProtocolType.MMRP)) {
					t = mmrp_controller.getSenderAndReceiver((MMRPData) m,
							logger, currentModus);
				} else {
					t = new Thread(getMcMap().get(m));
				}
				t.start();
				threads.put(m, t);
			} else {
				logger.log(Level.INFO,
						"Tried to start an already running Multicast.",
						Event.INFO);
			}
		}
	}

	/**
	 * Startet alle Multicasts aus dem uebergebenen Vektor.
	 * 
	 * @param m
	 *            Vektor mit MulticastData-Objekten.
	 */
	public void startMC(Vector<MulticastData> m) {
		for (MulticastData ms : m) {
			startMC(ms);
		}
	}

	/**
	 * Stoppt den uebergebenen Multicast. Der Thread wird nicht direkt gestoppt
	 * sondern laeuft von selber aus. Es wird gewartet bis der Thread sich
	 * gestoppt hat oder eine Fehlermeldung ausgegeben falls eine Interruption
	 * auftrit. Fehler oder auch das erfolgreiche Stoppen werden an die
	 * MesseageQueue angehaengt.
	 * 
	 * @param m
	 *            MulticastData-Objekt
	 */
	public void stopMC(MulticastData m) {
		if (threads.containsKey(m)) {
			// Der Receiver loggt selber; Fehler werden an in die MessageQueue
			// eingefuegt.
			getMcMap().get(m).setActive(false);
			/*
			 * try { // fuerht zu einer Blockierung der GUI
			 * threads.get(m).join(); } catch (InterruptedException e) {
			 * messageQueue.add(
			 * "[Fehler][MulticastController]Could not stop Multicast properly. This Multicast might not work anymore. In this case try to delete and add the Multicast."
			 * ); }
			 */
			if (m.getProtocolType().equals(ProtocolType.MMRP)) {
				mmrp_controller.stopMulticast((MMRPData) m, currentModus);
			}
			threads.remove(m);
		} else {

			logger.log(Level.INFO, "Tried to stop a not running Multicast.",
					Event.INFO);
		}
	}

	/**
	 * Stoppt alle Multicast im uebergebenen Vektor.
	 * 
	 * @param m
	 *            MulticastData-Objekt
	 */
	public void stopMC(Vector<MulticastData> m) {
		for (MulticastData ms : m) {
			stopMC(ms);
		}
	}

	/**
	 * castet alle Multicastdaten in anderen Modus
	 */
	private void castAllMulticastData() {
		// Status in den gewechselt wurde wird hier ï¿½berprï¿½ft
		if (!getCurrentModus().equals(Modus.SENDER)) {
			castToReceiver();
		}
	}

	private void castToReceiver() {
		boolean contains = false;
		Vector<MulticastData> receiver = getMcDataVectorReceiver();
		for (MulticastData mc : getMcDataVectorSender()) {
			for (MulticastData mcR : receiver) {
				if (mcR.getMulticastID() == mc.getMulticastID()) {
					contains = true;
				}
			}
			if (!contains) {
				MulticastData temp = mc.copy();
				temp.setActive(false);
				this.addMC(temp);
			}
		}
	}

	/*
	 * Found at http://java.dzone.com/articles/programmatically-restart-java
	 */
	public void restart() {
		// java binary
		String java = System.getProperty("java.home") + "/bin/java";
		// vm arguments
		List<String> vmArguments = ManagementFactory.getRuntimeMXBean()
				.getInputArguments();
		StringBuffer vmArgsOneLine = new StringBuffer();
		for (String arg : vmArguments) {
			// if it's the agent argument : we ignore it otherwise the
			// address of the old application and the new one will be in
			// conflict
			if (!arg.contains("-agentlib")) {
				vmArgsOneLine.append(arg);
				vmArgsOneLine.append(" ");
			}
		}
		// init the command to execute, add the vm args
		cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

		// program main and program arguments
		String[] mainCommand = System.getProperty("sun.java.command")
				.split(" ");
		// program main is a jar
		if (mainCommand[0].endsWith(".jar")) {
			// if it's a jar, add -jar mainJar
			cmd.append("-jar " + new File(mainCommand[0]).getPath());
		} else {
			// else it's a .class, add the classpath and mainClass
			cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" "
					+ mainCommand[0]);
		}
		// finally add program arguments
		for (int i = 1; i < mainCommand.length; i++) {
			cmd.append(" ");
			cmd.append(mainCommand[i]);
		}
		// execute the command in a shutdown hook, to be sure that all the
		// resources have been disposed before restarting the application
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {

					Runtime.getRuntime().exec(cmd.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// execute some custom code before restarting
		XmlParserWorkbench wbparser = new XmlParserWorkbench(this.logger,
				view_controller);
		wbparser.saveWorkbench(FileNames.WORKBENCH);
		// exit
		System.exit(0);
		logger.log(Level.INFO, "Multicastor terminated", Event.INFO);
	}

	public boolean checkIfInterfaceAlreadyInserted(MMRPData mmrp) {
		if (getCurrentModus().equals(Modus.SENDER)) {
			for (MulticastData mc : getMcDataVectorSender()) {
				if (mc.getType().equals(ProtocolType.MMRP)) {
					if (((MMRPData) mc).getMacSourceId().equals(
							mmrp.getMacSourceId())) {
						if(((MMRPData) mc).getMacGroupId().equals(
							mmrp.getMacGroupId())){
							return false;	
						}
						
					}
				}
			}
		} else {
			for (MulticastData mc : getMcDataVectorReceiver()) {
				if (mc.getType().equals(ProtocolType.MMRP)) {
					if (((MMRPData) mc).getMacSourceId().equals(
							mmrp.getMacSourceId())) {
						if(((MMRPData) mc).getMacGroupId().equals(
								mmrp.getMacGroupId())){
								return false;	
							}
					}
				}
			}
		}
		return true;
	}

	// **************************************************
	// Auto generated Getter and Setter
	// **************************************************

	public ConfigHandler getConfig() {
		return config;
	}

	public ViewController getView_controller() {
		return view_controller;
	}

	public Modus getCurrentModus() {
		return currentModus;
	}

	public void setCurrentModus(Modus currentModus) {
		this.currentModus = currentModus;
		// der gewï¿½nschte Modus muss vor dem Aufruf castAllMulticastData gesetzt
		// werden
		castAllMulticastData();
	}

	public Vector<MulticastData> getMcDataVectorSender() {
		return mcDataVectorSender;
	}

	public HashMap<MulticastData, MulticastThreadSuper> getMcMap() {
		return mcMap;
	}

	public Vector<MulticastData> getMcDataVectorReceiver() {
		return mcDataVectorReceiver;
	}

	public CLIController getCli_controller() {
		return cli_controller;
	}

	public Logger getLogger() {
		return logger;
	}

	public Map<MulticastData, Thread> getThreads() {
		return threads;
	}

	public MMRPController getMmrp_controller() {
		return mmrp_controller;
	}

	public void setMmrp_controller(MMRPController mmrp_controller) {
		this.mmrp_controller = mmrp_controller;
	}

	public RegularLoggingTask getRegularLoggingTask() {
		return regularLoggingTask;
	}

	public UpdateTask getUpdateTask() {
		return updateTask;
	}

}