package program.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;

import program.controller.Messages;
import program.data.MulticastData;
import program.interfaces.MulticastSenderInterface;
import program.interfaces.MulticastThreadSuper;
import program.model.MulticastSender.sendingMethod;

public class MulticastSenderMMRP extends MulticastThreadSuper implements
		MulticastSenderInterface {
	private PacketBuilderMMRP myPacketBuilder;

	/**
	 * Variablen f�r die verschiedenen Sendemethoden
	 */
	private int packetRateDes;

	private sendingMethod usedMethod = sendingMethod.PEAK;
	// Variablen f�r die Verbindung
	private PseudoSocketMmrp mcSocket;
	private byte[] sourceMac;

	// Variablen f�r das Senden
	private boolean isSending = false;
	private long pausePeriodMs;
	private int pausePeriodNs;
	private int totalPacketCount = 0;

	// Variablen f�r Logging
	private Logger messages;
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss"); //$NON-NLS-1$
	int resetablePcktCnt = 0, cumulatedResetablePcktCnt = 0;

	public MulticastSenderMMRP(MulticastData mcBean, Logger _logger) {
		super(mcBean);

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
			_logger.log(
					Level.WARNING,
					Messages.getString("MultiCastSenderMMRP.1"));
		} else {
			// Den hostName setzen
			try {
				mcData.setHostID(InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				proclaim(1,
						"Unable to get Host-Name. Error is: " + e.getMessage()); //$NON-NLS-1$
			}

			// �brige Variablen initialisieren
			messages = _logger;
			myPacketBuilder = new PacketBuilderMMRP(mcBean);
			packetRateDes = mcData.getPacketRateDesired();

			sourceMac = mcData.getSourceMac();

			// einen MultiCastSocket initiieren
			mcSocket = new PseudoSocketMmrp(mcData.getGroupMac(), sourceMac,
					mcData.getPcapp(), messages);

			// L�nge der Pause zwischen den Multicasts in Millisekunden und
			// Nanusekunden setzen
			pausePeriodMs = (int) (1000.0 / mcData.getPacketRateDesired()); // Pause
																			// in
																			// Milisekunden
			pausePeriodNs = (int) (((1000.0 / mcData.getPacketRateDesired()) - pausePeriodMs) * 1000000.0); // "Rest"-Pausenzeit(alles
																											// kleiner
																											// als
																											// 1ms)
																											// in
																											// ns

			mcSocket.joinGroup();

		}

	}

	@Override
	public void run() {
		// Paketz�hler auf 0 setzen
		totalPacketCount = 0;
		resetablePcktCnt = 0;
		cumulatedResetablePcktCnt = 0;

		// Der Multicastgruppe beitreten
		mcSocket.joinGroup();
		proclaim(
				2,
				"Joined Multicast-group " + NetworkAdapter.byteArrayToMac(mcData.getGroupMac()) + " as sender using method " + usedMethod + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		myPacketBuilder.setReset(true);
		mcSocket.send(myPacketBuilder.getPacket());
		myPacketBuilder.setReset(false);

		// So lange senden, bis setActive(false) aufgerufen wird
		// Variable 'usedMethod' bestimmt auf welche Art und Weise.
		// Wurde bei setActive keine Methode angegeben, wird standartm��ig
		// die
		// PEAK-Methode verwendet.
		switch (usedMethod) {
		case NO_SLEEP: // ohne sleep, unter Volllast
			while (isSending) {
				// try {
				mcSocket.send(myPacketBuilder.getPacket());
				if (totalPacketCount < 65535)
					totalPacketCount++;
				else
					totalPacketCount = 0;
				resetablePcktCnt++;
			}
			break;
		case PEAK: // Misst wie lange er sendt um die Paketrate zu erhalten
					// und sleept den Rest der Sekunde
			long endTime = 0,
			timeLeft = 0;
			while (isSending) {
				// Sleep wenn noch etwas von der letzten Sekunde �brig
				timeLeft = endTime - System.nanoTime();
				if (timeLeft > 0)
					try {
						Thread.sleep(timeLeft / 1000000,
								(int) (timeLeft % 1000000));
					} catch (InterruptedException e) {
						proclaim(1,
								"Sleep after sending with method PEAK failed: " //$NON-NLS-1$
										+ e.getMessage());
					}
				endTime = System.nanoTime() + 1000000000; // Plus 1s (in ns)
				do {
					mcSocket.send(myPacketBuilder.getPacket());
					if (totalPacketCount < 65535)
						totalPacketCount++;
					else
						totalPacketCount = 0;
					resetablePcktCnt++;

				} while (((totalPacketCount % packetRateDes) != 0) && isSending);
			}
			break;
		case SLEEP_MULTIPLE: // mit Thread.sleep zwischen jedem Senden
		default:
			while (isSending) {
				try {
					mcSocket.send(myPacketBuilder.getPacket());
					if (totalPacketCount < 65535)
						totalPacketCount++;
					else
						totalPacketCount = 0;
					resetablePcktCnt++;
					Thread.sleep(pausePeriodMs, pausePeriodNs);
				} catch (InterruptedException e2) {
					proclaim(1,
							"While sending: (sleep fails): " + e2.getMessage()); //$NON-NLS-1$
				}
			}
		}
		proclaim(2, totalPacketCount + " packets send in total"); //$NON-NLS-1$
		// Counter reseten
		totalPacketCount = 0;
		resetablePcktCnt = 0;
		cumulatedResetablePcktCnt = 0;
		update();
		updateMin();
		setStillRunning(false);
	}

	/**
	 * Wird der Methode true �bergeben, meldet diese sich bei der
	 * Multicastgruppe an und startet zu senden. Wird der Methode false
	 * �bergeben, stoppt sie das senden der Multicasts und veranlasst damit auch
	 * das Abmelden von der Multicastgruppe.
	 * 
	 * @param active
	 *            boolean
	 */
	@Override
	public void setActive(boolean active) {
		if (active) {
			// Setzen der ThreadID, da diese evtl.
			// im Controller noch einmal ge�ndert wird
			myPacketBuilder.alterThreadID(mcData.getThreadID());

			isSending = true;
			mcData.setActive(true);
			setStillRunning(true);
			proclaim(2, "MultiCast-Sender activated"); //$NON-NLS-1$
		} else {
			isSending = false;
			mcData.setActive(false);
			proclaim(2, "MultiCast-Sender deactivated"); //$NON-NLS-1$
		}
	}

	/**
	 * Siehe public void setActive(boolean active). Es l�sst sich zus�tzlich die
	 * Art und Weise angeben, mit der gesendet wird (diese M�glichkeit wird in
	 * der aktuellen Version nur auf Modulebene unterst�tzt und zielt auf
	 * sp�tere Versionen des ByteTools und Testzwecke ab)
	 * 
	 * @param active
	 *            true=senden, false=nicht senden
	 * @param _method
	 *            Art und Weise des Sendens
	 */
	// @Override
	public void setActive(boolean active, sendingMethod _method) {
		usedMethod = _method;
		setActive(active);
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
	 * Methode �ber die die Kommunikation zum MultiCastController realisiert
	 * wird.
	 * 
	 * @param level
	 *            unterscheidet zwischen Fehlern und Status-Meldungen
	 * @param mssg
	 *            Die zu sendende Nachricht (String)
	 */
	private void proclaim(int level, String mssg) {
		Level l;
		mssg = mcData.identify() + ": " + mssg; //$NON-NLS-1$
		switch (level) {
		case 1:
			l = Level.WARNING;
			break;
		case 2:
			l = Level.INFO;
			break;
		default:
			l = Level.SEVERE;
		}
		messages.log(l, mssg);
	}

	public void kill() {
		mcSocket.stop();

		mcSocket = null;
	}

}
