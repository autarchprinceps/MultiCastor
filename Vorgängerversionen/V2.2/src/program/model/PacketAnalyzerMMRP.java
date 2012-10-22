package program.model;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import program.data.MulticastData;
import program.data.MulticastData.Source;
import program.data.MulticastData.senderState;

public class PacketAnalyzerMMRP {
	/** Objekt in dem die ermittelten Werte gespeichert werden. */
	private MulticastData mcData;
	/** Dient der Ermittlung der Paketrate. */
	private int packetrate_counter = 0;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	/**
	 * Wird zur Berechnung des durschnittlichen Jitterwertes pro Sekunde
	 * verwendet.
	 */
	private int jitterHelper = 0;
	/** Wird fuer die Jitterberechnung verwendet. */
	private long timeStampSe = 0;
	/** Wird fuer die Jitterberechnung verwendet. */
	private long timeStampRe = 0;
	/** Wird fuer die Jitterberechnung verwendet. */
	private long dTimeStamp2 = 0;
	/** Wird fuer die Jitterberechnung verwendet. */
	private long dTimeStamp1 = 0;
	/**
	 * Wird verwendet, um zu verhindern, dass gerade gestartete
	 * MulticastReceiver in den Status RecentlyChanged gehen.
	 */
	private int lastActivated = 0;
	// Avg values
	/** Wird zur Berechnung der minuetlichen Durschnittswerte verwendet. */
	private int updateMinHelper = 0;
	/**
	 * Wird zur Berechnung des durschnittlichen Jitterwertes pro Minute
	 * verwendet.
	 */
	private int jitterAvgHelper = 0;
	/** Wird zur Berechnung der durschnittlichen Paketrate verwendet. */
	private int packetrateAvgHelper = 0;
	/**
	 * Wird zur Berechnung des durschnittlichen PacketsLostPerSecond-Wertes
	 * verwendet.
	 */
	private int plpsAvgHelper = 0;
	/** Wird zur Berechnung des durschnittlichen Traffics verwendet. */
	private int trafficAvgHelper = 0;
	// data is written only once each second to mcData
	/** Zwischenspeicher fuer die Paketlaenge. */
	private int packetLength = 0;
	/** Zwischenspeicher fuer die hostID. */
	private String hostID = ""; //$NON-NLS-1$
	/** Zwischenspeicher fuer die threadID. */
	private int threadID = 0;
	/** Zwischenspeicher fuer die desiredPacketRate. */
	private int desiredPacketRate = 0;
	/** Zwischenspeicher fuer den packetCount. */
	private long packetCount = 0;
	/** Speichert die vermutete Quelle des Pakets. */
	private MulticastData.Source source = Source.MULTICASTOR;
	/** Zwischenspeicher fuer die senderID. */
	private String senderID = ""; //$NON-NLS-1$
	/**
	 * If set to true a analyzePacketOnce() is executed during the next
	 * analyzePacket(). This looks for additional values.
	 */
	private Boolean complete = true;

	/** Used to track how much the SenderID changes. */
	private int senderChanges = 0;
	/** Speichert wann die letzte �nderung war. */
	private int recently_changed = 0;

	/**
	 * Wird genutzt, wenn fuer eine gewisse Zeit keine Pakete mehr empfangen
	 * wurden.
	 */
	private boolean timeout = false;
	/** Speichert fuer wie lange keine Pakete mehr empfangen wurden. */
	private int interruptiontime = 0;
	/**
	 * Speichert die Summe der Interruptiontimes um daraus einen Durschnittswert
	 * berechnen zu koennen.
	 */
	private int interruptiontimeAvgHelper = 0;
	/** Speichert die Anzahl der Interruptions */
	private int interruptionCounter = 0;

	/**
	 * Wird fuer PacketLostPerSecond genutzt. Zaehlt intern die Paketnummer mit.
	 * So kann die naechste erwartete Paketnummer berechnet werden.
	 */
	int internerPacketCount = 0;
	/**
	 * Wird fuer PacketLostPerSecond genutzt. Hier werden fehlende Paketnummern
	 * eingetragen, auf die anschlie�end geprueft wird.
	 */
	Vector<Integer> missingPackets;

