package zisko.multicastor.program.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import zisko.multicastor.program.data.GUIData;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.interfaces.MulticastSenderInterface;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;
import zisko.multicastor.program.interfaces.XMLParserInterface;
import zisko.multicastor.program.lang.LanguageManager;
import zisko.multicastor.program.model.MulticastMmrpReceiver;
import zisko.multicastor.program.model.MulticastMmrpSender;
import zisko.multicastor.program.model.MulticastReceiver;
import zisko.multicastor.program.model.MulticastSender;
import zisko.multicastor.program.model.RegularLoggingTask;
import zisko.multicastor.program.model.RunSnakeRun;
import zisko.multicastor.program.model.UpdateTask;
import zisko.multicastor.program.model.WrongConfigurationException;
import zisko.multicastor.program.model.xmlParser;

/**
 * Der MulticastController verwaltet Multicasts und abstrahiert das Speichern
 * und Laden von Konfigurationsdateien von der View.
 */
public class MulticastController {

	// ****************************************************
	// Felder fuer MultiCast Controller
	// ****************************************************

	/* Ein Vector fuer Layer3 Receiver und Sender */
	private Vector<MulticastData> mc_sender_l3;
	private Vector<MulticastData> mc_receiver_l3;
	private Vector<MulticastData> mc_sender_l2;
	private Vector<MulticastData> mc_receiver_l2;

	/* Eine neue Map fuer Layer3 Receiver und Sender */
	private Map<MulticastData, MulticastThreadSuper> mcMap_receiver_l3;
	private Map<MulticastData, MulticastThreadSuper> mcMap_sender_l3;
	private Map<MulticastData, MulticastThreadSuper> mcMap_receiver_l2;
	private Map<MulticastData, MulticastThreadSuper> mcMap_sender_l2;

	/** LanguageManager provides access to language files **/
	private LanguageManager lang;

	/**
	 * Diese Map bildet MulticastData-Objekte auf Threads ab, um von einem
	 * Multicast direkt mit dem entsprechenden Thread kommunizieren zu koennen.
	 * Dies wird vor allem beim Beenden der Multicasts genutzt.
	 */
	private Map<MulticastData, Thread> threads;
	/**
	 * Interner Threadcounter, der beim Anlegen oder Starten eines
	 * MulticastSenders hochgezaehlt wird und diesem den Wert als ThreadID
	 * mitgibt.
	 */
	private int threadCounter = 0;
	/**
	 * Der ViewController stellt eine Funktion zur Ausgabe von MessageBoxen
	 * bereit.
	 */
	private ViewController view_controller;
	/**
	 * Der XMLParser wird zum Speichern und Laden von Konfigurationsdateien
	 * genutzt.
	 */
	private XMLParserInterface xml_parser;
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
	/** Referenz auf einen Timer fuer regularLoggingTask */
	private Timer timer3;
	/**
	 * Loesst eine Aktualisierung der Durchschnittswerte in der Datenhaltung aus
	 * und speichert diese mittels Logger.
	 */
	private RegularLoggingTask regularLoggingTask;

	private int printTableIntervall;

	/** Informationen zum wiederherstellen des letzten GUI Status */
	private Vector<UserInputData> userInputData;
	/** Haelt die Pfade als String auf die zuletzt geladenen Konfigurationsdateien. Dieser Vektor sollte vier Eintraenge enthalten.*/
	private Vector<String> lastConfigs;

	/**
	 * Erzeugt einen MulticastController.
	 * 
	 * @param viewController
	 *            Ist dieses Objekt nicht null werden darueber MessageBoxen mit
	 *            Status- oder Fehlermeldungen dem Nutzer angezeigt.
	 * @param logger
	 *            Der Logger darf nicht null sein. Er wird benoetigt um
	 *            Programmereignisse und regelmaessig ermittelte
	 *            Durchschnittswerte zu loggen. Wird null uebergeben wird dies
	 *            in den Systemoutput geschrieben.
	 */
	public MulticastController(ViewController viewController, Logger logger) {
		this(viewController, logger, 60000);
	}

