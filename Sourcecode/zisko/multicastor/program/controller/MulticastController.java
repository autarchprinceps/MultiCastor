package zisko.multicastor.program.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.data.UserlevelData;
import zisko.multicastor.program.interfaces.MulticastSenderInterface;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;
import zisko.multicastor.program.interfaces.XMLParserInterface;
import zisko.multicastor.program.model.MulticastReceiver;
import zisko.multicastor.program.model.MulticastSender;
import zisko.multicastor.program.model.RegularLoggingTask;
import zisko.multicastor.program.model.RunSnakeRun;
import zisko.multicastor.program.model.UpdateTask;
import zisko.multicastor.program.model.WrongConfigurationException;
import zisko.multicastor.program.model.xmlParser;

/**
 * Der MulticastController verwaltet Multicasts und abstrahiert das Speichern und Laden
 * von Konfigurationsdateien von der View.
 * @author Bastian Wagener
 *
 */
public class MulticastController{

	// ****************************************************
	// Felder fuer MultiCast Controller
	// ****************************************************
	/** Haelt Referenzen auf alle MulticastData-Objekte vom Typ Sender_v4 */
	private Vector<MulticastData> mc_sender_v4;
	/** Haelt Referenzen auf alle MulticastData-Objekte vom Typ Sender_v6 */
	private Vector<MulticastData> mc_sender_v6;
	/** Haelt Referenzen auf alle MulticastData-Objekte vom Typ Receiver_v4 */
	private Vector<MulticastData> mc_receiver_v4;
	/** Haelt Referenzen auf alle MulticastData-Objekte vom Typ Receiver_v6 */
	private Vector<MulticastData> mc_receiver_v6;
	
	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
	private Map<MulticastData,MulticastThreadSuper> mcMap_sender_v4;
	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
	private Map<MulticastData,MulticastThreadSuper> mcMap_sender_v6;
	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
	private Map<MulticastData,MulticastThreadSuper> mcMap_receiver_v4;
	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
	private Map<MulticastData,MulticastThreadSuper> mcMap_receiver_v6;
	/** Diese Map bildet MulticastData-Objekte auf Threads ab, um von einem Multicast direkt mit dem entsprechenden Thread kommunizieren zu koennen. Dies wird vor allem beim Beenden der Multicasts genutzt. */
	private Map<MulticastData, Thread> threads;
	/** Interner Threadcounter, der beim Anlegen oder Starten eines MulticastSenders hochgezaehlt wird und diesem den Wert als ThreadID mitgibt. */
	private int threadCounter = 0;	
	/** Der ViewController stellt eine Funktion zur Ausgabe von MessageBoxen bereit. */
	private ViewController view_controller;
	/** Der XMLParser wird zum Speichern und Laden von Konfigurationsdateien genutzt. */
	private XMLParserInterface xml_parser;
	/** Der Logger speichert Daten in einer Logdatei */
	private Logger logger;
	/** Der UpdateTask sorgt fuer Aktualisierungen in der Datenhaltung und der View. */
	private UpdateTask updateTask;
	/** Referenz auf einen Timer fuer updateTask */
	private Timer timer1;
	/** Referenz auf einen Timer fuer messageCheck */
	private Timer timer2;
	/** Referenz auf einen Timer fuer regularLoggingTask */
	private Timer timer3;
	/** Loesst eine Aktualisierung der Durchschnittswerte in der Datenhaltung aus und speichert diese mittels Logger. */
	private RegularLoggingTask regularLoggingTask;
	
	// Config
	/** Informationen zum wiederherstellen des letzten GUI Status */
	private Vector<UserInputData> userInputData;
	/** In diesem Vektor werden UserLevelData-Objekte gespeichert. Daher sollte er bis zu 12 Objekte enthalten. Eines fuer jeden moeglichen GUI-Status. */
	private Vector<UserlevelData> userlevelData;
	/** In diesem Vektor werden die default UserLevelData-Objekte gespeichert. Daher sollte er jederzeit 12 Objekte enthalten. Eines fuer jeden moeglichen GUI-Status. */
	private Vector<UserlevelData> userlevelDataDefault;
	/** Haelt die Pfade als String auf die zuletzt geladenen Konfigurationsdateien. Dieser Vektor sollte vier Eintraenge enthalten.*/
	private Vector<String> lastConfigs;
	/** Speicher die Standardwerte fuer Userlevel: Beginner */
	private Vector<MulticastData> defaultValuesUserlevelData;

	
	
