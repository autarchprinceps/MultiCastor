package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;


public class MulticastReceiver extends MulticastThreadSuper {
	/** Javasocket fuer Multicasts. */
	private MulticastSocket multicastSocket;
	/** Wenn auf wahr, lauscht dieser Receiver auf ankommende Pakete. */
	private boolean active = false;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	/** Maximale Paketlaenge */
	private final int length = 65575;
	/** Byte Array in dem das Paket gespeichert wird. */
	private byte[] buf = new byte[length];
	/** Analysiert ankommende Pakete */
	PacketAnalyzer packetAnalyzer;

	/**
	 * Ein MulticastReceiver entspricht genau einem MulticastData-Objekt vom Receiver Typ. Der MulticastReceiver ist
	 * fuer das Beitreten und Verlassen der MulticastGruppen zustaending. Er kann gestartet und gestoppt werden.
	 * @param m MulticastData-Objekt mit GroupIP und Port Informationen. Alle ermittelten Informationen werden hier rein geschrieben.
	 * @param logger Wird genutzt um Status- und Fehlermeldungen auszugeben. Diese werden in die MessageQueue geschrieben.
	 */
	public MulticastReceiver(MulticastData m, Logger logger){
		super(m);
		if(logger==null){
			System.out.println("FATAL ERROR - cannot create MulticastReceiver without Logger");
			return;
		}
		if(m==null){
			logger.log(Level.WARNING, "Error while creating MulticastReceiver. MulticastData cannot be empty.");
			return;
		}
		
		this.logger = logger;
		
		// Erzeuge Socket mit Port; nutze Standardport falls eingegebener Port nicht funktioniert.
		try {
			multicastSocket = new MulticastSocket(m.getUdpPort());
			// Der Empfaenger nutzt alle verfuegbaren Netzwerkschnittstellen
			//	multicastSocket.setInterface(mcData.getSourceIp());
		}catch(IOException e){
			logger.log(Level.WARNING, "'" + e.getMessage() + "' Versuche default Port 4711 zu setzen...");
			try{
				int udpPort = 4711;
				mcData.setUdpPort(udpPort);
				multicastSocket = new MulticastSocket(udpPort);
			}catch(IOException e2){
				logger.log(Level.WARNING, e.getMessage());
				return;
			}
		}	
		packetAnalyzer = new PacketAnalyzer(mcData, logger);
		// resets MulticastData Object to avoid default value -1
		mcData.resetValues();
	}
	
	/**
	 * Setzt alle Vorraussetzungen damit der MulticastReceiver mit einem Thread gestartet werden kann.
	 * Hierbei muss der MulticastController sicherstellen, dass kein weiterer Thread mit diesem Objekt laeuft.
	 */
	@Override
	public void setActive(boolean b) {
		if(b){
			setStillRunning(true);
			// Verhindert eine "recentlyChanged"-Markierung direkt nach dem Starten
			packetAnalyzer.setLastActivated(6);
		}
		active = b;
		mcData.setActive(b);
	// Values will be resetted when Receiver actually stops. Just before calling update functions in PacketAnalyzer
	//	packetAnalyzer.resetValues();
	}
	
	/**
	 * Der MulticastReciever tritt der, im MulticastData-Objekt bei der Initialisierung angegebenen, Multicastgruppe bei.
	 * @return Gibt <code>true</code> zurueck, wenn der Multicastgruppe nicht beigetreten werden konnte.
	 */
	public boolean joinGroup(){
		//********************************************
		// Join MultiCast-Group
		//********************************************
		try {
			multicastSocket.joinGroup(mcData.getGroupIp());
			logger.log(Level.INFO, mcData.identify() + ": Started Receiver with MC Object.");
		} catch (IOException e1) {
			// Setzt Aktivwerte wieder auf false und gibt eine Fehlermeldung aus
			mcData.setActive(false);
			active = false;
			logger.log(Level.WARNING, mcData.identify() + ":Could not start Receiver." + e1.getMessage());
			return true;
		}
		return false;
	}
	
	/**
	 * Veranlasst den MulticastReceiver die Multicastgruppe zu verlassen.
	 */
	private void leaveGroup(){
		//********************************************
		// Leave MultiCast-Group
		//********************************************
		try {
			multicastSocket.leaveGroup(mcData.getGroupIp());
			logger.log(Level.INFO, mcData.identify() + ": Left Multicastgroup of MC Object.");
		} catch (IOException e) {
			logger.log(Level.WARNING, mcData.identify() + ": Could not leave MC group of MC Object.");
		}
	}
	
	/**
	 * Wartet auf einkommende Pakete bis der MulticastReceiver ueber setActive(false) deaktiviert wird.
	 * Empfangene Pakete werden an den PacketAnalyzer gegeben und dort analysiert.
	 */
	@Override
	public void run() {
		/*
		 * JoinGroup wurde in den MulticastController verlagert, da
		 * es hier oefters zu Problemen kam, wenn ein Receiver sehr 
		 * oft und schnell hinter einander gestartet und gestoppt 
		 * wurde.
		 */
		
		// Initialisiert den Buffer mit Einsen
		initializeBuf();
		
		// local variables
		DatagramPacket datagram = new DatagramPacket(buf, length);

		while(active){
			try {
				multicastSocket.setSoTimeout(1000);
				multicastSocket.receive(datagram);
				packetAnalyzer.setTimeout(false);
				//********************************************
				// Analyse received packet
				//********************************************
				packetAnalyzer.analyzePacket(datagram.getData());
			} catch (IOException e) {
				if(e instanceof SocketTimeoutException){
					// bei Timeout erfaehrt es der PacketAnalyzer wegen Interrupts
					packetAnalyzer.setTimeout(true);
				} else {
					logger.log(Level.WARNING, "Error while receiving packets: " + e.getMessage());
				}
			}
		}
		// Verlaesst die Multicast Gruppe
		leaveGroup();
		// Resetted gemessene Werte (SenderID bleibt erhalten, wegen des Wiedererkennungswertes)
		packetAnalyzer.resetValues();
		packetAnalyzer.update();
		packetAnalyzer.updateMin();
		// Thread ist beendet
		setStillRunning(false);
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
	 * Gibt eine Referenz auf das MulticastData-Objekt zurueck
	 * @return MulticastData-Objekt mit dem dieser MulticastReceiver erstellt wurde
	 */
	@Override
	public MulticastData getMultiCastData() {
		return mcData;
	}

	/**
	 * Setzt den InternenPacketCount
	 * @param packetCount Wert auf den der interne PacketCount gesetzt wird
	 */
/*	public void setInternerPacketCount(int packetCount){
		packetAnalyzer.setInternerPacketCount(packetCount);
	}*/
	
	/**
	 * Updated die Durchschnittswerte im MulticastData-Objekt
	 */
	@Override
	public void updateMin() {
		packetAnalyzer.updateMin();
	}
	
	/**
	 * Updated die Werte im MulticastData-Objekt
	 */
	@Override
	public void update() {
		packetAnalyzer.update();
		// damit die Paketlaenge beim naechsten Paket erneut bestimmt werden kann
		//		groessere Pakete fallen immer auf, jedoch sind kleinere Pakete nur so erkennbar
		initializeBuf();
	}
}