	/**
	 * Erzeugt einen MulticastController.
	 * 
	 * @param viewController
	 *            Ist dieses Objekt nicht null werden darueber MessageBoxen mit
	 *            Status- oder Fehlermeldungen dem Nutzer angezeigt.
	 * @param logger
	 *            Der Logger darf nicht null sein. Er wird benoetigt um
	 *            Programmereignisse und regelmaessig ermittelte
	 *            Durchschnittswerte zu loggen. Wird null uebergeben wird dies
	 *            in den Systemoutput geschrieben.
	 * @param pPrintTableIntervall
	 *            die intervall zeit fuer den konsolen tabellen output
	 */
	public MulticastController(ViewController viewController, Logger logger,
			int pPrintTableIntervall) {
		super();
		lang = LanguageManager.getInstance();
		this.printTableIntervall = pPrintTableIntervall;
		// MC_Data
		mc_sender_l3 = new Vector<MulticastData>();
		mc_receiver_l3 = new Vector<MulticastData>();
		mc_sender_l2 = new Vector<MulticastData>();
		mc_receiver_l2 = new Vector<MulticastData>();

		// Thread-Maps
		mcMap_receiver_l3 = new HashMap<MulticastData, MulticastThreadSuper>();
		mcMap_sender_l3 = new HashMap<MulticastData, MulticastThreadSuper>();
		mcMap_receiver_l2 = new HashMap<MulticastData, MulticastThreadSuper>();
		mcMap_sender_l2 = new HashMap<MulticastData, MulticastThreadSuper>();
		// other
		threads = new HashMap<MulticastData, Thread>();
		
		view_controller = viewController;

		// Der Logger darf eigentlich nicht null sein. Dies wird zu Fehlern im
		// Programm fuehren.
		if (logger != null) {
			this.logger = logger;
		} else {
			System.out
					.println(lang.getProperty("error.mc.logger"));
		}

		// **************************************************************
		// Config file stuff
		// **************************************************************
		xml_parser = new xmlParser(logger);
		lastConfigs = new Vector<String>();
		userInputData = new Vector<UserInputData>();
		
		updateTask = new UpdateTask(logger, mcMap_sender_l3, mcMap_receiver_l3, mcMap_sender_l2, mcMap_receiver_l2, view_controller);
		timer1 = new Timer();
		timer1.schedule(updateTask, 3000, 1000);
		if (view_controller != null) {
			RunSnakeRun n = new RunSnakeRun(view_controller);
			timer2 = new Timer();
			timer2.schedule(n, 3000, 100);
		}

		regularLoggingTask = new RegularLoggingTask(logger, mcMap_sender_l3,
				mcMap_receiver_l3);
		timer3 = new Timer();
		timer3.schedule(regularLoggingTask, this.printTableIntervall,
				this.printTableIntervall); // default 60000
	}

