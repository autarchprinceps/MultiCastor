   
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
 
 package dhbw.multicastor.testcases.mmrpsender;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;


public class PacketSender implements Runnable {
	Pcap pcap;

	PacketSender(Pcap pcap){
		this.pcap = pcap;
	}

	@Override
	public void run() {
		while(true){
			synchronized(this){
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sendNewPacket(); 

		}
	}

	/**
	 * Creates a new Packet from raw data and sends this Packet via pcap
	 */
	private void sendNewPacket() {
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
				
				//Holt sich aus dem Packet die Referenz auf den Ip4 und den TCP Header
				Ip4 ip = packet.getHeader(new Ip4());  
				Tcp tcp = packet.getHeader(new Tcp());  
			
				
				//Kann jetzt über die Referenz Daten aus dem Packet ändern
				tcp.destination(80);  
		
				ip.checksum(ip.calculateChecksum());  
				tcp.checksum(tcp.calculateChecksum());  
				
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

}
