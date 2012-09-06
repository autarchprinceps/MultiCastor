   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.testcases.jnetpcap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.RegistryHeaderErrors;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dhbw.multicastor.program.model.ByteTools;
import dhbw.multicastor.program.model.mmrp.Attribute;
import dhbw.multicastor.program.model.mmrp.MMRP;


public class TestPackets {
	public Pcap pcap;
	PcapIf device;
	
	@Before
	public void setUp(){

		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
		StringBuilder errbuf = new StringBuilder(); // For any error msgs  

		/*************************************************************************** 
		 * First get a list of devices on this system 
		 **************************************************************************/  
		int r = Pcap.findAllDevs(alldevs, errbuf);  
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
			return;  
		}  
		device = alldevs.get(4); // We know we have atleast 1 device  

		System.out  
		.printf("\nChoosing '%s' on your behalf:\n",  
				(device.getDescription() != null) ? device.getDescription()  
						: device.getName());  

		/***************************************** 
		 * Second we open a network interface 
		 *****************************************/  
		int snaplen = 64 * 1024; // Capture all packets, no trucation  
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
		int timeout = 10 * 1000; // 10 seconds in millis  
		System.out.println("Open pcap...");
		pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);  
		
		/**
		 * Registering the header
		 * 
		 * 
		 * */
	    try {  
	        int headerID = JRegistry.register(MMRP.class);  
	        
	        System.out.printf("Header registered successfully, its numeric ID is %d\n", headerID);  
	        
	      } catch (RegistryHeaderErrors e) {  
	        e.printStackTrace();  
	        System.exit(1);  
	      }  
	}
	@After 
	public void quit(){
		pcap.close();
		System.out.println("\nPcap closed.");
	}
	
	@Ignore
	@Test
	@Deprecated
	public void sendIpPacket(){
		

		/*******************************************************
		 * Create Packet from Raw data
		 */
	
		
		
		JPacket packet =  
			new JMemoryPacket(JProtocol.ETHERNET_ID,  
					" 0180c200 00200025 4bb7afec 88f60002 "  
							+ " 0041a983 40004006 45454545 6666");  
		Ethernet eth = packet.getHeader(new Ethernet());
		eth.checksum(eth.calculateChecksum());
		//mmrp header suchen
		MMRP mmrp = packet.getHeader(new MMRP());
		System.out.println(mmrp.toString());
		//werte setzen
		YArrayContent content = new YArrayContent();
		
		eth.checksum(eth.calculateChecksum());
		System.out.println(mmrp.toString());
		System.out.println(packet);

		/******************************************************* 
		 * Fourth We send our packet off using open device 
		 *******************************************************/  
		if (pcap.sendPacket(packet) != Pcap.OK) {  
			System.err.println(pcap.getErr());  
		}  
	}
	
	@Ignore
	@Test
	public void testLargePackets(){
		byte[] source = null;
		try {
			source = device.getHardwareAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JPacket packet =  
			new JMemoryPacket(JProtocol.ETHERNET_ID, (new YArrayContent()).createPDU());  
		Ethernet eth = packet.getHeader(new Ethernet());
		eth.checksum(eth.calculateChecksum());
		
		if (pcap.sendPacket(packet) != Pcap.OK) {  
			System.err.println(pcap.getErr());  
		} 
		//mmrp header suchen
		MMRP mmrp = packet.getHeader(new MMRP());
		
		//Attribute durchgehen
		for(Attribute at : mmrp.getListMes()){
			for(int i = 0;i<at.getFirstValue().length;i++){
				System.out.println("FirstValue: "+at.getFirstValue()[i]);
			}
		
			System.out.println("Number of all Events:"+at.getNumOfEvents());
			int i = 0;
			for(;i<at.getNumOfEvents();i++){
				System.out.println("Ein Event"+(i+1)+":"+at.getEventN(i));
			}
			System.out.println(at.getLeaveAll());
		}
	}
	
	@Test
	public void testLargePacketsTime(){
		byte[] source = null;
		try {
			source = device.getHardwareAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JPacket packet =  
			new JMemoryPacket(JProtocol.ETHERNET_ID, (new YArrayContent()).createPDU());  
		Ethernet eth = packet.getHeader(new Ethernet());
		eth.checksum(eth.calculateChecksum());
		
		if (pcap.sendPacket(packet) != Pcap.OK) {  
			System.err.println(pcap.getErr());  
		} 
		//mmrp header suchen
		MMRP mmrp = packet.getHeader(new MMRP());
		
		//Attribute durchgehen
		for(Attribute at : mmrp.getListMes()){
			for(int i = 0;i<at.getFirstValue().length;i++){
				System.out.println("FirstValue: "+at.getFirstValue()[i]);
			}
			if(at.getFirstValue().length == 6){
				long firstAddress = ByteTools.macToLong(at.getFirstValue());
				
				System.out.println("Number of all Events:"+at.getNumOfEvents());
				int i = 0;		
				long time1 = System.nanoTime();
				for(;i<at.getNumOfEvents();i++){
					at.getMyEvent(firstAddress+i);
					//System.out.println("Ein Event"+(i+1)+":"+at.getEventN(i));
				}
				long time2 = System.nanoTime();
				long diff = time2-time1;
				System.out.println("Took to get "+ at.getNumOfEvents()+" events:" + diff+" in ns");
				System.out.println(at.getLeaveAll());
			} else {
				System.out.println("fV = 0");
				System.out.println("Number of all Events:"+at.getNumOfEvents());
				int i = 0;		
				long time1 = System.nanoTime();
				for(;i<at.getNumOfEvents();i++){
					at.getEventN(i);
					//System.out.println("Ein Event"+(i+1)+":"+at.getEventN(i));
				}
				long time2 = System.nanoTime();
				long diff = time2-time1;
				System.out.println("Took to get "+ at.getNumOfEvents()+" events:" + diff+" in ns");
				System.out.println(at.getLeaveAll());
			}
			
		}
	}
	/**
	 * Creates a mmrp packet and read its event values.
	 * @throws UnsupportedDataTypeException
	 */
	@Ignore
	@Test
	public void testReadAttributes() throws UnsupportedDataTypeException{
		byte[] source = null;
		ArrayList<Byte> events = new ArrayList<Byte>();
		int leaveAll=1;
		try {
			source = device.getHardwareAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(source ==null){
			System.out.println("Die Adresse vom Gerät ist null");
			return;
		}
		//Initialisierung der Werte
			MMRPPacketCreator creator = new MMRPPacketCreator();
			
			events.add((byte)0x01);
			events.add((byte)0x02);
			events.add((byte)0x02);
			events.add((byte)0x03);
			events.add((byte)0x04);
			events.add((byte)0x05);
			events.add((byte)0x00);
			
			//multicast address
			byte[] firstValue = new byte[6];
			firstValue[0]=(byte) 1;
			firstValue[1]=(byte) 20;
			firstValue[2]=(byte) 50;
			firstValue[3]=(byte) 200;
			firstValue[4]=(byte) 10;
			firstValue[5]=(byte) 12;
			
		JPacket packet =  
			new JMemoryPacket(JProtocol.ETHERNET_ID, creator.createNewPacket(source,(byte) leaveAll, firstValue, events));  
		Ethernet eth = packet.getHeader(new Ethernet());
		eth.checksum(eth.calculateChecksum());
		
		if (pcap.sendPacket(packet) != Pcap.OK) {  
			System.err.println(pcap.getErr());  
		} 
		//mmrp header suchen
		MMRP mmrp = packet.getHeader(new MMRP());
		
		//Attribute durchgehen
		System.out.println("Number of all Events:"+mmrp.getListMes().get(0).getNumOfEvents());
		int i = 0;
		for(;i<mmrp.getListMes().get(0).getNumOfEvents();i++){
			System.out.println("Ein Event"+(i+1)+":"+mmrp.getListMes().get(0).getEventN(i));
		}
		System.out.println(mmrp.getListMes().get(0).getLeaveAll());

		eth.checksum(eth.calculateChecksum());
		System.out.println(packet);
	}	


	/**
	 * Initializes a mmrp packet.
	 * @throws UnsupportedDataTypeException
	 */
	@Ignore
	@Test
	public void testInitMMRP() throws UnsupportedDataTypeException{
		byte[] source = this.getSourceAddress();
		
		if(source ==null){
			System.out.println("Die Adresse vom Gerät ist null");
			return;
		}
		//Initialisierung der Werte
			MMRPPacketCreator creator = new MMRPPacketCreator();
			ArrayList<Byte> events = new ArrayList<Byte>();
			
			events.add((byte) 3);
			
			//multicast address
			byte[] firstValue = MMRPHelper.firstValue();			
			
		JPacket packet =  
			new JMemoryPacket(JProtocol.ETHERNET_ID,creator.createNewPacket(source,(byte) 0, firstValue, events));  
		Ethernet eth = packet.getHeader(new Ethernet());
		eth.checksum(eth.calculateChecksum());
		//mmrp header suchen
		MMRP mmrp = packet.getHeader(new MMRP());
		eth.checksum(eth.calculateChecksum());
		System.out.println(packet);		
		if (pcap.sendPacket(packet) != Pcap.OK) {
			System.err.println(pcap.getErr());
		}
	}
	
	public static void events(ArrayList<Byte> events, int event){
		switch (event) {
		//Begin!,Flush!
		case 0: {
			events.add((byte) 0x04);
			events.add((byte) 0x04);
			events.add((byte) 0x04);
			break;
		}
		case 1: {
			events.add((byte) 0x02);
			events.add((byte) 0x02);
			events.add((byte) 0x02);
			break;
		}
		//Re-declare
		case 2: {
			events.add((byte) 0x05);
			events.add((byte) 0x05);
			events.add((byte) 0x04);
			break;
		}
		case 3: {
			events.add((byte) 0x04);
			events.add((byte) 0x04);
			events.add((byte) 0x04);
			break;
		}
		case 4: {
			events.add((byte) 0x01);
			events.add((byte) 0x01);
			events.add((byte) 0x01);
			break;
		}
		default: break;
		}
	}
	
	/**
	 * 
	 */
	@Ignore
	@Test
	public void testSendPacket(){
		
		//initializes destination address
		byte[] destination = new byte[6];
		destination[0]= 1;
		destination[0]=20;
		destination[0]=50;
		destination[0]=20;
		destination[0]= 10;
		destination[0]=12;
		
		//initializes a packet
		JPacket packet = this.createPacketOnMMRP(destination);
		
		/******************************************************* 
		 * We send our packet off using open device 
		 *******************************************************/  
		while (true) {
			if (pcap.sendPacket(packet) != Pcap.OK) {
				System.err.println(pcap.getErr());
			}
		}
		
		
	}
	
	/**
	 * Gets the address of the actual interface
	 * @return mac address of the actual interface
	 */
	public byte[] getSourceAddress(){
		byte[] source = null;
		try {
			source = device.getHardwareAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return source;
	}

	/**
	 * Creates a packet to the mac multicast address
	 * @param destination the multicast address
	 */
	public JPacket createPacketOnMMRP(byte[] destination){
		/*******************************************************
		 * Create Packet from Raw data
		 */
		//buffer which will be sent
		JBuffer b = new JBuffer(20);
		int i = 0;
		//destination
		for (i = 0; i < destination.length; i++) {
			b.setByte(i, destination[i]);
		}
		//source
		byte[] source = this.getSourceAddress();
		if (source != null) {
			for (i = 0; i < source.length; i++) {
				b.setByte(6 + i, source[i]);
			}
		}
		i=12;
		
		//type:IPv4
		b.setByte(i++, (byte) 0x03);
		b.setByte(i++, (byte) 0x20);		
		//payload
		b.setByte(i++, (byte) 0xdc);
		b.setByte(i++, (byte) 0xdc);
		b.setByte(i++, (byte) 0xdc);
		b.setByte(i++, (byte) 0xdc);
		b.setByte(i++, (byte) 0xdc);
		
		//creates a packet
		JPacket packet = new JMemoryPacket(JProtocol.ETHERNET_ID,b);
		Ethernet eth = packet.getHeader(new Ethernet());
		eth.checksum(eth.calculateChecksum());
		
		//Überprüfung der Struktur des Packets
		packet.scan(Ethernet.ID);
		System.out.println(packet);
		return packet;
	}

}
