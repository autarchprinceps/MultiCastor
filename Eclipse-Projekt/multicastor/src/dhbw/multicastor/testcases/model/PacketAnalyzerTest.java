   
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
 
 package dhbw.multicastor.testcases.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.PacketAnalyzer;
import dhbw.multicastor.program.model.PacketBuilder;

import junit.framework.TestCase;

public class PacketAnalyzerTest extends TestCase {
		PacketAnalyzer packetAnalyzer;
		IgmpMldData mcData;
		PacketBuilder packetBuilder;
		
		final int packetLength = 3000;
		String hostID = "hostIDdummy";
		int threadID = 5;
		int ttl = 3;
		int packetRateDesired = 200;
		
		@Before
		public void setUp(){
			mcData = new IgmpMldData(ProtocolType.IGMP);
			try {
				mcData.setGroupIp(InetAddress.getByName("224.0.0.1"));
				mcData.setUdpPort(4711);
			} catch (UnknownHostException e) {
				fail();
			}
			Logger logger = Logger.getLogger("dhbw.multicastor.testcases.model");
			packetAnalyzer = new PacketAnalyzer(mcData, logger);
			
			assertNotNull(packetAnalyzer);
			
			IgmpMldData mcBean = new IgmpMldData(ProtocolType.IGMP);
			mcBean.setPacketLength(packetLength);
			mcBean.setHostID(hostID);
			mcBean.setThreadID(threadID);
			mcBean.setTtl(ttl);
			mcBean.setPacketRateDesired(packetRateDesired);
			packetBuilder = new PacketBuilder(mcBean);
		}
		
