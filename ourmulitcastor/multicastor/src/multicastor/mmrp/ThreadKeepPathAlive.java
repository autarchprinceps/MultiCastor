package multicastor.mmrp;

import java.io.IOException;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

/**
 * This class maintains the MMRP path connection.
 */
public class ThreadKeepPathAlive implements Runnable {
	PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {

		@Override
		public void nextPacket(final PcapPacket packet, final String arg1) {

			// check for stream address
			final byte[] source = new byte[6];
			final byte[] stream = new byte[6];

			for (int i = 0; i < 6; i++) {
				source[i] = packet.getByte(6 + i);
				stream[i] = packet.getByte(19 + i);
			}

			if (PcapHandler.compareMACs(stream, streamMACAddress)) {
				int cast = packet.getByte(25) & 0xff;
				cast = cast / 36;

				try {
					// if message is a leaveAll event
					if (packet.getByte(17) == (byte) 0x20) {
						PacketHandler.sendPacket(deviceMACAddress, MMRPPacket
								.getJoinEmpty(deviceMACAddress,
										streamMACAddress));
					} else {
						switch (cast) {
						// joinEmpty
						case 3:
							PacketHandler.sendPacket(deviceMACAddress,
									MMRPPacket.getJoinIn(deviceMACAddress,
											streamMACAddress));
							break;
						// empty
						case 4:
							PacketHandler.sendPacket(deviceMACAddress,
									MMRPPacket.getJoinIn(deviceMACAddress,
											streamMACAddress));
							break;
						// leave
						case 5:
							PacketHandler.sendPacket(deviceMACAddress,
									MMRPPacket.getJoinEmpty(deviceMACAddress,
											streamMACAddress));
							break;
						}
					}

				} catch (final IOException e) {
					e.printStackTrace();
				}

			}
		}

	};
	private final byte[] deviceMACAddress;
	private Pcap pcap = null;

	private final byte[] streamMACAddress;

	public ThreadKeepPathAlive(final byte[] deviceMACAddress,
			final byte[] streamMACAddress) throws IOException {
		this.deviceMACAddress = deviceMACAddress;
		this.streamMACAddress = streamMACAddress;
		pcap = PcapHandler.getPcapInstance(deviceMACAddress);
		// set the filter
		final PcapBpfProgram programm = new PcapBpfProgram();
		pcap.compile(programm, "ether proto 0x88f6", 0, 0xFFFFFF00);
		pcap.setFilter(programm);
	}

	@Override
	public void run() {
		waitForEmpty();
	}

	public void waitForEmpty() {
		while (!Thread.interrupted()) {
			pcap.loop(1, pcapPacketHandler, "");
		}
	}

}