	// ****************************************************
	// Multicast-Steuerung
	// ****************************************************
	/**
	 * Fuegt das uebergebene MulticastData-Objekt hinzu, erzeugt entsprechenden
	 * Thread und startet diesen falls notwendig.
	 * 
	 * @param m MulticastData-Objekt das hinzugefuegt werden soll.
	 */
	public void addMC(MulticastData m) {
		try {
			// Erzeugt den passenden MulticastThreadSuper
			MulticastThreadSuper t = null;

			switch (m.getTyp()) {
			case L3_SENDER:
				t = new MulticastSender(m, logger);
				// V1.5 [FH] Added MulticastController to stop it in case of
				// network error
				((MulticastSender) t).setMCtrl(this);
				break;

			case L3_RECEIVER:
				t = new MulticastReceiver(m, logger);
				break;

			case L2_SENDER:
				t = new MulticastMmrpSender(m, logger, this);
				break;

			case L2_RECEIVER:
				t = new MulticastMmrpReceiver(m, logger);
				break;

			default:
				break;
			}

			// Fuegt Multicasts zu der entsprechenden Map und Vector hinzu
			getMCVector(m).add(0, m);
			getMCMap(m).put(m, t);

			// Loggt das hinzufuegen des Multicasts
			logger.log(Level.INFO, lang.getProperty("message.mcAdded") + " :" + m.identify());
			// Startet den Multicast, falls notwendig
			if (m.isActive()) {
				startMC(m);
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Diese Methode muss aufgerufen werden, wenn sich Einstellungen des
	 * Multicasts aendern. Zum Beispeil die GroupIP. Hier wird der vorhandene
	 * Multicast geloescht und anschlieueend der Multicast neu erzeugt.
	 * 
	 * @param m Refernez auf das Datenobjekt, welches veraendert wurde.
	 */
	public void changeMC(MulticastData m) {
		// Loescht den Multicast und fuegt ihn neu hinzu. Dadurch werden
		// Sender/Receiver mit den neuen Einstellungen neu erzeugt.
		// Zwischenspeichern von active ist notwendig, da Multicasts beim
		// Loeschen gestoppt wird.
		boolean active = m.isActive();
		deleteMC(m);
		m.setActive(active);
		addMC(m);
	}

	/**
	 * Stoppt den zugehoerigen Thread und loescht alle Referenzen auf diesen,
	 * sowie das MulticastData-Objekt.
	 * 
	 * @param m Data object to delete.
	 */
	public void deleteMC(MulticastData m) {
		// Stoppen und Entfernen des Threads
		stopMC(m);
		getMCMap(m).remove(m);
		// Entfernen des Datenobjektes
		getMCVector(m).remove(m);
		// Log des Loeschens
		logger.log(Level.INFO, lang.getProperty("message.mcDeleted") + " :" + m.toStringConsole());
	}

	/**
	 * Entfernt alle Multicasts im Vektor.
	 * 
	 * @param m Vector mit zu entfernenden Multicasts.
	 */
	public void deleteMC(Vector<MulticastData> m) {
		for (MulticastData mc : m) {
			deleteMC(mc);
		}
	}

	/**
	 * Startet den Multicast und gibt eine Fehlermeldung aus, wenn dies nicht
	 * moeglich ist.
	 * 
	 * @param m MulticastData-Objekt des zu startenden Multicasts.
	 */
	public void startMC(MulticastData m) {

		synchronized (m) { // ohne sychronized ist das Programm in einen
							// Deadlock gelaufen
			if (!threads.containsKey(m)) { // prueft ob der Multicast schon laeuft.
				// versucht, 2 Sekunden lang auf den noch laufenden Thread zu warten.
				long time = System.currentTimeMillis() + 2000;
				while ((getMCMap(m).get(m)).isStillRunning()) {
					if (time < System.currentTimeMillis()) { // verhindert
																// haengen
																// bleiben; dies
																// kommt in
																// wenigen
																// Faellen noch
																// vor.
						logger.log(Level.SEVERE, lang.getProperty("message.mcAdvisePart1")
								+ m + lang.getProperty("message.mcAdvisePart2"));
						return;
					}
				}

				switch (m.getTyp()) {
				case L3_SENDER:
					// Thread ID nur bei Sendern setzen. Beim Receiver wird der Wert aus dem Datenpaket ausgelesen.
					m.setThreadID(threadCounter);
					// Random Number zur Unterscheidung von verschiedenen Instanzen generieren
					m.setRandomID(Integer.toHexString(new Random().nextInt()));
					threadCounter++;
					break;
				case L3_RECEIVER:
					// Fehlermeldung und Log werden im Receiver selber ausgegeben.
					if (((MulticastReceiver) getMCMap(m).get(m)).joinGroup())
						return;
					break;
				case L2_SENDER:
					m.setThreadID(threadCounter);
					// Random Number zur Unterscheidung von verschiedenen Instanzen generieren
					m.setRandomID(Integer.toHexString(new Random().nextInt()));
					threadCounter++;
					break;
				/*
				 * case L2_RECEIVER: break;
				 */
				default:
					break;
				}

				// Multicast auf aktiv setzen, einen neuen Thread erzeugen und starten.
				getMCMap(m).get(m).setActive(true);
				Thread t = new Thread(getMCMap(m).get(m));
				t.start();
				threads.put(m, t);
			} else {
				logger.log(Level.INFO, lang.getProperty("message.mcStartRunning"));
			}
		}
	}

	/**
	 * Startet alle Multicasts aus dem uebergebenen Vektor.
	 * 
	 * @param m Vektor mit MulticastData-Objekten.
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
	 * @param m MulticastData-Objekt
	 */
	public void stopMC(MulticastData m) {
		if (threads.containsKey(m)) {
			// Der Receiver loggt selber; Fehler werden an in die MessageQueue
			// eingefuegt.
			getMCMap(m).get(m).setActive(false);
			/*
			 * try { // fuerht zu einer Blockierung der GUI
			 * threads.get(m).join(); } catch (InterruptedException e) {
			 * messageQueue.add(
			 * "[Fehler][MulticastController]Could not stop Multicast properly. This Multicast might not work anymore. In this case try to delete and add the Multicast."
			 * ); }
			 */
			threads.remove(m);
		} else {
			logger.log(Level.INFO, lang.getProperty("message.mcStopNotRunning"));
		}
	}

	/**
	 * Stoppt alle Multicast im uebergebenen Vektor.
	 * 
	 * @param m
	 *            MulticastData-Objekt
	 */
	public void stopMC(Vector<MulticastData> m) {
		// log("stopMC mit Vector *****");
		for (MulticastData ms : m) {
			stopMC(ms);
		}
	}

	// ****************************************************
	// Config-File-Stuff
	// ****************************************************

	/**
	 * Gibt einen Vektor mit den drei zuletzt geoeffneten Konfigurationsdateien
	 * zurueck.
	 */
	public Vector<String> getLastConfigs() {
		return lastConfigs;
	}

	/**
	 * Saves not checked data from View necessary to reconstruct the exact state
	 * from View.
	 * 
	 * @param u
	 *            Vector of UserInputData obejcts. The Vector should contain
	 *            between one and four objects.
	 */
	void autoSave(Vector<UserInputData> u) {
		userInputData = u;
		saveCompleteConfig();
	}

	/**
	 * Speichert eine Konfigurationsdatei.
	 * 
	 * @param path
	 *            Pfad zur GUI Konfigurationsdatei.
	 * @param data
	 *            Alle zu speichernden GUI Configs.
	 */
	public void saveGUIConfig(String path, GUIData data) {
		final String p = "GUIConfig.xml";
		try { // Uebergibt den Vektor mit allen Multicasts an den XMLParser
			// FH Changed && to ||, think this is right ;)
			if (path == null || path.length() == 0) {
				xml_parser.saveGUIConfig(p, data);
				// logger.log(Level.INFO,
				// "Saved Multicastconfiguration at default location.");
			} else {
				xml_parser.saveGUIConfig(path, data);
				addLastConfigs(path);
				logger.log(Level.INFO, lang.getProperty("message.savedGUI"));
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, lang.getProperty("message.savedGUINot"));
		}
	}

	/**
	 * Speichert eine Konfigurationsdatei.
	 * 
	 * @param path
	 *            Pfad zur Konfigurationsdatei.
	 * @param v
	 *            Alle zu speichernden Multicasts.
	 */
	public void saveMulticastConfig(String path, Vector<MulticastData> v) {
		final String p = "MultiCastor.xml";

		try { // Uebergibt den Vektor mit allen Multicasts an den XMLParser
			// FH Changed && to ||, think this is right ;)
			if (path == null || path.length() == 0) {
				xml_parser.saveMulticastConfig(p, v);
				// logger.log(Level.INFO,
				// "Saved Multicastconfiguration at default location.");
			} else {
				xml_parser.saveMulticastConfig(path, v);
				addLastConfigs(path);
				logger.log(Level.INFO, lang.getProperty("message.savedMCConfig"));
			}
		} catch (Exception e) {
			logger
					.log(Level.WARNING,
							lang.getProperty("message.savedMCConfigNot"));
			e.printStackTrace();
		}
	}

	/**
	 * Speichert die Standardkonfigurationsdatei.
	 */
	public void updateGUIData(GUIData data) {
		data.setL2_SENDER(GUIData.TabState.invisible);
		data.setL3_SENDER(GUIData.TabState.invisible);
		data.setL2_RECEIVER(GUIData.TabState.invisible);
		data.setL3_RECEIVER(GUIData.TabState.invisible);
		data.setABOUT(GUIData.TabState.invisible);
		data.setPLUS(GUIData.TabState.visible);

		data.Default_L2_GroupMac = view_controller.guidata.Default_L2_GroupMac;
		data.Default_L2_PacketLength = view_controller.guidata.Default_L2_PacketLength;
		data.Default_L2_PacketRateDesired = view_controller.guidata.Default_L2_PacketRateDesired;
		data.Default_L3_GroupIp = view_controller.guidata.Default_L3_GroupIp;
		data.Default_L3_PacketLength = view_controller.guidata.Default_L3_PacketLength;
		data.Default_L3_Ttl = view_controller.guidata.Default_L3_Ttl;
		data.Default_L3_PacketRateDesired = view_controller.guidata.Default_L3_PacketRateDesired;
		data.Default_L3_UdpPort = view_controller.guidata.Default_L3_UdpPort;
		
		// set the new state if they are visible
		for (int i = 0; i < view_controller.getFrame().getTabpane()
				.getTabCount(); ++i) {
			String title = view_controller.getFrame().getTabpane()
					.getTitleAt(i);
			if (title.equals(" " + lang.getProperty("tab.l2s") + " "))
				data.setL2_SENDER(GUIData.TabState.visible);
			if (title.equals(" " + lang.getProperty("tab.l3s") + " "))
				data.setL3_SENDER(GUIData.TabState.visible);
			if (title.equals(" " + lang.getProperty("tab.l2r") + " "))
				data.setL2_RECEIVER(GUIData.TabState.visible);
			if (title.equals(" " + lang.getProperty("tab.l3r") + " "))
				data.setL3_RECEIVER(GUIData.TabState.visible);
			if (title.equals(" " + lang.getProperty("mi.about") + " "))
				data.setABOUT(GUIData.TabState.visible);
			if (title.equals(" + "))
				data.setPLUS(GUIData.TabState.visible);
		}

		// get the selected tab
		String title = view_controller.getFrame().getTabpane().getTitleAt(
				view_controller.getFrame().getTabpane().getSelectedIndex());

		if (title.equals(" " + lang.getProperty("tab.l2s") + " "))
			data.setL2_SENDER(GUIData.TabState.selected);
		if (title.equals(" " + lang.getProperty("tab.l3s") + " "))
			data.setL3_SENDER(GUIData.TabState.selected);
		if (title.equals(" " + lang.getProperty("tab.l2r") + " "))
			data.setL2_RECEIVER(GUIData.TabState.selected);
		if (title.equals(" " + lang.getProperty("tab.l3r") + " "))
			data.setL3_RECEIVER(GUIData.TabState.selected);
		if (title.equals(" " + lang.getProperty("mi.about") + " "))
			data.setABOUT(GUIData.TabState.selected);
		if (title.equals(" + "))
			data.setPLUS(GUIData.TabState.selected);

		data.setLanguage(LanguageManager.getCurrentLanguage());
		data.setWindowName(view_controller.getFrame().getBaseTitle());
	}

	/**
	 * Speichert die Standardkonfigurationsdatei.
	 */
	private void saveCompleteConfig() {
		Vector<MulticastData> v = new Vector<MulticastData>();
		v.addAll(getMCs(Typ.L2_RECEIVER));
		v.addAll(getMCs(Typ.L3_RECEIVER));
		v.addAll(getMCs(Typ.L2_SENDER));
		v.addAll(getMCs(Typ.L3_SENDER));
		saveMulticastConfig("MultiCastor.xml", v);

		GUIData data = new GUIData();
		// set everythign to invisible
		updateGUIData(data);

		saveGUIConfig("GUIConfig.xml", data);

	}
		
	/**
	 * Loads data from autoSave() method.
	 * 
	 * @return Vector of UserInputData objects. This Vector contains between one
	 *         and four objects. One for each tab in View at most.
	 */
	Vector<UserInputData> loadAutoSave() {
		return userInputData;
	}
	
	public void loadDefaultMulticastConfig() {
		loadMulticastConfig("", true);
	}

	/**
	 * Laedt die GUI Konfigurationsdatei
	 * 
	 * @param path
	 *            Pfad zur Konfigurationsdatei, die geladen werden soll.
	 * @param useDefaultXML
	 *            Wenn hier true gesetzt ist, wird der Standardpfad genommen und
	 *            MCD + UID + ULD geladen.
	 */
	public void loadGUIConfig(String path, boolean useDefaultXML) {
		
		final String defaultXML = "GUIConfig.xml";
		String message = new String();
		GUIData data = new GUIData();

		try {
			xml_parser.loadGUIConfig(useDefaultXML ? defaultXML : path, data);
			logger.log(Level.INFO, lang.getProperty("message.gui.Loaded"));
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.gui.NotFoundWithout");
				} else {
					message = lang.getProperty("message.gui.NotFound");
				}
			} else if (e instanceof SAXException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.gui.NotParsedWithout");
				} else {
					message = lang.getProperty("message.gui.NotParsed");
				}
			} else if (e instanceof IOException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.gui.NotLoadedWithout");
				} else {
					message = lang.getProperty("message.gui.NotLoaded");
				}
			} else if (e instanceof WrongConfigurationException) {
				message = ((WrongConfigurationException) e).getErrorMessage();
			} else if (e instanceof IllegalArgumentException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.gui.ErrorDefault");
				} else {
					message = lang.getProperty("message.gui.Error");
				}
			} else {
				message = lang.getProperty("message.gui.unexcpectedError") + e.getClass();
			}

