package zisko.multicastor.program.mmrp;

import java.io.IOException;

/**
 * A sender and a receiver have to be available to register and deregister MMRP
 * Paths. So this class contains the logic how to do these operations and the
 * sender and receiver will inherit of this class.
 */
public class MMRPEntity {

	protected byte[] deviceMACAddress = null;
	protected byte[] streamMACAddress = null;
	private Thread keepPathAlive = null;

	/**
	 * Create the MMRP Entity.
	 * 
	 * @param deviceMACAddress
	 *            is a byte array which contains the MAC address of the network
	 *            device.
	 * @param streamMACAddress
	 *            is a byte array which contains the Address of the multicast
	 *            group
	 * @throws IOException
	 *             if the network device was not found
	 */

	public MMRPEntity(final byte[] deviceMACAddress,
			final byte[] streamMACAddress) throws IOException {
		this.deviceMACAddress = deviceMACAddress;
		this.streamMACAddress = streamMACAddress;
	}

	/**
	 * Deregister all active MMRP paths
	 * 
	 * @throws IOException
	 *             if the network device was not found
	 */

	public void deregisterAllPaths() throws IOException {
		PacketHandler.sendPacket(deviceMACAddress,
				MMRPPacket.getLeaveAll(deviceMACAddress, streamMACAddress));
		keepPathAlive.interrupt();
	}

	/**
	 * Deregister a MMRP path
	 * 
	 * @throws IOException
	 *             if the network device was not found
	 */

	public void deregisterPath() throws IOException {
		// Send a leave message
		PacketHandler.sendPacket(deviceMACAddress,
				MMRPPacket.getLeave(deviceMACAddress, streamMACAddress));
		keepPathAlive.interrupt();
	}

	/**
	 * Register a MMRP path
	 * 
	 * @throws IOException
	 *             if the network device was not found
	 */
	public void registerPath() throws IOException {
		PacketHandler.sendPacket(deviceMACAddress,
				MMRPPacket.getJoinEmpty(deviceMACAddress, streamMACAddress));
		keepPathAlive = new Thread(new ThreadKeepPathAlive(deviceMACAddress,
				streamMACAddress));
		keepPathAlive.start();

	}
}