	/**
	 * Erzeugt einen MulticastController.
	 * @param viewController Ist dieses Objekt nicht null werden darueber MessageBoxen mit Status- oder 
	 * Fehlermeldungen dem Nutzer angezeigt.
	 * @param logger Der Logger darf nicht null sein. Er wird benoetigt um Programmereignisse 
	 * und regelmaessig ermittelte Durchschnittswerte zu loggen. Wird null uebergeben wird
	 * dies in den Systemoutput geschrieben.
	 */
	public MulticastController(ViewController viewController, Logger logger){
		super();
		// MC_Data
		mc_sender_v4 = new Vector<MulticastData>();
		mc_sender_v6 = new Vector<MulticastData>();
		mc_receiver_v4 = new Vector<MulticastData>();
		mc_receiver_v6 = new Vector<MulticastData>();
		// Thread-Maps
		mcMap_sender_v4 = new HashMap<MulticastData,MulticastThreadSuper>();
		mcMap_sender_v6 = new HashMap<MulticastData,MulticastThreadSuper>();
		mcMap_receiver_v4 = new HashMap<MulticastData,MulticastThreadSuper>();
		mcMap_receiver_v6 = new HashMap<MulticastData,MulticastThreadSuper>();
		// other
		threads = new HashMap<MulticastData, Thread>();
		view_controller = viewController;
		// load standard config file
		//	File f = new File("src"+File.separator+"zisko"+File.separator+"multicastor"+File.separator+"resources"+File.separator+"fonts",font_type); // geklaut von daniel
		//	loadConfigFile("config");
			
			// Der Logger darf eigentlich nicht null sein. Dies wird zu Fehlern im Programm fuehren.
			if(logger!=null){
				this.logger = logger;
			} else {
				System.out.println("Fehler im MulticastController: Logger-Objekt ist null");
			}
			
		// **************************************************************
		// Config file stuff
		// **************************************************************
		xml_parser = new xmlParser(logger);
		lastConfigs = new Vector<String>();
		// so lange wie noch keine geladen werden können:
	/*	lastConfigs.add("/home/wagener/test1.cfg");
		lastConfigs.add("/home/wagener/test2.cfg");
		lastConfigs.add("/home/wagener/test3.cfg");
		lastConfigs.add("/home/wagener/test4.cfg");
	*/	
		userlevelData = new Vector<UserlevelData>();
		userlevelDataDefault = new Vector<UserlevelData>();
		userInputData = new Vector<UserInputData>();
		defaultValuesUserlevelData = new Vector<MulticastData>();
		
	//	AllesLaden(); // sollte von Thomas gemacht werden
		
		updateTask = new UpdateTask(logger,mcMap_sender_v4, mcMap_sender_v6, mcMap_receiver_v4, mcMap_receiver_v6,view_controller);
		timer1 = new Timer();
		timer1.schedule(updateTask, 3000,1000);
		
	//	messageQueue = new LinkedList<String>(); 
	//	messageCheck = new MessageCheck(messageQueue, view_controller, this.logger);
		if(view_controller!=null){
			RunSnakeRun n = new RunSnakeRun(view_controller);
			timer2 = new Timer();
			timer2.schedule(n, 3000, 100);
		}
			
		regularLoggingTask = new RegularLoggingTask(logger,mcMap_sender_v4, mcMap_sender_v6, mcMap_receiver_v4, mcMap_receiver_v6);
		timer3 = new Timer();
		timer3.schedule(regularLoggingTask, 60000, 60000);
	}	
	
	// ****************************************************
	// Multicast-Steuerung
	// ****************************************************
	