		/**
		 * Erzeugt zwei aufeinander folgende Pakete und wertet diese aus
		 */
		@Test
		public void testAnalysePacket1(){
			mcData.resetValues();
			packetAnalyzer.setInternerPacketCount(-1);
			
			packetAnalyzer.analyzePacket(createPacket());
			packetAnalyzer.analyzePacket(createPacket());
			
			packetAnalyzer.update();
			
			assertEquals(packetLength, mcData.getPacketLength());
			assertEquals(hostID, mcData.getHostID());
			assertEquals(threadID, mcData.getThreadID());
			assertEquals(ttl, mcData.getTtl());
			assertEquals(packetRateDesired, mcData.getPacketRateDesired());
			assertEquals(2, mcData.getPacketCount());
			assertEquals(2, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			// Jitter wird nicht getestet....hab mir noch keinen guten Test dafÃ¼r Ã¼berlegt
			assertEquals(0, mcData.getPacketLossPerSecond());
			assertEquals(packetLength*2, mcData.getTraffic());
			
			packetAnalyzer.updateMin();
			assertEquals(packetLength, mcData.getPacketLength());
			assertEquals(hostID, mcData.getHostID());
			assertEquals(threadID, mcData.getThreadID());
			assertEquals(ttl, mcData.getTtl());
			assertEquals(packetRateDesired, mcData.getPacketRateDesired());
			assertEquals(2, mcData.getPacketCount());
			assertEquals(2, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			assertEquals(0, mcData.getPacketLossPerSecond());
			
	//		assertEquals(0, mcData.getNumberOfInterruptionsAvg());
			assertEquals(0, mcData.getPacketLossPerSecondAvg());
			assertEquals(2, mcData.getPacketRateAvg());
			assertEquals(packetLength*2, mcData.getTrafficAvg());
		}
		
		/**
		 * Testet mit 10 Paketen bei denen 2 in Reihe nicht "empfangen" wurden
		 */
		@Test
		public void testAnalysePacket2(){
			mcData.resetValues();
			packetAnalyzer.setInternerPacketCount(-1);
			
			byte[][] packets = new byte[10][65575];
			for(int i=0;i<10;i++){
				packets[i] = createPacket();
			}
			
			packetAnalyzer.analyzePacket(packets[0]);
			packetAnalyzer.analyzePacket(packets[1]);
			packetAnalyzer.analyzePacket(packets[2]);
		//	packetAnalyzer.analyzePacket(packets[3]);
		//	packetAnalyzer.analyzePacket(packets[4]);
			packetAnalyzer.analyzePacket(packets[5]);
			packetAnalyzer.analyzePacket(packets[6]);
			packetAnalyzer.analyzePacket(packets[7]);
			packetAnalyzer.analyzePacket(packets[8]);
			packetAnalyzer.analyzePacket(packets[9]);
			
			packetAnalyzer.update();
			
			assertEquals(8, mcData.getPacketCount());
			assertEquals(8, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			// Jitter wird nicht getestet....hab mir noch keinen guten Test dafÃ¼r Ã¼berlegt
			assertEquals(2, mcData.getPacketLossPerSecond());
			assertEquals(packetLength*8, mcData.getTraffic());
			
			packetAnalyzer.updateMin();
			
		//	assertEquals(0, mcData.getNumberOfInterruptionsAvg());
			assertEquals(2, mcData.getPacketLossPerSecondAvg());
			assertEquals(8, mcData.getPacketRateAvg());
			assertEquals(packetLength*8, mcData.getTrafficAvg());
		}
		
		/**
		 * Testet auf average-values mit zwei Gruppen von mc-paketen
		 */
		@Test
		public void testAnalysePacket3(){
			mcData.resetValues();
			packetAnalyzer.setInternerPacketCount(-1);
			
			byte[][] packets = new byte[20][65575];
			for(int i=0;i<20;i++){
				packets[i] = createPacket();
			}
			
			packetAnalyzer.analyzePacket(packets[0]);
			packetAnalyzer.analyzePacket(packets[1]);
			packetAnalyzer.analyzePacket(packets[2]);
		//	packetAnalyzer.analyzePacket(packets[3]);
		//	packetAnalyzer.analyzePacket(packets[4]);
			packetAnalyzer.analyzePacket(packets[5]);
			packetAnalyzer.analyzePacket(packets[6]);
			packetAnalyzer.analyzePacket(packets[7]);
			packetAnalyzer.analyzePacket(packets[8]);
			packetAnalyzer.analyzePacket(packets[9]);
			
			packetAnalyzer.update();
			
			packetAnalyzer.analyzePacket(packets[10]);
		//	packetAnalyzer.analyzePacket(packets[11]);
			packetAnalyzer.analyzePacket(packets[12]);
		//	packetAnalyzer.analyzePacket(packets[13]);
		//	packetAnalyzer.analyzePacket(packets[14]);
			packetAnalyzer.analyzePacket(packets[15]);
		//	packetAnalyzer.analyzePacket(packets[16]);
			packetAnalyzer.analyzePacket(packets[17]);
		//	packetAnalyzer.analyzePacket(packets[18]);
			packetAnalyzer.analyzePacket(packets[19]);
			
			packetAnalyzer.update();
			
			assertEquals(13, mcData.getPacketCount());
			assertEquals(5, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			// Jitter wird nicht getestet.
			assertEquals(5, mcData.getPacketLossPerSecond());
			assertEquals(packetLength*5, mcData.getTraffic());
			
			packetAnalyzer.updateMin();
			
	//		assertEquals(2, mcData.getNumberOfInterruptionsAvg());
			assertEquals(3, mcData.getPacketLossPerSecondAvg());
			assertEquals(6, mcData.getPacketRateAvg());
			assertEquals(packetLength*13/2, mcData.getTrafficAvg());
		}
		
		/**
		 * Testet ob die Interruptionmessung funktioniert, unter der Vorraussetzung, dass
		 * die update()- und setTimeout()-Methoden richtig verwendet werden.
		 */
		@Test
		public void testAnalysePacket4(){
			mcData.resetValues();
			
			packetAnalyzer.setTimeout(false);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
		
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(false);
			
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			
			assertEquals(1, mcData.getNumberOfInterruptions());
			assertEquals(4, mcData.getMaxInterruptionTime());
			assertEquals(4, mcData.getAverageInterruptionTime());
			
			packetAnalyzer.setTimeout(false);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
		
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(false);
			
			packetAnalyzer.update();
			assertEquals(2, mcData.getNumberOfInterruptions());
			assertEquals(6, mcData.getMaxInterruptionTime());
			assertEquals(5, mcData.getAverageInterruptionTime());
		}
		
		/**
		 * Testet was bei falscher Arraygrï¿½ï¿½e passiert
		 */
		@Test
		public void testAnalysePacket5(){
			mcData.resetValues();
			
			byte[] array = new byte[50];
			packetAnalyzer.analyzePacket(array);
			
			packetAnalyzer.update();
			
			assertEquals(0, mcData.getNumberOfInterruptions());
			assertEquals(0, mcData.getMaxInterruptionTime());
			assertEquals(0, mcData.getAverageInterruptionTime());
		}
		
		/**
		 * Erzeugt die Pakete gefÃ¼llt mit restlichen einsen. Dies ist kein perfektes 
		 * Ebenbild der realen Pakete, da diese nicht immer mit 1en gefÃ¼llt werden,
		 * sondern nur einmal pro Sekunde.
		 * Dies kann jedoch hierbei auÃ?eracht gelassen werden.
		 * @return
		 */
		private byte[] createPacket(){
			byte[] p = packetBuilder.getPacket();
			byte[] packet = new byte[65575];
			int i = 0;
			for(;i<p.length;i++){
				packet[i] = p[i];
			}
			for(;i<65575;i++){
				packet[i] = 1;
			}
			return packet;
		}
		
}
