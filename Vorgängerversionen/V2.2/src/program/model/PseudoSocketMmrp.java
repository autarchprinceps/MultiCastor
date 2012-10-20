package program.model;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class PseudoSocketMmrp implements Runnable {

	public PseudoSocketMmrp(byte[] streamMac, byte[] hostMac, Pcap pcap,
			Logger log) {
		this.streamMac = streamMac;
		this.hostMac = hostMac;
		this.pcap = pcap;

		this.log = log;
	}

	private byte[] streamMac = null;
	private byte[] hostMac = null;
	private Pcap pcap = null;

	private Logger log = null;

	private ByteBuffer bb = null;

	private byte[] packet = null;

	private Thread t = null;

	private boolean ether = false;

	private boolean prune = false;
	private int errcount = 0;

	private PacketAnalyzerMMRP paMmrp = null;

	public void stop() {
		t.interrupt();
	}

	public void joinGroup() {
		packet = new byte[32];

		// Setting destination to mmrp address
		packet[0] = (byte) 0x01;
		packet[1] = (byte) 0x80;
		packet[2] = (byte) 0xC2;
		packet[3] = (byte) 0x00;
		packet[4] = (byte) 0x00;
		packet[5] = (byte) 0x20;

		// Setting source to hostMac
		for (int i = 0; i < 6; i++) {
			packet[i + 6] = hostMac[i];
		}

		// Set protocol to MMRP
		packet[12] = (byte) 0x88;
		packet[13] = (byte) 0xf6;

		// Set protocol version to 1
		packet[14] = (byte) 0x01;

		// Set AttributeType to MAC
		packet[15] = (byte) 0x02;

		// Set AttributeLength to 6 (length of a MAC address)
		packet[16] = (byte) 0x06;

		// Set leaveAll to false
		packet[17] = (byte) 0x00;

		// Set number of events to one
		packet[18] = (byte) 0x01;

		// Set firstValue to streamMac
		for (int i = 0; i < 6; i++) {
			packet[i + 19] = streamMac[i];
		}

		// Set event to JoinMt
		packet[25] = (byte) (36 * 3);

		// Set endmark
		for (int i = 0; i < 6; i++) {
			packet[26 + i] = (byte) 0x00;
		}

		// Send JoinMt packet
		bb = ByteBuffer.wrap(packet);
		if (pcap.sendPacket(bb) != Pcap.OK) {
			System.err.println(pcap.getErr());
		}

		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		// Change event to JoinIn
		packet[25] = (byte) (36 * 1);

		while (!Thread.interrupted()) {
			bb = ByteBuffer.wrap(packet);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// exception tritt auf, muss aber nicht gesondert behandelt
				// werden
			}

			if (pcap.sendPacket(packet) != Pcap.OK) {
				if (pcap.getErr().equalsIgnoreCase("send: Network is down")
						&& errcount < 3) {
					log.log(Level.INFO,
							NetworkAdapter.byteArrayToMac(hostMac)
									+ " - Cannot send packet. Network interface is set down. Remaining tries: "
									+ (3 - errcount));
					errcount++;
				} else if (pcap.getErr().equalsIgnoreCase(
						"send: Network is down")
						&& errcount == 3) {
					log.log(Level.INFO,
							NetworkAdapter.byteArrayToMac(hostMac)
									+ " - Network interface is permanently down. Sending will be resumed when interface is up again.");
					errcount++;
					prune = true;
				} else if (!prune) {
					System.err.println(pcap.getErr());

				}
			} else {
				if (prune) {
					prune = false;
					log.log(Level.INFO,
							NetworkAdapter.byteArrayToMac(hostMac)
									+ " - Network interface is up again. Try to resume sending.");
				}
				errcount = 0;
			}

		}
	}

	public void send(byte[] packet) {
		bb = ByteBuffer.wrap(packet);

		if (pcap.sendPacket(packet) != Pcap.OK) {
			if (pcap.getErr().equalsIgnoreCase("send: Network is down")
					&& errcount < 3) {
				log.log(Level.INFO,
						NetworkAdapter.byteArrayToMac(hostMac)
								+ " - Cannot send packet. Network interface is set down. Remaining tries: "
								+ (3 - errcount));
				errcount++;
			} else if (pcap.getErr().equalsIgnoreCase("send: Network is down")
					&& errcount == 3) {
				log.log(Level.INFO,
						NetworkAdapter.byteArrayToMac(hostMac)
								+ " - Network interface is permanently down. Sending will be resumed when interface is up again.");
				errcount++;
				prune = true;
			} else if (!prune) {
				System.err.println(pcap.getErr() + "send");
			}
		} else {
			if (prune) {
				prune = false;
				log.log(Level.INFO,
						NetworkAdapter.byteArrayToMac(hostMac)
								+ " - Network interface is up again. Try to resume sending.");
			}
			errcount = 0;
		}
	}

	public void receive(PacketAnalyzerMMRP packetAnalyzer) {
		paMmrp = packetAnalyzer;

		PcapPacketHandler<String> packetHandler = new PcapPacketHandler<String>() {

			@Override
			public void nextPacket(PcapPacket packet, String arg1) {

				// check for stream address
				boolean streamcheck = true;
				for (int i = 0; i < 6; i++) {
					if (packet.getByte(i) != streamMac[i]) {
						streamcheck = false;
					}
				}

				if (streamcheck) {
					// check for ethertype = 0x1337
					ether = true;
					if (packet.getByte(12) != (byte) 0x13
							|| packet.getByte(13) != (byte) 0x37) {
						ether = false;
					}

					if (ether) {
						paMmrp.analyzePacket(packet.getByteArray(0,
								packet.size()));
					}
				}

			}
		};

		if (pcap == null)
			System.out.println("DEBUG: pcap == null"); //$NON-NLS-1$
		pcap.loop(1, packetHandler, ""); //$NON-NLS-1$
	}

}
