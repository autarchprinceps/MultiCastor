   
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

import java.util.Queue;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;

/**
 * Filtert aus den einkommenden Packeten die relevanten MMRP Packete
 * und legt diese in die Queue
 * @author tmichelc
 *
 */
public class MmrpPacketHandler implements PcapPacketHandler<Queue<PcapPacket>>{
	public final Ip4 ip = new Ip4(); 
	public Producer producer; 
	
	MmrpPacketHandler(Producer producer){
		System.out.println("Starting MmrpPacketHandler...");
		this.producer = producer;
	}
	
	/**
	 * Klasse zum vorsortieren
	 * Hier wird sortiert, dass nur MMRP-Paket in die Queue geschmissen werden
	 */
	@Override
	public void nextPacket(PcapPacket packet, Queue<PcapPacket> queue) {
		PcapPacket permanent = new PcapPacket(packet);

		//Legt alle IP Packets in die Queue
		if(permanent.hasHeader(ip)){
			System.out.println("Put 1 Packet ("+producer.packetAnalyser.q.size()+")  Packets in Queue");
			producer.packetAnalyser.q.offer(permanent);
			
			//Benachrichtige ConsumerThread, dass neue Packet in der Queue liegen
			synchronized(producer.packetAnalyser.q){
				producer.packetAnalyser.q.notify();
			}
		}
	}



}