	/**
	 * Fuegt das uebergebene MulticastData-Objekt hinzu, erzeugt entsprechenden Thread und startet diesen falls notwendig.
	 * @param m MulticastData-Objekt das hinzugefügt werden soll.
	 */
	public void addMC(MulticastData m) {
		// Erzeugt den passenden MulticastThreadSuper
		MulticastThreadSuper t = null; 
		if((m.getTyp() == MulticastData.Typ.SENDER_V4)||(m.getTyp() == MulticastData.Typ.SENDER_V6)){
			t = new MulticastSender(m, logger);
		} else if ((m.getTyp() == MulticastData.Typ.RECEIVER_V4)||(m.getTyp() == MulticastData.Typ.RECEIVER_V6)){
			t = new MulticastReceiver(m, logger);
		}
		// Fuegt Multicasts zu der entsprechenden Map hinzu
		getMCVector(m).add(0, m);
		getMCMap(m).put(m,t);
		
		// Loggt das hinzufuegen des Multicasts
		logger.log(Level.INFO, "Multicast added: " + m.identify());
		// Startet den Multicast, falls notwendig
		if(m.isActive()){
			startMC(m);
		}
	}
	
	/**
	 * Diese Methode muss aufgerufen werden, wenn sich Einstellungen des Multicasts aendern. 
	 * Zum Beispeil die GroupIP. Hier wird der vorhandene Multicast geloescht und anschließend
	 * der Multicast neu erzeugt.
	 * @param m Refernez auf das Datenobjekt, welches veraendert wurde.
	 */
	public void changeMC(MulticastData m) {
		// Loescht den Multicast und fuegt ihn neu hinzu. Dadurch werden Sender/Receiver mit den neuen Einstellungen neu erzeugt.
		// Zwischenspeichern von active ist notwendig, da Multicasts beim Loeschen gestoppt wird.
		boolean active = m.isActive();
		deleteMC(m);
		m.setActive(active);
		addMC(m);
	}
	
	/**
	 * Stoppt den zugehoerigen Thread und loescht alle Referenzen auf diesen, sowie das MulticastData-Objekt.
	 * @param m Data object to delete.
	 */
	public void deleteMC(MulticastData m) {
		// Stoppen und Entfernen des Threads
		stopMC(m);
		getMCMap(m).remove(m);
		// Entfernen des Datenobjektes
		getMCVector(m).remove(m);
		// Log des Loeschens
		logger.log(Level.INFO, "Multicast deleted: " + m.toStringConsole());
	}
	
	/**
	 * Entfernt alle Multicasts im Vektor.
	 * @param m Vector mit zu entfernenden Multicasts.
	 */
	public void deleteMC(Vector<MulticastData> m) {
	//	log("deleteMC mit Vector *****");
		for(MulticastData mc : m){
			deleteMC(mc);
		}
	}
	
	/**
	 * Startet den Multicast und gibt eine Fehlermeldung aus, wenn dies nicht moeglich ist.
	 * @param m MulticastData-Objekt des zu startenden Multicasts.
	 */
	public void startMC(MulticastData m) {
	//	writeConfig();
	//	System.out.println("writeConfig");
		synchronized(m){ // ohne sychronized ist das Programm in einen Deadlock gelaufen
			if(!threads.containsKey(m)){ // prueft ob der Multicast schon laeuft.
				long time = System.currentTimeMillis()+2000; // versucht, 2 Sekunden lang auf den noch laufenden Thread zu warten.
				while((getMCMap(m).get(m)).isStillRunning()){
					if(time < System.currentTimeMillis()){ // verhindert haengen bleiben; dies kommt in wenigen Faellen noch vor.
						logger.log(Level.SEVERE, "Could not start Multicast: " + m + " Advised a Change on that Multicast.");
						return;
					}
				}
				
				// Thread ID nur bei Sendern setzen. 
				//   Beim Receiver wird der Wert aus dem Datenpaket ausgelesen.
				if((m.getTyp()==MulticastData.Typ.SENDER_V4)||(m.getTyp()==MulticastData.Typ.SENDER_V6)){
					m.setThreadID(threadCounter);
					threadCounter++;
				} else { // Receiver haben den GroupJoin ausgelagert.
					if(((MulticastReceiver) getMCMap(m).get(m)).joinGroup()){
						// Fehlermeldung und Log werden im Receiver selber ausgegeben.
						return;
					}
				}
				// Multicast auf aktiv setzen, einen neuen Thread erzeugen und starten.
				getMCMap(m).get(m).setActive(true);
				Thread t = new Thread(getMCMap(m).get(m));
				t.start();
				threads.put(m,t);
			} else {
				logger.log(Level.INFO, "Tried to start an already running Multicast.");
			}
		}		
	}
	