			if (!useDefaultXML) {
				message += lang.getProperty("message.gui.usedPath") + path;
			}
			logger.log(Level.WARNING, message);
		}
		view_controller.setGUIConfig(data);
	}

	/**
	 * Laedt eine Konfigurationsdatei und fuegt markierte Multicasts hinzu.
	 * 
	 * @param path
	 *            Pfad zur Konfigurationsdatei, die geladen werden soll.
	 * @param useDefaultXML
	 *            Wenn hier true gesetzt ist, wird der Standardpfad genommen und
	 *            MCD + UID + ULD geladen.
	 */
	public void loadMulticastConfig(String path, boolean useDefaultXML) {
		final String defaultXML = "MultiCastor.xml";

		/* Nach dem Laden stehen hier alle MulticastData Objecte drin */
		Vector<MulticastData> multicasts = new Vector<MulticastData>();
		String message = new String();
		boolean skip = false;
		try {
			xml_parser.loadMultiCastConfig(useDefaultXML ? defaultXML : path,
					multicasts);
			logger.log(Level.INFO, lang.getProperty("message.mcc.Loaded"));
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.mcc.NotFoundWithout");
				} else {
					message = lang.getProperty("message.mcc.NotFound");
				}
			} else if (e instanceof SAXException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.mcc.NotParsedWithout");
				} else {
					message = lang.getProperty("message.mcc.NotParsed");
				}
			} else if (e instanceof IOException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.mcc.NotLoadedWithout");
				} else {
					message = lang.getProperty("message.mcc.NotLoaded");
				}
			} else if (e instanceof WrongConfigurationException) {
				message = ((WrongConfigurationException) e).getErrorMessage();
			} else if (e instanceof IllegalArgumentException) {
				if (useDefaultXML) {
					message = lang.getProperty("message.mcc.ErrorDefault");
				} else {
					message = lang.getProperty("message.mcc.Error");
				}
			} else {
				message = lang.getProperty("message.mcc.unexcpectedError")+ e.getClass();
			}
			skip = true;
			if (!useDefaultXML) {
				message += lang.getProperty("message.mcc.usedPath") + path;
			}
			logger.log(Level.WARNING, message);
		}
			
	    if(!skip) {
	    	// Fuege Multicast hinzu
	    	for(MulticastData m : multicasts){
	    		if (
	    			m.getTyp().equals(Typ.L3_RECEIVER) ||
		 	    	m.getTyp().equals(Typ.L2_RECEIVER) ||
		 	    	m.getTyp().equals(Typ.L3_SENDER) ||
		 	    	m.getTyp().equals(Typ.L2_SENDER)
		 	    ){
	    			view_controller.addMC(m);
	    		}
	    	}
	    }
	    view_controller.loadAutoSave();
	}
	
	/**
	 * Laedt die angegebene Konfigurationsdatei. Fehlermeldungen werden geworfen. Diese Funktion sollte nur aus der Main
	 * aufgerufen werden. Fehlermeldungen werden geworfen um versehentlichen Nutzen in der GUI zu verhindern.
	 * @param path Pfad zur Konfigurationsdatei
	 * @throws FileNotFoundException Die Datei wurde nicht gefunden.
	 * @throws SAXException Beim Parsen der Datei ist ein Fehler aufgetreten.
	 * @throws IOException Ein anderer Inputfehler ist aufgetreten.
	 * @throws WrongConfigurationException 
	 */
	public void loadConfigWithoutGUI(String path) throws FileNotFoundException, SAXException, IOException, WrongConfigurationException{
		Vector<MulticastData> v = new Vector<MulticastData>();
		xml_parser.loadMultiCastConfig(path, v);
		if (v != null) {
			for (MulticastData m : v) {
				addMC(m); // hier vllt. nur adden wenn man auch starten will, da
							// das leider nicht mehr geht
			}
		}
	}

	/**
	 * Fuegt zum lastConfigs-Vektor einen Pfad hinzu
	 * 
	 * @param path Pfad zur Konfigurationsdatei
	 */
	private void addLastConfigs(String path) {
		if (lastConfigs.size() < 3) {
			lastConfigs.add(0, path);
		} else {
			lastConfigs.remove(2);
			lastConfigs.add(0, path);
		}
	}

	// ****************************************************
	// Other-Stuff
	// ****************************************************

	/**
	 * 
	 * Adds up the measuredPacketRate from MulticastSenders
	 * 
	 * @param typ Specifies whether L2_SENDER or L3_SENDER is returned.
	 * @return Sum of all sent packets. Returns 0 if typ is invalid.
	 */
	public int getPPSSender(MulticastData.Typ typ) {
		int count = 0, tmpCount = 0;

		if (typ == Typ.L3_SENDER) {
			for (MulticastData ms : mc_sender_l3) {
				count += ((MulticastSenderInterface) mcMap_sender_l3.get(ms)).getMultiCastData().getPacketRateMeasured();
			}
		} else if (typ == Typ.L2_SENDER) {
			for (MulticastData ms : mc_sender_l2) {
				tmpCount = ((MulticastSenderInterface) mcMap_sender_l2.get(ms)).getMultiCastData().getPacketRateMeasured();
				if (tmpCount == -1)
					tmpCount = 0;
				count += tmpCount;
			}
		}
		return count;
	}

	/**
	 * Stops all Multicasts Threads and removes them from corresponding vectors.
	 */
	public void destroy() {
		saveCompleteConfig();
		Map<MulticastData, MulticastThreadSuper> v = null;
		for (int i = 0; i < 4; i++) {
			switch (i) {
			case 0:
				v = mcMap_sender_l3;
				break;
			case 1:
				v = mcMap_receiver_l3;
				break;
			case 2:
				v = mcMap_sender_l2;
				break;
			case 3:
				v = mcMap_receiver_l2;
				break;
			}
			for (Entry<MulticastData, MulticastThreadSuper> m : v.entrySet()) {
				m.getValue().setActive(false);
			}
			v.clear();
		}	
		
		mc_receiver_l3.removeAllElements();
		mc_sender_l3.removeAllElements();
		mc_receiver_l2.removeAllElements();
		mc_sender_l2.removeAllElements();

		mcMap_receiver_l3.clear();
		mcMap_sender_l3.clear();
		mcMap_receiver_l2.clear();
		mcMap_sender_l2.clear();

	}
	
	
	// ****************************************************
	// Ein Hilfsfunktionen vor allem fuer mich, teils auch fuer Daniel
	// ****************************************************

	/**
	 * Gibt das MulticastData-Objekt an der Stelle im Vektor zurueck.
	 * 
	 * @param index Index des MulticastData-Objekts
	 * @param multicastDataTyp
	 *            Der MulticastDatentyp um den entsprechenden Vektor zu
	 *            bestimmt.
	 * @return Das MulticastData-Objekt an der entsprechenden Stelle im Vektor.
	 *         Gibt <code>null</code> zurueck, wenn das Objekt nicht existiert.
	 */
	public MulticastData getMC(int index, MulticastData.Typ multicastDataTyp) {
		try {
			return (MulticastData) getMCVector(multicastDataTyp).get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gibt den Vektor mit MulticastData-Objekten in dem alle Objekte des Typs
	 * gespeichert sind.
	 * 
	 * @param m Gibt den Typ der MulticastData-Objekte an.
	 * @return Vektor mit MulticastData-Objekten des angegebenen Typs. Ist der
	 *         Typ <code>UNDEFIENED</code> wird <code>null</code>
	 *         zurueckgegeben.
	 */
	public Vector<MulticastData> getMCs(MulticastData.Typ m) {
		return getMCVector(m);
	}

	/**
	 * Gibt den Vektor mit MulticastData-Objekten in dem alle Objekte des Typs
	 * gespeichert sind.
	 * 
	 * @param m Der Typ wird dem uebergebenen MulticastData-Objekt entnommen.
	 * @return Vektor mit MulticastData-Objekten des angegebenen Typs. Ist der
	 *         Typ <code>UNDEFINED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Vector<MulticastData> getMCVector(MulticastData m) {
		return getMCVector(m.getTyp());
	}

	/**
	 * Gibt den Vektor mit MulticastData-Objekten in dem alle Objekte des Typs
	 * gespeichert sind.
	 * 
	 * @param m Gibt den Typ der MulticastData-Objekte an.
	 * @return Vektor mit MulticastData-Objekten des angegebenen Typs. Ist der
	 *         Typ <code>UNDEFINED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Vector<MulticastData> getMCVector(MulticastData.Typ multicastDataTyp) {
		Vector<MulticastData> vector = null;
		switch (multicastDataTyp) {
		/* v1.5 */
		case L3_RECEIVER:
			vector = mc_receiver_l3;
			break;
		case L3_SENDER:
			vector = mc_sender_l3;
			break;
		case L2_RECEIVER:
			vector = mc_receiver_l2;
			break;
		case L2_SENDER:
			vector = mc_sender_l2;
			break;

		default:
			logger
					.log(Level.SEVERE,lang.getProperty("message.mc.undefined"));
			return null;
		}
		return vector;
	}

	/**
	 * Gibt die Map mit gespeicherten MulticastData-Objekten von dem
	 * entsprechenden Typ zurueck.
	 * 
	 * @param multicastDataTyp Typ der MulticastData-Objekte in der Map.
	 * @return Gibt die entsprechende Map zurueck. Ist der Typ
	 *         <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Map<MulticastData, MulticastThreadSuper> getMCMap(
			MulticastData.Typ multicastDataTyp) {
		Map<MulticastData, MulticastThreadSuper> map = null;
		switch (multicastDataTyp) {
		/* v1.5 */
		case L3_RECEIVER:
			map = mcMap_receiver_l3;
			break;
		case L3_SENDER:
			map = mcMap_sender_l3;
			break;
		case L2_RECEIVER:
			map = mcMap_receiver_l2;
			break;
		case L2_SENDER:
			map = mcMap_sender_l2;
			break;

		default:
			logger
					.log(Level.SEVERE, lang.getProperty("message.mc.undefined"));
			return null;
		}
		return map;
	}

	/**
	 * Gibt die Map mit gespeicherten MulticastData-Objekten von dem
	 * entsprechenden Typ zurueck.
	 * 
	 * @param m Wird genutzt um den Typ zu bestimmen.
	 * @return Gibt die entsprechende Map zurueck. Ist der Typ
	 *         <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Map<MulticastData, MulticastThreadSuper> getMCMap(MulticastData m) {
		return getMCMap(m.getTyp());
	}

	/**
	 * setzt das Zeitintervall fuer die Ausgabe der Tabelle auf der Konsole
	 * 
	 * @param printTableTime Zeitintervall in milliseconds
	 */
	public void setPrintTableTime(int printTableTime) {
		this.printTableIntervall = printTableTime;
	}

	/**
	 * Getter of XmlParser
	 * @return the Xml Parser
	 */
	public XMLParserInterface getXml_parser() {
		return xml_parser;
	}

}