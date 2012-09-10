package zisko.multicastor.program.mmrp;

import java.io.IOException;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;


/**
 * Receives packets which are send from the stream. 
 *
 */
public class MMRPReceiver extends MMRPEntity {

	private boolean found;
	private boolean stop = false;
	private byte[] foundPacket;
	private Pcap pcap = null;

	PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {
		public void nextPacket(PcapPacket packet, String arg1) {
			int length = (int) (packet.getByte(13) & 0xFF)
					+ ((int) (packet.getByte(12) & 0xFF)) * 255;
			System.arraycopy(packet.getByteArray(14, length), 0, foundPacket,
					0, length);
			found = true;
		}
	};
/**
 * Stops the procedure which caputures the packets
 */
	public void stopLoop() {
		stop = true;
		
		byte[] data = {(byte)0x00,(byte)0x00};
		try {
			PacketHandler.sendPacket(this.deviceMACAddress,DataPacket.getDataPacket(this.deviceMACAddress,this.streamMACAddress,data));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param deviceMACAddress is a byte array which contains the MAC address of the network device.
	 * @param streamMACAddress is a byte array which contains the Address of the multicast group
	 * @throws IOException if the network device was not found
	 */
	public MMRPReceiver(byte[] deviceMACAddress, byte[] streamMACAddress)
			throws IOException {
		super(deviceMACAddress, streamMACAddress);
		this.pcap = PcapHandler.getPcapInstance(deviceMACAddress);
		PcapBpfProgram programm = new PcapBpfProgram();
		this.pcap.compile(programm,
				"ether dst " + PcapHandler.byteMACToString(streamMACAddress),
				0, (int) 0xFFFFFF00);
		this.pcap.setFilter(programm);
	}
	/**
	 * Capture all packets which has the stream MAC address as destination
	 * 
	 * @param buffer in which the content of the packet will be written
	 * @return true if capture a packet.
	 */
	public boolean waitForDataPacketAndGetIt(byte[] buffer) {
		found = false;
		foundPacket = buffer;
		
		while (!found) {
			pcap.loop(1, pcapPacketHandler,"");
		}
		
		return !stop;
	}
}
