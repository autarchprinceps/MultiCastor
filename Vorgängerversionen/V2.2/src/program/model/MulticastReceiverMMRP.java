package program.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;

import program.data.MulticastData;
import program.interfaces.MulticastThreadSuper;

public class MulticastReceiverMMRP extends MulticastThreadSuper {
	/** Pseudosocket fuer MMRP. */
	private PseudoSocketMmrp multicastSocket;
	/** Wenn auf wahr, lauscht dieser Receiver auf ankommende Pakete. */
	private boolean active = false;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	/** Maximale Paketlaenge */
	private final int length = 65575;
	/** Byte Array in dem das Paket gespeichert wird. */
	private byte[] buf = new byte[length];
	/** Analysiert ankommende Pakete */
	PacketAnalyzerMMRP packetAnalyzer;

	private boolean waitForDeactivate = false;

	/**
	 * Ein MulticastReceiver entspricht genau einem MulticastData-Objekt vom
	 * Receiver Typ. Der MulticastReceiver ist fuer das Beitreten und Verlassen
	 * der MulticastGruppen zustaending. Er kann gestartet und gestoppt werden.
	 * 
	 * @param m
	 *            MulticastData-Objekt mit Informationen zum Stream. Alle
	 *            ermittelten Informationen werden hier rein geschrieben.
	 * @param logger
	 *            Wird genutzt um Status- und Fehlermeldungen auszugeben. Diese
	 *            werden in die MessageQueue geschrieben.
	 */
	public MulticastReceiverMMRP(MulticastData m, Logger logger) {
		super(m);

		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis

		try {
			Pcap pcap = Pcap.openLive(mcData.getDevice().getName(), snaplen,
					flags, timeout, new StringBuilder());
			mcData.setPcapp(pcap);
		} catch (Exception e) {
			// exception wird verworfen, aufruf wird im Anschluss geprueft
		}

		if (mcData.getPcapp() == null) {
			logger.log(
					Level.WARNING,
					"Gewähltes Interface ist von jnetpcap nicht unterstützt. Bitte anderes Interface wählen.");
		}

		if (logger == null) {
			System.out
					.println("FATAL ERROR - cannot create MulticastReceiver without Logger"); //$NON-NLS-1$
			return;
		}
		if (m == null) {
			logger.log(Level.WARNING,
					"Error while creating MulticastReceiver. MulticastData cannot be empty."); //$NON-NLS-1$
			return;
		}

		this.logger = logger;

		// Erzeuge Socket mit Port; nutze Standardport falls eingegebener Port
		// nicht funktioniert.
		multicastSocket = new PseudoSocketMmrp(mcData.getGroupMac(),
				mcData.getSourceMac(), mcData.getPcapp(), logger);

		packetAnalyzer = new PacketAnalyzerMMRP(mcData, logger);

		// resets MulticastData Object to avoid default value -1
		mcData.resetValues();
	}

	/**
	 * Setzt alle Vorraussetzungen damit der MulticastReceiver mit einem Thread
	 * gestartet werden kann. Hierbei muss der MulticastController
	 * sicherstellen, dass kein weiterer Thread mit diesem Objekt laeuft.
	 */
	@Override
	public void setActive(boolean b) {
		active = b;
		if (b) {
			setStillRunning(true);
			// Verhindert eine "recentlyChanged"-Markierung direkt nach dem
			// Starten
			packetAnalyzer.setLastActivated(6);
		} else {
			waitForDeactivate = true;
			mcData.getPcapp().breakloop();
		}
		mcData.setActive(b);
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
		multicastSocket.joinGroup();
		logger.log(Level.INFO, mcData.identify()
				+ ": Started Receiver with MC Object."); //$NON-NLS-1$
		return false;
	}

	/**
	 * Veranlasst den MulticastReceiver die Multicastgruppe zu verlassen.
	 */
	private void leaveGroup() {
		// ********************************************
		// Leave MultiCast-Group
		// ********************************************
		multicastSocket.stop();
		logger.log(Level.INFO, mcData.identify()
				+ ": Left Multicastgroup of MC Object."); //$NON-NLS-1$
		logger.log(
				Level.INFO,
				mcData.identify()
						+ ": " + mcData.getPacketCount() + " packets received in total."); //$NON-NLS-1$
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

		while (active) {
			multicastSocket.receive(packetAnalyzer);
		}

		if (!active && waitForDeactivate) {
			waitForDeactivate = false;
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
	 * Schreibt lauter Einsen in den Buffer.
	 */
	private void initializeBuf() {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = 1;
		}
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
		// damit die Paketlaenge beim naechsten Paket erneut bestimmt werden
		// kann
		// groessere Pakete fallen immer auf, jedoch sind kleinere Pakete nur so
		// erkennbar
		initializeBuf();
	}
}
