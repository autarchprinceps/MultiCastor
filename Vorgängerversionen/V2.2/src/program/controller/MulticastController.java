package program.controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.data.UserInputData;
import program.interfaces.MulticastSenderInterface;
import program.interfaces.MulticastThreadSuper;
import program.model.MulticastReceiver;
import program.model.MulticastReceiverMMRP;
import program.model.MulticastSender;
import program.model.MulticastSenderMMRP;
import program.model.RegularLoggingTask;
import program.model.UpdateTask;
import program.model.XmlParserConfig;
import program.model.XmlParserStream;
import program.view.FrameFileChooser;
import program.view.StreamImport;

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
	private Vector<MulticastData> mc_sender;
	/** Haelt Referenzen auf alle MulticastData-Objekte vom Typ Sender_v6 */
//	private Vector<MulticastData> mc_sender_v6;
	/** Haelt Referenzen auf alle MulticastData-Objekte vom Typ Receiver_v4 */
	private Vector<MulticastData> mc_receiver;
	/** Haelt Referenzen auf alle MulticastData-Objekte vom Typ Receiver_v6 */
//	private Vector<MulticastData> mc_receiver_v6;

	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
	private Map<MulticastData,MulticastThreadSuper> mcMap_sender;
	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
//	private Map<MulticastData,MulticastThreadSuper> mcMap_sender_v6;
	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
	private Map<MulticastData,MulticastThreadSuper> mcMap_receiver;
	/** In der Map werden MulticastData-Objekte auf MulticastTrehadSuper-Objekte abgebildet um die Verbindung von MulticastData-Objektu zu MulticastThreadSuper herstellen zu koennen. */
//	private Map<MulticastData,MulticastThreadSuper> mcMap_receiver_v6;
	/** Diese Map bildet MulticastData-Objekte auf Threads ab, um von einem Multicast direkt mit dem entsprechenden Thread kommunizieren zu koennen. Dies wird vor allem beim Beenden der Multicasts genutzt. */
	private Map<MulticastData, Thread> threads;
	/** Interner Threadcounter, der beim Anlegen oder Starten eines MulticastSenders hochgezaehlt wird und diesem den Wert als ThreadID mitgibt. */
	private int threadCounter = 0;
	/** Der ViewController stellt eine Funktion zur Ausgabe von MessageBoxen bereit. */
	private ViewController view_controller;
	/** Der XMLParser wird zum Speichern und Laden von Konfigurationsdateien genutzt. */
	private XmlParserConfig xml_parser;
	/** XMLParser f�r Profile */
	private XmlParserStream xml_parser_profile;
	/** Der Logger speichert Daten in einer Logdatei */
	private Logger logger;
	/** Der UpdateTask sorgt fuer Aktualisierungen in der Datenhaltung und der View. */
	private UpdateTask updateTask;
	/** Referenz auf einen Timer fuer updateTask */
	private Timer timer1;
	/** Referenz auf einen Timer fuer messageCheck */
//	private Timer timer2;
//	/** Referenz auf einen Timer fuer regularLoggingTask */
	private Timer timer3;
	/** Loesst eine Aktualisierung der Durchschnittswerte in der Datenhaltung aus und speichert diese mittels Logger. */
	private RegularLoggingTask regularLoggingTask;

	// Config
	/** Informationen zum wiederherstellen des letzten GUI Status */
	private UserInputData userInputData;
	/** In diesem Vektor werden die default UserLevelData-Objekte gespeichert. Daher sollte er jederzeit 12 Objekte enthalten. Eines fuer jeden moeglichen GUI-Status. */
	private Vector<String> lastConfigs;
	/** Speicher die Standardwerte fuer Userlevel: Beginner */



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
		mc_sender = new Vector<MulticastData>();
//		mc_sender_v6 = new Vector<MulticastData>();
		mc_receiver = new Vector<MulticastData>();
//		mc_receiver_v6 = new Vector<MulticastData>();
		// Thread-Maps
		mcMap_sender = new HashMap<MulticastData,MulticastThreadSuper>();
//		mcMap_sender_v6 = new HashMap<MulticastData,MulticastThreadSuper>();
		mcMap_receiver = new HashMap<MulticastData,MulticastThreadSuper>();
