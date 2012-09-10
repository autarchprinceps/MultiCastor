package zisko.multicastor.program.mmrp;

import java.io.IOException;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
/**
 * This class maintains the MMRP path connection.  
 * 
 */
public class ThreadKeepPathAlive implements Runnable {
	private byte[] deviceMACAddress;
	private byte[] streamMACAddress;
	private Pcap pcap = null;
	
	PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {

		public void nextPacket(PcapPacket packet, String arg1) {

			// check for stream address
			byte[] source = new byte[6];
			byte[] stream = new byte[6];

			for (int i = 0; i < 6; i++) {
				source[i] = packet.getByte(6 + i);
				stream[i] = packet.getByte(19 + i);
			}

			if (PcapHandler.compareMACs(stream, streamMACAddress)) {
				int cast = (int) packet.getByte(25) & 0xff;
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

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	};

	public ThreadKeepPathAlive(byte[] deviceMACAddress, byte[] streamMACAddress)
			throws IOException {
		this.deviceMACAddress = deviceMACAddress;
		this.streamMACAddress = streamMACAddress;
		this.pcap = PcapHandler.getPcapInstance(deviceMACAddress);
		// set the filter
		PcapBpfProgram programm = new PcapBpfProgram();
		this.pcap.compile(programm, "ether proto 0x88f6", 0, (int) 0xFFFFFF00);
		this.pcap.setFilter(programm);
	}

	public void waitForEmpty() {
		while (!Thread.interrupted()) {
			pcap.loop(1, pcapPacketHandler, "");
		}
	}

	public void run() {
		this.waitForEmpty();
	}

}