	/**
	 * Startet alle Multicasts aus dem uebergebenen Vektor.
	 * @param m Vektor mit MulticastData-Objekten.
	 */
	public void startMC(Vector<MulticastData> m) {
		for(MulticastData ms: m){
			startMC(ms);
		}
	}

	/**
	 * Stoppt den uebergebenen Multicast. Der Thread wird nicht direkt gestoppt sondern laeuft von selber aus.
	 * Es wird gewartet bis der Thread sich gestoppt hat oder eine Fehlermeldung ausgegeben falls eine Interruption auftrit.
	 * Fehler oder auch das erfolgreiche Stoppen werden an die MesseageQueue angehaengt.
	 * @param m MulticastData-Objekt
	 */
	public void stopMC(MulticastData m) {
		if(threads.containsKey(m)){
			// Der Receiver loggt selber; Fehler werden an in die MessageQueue eingefuegt.
			getMCMap(m).get(m).setActive(false);
		/*	try { // fuerht zu einer Blockierung der GUI
				threads.get(m).join();
			} catch (InterruptedException e) {
				messageQueue.add("[Fehler][MulticastController]Could not stop Multicast properly. This Multicast might not work anymore. In this case try to delete and add the Multicast.");
			} */
			threads.remove(m);
		} else {
			logger.log(Level.INFO, "Tried to stop a not running Multicast.");
		}	
	}

	/**
	 * Stoppt alle Multicast im uebergebenen Vektor.
	 * @param m MulticastData-Objekt
	 */
	public void stopMC(Vector<MulticastData> m) {
	//	log("stopMC mit Vector *****");
		for(MulticastData ms: m){
			stopMC(ms);
		}
	}
	
	
	
	// ****************************************************
	// Config-File-Stuff
	// ****************************************************

	/**
	 * Gibt einen Vektor mit den drei zuletzt geoeffneten Konfigurationsdateien zurueck.
	 */
	public Vector<String> getLastConfigs() {
		return lastConfigs;
	}

	/**
	 * Gibt ULD zu den entsprechenden Parametern zurueck.
	 * @param typ Chooses tab like RECEIVER_V4,RECEIVER_V6,SENDER_V4,SENDER_V6
	 * @param userlevel Chooses userlevel like beginner,expert,custom
	 */
	public UserlevelData getUserLevel(MulticastData.Typ typ, UserlevelData.Userlevel userlevel) {	
	//	System.out.println("Requested ULD: " + typ + " " + userlevel);
		for(UserlevelData uld : userlevelData){
			if((uld.getTyp().equals(typ))&&(uld.getUserlevel().equals(userlevel))){
	//			System.out.println("Gefunden in der datei");
				return uld;
			}		
		}
		if(userlevelDataDefault.isEmpty()){
			defaultUserlevelDataLaden();
		}
		for(UserlevelData uld : userlevelDataDefault){
			if((uld.getTyp().equals(typ))&&(uld.getUserlevel().equals(userlevel))){
			//	userlevelData.add(uld);
				return uld;
			}	
		}
		logger.log(Level.SEVERE, "Could not find requested UserlevelData in MulticastController");
		return null;
	}
	
	/**
	 * Hilfsfunktion feur mich, die eine Konfigurationsdatei erzeugt.
	 */
/*	private void writeConfig(){
		defaultUserlevelDataLaden();
		userlevelData = userlevelDataDefault;
		saveCompleteConfig();
	}*/
	
	/**
	 * Gibt die Standardwerte fuer ausgeblende Felder fuer den ULD Beginner zurueck.
	 * @param typ MC Typ
	 * @return Vektor mit ULDs drin.
	 */
	public MulticastData getUserlevelBeginnerDefaultValues(Typ typ){
		if(defaultValuesUserlevelData.isEmpty()){
			defaultUserlevelDataLaden();
		}
		for(MulticastData m:defaultValuesUserlevelData){
			if(m.getTyp().equals(typ)){
				return m;
			}
		}
		logger.log(Level.SEVERE, "Konnte default-Werte fuer Typ " + typ + " in der default-Konfigurationsdatei nicht finden.");
		return null;
	}
	