//		mcMap_receiver_v6 = new HashMap<MulticastData,MulticastThreadSuper>();
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
				System.out.println("Fehler im MulticastController: Logger-Objekt ist null"); //$NON-NLS-1$
			}

		// **************************************************************
		// Config file stuff
		// **************************************************************
		xml_parser = new XmlParserConfig();
		xml_parser_profile = new XmlParserStream(logger);
		lastConfigs = new Vector<String>();
		userInputData = new UserInputData();

	//	AllesLaden(); // sollte von Thomas gemacht werden

		updateTask = new UpdateTask(logger,
				mcMap_sender,
//				mcMap_sender_v6,
				mcMap_receiver,
//				mcMap_receiver_v6,
				view_controller);
		timer1 = new Timer();
		timer1.schedule(updateTask, 3000,1000);

	//	messageQueue = new LinkedList<String>();
	//	messageCheck = new MessageCheck(messageQueue, view_controller, this.logger);

		regularLoggingTask = new RegularLoggingTask(logger,
				mcMap_sender,
//				mcMap_sender_v6,
				mcMap_receiver
//				,mcMap_receiver_v6
				);
		timer3 = new Timer();
		timer3.schedule(regularLoggingTask, 20000, 20000);
	}

	// ****************************************************
	// Multicast-Steuerung
	// ****************************************************

	/**
	 * Fuegt das uebergebene MulticastData-Objekt hinzu, erzeugt entsprechenden Thread und startet diesen falls notwendig.
	 * @param m MulticastData-Objekt das hinzugef�gt werden soll.
	 */
	public void addMC(MulticastData m) {
		// Erzeugt den passenden MulticastThreadSuper
		MulticastThreadSuper t = null;
		Typ typ = m.getTyp();
		if(Typ.isSender(typ)){
			if(typ==Typ.SENDER_MMRP){
				t = new MulticastSenderMMRP(m, logger);
			}else{
				t = new MulticastSender(m, logger);		}
		} else if (Typ.isReceiver(typ)){
			if(typ==Typ.RECEIVER_MMRP){
				t = new MulticastReceiverMMRP(m, logger);
			}else{
				t = new MulticastReceiver(m, logger);		}
		}
		// Fuegt Multicasts zu der entsprechenden Map hinzu
		getMCVector(m.getTyp()).add(0, m);
		getMCMap(m).put(m,t);

		// Loggt das hinzufuegen des Multicasts
		logger.log(Level.INFO, "Multicast added: " + m.identify()); //$NON-NLS-1$
		// Startet den Multicast, falls notwendig
		if(m.isActive()){
			startMC(m);
		}
	}

	/**
	 * Diese Methode muss aufgerufen werden, wenn sich Einstellungen des Multicasts aendern.
	 * Zum Beispeil die GroupIP. Hier wird der vorhandene Multicast geloescht und anschlie�end
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
		getMCVector(m.getTyp()).remove(m);
		// Log des Loeschens
		logger.log(Level.INFO, "Multicast deleted: " + m.toStringConsole()); //$NON-NLS-1$
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
				long time = System.currentTimeMillis()+10000; // versucht, 10 Sekunden lang auf den noch laufenden Thread zu warten.
				while((getMCMap(m).get(m)).isStillRunning()){
					if(time < System.currentTimeMillis()){ // verhindert haengen bleiben; dies kommt in wenigen Faellen noch vor.
						logger.log(Level.SEVERE, "Could not start Multicast: " + m + " Advised a Change on that Multicast."); //$NON-NLS-1$ //$NON-NLS-2$
						return;
					}
				}

				// Thread ID nur bei Sendern setzen.
				//   Beim Receiver wird der Wert aus dem Datenpaket ausgelesen.
				if(Typ.isSender(m.getTyp())){
					m.setThreadID(threadCounter);
					threadCounter++;
				} else if(Typ.isIP(m.getTyp())){ // Receiver haben den GroupJoin ausgelagert.
					if(((MulticastReceiver) getMCMap(m).get(m)).joinGroup()){
						// Fehlermeldung und Log werden im Receiver selber ausgegeben.
						return;
					}
				} else
				{
					if(((MulticastReceiverMMRP) getMCMap(m).get(m)).joinGroup()){
						// Fehlermeldung und Log werden im Receiver selber ausgegeben.
						return;
					}


//					System.out.println("DEBUG: Hier fehlt toller Code für Receiver"); //$NON-NLS-1$
				}
				// Multicast auf aktiv setzen, einen neuen Thread erzeugen und starten.
				getMCMap(m).get(m).setActive(true);
				Thread t = new Thread(getMCMap(m).get(m));
				t.start();
				threads.put(m,t);
			} else {
				logger.log(Level.INFO, "Tried to start an already running Multicast."); //$NON-NLS-1$
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
			logger.log(Level.INFO, "Tried to stop a not running Multicast."); //$NON-NLS-1$
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
	 * Saves not checked data from View necessary to reconstruct the exact state from View.
	 * @param u Vector of UserInputData obejcts. The Vector should contain between one and four objects.
	 */
	void autoSave(UserInputData u){
		userInputData = u;
		saveConfig("MultiCastor.xml",true); //$NON-NLS-1$
	}

	/**
	 * Loads data from autoSave() method.
	 * @return Vector of UserInputData objects. This Vector contains between one and four objects. One for each tab in View at most.
	 */
	UserInputData loadAutoSave(){	// noch bei Daniel nachhoeren ob es ok ist, wenn da ein leerer Vector zur�ck kommt!!!!!!!!!
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
	public void saveConfig(String s) {
		saveConfig(s, false);
	}

	/**
	 * Laedt die Konfigurationsdatei am angegebenen Pfad. Fehlermeldungen werden ausgegeben.
	 * Diese Funktion sollte
	 * @param s String to Configuration file
	 */
	public void loadConfigFile(String s, FrameFileChooser FFC){
		loadConfig(s, FFC);
	}

	/**
	 * Speichert eine Konfigurationsdatei.
	 * @param path Pfad zur Konfigurationsdatei.
	 * @param complete Wenn true gesetzt, wird der Standardpfad genommen.
	 * @param v Alle zu speichernden Multicasts.
	 */
	private void saveConfig(String path, boolean complete){
		try{	// Uebergibt den Vektor mit allen Multicasts an den XMLParser
			xml_parser.saveConfig(path, userInputData);
			addLastConfigs(path);
			logger.log(Level.CONFIG, "Saved Configfile to: " + (new File(path)).getAbsolutePath()); //$NON-NLS-1$
			
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not save default Configurationfile!"); //$NON-NLS-1$
		}
	}

	/**
	 * Speichert eine Profildatei.
	 * @param path Pfad zur Profildatei.
	 * @param v Alle zu speichernden Multicasts.
	 */
	public void saveProfile(String path, Vector<MulticastData> v){
		try{
			// Uebergibt den Vektor mit allen Multicasts an den XMLParser
			xml_parser_profile.saveConfig(path, v);
			logger.log(Level.CONFIG, "Saved Profile"); //$NON-NLS-1$
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not save profile."); //$NON-NLS-1$
		}
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
	private void loadConfig(String path, FrameFileChooser FFC ){
		String p;
		if (path.equals("")) {
			p = "MultiCastor.xml"; //$NON-NLS-1$
		} else {
			p = path;
		}
				
		// Diese Vektoren werden geladen
		String message = new String();
			try {
				 xml_parser.loadConfig(p,userInputData);
				 logger.log(Level.CONFIG, "Default Configurationfile loaded."); //$NON-NLS-1$
				 if (FFC!=null)
					 FFC.toggle();
				 
			} catch (Exception e) {
				if(e instanceof FileNotFoundException){
					message = Messages.getString("MulticastController.17"); //$NON-NLS-1$
				} else if (e instanceof SAXException) {
					message = Messages.getString("MulticastController.18"); //$NON-NLS-1$
				} else if (e instanceof IOException) {
					message = Messages.getString("MulticastController.19"); //$NON-NLS-1$´
				} else if (e instanceof IllegalArgumentException){
					message = Messages.getString("MulticastController.20"); //$NON-NLS-1$
				} else {
					message = Messages.getString("MulticastController.21"); //$NON-NLS-1$
				}
				logger.log(Level.WARNING, message);
			}
			view_controller.loadAutoSave();
	}

	public void loadProfile(String path, FrameFileChooser FFC){
		// Diese Vektoren werden geladen
		Vector<MulticastData> multicasts = new Vector<MulticastData>();
		String message = new String();
		try {
			 xml_parser_profile.loadConfig(path, multicasts);
			 logger.log(Level.CONFIG, Messages.getString("MulticastController.29")+path); //$NON-NLS-1$
			 FFC.toggle();
		} catch (Exception e) {
			if(e instanceof FileNotFoundException){
				message = Messages.getString("MulticastController.30"); //$NON-NLS-1$
			} else if (e instanceof SAXException) {
				message = Messages.getString("MulticastController.31"); //$NON-NLS-1$
			} else if (e instanceof IOException) {
				message = Messages.getString("MulticastController.32"); //$NON-NLS-1$
			} else if (e instanceof IllegalArgumentException){
				e.printStackTrace();
				message = Messages.getString("MulticastController.33"); //$NON-NLS-1$
			} else {
				message = Messages.getString("MulticastController.27"); //$NON-NLS-1$
				e.printStackTrace();
			}
			logger.log(Level.WARNING, message);
		}

		if (multicasts.size()>0)
		{
			Iterator<MulticastData> it = multicasts.iterator();  
			while(it.hasNext()){  
				//Special condition: the element is "hello3", remove it  
				if(((MulticastData)it.next()).getTyp()==Typ.UNDEFINED)  
					it.remove();  
			}
			multicasts = StreamImport.showImportDialog(view_controller.getFrame(), multicasts, this);
		}	
		
		else
			logger.log(Level.WARNING, "No Multicasts in this Configurationfile found");

		// Add loaded profiles to view
		for(MulticastData m : multicasts){
 	    		view_controller.addMC(m);
    	}
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
		loadConfig("", null); //$NON-NLS-1$
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
	public void loadConfigWithoutGUI(String path) throws FileNotFoundException, SAXException, IOException{
		UserInputData u = new UserInputData(); // wird nicht wirklich genutzt.
		xml_parser.loadConfig(path,u);
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
			for(MulticastData ms: mc_sender){
				count += ((MulticastSenderInterface) mcMap_sender.get(ms)).getMultiCastData().getPacketRateMeasured();
			}
//		} else if(typ.equals(MulticastData.Typ.SENDER_V6)){
//			for(MulticastData ms: mc_sender_v6){
//				count += ((MulticastSenderInterface) mcMap_sender_v6.get(ms)).getMultiCastData().getPacketRateMeasured();
//			}
		}
		return count;
	}

	/**
	 * Stops all Multicasts Threads and removes them from corresponding vectors.
	 */
	public void destroy(){
		saveConfig("MultiCastor.xml",true); //$NON-NLS-1$
		Map<MulticastData, MulticastThreadSuper> v = null;
		for(int i=0; i<4; i++){
			switch(i){
				case 0: v = mcMap_sender; break;
//				case 1: v = mcMap_sender_v6; break;
				case 2: v = mcMap_receiver; break;
//				case 3: v = mcMap_receiver_v6; break;
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

		for(int i=0;i<4;i++,i++){
			Vector<MulticastData> vector = null;
			switch(i){
				case 0: vector = mc_receiver; break;
//				case 1: vector = mc_receiver_v6; break;
				case 2: vector = mc_sender; break;
//				case 3: vector = mc_sender_v6; break;
			}
			vector.removeAllElements();
		}
		for(int i=0;i<4;i++,i++){
			Map<MulticastData, MulticastThreadSuper> vector = null;
			switch(i){
				case 0: vector = mcMap_receiver; break;
				case 2: vector = mcMap_sender; break;
			}
			vector.clear();
		}
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
			return getMCVector(multicastDataTyp).get(index);
		}catch(IndexOutOfBoundsException e){
			logger.log(Level.SEVERE, "IndexOutOfBoundsException in MulticastController - getMC. Index was:"+index+"size:"+ getMCVector(multicastDataTyp).size()); //$NON-NLS-1$ //$NON-NLS-2$
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
	 * Gibt den Vektor mit allen MulticastData-Objekten
	 * @return Vektor mit allen MulticastData-Objekten
	 */
	public Vector<MulticastData> getMCs(){
		return getMCVector();
	}

	/**
	 * Gibt den Vektor mit MulticastData-Objekten in dem alle Objekte des Typs gespeichert sind.
	 * @param m Gibt den Typ der MulticastData-Objekte an.
	 * @return Vektor mit MulticastData-Objekten des angegebenen Typs. Ist der Typ <code>UNDEFIENED</code> wird <code>null</code> zurueckgegeben.
	 */
	private Vector<MulticastData> getMCVector(MulticastData.Typ multicastDataTyp){
		Vector<MulticastData> vector = null;
		switch(multicastDataTyp){
			case RECEIVER_MMRP:
			case RECEIVER_V4:
			case RECEIVER_V6: vector = mc_receiver;break;
			case SENDER_MMRP:
			case SENDER_V4:
			case SENDER_V6: vector = mc_sender;break;
			default: logger.log(Level.SEVERE, "Uebergebener Typ in getMCs im MulticastController ist UNDEFINED.");return null; //$NON-NLS-1$
		}
		return vector;
	}

	/**
	 * Liefert alle MulticastData Objekte unabh�ngig vom Typ zur�ck
	 * @return Vector mit allen MulticastData Objekten
	 */
	private Vector<MulticastData> getMCVector(){
		Vector<MulticastData> vector = new Vector<MulticastData>();
		vector.addAll(mc_receiver);
//		vector.addAll(mc_receiver_v6);
		vector.addAll(mc_sender);
//		vector.addAll(mc_sender_v6);

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
			case RECEIVER_MMRP:
			case RECEIVER_V4:
			case RECEIVER_V6:map = mcMap_receiver;break;
//			case RECEIVER_V6: map = mcMap_receiver_v6;break;
			case SENDER_MMRP:
			case SENDER_V4:
			case SENDER_V6:  map = mcMap_sender;break;
//			case SENDER_V6: map = mcMap_sender_v6;break;
			default: logger.log(Level.SEVERE, "Uebergebener Typ in getMCs im MulticastController ist UNDEFINED.");return null; //$NON-NLS-1$
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