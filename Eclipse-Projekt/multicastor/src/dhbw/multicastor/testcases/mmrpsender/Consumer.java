   
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
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;

/**
 * 
 * @author tmichelc
 *
 */
public class Consumer implements Runnable {
	PacketReceiver packetAnalyser;
	Pcap pcap;

	Consumer(PacketReceiver packetAnalyser, Pcap pcap){
		System.out.println("Starting Consumer...");
		this.packetAnalyser = packetAnalyser;
	}

	/**
	 * Hier werden die MMRP-Packete ausgewertet und analysiert
	 */
	@Override
	public void run() {
		while(true){
			if(packetAnalyser.q.isEmpty()){
				synchronized(packetAnalyser.q){
					try {
						System.out.println("Consumer is waiting for new Packets in queue");
						packetAnalyser.q.wait();
						System.out.println("Consumer waked up");
					} catch (InterruptedException e) {
						System.out.println("InterruptedException caught");
					}
				}
			}else{
				
				doSthWithIncomingMmrpPackets();
			
			}
		}
	}

	private void doSthWithIncomingMmrpPackets() {
		
		//Code zur analyse des Paketes
		System.out.println("Got 1 Packet ("+packetAnalyser.q.size()+" Packets in Queue)");
		PcapPacket p = packetAnalyser.q.poll();
		
		//Insert MMRP Code here
		Ip4 ipHead = p.getHeader(new Ip4());

		ipHead.id(35);

		System.out.println("Found Packet with size: "+p.size()+" Byte");
		System.out.println(p);
		
	}

}
