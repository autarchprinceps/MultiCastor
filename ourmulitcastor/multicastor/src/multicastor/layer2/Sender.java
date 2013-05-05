package multicastor.layer2;

import java.io.IOException;

/**
 * Send data packets into the MMRPPath
 */
public class Sender extends Entity {
	/**
	 * @param deviceMACAddress
	 *            is a byte array which contains the MAC address of the network
	 *            device.
	 * @param streamMACAddress
	 *            is a byte array which contains the Address of the multicast
	 *            group
	 * @throws IOExceptionleaveAll
	 *             if the network device was not found
	 */

	public Sender(final byte[] deviceMACAddress,
			final byte[] streamMACAddress) throws IOException {
		super(deviceMACAddress, streamMACAddress);
	}

	/**
	 * @param data
	 *            is a byte array which contains the data which should be send
	 * @throws IOException
	 *             if network device was not found
	 */
	public void sendDataPacket(final byte[] data) throws IOException {
		PacketHandler.sendPacket(deviceMACAddress, DataPacket.getDataPacket(
				deviceMACAddress, streamMACAddress, data));
	}
}
