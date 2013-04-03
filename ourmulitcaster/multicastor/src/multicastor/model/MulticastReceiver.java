package multicastor.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import multicastor.data.MulticastData;
import multicastor.interfaces.MulticastThreadSuper;
import multicastor.lang.LanguageManager;

/**
 * Ein MulticastReceiver entspricht genau einem MulticastData-Objekt vom
 * Receiver Typ. Der MulticastReceiver ist fuer das Beitreten und Verlassen der
 * MulticastGruppen zustaending. Er kann gestartet und gestoppt werden. Sie
 * extended {@link MulticastThreadSuper}, ist also ein Runnable.
 */
public class MulticastReceiver extends MulticastThreadSuper {

	/** Analysiert ankommende Pakete */
	PacketAnalyzer packetAnalyzer;

	/** Wenn auf wahr, lauscht dieser Receiver auf ankommende Pakete. */
	private boolean active = false;
	/** Byte Array in dem das Paket gespeichert wird. */
	private final byte[] buf;
	/**
	 * Language Manager ist wichtig fuer die multi Language Unterstuetzung
	 */
	private final LanguageManager lang;
	/** Maximale Paketlaenge */
	private final int length;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	/** Javasocket fuer Multicasts. */
	private MulticastSocket multicastSocket;

	/**
	 * Ein MulticastReceiver entspricht genau einem MulticastData-Objekt vom
	 * Receiver Typ. Der MulticastReceiver ist fuer das Beitreten und Verlassen
	 * der MulticastGruppen zustaending. Er kann gestartet und gestoppt werden.
	 * 
	 * @param m
	 *            MulticastData-Objekt mit GroupIP und Port Informationen. Alle
	 *            ermittelten Informationen werden hier rein geschrieben.
	 * @param logger
	 *            Wird genutzt um Status- und Fehlermeldungen auszugeben. Diese
	 *            werden in die MessageQueue geschrieben.
	 */
	public MulticastReceiver(final MulticastData m, final Logger logger) {
		super(m);
		
		length = 65575;
		buf = new byte[length];
		lang = LanguageManager.getInstance();
		
		if(logger == null) {
			System.out.println(lang.getProperty("error.mr.logger"));
			return;
		}
		if(m == null) {
			logger.log(Level.WARNING, lang.getProperty("error.mr.mcdata"));
			return;
		}

		this.logger = logger;

		// Erzeuge Socket mit Port; nutze Standardport falls eingegebener Port
		// nicht funktioniert.
		try {
			multicastSocket = new MulticastSocket(m.getUdpPort());
			// Der Empfaenger nutzt alle verfuegbaren Netzwerkschnittstellen
			multicastSocket.setInterface(mcData.getSourceIp());
		} catch(final IOException e) {
			logger.log(
					Level.WARNING,
					"'" + e.getMessage() + "' "
							+ lang.getProperty("message.defaultPort4711"));
			try {
				final int udpPort = 4711;
				mcData.setUdpPort(udpPort);
				multicastSocket = new MulticastSocket(udpPort);
			} catch(final IOException e2) {
				logger.log(Level.WARNING, e.getMessage());
				return;
			}
		}
		packetAnalyzer = new PacketAnalyzer(mcData, logger, length);
		// resets MulticastData Object to avoid default value -1
		mcData.resetValues();
	}

	/**
	 * Gibt eine Referenz auf das MulticastData-Objekt zurueck
	 * 
	 * @return MulticastData-Objekt mit dem dieser MulticastReceiver erstellt
	 *         wurde
	 */
	@Override
	public MulticastData getMultiCastData() {
		return mcData;
	}

	/**
	 * Der MulticastReciever tritt der, im MulticastData-Objekt bei der
	 * Initialisierung angegebenen, Multicastgruppe bei.
	 * 
	 * @return Gibt <code>true</code> zurueck, wenn der Multicastgruppe nicht
	 *         beigetreten werden konnte.
	 */
	public boolean joinGroup() {
		// ********************************************
		// Join MultiCast-Group
		// ********************************************
		try {
			multicastSocket.joinGroup(mcData.getGroupIp());
			logger.log(
					Level.INFO,
					mcData.identify() + ": "
							+ lang.getProperty("message.receiverMcObj"));
		} catch(final IOException e1) {
			// Setzt Aktivwerte wieder auf false und gibt eine Fehlermeldung aus
			mcData.setActive(false);
			active = false;
			logger.log(
					Level.WARNING,
					mcData.identify() + ": "
							+ lang.getProperty("message.receiverStartFail")
							+ " " + e1.getMessage());
			return true;
		}
		return false;
	}

