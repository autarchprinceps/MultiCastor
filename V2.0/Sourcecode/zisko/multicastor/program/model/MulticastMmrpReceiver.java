package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.logging.Level;

import java.util.logging.Logger;

import zisko.multicastor.program.lang.LanguageManager;
import zisko.multicastor.program.mmrp.*;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;


/**
 * Die MulticastMmrpReceiver-Klasse kuemmert sich um das tatsaechliche Empfangen der
 * Multicast-Objekte ueber das Netzwerk per MMRP Protokoll.
 * Sie extended{@link MulticastThreadSuper}, ist also ein Runnable. 
 * 
 * Ein MulticastMmrpReceiver hat eine Grundkonfiguration, 
 * die nicht mehr abgeaendert werden kann, wie zum
 * Beispiel die gesetzten MACs. Soll diese Grundkonfiguration geaendert werden,
 * muss eine neue Instanz de Klasse gebildet werden. Das Erleichtert die
 * nachtraegliche Analyse, Da das Objekt eindeutig einem "Test" zuordnungsbar
 * ist.
 * 
 */
public class MulticastMmrpReceiver extends MulticastThreadSuper {
	
	/**
	 * Language Manager ist wichtig fuer die multi Language Unterstuetzung 
	 */
	private LanguageManager lang = LanguageManager.getInstance();
	
	/** Wenn auf wahr, lauscht dieser Receiver auf ankommende Pakete. */
	private boolean active = false;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	/** Maximale Paketlaenge */
	private final int length = 1500;
	/** Byte Array in dem das Paket gespeichert wird. */
	private byte[] buf = new byte[length];
	/** Analysiert ankommende Pakete */
	PacketAnalyzer packetAnalyzer;
	
	private MMRPReceiver receiver;

	/**
	 * Einziger Konstruktor der Klasse (Sieht man vom Konstruktor der
	 * Superklasse ab). Im Konstruktor wird die hostID gesetzt (entspricht dem
	 * hostnamen des Geraets), der {@link MMRPReceiver} initialisiert
	 * und das Datenpaket mit dem {@link PacketBuilder} erstellt.
	 * 
	 * @param multicastData
	 *            Das {@link MulticastData}-Object, dass alle fuer den Betrieb
	 *            nuetigen Daten enthuelt.
	 * @param logger
	 *            Eine {@link Queue}, ueber den der Receiver seine Ausgaben an
	 *            den Controller weitergibt.
	 */
	public MulticastMmrpReceiver(MulticastData multicastData, Logger logger) throws IOException{
		super(multicastData);
		if(logger==null){
			System.out.println(lang.getProperty("error.mr.logger"));
			return;
		}
		if(multicastData==null){
			logger.log(Level.WARNING, lang.getProperty("error.mr.mcdata"));
			return;
		}
		
		try {
			multicastData.setHostID(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			proclaim(1, lang.getProperty("message.getHostname") + " " + e.getMessage());
		}
		
		this.logger = logger;
		packetAnalyzer = new PacketAnalyzer(mcData, logger, length);
		try {
			receiver = new MMRPReceiver(mcData.getMmrpSourceMac(), mcData.getMmrpGroupMac());
		} catch (IOException e) {
			proclaim(3, lang.getProperty("message.receiverInterfaceFail") + " (" + mcData.getMmrpSourceMacAsString() + ")");
			throw new IOException();
		}

	}
	
	/**
	 * Methode ueber die die Kommunikation zum MultiCastController realisiert wird.
	 * @param level unterscheidet zwischen Fehlern und Status-Meldungen
	 * @param mssg Die zu sendende Nachricht (String)
	 */
	private void proclaim(int level, String mssg){
		Level l;
		mssg = mcData.identify() + ": " + mssg;
		switch(level){	
			case 1:		l = Level.WARNING;
						break;
			case 2:		l = Level.INFO;
						break;
			default:	l = Level.SEVERE;
		}
		logger.log(l,mssg);
	}

	/**
	 * Wird der Methode true uebergeben, startet der Multicast 
	 * zu empfangen. 
	 * 
	 * Wird der Methode false uebergeben, stoppt 
	 * sie das empfangen der Multicasts.
	 * 
	 * @param b
	 *            boolean
	 */
	public void setActive(boolean b) {
		if(active){
			active = b;
			receiver.stopLoop();
		}
		
		if(b){
			setStillRunning(true);
			// Verhindert eine "recentlyChanged"-Markierung direkt nach dem Starten
			packetAnalyzer.setLastActivated(6);
			active = b;
		}
		
		mcData.setActive(b);
		packetAnalyzer.resetValues();		
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt
	 * und resetet den internen Paket-Counter
	 */
	public void update() {
		packetAnalyzer.update();
		// damit die Paketlaenge beim naechsten Paket erneut bestimmt werden kann
		//		groessere Pakete fallen immer auf, jedoch sind kleinere Pakete nur so erkennbar
		initializeBuf();
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt.
	 * Dieser Counter summiert die gemessene Paketrate,
	 * die mit der update()-Funktion ins MultiCastData-Objekt geschrieben wird.
	 * Diese Summe wird bei jedem Aufruf resetet.
	 */
	public void updateMin() {
		packetAnalyzer.updateMin();
	}

	/**
	 * Schreibt lauter Einsen in den Buffer.
	 */
	private void initializeBuf(){
		for(int i = 0; i<buf.length; i++){
			buf[i] = 1;
		}
	}

	/**
	 * Hier geschieht das eigentliche Empfangen. 
	 * 
	 * Beim Starten des Threads wird probiert, denn Mmrp Pfad zu registrieren. 
	 * Gelingt dies nicht, wird ein Fehler ausgegeben und das 
	 * Empfangen wird garnicht erst gestartet. Gelingt
	 * das registrieren, wird so lange gewartet, bis setActive(false) aufgerufen
	 * wird.
	 */
	public void run() {
		
		boolean isStopped;
		
		// Initialisiert den Buffer mit Einsen
		initializeBuf();

		try {
			receiver.registerPath();
		} catch (IOException e) {
			proclaim(3, lang.getProperty("message.registerReceiverPath"));
			this.setActive(false);
		} catch (NullPointerException e) {
			proclaim(1, lang.getProperty("message.jnetpcapNotInstalled"));
			this.setActive(false);
		}
		
		while(active){
			isStopped = receiver.waitForDataPacketAndGetIt(buf);
			//packetAnalyzer.setTimeout(false);
			if(isStopped){
				packetAnalyzer.analyzePacket(buf);
				initializeBuf();
			}
		}
		
		// Resetted gemessene Werte (SenderID bleibt erhalten, wegen des Wiedererkennungswertes)
		packetAnalyzer.resetValues();
		packetAnalyzer.update();
		packetAnalyzer.updateMin();
		
		// Thread ist beendet
		setStillRunning(false);
		
		try {
			receiver.deregisterPath();
			
			
		} catch (IOException e) {
			proclaim(3, lang.getProperty("message.deregisterReceiverPath"));
		} catch (NullPointerException e) {
			proclaim(1, lang.getProperty("message.jnetpcapNotInstalled"));
		}
	}

}