	/**
	 * Erzeugt einen PacketAnalyzer.
	 * 
	 * @param multicastData
	 *            Aus diesem Datenobjekt wird die Multicastgruppe und der Port
	 *            zur Initialisierung genommen. Ermittelte Werte werden in
	 *            dieses Objekt geschrieben.
	 * @param logger
	 *            Der Logger wird genutzt um Informationsmeldungen oder
	 *            Fehlermeldungen auszugeben.
	 */
	public PacketAnalyzerMMRP(MulticastData multicastData, Logger logger) {
		if (multicastData == null) {
			System.out
					.println("B�ser Fehler!!! multicastData ist null im PacketAnalyzer."); //$NON-NLS-1$
		}
		mcData = multicastData;
		if (logger == null) {
			System.out
					.println("B�ser Fehler!!! Message Queue ist null im PacketAnalyzer."); //$NON-NLS-1$
		}
		this.logger = logger;
		missingPackets = new Vector<Integer>();
	}

	/**
	 * Analysiert das uebergebene Paket auf, oder speichert zumindest notwendige
	 * Informationen fuer:
	 * <ul>
	 * <li>Resetvalue
	 * <li>SenderID
	 * <ul>
	 * <li>HostID
	 * <li>ThreadID
	 * </ul>
	 * <li>PacketLoss
	 * <li>MeasuredPacketRate
	 * <li>Jitter
	 * <li>PacketCount
	 * <li>Senders
	 * </ul>
	 * Hierbei werden in der Regel nur Hilfswerte ermittelt und noch nichts in
	 * das MulticastData-Objekt geschrieben.
	 * 
	 * @param mcPacket
	 *            Zu anlysierendes Paket.
	 */
	public void analyzePacket(byte[] mcPacket) {
		if (getComplete()) {
			analyzePacketOnce(mcPacket);
			setComplete(false);
		}

		// ********************************************
		// Paketanalyse - geklaut von Janniks TestCase
		// ********************************************

		byte[] snippet = new byte[29];
		byte[] intSnippet = new byte[4];
		byte[] shortSnippet = new byte[2];
		byte[] longSnippet = new byte[8];

		// Perform value-reset
		System.arraycopy(mcPacket, 49, intSnippet, 0, 4);
		boolean reset = ByteTools.byteToBoolean(intSnippet);
		if (reset == true) {
			mcData.resetValues();
			this.resetValues();
		}

		// HostID
		System.arraycopy(mcPacket, 14, snippet, 0, 29);
		// Ende des Strings suchen
		int eof = 0;
		for (; eof < 29 && snippet[eof] != 0; eof++)
			;
		hostID = new String(snippet).substring(0, eof);

		// Thread-ID
		System.arraycopy(mcPacket, 43, shortSnippet, 0, 2);
		threadID = ByteTools.shortByteToInt(shortSnippet);

		// tracks how much the senderID changes
		// senderID = hostID + threadID;
		if (!senderID.equals(hostID + threadID)) {
			senderID = hostID + threadID;
			senderChanges++;
		}

		// PacketLoss
		shortSnippet[0] = mcPacket[45];
		shortSnippet[1] = mcPacket[46];
		int packetcount = ByteTools.shortByteToInt(shortSnippet);

		if (packetcount != internerPacketCount) {
			if (missingPackets.contains(packetcount)) { // werden hier die
														// Pointer oder die
														// Werte verglichen???
				missingPackets.remove((Object) packetcount);
			} else {
				if ((packetcount - internerPacketCount < 1000)
						&& (internerPacketCount > 0)) {
					for (int n = internerPacketCount; n < packetcount; n++) {
						missingPackets.add(internerPacketCount);
					}
				} else {
				}
				internerPacketCount = packetcount + 1;
			}
		} else {
			internerPacketCount++;
		}

		// MeasuredPacketRate
		packetrate_counter++;

		// time stamp
		System.arraycopy(mcPacket, 53, longSnippet, 0, 8);
		timeStampSe = ByteTools.byteToLong(longSnippet);
		timeStampRe = System.nanoTime();
		// jitterMessung(timeStamp);
		if (dTimeStamp1 != 0) {
			dTimeStamp2 = dTimeStamp1;
			dTimeStamp1 = timeStampRe - timeStampSe;
			jitterHelper += Math.abs(dTimeStamp2 - dTimeStamp1);
		} else {
			dTimeStamp1 = timeStampRe - timeStampSe;
		}

		// total packets received
		packetCount++;
	}