	/**
	 * Saves not checked data from View necessary to reconstruct the exact state from View.
	 * @param u Vector of UserInputData obejcts. The Vector should contain between one and four objects.
	 */
	void autoSave(Vector<UserInputData> u){
		userInputData = u;
		saveCompleteConfig();
	}
	
	/**
	 * Loads data from autoSave() method.
	 * @return Vector of UserInputData objects. This Vector contains between one and four objects. One for each tab in View at most.
	 */
	Vector<UserInputData> loadAutoSave(){	// noch bei Daniel nachhoeren ob es ok ist, wenn da ein leerer Vector zur\FCck kommt!!!!!!!!!
		return userInputData;
	}

	/**
	 * Speichert eine Konfigurationsdatei an den angegebenen Pfad. Hierbei werden nur die
	 * Multicasts von Typen mit uebergebenem True gespeichert.
	 * @param s Pfad zur Konfigurationsdatei
	 * @param Sender_v4 Wenn <code>true</code> werden Multicasts vom Typ Sender_v4 gespeichert.
	 * @param Sender_v6 Wenn <code>true</code> werden Multicasts vom Typ Sender_v6 gespeichert.
	 * @param Receiver_v4 Wenn <code>true</code> werden Multicasts vom Typ Receiver_v4 gespeichert.
	 * @param Receiver_v6 Wenn <code>true</code> werden Multicasts vom Typ Receiver_v6 gespeichert.
	 */
	public void saveConfig(String s, boolean Sender_v4, boolean Sender_v6, boolean Receiver_v4, boolean Receiver_v6) {
		// Sammelt alle zu speichernden Multicasts in einem Vektor
		Vector<MulticastData> v = new Vector<MulticastData>();
		if(Sender_v4){
			v.addAll(mc_sender_v4);
		}
		if(Sender_v6){
			v.addAll(mc_sender_v6);
		}
		if(Receiver_v4){
			v.addAll(mc_receiver_v4);
		}
		if(Receiver_v6){
			v.addAll(mc_receiver_v6);
		}
		saveConfig(s, false, v);
	}
	
	/**
	 * Laedt die Konfigurationsdatei am angegebenen Pfad. Fehlermeldungen werden ausgegeben.
	 * Diese Funktion sollte 
	 * @param s String to Configuration file
	 */
	public void loadConfigFile(String s, boolean Sender_v4, boolean Sender_v6, boolean Receiver_v4, boolean Receiver_v6){
		loadConfig(s, false, Sender_v4, Sender_v6, Receiver_v4, Receiver_v6);
	}
	
	/**
	 * Speichert eine Konfigurationsdatei.
	 * @param path Pfad zur Konfigurationsdatei.
	 * @param complete Wenn true gesetzt, wird der Standardpfad genommen.
	 * @param v Alle zu speichernden Multicasts.
	 */
	private void saveConfig(String path, boolean complete, Vector<MulticastData> v){
		final String p = "MultiCastor.xml";	

		try{	// Uebergibt den Vektor mit allen Multicasts an den XMLParser
			if(complete){
				xml_parser.saveConfig(p, v, userlevelData, userInputData, lastConfigs);
			//	logger.log(Level.INFO, "Saved default Configfile.");
			} else {
				xml_parser.saveConfig(path, v, userlevelData);
				addLastConfigs(path);
				logger.log(Level.INFO, "Saved Configfile.");
			}		
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not save default Configurationfile.");
		}
	}
	
	/**
	 * Speichert die Standardkonfigurationsdatei.
	 */
	private void saveCompleteConfig(){
		Vector<MulticastData> v = new Vector<MulticastData>();
		v.addAll(mc_sender_v4);
		v.addAll(mc_sender_v6);
		v.addAll(mc_receiver_v4);
		v.addAll(mc_receiver_v6);
		
		saveConfig("",true,v);
	}
	
