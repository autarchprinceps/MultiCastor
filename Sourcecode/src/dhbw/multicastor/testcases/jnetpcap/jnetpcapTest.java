   
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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.JBufferHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class jnetpcapTest{
	public Pcap pcap;

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
		PcapIf device = alldevs.get(2); // We know we have atleast 1 device  

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
	}

	@After 
	public void quit(){
		//pcap.close();
		//		System.out.println("\nPcap closed.");
	}


	@Test
	public void listAllDevices(){
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

		int i = 0;  
		for (PcapIf device : alldevs) {  
			String description =  
					(device.getDescription() != null) ? device.getDescription()  
							: "No description available";  
					System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);  
		}   
	}


	@Test
	public void sendPacket(){

		/******************************************************* 
		 * Third we create our crude packet we will transmit out 
		 * This creates a broadcast packet 
		 *******************************************************/  
		byte[] a = new byte[14];  
		Arrays.fill(a, (byte) 0xff);  
		ByteBuffer b = ByteBuffer.wrap(a);  

		/******************************************************* 
		 * Fourth We send our packet off using open device 
		 *******************************************************/  
		if (pcap.sendPacket(b) != Pcap.OK) {  
			System.err.println(pcap.getErr());  
		}  
	}

	@Test 
	public void modifieIgmpPacket(){

		JPacket packet = 	new JMemoryPacket(JProtocol.ETHERNET_ID,  
						" 001801bf 6adc0025 4bb7afec 08004500 "  
								+ " 0041a983 40004006 d69ac0a8 00342f8c "  
								+ " ca30c3ef 008f2e80 11f52ea8 4b578018 "  
								+ " ffffa6ea 00000101 080a152e ef03002a "  
								+ " 2c943538 322e3430 204e4f4f 500d0a");  


		//Holt sich aus dem Packet die Referenz auf den Ip4 und den TCP Header
		Ip4 ip = packet.getHeader(new Ip4());  
		Tcp tcp = packet.getHeader(new Tcp());  
	
		
		//Kann jetzt über die Referenz Daten aus dem Packet ändern
		tcp.destination(80);  

		ip.checksum(ip.calculateChecksum());  
		tcp.checksum(tcp.calculateChecksum());  

		//Prüfung ob Struktur des Packets noch passt
		packet.scan(Ethernet.ID);  
		System.out.println(packet);  
		
		pcap.sendPacket(packet);

	}

	@Test
	public void sendIpPacket(){

		/*******************************************************
		 * Create Packet from Raw data
		 */

		JPacket packet =  
				new JMemoryPacket(JProtocol.ETHERNET_ID,  
						" 001801bf 6adc0025 4bb7afec 08004500 "  
								+ " 0041a983 40004006 d69ac0a8 00342f8c "  
								+ " ca30c3ef 008f2e80 11f52ea8 4b578018 "  
								+ " ffffa6ea 00000101 080a152e ef03002a "  
								+ " 2c943538 322e3430 204e4f4f 500d0a");  

		//Überprüfung der Struktur des Packets
		packet.scan(Ethernet.ID);
		System.out.println(packet);

		/******************************************************* 
		 * Fourth We send our packet off using open device 
		 *******************************************************/  
		if (pcap.sendPacket(packet) != Pcap.OK) {  
			System.err.println(pcap.getErr());  
		}  
	}

	@Test
	public void capturePackets(){

		JBufferHandler<String> handler = new JBufferHandler<String>() {  
			public void nextPacket(PcapHeader header, JBuffer buffer, String user) {  
				System.out.println("Packet size of packet is=" + buffer.size());  
			} 
		} ;

		System.out.println("Scan packets...\n");
		pcap.loop(10, handler, "jNetPcap rocks!");  

	}


	/**
	 * Für MMRP unrelevant
	 */
	@Test
	public void threadNotifyTest(){
		System.out.println("\n\n-----------------\ntest begins");
		class MyR implements Runnable{

			@Override
			public void run() {
				System.out.println(this.getClass().getSimpleName());
				synchronized(this){
					System.out.println("going to sleep");
					try {
						System.out.println("Thread holdsLock:" +Thread.holdsLock(this));
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("wacked up");
				}

			}

		}
		System.out.println("MainThread: "+Thread.currentThread().getName());
		Runnable r = new MyR();
		Thread t = new Thread(r, "MyThread");
		t.start();
		System.out.println("MainThread holdsLock: "+Thread.holdsLock(r));
		System.out.println("MyThread: "+t.getName()+" state: "+t.getState());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("MyThread: "+t.getName()+" state: "+t.getState());
		synchronized(r){
		r.notify();
		}
		//WICHTIG Threads müssen sich auf das gleiche Objekt synchronisieren


	}

	 
}