	/**
	 * Analysiert das Paket auf Werte, die sich nicht so oft aendern.
	 * <ul>
	 * <li>PacketLength
	 * <li>DesiredPacketRate
	 * <li>TTL
	 * <li>Source
	 * </ul>
	 * 
	 * @param mcPacket
	 *            Zu untersuchendes Datenpaket.
	 */
	public void analyzePacketOnce(byte[] mcPacket) {
		// Die Bufferlaenge fuer Pakete ist im MulticastReceiver
		// festgelegt und darf davon nicht abweichen
		if (mcPacket.length < 54) {
			logger.log(Level.SEVERE,
					"Invalid Packet Receivied in analysePacketOnce()"); //$NON-NLS-1$
			return;
		}

		byte[] shortSnippet = new byte[2];

		packetLength = mcPacket.length;

		// DesiredPacketRate
		shortSnippet[0] = mcPacket[47];
		shortSnippet[1] = mcPacket[48];
		desiredPacketRate = ByteTools.shortByteToInt(shortSnippet);

	}

	/**
	 * Setzt eine Variable, die jede Sekunde dekrementiert wird auf eine ganze
	 * Zahl. Die angegebene Anzahl an Sekunden wird senders nicht auf
	 * CHANGED_RECENTLY oder MULTIPLE gesetzt.
	 * 
	 * @param lastActivated
	 */
	public void setLastActivated(int lastActivated) {
		this.lastActivated = Math.abs(lastActivated);
	}

	/**
	 * Hier werden folgende ermittelten Werte im MulticastData-Objekt
	 * aktualisiert:
	 * <ul>
	 * <li>MaxInterruptionTime
	 * <li>PacketLength
	 * <li>SenderID
	 * <ul>
	 * <li>HostID
	 * <li>ThreadID
	 * </ul>
	 * <li>DesiredPacketRade
	 * <li>TTL
	 * <li>PacketCount
	 * <li>PacketSource
	 * <li>Senders
	 * <li>PacketRateMeasured
	 * <li>Traffic
	 * <li>PacketLossPerSecond
	 * </ul>
	 */
	public void update() {
		mcData.setNumberOfInterruptions(interruptionCounter);
		if (interruptionCounter != 0) {
			mcData.setAverageInterruptionTime(interruptiontimeAvgHelper
					/ interruptionCounter);
		}
		if (timeout) {
			interruptiontime++;
			if (mcData.getMaxInterruptionTime() < interruptiontime) {
				mcData.setMaxInterruptionTime(interruptiontime);
			}
		}
		lastActivated--;
		// ********************************************
		// Writes all the data, which do not need any calculation
		// ********************************************
		mcData.setPacketLength(packetLength);
		mcData.setHostID(hostID);
		mcData.setThreadID(threadID);
		mcData.setPacketRateDesired(desiredPacketRate);
		mcData.setPacketCount(packetCount);
		mcData.setPacketSource(source);

		// uses analyzePacketOnce on next received packet, too
		// sorgt dafür, dass jegliche Änderungen erst mit einer Sekunde
		// Verzögerungen
		// in der GUI erscheinen -> updatet aber auch einige Werte deshalb nur
		// einmal pro Sekunde
		setComplete(true);

		// ********************************************
		// Writes calculated data
		// ********************************************
		if ((recently_changed >= 0) && (senderChanges < 2)) {
			recently_changed--;
			mcData.setSenders(senderState.RECENTLY_CHANGED);
		} else {
			if (senderChanges > 1) {
				mcData.setSenders(senderState.MULTIPLE);
			} else if ((senderChanges == 1) && (lastActivated <= 0)) {
				recently_changed = 4;
				mcData.setSenders(senderState.RECENTLY_CHANGED);
			} else {
				mcData.setSenders(senderState.SINGLE);
			}
		}
		senderChanges = 0;

		// Jitter
		if (packetrate_counter > 1) {
			int j = (jitterHelper / (packetrate_counter - 1));
			jitterAvgHelper += j;
			mcData.setJitter(j);
			jitterHelper = 0;
		}

		// MeasuredPacketRate
		mcData.setPacketRateMeasured(packetrate_counter);
		packetrateAvgHelper += packetrate_counter;
		packetrate_counter = 0;

		// Traffic
		int traffic = mcData.getPacketRateMeasured() * mcData.getPacketLength();
		mcData.setTraffic(traffic);
		trafficAvgHelper += traffic;

		// PacketsLostPerSecond
		mcData.setPacketLossPerSecond(missingPackets.size());
		plpsAvgHelper += missingPackets.size();
		missingPackets.removeAllElements();

		updateMinHelper++;
	}

