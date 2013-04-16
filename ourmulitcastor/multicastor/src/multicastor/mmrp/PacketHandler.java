package multicastor.mmrp;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jnetpcap.Pcap;

/**
 * The PacketHandler sends packets through the network device
 */
public class PacketHandler {
	/**
	 * @param deviceMACAddress
	 *            is the MAC address of the network device
	 * @param packet
	 *            is the packet which should be send
	 * @throws IOException
	 *             if the hardware device was not found
	 */

	public static void sendPacket(final byte[] deviceMACAddress,
			final byte[] packet) throws IOException {
		final Pcap pcap = PcapHandler.getPcapInstance(deviceMACAddress);
		final ByteBuffer b = ByteBuffer.wrap(packet);
		if (pcap.sendPacket(b) != Pcap.OK) {
			System.err.println(pcap.getErr());
		}
		pcap.close();
	}
}
