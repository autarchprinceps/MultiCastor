package zisko.multicastor.program.mmrp;

import java.io.IOException;

/**
 * A sender and a receiver have to be available to register and deregister MMRP Paths. 
 * So this class contains the logic how to do these operations and the sender and receiver will inherit of this class.
 *  
 *
 */
public class MMRPEntity {
	
	protected byte[] deviceMACAddress = null;
	protected byte[] streamMACAddress = null;
	private Thread keepPathAlive = null;
	
	/**
	 * Create the MMRP Entity.
	 * 
	 * @param deviceMACAddress is a byte array which contains the MAC address of the network device. 
	 * @param streamMACAddress is a byte array which contains the Address of the multicast group
	 * @throws IOException if the network device was not found
	 */
	
	public MMRPEntity(byte[] deviceMACAddress,byte[] streamMACAddress) throws IOException {
		this.deviceMACAddress = deviceMACAddress;
		this.streamMACAddress = streamMACAddress;
	}
	
	/**
	 * Register a MMRP path
	 * @throws IOException if the network device was not found
	 */
	public void registerPath() throws IOException{
		PacketHandler.sendPacket(this.deviceMACAddress,MMRPPacket.getJoinEmpty(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive = new Thread(new ThreadKeepPathAlive(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive.start();
		
	}
	
	/**
	 * Deregister a MMRP path
	 * @throws IOException if the network device was not found
	 */
	
	public void deregisterPath() throws IOException{
		// Send a leave message 
		PacketHandler.sendPacket(this.deviceMACAddress,MMRPPacket.getLeave(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive.interrupt();
	}
	
	/**
	 * Deregister all active MMRP paths
	 * @throws IOException if the network device was not found
	 */
	
	public void deregisterAllPaths() throws IOException{
		PacketHandler.sendPacket(this.deviceMACAddress,MMRPPacket.getLeaveAll(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive.interrupt();
	}
}