	/**
	 * Wartet auf einkommende Pakete bis der MulticastReceiver ueber
	 * setActive(false) deaktiviert wird. Empfangene Pakete werden an den
	 * PacketAnalyzer gegeben und dort analysiert.
	 */
	@Override
	public void run() {
		/*
		 * JoinGroup wurde in den MulticastController verlagert, da es hier
		 * oefters zu Problemen kam, wenn ein Receiver sehr oft und schnell
		 * hinter einander gestartet und gestoppt wurde.
		 */

		// Initialisiert den Buffer mit Einsen
		initializeBuf();

		// local variables
		final DatagramPacket datagram = new DatagramPacket(buf, length);

		while(active) {
			try {
				multicastSocket.setSoTimeout(1000);
				multicastSocket.receive(datagram);
				packetAnalyzer.setTimeout(false);
				// ********************************************
				// Analyse received packet
				// ********************************************
				packetAnalyzer.analyzePacket(datagram.getData());
			} catch(final IOException e) {
				if(e instanceof SocketTimeoutException) {
					// bei Timeout erfaehrt es der PacketAnalyzer wegen
					// Interrupts
					packetAnalyzer.setTimeout(true);
				} else {
					logger.log(Level.WARNING,
							lang.getProperty("message.packetReceiver") + " "
									+ e.getMessage());
				}
			}
		}
		// Verlaesst die Multicast Gruppe
		leaveGroup();
		// Resetted gemessene Werte (SenderID bleibt erhalten, wegen des
		// Wiedererkennungswertes)
		packetAnalyzer.resetValues();
		packetAnalyzer.update();
		packetAnalyzer.updateMin();
		// Thread ist beendet
		setStillRunning(false);
	}

	/**
	 * Setzt alle Vorraussetzungen damit der MulticastReceiver mit einem Thread
	 * gestartet werden kann. Hierbei muss der MulticastController
	 * sicherstellen, dass kein weiterer Thread mit diesem Objekt laeuft.
	 */
	@Override
	public void setActive(final boolean b) {
		if(b) {
			setStillRunning(true);
			// Verhindert eine "recentlyChanged"-Markierung direkt nach dem
			// Starten
			packetAnalyzer.setLastActivated(6);
		}
		active = b;
		mcData.setActive(b);
		// Values will be resetted when Receiver actually stops. Just before
		// calling update functions in PacketAnalyzer
		packetAnalyzer.resetValues();
	}

	/**
	 * Updated die Werte im MulticastData-Objekt
	 */
	@Override
	public void update() {
		packetAnalyzer.update();
		// damit die Paketlaenge beim naechsten Paket erneut bestimmt werden
		// kann
		// groessere Pakete fallen immer auf, jedoch sind kleinere Pakete nur so
		// erkennbar
		initializeBuf();
	}

	/**
	 * Updated die Durchschnittswerte im MulticastData-Objekt
	 */
	@Override
	public void updateMin() {
		packetAnalyzer.updateMin();
	}

	/**
	 * Setzt den InternenPacketCount
	 * 
	 * @param packetCount
	 *            Wert auf den der interne PacketCount gesetzt wird
	 */
	/*
	 * public void setInternerPacketCount(int packetCount){
	 * packetAnalyzer.setInternerPacketCount(packetCount); }
	 */

	/**
	 * Schreibt lauter Einsen in den Buffer.
	 */
	private void initializeBuf() {
		for(int i = 0; i < buf.length; i++) {
			buf[i] = 1;
		}
	}

	/**
	 * Veranlasst den MulticastReceiver die Multicastgruppe zu verlassen.
	 */
	private void leaveGroup() {
		// ********************************************
		// Leave MultiCast-Group
		// ********************************************
		try {
			multicastSocket.leaveGroup(mcData.getGroupIp());
			logger.log(
					Level.INFO,
					mcData.identify() + ": "
							+ lang.getProperty("message.mcgroupMcobj"));
		} catch(final IOException e) {
			logger.log(
					Level.WARNING,
					mcData.identify() + ": "
							+ lang.getProperty("message.mcgroupLeaveFail"));
		}
	}
}