	/**
	 * Aktualisiert folgende ermittelte Durchschnittswerte im
	 * MulticastData-Objekt:
	 * <ul>
	 * <li>JitterAvg
	 * <li>PacketLossPerSecondAvg
	 * <li>PacketRateAvg
	 * <li>TrafficAvg
	 * </ul>
	 */
	public void updateMin() {
		if (updateMinHelper != 0) {
			mcData.setJitterAvg(jitterAvgHelper / updateMinHelper);
			jitterAvgHelper = 0;

			// umdefiniert - siehe interruptiontimeAvgHelper
			// mcData.setNumberOfInterruptionsAvg(noiAvgHelper/updateMinHelper);
			// noiAvgHelper = 0;
			mcData.setPacketLossPerSecondAvg(plpsAvgHelper / updateMinHelper);
			plpsAvgHelper = 0;
			mcData.setPacketRateAvg(packetrateAvgHelper / updateMinHelper);
			packetrateAvgHelper = 0;
			mcData.setTrafficAvg(trafficAvgHelper / updateMinHelper);
			trafficAvgHelper = 0;

			updateMinHelper = 0;
		} else {
			// hier wird einfach nichts getan, das ist absicht.
		}
	}

	/**
	 * Setzt den <code>complete</code> Wert.
	 * 
	 * @param b
	 *            Wird diese Funktion mit <code>TRUE</code> aufgerufen,
	 *            analisiert der PacketAnalyzer das naechste Paket auf alle
	 *            Werte.
	 */
	public void setComplete(boolean b) {
		synchronized (complete) {
			complete = b;
		}
	}

	private Boolean getComplete() {
		synchronized (complete) {
			return complete;
		}
	}

	/**
	 * Setzt den internen PacketCount des Packetanalyzers.
	 * 
	 * @param packetCount
	 *            Neuer PacketCount
	 */
	public void setInternerPacketCount(int packetCount) {
		internerPacketCount = packetCount;
	}

	/**
	 * Mit dieser Methode kann der PacketAnalyzer ueber einen Timeout vom
	 * Receiver informiert werden.
	 * 
	 * @param timeout
	 *            Wenn ein Timeout vor liegt, diese Methode mit
	 *            <code>TRUE</code> aufrufen.
	 */
	public void setTimeout(boolean timeout) {
		if ((!timeout) && (this.timeout != timeout)) {
			interruptiontimeAvgHelper += interruptiontime;
			interruptiontime = 0;
		} else if ((timeout) && (this.timeout != timeout)) {
			interruptionCounter++;
		}
		this.timeout = timeout;
	}

	/**
	 * Setzt alle internen Werte zurueck. Diese werden dann auch ins
	 * MulticastData-Objekt geschrieben.
	 */
	public void resetValues() {
		final int d = 0;
		desiredPacketRate = d;
		dTimeStamp1 = d;
		dTimeStamp2 = d;
		// hostID = "";
		internerPacketCount = d;
		interruptionCounter = d;
		interruptiontime = d;
		interruptiontimeAvgHelper = d;
		interruptionCounter = d;
		jitterAvgHelper = d;
		jitterHelper = d;
		missingPackets.removeAllElements();
		packetCount = d;
		packetLength = d;
		packetrate_counter = d;
		packetrateAvgHelper = d;
		plpsAvgHelper = d;
		timeStampRe = d;
		timeStampSe = d;
		trafficAvgHelper = d;
		updateMinHelper = d;
		// Performs a complete packetanalyzis with next packet.
		this.setComplete(true);
		mcData.resetValues();
	}
}
