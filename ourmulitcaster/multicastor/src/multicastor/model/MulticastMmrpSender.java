package multicastor.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import multicastor.controller.MulticastController;
import multicastor.data.MulticastData;
import multicastor.interfaces.MulticastSenderInterface;
import multicastor.interfaces.MulticastThreadSuper;
import multicastor.lang.LanguageManager;
import multicastor.mmrp.MMRPSender;


/**
 * Die MultiCastMmrpSender-Klasse kuemmert sich um das tatsaechliche Senden der
 * Multicast-Objekte ueber das Netzwerk per MMRP Protokoll. Sie extended
 * {@link MulticastThreadSuper}, ist also ein Runnable. Ein MultiCastMmrpSender
 * hat eine Grundkonfiguration, die nicht mehr abgeaendert werden kann, wie zum
 * Beispiel die gesetzten MACs. Soll diese Grundkonfiguration geaendert werden,
 * muss eine neue Instanz de Klasse gebildet werden. Das Erleichtert die
 * nachtraegliche Analyse, Da das Objekt eindeutig einem "Test" zuordnungsbar
 * ist.
 */
public class MulticastMmrpSender extends MulticastThreadSuper implements
		MulticastSenderInterface {

	private int cumulatedResetablePcktCnt = 0;

	/** Wenn auf wahr, sendet dieser Sender. */
	private boolean isSending = false;
	/**
	 * Language Manager ist wichtig fuer die multi Language Unterstuetzung
	 */
	private final LanguageManager lang = LanguageManager.getInstance();

	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;

	private MulticastController mCtrl;
	/** baut das ByteArray fuer die Pakete */
	private PacketBuilder myPacketBuilder;
	private int packetRateDes;
	private int resetablePcktCnt = 0;

	private MMRPSender sender;
	/** Anzahl aller Pakete */
	private int totalPacketCount = 0;

	/**
	 * Einziger Konstruktor der Klasse (Sieht man vom Konstruktor der
	 * Superklasse ab). Im Konstruktor wird die hostID gesetzt (entspricht dem
	 * hostnamen des Geraets), der {@link MMRPSender} initialisiert und das
	 * Datenpaket mit dem {@link PacketBuilder} erstellt.
	 * 
	 * @param multicastData
	 *            Das {@link MulticastData}-Object, dass alle fuer den Betrieb
	 *            nuetigen Daten enthuelt.
	 * @param logger
	 *            Eine {@link Queue}, ueber den der Sender seine Ausgaben an den
	 *            Controller weitergibt.
	 * @param multiCtrl
	 *            Eine Referenz auf den entsprechenden
	 *            {@link MulticastController} damit MulticastStroeme ggf.
	 *            richtig gestoppt werden kann
	 */
	public MulticastMmrpSender(final MulticastData multicastData,
			final Logger logger, final MulticastController multiCtrl)
			throws IOException {
		super(multicastData);

		if(logger == null) {
			System.out.println(lang.getProperty("error.mr.logger"));
			return;
		}
		if(multicastData == null) {
			logger.log(Level.WARNING, lang.getProperty("error.mr.mcdata"));
			return;
		}

		try {
			multicastData.setHostID(InetAddress.getLocalHost().getHostName());
		} catch(final UnknownHostException e) {
			proclaim(
					1,
					lang.getProperty("message.getHostname") + " "
							+ e.getMessage());
		}

		this.logger = logger;
		mCtrl = multiCtrl;
		myPacketBuilder = new PacketBuilder(mcData);
		packetRateDes = mcData.getPacketRateDesired();
		try {
			sender = new MMRPSender(mcData.getMmrpSourceMac(),
					mcData.getMmrpGroupMac());
		} catch(final IOException e) {
			proclaim(3, lang.getProperty("message.mmrpSender"));
			throw new IOException();
		}
	}

	/**
	 * hier geschieht das eigentliche Senden. Beim Starten des Threads wird
	 * probiert, denn Mmrp Pfad zu registrieren. Gelingt dies nicht, wird ein
	 * Fehler ausgegeben und das Senden wird garnicht erst gestartet. Gelingt
	 * das registrieren, wird so lange gesendet, bis setActive(false) aufgerufen
	 * wird.
	 */
	@Override
	public void run() {

		try {
			sender.registerPath();
			// Paketzaehler auf 0 setzen
			totalPacketCount = 0;
			resetablePcktCnt = 0;
			cumulatedResetablePcktCnt = 0;

			// Misst wie lange er sendt um die Paketrate zu erhalten
			// und sleept den Rest der Sekunde
			long endTime = 0, timeLeft = 0;

			while(isSending) {
				// Sleep wenn noch etwas von der letzten Sekunde uebrig
				timeLeft = endTime - System.nanoTime();
				if(timeLeft > 0) {
					try {
						Thread.sleep(timeLeft / 1000000,
								(int)(timeLeft % 1000000));
					} catch(final InterruptedException e) {
						proclaim(1, lang.getProperty("message.sleapPeak") + " "
								+ e.getMessage());
					}
				}
				endTime = System.nanoTime() + 1000000000; // Plus 1s (in ns)
				do {
					try {
						sender.sendDataPacket(myPacketBuilder.getPacket());
						if(totalPacketCount < 65535) {
							totalPacketCount++;
						} else {
							totalPacketCount = 0;
						}
						resetablePcktCnt++;

					} catch(final Exception e1) {
						proclaim(2, lang.getProperty("message.problemSending"));
					}
				} while(((totalPacketCount % packetRateDes) != 0) && isSending);
			}
			try {
				sender.deregisterPath();
			} catch(final IOException e) {
				proclaim(3, "Could not deregister Path");
			}
			proclaim(
					2,
					totalPacketCount + " "
							+ lang.getProperty("message.packetsSendTotal"));

			// Counter reseten
			totalPacketCount = 0;
			resetablePcktCnt = 0;
			cumulatedResetablePcktCnt = 0;
			update();
			updateMin();
			setStillRunning(false);

		} catch(final IOException e2) {
			proclaim(3, lang.getProperty("message.registerMmrpSender"));
			isSending = false;
			setActive(false);
			mCtrl.stopMC(mcData);
		}

	}

	/**
	 * Wird der Methode true uebergeben, startet der Multicast zu senden. Wird
	 * der Methode false uebergeben, stoppt sie das senden der Multicasts.
	 * 
	 * @param active
	 *            boolean
	 */
	@Override
	public void setActive(final boolean active) {
		isSending = active;
		mcData.setActive(active);
		if(active) {
			// Setzen der ThreadID, da diese evtl.
			// im Controller noch einmal geuendert wird
			myPacketBuilder.alterThreadID(mcData.getThreadID());
			myPacketBuilder.alterRandomID(mcData.getRandomID());
			setStillRunning(true);
			proclaim(2, lang.getProperty("message.mcSenderActivated"));
		} else {
			proclaim(2, lang.getProperty("message.mcSenderDeactivated"));
		}
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt und resetet den internen
	 * Paket-Counter
	 */
	@Override
	public void update() {
		mcData.setPacketRateMeasured(resetablePcktCnt);
		mcData.setPacketCount(totalPacketCount);
		mcData.setTraffic(resetablePcktCnt * mcData.getPacketLength());
		cumulatedResetablePcktCnt = +resetablePcktCnt;
		resetablePcktCnt = 0;
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt. Dieser Counter summiert die
	 * gemessene Paketrate, die mit der update()-Funktion ins
	 * MultiCastData-Objekt geschrieben wird. Diese Summe wird bei jedem Aufruf
	 * resetet.
	 */
	@Override
	public void updateMin() {
		mcData.setPacketRateAvg(cumulatedResetablePcktCnt);
		cumulatedResetablePcktCnt = 0;
	}

	/**
	 * Methode ueber die die Kommunikation zum MultiCastController realisiert
	 * wird.
	 * 
	 * @param level
	 *            unterscheidet zwischen Fehlern und Status-Meldungen
	 * @param mssg
	 *            Die zu sendende Nachricht (String)
	 */
	private void proclaim(final int level, String mssg) {
		Level l;
		mssg = mcData.identify() + ": " + mssg;
		switch(level) {
			case 1:
				l = Level.WARNING;
				break;
			case 2:
				l = Level.INFO;
				break;
			default:
				l = Level.SEVERE;
		}
		logger.log(l, mssg);
	}

}
