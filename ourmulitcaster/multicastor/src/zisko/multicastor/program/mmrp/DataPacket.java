package zisko.multicastor.program.mmrp;

/**
 * Build a packet with source, destination, data length and the data which should be send as information. 
 */

public class DataPacket {
	
	
	/**
	 * 
	 * @param source is a byte array which contains the MAC address of the device which will send the packet
	 * @param destination is a byte array which contains the MAC address of the device which should received the packet
	 * @param data is a byte array which contains the data which should be send
	 * @return the data packet as a byte array
	 */
	
	public static byte[] getDataPacket(byte[] source, byte[] destination, byte[] data){
		byte [] packet = new byte[14 + data.length];
		
		//Put the source and destination into the packet
		for(int i = 0; i < 6; i++){
			packet[i] = destination[i];
			packet[6 + i] = source[i];
		}
		
		//Set the length of the data which should be send
		packet[12] = (byte) (data.length/255);
		packet[13] = (byte) (data.length%255); 
		
		//Put the data into the packet
		for(int i = 0; i < data.length; i++){
			packet[14 + i] = data[i];
		}
		
		return packet;
	}
}
