package multicastor.mmrp;

import java.io.IOException;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

/**
 * Receives packets which are send from the stream.
 */
public class MMRPReceiver extends MMRPEntity {

	PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {
		@Override
		public void nextPacket(final PcapPacket packet, final String arg1) {
			final int length = (packet.getByte(13) & 0xFF)
					+ ((packet.getByte(12) & 0xFF) * 255);
			System.arraycopy(packet.getByteArray(14, length), 0, foundPacket,
					0, length);
			found = true;
		}
	};
	private boolean found;
	private byte[] foundPacket;
	private Pcap pcap = null;

	private boolean stop = false;

	/**
	 * @param deviceMACAddress
	 *            is a byte array which contains the MAC address of the network
	 *            device.
	 * @param streamMACAddress
	 *            is a byte array which contains the Address of the multicast
	 *            group
	 * @throws IOException
	 *             if the network device was not found
	 */
	public MMRPReceiver(final byte[] deviceMACAddress,
			final byte[] streamMACAddress) throws IOException {
		super(deviceMACAddress, streamMACAddress);
		pcap = PcapHandler.getPcapInstance(deviceMACAddress);
		final PcapBpfProgram programm = new PcapBpfProgram();
		pcap.compile(programm,
				"ether dst " + PcapHandler.byteMACToString(streamMACAddress),
				0, 0xFFFFFF00);
		pcap.setFilter(programm);
	}

	/**
	 * Stops the procedure which caputures the packets
	 */
	public void stopLoop() {
		stop = true;

		final byte[] data = { (byte) 0x00, (byte) 0x00 };
		try {
			PacketHandler.sendPacket(deviceMACAddress, DataPacket
					.getDataPacket(deviceMACAddress, streamMACAddress, data));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Capture all packets which has the stream MAC address as destination
	 * 
	 * @param buffer
	 *            in which the content of the packet will be written
	 * @return true if capture a packet.
	 */
	public boolean waitForDataPacketAndGetIt(final byte[] buffer) {
		found = false;
		foundPacket = buffer;

		while (!found) {
			pcap.loop(1, pcapPacketHandler, "");
		}

		return !stop;
	}
}
