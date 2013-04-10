package com.ibm.MMRP;

import java.io.IOException;

import org.jnetpcap.PcapIf;

public class MMRPBuilder {
	private static final int protocolVersion = 1;
	public static final int none = 0;
	public static final int joinIn = 1;
	public static final int in = 2;
	public static final int joinEmpty = 3;
	public static final int empty = 4;
	public static final int leave = 5;
	
	public byte[] getPacket(byte[] destination,PcapIf device, int attributeType, boolean leaveAll,byte[] firstValue ,int attributeEvent){
		byte[] frame = this.getMMRPFrame(attributeType, leaveAll, firstValue, attributeEvent);
		byte[] packet = new byte[frame.length + 14];
		byte[] source = null;
		
		for(int i = 0; i < 6; i++){
			packet[i] = destination[i];
		}
		
		try {
			source = device.getHardwareAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < 6; i++){
			packet[i + 6] = source[i];
		}
		
		packet[12] = (byte) 0x88;
		packet[13] = (byte) 0xf6;
		
		for(int i = 0; i < frame.length; i++){
			packet[14 + i] = frame[i];
		}
		
		return packet;
	}
	
	private byte[] getMMRPFrame(int attributeType, boolean leaveAll,byte[] firstValue ,int attributeEvent){
		byte[] frame = new byte[16];
		
		//Protocol Version
		frame[0] = (byte) this.protocolVersion;
		
		//AttributeType (1 = Service Requirement; 2 = MAC Address)
		switch(attributeType){
			case MMRPBuilder.serviceRequirement: frame[1] = (byte) 0x01; break;
			case MMRPBuilder.mac: frame[1] = (byte) 0x02; break;
		}
		
		//AttributLength (Need only one event for the Multicastor)
		frame[2] = (byte) 0x06;
		
		//Check if a leaveAll 
		if(leaveAll){
			frame[3] = (byte) 0x20;
		}
		else {
			frame[3] = (byte) 0x00;
		}
		// Always one event will be send
		frame[4] =(byte) 0x01;
		
		// Add the firstValue to the frame
		frame[5] =  firstValue[0];
		frame[6] =  firstValue[1];
		frame[7] =  firstValue[2];
		frame[8] =  firstValue[3];
		frame[9] =  firstValue[4];
		frame[10] =  firstValue[5];
		
		// Add the event to the frame
		frame[11] = (byte) (36 * attributeEvent);
		
		// Set Endmark
		frame[12] = (byte) 0x00;
		frame[13] = (byte) 0x00;
		frame[14] = (byte) 0x00;
		frame[15] = (byte) 0x00;
		
		return frame;
		
	}
}