	/**
	 * Laedt eine Konfigurationsdatei und fuegt markierte Multicasts hinzu.
	 * @param path Pfad zur Konfigurationsdatei, die geladen werden soll.
	 * @param complete Wenn hier true gesetzt ist, wird der Standardpfad genommen und MCD + UID + ULD geladen.
	 * @param Sender_v4 Wenn true wird Sender_v4 geladen.
	 * @param Sender_v6 Wenn true wird Sender_v6 geladen.
	 * @param Receiver_v4 Wenn true wird Receiver_v4 geladen.
	 * @param Receiver_v6 Wenn true wird Receiver_v4 geladen.
	 */
	private void loadConfig(String path, boolean complete, boolean Sender_v4, boolean Sender_v6, boolean Receiver_v4, boolean Receiver_v6){
		final String p = "MultiCastor.xml";
		// Diese Vektoren werden geladen
		Vector<MulticastData> multicasts = new Vector<MulticastData>();
		String message = new String();
		boolean skip = false;
		if(complete){
			try {
				 xml_parser.loadConfig(p,multicasts,userlevelData,userInputData,lastConfigs);
				 logger.log(Level.INFO, "Default Configurationfile loaded.");
			} catch (Exception e) {
				if(e instanceof FileNotFoundException){
					message = "Default configurationfile was not found. MultiCastor starts without preconfigured Multicasts and with default GUI configuration.";
				} else if (e instanceof SAXException) {
					message = "Default configurationfile could not be parsed correctly. MultiCastor starts without preconfigured Multicasts and with default GUI configuration.";
				} else if (e instanceof IOException) {
					message = "Default configurationfile could not be loaded. MultiCastor starts without preconfigured Multicasts and with default GUI configuration.";
				} else if (e instanceof WrongConfigurationException) {
					message = ((WrongConfigurationException)e).getErrorMessage();
				} else if (e instanceof IllegalArgumentException){
					message = "Error in default configurationfile.";
				} else {
					message = "Unexpected error of type: "+ e.getClass();
				}
				skip = true;
				logger.log(Level.WARNING, message);
			}
		} else {
			try {
				 xml_parser.loadConfig(path,multicasts,userlevelData);
				 logger.log(Level.INFO, "Configurationfile loaded: "+path);
			} catch (Exception e) {
				if(e instanceof FileNotFoundException){
					message = "Configurationfile not found.";	
				} else if (e instanceof SAXException) {
					message = "Configurationfile could not be parsed.";
				} else if (e instanceof IOException) {
					message = "Configurationfile could not be loaded.";
				} else if (e instanceof WrongConfigurationException) {
					message = ((WrongConfigurationException)e).getErrorMessage();
				} else if (e instanceof IllegalArgumentException){
					message = "Error in configurationfile.";
				} else {
					message = "Unexpected error of type: "+ e.getClass();
				}
				skip = true;
				message = message + " Used path: " + path;
				logger.log(Level.WARNING, message);
			}
		}	    
	    if(!skip){
	    	 // Füge Multicast hinzu
	    	 for(MulticastData m : multicasts){
	    		 if 	( 	((m.getTyp().equals(Typ.RECEIVER_V4)&&Receiver_v4	))||
		 	    			((m.getTyp().equals(Typ.RECEIVER_V6)&&Receiver_v6	))||
		 	    			((m.getTyp().equals(Typ.SENDER_V4)	&&Sender_v4		))||
		 	    			((m.getTyp().equals(Typ.SENDER_V6)	&&Sender_v6		))
		 	    		){
	 	    		view_controller.addMC(m);
	    		 }
	    	 }
	    	 
	 	    if(userlevelData.size() < 4){
	 	    	// log("Error in loadConfigFile - ConfigFile did not contain 12 userLevelData objects (this is ignored for test purposes)");
	 	    	logger.log(Level.INFO,"In the Configfile were less than 4 UserlevelData objects. Default ULD will be used.");
	 	    }
	    }
	    view_controller.loadAutoSave();
	}
	
	/**
	 * Fuegt zum lastConfigs-Vektor einen Pfad hinzu
	 * @param path Pfad zur Konfigurationsdatei
	 */
	private void addLastConfigs(String path){
		if(lastConfigs.size()<3){
			lastConfigs.add(0,path);
		} else {
			lastConfigs.remove(2);
			lastConfigs.add(0,path);
		}
	}
	
