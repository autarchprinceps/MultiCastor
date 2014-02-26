package multicastor.layer2;

import java.io.IOException;

import multicastor.data.MulticastData.Protocol;

/**
 * A sender and a receiver have to be available to register and deregister MMRP
 * Paths. So this class contains the logic how to do these operations and the
 * sender and receiver will inherit of this class.
 */
public class Entity {

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

	public Entity(final byte[] deviceMACAddress,
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

	public void deregisterAllPaths(Protocol protocol) throws IOException {
		if(protocol == Protocol.GMRP)
		{
			PacketHandler.sendPacket(deviceMACAddress,
				GMRPPacket.getLeaveAll(deviceMACAddress, streamMACAddress));
		} else {
			PacketHandler.sendPacket(deviceMACAddress,
				MMRPPacket.getLeaveAll(deviceMACAddress, streamMACAddress));
			if(protocol != Protocol.MMRP)
				System.out.println("PROTOCOL ERROR fallback to MMRP");
		}
		keepPathAlive.interrupt();
	}

	/**
	 * Deregister a MMRP path
	 * 
	 * @throws IOException
	 *             if the network device was not found
	 */

	public void deregisterPath(Protocol protocol) throws IOException {
		if(protocol == Protocol.GMRP)
		{
			PacketHandler.sendPacket(deviceMACAddress,
				GMRPPacket.getLeave(deviceMACAddress, streamMACAddress));
		} else {
			PacketHandler.sendPacket(deviceMACAddress,
				MMRPPacket.getLeave(deviceMACAddress, streamMACAddress));
			if(protocol != Protocol.MMRP)
				System.out.println("PROTOCOL ERROR fallback to GMRP");
		}
		keepPathAlive.interrupt();
	}

	/**
	 * Register a MMRP path
	 * 
	 * @throws IOException
	 *             if the network device was not found
	 */
	public void registerPath(Protocol protocol) throws IOException {
		if(protocol == Protocol.GMRP)
		{
			PacketHandler.sendPacket(deviceMACAddress,
				GMRPPacket.getJoinEmpty(deviceMACAddress, streamMACAddress));
		} else {
			PacketHandler.sendPacket(deviceMACAddress,
				MMRPPacket.getJoinEmpty(deviceMACAddress, streamMACAddress));
			if(protocol != Protocol.MMRP)
				System.out.println("PROTOCOL ERROR fallback to GMRP");
		}
		keepPathAlive = new Thread(new ThreadKeepPathAlive(deviceMACAddress,
				streamMACAddress));
		keepPathAlive.start();

	}
}