	/**
	 * Laedt die Configurationsdatei, die im gleichen Ordner wie MultiCastor liegt.
	 */
	public void loadCompleteConfig(){
		loadConfig("", true, true, true, true, true);
	}
	
	/**
	 * Laedt die ULD-Objekte aus dem JAR-file.
	 */
	private void defaultUserlevelDataLaden(){
		try {
			xml_parser.loadDefaultULD(defaultValuesUserlevelData, userlevelDataDefault);
		} catch (Exception e) { // darf nicht passieren
			logger.log(Level.SEVERE, "Default UserlevelData could not be loaded.");		
//			System.out.println(((WrongConfigurationException) e).getErrorMessage());
//			e.printStackTrace();
//			System.out.println(e.getClass());
		}
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
		Vector<UserlevelData> u = new Vector<UserlevelData>(); // wird nicht wirklich genutzt.	
		xml_parser.loadConfig(path,v,u);
		if(v!=null){
			for(MulticastData m : v){
				//System.out.println("Found Multicast: " + m);
				addMC(m); // hier vllt. nur adden wenn man auch starten will, da das leider nicht mehr geht
			/*	if(m.isActive()){ // Startet auf aktiv gesetzte Multicasts aus der Konfigurationsdatei
				//	System.out.println("Started Multicast: " + m);
					startMC(m);
				} */
			}
		}
	}
	
	// ****************************************************
	// Other-Stuff
	// ****************************************************
	
	/**
	 * 
	 * Adds up the measuredPacketRate from MulticastSenders
	 * @param typ Specifies wehter Sender_v4 or Sender_v6 is returned.
	 * @return Sum of all sent packets. Returns 0 if typ is invalid.
	 */
	public int getPPSSender(MulticastData.Typ typ) {
		int count = 0;
		
		if(typ.equals(MulticastData.Typ.SENDER_V4)){
			for(MulticastData ms: mc_sender_v4){
				count += ((MulticastSenderInterface) mcMap_sender_v4.get(ms)).getMultiCastData().getPacketRateMeasured();
			}
		} else if(typ.equals(MulticastData.Typ.SENDER_V6)){
			for(MulticastData ms: mc_sender_v6){
				count += ((MulticastSenderInterface) mcMap_sender_v6.get(ms)).getMultiCastData().getPacketRateMeasured();
			}
		}	
		return count;
	}
	
	/**
	 * Stops all Multicasts Threads and removes them from corresponding vectors.
	 */
	public void destroy(){
		//System.out.println("test");
		saveCompleteConfig();
		Map<MulticastData, MulticastThreadSuper> v = null;
		for(int i=0; i<4; i++){
			switch(i){
				case 0: v = mcMap_sender_v4; break;
				case 1: v = mcMap_sender_v6; break;
				case 2: v = mcMap_receiver_v4; break;
				case 3: v = mcMap_receiver_v6; break;
			}
			for(Entry<MulticastData, MulticastThreadSuper> m : v.entrySet()){
				m.getValue().setActive(false);
			}
			v.clear();
		}	
		//	System.out.println("destroy 1");
			
			// Er sollte definitiv noch auf die laufenden Threads warten..... das fehlt hier jetzt noch
			// join() hat leider kein Ende gefunden.
		/*	for(Map.Entry<MulticastData, Thread> m: threads.entrySet()){
				try {
					m.getValue().join();
				} catch (InterruptedException e) {
					logger.log(Level.INFO, "Thread got interrupted while dying");
				//	e.printStackTrace();
				}
			} */
		
		for(int i=0;i<4;i++){
			Vector<MulticastData> vector = null;
			switch(i){
				case 0: vector = mc_receiver_v4; break;
				case 1: vector = mc_receiver_v6; break;
				case 2: vector = mc_sender_v4; break;
				case 3: vector = mc_sender_v6; break;
			}
			vector.removeAllElements();
		}
		for(int i=0;i<4;i++){
			Map<MulticastData, MulticastThreadSuper> vector = null;
			switch(i){
				case 0: vector = mcMap_receiver_v4; break;
				case 1: vector = mcMap_receiver_v6; break;
				case 2: vector = mcMap_sender_v4; break;
				case 3: vector = mcMap_sender_v6; break;
			}
			vector.clear();
		}
		userlevelData.removeAllElements();
	}
	
	
	// ****************************************************
	// Ein Hilfsfunktionen vor allem fuer mich, teils auch fuer Daniel
	// ****************************************************
	
	/**
	 * Gibt das MulticastData-Objekt an der Stelle im Vektor zurueck.
	 * @param index Index des MulticastData-Objekts
	 * @param multicastDataTyp Der MulticastDatentyp um den entsprechenden Vektor zu bestimmt.
	 * @return Das MulticastData-Objekt an der entsprechenden Stelle im Vektor. Gibt <code>null</code> zurueck, wenn das Objekt nicht existiert.
	 */
	public MulticastData getMC(int index,MulticastData.Typ multicastDataTyp){
		try{
			return (MulticastData) getMCVector(multicastDataTyp).get(index);
		}catch(IndexOutOfBoundsException e){
			logger.log(Level.SEVERE, "IndexOutOfBoundsException in MulticastController - getMC");
			return null;
		}
	}

	/**
	 * Gibt den Vektor mit MulticastData-Objekten in dem alle Objekte des Typs gespeichert sind.
	 * @param m Gibt den Typ der MulticastData-Objekte an.
	 * @return Vektor mit MulticastData-Objekten des angegebenen Typs. Ist der Typ <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	public Vector<MulticastData> getMCs(MulticastData.Typ m){
		return getMCVector(m);
	}
	
	/**
	 * Gibt den Vektor mit MulticastData-Objekten in dem alle Objekte des Typs gespeichert sind.
	 * @param m Der Typ wird dem uebergebenen MulticastData-Objekt entnommen.
	 * @return Vektor mit MulticastData-Objekten des angegebenen Typs. Ist der Typ <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Vector<MulticastData> getMCVector(MulticastData m){
		return getMCVector(m.getTyp());
	}
	
	/**
	 * Gibt den Vektor mit MulticastData-Objekten in dem alle Objekte des Typs gespeichert sind.
	 * @param m Gibt den Typ der MulticastData-Objekte an.
	 * @return Vektor mit MulticastData-Objekten des angegebenen Typs. Ist der Typ <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Vector<MulticastData> getMCVector(MulticastData.Typ multicastDataTyp){
		Vector<MulticastData> vector = null;
		switch(multicastDataTyp){
			case RECEIVER_V4: vector = mc_receiver_v4;break;
			case RECEIVER_V6: vector = mc_receiver_v6;break;
			case SENDER_V4: vector = mc_sender_v4;break;
			case SENDER_V6: vector = mc_sender_v6;break;
			default: logger.log(Level.SEVERE, "Uebergebener Typ in getMCs im MulticastController ist UNDEFINED.");return null;
		}
		return vector;
	}
	
	/**
	 * Gibt die Map mit gespeicherten MulticastData-Objekten von dem entsprechenden Typ zurueck.
	 * @param multicastDataTyp Typ der MulticastData-Objekte in der Map.
	 * @return Gibt die entsprechende Map zurueck. Ist der Typ <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Map<MulticastData,MulticastThreadSuper> getMCMap(MulticastData.Typ multicastDataTyp){
		Map<MulticastData,MulticastThreadSuper> map = null;
		switch(multicastDataTyp){
			case RECEIVER_V4: map = mcMap_receiver_v4;break;
			case RECEIVER_V6: map = mcMap_receiver_v6;break;
			case SENDER_V4: map = mcMap_sender_v4;break;
			case SENDER_V6: map = mcMap_sender_v6;break;
			default: logger.log(Level.SEVERE, "Uebergebener Typ in getMCs im MulticastController ist UNDEFINED.");return null;
		}
		return map;
	}
	
	/**
	 * Gibt die Map mit gespeicherten MulticastData-Objekten von dem entsprechenden Typ zurueck.
	 * @param m Wird genutzt um den Typ zu bestimmen.
	 * @return Gibt die entsprechende Map zurueck. Ist der Typ <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Map<MulticastData,MulticastThreadSuper> getMCMap(MulticastData m){
		return getMCMap(m.getTyp());
	}
}
